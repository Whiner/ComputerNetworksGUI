package org.donntu.generator;

import sun.nio.ch.Net;

import java.util.ArrayList;
import java.util.List;

public class Topology {

    private List<Network> networks;
    private boolean isWANExist;
    private int LANQuantity;

    public Network getNetworkByID(int ID){
        for (Network network: networks){
            if(network.getID() == ID){
                return network;
            }
        }
        return null;
    }

    public void setNetworks(List<Network> networks) {
        isWANExist = false;
        LANQuantity = 0;
        for (Network network: networks){
            if(network.getType() == NetworkType.WAN){
                if(isWANExist){
                    continue;
                } else {
                    isWANExist = true;
                }
            } else {
                LANQuantity++;
            }
        }
        this.networks = networks;
    }

    public Topology(){
       networks = new ArrayList<>();
       LANQuantity = 0;
       isWANExist = false;
    }

    public List<NetworksConnection> getUniqueNetworksConnections(){
        if(networks.size() < 2){
            return null;
        }
        List<NetworksConnection> uniqueConnections = new ArrayList<>();
        for (Network network: networks){
            for (NetworksConnection connection: network.getNetworkConnections()){
                if(!uniqueConnections.contains(connection) && !uniqueConnections.contains(connection.getInvertedConnection())){
                    uniqueConnections.add(connection);
                }
            }
        }
        return uniqueConnections;
    }

    public Network getWAN(){
        if(isWANExist) {
            for (Network network : networks) {
                if (network.getType() == NetworkType.WAN) {
                    return network;
                }
            }
        }
        return null;
    }

    public List<Network> getLANs(){
        if(LANQuantity > 0){
            List<Network> LANs = new ArrayList<>();
            for (Network network: networks){
                if(network.getType() == NetworkType.LAN){
                    LANs.add(network);
                }
            }
            return LANs;
        }
        return null;
    }

    public void AddNetwork(Network network) throws Exception {
        if(network == null) {
            throw new NullPointerException();
        }
        if(networks.contains(network)) {
            throw new Exception("Эта сеть уже существует в топологии");
        }
        if(network.getType() == NetworkType.WAN){
            if(isWANExist) {
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


}
