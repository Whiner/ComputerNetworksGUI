package org.donntu.generator.drawer.other;

import java.awt.*;
import java.util.List;

public class ColorComparator {
    private static int likeValue = 50;
    public static boolean isLikeTone(Color color1, Color color2){
        return (Math.abs(color1.getRed() - color2.getRed()) < likeValue)
                || (Math.abs(color1.getGreen() - color2.getGreen()) < likeValue)
                || (Math.abs(color1.getBlue() - color2.getBlue()) < likeValue);
    }

    public static boolean isContainLikeTone(Color color1, List<Color> colorList){
        for (Color c: colorList){
            if(isLikeTone(color1, c)){
                return true;
            }
        }
        return false;
    }
}
