package ui.generate;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.donntu.databaseworker.DBConnector;
import org.donntu.databaseworker.DBWorker;
import org.donntu.databaseworker.Student;
import org.donntu.databaseworker.StudentTask;
import org.donntu.drawer.GeneratorDrawer;
import org.donntu.generator.*;
import org.donntu.generator.configs.DefaultConfig;
import org.donntu.generator.configs.GenerateConfig;
import ui.Animation;
import ui.ComboBoxWorker;
import ui.MessageBox;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
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
    ComboBox<Integer> cb_WAN_max_ports_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_max_ports_quantity;

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
    ComboBox<Integer> cb_WAN_ports_with_LAN_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_networks_quantity;

    @FXML
    ProgressIndicator progressBar;


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
        cb_WAN_max_ports_quantity.setOnHidden(event ->
                config.setWanPortsQuantity(cb_WAN_max_ports_quantity.getValue()));
        cb_WAN_ports_with_LAN_quantity.setOnHidden(event ->
                config.setNetworksPortsQuantity(cb_WAN_ports_with_LAN_quantity.getValue()));
        cb_LAN_networks_quantity.setOnHidden(event -> {
            config.setLanQuantity(cb_LAN_networks_quantity.getValue());
            slider_RAM.setValue(slider_RAM.getValue() - 1);
            slider_RAM.setValue(slider_RAM.getValue() + 1);
        });
        cb_LAN_max_ports_quantity.setOnHidden(event ->
                config.setLanPortsQuantity(cb_LAN_max_ports_quantity.getValue()));
        cb_LAN_nodes_quantity.setOnHidden(event ->
                config.setLanNodesQuantity(cb_LAN_nodes_quantity.getValue()));

        groupComboBox.setOnAction(event -> {
            if(groupComboBox.getValue() != null){
                studentComboBox.setDisable(false);
                try {
                    final List<Student> studentsByGroup = DBWorker.getStudentsByGroup(groupComboBox.getValue());
                    List<String> students = new ArrayList<>();
                    for (Student student: studentsByGroup){
                        students.add(student.getSurname() + " " + student.getName());
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
        ComboBoxWorker.fillComboBox(2, 5, config.getWanPortsQuantity(), cb_WAN_max_ports_quantity);
        ComboBoxWorker.fillComboBox(1, 3, config.getNetworksPortsQuantity(), cb_WAN_ports_with_LAN_quantity);
        ComboBoxWorker.fillComboBox(3, 10, config.getLanNodesQuantity(), cb_LAN_nodes_quantity);
        ComboBoxWorker.fillComboBox(2, 5, config.getLanPortsQuantity(), cb_LAN_max_ports_quantity);
        ComboBoxWorker.fillComboBox(2, 3, config.getLanQuantity(), cb_LAN_networks_quantity);
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
                if(quantity < 3){
                    quantity = 3;
                }
                ComboBoxWorker.fillComboBox(3, quantity, quantity, cb_LAN_nodes_quantity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    private class ProgressIndicatorWorker {
        private double step;
        private double currentProgress;

        ProgressIndicatorWorker(int stepsCount){
            step = 100 / stepsCount;
            currentProgress = 0;
        }

        void start(){
            progressBar.setVisible(true);
            progressBar.setProgress(0.0);
        }

        void addStep(){
            if(currentProgress >= 100){
                progressBar.setVisible(false);
            } else {
                if(currentProgress >= 100 - step){
                    currentProgress = 100;
                } else {
                    currentProgress += step;
                }
                progressBar.setProgress(currentProgress);
            }
        }

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
                if(!empty) {
                    final String[] splited = studentComboBox.getValue().split(" ");
                    try {
                        StudentTask task = Generator.generateIndividualTask(splited[1], splited[0], groupComboBox.getValue(), config);
                        //StudentTask task = DBWorker.getTaskByID(38);
                        final List<StudentTask> allTasks = DBWorker.getAllTasks();

                        for (int i = 0; i < allTasks.size(); i++) {
                            if(allTasks.get(i).getCreationDate().get(Calendar.YEAR)
                                    != task.getCreationDate().get(Calendar.YEAR)){
                                continue;
                            }
                            final TopologyCompareCriteria criteria = task.getTopology().whatIsLike(allTasks.get(i).getTopology());
                            if (criteria.isWan() && criteria.isLan()) {
                                List<IP> ip = new ArrayList<>();
                                for (Network network: task.getTopology().getNetworks()) {
                                    ip.add(network.getIp().getCopy());
                                }
                                task = Generator.generateIndividualTask(splited[1], splited[0], groupComboBox.getValue(), config);
                                int j = 0;
                                for (Network network: task.getTopology().getNetworks()) {
                                    network.setIp(ip.get(j++));
                                }
                                i = -1;
                                continue;
                            }
                            if (criteria.isIp()) {
                                for (Network network : task.getTopology().getNetworks()) {
                                    TopologyGenerator.generateIPForNetwork(network);
                                }
                                i = -1;
                            }
                        }


                        GeneratorDrawer.saveImage(
                                "task/" + groupComboBox.getValue(),
                                task.toString(),
                                GeneratorDrawer.drawStudentTask(task));
                        DBWorker.addStudentTask(task);
                        successLabel.setText("Успешно сгенерировано");
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
                        final List<Student> students = DBWorker.getStudentsByGroup(groupComboBox.getValue());
                        final List<StudentTask> studentTasks = Generator.generateTasksForGroup(students, groupComboBox.getValue(), config);
                        final List<StudentTask> allTasks = DBWorker.getAllTasks();
                        for (StudentTask task: studentTasks) {
                            for (int i = 0; i < allTasks.size(); i++) {
                                final TopologyCompareCriteria criteria = task.getTopology().whatIsLike(allTasks.get(i).getTopology());
                                if (criteria.isWan() && criteria.isLan()) {
                                    task = Generator.generateIndividualTask(task.getName(), task.getSurname(), task.getGroup(), config);
                                    i = -1;
                                    continue;
                                }
                                if (criteria.isIp()) {
                                    for (Network network : task.getTopology().getNetworks()) {
                                        TopologyGenerator.generateIPForNetwork(network);
                                    }
                                    i = -1;
                                }
                            }
                        }
                        ProgressIndicatorWorker progress = new ProgressIndicatorWorker(studentTasks.size());
                        progress.start();
                        for (StudentTask task: studentTasks){
                            GeneratorDrawer.saveImage(
                                    "task/" + groupComboBox.getValue() + "/" + task.getCreationDate().toString(),
                                    task.toString(),
                                    GeneratorDrawer.drawStudentTask(task));
                            DBWorker.addStudentTask(task);
                            progress.addStep();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        saveButton.setOnAction(event -> {
            if(studentRadiobutton.isSelected()) {
                boolean empty = false;
                if (groupComboBox.getSelectionModel().getSelectedItem() == null) {
                    Animation.shake(groupComboBox);
                    empty = true;
                }
                if (studentComboBox.getSelectionModel().getSelectedItem() == null) {
                    Animation.shake(studentComboBox);
                    empty = true;
                }
                if (!empty) {
                    try {
                        String[] splited = studentComboBox.getSelectionModel().getSelectedItem().split(" ");
                        final List<StudentTask> tasks = DBWorker.getStudentTasks(
                                groupComboBox.getSelectionModel().getSelectedItem(),
                                splited[1],
                                splited[0]);
                        for (StudentTask task : tasks) {
                            GeneratorDrawer.saveImage("task/" + groupComboBox.getValue() + "/" + task.getSurname() + " " + task.getName(),
                                    task.toString(),
                                    GeneratorDrawer.drawStudentTask(task));
                        }

                        successLabel.setText("Успешно сохранено");
                        successLabel.setVisible(true);
                        Animation.attenuation(successLabel);
                    } catch (SQLException e) {
                        MessageBox.error("Ошибка",
                                "",
                                "Ошибка считывания заданий с базы. Проверьте подключение к базе данных."
                                        + "\n Текст ошибки: \"" + e.getMessage() + "\"");
                    } catch (Exception e) {
                        MessageBox.error("Ошибка",
                                "",
                                "Ошибка сохранения заданий" +
                                        "\n Текст ошибки: \"" + e.getMessage() + "\"");
                    }
                }
            } else {
                if (groupComboBox.getSelectionModel().getSelectedItem() == null) {
                    Animation.shake(groupComboBox);
                } else {
                    try {

                        final List<StudentTask> tasks = DBWorker.getTasksByGroup(groupComboBox.getSelectionModel().getSelectedItem());
                        for (StudentTask task : tasks) {
                            GeneratorDrawer.saveImage("task/" + groupComboBox.getValue() + "/" + task.getSurname() + " " + task.getName(),
                                    task.toString(),
                                    GeneratorDrawer.drawStudentTask(task));
                        }

                        successLabel.setText("Успешно сохранено");
                        successLabel.setVisible(true);
                        Animation.attenuation(successLabel);
                    } catch (SQLException e) {
                        MessageBox.error("Ошибка",
                                "",
                                "Ошибка считывания заданий с базы. Проверьте подключение к базе данных."
                                        + "\n Текст ошибки: \"" + e.getMessage() + "\"");
                    } catch (Exception e) {
                        MessageBox.error("Ошибка",
                                "",
                                "Ошибка сохранения заданий" +
                                        "\n Текст ошибки: \"" + e.getMessage() + "\"");
                    }
                }
            }
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
            progressBar.setVisible(false);
            successLabel.setVisible(false);
            comboBoxesActions();
            fillComboBoxes();
            fillSlider();
            buttonsSetOnAction();
            DBWorker.setDBConnector(DBConnector.getInstance());
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
