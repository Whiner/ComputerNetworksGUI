package mainPackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("../interfaceClasses/main/forms.fxml"));
        primaryStage.setTitle("Компьютерные сети");
        primaryStage.setScene(new Scene(root));
        primaryStage.setResizable(false);
        primaryStage.show();

        primaryStage.close();
    }


    public static void main(String[] args) throws Exception {
        launch(args);
        pozhiloiClass.pozhiloiMetod();

        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(new File("E:/Projects/JavaProjects/ComputerNetworksGUI/1.png"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
}
