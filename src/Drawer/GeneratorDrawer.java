package Drawer;

import NodeGenerator.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class GeneratorDrawer {
        private BufferedImage bufferedImage;
        private Graphics2D graphics2D;
        private int height;
        private int width;

        public Image getImage(){
            return SwingFXUtils.toFXImage(bufferedImage, null);
        }

        public GeneratorDrawer(int height, int width) {
            this.height = height;
            this.width = width;
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            graphics2D = (Graphics2D) bufferedImage.getGraphics();
            fillBackground(Color.WHITE);
            drawSections();
        }

        private void drawSections() {

            Section section = Field.GetInstance().getWAN_Section();
            int y = section.getCells_Count_Y() * Field.GetInstance().getNodeSize() * 2;
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawLine(0, y, Field.GetInstance().getSizeBorderInPx(), y);

            for (Section t : Field.GetInstance().getLAN_Sections()) {
                int x = t.getCells_Count_X() * Field.GetInstance().getNodeSize() * 2;
                graphics2D.drawLine(x, t.getBeginCell_Y() * Field.GetInstance().getNodeSize() * 2,
                        x, Field.GetInstance().getSizeBorderInPx());
            }

        }

        private void fillBackground(Color color) {
            if(color != null) {
                graphics2D.setColor(color);
                graphics2D.fillRect(0, 0, width, height);
            }
        }

        private void drawConnection(Node from, Node to) {
            if(from != null && to != null) {
                graphics2D.setColor(Color.BLACK);
                int x_from = from.getCellNumber_X() * Field.GetInstance().getNodeSize() * 2 + Field.GetInstance().getNodeSize() / 2;
                int y_from = from.getCellNumber_Y() * Field.GetInstance().getNodeSize() * 2 + Field.GetInstance().getNodeSize() / 2;
                int x_to = to.getCellNumber_X() * Field.GetInstance().getNodeSize() * 2 + Field.GetInstance().getNodeSize() / 2;
                int y_to = to.getCellNumber_Y() * Field.GetInstance().getNodeSize() * 2 + Field.GetInstance().getNodeSize() / 2;
                graphics2D.drawLine(x_from, y_from, x_to, y_to);
            }

        }

        private static java.awt.Image resize(java.awt.Image originalImage,
                                             int scaledWidth, int scaledHeight,
                                             boolean preserveAlpha)
        {
            int imageType = preserveAlpha ? BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
            BufferedImage scaledBI = new BufferedImage(scaledWidth, scaledHeight, imageType);
            Graphics2D g = scaledBI.createGraphics();
            if (preserveAlpha) {
                g.setComposite(AlphaComposite.Src);
            }
            g.drawImage(originalImage, 0, 0, scaledWidth, scaledHeight, null);
            g.dispose();
            return scaledBI;
        }

    private void drawNode(Node node) throws IOException { // сделать отдельные методы в Field, чтобы получать край узла и центр
            if(node != null){
                java.awt.Image node_image = ImageIO.read(new File("src/images-fx/1.png"));
                node_image = resize(node_image,
                        Field.GetInstance().getNodeSize(),
                        Field.GetInstance().getNodeSize(),
                        false);
                BufferedImage node_buffered_image = new BufferedImage(
                        node_image.getWidth(null),
                        node_image.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);

                Graphics bGr = node_buffered_image.createGraphics();

                bGr.drawImage(node_image, 0, 0, null);
                bGr.dispose();
                int x = node.getCellNumber_X() * Field.GetInstance().getNodeSize() * 2;
                int y = node.getCellNumber_Y() * Field.GetInstance().getNodeSize() * 2;
                graphics2D.drawImage(node_buffered_image, x, y, null);
            }

        }


        public void drawNetwork(NodeGenerator.Network network) {
            for (Node node: network.getNodes()){
                for(NodeNavigation nodeNavigation: node.getConnectedNodes()) {
                    drawConnection(node, nodeNavigation.getNode());
                }
                try {
                    drawNode(node);
                } catch (IOException e) {
                    System.out.println("Node error: " + node.getID());
                }
            }


        }
        public void saveImage(String imageDirectory) throws IOException {
            ImageIO.write(bufferedImage,"png", new FileOutputStream(imageDirectory));
        }
}
