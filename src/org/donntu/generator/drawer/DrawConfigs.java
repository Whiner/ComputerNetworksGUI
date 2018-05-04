package org.donntu.generator.drawer;


import org.donntu.generator.field.Field;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class DrawConfigs {
    private int nodeHeight;
    private int nodeWidth;
    private Image nodeImage;

    public int getNodeHeight() {
        return nodeHeight;
    }

    public void setNodeHeight(int nodeHeight) {
        this.nodeHeight = nodeHeight;
    }

    public int getNodeWidth() {
        return nodeWidth;
    }

    public void setNodeWidth(int nodeWidth) {
        this.nodeWidth = nodeWidth;
    }

    public Image getNodeImage() {
        return nodeImage;
    }

    public void calcNodeSize() throws Exception {
        try{
            nodeWidth = Field.getInstance().getWidth() / (Field.getInstance().getCellsCountX() * 2);
            nodeHeight = Field.getInstance().getHeight() / (Field.getInstance().getCellsCountY() * 2);
            this.nodeImage = ImageEditor.resizeImage(nodeImage, nodeWidth, nodeHeight, true);
        }
        catch (Exception e){
            throw new Exception("Division by 0");
        }
    }


    private static DrawConfigs instance;

    public void setNodeImage(Image nodeImage){
        if(nodeImage == null)
           throw new NullPointerException();
        this.nodeImage = ImageEditor.deepImageCopy(nodeImage);
        nodeHeight = nodeImage.getHeight(null);
        nodeWidth = nodeImage.getWidth(null);
        if(nodeImage.getHeight(null) != nodeImage.getWidth(null)){
            this.nodeImage = ImageEditor.resizeImage(nodeImage, nodeWidth, nodeHeight, false);
        }

    }

    public void setNodeImage(File directory) throws Exception  {
        if(directory == null){
            throw new NullPointerException();
        }
        if(directory.isFile()) {
            try {
                nodeImage = ImageIO.read(directory);
                nodeHeight = nodeImage.getHeight(null);
                nodeWidth = nodeImage.getWidth(null);
                if(nodeImage.getHeight(null) != nodeImage.getWidth(null)) {
                    nodeImage = ImageEditor.resizeImage(nodeImage, nodeWidth, nodeHeight, false);
                }
            } catch (IOException e) {
                throw new Exception("Directory does not point to an image or image is corrupted");
            }
        }
        else {
            throw new Exception("Directory point to folder or is not exist");
        }
    }



    public static DrawConfigs getInstance(){
        if (instance == null) {
            instance = new DrawConfigs();
        }
        return instance;
    }


    private DrawConfigs() {}
}
