package org.donntu.databaseworker;

class NodeConnection {
    int idNode;
    long idNetwork;
    int idConnectedNode;
    long idConnectedNetwork;

    public NodeConnection(int idNode, long idNetwork, int idConnectedNode, long idConnectedNetwork) {
        this.idNode = idNode;
        this.idNetwork = idNetwork;
        this.idConnectedNode = idConnectedNode;
        this.idConnectedNetwork = idConnectedNetwork;
    }
}
