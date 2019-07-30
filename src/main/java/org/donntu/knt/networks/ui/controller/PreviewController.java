package org.donntu.knt.networks.ui.controller;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.donntu.knt.networks.databaseworker.StudentTask;
import org.donntu.knt.networks.drawer.GeneratorDrawer;
import org.donntu.knt.networks.drawer.ImageEditor;
import org.donntu.knt.networks.ui.component.MessageBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class PreviewController implements Initializable {

    @FXML
    ImageView imageView;

    @FXML
    Button saveButton;

    private static java.awt.Image awtImage;
    private static StudentTask studentTask;
    private static Image fxImage;

    public static void setStudentTask(StudentTask studentTask) throws Exception {
        if (studentTask != null) {
            PreviewController.studentTask = studentTask;
            awtImage = GeneratorDrawer.drawStudentTask(studentTask);
            fxImage = SwingFXUtils.toFXImage(
                    ImageEditor.imageToBufferedImage(
                            awtImage
                    ), null);

        } else {
            throw new NullPointerException();
        }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if(studentTask == null){
            return;
        }
        imageView.setImage(fxImage);
        saveButton.setOnAction(event -> {
            if(awtImage != null){
                try {
                    GeneratorDrawer.saveImage("task/" + studentTask.getGroup(),
                            studentTask.toString(),
                            awtImage);
                    MessageBox.information("Сохранено!");
                } catch (IOException e) {
                    MessageBox.error( "Сохранение произошло с ошибкой: \n " + e.getMessage());
                }
            }
        });
    }
}
