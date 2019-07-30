package org.donntu.knt.networks.generator;

import org.donntu.knt.networks.generator.generatorException.NodeRelationsException;
import org.donntu.knt.networks.generator.generatorException.OneselfConnection;

import java.util.ArrayList;
import java.util.List;

public class Node {
    private int relationsCount;
    private List<Node> connectedNodes;
    private int cellNumberX, cellNumberY;
    private int maxRelationsCount;
    private int id;

    public Node(int cellNumber_X, int cellNumber_Y, int id) {
        cellNumberX = cellNumber_X;
        cellNumberY = cellNumber_Y;
        this.id = id;
        connectedNodes = new ArrayList<>();
        maxRelationsCount = 88888888;
        relationsCount = 0;
    }

    public boolean deleteConnectedNode(int ID) {
        relationsCount--;
        for(Node t: connectedNodes)
            if(t.getId() == ID)
                return connectedNodes.remove(t);
        return false;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public List<Node> getConnectedNodes() {
        return connectedNodes;
    }

    public int getCellNumberX() {
        return cellNumberX;
    }

    public void setCellNumberX(int cellNumberX) {
        this.cellNumberX = cellNumberX;
    }

    public int getCellNumberY() {
        return cellNumberY;
    }

    public void setCellNumberY(int cellNumberY) {
        this.cellNumberY = cellNumberY;
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
        return this.cellNumberX == ((Node)object).cellNumberX
            && this.cellNumberY == ((Node)object).cellNumberY && this.id == ((Node)object).id;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    public int getRelationsCount() {
        return relationsCount;
    }

    public int getMaxRelationsCount() {
        return maxRelationsCount;
    }

    public void setMaxRelationsCount(int maxRelationsCount) throws Exception {
        if(maxRelationsCount < 0)
            throw new NodeRelationsException("Максимальное количество связей не может быть меньше 0");
        this.maxRelationsCount = maxRelationsCount;
    }

    public void connectNode(Node connectingNode, boolean forcibly) throws OneselfConnection, NodeRelationsException {
        if(connectingNode == null) {
            throw new NullPointerException("Соединяемый узел - null");
        }
        if(connectingNode.equals(this)) {
            System.out.println(this.id + " / " + connectingNode.id + " Самосоединение");
            throw new OneselfConnection("Соединение с самим собой");
        }
        if(!forcibly) {
            if (this.maxRelationsCount <= relationsCount
                    || connectingNode.maxRelationsCount <= connectingNode.relationsCount) {
                throw new NodeRelationsException("Максимальное количество связей достигнуто");
            }
        }

        if(this.connectedNodes.contains(connectingNode) || connectingNode.connectedNodes.contains(this)){
            System.out.println(this.id + " / " + connectingNode.id + " Уже существует");
            throw new NodeRelationsException("Связь уже существует");
        }

        connectedNodes.add(connectingNode);
        relationsCount++;
        connectingNode.connectedNodes.add(this);
        connectingNode.relationsCount++;
    }
}
