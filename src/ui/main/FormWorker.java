package ui.main;


import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import mainpackage.Main;
import org.donntu.databaseworker.DBWorker;
import org.donntu.databaseworker.GregorianCalendar;
import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.configs.DefaultConfig;
import org.donntu.generator.configs.GenerateConfig;
import javafx.collections.ObservableList;
import org.donntu.generator.RAMCalculator;
import ui.ComboBoxWorker;
import ui.MessageBox;

import java.io.IOException;
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



    private void fillButtonActions(){
        controller.addButton.setOnAction(event -> {
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
            newWindow.setOnCloseRequest(event1 -> {
                try {
                    //добавить обновления
                    refreshDataOnTable();
                } catch (SQLException e) {
                    MessageBox.error("Ошибка",
                            "Ошибка обновления списка студентов.",
                            "");
                }
            });
        });

        controller.aboutProgramButton.setOnAction(event -> {

        });

        controller.generationButton.setOnAction(event -> {
            try {

                StudentTask task = GenerateTask.generateIndividual(
                        config,
                        "Максим Владимирович",
                        "Привалов",
                        "Кафедра АСУ",
                        "task",
                        "MB");


            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        controller.defaultButton.setOnAction(event -> {
            config = DefaultConfig.getDefaultConfig();
            try {
                fillComboBoxes();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
            fillSlider();
        });

    }

    private void setMemoryToSlider(){
        controller.slider_RAM.setDisable(true);
        int ram = RAMCalculator.getRAM(
                config.getWanNodesQuantity()
                + config.getLanQuantity() * config.getLanNodesQuantity());
        controller.slider_RAM.setValue(ram);
        controller.textField_RAM.setText(String.valueOf(ram));

    }

    private void comboBoxesActions(){

        controller.cb_WAN_nodes_quantity.setOnHidden(event ->
                config.setWanNodesQuantity(controller.cb_WAN_nodes_quantity.getValue())
        );
        controller.cb_WAN_max_rel_quantity.setOnHidden(event ->
                config.setWanRelationsQuantity(controller.cb_WAN_max_rel_quantity.getValue()));
        controller.cb_WAN_rel_with_LAN_quantity.setOnHidden(event ->
                config.setNetworksRelationsQuantity(controller.cb_WAN_rel_with_LAN_quantity.getValue()));
        controller.cb_LAN_networks_quantity.setOnHidden(event -> {
            config.setLanQuantity(controller.cb_LAN_networks_quantity.getValue());
            controller.slider_RAM.setValue(controller.slider_RAM.getValue() - 1);
            controller.slider_RAM.setValue(controller.slider_RAM.getValue() + 1);
        });
        controller.cb_LAN_max_rel_quantity.setOnHidden(event ->
                config.setLanRelationsQuantity(controller.cb_LAN_max_rel_quantity.getValue()));
        controller.cb_LAN_nodes_quantity.setOnHidden(event ->
                config.setLanNodesQuantity(controller.cb_LAN_nodes_quantity.getValue()));
    }

    private void fillComboBoxes() throws Exception {
        ComboBoxWorker.fillComboBox(3, 10, config.getWanNodesQuantity(), controller.cb_WAN_nodes_quantity);
        ComboBoxWorker.fillComboBox(2, 5, config.getWanRelationsQuantity(), controller.cb_WAN_max_rel_quantity );
        ComboBoxWorker.fillComboBox(1, 3, config.getNetworksRelationsQuantity(), controller.cb_WAN_rel_with_LAN_quantity );
        ComboBoxWorker.fillComboBox(3, 10, config.getLanNodesQuantity(), controller.cb_LAN_nodes_quantity);
        ComboBoxWorker.fillComboBox(2, 5, config.getLanRelationsQuantity(), controller.cb_LAN_max_rel_quantity);
        ComboBoxWorker.fillComboBox(1, 3, config.getLanQuantity(), controller.cb_LAN_networks_quantity);
    }

    private void fillSlider(){
        controller.slider_RAM.setMin(1024);
        controller.slider_RAM.setMax(8192);
        controller.slider_RAM.setBlockIncrement(512);
        controller.slider_RAM.setShowTickMarks(true);
        controller.slider_RAM.setMajorTickUnit(1024);
        controller.slider_RAM.setMinorTickCount(4);
        controller.textField_RAM.setText(notPointed);
        setMemoryToSlider();
        controller.slider_RAM.setDisable(false);
        controller.slider_RAM.valueProperty().addListener((ov, old_val, new_val) -> {
            if(controller.slider_RAM.isDisabled()){
                controller.slider_RAM.setDisable(false);
                return;
            }
            controller.textField_RAM.setText(String.valueOf(new_val.intValue()));
            int quantity;
            quantity = RAMCalculator.getNodeQuantityInWAN(new_val.intValue());//количество WAN узлов

            config.setWanNodesQuantity(quantity);
            try {
                ComboBoxWorker.fillComboBox(3, quantity, quantity,controller.cb_WAN_nodes_quantity);
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            quantity = RAMCalculator.getNodeQuantityInLAN( //количество LAN узлов
                    new_val.intValue(),
                    RAMCalculator.getRAM(config.getWanNodesQuantity()),
                    config.getLanQuantity());
            config.setLanNodesQuantity(quantity);
            try {
                ComboBoxWorker.fillComboBox(3, quantity, quantity,controller.cb_LAN_nodes_quantity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    void fillAll() throws Exception {
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
        fillButtonActions();
        comboBoxesActions();
        fillComboBoxes();
        fillSlider();

    }
}
