package interfaceClasses.main;


import interfaceClasses.main.generateButton.GenerateImage;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;


import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;



public class MainWindowController implements Initializable {
    @FXML
    private Button taskButton;

    @FXML
    private ImageView top_panel_imageView;

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
                GenerateImage.generate();
                GenerateImage.SaveImage("dfd");

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        generationParametersButton.setOnAction(event -> {

        });
    }


}
