package ui.main;


import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import mainpackage.Main;
import org.donntu.databaseworker.DBWorker;
import org.donntu.GregorianCalendar;
import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.configs.DefaultConfig;
import org.donntu.generator.configs.GenerateConfig;
import javafx.collections.ObservableList;
import ui.MessageBox;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

class FormWorker {
    private Controller controller;
    private String notPointed = "Не указано";


    private GenerateConfig config;

    FormWorker(Controller controller){
        if(controller == null){
            throw new NullPointerException();
        }
        this.controller = controller;
        config = DefaultConfig.getDefaultConfig();
    }

    private TableColumn<StudentTask, String> surname;
    private TableColumn<StudentTask, String> name;
    private TableColumn<StudentTask, String> group;
    private TableColumn<StudentTask, GregorianCalendar> date;

    private boolean createdStudentColumns = false;

    private void createTableColumns(){
        if(!createdStudentColumns) {
            controller.table.getColumns().clear();

            surname = new TableColumn<>("Фамилия");
            name = new TableColumn<>("Имя");
            group = new TableColumn<>("Группа");
            date = new TableColumn<>("Дата создания");

            surname.setCellValueFactory(new PropertyValueFactory<>("surname"));
            name.setCellValueFactory(new PropertyValueFactory<>("name"));
            group.setCellValueFactory(new PropertyValueFactory<>("group"));
            date.setCellValueFactory(new PropertyValueFactory<>("creationDate"));

            surname.setMinWidth(200);
            name.setMinWidth(200);
            group.setMinWidth(200);
            date.setMinWidth(200);

            createdStudentColumns = true;
            controller.table.getColumns().addAll(group, surname, name, date);
        }
    }

    private void refreshDataOnTable() throws SQLException {
        controller.table.getItems().clear();
        ObservableList<StudentTask> tableTaskStruct = FXCollections.observableArrayList();
        final List<HashMap<String, String>> students = DBWorker.getStudentsTask();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-dd-DD");
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
        controller.table.setItems(tableTaskStruct);
    } //сомнительно каждый раз по новой загружать в таблицу



    void fillAll() {
        try {
            createTableColumns();
            refreshDataOnTable();
        } catch (SQLException e) {
            MessageBox.confirmationWithClose("Ошибка соединения с базой данных",
                    "Продолжить?",
                    "Работа с базой данных завершена с ошибкой: \n\t \"" + e.getMessage() +
                            "\"\nНажмите \"ОК\", чтобы продолжить работу с программой. " +
                            "Данные не будут сохранены в базу и будут существовать только на вашем компьютере.");
            controller.addButton.setDisable(true);
        }
    }
}
