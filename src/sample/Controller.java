package sample;


import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable {
    @FXML
    private ImageView topology_imageView;
    @FXML
    private ImageView top_panel_imageView;
    @FXML
    private Button button1;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Image image = new Image("images-fx/knt.png", 50,50,false,false);
        top_panel_imageView.setImage(image);
        button1.setOnAction(event -> {
            try {

                topology_imageView.setImage(pozhiloiClass.pozhiloiMetod());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }


}
