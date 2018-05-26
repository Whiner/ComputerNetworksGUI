package org.donntu.generator;

import javafx.util.Pair;
import org.donntu.generator.generatorException.NodeExistException;
import org.donntu.generator.generatorException.NodeInterseptionException;
import org.donntu.generator.generatorException.NodeRelationsException;
import org.donntu.generator.generatorException.OneselfConnection;

import java.util.ArrayList;
import java.util.List;


public class Network {
    private int ID;
    private NetworkType type;
    private List<Node> nodes = new ArrayList<>();
    private IP ip = new IP();
    private List<NetworksConnection> networkConnections = new ArrayList<>();

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }
    public void setNodes(List<Node> nodes) {
        this.nodes = nodes;
    }

    public void setNetworkConnections(List<NetworksConnection> networkConnections) {
        this.networkConnections = networkConnections;
    }

    public List<NetworksConnection> getNetworkConnections() {
        return networkConnections;
    }

    public List<Pair<Node, Node>> getUniqueConnections() { // сам себя и с другой сетью
        List<Pair<Node, Node>> pairList = new ArrayList<>();
        for (Node node : nodes) {
            for (Node connectNode : node.getConnectedNodes()) {
                if (!pairList.contains(new Pair<>(node, connectNode))
                        && !pairList.contains(new Pair<>(connectNode, node))) {
                    pairList.add(new Pair<>(node, connectNode));
                }
            }
        }
        return pairList;
    }

    public int getCountRelations(){
        return getUniqueConnections().size();
    }

    public IP getIp() {
        return ip;
    }

    public void setIp(IP ip) {
        this.ip = ip;
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


    public boolean checkRelationIntersection(Node from, Node to){  //при соединении
        int t_x = Math.abs(from.getCellNumber_X() - to.getCellNumber_X());
        int t_y = Math.abs(from.getCellNumber_Y() - to.getCellNumber_Y());
        if(t_x == 0) {
            int start = Math.min(from.getCellNumber_Y(), to.getCellNumber_Y());
            for (int i = 1; i < t_y; i++) {
                if (getByCoord(from.getCellNumber_X(), start + i) != null) {
                    return true;
                }
            }
        } else if(t_y == 0){
            int start = Math.min(from.getCellNumber_X(), to.getCellNumber_X());
            for (int i = 1; i < t_x; i++){
                if(getByCoord(start + i, from.getCellNumber_Y()) != null) {
                    return true;
                }
            }
        } else if(t_x == t_y) {
            int start_x = from.getCellNumber_X();
            int start_y = from.getCellNumber_Y();
            int step_x;
            int step_y;
            if(from.getCellNumber_X() > to.getCellNumber_X()){
                step_x = -1;
            } else {
                step_x = 1;
            }
            if(from.getCellNumber_Y() > to.getCellNumber_Y()){
                step_y = -1;
            } else {
                step_y = 1;
            }
                for (int i = 1; i < t_x; i++){
                    if(getByCoord(start_x + step_x * i, start_y + step_y * i) != null) {
                        return true;
                    }
                }
        }
        return false;
    }

    public boolean checkNodeIntersection(int x, int y){ //при создании узла
        List<Pair<Node, Node>> uniqueRelations = getUniqueConnections();

        for (Pair<Node, Node> link: uniqueRelations){
            int t_x = Math.abs(link.getValue().getCellNumber_X() - link.getKey().getCellNumber_X());
            int t_y = Math.abs(link.getValue().getCellNumber_Y() - link.getKey().getCellNumber_Y());
            if (t_x == 0) {
                int start = Math.min(link.getValue().getCellNumber_Y(), link.getKey().getCellNumber_Y());
                for (int i = 1; i < t_y; i++) {
                    if (link.getValue().getCellNumber_X() == x && (start + i) == y) {
                        return true;
                    }
                }
            } else if (t_y == 0) {
                int start = Math.min(link.getValue().getCellNumber_X(), link.getKey().getCellNumber_X());
                for (int i = 1; i < t_x; i++) {
                    if (link.getValue().getCellNumber_Y() == y && (start + i) == x) {
                        return true;
                    }
                }
            } else if (t_x == t_y) {
                Node first = link.getValue();
                Node second = link.getKey();
                int start_x = first.getCellNumber_X();
                int start_y = first.getCellNumber_Y();
                int step_x;
                int step_y;
                if(first.getCellNumber_X() > second.getCellNumber_X()){
                    step_x = -1;
                } else {
                    step_x = 1;
                }
                if(first.getCellNumber_Y() > second.getCellNumber_Y()){
                    step_y = -1;
                } else {
                    step_y = 1;
                }

                for (int i = 1; i < t_x; i++) {
                    if ((start_x + step_x * i) == x && (start_y + step_y * i) == y) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public void doCycleConnection() throws Exception {
        if(nodes.isEmpty()){
            throw new Exception("Нет узлов");
        }
        GraphWorker graphWorker = new GraphWorker();
        if(!graphWorker.searchCycle(nodes.get(0))){
            graphWorker.doCycle(this);
        }
    }

    public void addNode(int x, int y, List<Integer> connectWith, int maxRelationsQuantity) throws Exception {

        if(maxRelationsQuantity <= 0){
            throw new NodeRelationsException("Значение максимального количества связей не корректно");
        }
        if(getByCoord(x, y) != null){
            throw new NodeExistException("В этой ячейке уже существует узел");
        }

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

        nodes.add(new Node(x, y, ID));

        Node lastAdded = nodes.get(ID);
        lastAdded.setMaxRelationsCount(maxRelationsQuantity);

        for (int i = 0; i < connectWith.size(); i++) {
            Node ConnectingNode = getNodeByID(connectWith.get(i));
            if (ConnectingNode != null) {
                try {
                    if (checkRelationIntersection(lastAdded, ConnectingNode)) {
                        if (lastAdded.getRelationsCount() == 0 && i == connectWith.size() - 1) { // лишнее походу
                            throw new NodeRelationsException("Пересечение с узлами");
                        }
                        continue;
                    }
                    lastAdded.connectNode(ConnectingNode, false);
                }
                catch(OneselfConnection | NodeRelationsException e) {
                   if(lastAdded.getRelationsCount() == 0 && i == connectWith.size() - 1){
                       nodes.remove(lastAdded);
                       throw new NodeExistException("Узел был удален т.к. не соединился ни с одним уже существующим");
                   }
                }
            }
        }
    }

    public int size(){
        return nodes.size();
    }

    public Network() {
    }


    public NetworkType getType() {
        return type;
    }

    public void setType(NetworkType type) {
        this.type = type;
    }

    public List<Node> getNodes() {
        return nodes;
    }


    public void connectNetworks(Node thisNetworkNode, Network connectedNetwork, Node connectedNode) throws Exception {
        if (thisNetworkNode != null && connectedNetwork != null && connectedNode != null) {
            if(!nodes.contains(thisNetworkNode)){
                throw new Exception("Неверный первый параметр. Этот узел не существует в этой сети");
            }
            if(!connectedNetwork.nodes.contains(connectedNode)){
                throw new Exception("ConnectedNode должен находиться в ConnectedNetwork");
            }
            if(thisNetworkNode.equals(connectedNode)){
                throw new Exception("Соединение с самим собой");
            }

            if(networkConnections.contains(new NetworksConnection(
                    this,
                    thisNetworkNode,
                    connectedNetwork,
                    connectedNode))) {
                throw new Exception("Связь уже существует");
            }

            networkConnections.add(new NetworksConnection(this, thisNetworkNode, connectedNetwork, connectedNode));
            connectedNetwork.networkConnections.add(new NetworksConnection(connectedNetwork, connectedNode, this, thisNetworkNode));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Network network = (Network) o;

        if (type != network.type) return false;
        if (!nodes.equals(network.nodes)) return false;
        return ip.equals(network.ip);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 31 * result + nodes.hashCode();
        result = 31 * result + ip.hashCode();
        return result;
    }

    public boolean isLike(Network network){
        if(network.getNodes().size() == this.nodes.size()){
            return network.getUniqueConnections().size() == this.getUniqueConnections().size();
        }
        return false;
    }

}
