package nodegenerator;

public class NodeNavigation {
    private Node node;
    private Direction direction;

    @Override
    public boolean equals(Object object)
    {
        boolean equal = false;
        if (object != null && object instanceof NodeNavigation)
        {
            equal = (this.direction == ((NodeNavigation) object).direction) &&
            node.equals(((NodeNavigation) object).node);
        }

        return equal;
    }

    public NodeNavigation(Node node, Direction direction) {
        this.node = node;
        this.direction = direction;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }
}
