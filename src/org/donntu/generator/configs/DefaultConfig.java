package org.donntu.generator.configs;

public class DefaultConfig{
    private static GenerateConfig config;

    public static GenerateConfig getDefaultConfig(){
        config = new GenerateConfig();
        buildCellsCount();
        buildLANSettings();
        buildWANSettings();
        buildNetworkPorts();
        return config;
    }

    private DefaultConfig() {}


    private static void buildWANSettings() {
        config.setWanNodesQuantity(6);
        config.setWanPortsQuantity(3);
    }

    private static void buildLANSettings() {
        config.setLanQuantity(2);
        config.setLanNodesQuantity(4);
        config.setLanPortsQuantity(3);
    }

    private static void buildNetworkPorts() {
        config.setNetworksPortsQuantity(1);
    }

    private static void buildCellsCount() {
        config.setCellsCountX(8);
        config.setCellsCountY(8);
    }
}
