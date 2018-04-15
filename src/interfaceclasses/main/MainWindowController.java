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
import javafx.scene.control.Slider;
import javafx.scene.image.ImageView;
import javafx.scene.control.TextField;

public class MainWindowController implements Initializable {

    @FXML
    ImageView top_panel_imageView;

    @FXML
    Button generationButton;

    @FXML
    Button taskButton;

    @FXML
    Button aboutProgramButton;

    @FXML
    ComboBox<Integer> cb_WAN_nodes_quantity;

    @FXML
    ComboBox<Integer> cb_WAN_max_rel_quantity;

    @FXML
    ComboBox<Integer> cb_WAN_rel_with_LAN_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_networks_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_nodes_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_max_rel_quantity;

    @FXML
    Slider slider_RAM;

    @FXML
    TextField textField_RAM;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        new ElementsWorker(this).fillAll();

    }



}
