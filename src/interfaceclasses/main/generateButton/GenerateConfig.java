package interfaceclasses.main.generateButton;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GenerateConfig {
    private int imagesQuantity;
    private File directory;
    private int imageSize;
    private Image nodeImage;
    private int wanNodesQuantity;
    private int wanRelationsQuantity;
    private int lanQuantity;
    private int lanNodesQuantity;
    private int lanRelationsQuantity;
    private int networksRelationsQuantity;
    private int cellsCount;

    private GenerateConfig() {
        imagesQuantity = 1;
        directory = new File("");
        try {
            nodeImage = ImageIO.read(new File("src/images-fx/2.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        imageSize = 2000;
        wanNodesQuantity = 6;
        wanRelationsQuantity = 3;
        lanQuantity = 2;
        lanNodesQuantity = 4;
        lanRelationsQuantity = 2;
        networksRelationsQuantity = 1;
        cellsCount = 8;
    }

    private static GenerateConfig instance;

    public static GenerateConfig getInstance(){
        if (instance == null) {
            instance = new GenerateConfig();
        }
        return instance;
    }

    public int getImagesQuantity() {
        return imagesQuantity;
    }

    public void setImagesQuantity(int imagesQuantity) {
        this.imagesQuantity = imagesQuantity;
    }

    public File getDirectory() {
        return directory;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }

    public int getImageSize() {
        return imageSize;
    }

    public void setImageSize(int imageSize) {
        this.imageSize = imageSize;
    }

    public Image getNodeImage() {
        return nodeImage;
    }

    public void setNodeImage(Image nodeImage) {
        this.nodeImage = nodeImage;
    }

    public int getWanNodesQuantity() {
        return wanNodesQuantity;
    }

    public void setWanNodesQuantity(int wanNodesQuantity) {
        this.wanNodesQuantity = wanNodesQuantity;
    }

    public int getWanRelationsQuantity() {
        return wanRelationsQuantity;
    }

    public void setWanRelationsQuantity(int wanRelationsQuantity) {
        this.wanRelationsQuantity = wanRelationsQuantity;
    }

    public int getLanQuantity() {
        return lanQuantity;
    }

    public void setLanQuantity(int lanQuantity) {
        this.lanQuantity = lanQuantity;
    }

    public int getLanNodesQuantity() {
        return lanNodesQuantity;
    }

    public void setLanNodesQuantity(int lanNodesQuantity) {
        this.lanNodesQuantity = lanNodesQuantity;
    }

    public int getLanRelationsQuantity() {
        return lanRelationsQuantity;
    }

    public void setLanRelationsQuantity(int lanRelationsQuantity) {
        this.lanRelationsQuantity = lanRelationsQuantity;
    }

    public int getNetworksRelationsQuantity() {
        return networksRelationsQuantity;
    }

    public void setNetworksRelationsQuantity(int networksRelationsQuantity) {
        this.networksRelationsQuantity = networksRelationsQuantity;
    }

    public int getCellsCount() {
        return cellsCount;
    }

    public void setCellsCount(int cellsCount) {
        this.cellsCount = cellsCount;
    }
}
