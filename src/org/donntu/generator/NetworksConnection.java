package org.donntu.generator;

public class NetworksConnection {
    private Network fromNetwork;
    private Node fromNetworkNode;
    private Network toNetwork;
    private Node toNetworkNode;

    public NetworksConnection(Network fromNetwork, Node fromNetworkNode, Network toNetwork, Node toNetworkNode) {
        this.fromNetwork = fromNetwork;
        this.fromNetworkNode = fromNetworkNode;
        this.toNetwork = toNetwork;
        this.toNetworkNode = toNetworkNode;
    }

    public NetworksConnection getInvertedConnection(){
        return new NetworksConnection(toNetwork, toNetworkNode, fromNetwork, fromNetworkNode);
    }

    public void setFromNetwork(Network fromNetwork) {
        this.fromNetwork = fromNetwork;
    }

    public void setFromNetworkNode(Node fromNetworkNode) {
        this.fromNetworkNode = fromNetworkNode;
    }

    public void setToNetwork(Network toNetwork) {
        this.toNetwork = toNetwork;
    }

    public void setToNetworkNode(Node toNetworkNode) {
        this.toNetworkNode = toNetworkNode;
    }

    public Network getFromNetwork() {
        return fromNetwork;
    }

    public Node getFromNetworkNode() {
        return fromNetworkNode;
    }

    public Network getToNetwork() {
        return toNetwork;
    }

    public Node getToNetworkNode() {
        return toNetworkNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        NetworksConnection that = (NetworksConnection) o;

        if (!fromNetwork.equals(that.fromNetwork)) return false;
        if (!fromNetworkNode.equals(that.fromNetworkNode)) return false;
        if (!toNetwork.equals(that.toNetwork)) return false;
        return toNetworkNode.equals(that.toNetworkNode);
    }

    @Override
    public int hashCode() {
        int result = fromNetwork.hashCode();
        result = 31 * result + fromNetworkNode.hashCode();
        result = 31 * result + toNetwork.hashCode();
        result = 31 * result + toNetworkNode.hashCode();
        return result;
    }
}