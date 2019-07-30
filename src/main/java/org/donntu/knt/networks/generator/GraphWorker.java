package org.donntu.knt.networks.generator;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class GraphWorker {

    List<Node> viewedNodes = new ArrayList<>();
    List<Pair<Node, Node>> viewedConnections = new ArrayList<>();

    public boolean searchCycle(Node node) {
        if (viewedNodes.contains(node)) {
            return true;
        } else {
            viewedNodes.add(node);
        }
        for (Node connections : node.getConnectedNodes()) {
            Pair<Node, Node> nodePair = new Pair<>(node, connections);
            if (!viewedConnections.contains(nodePair)
                    && !viewedConnections.contains(new Pair<>(connections, node))) {
                viewedConnections.add(nodePair);
                if (searchCycle(connections)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean doCycle(Network network) throws Exception {
        if (network.getNodes().size() < 3) {
            throw new Exception("Слишком мало узлов для цикличности");
        }
        Node firstNode = null;
        for (Node node : network.getNodes()) {
            if (node.getConnectedNodes().size() == 1) {
                if (firstNode == null) {
                    firstNode = node;
                } else {
                    firstNode.connectNode(node, true);
                    return true;
                }
            }
        }
        return false;
    }
}
