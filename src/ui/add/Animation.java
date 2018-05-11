package ui.add;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.TextField;
import javafx.util.Duration;

public class Animation {

    public static void shake(Node node){
        TranslateTransition translateTransition = new TranslateTransition(new Duration(50), node);
        translateTransition.setByX(0);
        translateTransition.setByX(15);
        translateTransition.setAutoReverse(true);
        translateTransition.setCycleCount(2);
        translateTransition.playFromStart();
    }
}
