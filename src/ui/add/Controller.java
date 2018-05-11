package ui.add;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.donntu.databaseworker.DBConnector;
import org.donntu.databaseworker.DBWorker;
import ui.ComboBoxWorker;

import java.net.URL;

import java.util.ResourceBundle;



public class Controller implements Initializable {
    @FXML
    private ToggleGroup rb;

    @FXML
    private RadioButton groupRadiobutton;

    @FXML
    private TextField surnameTextbox;

    @FXML
    private TextField groupNameTextbox;

    @FXML
    private Button addButton;

    @FXML
    private TextField nameTextbox;

    @FXML
    private RadioButton studentRadiobutton;

    @FXML
    private ComboBox<String> groupsComboBox;

    @FXML
    private VBox studentPane;

    @FXML
    private HBox groupPane;

    private void radioButtonsSetOnAction(){
        rb.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(studentRadiobutton.isSelected()){
                studentPane.setVisible(true);
                groupPane.setVisible(false);
            } else {
                studentPane.setVisible(false);
                groupPane.setVisible(true);
            }
        });
    }



    private void buttonsSetOnAction(){
        addButton.setOnAction(event -> {
            if(studentRadiobutton.isSelected()) {
                boolean empty = false;
                if (nameTextbox.getText().isEmpty()) {
                    Animation.shake(nameTextbox);
                    empty = true;
                }
                if (surnameTextbox.getText().isEmpty()) {
                    Animation.shake(surnameTextbox);
                    empty = true;
                }
                if (groupsComboBox.getValue() == null) {
                    Animation.shake(groupsComboBox);
                    empty = true;
                }
                if (!empty) {
                    System.out.println("Всанармуль");
                    //DBWorker.addStudent(nameTextbox.getText(), surnameTextbox.getText(), groupsComboBox.getValue());
                }
            } else {
                if(groupNameTextbox.getText().isEmpty()){
                    Animation.shake(groupNameTextbox);
                } else {
                    System.out.println("Всанармална");
                    //DBWorker.addGroup(groupNameTextbox.getText());
                }
            }
        });
    }



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            buttonsSetOnAction();
            DBWorker.setDbConnector(DBConnector.getInstance());
            ComboBoxWorker.fillComboBox(groupsComboBox, DBWorker.getGroups());
            radioButtonsSetOnAction();
            studentRadiobutton.setSelected(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
