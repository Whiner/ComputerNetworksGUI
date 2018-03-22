package sample;


import NodeGenerator.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;



import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;



public class Drawer {
    private WritableImage writableImage;
    private PixelWriter pixelWriter;

    public Drawer() {
        writableImage = new WritableImage(NodeGenerator.Field.GetInstance().getSizeBorderInPx(),
                NodeGenerator.Field.GetInstance().getSizeBorderInPx());
        pixelWriter = writableImage.getPixelWriter();
        FillWhite();
        DrawGrid();
        DrawSections();
    }

    private void DrawSections() {
        pixelWriter = writableImage.getPixelWriter();
        Section section = Field.GetInstance().getWAN_Section();
        int y = section.getCells_Count_Y() * Field.GetInstance().getNodeSize() * 2;
        for (int i = 0; i < Field.GetInstance().getSizeBorderInPx(); i++) {
            pixelWriter.setColor(i, y, Color.DARKRED);
        }

        for (Section t : Field.GetInstance().getLAN_Sections()) {
            int x = t.getCells_Count_X() * Field.GetInstance().getNodeSize() * 2;
            for (int i = y; i < Field.GetInstance().getSizeBorderInPx(); i++)
                pixelWriter.setColor(x, i, Color.DARKRED);
        }

    }

    private void FillWhite() {

        for (int i = 0; i < writableImage.getWidth(); i++)
            for (int j = 0; j < writableImage.getHeight(); j++)
                pixelWriter.setColor(i, j, Color.WHITE);
    }

    private void DrawConnection(Node from, Direction direction) { // не так. соединять 2 точки просто
        int x_from = (from.getCellNumber_X() * NodeGenerator.Field.GetInstance().getNodeSize() * 2)
                + NodeGenerator.Field.GetInstance().getNodeSize() / 2;
        int y_from = from.getCellNumber_Y() * NodeGenerator.Field.GetInstance().getNodeSize() * 2
                + NodeGenerator.Field.GetInstance().getNodeSize() / 2;
        int LineLenght;
        if (direction == Direction.Up || direction == Direction.Left || direction == Direction.Down || direction == Direction.Right)
            LineLenght = Field.GetInstance().getNodeSize() * 2;
        else
            LineLenght = (int) ( Math.sqrt(2 * Math.pow(Field.GetInstance().getNodeSize(), 2)) +
                    Math.sqrt(2 * Math.pow(Field.GetInstance().getNodeSize(), 2)) * 1/2);

        try {
            for (int i = 0; i < LineLenght; i++)
                switch (direction) {
                    case Up_Left:
                        pixelWriter.setColor(x_from + i, y_from + i, Color.BLACK);
                        break;
                    case Up:
                        pixelWriter.setColor(x_from, y_from + i, Color.BLACK);
                        break;
                    case Up_Right:
                        pixelWriter.setColor(x_from - i, y_from + i, Color.BLACK);
                        break;
                    case Left_Down:
                        pixelWriter.setColor(x_from + i, y_from - i, Color.BLACK);
                        break;
                    case Right_Down:
                        pixelWriter.setColor(x_from - i, y_from - i, Color.BLACK);
                        break;
                    case Down:
                        pixelWriter.setColor(x_from, y_from - i, Color.BLACK);
                        break;
                    case Left:
                        pixelWriter.setColor(x_from + i, y_from, Color.BLACK);
                        break;
                    case Right:
                        pixelWriter.setColor(x_from - i, y_from, Color.BLACK);
                        break;
                }
        } catch (Exception e) {
            System.out.println("X = " + from.getCellNumber_X() + " Y = " + from.getCellNumber_Y());
            System.out.println("Dir = " + direction + " LineLenght = " + LineLenght);
            System.out.println("-----------------------");
        }

    }

    private void DrawGrid(){
        for (int i = 0; i < Field.GetInstance().getCells_Count() * 2; i++){
            for (int j = 0; j < Field.GetInstance().getSizeBorderInPx(); j++){
                pixelWriter.setColor(i * Field.GetInstance().getNodeSize(), j, Color.YELLOW);
            }
            for (int j = 0; j < Field.GetInstance().getSizeBorderInPx(); j++){
                pixelWriter.setColor(j, i* Field.GetInstance().getNodeSize(), Color.YELLOW);
            }
        }
    }


    private void DrawNode(Node node){
        Image node_img = new Image("images-fx/1.png",
                NodeGenerator.Field.GetInstance().getNodeSize(),
                NodeGenerator.Field.GetInstance().getNodeSize(),
                true, false);



        PixelReader reader = node_img.getPixelReader();
        int x = node.getCellNumber_X() * NodeGenerator.Field.GetInstance().getNodeSize() * 2;
        int y = node.getCellNumber_Y() * NodeGenerator.Field.GetInstance().getNodeSize() * 2;
        for (int i = 0; i < node_img.getWidth(); i++){
            for (int j = 0; j < node_img.getHeight(); j++)
            {
                Color color = reader.getColor( i, j);
                pixelWriter.setColor(i + x, j + y, color);
            }
        }
        for (NodeNavigation t: node.getConnectedNodes()){
        //try{
            DrawConnection(node.getConnectedNodes().get(0).getNode(), node.getConnectedNodes().get(0).getDirection());

        //} catch (Exception e){

        }
        //}

    }


    public void DrawNetwork(NodeGenerator.Network network) throws IOException {

        pixelWriter = writableImage.getPixelWriter();


        for (Node t: network.getNodes()){
            DrawNode(t);
        }


    }
    public void SaveImage(String imageDirectory) throws IOException {
        if(writableImage != null)
            ImageIO.write(SwingFXUtils.fromFXImage(writableImage, null),
                    "png", new FileOutputStream(imageDirectory));
    }
}
