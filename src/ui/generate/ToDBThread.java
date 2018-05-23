package ui.generate;

import org.donntu.databaseworker.DBWorker;
import org.donntu.databaseworker.StudentTask;
import org.donntu.drawer.GeneratorDrawer;

import java.util.ArrayList;
import java.util.List;

public class ToDBThread extends CustomThread{

    @Override
    public void run() {
        try {
            for (StudentTask task: tasks) {
                DBWorker.addStudentTask(task);

            }
        } catch (Exception e) {
            exceptions.add(e);
        }
    }

    ToDBThread(List<StudentTask> tasks){
        super(tasks);
    }

}
