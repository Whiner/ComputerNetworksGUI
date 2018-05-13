package ui.generate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Pair;
import org.donntu.databaseworker.DBConnector;
import org.donntu.databaseworker.DBWorker;
import org.donntu.generator.StudentTask;
import org.donntu.drawer.GeneratorDrawer;
import org.donntu.generator.Generator;
import org.donntu.generator.RAMCalculator;
import org.donntu.generator.configs.DefaultConfig;
import org.donntu.generator.configs.GenerateConfig;
import ui.Animation;
import ui.ComboBoxWorker;
import ui.MessageBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    @FXML
    ToggleGroup rb;

    @FXML
    Label successLabel;

    @FXML
    RadioButton groupRadiobutton;

    @FXML
    ComboBox<String> studentComboBox;

    @FXML
    ComboBox<String> groupComboBox;

    @FXML
    Button generateButton;

    @FXML
    RadioButton studentRadiobutton;

    @FXML
    Button defaultButton;

    @FXML
    Button saveButton;

    @FXML
    ComboBox<Integer> cb_WAN_max_rel_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_max_rel_quantity;

    @FXML
    Slider slider_RAM;

    @FXML
    Label studentLabel;

    @FXML
    ComboBox<Integer> cb_LAN_nodes_quantity;

    @FXML
    ComboBox<Integer> cb_WAN_nodes_quantity;

    @FXML
    TextField textField_RAM;

    @FXML
    HBox groupPane;

    @FXML
    ComboBox<Integer> cb_WAN_rel_with_LAN_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_networks_quantity;

    private GenerateConfig config = DefaultConfig.getDefaultConfig();


    private void radioButtonsSetOnAction() {
        rb.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (studentRadiobutton.isSelected()) {
                studentComboBox.setVisible(true);
                studentLabel.setVisible(true);
                if(groupComboBox.getValue() == null) {
                    studentComboBox.setDisable(true);
                }
            } else {
                studentComboBox.setVisible(false);
                studentLabel.setVisible(false);
            }
        });
    }

    private void setMemoryToSlider(){
        slider_RAM.setDisable(true);
        int ram = RAMCalculator.getRAM(
                config.getWanNodesQuantity()
                        + config.getLanQuantity() * config.getLanNodesQuantity());
        slider_RAM.setValue(ram);
        textField_RAM.setText(String.valueOf(ram));

    }

    private void comboBoxesActions(){

        cb_WAN_nodes_quantity.setOnHidden(event ->
                config.setWanNodesQuantity(cb_WAN_nodes_quantity.getValue())
        );
        cb_WAN_max_rel_quantity.setOnHidden(event ->
                config.setWanRelationsQuantity(cb_WAN_max_rel_quantity.getValue()));
        cb_WAN_rel_with_LAN_quantity.setOnHidden(event ->
                config.setNetworksRelationsQuantity(cb_WAN_rel_with_LAN_quantity.getValue()));
        cb_LAN_networks_quantity.setOnHidden(event -> {
            config.setLanQuantity(cb_LAN_networks_quantity.getValue());
            slider_RAM.setValue(slider_RAM.getValue() - 1);
            slider_RAM.setValue(slider_RAM.getValue() + 1);
        });
        cb_LAN_max_rel_quantity.setOnHidden(event ->
                config.setLanRelationsQuantity(cb_LAN_max_rel_quantity.getValue()));
        cb_LAN_nodes_quantity.setOnHidden(event ->
                config.setLanNodesQuantity(cb_LAN_nodes_quantity.getValue()));

        groupComboBox.setOnAction(event -> {
            if(groupComboBox.getValue() != null){
                studentComboBox.setDisable(false);
                try {
                    final List<Pair<String, String>> studentsByGroup = DBWorker.getStudentsByGroup(groupComboBox.getValue());
                    List<String> students = new ArrayList<>();
                    for (Pair<String, String> map: studentsByGroup){
                        students.add(map.getValue() + " " + map.getKey());
                    }
                    ComboBoxWorker.fillComboBox(studentComboBox, students);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void fillComboBoxes() throws Exception {
        ComboBoxWorker.fillComboBox(3, 10, config.getWanNodesQuantity(), cb_WAN_nodes_quantity);
        ComboBoxWorker.fillComboBox(2, 5, config.getWanRelationsQuantity(), cb_WAN_max_rel_quantity );
        ComboBoxWorker.fillComboBox(1, 3, config.getNetworksRelationsQuantity(), cb_WAN_rel_with_LAN_quantity );
        ComboBoxWorker.fillComboBox(3, 10, config.getLanNodesQuantity(), cb_LAN_nodes_quantity);
        ComboBoxWorker.fillComboBox(2, 5, config.getLanRelationsQuantity(), cb_LAN_max_rel_quantity);
        ComboBoxWorker.fillComboBox(1, 3, config.getLanQuantity(), cb_LAN_networks_quantity);
    }

    private void fillSlider(){
        slider_RAM.setMin(1024);
        slider_RAM.setMax(8192);
        slider_RAM.setBlockIncrement(512);
        slider_RAM.setShowTickMarks(true);
        slider_RAM.setMajorTickUnit(1024);
        slider_RAM.setMinorTickCount(4);
        textField_RAM.setText("Не задано");
        setMemoryToSlider();
        slider_RAM.setDisable(false);
        slider_RAM.valueProperty().addListener((ov, old_val, new_val) -> {
            if(slider_RAM.isDisabled()){
                slider_RAM.setDisable(false);
                return;
            }
            textField_RAM.setText(String.valueOf(new_val.intValue()));
            int quantity;
            quantity = RAMCalculator.getNodeQuantityInWAN(new_val.intValue());//количество WAN узлов

            config.setWanNodesQuantity(quantity);
            try {
                ComboBoxWorker.fillComboBox(3, quantity, quantity, cb_WAN_nodes_quantity);
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
                ComboBoxWorker.fillComboBox(3, quantity, quantity, cb_LAN_nodes_quantity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    private void buttonsSetOnAction() {
        generateButton.setOnAction(event -> {
            if (studentRadiobutton.isSelected()) {
                boolean empty = false;
                if (groupComboBox.getValue() == null) {
                    Animation.shake(groupComboBox);
                    empty = true;
                }
                if (studentComboBox.getValue() == null) {
                    Animation.shake(studentComboBox);
                    empty = true;
                }
                if(!empty){
                    final String[] splited = studentComboBox.getValue().split(" ");
                    try {
                        StudentTask task = Generator.generateIndividualTask(splited[1], splited[0], groupComboBox.getValue(), config);
                        GeneratorDrawer.getInstance().drawAndSaveStudentTask(task, "task/" + groupComboBox.getValue());
                        DBWorker.addStudentTask(task);
                        successLabel.setVisible(true);
                        Animation.attenuation(successLabel);
                    } catch (SQLException e) {
                        MessageBox.error("Ошибка",
                                "Занесение в базу данных вызвало ошибку",
                                "Текст ошибки: \n \"" + e.getMessage() + "\"");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            } else {
                if (groupComboBox.getValue() == null) {
                    Animation.shake(groupComboBox);
                } else {
                    try {
                        final List<Pair<String, String>> students = DBWorker.getStudentsByGroup(groupComboBox.getValue());
                        final List<StudentTask> studentTasks = Generator.generateTasksForGroup(students, groupComboBox.getValue(), config);
                        for (StudentTask task: studentTasks){
                            GeneratorDrawer.getInstance().drawAndSaveStudentTask(
                                    task,
                                    "task/" + groupComboBox.getValue());
                            DBWorker.addStudentTask(task);
                        }

                    } catch (SQLException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });
        saveButton.setOnAction(event -> {
            //вытащить все из базы по параметрам и сохранить в папку
        });
        defaultButton.setOnAction(event -> {
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

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            successLabel.setVisible(false);
            comboBoxesActions();
            fillComboBoxes();
            fillSlider();
            buttonsSetOnAction();

            DBWorker.setDbConnector(DBConnector.getInstance());

            ComboBoxWorker.fillComboBox(groupComboBox, DBWorker.getGroups());

            radioButtonsSetOnAction();
            studentRadiobutton.setSelected(true);
        } catch (SQLException e) {
            MessageBox.error("Ошибка соединения с базой данных",
                    "",
                    "Соединение с базой данных потеряно с ошибкой: \n\t\"" + e.getMessage() + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
