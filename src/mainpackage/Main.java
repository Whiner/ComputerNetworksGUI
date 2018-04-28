package mainpackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.donntu.databaseworker.DBConnector;
import org.donntu.databaseworker.DBWorker;

import java.io.IOException;
import java.sql.SQLException;

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
            System.out.println(dbWorker.checkGroupInDB("АСУ-16"));
        } catch (SQLException e) {
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
