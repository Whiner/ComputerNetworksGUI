package org.donntu.generator.configs;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class GenerateConfig {
    private int wanNodesQuantity;
    private int wanRelationsQuantity;
    private int lanQuantity;
    private int lanNodesQuantity;
    private int lanRelationsQuantity;
    private int networksRelationsQuantity;
    private int cellsCountX;
    private int cellsCountY;

    public GenerateConfig() {}


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

    public int getCellsCountX() {
        return cellsCountX;
    }

    public void setCellsCountX(int cellsCountX) {
        this.cellsCountX = cellsCountX;
    }

    public int getCellsCountY() {
        return cellsCountY;
    }

    public void setCellsCountY(int cellsCountY) {
        this.cellsCountY = cellsCountY;
    }

}
