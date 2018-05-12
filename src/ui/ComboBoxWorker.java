package ui;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;

import java.util.List;

public class ComboBoxWorker {

    public static void fillComboBox(int min, int max, int setValue, ComboBox<Integer> comboBox) throws Exception {
        if(min > max){
            throw new Exception("min должен быть меньше max");
        }
        comboBox.getItems().clear();
        comboBox.setItems(fillObsList(min, max));
        comboBox.setValue(setValue);
    }
    public static void fillComboBox(ComboBox<String> comboBox, List<String> list) {
        comboBox.getItems().clear();
        ObservableList<String> strings = FXCollections.observableArrayList();
        for (String string: list){
            strings.addAll(string);
        }
        comboBox.setItems(strings);
    }


    private static ObservableList<Integer> fillObsList(int min, int max){
        ObservableList<Integer> integers = FXCollections.observableArrayList();
        for (int i = min; i <= max; i++){
            integers.add(i);
        }
        return integers;
    }
}

