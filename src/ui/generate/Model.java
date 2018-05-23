package ui.generate;

import org.donntu.databaseworker.DBWorker;
import org.donntu.databaseworker.Student;
import org.donntu.databaseworker.StudentTask;
import org.donntu.drawer.GeneratorDrawer;
import org.donntu.generator.*;
import org.donntu.generator.configs.GenerateConfig;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Model {

    public static void generateIndividual(String group, String name, String surname, GenerateConfig config) throws Exception {
        StudentTask task = Generator.generateIndividualTask(surname, name, group, config);
        final List<StudentTask> allTasks = DBWorker.getAllTasks();

        for (int i = 0; i < allTasks.size(); i++) {
            if (allTasks.get(i).getCreationDate().get(Calendar.YEAR)
                    != task.getCreationDate().get(Calendar.YEAR)) {
                continue;
            }
            final TopologyCompareCriteria criteria = task.getTopology().whatIsLike(allTasks.get(i).getTopology());
            if (criteria.isWan() && criteria.isLan()) {
                List<IP> ip = new ArrayList<>();
                for (Network network : task.getTopology().getNetworks()) {
                    ip.add(network.getIp().getCopy());
                }
                task = Generator.generateIndividualTask(surname, name, group, config);
                int j = 0;
                for (Network network : task.getTopology().getNetworks()) {
                    network.setIp(ip.get(j++));
                }
                i = -1;
                continue;
            }
            if (criteria.isIp()) {
                for (Network network : task.getTopology().getNetworks()) {
                    TopologyGenerator.generateIPForNetwork(network);
                }
                i = -1;
            }
        }
        GeneratorDrawer.saveImage(
                "task/" + group + "/" + task.getSurname() + " " + task.getName(),
                task.toString(),
                GeneratorDrawer.drawStudentTask(task));
        DBWorker.addStudentTask(task);
    }

    public static void generateByGroup(String group, GenerateConfig config) throws Exception {
        final List<Student> students = DBWorker.getStudentsByGroup(group);
        final List<StudentTask> studentTasks = Generator.generateTasksForGroup(students, group, config);
        final List<StudentTask> allTasks = DBWorker.getAllTasks();

        for (StudentTask task : studentTasks) {
            for (int i = 0; i < allTasks.size(); i++) {
                final TopologyCompareCriteria criteria = task.getTopology().whatIsLike(allTasks.get(i).getTopology());
                if (criteria.isWan() && criteria.isLan()) {
                    task = Generator.generateIndividualTask(task.getName(), task.getSurname(), task.getGroup(), config);
                    i = -1;
                    continue;
                }
                if (criteria.isIp()) {
                    for (Network network : task.getTopology().getNetworks()) {
                        TopologyGenerator.generateIPForNetwork(network);
                    }
                    i = -1;
                }
            }
        }

        /*ToDBThread toDBThread = new ToDBThread(studentTasks);
        toDBThread.start();*/



        for (StudentTask task : studentTasks) {
            DBWorker.addStudentTask(task);
        }

        SaveThread saveThread = new SaveThread(studentTasks);
        saveThread.start();
    }
}
