package interfaceclasses.main;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import interfaceclasses.main.generateButton.GenerateConfig;
import interfaceclasses.main.generateButton.GenerateImage;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;

public class MainWindowController implements Initializable {

    @FXML
    private ImageView top_panel_imageView;

    @FXML
    private Button generationButton;

    @FXML
    private Button taskButton;

    @FXML
    private Button aboutProgramButton;

    @FXML
    private ComboBox<Integer> cb_WAN_node_quantity;

    @FXML
    private ComboBox<Integer> cb_WAN_max_rel_quantity;

    @FXML
    private ComboBox<Integer> cb_WAN_rel_with_LAN_quantity;

    @FXML
    private ComboBox<Integer> cb_LAN_networks_quantity;

    @FXML
    private ComboBox<Integer> cb_LAN_nodes_quantity;

    @FXML
    private ComboBox<Integer> cb_LAN_max_rel_quantity;

    @FXML
    private ComboBox<Integer> cb_RAM;

    @FXML
    private ComboBox<Integer> cb_LAN_nodes_quantity1;

    @FXML
    private ComboBox<Integer> cb_LAN_max_rel_quantity1;

    private void fillButtonActions(){
        generationButton.setOnAction(event -> {
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

    private void comboBoxesActions(){

        cb_WAN_node_quantity.setOnAction(event ->
                GenerateConfig.getInstance().setWanNodesQuantity(cb_WAN_node_quantity.getValue()));
        cb_WAN_max_rel_quantity.setOnAction(event ->
                GenerateConfig.getInstance().setWanRelationsQuantity(cb_WAN_max_rel_quantity.getValue()));
        cb_WAN_rel_with_LAN_quantity.setOnAction(event ->
                GenerateConfig.getInstance().setNetworksRelationsQuantity(cb_WAN_rel_with_LAN_quantity.getValue()));
        cb_LAN_networks_quantity.setOnAction(event ->
                GenerateConfig.getInstance().setLanQuantity(cb_LAN_networks_quantity.getValue()));
        cb_LAN_max_rel_quantity.setOnAction(event ->
                GenerateConfig.getInstance().setLanRelationsQuantity(cb_LAN_max_rel_quantity.getValue()));
        cb_LAN_nodes_quantity.setOnAction(event ->
                GenerateConfig.getInstance().setLanNodesQuantity(cb_LAN_nodes_quantity.getValue()));
    }

    private void fillComboboxes(){

        ObservableList<Integer> nodecountlist = FXCollections.observableArrayList(3, 4, 5, 6, 7, 8, 9, 10);
        ObservableList<Integer> maxrelcount = FXCollections.observableArrayList(2, 3, 4, 5);
        ObservableList<Integer> ram = FXCollections.observableArrayList(1024, 2048, 4086, 8192);

        cb_WAN_node_quantity.setItems(nodecountlist);
        cb_WAN_node_quantity.setValue(GenerateConfig.getInstance().getWanNodesQuantity());

        cb_WAN_max_rel_quantity.setItems(maxrelcount);
        cb_WAN_max_rel_quantity.setValue(GenerateConfig.getInstance().getWanRelationsQuantity());

        cb_WAN_rel_with_LAN_quantity.setItems(FXCollections.observableArrayList(1, 2, 3));
        cb_WAN_rel_with_LAN_quantity.setValue(GenerateConfig.getInstance().getNetworksRelationsQuantity());

        cb_LAN_networks_quantity.setItems(FXCollections.observableArrayList(1, 2, 3));
        cb_LAN_networks_quantity.setValue(GenerateConfig.getInstance().getLanQuantity());

        cb_LAN_max_rel_quantity.setItems(maxrelcount);
        cb_LAN_max_rel_quantity.setValue(GenerateConfig.getInstance().getLanRelationsQuantity());

        cb_LAN_nodes_quantity.setItems(nodecountlist);
        cb_LAN_nodes_quantity.setValue(GenerateConfig.getInstance().getLanNodesQuantity());

        cb_RAM.setItems(ram);


    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        fillButtonActions();
        fillComboboxes();
        comboBoxesActions();
    }

    @FXML
    void initialize() {
        assert top_panel_imageView != null : "fx:id=\"top_panel_imageView\" was not injected: check your FXML file 'forms.fxml'.";
        assert generationButton != null : "fx:id=\"generationButton\" was not injected: check your FXML file 'forms.fxml'.";
        assert taskButton != null : "fx:id=\"taskButton\" was not injected: check your FXML file 'forms.fxml'.";
        assert aboutProgramButton != null : "fx:id=\"aboutProgramButton\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_WAN_node_quantity != null : "fx:id=\"cb_WAN_node_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_WAN_max_rel_quantity != null : "fx:id=\"cb_WAN_max_rel_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_LAN_networks_quantity != null : "fx:id=\"cb_LAN_networks_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_LAN_nodes_quantity != null : "fx:id=\"cb_LAN_nodes_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_LAN_max_rel_quantity != null : "fx:id=\"cb_LAN_max_rel_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_WAN_rel_with_LAN_quantity != null : "fx:id=\"cb_WAN_rel_with_LAN_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_LAN_nodes_quantity1 != null : "fx:id=\"cb_LAN_nodes_quantity1\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_LAN_max_rel_quantity1 != null : "fx:id=\"cb_LAN_max_rel_quantity1\" was not injected: check your FXML file 'forms.fxml'.";

    }
}
