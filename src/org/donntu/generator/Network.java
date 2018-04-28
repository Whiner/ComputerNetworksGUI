package org.donntu.generator;

import javafx.util.Pair;
import org.donntu.generator.generatorException.NodeExistException;
import org.donntu.generator.generatorException.NodeInterseptionException;
import org.donntu.generator.generatorException.NodeRelationsException;
import org.donntu.generator.generatorException.OneselfConnection;

import java.util.ArrayList;
import java.util.List;


public class Network {
    private NetworkType type;
    private List<Node> nodes;
    //private int maxNodeCount;
    private IP ip = new IP();
    private List<Network> connectedWith = new ArrayList<>();

    public int getCountRelations(){
        List<Pair<Node, Node>> pairList = new ArrayList<>();
        for (Node node : nodes) {
            for (Node connectNode : node.getConnectedNodes()) {
                if (!pairList.contains(new Pair<>(node, connectNode))
                        && !pairList.contains(new Pair<>(connectNode, node))) {
                    pairList.add(new Pair<>(node, connectNode));
                }
            }
        }
        return pairList.size();
    }

    public IP getIp() {
        return ip;
    }

    public void setIp(IP ip) {
        this.ip = ip;
    }

    public Node getLastNode(){
        return nodes.get(nodes.size() - 1);
    }
    public void setMaxNodeRelations(int maxNodeRelations) throws Exception {
        for(Node t: nodes){
            t.setMaxRelationsCount(maxNodeRelations);
        }
    }

    public Node getByCoord(int x, int y){
        for (Node t: nodes){
            if(t.getCellNumber_X() == x)
                if(t.getCellNumber_Y() == y)
                    return t;
        }
        return null;
    }

    public Node getNodeByID(int ID){
        for(Node t: nodes)
            if(t.getID() == ID)
                return t;
         return null;
    }
    public boolean isAllHaveMaxRelations(){
        if(nodes.isEmpty()){
            return false;
        }
        for (Node t: nodes){
            if(t.getRelationsCount() < t.getMaxRelationsCount())
                return false;
        }
        return true;
    }


    private boolean checkRelationIntersection(Node from, Node to){
        int t_x = Math.abs(from.getCellNumber_X() - to.getCellNumber_X());
        int t_y = Math.abs(from.getCellNumber_Y() - to.getCellNumber_Y());
        if(t_x == 0) {
            int start = Math.min(from.getCellNumber_Y(), to.getCellNumber_Y());
            for (int i = 1; i < t_y; i++) {
                if (getByCoord(from.getCellNumber_X(), start + i) != null) {
                    return true;
                }
            }
            return false;
        } else if(t_y == 0){
            int start = Math.min(from.getCellNumber_X(), to.getCellNumber_X());
            for (int i = 1; i < t_x; i++){
                if(getByCoord(start + i, from.getCellNumber_Y()) != null) {
                    return true;
                }
            }
            return false;
        } else if(t_x == t_y) {
            int start_x = Math.min(from.getCellNumber_X(), to.getCellNumber_X());
            int start_y = Math.min(from.getCellNumber_Y(), to.getCellNumber_Y());
                for (int i = 1; i < t_x; i++){
                    if(getByCoord(start_x + i, start_y + i) != null) {
                        return true;
                    }
                }
                return false;
        }
        return false;
    }

    private boolean checkNodeIntersection(int x, int y){
        for (Node node: nodes) {
            for (Node rel : node.getConnectedNodes()) {
                int t_x = Math.abs(node.getCellNumber_X() - rel.getCellNumber_X());
                int t_y = Math.abs(node.getCellNumber_Y() - rel.getCellNumber_Y());
                if (t_x == 0) {
                    int start = Math.min(node.getCellNumber_Y(), rel.getCellNumber_Y());
                    for (int i = 1; i < t_y; i++) {
                        if (node.getCellNumber_X() == x && (start + i) == y) {
                            return true;
                        }
                    }
                } else if (t_y == 0) {
                    int start = Math.min(node.getCellNumber_X(), rel.getCellNumber_X());
                    for (int i = 1; i < t_x; i++) {
                        if (node.getCellNumber_Y() == y && (start + i) == x) {
                            return true;
                        }
                    }
                } else if (t_x == t_y) {
                    int start_x = Math.min(node.getCellNumber_X(), rel.getCellNumber_X());
                    int start_y = Math.min(node.getCellNumber_Y(), rel.getCellNumber_Y());
                    for (int i = 1; i < t_x; i++) {
                        if ((start_x + i) == x && (start_y + i) == y) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public void addNode(int x, int y, List<Integer> connectWith, int maxRelationsQuantity) throws Exception {

        if(maxRelationsQuantity <= 0){
            throw new NodeRelationsException("Значение максимального количества связей не корректно");
        }
        if(getByCoord(x, y) != null){
            throw new NodeExistException("В этой ячейке уже существует узел");
        }
        System.out.println(x + "/" + y + "----------------");
        if(checkNodeIntersection(x, y)){
            throw new NodeInterseptionException("Этот узел будет пересекаться с соединениями других");
        }
        int ID;
        if(nodes.isEmpty()) {
            ID = 0;
        }
        else {
            ID = nodes.get(nodes.size() - 1).getID() + 1;
        }

        nodes.add(new Node(type, x, y, ID));

        Node lastAdded = nodes.get(ID);
        lastAdded.setMaxRelationsCount(maxRelationsQuantity);

        int con = connectWith.size();

        for (int i = 0; i < connectWith.size(); i++) {
            Node ConnectingNode = getNodeByID(connectWith.get(i));
            if (ConnectingNode != null) {
                try {
                    if (checkRelationIntersection(lastAdded, ConnectingNode)) {
                        if (lastAdded.getRelationsCount() == 0 && i == connectWith.size() - 1) {
                            throw new NodeRelationsException("");
                        }
                        con--;
                        continue;
                    }
                    lastAdded.connectNode(ConnectingNode, false);
                }
                catch(OneselfConnection | NodeRelationsException e) {
                    con--;
                   if(lastAdded.getRelationsCount() == 0 && i == connectWith.size() - 1){
                       nodes.remove(lastAdded);
                       throw new NodeExistException("Узел был удален т.к. не соединился ни с одним уже существующим");
                   }
                }
            }
        }
        System.out.println("Conn " + con + "/" + connectWith.size());
    }

    public int size(){
        return nodes.size();
    }

    public Network() throws Exception {
        nodes = new ArrayList<>();
    }

    public Network(NetworkType type, int maxNodeCount) throws Exception {
        nodes = new ArrayList<>();
        this.type = type;
    }

    public NetworkType getType() {
        return type;
    }

    public void setType(NetworkType type) {
        this.type = type;
    }

    public List<org.donntu.generator.Node> getNodes() {
        return nodes;
    }

    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public List<Network> getConnectedWith() {
        return connectedWith;
    }

    public void addConnectedNetwork(Network network) {
        this.connectedWith.add(network);
    }
}
