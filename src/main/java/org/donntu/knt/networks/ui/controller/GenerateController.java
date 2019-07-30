package org.donntu.knt.networks.ui.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import lombok.Getter;
import org.donntu.knt.networks.ui.component.MessageBox;
import org.donntu.knt.networks.ui.generate.Presenter;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;


@Getter
public class GenerateController implements Initializable {
    @FXML
    private ToggleGroup rb;

    @FXML
    private Label successLabel;

    @FXML
    private RadioButton groupRadiobutton;

    @FXML
    private ComboBox<String> studentComboBox;

    @FXML
    private ComboBox<String> groupComboBox;

    @FXML
    private Button generateButton;

    @FXML
    private RadioButton studentRadiobutton;

    @FXML
    private Button defaultButton;

    @FXML
    private Button saveButton;

    @FXML
    private ComboBox<Integer> cb_WAN_max_ports_quantity;

    @FXML
    private ComboBox<Integer> cb_LAN_max_ports_quantity;

    @FXML
    private Slider slider_RAM;

    @FXML
    private Label studentLabel;

    @FXML
    private ComboBox<Integer> cb_LAN_nodes_quantity;

    @FXML
    private ComboBox<Integer> cb_WAN_nodes_quantity;

    @FXML
    private TextField textField_RAM;

    @FXML
    private HBox groupPane;

    @FXML
    private ComboBox<Integer> cb_WAN_ports_with_LAN_quantity;

    @FXML
    private ComboBox<Integer> cb_LAN_networks_quantity;

    @FXML
    private ProgressIndicator progressBar;



    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            new Presenter(this);
        } catch (SQLException e) {
            MessageBox.error( "Соединение с базой данных потеряно с ошибкой: \n\t\"" + e.getMessage() + "\"");
        } catch (Exception e) {
            MessageBox.error( "Сбой компонентов. Тект ошибки: \n\t\"" + e.getMessage() + "\"");
        }
    }


}
