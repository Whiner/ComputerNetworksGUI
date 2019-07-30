package org.donntu.knt.networks.ui.component;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;

public class NewWindowCreator {
    public static void create(String name, String title, boolean modality, boolean resizable, Stage owner, Integer width, Integer height, EventHandler<WindowEvent> onCloseRequest) throws IOException {
        Parent secondaryLayout;
        secondaryLayout = FXMLLoader.load(NewWindowCreator.class.getResource(name));

        Scene secondScene;
        if (width == null || height == null) {
            secondScene = new Scene(secondaryLayout);
        } else {
            secondScene = new Scene(secondaryLayout, width, height);
        }

        Stage newWindow = new Stage();
        newWindow.setTitle(title);
        newWindow.setScene(secondScene);
        if (modality) {
            newWindow.initModality(Modality.WINDOW_MODAL);
        }
        newWindow.initOwner(owner);
        newWindow.setResizable(resizable);
        newWindow.show();
        newWindow.setOnCloseRequest(onCloseRequest);
    }

    public static void create(String name, String title, boolean modality, boolean resizable, Stage owner, EventHandler<WindowEvent> onCloseRequest) throws IOException {
        NewWindowCreator.create(name, title, modality, resizable, owner, null, null, onCloseRequest);
    }
}
