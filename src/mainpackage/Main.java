package mainpackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.donntu.databaseworker.DBConnector;
import org.donntu.databaseworker.DBWorker;
import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.Generator;
import org.donntu.generator.Topology;
import org.donntu.generator.TopologyGenerator;
import org.donntu.generator.configs.DefaultConfig;
import org.donntu.generator.drawer.GeneratorDrawer;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../ui/main/forms.fxml"));
        primaryStage.setTitle("Компьютерные сети");
        primaryStage.setScene(new Scene(root));
        //primaryStage.setResizable(false);
        primaryStage.setMinHeight(780);
        primaryStage.setMinWidth(1200);
        primaryStage.show();

        primaryStage.close();
    }


    public static void main(String[] args) {
        launch(args);

        try {
            DBWorker dbWorker = new DBWorker(new DBConnector());
            StudentTask studentTask = Generator.generateIndividualTask(
                    "Саня", "Шляпик", "ИУС-15", DefaultConfig.getDefaultConfig());
            GeneratorDrawer drawer = new GeneratorDrawer(2000, 2000, true);
            drawer.drawTopology(studentTask.getTopology());
            drawer.saveImage("task/" + studentTask.getName()
                    + " " + studentTask.getSurname() + " " + studentTask.getGroup() +
                    "Прям щас" + ".png");
            dbWorker.addStudentTask(studentTask);

        } catch (Exception e) {
            e.printStackTrace();
        }

        //pozhiloiClass.pozhiloiMetod();

        /*Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(new File("E:/Projects/JavaProjects/ComputerNetworksGUI/1.png"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }*/
    }
}
