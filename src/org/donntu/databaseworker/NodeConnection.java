package org.donntu.databaseworker;

class NodeConnection {
    int idNode;
    int idNetwork;
    int idConnectedNode;
    int idConnectedNetwork;

    public NodeConnection(int idNode, int idNetwork, int idConnectedNode, int idConnectedNetwork) {
        this.idNode = idNode;
        this.idNetwork = idNetwork;
        this.idConnectedNode = idConnectedNode;
        this.idConnectedNetwork = idConnectedNetwork;
    }
}
