package ui.main;


import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import mainpackage.Main;
import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.configs.DefaultConfig;
import org.donntu.generator.configs.GenerateConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import org.donntu.generator.RAMCalculator;
import ui.ComboBoxWorker;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;

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
        });

        controller.showStudentsButton.setOnAction(event -> {

        });

        controller.showGroupsButton.setOnAction(event -> {

        });

        controller.showTasksButton.setOnAction(event -> {

        });

        controller.taskButton.setOnAction(event -> {

        });

        controller.aboutProgramButton.setOnAction(event -> {

        });

        controller.generationButton.setOnAction(event -> {
            try {

                Date date = new Date();
                //GenerateTask.generateIndividual(config);
                StudentTask task = GenerateTask.getLastStudentTask();
                String lastPath = "task/"
                        + task.getName() + " " + task.getSurname()
                        + " " + task.getGroup() + " " + date.getTime() + ".png";
                GenerateTask.saveImage(lastPath);
                Desktop desktop = null;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                }
                try {
                    assert desktop != null;
                    desktop.open(new File(lastPath));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
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
        fillButtonActions();
        comboBoxesActions();
        fillComboBoxes();
        fillSlider();
    }
}
