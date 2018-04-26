package ui.main;

import ui.main.generateButton.GenerateConfig;
import ui.main.generateButton.GenerateImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import org.donntu.nodegenerator.RAMCalculator;

import java.awt.*;
import java.io.File;
import java.io.IOException;

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


    ElementsWorker(MainWindowController controller){
        if(controller == null){
            throw new NullPointerException();
        }
        this.controller = controller;
    }




    private void fillButtonActions(){
        controller.generationButton.setOnAction(event -> {
            try {
                GenerateImage.generate();
                GenerateImage.SaveImage("generated_image");
                Desktop desktop = null;
                if (Desktop.isDesktopSupported()) {
                    desktop = Desktop.getDesktop();
                }
                try {
                    assert desktop != null;
                    desktop.open(new File("E:/Projects/JavaProjects/ComputerNetworksGUI/generated_image.png"));
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }


    private void setMemoryToSlider(){
        controller.slider_RAM.setDisable(true);
        int ram = RAMCalculator.getRAM(
                GenerateConfig.getInstance().getWanNodesQuantity()
                + GenerateConfig.getInstance().getLanQuantity() * GenerateConfig.getInstance().getLanNodesQuantity());
        controller.slider_RAM.setValue(ram);
        controller.textField_RAM.setText(String.valueOf(ram));

    }

    private void comboBoxesActions(){

        controller.cb_WAN_nodes_quantity.setOnHidden(event ->
        {
            GenerateConfig.getInstance().setWanNodesQuantity(controller.cb_WAN_nodes_quantity.getValue());
        }
        );
        controller.cb_WAN_max_rel_quantity.setOnHidden(event ->
                GenerateConfig.getInstance().setWanRelationsQuantity(controller.cb_WAN_max_rel_quantity.getValue()));
        controller.cb_WAN_rel_with_LAN_quantity.setOnHidden(event ->
                GenerateConfig.getInstance().setNetworksRelationsQuantity(controller.cb_WAN_rel_with_LAN_quantity.getValue()));
        controller.cb_LAN_networks_quantity.setOnHidden(event -> {
            GenerateConfig.getInstance().setLanQuantity(controller.cb_LAN_networks_quantity.getValue());
            controller.slider_RAM.setValue(controller.slider_RAM.getValue() - 1);
            controller.slider_RAM.setValue(controller.slider_RAM.getValue() + 1);
        });
        controller.cb_LAN_max_rel_quantity.setOnHidden(event ->
                GenerateConfig.getInstance().setLanRelationsQuantity(controller.cb_LAN_max_rel_quantity.getValue()));
        controller.cb_LAN_nodes_quantity.setOnHidden(event -> {
            GenerateConfig.getInstance().setLanNodesQuantity(controller.cb_LAN_nodes_quantity.getValue());

        });
    }

    private ObservableList<Integer> fillObsList(int min, int max){
        ObservableList<Integer> integers = FXCollections.observableArrayList();
        for (int i = min; i <= max; i++){
            integers.add(i);
        }
        return integers;
    }


    private void fillComboBoxes(){
        quantity_wan_nodes_CB.fillComboBox(controller.cb_WAN_nodes_quantity, GenerateConfig.getInstance().getWanNodesQuantity());
        maxrelCB.fillComboBox(controller.cb_WAN_max_rel_quantity, GenerateConfig.getInstance().getWanRelationsQuantity());
        t_CB.fillComboBox(controller.cb_WAN_rel_with_LAN_quantity, GenerateConfig.getInstance().getNetworksRelationsQuantity());
        quantity_wan_nodes_CB.fillComboBox(controller.cb_LAN_nodes_quantity, GenerateConfig.getInstance().getLanNodesQuantity());
        maxrelCB.fillComboBox(controller.cb_LAN_max_rel_quantity, GenerateConfig.getInstance().getLanRelationsQuantity());
        t_CB.fillComboBox(controller.cb_LAN_networks_quantity, GenerateConfig.getInstance().getLanQuantity());
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

            GenerateConfig.getInstance().setWanNodesQuantity(quantity);
            quantity_wan_nodes_CB.max = quantity;
            quantity_wan_nodes_CB.fillComboBox(controller.cb_WAN_nodes_quantity, quantity);

            quantity = RAMCalculator.getNodeQuantityInLAN( //количество LAN узлов
                    new_val.intValue(),
                    RAMCalculator.getRAM(GenerateConfig.getInstance().getWanNodesQuantity()),
                    GenerateConfig.getInstance().getLanQuantity());
            GenerateConfig.getInstance().setLanNodesQuantity(quantity);
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
