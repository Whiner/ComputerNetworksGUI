package nodegenerator.drawer.other;

import nodegenerator.Node;
import nodegenerator.drawer.DrawConfigs;

public class NodeCoordinatesConvertor {
    public static Coordinates getCenter(Node node){
        return new Coordinates(node.getCellNumber_X() * DrawConfigs.getInstance().getNodeSize() * 2
                + DrawConfigs.getInstance().getNodeSize() / 2,
                node.getCellNumber_Y() * DrawConfigs.getInstance().getNodeSize() * 2
                        + DrawConfigs.getInstance().getNodeSize() / 2);
    }

    public static Coordinates getLeftTopCorner(Node node){
        return new Coordinates(node.getCellNumber_X() * DrawConfigs.getInstance().getNodeSize() * 2,
                node.getCellNumber_Y() * DrawConfigs.getInstance().getNodeSize() * 2);
    }


}
