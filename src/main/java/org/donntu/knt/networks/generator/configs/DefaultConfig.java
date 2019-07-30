package org.donntu.knt.networks.generator.configs;

public class DefaultConfig{
    protected static GenerateConfig config;

    public static GenerateConfig getDefaultConfig(){
        config = new GenerateConfig();
        buildCellsCount();
        buildLANSettings();
        buildWANSettings();
        buildNetworkPorts();
        return config;
    }

    private DefaultConfig() {}


    protected static void buildWANSettings() {
        config.setWanNodesQuantity(6);
        config.setWanPortsQuantity(3);
    }

    protected static void buildLANSettings() {
        config.setLanNetworksQuantity(2);
        config.setLanNodesQuantity(4);
        config.setLanPortsQuantity(3);
    }

    protected static void buildNetworkPorts() {
        config.setNetworksPortsQuantity(1);
    }

    protected static void buildCellsCount() {
        config.setCellsCountX(8);
        config.setCellsCountY(8);
    }
}
