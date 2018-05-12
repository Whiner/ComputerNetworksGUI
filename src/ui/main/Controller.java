package ui.main;


import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import org.donntu.databaseworker.StudentTask;

public class Controller implements Initializable {

    @FXML
    ImageView top_panel_imageView;

    @FXML
    Button generationButton;

    @FXML
    Button searchButton;

    @FXML
    TextField searchTextBox;

    @FXML
    Button aboutProgramButton;

    @FXML
    Button addButton;

    @FXML
    TableView<StudentTask> table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            new FormWorker(this).fillAll();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
