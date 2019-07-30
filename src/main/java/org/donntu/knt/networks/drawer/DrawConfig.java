package org.donntu.knt.networks.drawer;

import org.donntu.knt.networks.generator.field.Field;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class DrawConfig {
    private int nodeHeight;
    private int nodeWidth;
    private Image nodeImage;
    private int imageHeight = 2000;
    private int imageWidth = 2000;

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
            nodeWidth = imageWidth / (Field.getInstance().getCellsCountX() * 2);
            nodeHeight = imageHeight / (Field.getInstance().getCellsCountY() * 2);
            this.nodeImage = ImageEditor.resizeImage(nodeImage, nodeWidth, nodeHeight, true);
        }
        catch (Exception e){
            throw new Exception("Division by 0");
        }
    }

    public void setNodeImage(Image nodeImage) {
        if (nodeImage == null) {
            throw new NullPointerException();
        }
        this.nodeImage = ImageEditor.deepImageCopy(nodeImage);
        nodeHeight = nodeImage.getHeight(null);
        nodeWidth = nodeImage.getWidth(null);
        if (nodeImage.getHeight(null) != nodeImage.getWidth(null)) {
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
        } else {
            throw new Exception("Directory point to folder or is not exist");
        }
    }

    protected static DrawConfig instance;

    public static DrawConfig getInstance(){
        if (instance == null) {
            instance = new DrawConfig();
        }
        return instance;
    }

    private DrawConfig() {
        try {
            setNodeImage(new File(DrawConfig.class.getResource("/images/node.png").getFile()));
            calcNodeSize();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getImageWidth() {
        return imageWidth;
    }

    public void setImageWidth(int imageWidth) throws Exception {
        this.imageWidth = imageWidth;
        calcNodeSize();
    }

    public int getImageHeight() {
        return imageHeight;
    }

    public void setImageHeight(int imageHeight) throws Exception {
        this.imageHeight = imageHeight;
        calcNodeSize();
    }
}
