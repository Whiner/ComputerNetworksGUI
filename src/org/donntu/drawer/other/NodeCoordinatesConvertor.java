package org.donntu.drawer.other;

import org.donntu.drawer.DrawConfig;
import org.donntu.generator.Node;

public class NodeCoordinatesConvertor {
    public static Coordinates getCenter(Node node){
        return new Coordinates(node.getCellNumber_X() * DrawConfig.getInstance().getNodeWidth() * 2
                + DrawConfig.getInstance().getNodeWidth() / 2,
                node.getCellNumber_Y() * DrawConfig.getInstance().getNodeHeight() * 2
                        + DrawConfig.getInstance().getNodeHeight() / 2);
    }

    public static Coordinates getLeftTopCorner(Node node){
        return new Coordinates(node.getCellNumber_X() * DrawConfig.getInstance().getNodeWidth() * 2,
                node.getCellNumber_Y() * DrawConfig.getInstance().getNodeHeight() * 2);
    }


}
