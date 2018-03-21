package sample;

import NodeGenerator.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        primaryStage.close();
    }


    public static void main(String[] args) throws Exception {
        launch(args);
        Field.GetInstance().setCells_Count(8);
        Field.GetInstance().setSizeBorderInPx(1000);
        Field.GetInstance().setMaxSectionsCount(3);
        Field.GetInstance().AddWAN_Section();
        Field.GetInstance().CreateLAN_Sections(2);


        Topology t = new Topology();

        try {
            t.AddNetwork(TopologyGenerator.GenerateWAN(10,4));
            t.AddNetwork(TopologyGenerator.GenerateLAN(10, 4));
            t.AddNetwork(TopologyGenerator.GenerateLAN(10, 4));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        Drawer drawer = new Drawer();
        List<Network> networks = t.getNetworks();
        for (Network _t: networks){
            drawer.DrawNetwork(_t);
        }
        drawer.SaveImage("E:/Projects/JavaProjects/ComputerNetworksGUI/1.png");

        Desktop desktop = null;
        if (Desktop.isDesktopSupported()) {
            desktop = Desktop.getDesktop();
        }
        try {
            desktop.open(new File("E:/Projects/JavaProjects/ComputerNetworksGUI/1.png"));
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        networks = t.getNetworks();
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

    }
}
