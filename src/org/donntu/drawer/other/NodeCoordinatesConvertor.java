package org.donntu.drawer.other;

import org.donntu.generator.Node;
import org.donntu.drawer.DrawConfigs;

public class NodeCoordinatesConvertor {
    public static Coordinates getCenter(Node node){
        return new Coordinates(node.getCellNumber_X() * DrawConfigs.getInstance().getNodeWidth() * 2
                + DrawConfigs.getInstance().getNodeWidth() / 2,
                node.getCellNumber_Y() * DrawConfigs.getInstance().getNodeHeight() * 2
                        + DrawConfigs.getInstance().getNodeHeight() / 2);
    }

    public static Coordinates getLeftTopCorner(Node node){
        return new Coordinates(node.getCellNumber_X() * DrawConfigs.getInstance().getNodeWidth() * 2,
                node.getCellNumber_Y() * DrawConfigs.getInstance().getNodeHeight() * 2);
    }


}
