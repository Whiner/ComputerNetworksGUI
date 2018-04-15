package interfaceclasses.main;

import interfaceclasses.main.generateButton.GenerateConfig;
import interfaceclasses.main.generateButton.GenerateImage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import nodegenerator.RAMCalculator;

import java.awt.*;
import java.io.File;
import java.io.IOException;

public class ElementsWorker {
    private MainWindowController controller;
    private String notPointed = "Не указано";

    private class ComboBoxValuesStruct {
        int min;
        int max;

        public ComboBoxValuesStruct(int min, int max) {
            this.min = min;
            this.max = max;
        }

        void fillComboBox(ComboBox<Integer> comboBox, int value){
            comboBox.setItems(fillObsList(min, max));
            comboBox.setValue(value);
        }
    }

    ComboBoxValuesStruct countCB = new ComboBoxValuesStruct(3, 10);
    ComboBoxValuesStruct maxrelCB = new ComboBoxValuesStruct(2, 5);
    ComboBoxValuesStruct t_CB = new ComboBoxValuesStruct(1, 3);


    public ElementsWorker(MainWindowController controller){
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
        controller.cb_WAN_nodes_quantity.setOnAction(event ->
        {
            GenerateConfig.getInstance().setWanNodesQuantity(controller.cb_WAN_nodes_quantity.getValue());
            setMemoryToSlider();
        }
        );
        controller.cb_WAN_max_rel_quantity.setOnAction(event ->
                GenerateConfig.getInstance().setWanRelationsQuantity(controller.cb_WAN_max_rel_quantity.getValue()));
        controller.cb_WAN_rel_with_LAN_quantity.setOnAction(event ->
                GenerateConfig.getInstance().setNetworksRelationsQuantity(controller.cb_WAN_rel_with_LAN_quantity.getValue()));
        controller.cb_LAN_networks_quantity.setOnAction(event -> {
                GenerateConfig.getInstance().setLanQuantity(controller.cb_LAN_networks_quantity.getValue());
                setMemoryToSlider();
        });
        controller.cb_LAN_max_rel_quantity.setOnAction(event ->
                GenerateConfig.getInstance().setLanRelationsQuantity(controller.cb_LAN_max_rel_quantity.getValue()));
        controller.cb_LAN_nodes_quantity.setOnAction(event -> {
            GenerateConfig.getInstance().setLanNodesQuantity(controller.cb_LAN_nodes_quantity.getValue());
            setMemoryToSlider();
        });
    }

    private ObservableList<Integer> fillObsList(int min, int max){
        ObservableList<Integer> integers = FXCollections.observableArrayList();
        for (int i = min; i <= max; i++){
            integers.add(i);
        }
        return integers;
    }


    private void fillComboboxes(){
        countCB.fillComboBox(controller.cb_WAN_nodes_quantity, GenerateConfig.getInstance().getWanNodesQuantity());
        maxrelCB.fillComboBox(controller.cb_WAN_max_rel_quantity, GenerateConfig.getInstance().getWanRelationsQuantity());
        t_CB.fillComboBox(controller.cb_WAN_rel_with_LAN_quantity, GenerateConfig.getInstance().getNetworksRelationsQuantity());
        countCB.fillComboBox(controller.cb_LAN_nodes_quantity, GenerateConfig.getInstance().getLanNodesQuantity());
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
        controller.slider_RAM.valueProperty().addListener((ov, old_val, new_val) -> {
            if(controller.slider_RAM.isDisabled()){
                controller.slider_RAM.setDisable(false);
                return;
            }
            controller.textField_RAM.setText(String.valueOf(new_val.intValue()));
            int q;
            q = RAMCalculator.getNodeQuantityInWAN(new_val.intValue());//количество WAN узлов
            if(q >= countCB.min && q <= countCB.max){
                GenerateConfig.getInstance().setWanNodesQuantity(q);
                controller.cb_WAN_nodes_quantity.setValue(q);
            } else {
                if(q > countCB.max) {
                    GenerateConfig.getInstance().setWanNodesQuantity(10);
                    controller.cb_WAN_nodes_quantity.setValue(10);
                }
            }


            q = RAMCalculator.getNodeQuantityInLAN( //количество LAN узлов
                    new_val.intValue(),
                    RAMCalculator.getRAM(GenerateConfig.getInstance().getWanNodesQuantity()),
                    GenerateConfig.getInstance().getLanQuantity());
            if(q <= countCB.max){
                GenerateConfig.getInstance().setLanNodesQuantity(q);
                controller.cb_LAN_nodes_quantity.setValue(q);
            } else {
                GenerateConfig.getInstance().setLanNodesQuantity(10);
                controller.cb_LAN_nodes_quantity.setValue(10);
            }


        });


    }

    public void fillAll(){
        fillButtonActions();
        comboBoxesActions();
        fillComboboxes();
        fillSlider();
    }
}
