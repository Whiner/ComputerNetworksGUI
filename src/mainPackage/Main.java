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
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 800, 550));
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
        /*networks = t.getNetworks();
        for (Network _t: networks){
            System.out.println("--------------------------------------------------------");
            System.out.println("Сеть " + _t.getType());
            List<Node> nodes = _t.getNodes();
            for (Node _tt: nodes){
                System.out.println("----------------------------");
                System.out.println("ID узла: " + _tt.getID() + " \nX = " + _tt.getCellNumber_X() + " Y = " + _tt.getCellNumber_Y());
                System.out.println("Соединения: ");
                for (NodeNavigation _ttt: _tt.getConnectedNodes()){
                    System.out.println("ID: " + _ttt.getNode().getID() + " Direction: " + _ttt.getDirection());
                }
                System.out.println();
            }

        }
*/
    }
}
