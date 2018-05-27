package ui.generate;

import org.donntu.GregorianCalendar;
import org.donntu.databaseworker.DBWorker;
import org.donntu.databaseworker.Student;
import org.donntu.databaseworker.StudentTask;
import org.donntu.drawer.GeneratorDrawer;
import org.donntu.generator.*;
import org.donntu.generator.configs.GenerateConfig;
import org.donntu.generator.field.Field;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Model {

    private static StudentTask generateIndividualTask(
            String name, String surname, String group, GenerateConfig generateConfig) throws Exception {
        try {
            return new StudentTask(Generator.generateTask(generateConfig), name, surname, group, new GregorianCalendar());
        } catch (Exception e) {
            throw new Exception("Генерация прервана с ошибкой: \n" + e.getMessage());
        }
    }

    private static List<StudentTask> generateTasksForGroup(
            List<Student> namesAndSurnames,
            String group,
            GenerateConfig generateConfig) {
        List<StudentTask> tasks = new ArrayList<>();

        for (Student nameAndSurname: namesAndSurnames){
            try {
                tasks.add(
                        generateIndividualTask(
                                nameAndSurname.getName(),
                                nameAndSurname.getSurname(),
                                group,
                                generateConfig)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tasks;
    }
    public static void generateIndividual(String group, String name, String surname, GenerateConfig config) throws Exception {
        StudentTask task = generateIndividualTask(name, surname, group, config);
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
                task = generateIndividualTask(name, surname, group, config);
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
        final List<StudentTask> studentTasks = generateTasksForGroup(students, group, config);
        final List<StudentTask> allTasks = DBWorker.getAllTasks();

        for (StudentTask task : studentTasks) {
            for (int i = 0; i < allTasks.size(); i++) {
                final TopologyCompareCriteria criteria = task.getTopology().whatIsLike(allTasks.get(i).getTopology());
                if (criteria.isWan() && criteria.isLan()) {
                    task = generateIndividualTask(task.getName(), task.getSurname(), task.getGroup(), config);
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


        for (StudentTask task : studentTasks) {
            DBWorker.addStudentTask(task);
        }

        SaveThread saveThread = new SaveThread(studentTasks);
        saveThread.start();
    }
}
