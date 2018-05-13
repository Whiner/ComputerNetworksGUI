package ui.main;


import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mainpackage.Main;
import org.donntu.GregorianCalendar;
import org.donntu.databaseworker.DBWorker;
import org.donntu.generator.StudentTask;
import ui.MessageBox;

public class Controller implements Initializable {

    @FXML
    ImageView top_panel_imageView;

    @FXML
    Button generationButton;

    @FXML
    Button searchButton;

    @FXML
    TextField searchTextBox;

    @FXML
    Button aboutProgramButton;

    @FXML
    Button addButton;

    @FXML
    TableView<StudentTask> table;

    private boolean createdStudentColumns = false;

    private void createTableColumns(){
        if(!createdStudentColumns) {
            table.getColumns().clear();

            TableColumn<StudentTask, String> surname = new TableColumn<>("Фамилия");
            TableColumn<StudentTask, String> name = new TableColumn<>("Имя");
            TableColumn<StudentTask, String> group = new TableColumn<>("Группа");
            TableColumn<StudentTask, GregorianCalendar> date = new TableColumn<>("Дата создания");

            surname.setCellValueFactory(new PropertyValueFactory<>("surname"));
            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            group.setCellValueFactory(new PropertyValueFactory<>("group"));
            date.setCellValueFactory(new PropertyValueFactory<>("creationDate"));

            surname.setMinWidth(200);
            name.setMinWidth(200);
            group.setMinWidth(200);
            date.setMinWidth(200);

            createdStudentColumns = true;
            table.getColumns().addAll(group, surname, name, date);
        }
    }

    private void refreshDataOnTable() throws SQLException {
        table.getItems().clear();
        ObservableList<StudentTask> tableTaskStruct = FXCollections.observableArrayList();
        final List<HashMap<String, String>> students = DBWorker.getStudentsTaskList();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date tempDate;
        GregorianCalendar tempCalendar;
        for (HashMap<String, String> record: students){
            try {
                tempDate = dateFormat.parse(record.get("Дата"));
            } catch (ParseException e) {
                System.out.println("Date parse error");
                continue;
            }
            tempCalendar = new GregorianCalendar();
            tempCalendar.setTime(tempDate);
            tableTaskStruct.add(new StudentTask(
                    tempCalendar,
                    record.get("Имя"),
                    record.get("Фамилия"),
                    record.get("Группа")));
        }
        table.setItems(tableTaskStruct);
    } //сомнительно каждый раз по новой загружать в таблицу

    private void setOnButtonsActions() {
        addButton.setOnAction(event -> {
            Parent secondaryLayout;
            try {
                secondaryLayout = FXMLLoader.load(getClass().getResource("/ui/add/forms.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Scene secondScene = new Scene(secondaryLayout, 700, 460);
            Stage newWindow = new Stage();
            newWindow.setTitle("Добавить...");
            newWindow.setScene(secondScene);
            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.initOwner(Main.primaryStage);
            newWindow.setResizable(false);
            newWindow.show();
        });

        generationButton.setOnAction(event -> {
            Parent secondaryLayout;
            try {
                secondaryLayout = FXMLLoader.load(getClass().getResource("/ui/generate/forms.fxml"));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Scene secondScene = new Scene(secondaryLayout, 1200, 560);
            Stage newWindow = new Stage();
            newWindow.setTitle("Генерация");
            newWindow.setScene(secondScene);
            newWindow.initModality(Modality.WINDOW_MODAL);
            newWindow.initOwner(Main.primaryStage);
            newWindow.setResizable(false);
            newWindow.show();
            newWindow.setOnCloseRequest(event1 -> {
                try {
                    refreshDataOnTable();
                } catch (SQLException e) {
                    MessageBox.error("Ошибка",
                            "Ошибка обновления списка студентов.",
                            "");
                }
            });

        });
    }




    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            createTableColumns();
            refreshDataOnTable();
            setOnButtonsActions();
        } catch (SQLException e) {
            MessageBox.confirmationWithClose("Ошибка соединения с базой данных",
                    "Продолжить?",
                    "Работа с базой данных завершена с ошибкой: \n\t \"" + e.getMessage() +
                            "\"\nНажмите \"ОК\", чтобы продолжить работу с программой. " +
                            "Данные не будут сохранены в базу и будут существовать только на вашем компьютере.");
            addButton.setDisable(true);
        }
    }



}
