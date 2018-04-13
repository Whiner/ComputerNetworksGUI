package nodegenerator;

import java.util.ArrayList;
import java.util.List;

public class Topology {

    private List<Network> networks;
    private boolean WAN;
    private int LAN;

    public Topology(){
       networks = new ArrayList<>();
       LAN = 0;
       WAN = false;
    }

    public void AddNetwork(Network network) throws Exception {
        if(network == null)
            throw new NullPointerException("Network is null pointer");
        if(networks.contains(network))
            throw new Exception("This network is already exist in topology");
        if(network.getType() == NetworkType.WAN){
            if(WAN)
                throw new Exception("WAN network is already exist in topology");
            else
                WAN = true;
        }
        else
            LAN++;
        networks.add(network);
    }

    public List<Network> getNetworks() {
        return networks;
    }

    public boolean isWAN() {
        return WAN;
    }

    public void setWAN() {
        this.WAN = true;
    }

    public int get_LAN_Quantity() {
        return LAN;
    }


}
