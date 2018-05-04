package org.donntu.generator.configs;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class DefaultConfig{
    private static GenerateConfig config;
    public static GenerateConfig getDefaultConfig(){
        config = new GenerateConfig();
        buildCellsCount();
        buildDirectory();
        buildImageSize();
        buildImagesQuantity();
        buildLANsettings();
        buildWANSettings();
        buildNetworkRelations();
        buildNodeImage();
        return config;
    }
    private DefaultConfig() {}
    private static void buildImagesQuantity() {
        config.setImagesQuantity(1);
    }

    private static void buildDirectory() {
        config.setDirectory(new File(""));
    }

    private static void buildNodeImage() {
        try {
            config.setNodeImage(ImageIO.read(new File("images-fx/2.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void buildImageSize() {
        config.setImageHeight(2000);
        config.setImageWidth(2000);
    }

    private static void buildWANSettings() {
        config.setWanNodesQuantity(6);
        config.setWanRelationsQuantity(3);
    }

    private static void buildLANsettings() {
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
