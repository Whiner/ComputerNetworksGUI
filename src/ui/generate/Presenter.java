package ui.generate;

import org.donntu.databaseworker.DBConnector;
import org.donntu.databaseworker.DBWorker;
import org.donntu.databaseworker.Student;
import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.RAMCalculator;
import org.donntu.generator.configs.DefaultConfig;
import org.donntu.generator.configs.GenerateConfig;
import ui.Animation;
import ui.ComboBoxWorker;
import ui.MessageBox;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Presenter {
    private View view;

    public Presenter(View view) throws Exception {
        this.view = view;
        view.progressBar.setVisible(false);
        view.successLabel.setVisible(false);
        comboBoxesActions();
        fillComboBoxes();
        fillSlider();
        buttonsSetOnAction();
        DBWorker.setDBConnector(DBConnector.getInstance());
        ComboBoxWorker.fillComboBox(view.groupComboBox, DBWorker.getGroups());
        radioButtonsSetOnAction();
        view.studentRadiobutton.setSelected(true);
    }

    private GenerateConfig config = DefaultConfig.getDefaultConfig();

    private void radioButtonsSetOnAction() {
        view.rb.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (view.studentRadiobutton.isSelected()) {
                view.studentComboBox.setVisible(true);
                view.studentLabel.setVisible(true);
                if(view.groupComboBox.getValue() == null) {
                    view.studentComboBox.setDisable(true);
                }
            } else {
                view.studentComboBox.setVisible(false);
                view.studentLabel.setVisible(false);
            }
        });
    }

    private void setMemoryToSlider(){
        view.slider_RAM.setDisable(true);
        int ram = RAMCalculator.getRAM(
                config.getWanNodesQuantity()
                        + config.getLanQuantity() * config.getLanNodesQuantity());
        view.slider_RAM.setValue(ram);
        view.textField_RAM.setText(String.valueOf(ram));

    }

    private void comboBoxesActions(){

        view.cb_WAN_nodes_quantity.setOnHidden(event ->
                config.setWanNodesQuantity(view.cb_WAN_nodes_quantity.getValue())
        );
        view.cb_WAN_max_ports_quantity.setOnHidden(event ->
                config.setWanPortsQuantity(view.cb_WAN_max_ports_quantity.getValue()));
        view.cb_WAN_ports_with_LAN_quantity.setOnHidden(event ->
                config.setNetworksPortsQuantity(view.cb_WAN_ports_with_LAN_quantity.getValue()));
        view.cb_LAN_networks_quantity.setOnHidden(event -> {
            config.setLanQuantity(view.cb_LAN_networks_quantity.getValue());
            view.slider_RAM.setValue(view.slider_RAM.getValue() - 1);
            view.slider_RAM.setValue(view.slider_RAM.getValue() + 1);
        });
        view.cb_LAN_max_ports_quantity.setOnHidden(event ->
                config.setLanPortsQuantity(view.cb_LAN_max_ports_quantity.getValue()));
        view.cb_LAN_nodes_quantity.setOnHidden(event ->
                config.setLanNodesQuantity(view.cb_LAN_nodes_quantity.getValue()));

        view. groupComboBox.setOnAction(event -> {
            if(view.groupComboBox.getValue() != null){
                view.studentComboBox.setDisable(false);
                try {
                    final List<Student> studentsByGroup = DBWorker.getStudentsByGroup(view.groupComboBox.getValue());
                    List<String> students = new ArrayList<>();
                    for (Student student: studentsByGroup){
                        students.add(student.getSurname() + " " + student.getName());
                    }
                    ComboBoxWorker.fillComboBox(view.studentComboBox, students);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void fillComboBoxes() throws Exception {
        ComboBoxWorker.fillComboBox(3, 10, config.getWanNodesQuantity(), view.cb_WAN_nodes_quantity);
        ComboBoxWorker.fillComboBox(2, 5, config.getWanPortsQuantity(), view.cb_WAN_max_ports_quantity);
        ComboBoxWorker.fillComboBox(1, 3, config.getNetworksPortsQuantity(), view.cb_WAN_ports_with_LAN_quantity);
        ComboBoxWorker.fillComboBox(3, 10, config.getLanNodesQuantity(), view.cb_LAN_nodes_quantity);
        ComboBoxWorker.fillComboBox(2, 5, config.getLanPortsQuantity(), view.cb_LAN_max_ports_quantity);
        ComboBoxWorker.fillComboBox(2, 3, config.getLanQuantity(), view.cb_LAN_networks_quantity);
    }

    private void fillSlider(){
        view.slider_RAM.setMin(1024);
        view.slider_RAM.setMax(8192);
        view.slider_RAM.setBlockIncrement(512);
        view.slider_RAM.setShowTickMarks(true);
        view.slider_RAM.setMajorTickUnit(1024);
        view.slider_RAM.setMinorTickCount(4);
        view.textField_RAM.setText("Не задано");
        setMemoryToSlider();
        view.slider_RAM.setDisable(false);
        view.slider_RAM.valueProperty().addListener((ov, old_val, new_val) -> {
            if(view.slider_RAM.isDisabled()){
                view.slider_RAM.setDisable(false);
                return;
            }
            view.textField_RAM.setText(String.valueOf(new_val.intValue()));
            int quantity;
            quantity = RAMCalculator.getNodeQuantityInWAN(new_val.intValue());//количество WAN узлов

            config.setWanNodesQuantity(quantity);
            try {
                ComboBoxWorker.fillComboBox(3, quantity, quantity, view.cb_WAN_nodes_quantity);
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
                ComboBoxWorker.fillComboBox(3, quantity, quantity, view.cb_LAN_nodes_quantity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    private void buttonsSetOnAction() {
        view.generateButton.setOnAction(event -> {
            if (view.studentRadiobutton.isSelected()) {
                boolean empty = false;
                if (view.groupComboBox.getValue() == null) {
                    Animation.shake(view.groupComboBox);
                    empty = true;
                }
                if (view.studentComboBox.getValue() == null) {
                    Animation.shake(view.studentComboBox);
                    empty = true;
                }
                if(!empty) {
                    try{
                        final String[] splited = view.studentComboBox.getValue().split(" ");
                        Model.generateIndividual(view.groupComboBox.getValue(), splited[1], splited[0], config);
                        view.successLabel.setText("Успешно сгенерировано");
                        view.successLabel.setVisible(true);
                        Animation.attenuation(view.successLabel);
                    } catch (SQLException e) {
                        MessageBox.error("Занесение в базу данных вызвало ошибку/\n" +
                                "Текст ошибки: \n \"" + e.getMessage() + "\"");
                    } catch (Exception e) {
                        MessageBox.error("Текст ошибки: \n \"" + e.getMessage() + "\"");
                    }
                }

            } else {
                if (view.groupComboBox.getValue() == null) {
                    Animation.shake(view.groupComboBox);
                } else {
                    try {
                        Model.generateByGroup(view.groupComboBox.getValue(), config);
                        view.successLabel.setText("Успешно сгенерировано");
                        view.successLabel.setVisible(true);
                        Animation.attenuation(view.successLabel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        view.saveButton.setOnAction(event -> {
            if(view.studentRadiobutton.isSelected()) {
                boolean empty = false;
                if (view.groupComboBox.getSelectionModel().getSelectedItem() == null) {
                    Animation.shake(view.groupComboBox);
                    empty = true;
                }
                if (view.studentComboBox.getSelectionModel().getSelectedItem() == null) {
                    Animation.shake(view.studentComboBox);
                    empty = true;
                }
                if (!empty) {
                    try {
                        view.successLabel.setText("Сохранение...");
                        view.successLabel.setVisible(true);
                        Animation.attenuation(view.successLabel);
                        String[] splited = view.studentComboBox.getSelectionModel().getSelectedItem().split(" ");
                        final List<StudentTask> tasks = DBWorker.getStudentTasks(
                                view.groupComboBox.getSelectionModel().getSelectedItem(),
                                splited[1],
                                splited[0]);
                        for (StudentTask task : tasks) {
                            task.setGroup(view.groupComboBox.getValue());
                        }
                        SaveThread thread = new SaveThread(tasks);
                        thread.start();
                        view.successLabel.setText("Сохранение в фоновом \nрежиме");
                        view.successLabel.setVisible(true);

                        Animation.attenuation(view.successLabel);
                    } catch (SQLException e) {
                        MessageBox.error("Ошибка считывания заданий с базы. Проверьте подключение к базе данных."
                                + "\n Текст ошибки: \"" + e.getMessage() + "\"");
                    } catch (Exception e) {
                        MessageBox.error("Ошибка сохранения заданий" +
                                "\n Текст ошибки: \"" + e.getMessage() + "\"");
                    }
                }
            } else {
                if (view.groupComboBox.getSelectionModel().getSelectedItem() == null) {
                    Animation.shake(view.groupComboBox);
                } else {
                    try {

                        final List<StudentTask> tasks = DBWorker.getTasksByGroup(view.groupComboBox.getSelectionModel().getSelectedItem());
                        for (StudentTask task : tasks) {
                            task.setGroup(view.groupComboBox.getValue());
                        }
                        SaveThread thread = new SaveThread(tasks);
                        thread.start();
                        view.successLabel.setText("Сохранение в фоне");
                        view.successLabel.setVisible(true);
                        Animation.attenuation(view.successLabel);
                    } catch (SQLException e) {
                        MessageBox.error("Ошибка считывания заданий с базы. Проверьте подключение к базе данных."
                                + "\n Текст ошибки: \"" + e.getMessage() + "\"");
                    } catch (Exception e) {
                        MessageBox.error("Ошибка сохранения заданий" +
                                "\n Текст ошибки: \"" + e.getMessage() + "\"");
                    }
                }
            }
        });

        view.defaultButton.setOnAction(event -> {
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
}
