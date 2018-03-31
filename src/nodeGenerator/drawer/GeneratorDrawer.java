package nodeGenerator.drawer;

import nodeGenerator.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
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

        public GeneratorDrawer(int height, int width) throws Exception {
            DrawConfigs.getInstance().calcNodeSize();
            this.height = height;
            this.width = width;
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            graphics2D = (Graphics2D) bufferedImage.getGraphics();
            fillBackground(Color.WHITE);
            drawSections();
        }

        private void drawSections() {

            Section section = Field.getInstance().getWanSection();
            int y = section.getCells_Count_Y() * DrawConfigs.getInstance().getNodeSize() * 2;
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawLine(0, y, Field.getInstance().getFieldSize_px(), y);

            for (Section t : Field.getInstance().getLanSections()) {
                int x = t.getCells_Count_X() * DrawConfigs.getInstance().getNodeSize() * 2;
                graphics2D.drawLine(x, t.getBeginCell_Y() * DrawConfigs.getInstance().getNodeSize() * 2,
                        x, Field.getInstance().getFieldSize_px());
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
                int x_from = from.getCellNumber_X() * DrawConfigs.getInstance().getNodeSize() * 2 + DrawConfigs.getInstance().getNodeSize() / 2;
                int y_from = from.getCellNumber_Y() * DrawConfigs.getInstance().getNodeSize() * 2 + DrawConfigs.getInstance().getNodeSize() / 2;
                int x_to = to.getCellNumber_X() * DrawConfigs.getInstance().getNodeSize() * 2 + DrawConfigs.getInstance().getNodeSize() / 2;
                int y_to = to.getCellNumber_Y() * DrawConfigs.getInstance().getNodeSize() * 2 + DrawConfigs.getInstance().getNodeSize() / 2;
                graphics2D.drawLine(x_from, y_from, x_to, y_to);
            }

        }


    private void drawNode(Node node) throws Exception { // сделать отдельные методы в Field, чтобы получать край узла и центр
            if(node != null){

                java.awt.Image node_image = DrawConfigs.getInstance().getNodeImage();
                if(node_image == null){
                    throw new Exception("Set node image to DrawConfigs");
                }
                BufferedImage node_buffered_image = new BufferedImage(
                        node_image.getWidth(null),
                        node_image.getHeight(null),
                        BufferedImage.TYPE_INT_ARGB);

                Graphics bGr = node_buffered_image.createGraphics();

                bGr.drawImage(node_image, 0, 0, null);
                bGr.dispose();
                int x = node.getCellNumber_X() * DrawConfigs.getInstance().getNodeSize() * 2;
                int y = node.getCellNumber_Y() * DrawConfigs.getInstance().getNodeSize() * 2;
                graphics2D.drawImage(node_buffered_image, x, y, null);
            }

        }


        public void drawNetwork(nodeGenerator.Network network) throws Exception {
            for (Node node: network.getNodes()){
                for(NodeNavigation nodeNavigation: node.getConnectedNodes()) {
                    drawConnection(node, nodeNavigation.getNode());
                }
                drawNode(node);
            }


        }
        public void saveImage(String imageDirectory) throws IOException {
            ImageIO.write(bufferedImage,"png", new FileOutputStream(imageDirectory));
        }
}
