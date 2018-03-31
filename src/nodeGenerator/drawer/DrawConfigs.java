package nodeGenerator.drawer;


import javax.imageio.ImageIO;
import java.awt.*;
import java.io.File;
import java.io.IOException;


public class DrawConfigs {
    private int nodeSize;
    private Image nodeImage;

    public int getNodeSize() {
        return nodeSize;
    }

    public Image getNodeImage() {
        return nodeImage;
    }

    public void calcNodeSize() throws Exception {
        if(Field.getInstance().getCellsCount() == -1 || Field.getInstance().getFieldSize_px() == -1){
            throw new Exception("Fill field parameters in class Field");
        }
        try{
            nodeSize = Field.getInstance().getFieldSize_px() / (Field.getInstance().getCellsCount() * 2);
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
        nodeSize = nodeImage.getHeight(null);
        if(nodeImage.getHeight(null) != nodeImage.getWidth(null)){
            this.nodeImage = ImageEditor.resizeImage(nodeImage, nodeSize, nodeSize, false);
        }

    }

    public void setNodeImage(File directory) throws Exception  {
        if(directory == null){
            throw new NullPointerException();
        }
        if(directory.isFile()) {
            try {
                nodeImage = ImageIO.read(directory);
                nodeSize = nodeImage.getHeight(null);
                if(nodeImage.getHeight(null) != nodeImage.getWidth(null)) {
                    nodeImage = ImageEditor.resizeImage(nodeImage, nodeSize, nodeSize, false);
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
