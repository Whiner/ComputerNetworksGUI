package ui.generate;

import org.donntu.databaseworker.Student;
import org.donntu.databaseworker.StudentTask;
import org.donntu.drawer.GeneratorDrawer;

import java.util.ArrayList;
import java.util.List;

public class SaveThread extends CustomThread {
    @Override
    public void run() {
        try {
            for (StudentTask task: tasks) {
                GeneratorDrawer.saveImage("task/" + task.getGroup() + "/" + task.getSurname() + " " + task.getName(),
                        task.toString(),
                        GeneratorDrawer.drawStudentTask(task));
            }
        } catch (Exception e) {
            exceptions.add(e);
        }
    }

    SaveThread(List<StudentTask> tasks){
        super(tasks);
    }

}
