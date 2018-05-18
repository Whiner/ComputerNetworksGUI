package org.donntu.generator;

public class TopologyCompareCriteria {
    private boolean wan = false;
    private boolean lan  = false;
    private boolean ip = false;

    public boolean isWan() {
        return wan;
    }

    public void setWan(boolean wan) {
        this.wan = wan;
    }

    public boolean isLan() {
        return lan;
    }

    public void setLan(boolean lan) {
        this.lan = lan;
    }

    public boolean isIp() {
        return ip;
    }

    public void setIp(boolean ip) {
        this.ip = ip;
    }
}
