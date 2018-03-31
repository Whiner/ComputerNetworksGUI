package nodeGenerator;

import nodeGenerator.generatorException.NodeRelationsCountException;
import nodeGenerator.generatorException.OneselfConnection;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private NetworkType networkType;
    private int RelationsCount;
    private List<Node> ConnectedNodes;
    private int CellNumber_X, CellNumber_Y;
    private int MaxRelationsCount;
    private int ID;


    public Node(NetworkType networkType, int cellNumber_X, int cellNumber_Y, int ID) {
        this.networkType = networkType;
        CellNumber_X = cellNumber_X;
        CellNumber_Y = cellNumber_Y;
        this.ID = ID;
        ConnectedNodes = new ArrayList<>();
        MaxRelationsCount = 88888888;
        RelationsCount = 0;
    }



    /*public void ConnectNode(Node node, Direction direction) throws Exception {
        if(node == null)
            throw new NullPointerException("Node is null");
        if(node.equals(this))
            throw new OneselfConnection("Comparison of oneself node");
        if(this.MaxRelationsCount <= RelationsCount
                || node.MaxRelationsCount <= node.RelationsCount)
            throw new NodeRelationsCountException("Max relations count");
        NodeNavigation nodeNavigation = new NodeNavigation(node, direction);
        if(!ConnectedNodes.contains(nodeNavigation))
        {
            ConnectedNodes.add(nodeNavigation);
            RelationsCount++;
        }

        nodeNavigation = new NodeNavigation(this, direction.reverse());
        if(!node.ConnectedNodes.contains(nodeNavigation))
        {
            node.ConnectedNodes.add(nodeNavigation);
            node.RelationsCount++;
        }


    }
    */
    public boolean deleteConnectedNode(int ID) {
        RelationsCount--;
        for(Node t: ConnectedNodes)
            if(t.getID() == ID)
                return ConnectedNodes.remove(t);
        return false;
    }


    public Node() {
        ConnectedNodes = new ArrayList<>();
        RelationsCount = 0;
        MaxRelationsCount = 8888888;
    }

    public Node(NetworkType networkType, int ID) {
        this.ConnectedNodes = new ArrayList<>();
        this.networkType = networkType;
        this.ID = ID;
        this.RelationsCount = 0;
        MaxRelationsCount = 8888888;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public List<Node> getConnectedNodes() {
        return ConnectedNodes;
    }



    public int getCellNumber_X() {
        return CellNumber_X;
    }

    public void setCellNumber_X(int cellNumber_X) {
        CellNumber_X = cellNumber_X;
    }

    public int getCellNumber_Y() {
        return CellNumber_Y;
    }

    public void setCellNumber_Y(int cellNumber_Y) {
        CellNumber_Y = cellNumber_Y;
    }

    @Override
    public boolean equals(Object object)
    {
        boolean equal = false;
        if (object != null && object instanceof Node)
            equal = this.CellNumber_X == ((Node)object).CellNumber_X
            && this.CellNumber_Y == ((Node)object).CellNumber_Y && this.ID == ((Node)object).ID;
        return equal;
    }

    public int getRelationsCount() {
        return RelationsCount;
    }

    public int getMaxRelationsCount() {
        return MaxRelationsCount;
    }

    public void setMaxRelationsCount(int maxRelationsCount) throws Exception {
        if(maxRelationsCount < 0)
            throw new NodeRelationsCountException("Максимальное количество связей не может быть меньше 0");
        MaxRelationsCount = maxRelationsCount;
    }

    public void connectNode(Node connectingNode) throws OneselfConnection, NodeRelationsCountException {
        if(connectingNode == null)
            throw new NullPointerException("Соединяемый узел - null");
        if(connectingNode.equals(this))
            throw new OneselfConnection("Соединение с самим собой");
        if(this.MaxRelationsCount <= RelationsCount
                || connectingNode.MaxRelationsCount <= connectingNode.RelationsCount)
            throw new NodeRelationsCountException("Максимальное количество связей достигнуто");

        if(!ConnectedNodes.contains(connectingNode))
        {
            ConnectedNodes.add(connectingNode);
            RelationsCount++;
        }

        if(!connectingNode.ConnectedNodes.contains(connectingNode))
        {
            connectingNode.ConnectedNodes.add(connectingNode);
            connectingNode.RelationsCount++;
        }

    }
}
