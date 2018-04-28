package ui.main;


import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.configs.DefaultConfig;
import org.donntu.generator.configs.GenerateConfig;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import org.donntu.generator.RAMCalculator;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;

class ElementsWorker {
    private MainWindowController controller;
    private String notPointed = "Не указано";

    private class ComboBoxValuesStruct {
        int min;
        int max;

        ComboBoxValuesStruct(int min, int max) {
            this.min = min;
            this.max = max;
        }

        void fillComboBox(ComboBox<Integer> comboBox, int value){
            comboBox.getItems().clear();
            comboBox.setItems(fillObsList(min, max));
            comboBox.setValue(value);
        }
    }

    private ComboBoxValuesStruct quantity_wan_nodes_CB = new ComboBoxValuesStruct(3, 10);
    private ComboBoxValuesStruct quantity_lan_nodes_CB = new ComboBoxValuesStruct(3, 10);
    private ComboBoxValuesStruct maxrelCB = new ComboBoxValuesStruct(2, 5);
    private ComboBoxValuesStruct t_CB = new ComboBoxValuesStruct(1, 3);


    private GenerateConfig config;

    ElementsWorker(MainWindowController controller){
        if(controller == null){
            throw new NullPointerException();
        }
        this.controller = controller;
        config = DefaultConfig.getDefaultConfig();
    }




    private void fillButtonActions(){
        controller.generationButton.setOnAction(event -> {
            try {

                Date date = new Date();
                GenerateTask.generate(config);
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
            fillComboBoxes();
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

    private ObservableList<Integer> fillObsList(int min, int max){
        ObservableList<Integer> integers = FXCollections.observableArrayList();
        for (int i = min; i <= max; i++){
            integers.add(i);
        }
        return integers;
    }


    private void fillComboBoxes(){
        quantity_wan_nodes_CB.fillComboBox(controller.cb_WAN_nodes_quantity, config.getWanNodesQuantity());
        maxrelCB.fillComboBox(controller.cb_WAN_max_rel_quantity, config.getWanRelationsQuantity());
        t_CB.fillComboBox(controller.cb_WAN_rel_with_LAN_quantity, config.getNetworksRelationsQuantity());
        quantity_wan_nodes_CB.fillComboBox(controller.cb_LAN_nodes_quantity,config.getLanNodesQuantity());
        maxrelCB.fillComboBox(controller.cb_LAN_max_rel_quantity, config.getLanRelationsQuantity());
        t_CB.fillComboBox(controller.cb_LAN_networks_quantity, config.getLanQuantity());
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
            quantity_wan_nodes_CB.max = quantity;
            quantity_wan_nodes_CB.fillComboBox(controller.cb_WAN_nodes_quantity, quantity);

            quantity = RAMCalculator.getNodeQuantityInLAN( //количество LAN узлов
                    new_val.intValue(),
                    RAMCalculator.getRAM(config.getWanNodesQuantity()),
                    config.getLanQuantity());
            config.setLanNodesQuantity(quantity);
            quantity_lan_nodes_CB.max = quantity;
            quantity_lan_nodes_CB.fillComboBox(controller.cb_LAN_nodes_quantity, quantity);

        });


    }

    void fillAll(){
        fillButtonActions();
        comboBoxesActions();
        fillComboBoxes();
        fillSlider();
    }
}
