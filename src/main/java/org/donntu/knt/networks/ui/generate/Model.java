package org.donntu.knt.networks.ui.generate;

import javafx.util.Pair;
import org.donntu.knt.networks.GregorianCalendar;
import org.donntu.knt.networks.databaseworker.DBWorker;
import org.donntu.knt.networks.databaseworker.Student;
import org.donntu.knt.networks.databaseworker.StudentTask;
import org.donntu.knt.networks.drawer.GeneratorDrawer;
import org.donntu.knt.networks.generator.Generator;
import org.donntu.knt.networks.generator.Network;
import org.donntu.knt.networks.generator.configs.GenerateConfig;
import org.donntu.knt.networks.generator.field.Field;
import org.donntu.knt.networks.ui.component.MessageBox;
import org.donntu.knt.networks.ui.thread.SaveThread;

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


    public static StudentTask generateIndividual(List<StudentTask> comparedTasks, String group, String name, String surname, GenerateConfig config, boolean save) throws Exception { // проверка между собой еще
        if(comparedTasks == null){
            throw new NullPointerException();
        }
        StudentTask task = generateIndividualTask(name, surname, group, config);
        int currentYear = new GregorianCalendar().get(Calendar.YEAR);
        int error = 0;
        for (int i = 0; i < comparedTasks.size(); i++) {
            if(error > 2000){
                MessageBox.information("Сгенерировано не уникальное задание для "
                        + surname + " " + name + " (Похоже на "
                        + comparedTasks.get(i) + "). Измените параметры генерации, если уникальность необходима.");
                break;
            }

            Pair<Network, Network> similar = task.getTopology().findSimilar(comparedTasks.get(i).getTopology());
            if (similar != null && (currentYear - task.getCreationDate().get(Calendar.YEAR)) < 1) {
                task.setTopology(Generator.generateTask(config));
                error++;
                i = 0;
            }

        }
        if(save) {
            GeneratorDrawer.saveImage(
                    "task/" + group + "/",
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
            StudentTask task = generateIndividual(allTasks, student.getGroup(), student.getName(), student.getSurname(), config, false);
            studentTasks.add(task);
            allTasks.add(task);
        }

        SaveThread saveThread = new SaveThread(studentTasks);
        saveThread.start();
        return studentTasks;
    }
}
