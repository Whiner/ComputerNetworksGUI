package org.donntu.knt.networks.generator;

import javafx.util.Pair;
import org.donntu.knt.networks.generator.generatorException.NodeExistException;
import org.donntu.knt.networks.generator.generatorException.NodeInterseptionException;
import org.donntu.knt.networks.generator.generatorException.NodeRelationsException;
import org.donntu.knt.networks.generator.generatorException.OneselfConnection;

import java.util.ArrayList;
import java.util.List;


public class Network {
    private long ID;
    private NetworkType type;
    private List<Node> nodes = new ArrayList<>();
    private IP ip = new IP();
    private List<NetworksConnection> networkConnections = new ArrayList<>();

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
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

    public int getMaxNodeRelations() {
        if (nodes.size() > 0) {
            return nodes.get(0).getMaxRelationsCount();
        } else {
            return 0;
        }
    }

    public Node getByCoord(int x, int y){
        for (Node t: nodes){
            if(t.getCellNumberX() == x)
                if(t.getCellNumberY() == y)
                    return t;
        }
        return null;
    }

    public Node getNodeByID(int ID){
        for(Node t: nodes)
            if(t.getId() == ID)
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
        int t_x = Math.abs(from.getCellNumberX() - to.getCellNumberX());
        int t_y = Math.abs(from.getCellNumberY() - to.getCellNumberY());
        if(t_x == 0) {
            int start = Math.min(from.getCellNumberY(), to.getCellNumberY());
            for (int i = 1; i < t_y; i++) {
                if (getByCoord(from.getCellNumberX(), start + i) != null) {
                    return true;
                }
            }
        } else if(t_y == 0){
            int start = Math.min(from.getCellNumberX(), to.getCellNumberX());
            for (int i = 1; i < t_x; i++){
                if(getByCoord(start + i, from.getCellNumberY()) != null) {
                    return true;
                }
            }
        } else if(t_x == t_y) {
            int start_x = from.getCellNumberX();
            int start_y = from.getCellNumberY();
            int step_x;
            int step_y;
            if(from.getCellNumberX() > to.getCellNumberX()){
                step_x = -1;
            } else {
                step_x = 1;
            }
            if(from.getCellNumberY() > to.getCellNumberY()){
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
            int t_x = Math.abs(link.getValue().getCellNumberX() - link.getKey().getCellNumberX());
            int t_y = Math.abs(link.getValue().getCellNumberY() - link.getKey().getCellNumberY());
            if (t_x == 0) {
                int start = Math.min(link.getValue().getCellNumberY(), link.getKey().getCellNumberY());
                for (int i = 1; i < t_y; i++) {
                    if (link.getValue().getCellNumberX() == x && (start + i) == y) {
                        return true;
                    }
                }
            } else if (t_y == 0) {
                int start = Math.min(link.getValue().getCellNumberX(), link.getKey().getCellNumberX());
                for (int i = 1; i < t_x; i++) {
                    if (link.getValue().getCellNumberY() == y && (start + i) == x) {
                        return true;
                    }
                }
            } else if (t_x == t_y) {
                Node first = link.getValue();
                Node second = link.getKey();
                int start_x = first.getCellNumberX();
                int start_y = first.getCellNumberY();
                int step_x;
                int step_y;
                if(first.getCellNumberX() > second.getCellNumberX()){
                    step_x = -1;
                } else {
                    step_x = 1;
                }
                if(first.getCellNumberY() > second.getCellNumberY()){
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
            ID = nodes.get(nodes.size() - 1).getId() + 1;
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
