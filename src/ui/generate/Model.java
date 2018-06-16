package ui.generate;

import org.donntu.GregorianCalendar;
import org.donntu.databaseworker.DBWorker;
import org.donntu.databaseworker.Student;
import org.donntu.databaseworker.StudentTask;
import org.donntu.drawer.GeneratorDrawer;
import org.donntu.generator.*;
import org.donntu.generator.configs.GenerateConfig;
import org.donntu.generator.field.Field;
import ui.MessageBox;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Model {

    private static StudentTask generateIndividualTask(
            String name, String surname, String group, GenerateConfig generateConfig) throws Exception {
        try {
            return new StudentTask(Generator.generateTask(generateConfig),
                    name,
                    surname,
                    group,
                    new GregorianCalendar(),
                    Field.getInstance().getCellsCountX(),
                    Field.getInstance().getCellsCountY());
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

    public static StudentTask generateIndividual(List<StudentTask> comparedTasks, String group, String name, String surname, GenerateConfig config, boolean save) throws Exception { // проверка между собой еще
        if(comparedTasks == null){
            throw new NullPointerException();
        }
        StudentTask task = generateIndividualTask(name, surname, group, config);
        int currentYear = new GregorianCalendar().get(Calendar.YEAR);
        int error = 0;
        for (int i = 0; i < comparedTasks.size(); i++) {
            error++;
            if(error > 2000){
                MessageBox.error("Сгенерировано не уникальное задание. Измените параметры генерации, если уникальность необходима.");
                break;
            }

            final TopologyCompareCriteria criteria = task.getTopology().whatIsLike(comparedTasks.get(i).getTopology());
            if (criteria.isWan() && criteria.isLan()) {
                if (!criteria.isIp() && comparedTasks.get(i).getCreationDate().get(GregorianCalendar.YEAR) != currentYear ) {
                    System.out.println("Only networks");
                    continue;
                }
                System.out.println("Networks and ip");
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
            }

        }
        if(save) {
            GeneratorDrawer.saveImage(
                    "task/" + group + "/" + task.getSurname() + " " + task.getName(),
                    task.toString(),
                    GeneratorDrawer.drawStudentTask(task));
        }
        DBWorker.addStudentTask(task);
        return task;
    }

    public static List<StudentTask> generateByGroup(String group, GenerateConfig config) throws Exception {
        final List<Student> students = DBWorker.getStudentsByGroup(group);
        final List<StudentTask> studentTasks = new ArrayList<>();
        int currentYear = new GregorianCalendar().get(Calendar.YEAR);
        final List<StudentTask> allTasks = DBWorker.getAllTasksByYears(currentYear - 2, currentYear);
        for (Student student : students) {
            System.out.println("Цикл Генерация для группы");
            StudentTask task = generateIndividual(allTasks, student.getGroup(), student.getName(), student.getSurname(), config, false);
            studentTasks.add(task);
            allTasks.add(task);
        }


        /*for (StudentTask task : studentTasks) {
            DBWorker.addStudentTask(task);
        }*/

        SaveThread saveThread = new SaveThread(studentTasks);
        saveThread.start();
        return studentTasks;
    }
}
