package org.donntu.knt.networks.ui.thread;

import javafx.application.Platform;
import org.donntu.knt.networks.databaseworker.StudentTask;
import org.donntu.knt.networks.drawer.GeneratorDrawer;
import org.donntu.knt.networks.ui.component.MessageBox;

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

    public SaveThread(List<StudentTask> tasks) {
        super(tasks);
    }

}
