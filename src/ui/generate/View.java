package ui.generate;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import org.donntu.databaseworker.DBConnector;
import org.donntu.databaseworker.DBWorker;
import org.donntu.databaseworker.Student;
import org.donntu.databaseworker.StudentTask;
import org.donntu.drawer.GeneratorDrawer;
import org.donntu.generator.*;
import org.donntu.generator.configs.DefaultConfig;
import org.donntu.generator.configs.GenerateConfig;
import ui.Animation;
import ui.ComboBoxWorker;
import ui.MessageBox;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;


public class View implements Initializable {
    @FXML
    ToggleGroup rb;

    @FXML
    Label successLabel;

    @FXML
    RadioButton groupRadiobutton;

    @FXML
    ComboBox<String> studentComboBox;

    @FXML
    ComboBox<String> groupComboBox;

    @FXML
    Button generateButton;

    @FXML
    RadioButton studentRadiobutton;

    @FXML
    Button defaultButton;

    @FXML
    Button saveButton;

    @FXML
    ComboBox<Integer> cb_WAN_max_ports_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_max_ports_quantity;

    @FXML
    Slider slider_RAM;

    @FXML
    Label studentLabel;

    @FXML
    ComboBox<Integer> cb_LAN_nodes_quantity;

    @FXML
    ComboBox<Integer> cb_WAN_nodes_quantity;

    @FXML
    TextField textField_RAM;

    @FXML
    HBox groupPane;

    @FXML
    ComboBox<Integer> cb_WAN_ports_with_LAN_quantity;

    @FXML
    ComboBox<Integer> cb_LAN_networks_quantity;

    @FXML
    ProgressIndicator progressBar;



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
