package ui.main;

import javafx.scene.image.Image;
import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.Generator;
import org.donntu.generator.configs.GenerateConfig;
import org.donntu.drawer.GeneratorDrawer;
import org.donntu.generator.field.Field;

import java.io.IOException;
import java.util.List;

public class GenerateTask {


    public static StudentTask getLastStudentTask() {
        return lastStudentTask;
    }

    private static StudentTask lastStudentTask;

    public static void saveImage(String imagePath) throws Exception {
        GeneratorDrawer.getInstance().saveImage(imagePath);
    }

    public static StudentTask generateIndividual(GenerateConfig config, String name, String surname, String group, String directory, String imageName) throws Exception {
        StudentTask task = Generator.generateIndividualTask(name, surname, group, config);
        GeneratorDrawer.getInstance().drawAndSaveStudentTask(task, directory, imageName);
        lastStudentTask = task;
        return task;
    }

}
