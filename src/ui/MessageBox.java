package ui;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

public class MessageBox {
    public static void error(String title, String header, String errorText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(errorText);
        alert.showAndWait();
    }
    public static void criticalError(String title, String header, String errorText){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(errorText);
        alert.showAndWait();
        System.exit(-1);
    }
    public static void information(String title, String header, String infoText){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(infoText);
        alert.showAndWait();
    }

    public static void confirmationWithClose(String title, String header, String confirmationText){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(confirmationText);
        if(alert.showAndWait().get() == ButtonType.OK)
        {
            System.exit(0);
        }
    }
}
