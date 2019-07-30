package org.donntu.knt.networks.ui.component;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
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

    public static void attenuation(Node node){
        FadeTransition fadeTransition = new FadeTransition(new Duration(3000), node);
        fadeTransition.setFromValue(1.0);
        fadeTransition.setToValue(0.0);
        fadeTransition.play();
    }
}
