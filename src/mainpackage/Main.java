package mainpackage;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    public static Stage primaryStage;
    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("../ui/main/forms.fxml"));
        primaryStage.setTitle("Компьютерные сети");
        primaryStage.setScene(new Scene(root));
        Main.primaryStage = primaryStage;
        //primaryStage.setResizable(false);
        primaryStage.setMinHeight(780);
        primaryStage.setMinWidth(1200);
        primaryStage.show();

        /*try {
            final List<StudentTask> tasks = DBWorker.getStudentTasks("АСУ-16", "Игорь", "Ввэдэнский");
            GeneratorDrawer.saveImage(
                    "task",
                     tasks.get(0).getSurname() + " "
                            + tasks.get(0).getName() + " "
                            + tasks.get(0).getGroup() + " "
                            + new GregorianCalendar().toString(),
                    GeneratorDrawer.drawStudentTask(tasks.get(1)));
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);*/
    }


    public static void main(String[] args) {
        launch(args);
    }
}
