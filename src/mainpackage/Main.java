package mainpackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.donntu.databaseworker.DBConnector;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

public class Main extends Application {
    public static Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/ui/main/forms.fxml"));
        primaryStage.setTitle("Компьютерные сети");
        primaryStage.setScene(new Scene(root));

        primaryStage.getIcons().add(new Image("images/knt.png"));
        Main.primaryStage = primaryStage;
        primaryStage.setMinHeight(780);
        primaryStage.setMinWidth(1200);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> {
            try {
                DBConnector.getInstance().closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });

    }


    public static void main(String[] args) {
        launch(args);
    }
}
