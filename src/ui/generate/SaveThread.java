package ui.generate;

import javafx.application.Platform;
import org.donntu.databaseworker.StudentTask;
import org.donntu.drawer.GeneratorDrawer;
import ui.MessageBox;

import java.util.List;

public class SaveThread extends CustomThread {
    @Override
    public void run() {
        try {
            for (StudentTask task: tasks) {
                GeneratorDrawer.saveImage("task/" + task.getGroup(),
                        task.toString(),
                        GeneratorDrawer.drawStudentTask(task));
            }

            Platform.runLater(() ->
                    MessageBox.information("Сохранение завершено")
            );
        } catch (Exception e) {
            exceptions.add(e);
        }
    }

    SaveThread(List<StudentTask> tasks){
        super(tasks);
    }

}
