package org.donntu.knt.networks.drawer.other;

import org.donntu.knt.networks.drawer.DrawConfig;
import org.donntu.knt.networks.generator.Node;

public class NodeCoordinatesConvertor {
    public static Coordinates getCenter(Node node){
        return new Coordinates(node.getCellNumberX() * DrawConfig.getInstance().getNodeWidth() * 2
                + DrawConfig.getInstance().getNodeWidth() / 2,
                node.getCellNumberY() * DrawConfig.getInstance().getNodeHeight() * 2
                        + DrawConfig.getInstance().getNodeHeight() / 2);
    }

    public static Coordinates getLeftTopCorner(Node node){
        return new Coordinates(node.getCellNumberX() * DrawConfig.getInstance().getNodeWidth() * 2,
                node.getCellNumberY() * DrawConfig.getInstance().getNodeHeight() * 2);
    }


}
