package org.donntu.generator;

import org.donntu.generator.generatorException.NodeRelationsException;
import org.donntu.generator.generatorException.OneselfConnection;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private int RelationsCount;
    private List<Node> ConnectedNodes;
    private int CellNumber_X, CellNumber_Y;
    private int MaxRelationsCount;
    private int ID;


    Node(NetworkType networkType, int cellNumber_X, int cellNumber_Y, int ID) {
        CellNumber_X = cellNumber_X;
        CellNumber_Y = cellNumber_Y;
        this.ID = ID;
        ConnectedNodes = new ArrayList<>();
        MaxRelationsCount = 88888888;
        RelationsCount = 0;
    }


    public boolean deleteConnectedNode(int ID) {
        RelationsCount--;
        for(Node t: ConnectedNodes)
            if(t.getID() == ID)
                return ConnectedNodes.remove(t);
        return false;
    }


    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
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
        if (object == this) {
            return true;
        }
        if (object == null || object.getClass() != this.getClass()) {
            return false;
        }
        return this.CellNumber_X == ((Node)object).CellNumber_X
            && this.CellNumber_Y == ((Node)object).CellNumber_Y && this.ID == ((Node)object).ID;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public int getRelationsCount() {
        return RelationsCount;
    }

    public int getMaxRelationsCount() {
        return MaxRelationsCount;
    }

    public void setMaxRelationsCount(int maxRelationsCount) throws Exception {
        if(maxRelationsCount < 0)
            throw new NodeRelationsException("Максимальное количество связей не может быть меньше 0");
        MaxRelationsCount = maxRelationsCount;
    }


    public void connectNode(Node connectingNode, boolean forcibly) throws OneselfConnection, NodeRelationsException {
        if(connectingNode == null) {
            System.out.println(this.ID + " / " + connectingNode.ID + " NULL");
            throw new NullPointerException("Соединяемый узел - null");
        }
        if(connectingNode.equals(this)) {
            System.out.println(this.ID + " / " + connectingNode.ID + " Самосоединение");
            throw new OneselfConnection("Соединение с самим собой");
        }
        if(!forcibly) {
            if (this.MaxRelationsCount <= RelationsCount
                    || connectingNode.MaxRelationsCount <= connectingNode.RelationsCount) {
                throw new NodeRelationsException("Максимальное количество связей достигнуто");
            }
        }

        if(this.ConnectedNodes.contains(connectingNode) || connectingNode.ConnectedNodes.contains(this)){
            System.out.println(this.ID + " / " + connectingNode.ID + " Уже существует");
            throw new NodeRelationsException("Связь уже существует");
        }

        ConnectedNodes.add(connectingNode);
        RelationsCount++;
        connectingNode.ConnectedNodes.add(this);
        connectingNode.RelationsCount++;
    }
}
