package interfaceClasses.main;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import mainPackage.pozhiloiClass;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private Button taskButton;

    @FXML
    private ImageView top_panel_imageView;

    @FXML
    private ImageView topology_imageView;

    @FXML
    private Button generationButton;

    @FXML
    private Button generationParametersButton;

    @FXML
    private Button aboutProgramButton;
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        generationButton.setOnAction(event -> {
            try {
                topology_imageView.setImage(pozhiloiClass.pozhiloiMetod());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
