package org.donntu.knt.networks.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Hyperlink;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class AboutProgramController implements Initializable {
    @FXML
    Hyperlink hyperlink;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        hyperlink.setOnAction(event -> {
            if(Desktop.isDesktopSupported())
            {
                try {
                    Desktop.getDesktop().browse(
                            new URI("https://github.com/Whiner/ComputerNetworksGUI/tree/algorithm_second_variant")
                    );
                } catch (IOException | URISyntaxException e) {
                    e.printStackTrace();
                }
                hyperlink.setVisited(false);
            }
        });
    }
}
