package ui.main;

import javafx.scene.image.Image;
import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.Generator;
import org.donntu.generator.configs.GenerateConfig;
import org.donntu.generator.Topology;
import org.donntu.generator.TopologyGenerator;
import org.donntu.generator.drawer.DrawConfigs;
import org.donntu.generator.drawer.GeneratorDrawer;
import org.donntu.generator.field.Field;

import java.awt.*;
import java.io.IOException;
import java.util.Date;

public class GenerateTask {

    private static GeneratorDrawer drawer;
    private static Image lastImage;

    public static StudentTask getLastStudentTask() {
        return lastStudentTask;
    }

    private static StudentTask lastStudentTask;


    public static void saveImage(String imagePath) throws IOException {
        drawer.saveImage(imagePath);
    }

    public static void generate(GenerateConfig config) throws Exception {
        lastStudentTask = Generator.generateIndividualTask("Саня", "Шляпик", "ИС-16", config);
        drawer = new GeneratorDrawer(
                    Field.getInstance().getHeight(),
                    Field.getInstance().getWidth(),
                    true);
        drawer.drawTopology(lastStudentTask.getTopology());
        lastImage = drawer.getImage();
    }

    public static Image getLastImage() {
        return lastImage;
    }
}
