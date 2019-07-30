package org.donntu.knt.networks.generator;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class Topology {

    private List<Network> networks;
    private boolean isWANExist;
    private int LANQuantity;

    public Network getNetworkByID(long ID) {
        for (Network network : networks) {
            if (network.getID() == ID) {
                return network;
            }
        }
        return null;
    }

    public void setNetworks(List<Network> networks) {
        isWANExist = false;
        LANQuantity = 0;
        for (Network network : networks) {
            if (network.getType() == NetworkType.WAN) {
                if (!isWANExist) {
                    isWANExist = true;
                }
            } else {
                LANQuantity++;
            }
        }
        this.networks = networks;
    }

    public Topology() {
        networks = new ArrayList<>();
        LANQuantity = 0;
        isWANExist = false;
    }

    public List<NetworksConnection> getUniqueNetworksConnections() {
        if (networks.size() < 2) {
            return null;
        }
        List<NetworksConnection> uniqueConnections = new ArrayList<>();
        for (Network network : networks) {
            for (NetworksConnection connection : network.getNetworkConnections()) {
                if (!uniqueConnections.contains(connection) && !uniqueConnections.contains(connection.getInvertedConnection())) {
                    uniqueConnections.add(connection);
                }
            }
        }
        return uniqueConnections;
    }

    public Network getWAN() {
        if (isWANExist) {
            for (Network network : networks) {
                if (network.getType() == NetworkType.WAN) {
                    return network;
                }
            }
        }
        return null;
    }

    public List<Network> getLANs() {
        if (LANQuantity > 0) {
            List<Network> LANs = new ArrayList<>();
            for (Network network : networks) {
                if (network.getType() == NetworkType.LAN) {
                    LANs.add(network);
                }
            }
            return LANs;
        }
        return null;
    }

    public void AddNetwork(Network network) throws Exception {
        if (network == null) {
            throw new NullPointerException();
        }
        if (networks.contains(network)) {
            throw new Exception("Эта сеть уже существует в топологии");
        }
        if (network.getType() == NetworkType.WAN) {
            if (isWANExist) {
                throw new Exception("isWANExist уже существует в этой топологии");
            } else {
                isWANExist = true;
            }
        } else {
            LANQuantity++;
        }
        networks.add(network);
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public boolean isWANExist() {
        return isWANExist;
    }

    public void setWAN() {
        this.isWANExist = true;
    }

    public int get_LAN_Quantity() {
        return LANQuantity;
    }

    public boolean checkRelationIntersection(NetworksConnection connection) {

        int t_x = Math.abs(connection.getFromNetworkNode().getCellNumberX() - connection.getToNetworkNode().getCellNumberX());
        int t_y = Math.abs(connection.getFromNetworkNode().getCellNumberY() - connection.getToNetworkNode().getCellNumberY());
        if (t_x == 0) {
            int start = Math.min(connection.getFromNetworkNode().getCellNumberY(), connection.getToNetworkNode().getCellNumberY());
            for (int i = 1; i < t_y; i++) {
                for (Network network : networks) {
                    if (network.getByCoord(connection.getFromNetworkNode().getCellNumberX(), start + i) != null) {
                        return true;
                    }
                }
            }
        } else if (t_y == 0) {
            int start = Math.min(connection.getFromNetworkNode().getCellNumberX(), connection.getToNetworkNode().getCellNumberX());
            for (int i = 1; i < t_x; i++) {
                for (Network network : networks) {
                    if (network.getByCoord(start + i, connection.getFromNetworkNode().getCellNumberY()) != null) {
                        return true;
                    }
                }
            }
        } else if (t_x == t_y) {
            int start_x = connection.getFromNetworkNode().getCellNumberX();
            int start_y = connection.getFromNetworkNode().getCellNumberY();
            int step_x;
            int step_y;
            if (connection.getFromNetworkNode().getCellNumberX() > connection.getToNetworkNode().getCellNumberX()) {
                step_x = -1;
            } else {
                step_x = 1;
            }
            if (connection.getFromNetworkNode().getCellNumberY() > connection.getToNetworkNode().getCellNumberY()) {
                step_y = -1;
            } else {
                step_y = 1;
            }
            for (int i = 1; i < t_x; i++) {
                for (Network network : networks) {
                    if (network.getByCoord(start_x + step_x * i, start_y + step_y * i) != null) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public Pair<Network, Network> findSimilar(Topology topology) {

        for (int i = 0; i < this.getNetworks().size(); i++) {
            if (this.networks.get(i).isLike(topology.getNetworks().get(i))
                    && this.networks.get(i).getIp().isLike(topology.getNetworks().get(i).getIp())) {
                return new Pair<>(this.networks.get(i), topology.getNetworks().get(i));
            }
        }
        return null;
        /*int similar = 0;
        TopologyCompareCriteria criteria = new TopologyCompareCriteria();
        if (topology.LANQuantity == this.LANQuantity && topology.isWANExist && this.isWANExist) {
            for (int i = 0; i < this.getNetworks().size(); i++) {
                if (networks.get(i).isLike(topology.getNetworks().get(i))) {
                    if (networks.get(i).getType() == NetworkType.WAN && topology.getNetworks().get(i).getType() == NetworkType.WAN) {
                        criteria.setWan(true);
                    }
                    similar++;
                }
                if (networks.get(i).getIp().isLike(topology.getNetworks().get(i).getIp())) {
                    criteria.setIp(true);
                }
            }
            if (similar > topology.LANQuantity / 2) {
                criteria.setLan(true);
            }
        }

        return criteria;*/
    }
}
