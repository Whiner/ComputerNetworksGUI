package org.donntu.knt.networks.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.donntu.knt.networks.databaseworker.DBConnector;
import org.donntu.knt.networks.databaseworker.DBWorker;
import org.donntu.knt.networks.ui.component.Animation;
import org.donntu.knt.networks.ui.component.ComboBoxWorker;
import org.donntu.knt.networks.ui.component.MessageBox;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class AddController implements Initializable {
    @FXML
    ToggleGroup rb;

    @FXML
    Label successLabel;

    @FXML
    RadioButton groupRadiobutton;

    @FXML
    TextField surnameTextBox;

    @FXML
    TextField groupNameTextBox;

    @FXML
    Button addButton;

    @FXML
    TextField nameTextBox;

    @FXML
    RadioButton studentRadiobutton;

    @FXML
    ComboBox<String> groupsComboBox;

    @FXML
    VBox studentPane;

    @FXML
    HBox groupPane;

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
                if (nameTextBox.getText().isEmpty()) {
                    Animation.shake(nameTextBox);
                    empty = true;
                }
                if (surnameTextBox.getText().isEmpty()) {
                    Animation.shake(surnameTextBox);
                    empty = true;
                }
                if (groupsComboBox.getValue() == null) {
                    Animation.shake(groupsComboBox);
                    empty = true;
                }
                if (!empty) {
                    try {
                        if(!DBWorker.addStudent(
                                nameTextBox.getText().trim(),
                                surnameTextBox.getText().trim(),
                                groupsComboBox.getValue().trim())){
                            successLabel.setText("Уже существует в базе");
                        } else {
                            successLabel.setText("Успешно добавлено");
                        }
                        successLabel.setVisible(true);
                        Animation.attenuation(successLabel);
                        nameTextBox.clear();
                        surnameTextBox.clear();
                    } catch (SQLException e) {
                        MessageBox.error("Ошибка добавления студента в базу. Проверьте подключение к базе данных.");
                    }
                }
            } else {
                if(groupNameTextBox.getText().isEmpty()){
                    Animation.shake(groupNameTextBox);
                } else {
                    try {
                        if(!DBWorker.addGroup(groupNameTextBox.getText().trim())){
                            successLabel.setText("Уже существует в базе");
                        } else {
                            successLabel.setText("Успешно добавлено");
                        }
                        successLabel.setVisible(true);
                        Animation.attenuation(successLabel);
                        ComboBoxWorker.fillComboBox(groupsComboBox, DBWorker.getGroups());
                        groupNameTextBox.clear();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            successLabel.setVisible(false);
            buttonsSetOnAction();
            DBWorker.setDBConnector(DBConnector.getInstance());
            ComboBoxWorker.fillComboBox(groupsComboBox, DBWorker.getGroups());
            radioButtonsSetOnAction();
            studentRadiobutton.setSelected(true);
        } catch (SQLException e) {
            MessageBox.error("Соединение с базой данных потеряно с ошибкой: \n\t\"" + e.getMessage() + "\"");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
