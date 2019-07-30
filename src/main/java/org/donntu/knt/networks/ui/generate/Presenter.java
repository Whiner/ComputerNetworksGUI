package org.donntu.knt.networks.ui.generate;

import org.donntu.knt.networks.GregorianCalendar;
import org.donntu.knt.networks.databaseworker.DBConnector;
import org.donntu.knt.networks.databaseworker.DBWorker;
import org.donntu.knt.networks.databaseworker.Student;
import org.donntu.knt.networks.databaseworker.StudentTask;
import org.donntu.knt.networks.generator.RAMCalculator;
import org.donntu.knt.networks.generator.configs.DefaultConfig;
import org.donntu.knt.networks.generator.configs.GenerateConfig;
import org.donntu.knt.networks.ui.component.Animation;
import org.donntu.knt.networks.ui.component.ComboBoxWorker;
import org.donntu.knt.networks.ui.component.MessageBox;
import org.donntu.knt.networks.ui.controller.GenerateController;
import org.donntu.knt.networks.ui.thread.SaveThread;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Presenter {
    private GenerateController generateController;
    private Priority priority = Priority.NONE;
    private enum Priority{LANCB, WANCB, NONE}

    private final int minNodesQuantity = 3;


    public Presenter(GenerateController generateController) throws Exception {
        this.generateController = generateController;
        generateController.getProgressBar().setVisible(false);
        generateController.getSuccessLabel().setVisible(false);
        generateController.getCb_WAN_nodes_quantity().setDisable(true);
        generateController.getCb_LAN_nodes_quantity().setDisable(true);
        comboBoxesActions();
        fillSlider();
        fillComboBoxes();
        buttonsSetOnAction();
        DBWorker.setDBConnector(DBConnector.getInstance());
        ComboBoxWorker.fillComboBox(generateController.getGroupComboBox(), DBWorker.getGroups());
        radioButtonsSetOnAction();
        generateController.getStudentRadiobutton().setSelected(true);
    }

    private GenerateConfig config = DefaultConfig.getDefaultConfig();

    private void radioButtonsSetOnAction() {
        generateController.getRb().selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (generateController.getStudentRadiobutton().isSelected()) {
                generateController.getStudentComboBox().setVisible(true);
                generateController.getStudentLabel().setVisible(true);
                if (generateController.getGroupComboBox().getValue() == null) {
                    generateController.getStudentComboBox().setDisable(true);
                }
            } else {
                generateController.getStudentComboBox().setVisible(false);
                generateController.getStudentLabel().setVisible(false);
            }
        });
    }

    private void comboBoxesActions() {

        generateController.getCb_WAN_nodes_quantity().valueProperty().addListener((observable, oldValue, newValue) -> {
            if (generateController.getCb_WAN_nodes_quantity().isDisabled()) {
                generateController.getCb_WAN_nodes_quantity().setDisable(false);
                return;
            }
            if(priority == Priority.LANCB){
                return;
            }
            if(newValue!= null && !newValue.equals(oldValue)){
                try {
                    whenWanNodesQuantityChange();
                } catch (Exception e) {
                    MessageBox.error("Ошибка заполнения ComboBox");
                }
            }
            priority = Priority.NONE;
        });

        generateController.getCb_WAN_nodes_quantity().setOnHidden(event -> config.setWanNodesQuantity(generateController.getCb_WAN_nodes_quantity().getValue()));
        generateController.getCb_WAN_max_ports_quantity().setOnHidden(event ->
                config.setWanPortsQuantity(generateController.getCb_WAN_max_ports_quantity().getValue()));
        generateController.getCb_WAN_ports_with_LAN_quantity().setOnHidden(event ->
                config.setNetworksPortsQuantity(generateController.getCb_WAN_ports_with_LAN_quantity().getValue()));
        generateController.getCb_LAN_networks_quantity().setOnHidden(event -> {
            config.setLanNetworksQuantity(generateController.getCb_LAN_networks_quantity().getValue());
            generateController.getSlider_RAM().setValue(generateController.getSlider_RAM().getValue() - 1);
            generateController.getSlider_RAM().setValue(generateController.getSlider_RAM().getValue() + 1);
        });
        generateController.getCb_LAN_max_ports_quantity().setOnHidden(event -> config.setLanPortsQuantity(generateController.getCb_LAN_max_ports_quantity().getValue()));
        generateController.getCb_LAN_nodes_quantity().valueProperty().addListener((observable, oldValue, newValue) -> {
            if (generateController.getCb_LAN_nodes_quantity().isDisabled()) {
                generateController.getCb_LAN_nodes_quantity().setDisable(false);
                return;
            }
            if(priority == Priority.WANCB){
                return;
            }
            if(newValue!= null && !newValue.equals(oldValue)) {
                try {
                    whenLanNodesQuantityChange();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            priority = Priority.NONE;
        });
        generateController.getCb_LAN_nodes_quantity().setOnHidden(event -> {
            config.setLanNodesQuantity(generateController.getCb_LAN_nodes_quantity().getValue());
        });

        generateController.getGroupComboBox().setOnAction(event -> {
            if (generateController.getGroupComboBox().getValue() != null) {
                generateController.getStudentComboBox().setDisable(false);
                try {
                    final List<Student> studentsByGroup = DBWorker.getStudentsByGroup(generateController.getGroupComboBox().getValue());
                    List<String> students = new ArrayList<>();
                    for (Student student : studentsByGroup) {
                        students.add(student.getSurname() + " " + student.getName());
                    }
                    ComboBoxWorker.fillComboBox(generateController.getStudentComboBox(), students);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void fillComboBoxes() throws Exception {
        config.setWanNodesQuantity(RAMCalculator.getNodeQuantityInWAN(
                (int) generateController.getSlider_RAM().getValue(),
                (int) generateController.getSlider_RAM().getValue() / RAMCalculator.getNodeRAM() / 2 / config.getLanNetworksQuantity(),
                config.getLanNetworksQuantity()));
        ComboBoxWorker.fillComboBox(minNodesQuantity,
                config.getWanNodesQuantity(),
                config.getWanNodesQuantity(),
                generateController.getCb_WAN_nodes_quantity());
        ComboBoxWorker.fillComboBox(2, 5, config.getWanPortsQuantity(), generateController.getCb_WAN_max_ports_quantity());
        ComboBoxWorker.fillComboBox(1, 3, config.getNetworksPortsQuantity(), generateController.getCb_WAN_ports_with_LAN_quantity());

        config.setLanNodesQuantity(RAMCalculator.getNodeQuantityInLAN(
                (int) generateController.getSlider_RAM().getValue(),
                config.getWanNodesQuantity(),
                config.getLanNetworksQuantity()));

        ComboBoxWorker.fillComboBox(minNodesQuantity,
                config.getLanNodesQuantity(),
                config.getLanNodesQuantity(),
                generateController.getCb_LAN_nodes_quantity());
        ComboBoxWorker.fillComboBox(2, 5, config.getLanPortsQuantity(), generateController.getCb_LAN_max_ports_quantity());
        ComboBoxWorker.fillComboBox(2, 3, config.getLanNetworksQuantity(), generateController.getCb_LAN_networks_quantity());
    }

    private void whenWanNodesQuantityChange() throws Exception {

        int maxLanQuantity = RAMCalculator.getNodeQuantityInLAN(
                (int) generateController.getSlider_RAM().getValue(),
                generateController.getCb_WAN_nodes_quantity().getValue(),
                generateController.getCb_LAN_networks_quantity().getValue());
        int min = 3;
        int value = generateController.getCb_LAN_nodes_quantity().getValue();
        if(value > maxLanQuantity){
            value = maxLanQuantity;
        }
        if(maxLanQuantity < min){
            min = maxLanQuantity;
        }
        priority = Priority.WANCB;
        ComboBoxWorker.fillComboBox(
                min,
                maxLanQuantity,
                value,
                generateController.getCb_LAN_nodes_quantity());
    }

    private void whenLanNodesQuantityChange() throws Exception {

        int maxWanQuantity = RAMCalculator.getNodeQuantityInWAN(
                (int) generateController.getSlider_RAM().getValue(),
                generateController.getCb_LAN_nodes_quantity().getValue(),
                generateController.getCb_LAN_networks_quantity().getValue());
        int min = minNodesQuantity;
        int value = generateController.getCb_WAN_nodes_quantity().getValue();
        if(maxWanQuantity < min){
            min = maxWanQuantity;
        }
        if(value > maxWanQuantity){
            value = maxWanQuantity;
        }
        priority = Priority.LANCB;
        ComboBoxWorker.fillComboBox(
                min,
                maxWanQuantity,
                value,
                generateController.getCb_WAN_nodes_quantity());
    }


    private void fillSlider(){
        generateController.getSlider_RAM().setMin(1024);
        generateController.getSlider_RAM().setMax(8192);
        generateController.getSlider_RAM().setBlockIncrement(512);
        generateController.getSlider_RAM().setShowTickMarks(true);
        generateController.getSlider_RAM().setMajorTickUnit(1024);
        generateController.getSlider_RAM().setMinorTickCount(4);

        generateController.getTextField_RAM().setEditable(false);
        generateController.getSlider_RAM().setValue(2048);
        generateController.getTextField_RAM().setText("" + (int) generateController.getSlider_RAM().getValue());
        generateController.getSlider_RAM().setDisable(false);
        generateController.getSlider_RAM().valueProperty().addListener((ov, old_val, new_val) -> {
            if (generateController.getSlider_RAM().isDisabled()) {
                generateController.getSlider_RAM().setDisable(false);
                return;
            }
            generateController.getTextField_RAM().setText(String.valueOf(new_val.intValue()));

            int totalQuantity = RAMCalculator.getNodeQuantity(new_val.intValue());

            int wanQuantity = totalQuantity / 2;

            config.setWanNodesQuantity(wanQuantity);
            if(wanQuantity < minNodesQuantity){
                wanQuantity = minNodesQuantity;
            }
            priority = Priority.NONE;
            try {
                ComboBoxWorker.fillComboBox(minNodesQuantity, wanQuantity, wanQuantity, generateController.getCb_WAN_nodes_quantity());
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }

            int lanQuantity = (totalQuantity - wanQuantity) / config.getLanNetworksQuantity();
            config.setLanNodesQuantity(lanQuantity);
            try {
                if(lanQuantity < minNodesQuantity){
                    lanQuantity = minNodesQuantity;
                }
                ComboBoxWorker.fillComboBox(minNodesQuantity, lanQuantity, lanQuantity, generateController.getCb_LAN_nodes_quantity());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });


    }

    private void buttonsSetOnAction() {
        generateController.getGenerateButton().setOnAction(event -> {
            if (generateController.getStudentRadiobutton().isSelected()) {
                boolean empty = false;
                if (generateController.getGroupComboBox().getValue() == null) {
                    Animation.shake(generateController.getGroupComboBox());
                    empty = true;
                }
                if (generateController.getStudentComboBox().getValue() == null) {
                    Animation.shake(generateController.getStudentComboBox());
                    empty = true;
                }
                if(!empty) {
                    try{
                        final String[] splited = generateController.getStudentComboBox().getValue().split(" ");
                        int currentYear = new GregorianCalendar().get(Calendar.YEAR);
                        Model.generateIndividual(DBWorker.getAllTasksByYears(currentYear - 2, currentYear), generateController.getGroupComboBox().getValue(), splited[1], splited[0], config, true);
                        generateController.getSuccessLabel().setText("Успешно сгенерировано");
                        generateController.getSuccessLabel().setVisible(true);
                        Animation.attenuation(generateController.getSuccessLabel());
                    } catch (SQLException e) {
                        MessageBox.error("Занесение в базу данных вызвало ошибку/\n" +
                                "Текст ошибки: \n \"" + e.getMessage() + "\"");
                    } catch (Exception e) {
                        MessageBox.error("Текст ошибки: \n \"" + e.getMessage() + "\"");
                    }
                }

            } else {
                if (generateController.getGroupComboBox().getValue() == null) {
                    Animation.shake(generateController.getGroupComboBox());
                } else {
                    try {
                        Model.generateByGroup(generateController.getGroupComboBox().getValue(), config);
                        generateController.getSuccessLabel().setText("Успешно сгенерировано");
                        generateController.getSuccessLabel().setVisible(true);
                        Animation.attenuation(generateController.getSuccessLabel());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });

        generateController.getSaveButton().setOnAction(event -> {
            if (generateController.getStudentRadiobutton().isSelected()) {
                boolean empty = false;
                if (generateController.getGroupComboBox().getSelectionModel().getSelectedItem() == null) {
                    Animation.shake(generateController.getGroupComboBox());
                    empty = true;
                }
                if (generateController.getStudentComboBox().getSelectionModel().getSelectedItem() == null) {
                    Animation.shake(generateController.getStudentComboBox());
                    empty = true;
                }
                if (!empty) {
                    try {
                        generateController.getSuccessLabel().setText("Сохранение...");
                        generateController.getSuccessLabel().setVisible(true);
                        Animation.attenuation(generateController.getSuccessLabel());
                        String[] splited = generateController.getStudentComboBox().getSelectionModel().getSelectedItem().split(" ");
                        final List<StudentTask> tasks = DBWorker.getStudentTasks(
                                generateController.getGroupComboBox().getSelectionModel().getSelectedItem(),
                                splited[1],
                                splited[0]);
                        for (StudentTask task : tasks) {
                            task.setGroup(generateController.getGroupComboBox().getValue());
                        }
                        SaveThread thread = new SaveThread(tasks);
                        thread.start();
                        generateController.getSuccessLabel().setText("Сохранение в фоновом \nрежиме");
                        generateController.getSuccessLabel().setVisible(true);

                        Animation.attenuation(generateController.getSuccessLabel());
                    } catch (SQLException e) {
                        MessageBox.error("Ошибка считывания заданий с базы. Проверьте подключение к базе данных."
                                + "\n Текст ошибки: \"" + e.getMessage() + "\"");
                    } catch (Exception e) {
                        MessageBox.error("Ошибка сохранения заданий" +
                                "\n Текст ошибки: \"" + e.getMessage() + "\"");
                    }
                }
            } else {
                if (generateController.getGroupComboBox().getSelectionModel().getSelectedItem() == null) {
                    Animation.shake(generateController.getGroupComboBox());
                } else {
                    try {

                        final List<StudentTask> tasks = DBWorker.getTasksByGroup(generateController.getGroupComboBox().getSelectionModel().getSelectedItem());
                        for (StudentTask task : tasks) {
                            task.setGroup(generateController.getGroupComboBox().getValue());
                        }
                        SaveThread thread = new SaveThread(tasks);
                        thread.start();
                        generateController.getSuccessLabel().setText("Сохранение в фоне");
                        generateController.getSuccessLabel().setVisible(true);
                        Animation.attenuation(generateController.getSuccessLabel());
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

        generateController.getDefaultButton().setOnAction(event -> {
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
