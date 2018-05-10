package org.donntu.generator.configs;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class DefaultConfig{
    private static GenerateConfig config;
    public static GenerateConfig getDefaultConfig(){
        config = new GenerateConfig();
        buildCellsCount();
        buildLANSettings();
        buildWANSettings();
        buildNetworkRelations();
        return config;
    }
    private DefaultConfig() {}


    private static void buildWANSettings() {
        config.setWanNodesQuantity(6);
        config.setWanRelationsQuantity(3);
    }

    private static void buildLANSettings() {
        config.setLanQuantity(2);
        config.setLanNodesQuantity(4);
        config.setLanRelationsQuantity(2);
    }

    private static void buildNetworkRelations() {
        config.setNetworksRelationsQuantity(1);
    }

    private static void buildCellsCount() {
        config.setCellsCountX(8);
        config.setCellsCountY(8);
    }
}
