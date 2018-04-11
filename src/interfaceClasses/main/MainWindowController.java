package interfaceClasses.main;


import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import interfaceClasses.main.generateButton.GenerateImage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.ImageView;

public class MainWindowController implements Initializable {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private ImageView top_panel_imageView;

    @FXML
    private Button generationButton;

    @FXML
    private Button generationParametersButton;

    @FXML
    private Button taskButton;

    @FXML
    private Button aboutProgramButton;

    @FXML
    private ComboBox<?> cb_WAN_node_quantity;

    @FXML
    private ComboBox<?> cb_WAN_max_rel_quantity;

    @FXML
    private ComboBox<?> cb_LAN_networks_quantity;

    @FXML
    private ComboBox<?> cb_LAN_nodes_quantity;

    @FXML
    private ComboBox<?> cb_LAN_max_rel_quantity;

    @FXML
    private ComboBox<?> cb_WAN_rel_with_LAN_quantity;

    @FXML
    private ComboBox<?> cb_LAN_networks_quantity1;

    @FXML
    private ComboBox<?> cb_LAN_nodes_quantity1;

    @FXML
    private ComboBox<?> cb_LAN_max_rel_quantity1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
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
        generationParametersButton.setOnAction(event -> {

        });
    }

    @FXML
    void initialize() {
        assert top_panel_imageView != null : "fx:id=\"top_panel_imageView\" was not injected: check your FXML file 'forms.fxml'.";
        assert generationButton != null : "fx:id=\"generationButton\" was not injected: check your FXML file 'forms.fxml'.";
        assert generationParametersButton != null : "fx:id=\"generationParametersButton\" was not injected: check your FXML file 'forms.fxml'.";
        assert taskButton != null : "fx:id=\"taskButton\" was not injected: check your FXML file 'forms.fxml'.";
        assert aboutProgramButton != null : "fx:id=\"aboutProgramButton\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_WAN_node_quantity != null : "fx:id=\"cb_WAN_node_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_WAN_max_rel_quantity != null : "fx:id=\"cb_WAN_max_rel_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_LAN_networks_quantity != null : "fx:id=\"cb_LAN_networks_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_LAN_nodes_quantity != null : "fx:id=\"cb_LAN_nodes_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_LAN_max_rel_quantity != null : "fx:id=\"cb_LAN_max_rel_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_WAN_rel_with_LAN_quantity != null : "fx:id=\"cb_WAN_rel_with_LAN_quantity\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_LAN_networks_quantity1 != null : "fx:id=\"cb_LAN_networks_quantity1\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_LAN_nodes_quantity1 != null : "fx:id=\"cb_LAN_nodes_quantity1\" was not injected: check your FXML file 'forms.fxml'.";
        assert cb_LAN_max_rel_quantity1 != null : "fx:id=\"cb_LAN_max_rel_quantity1\" was not injected: check your FXML file 'forms.fxml'.";

    }
}
