package org.donntu.generator.drawer;

import javafx.util.Pair;
import org.donntu.generator.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.donntu.generator.drawer.other.ColorComparator;
import org.donntu.generator.drawer.other.Coordinates;
import org.donntu.generator.drawer.other.NodeCoordinatesConvertor;
import org.donntu.generator.field.Field;
import org.donntu.generator.field.Section;

import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class GeneratorDrawer {
        private BufferedImage bufferedImage;
        private Graphics2D graphics2D;
        private int height;
        private int width;
        private List<Color> usedColors = new ArrayList<>();


        public Image getImage(){
            return SwingFXUtils.toFXImage(bufferedImage, null);
        }

        public GeneratorDrawer(int height, int width, boolean cells) throws Exception {
            DrawConfigs.getInstance().calcNodeSize();
            this.height = height;
            this.width = width;
            bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            graphics2D = (Graphics2D) bufferedImage.getGraphics();
            fillBackground(Color.WHITE);
            if(cells){
                drawCells();
            }
            drawSections();
        }

        private void drawCells(){
            graphics2D.setColor(Color.PINK);
            for (int i = 0; i < Field.getInstance().getCellsCount() * 2 + 1; i++){
                graphics2D.drawLine(i * DrawConfigs.getInstance().getNodeSize(),
                        0,
                        i * DrawConfigs.getInstance().getNodeSize(),
                        Field.getInstance().getFieldSize_px());
                graphics2D.drawLine(0, i * DrawConfigs.getInstance().getNodeSize(),
                        Field.getInstance().getFieldSize_px(),
                        i * DrawConfigs.getInstance().getNodeSize());
            }
        }

        private void drawSections() {

            Section section = Field.getInstance().getWanSection();
            int y = section.getCells_Count_Y() * DrawConfigs.getInstance().getNodeSize() * 2;
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawLine(0, y, Field.getInstance().getFieldSize_px(), y);
            graphics2D.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 50));
            graphics2D.drawString(section.getName(),
                    Field.getInstance().getFieldSize_px()
                    - DrawConfigs.getInstance().getNodeSize(),
                    DrawConfigs.getInstance().getNodeSize() / 2);

            for (Section t : Field.getInstance().getLanSections()) {
                Color color;
                do { //
                    color = new Color( ThreadLocalRandom.current().nextInt(0, 256),
                            ThreadLocalRandom.current().nextInt(0, 256),
                            ThreadLocalRandom.current().nextInt(0, 256), 25);
                }
                while(ColorComparator.isContainLikeTone(color, usedColors));
                usedColors.add(color);
                graphics2D.setColor(color);
                int x = t.getBeginCell_X() * DrawConfigs.getInstance().getNodeSize() * 2;
                graphics2D.fillRect(x, t.getBeginCell_Y() * DrawConfigs.getInstance().getNodeSize() * 2,
                        DrawConfigs.getInstance().getNodeSize() * t.getCells_Count_X() * 2,
                        DrawConfigs.getInstance().getNodeSize() * t.getCells_Count_Y() * 2);
                graphics2D.setColor(Color.BLACK);
                graphics2D.drawString(t.getName(), x + DrawConfigs.getInstance().getNodeSize()
                        * t.getCells_Count_X() * 2 - DrawConfigs.getInstance().getNodeSize(),
                        t.getBeginCell_Y() * DrawConfigs.getInstance().getNodeSize() * 2 + DrawConfigs.getInstance().getNodeSize() / 2);
            }

        }

        private void fillBackground(Color color) {
            if(color != null) {
                graphics2D.setColor(color);
                graphics2D.fillRect(0, 0, width, height);
            }
        }

        private void drawPointsOnConnection(Node from, Node to){
            if(from != null && to != null) {
                Coordinates from_c = NodeCoordinatesConvertor.getCenter(from);
                Coordinates to_c = NodeCoordinatesConvertor.getCenter(to);
                graphics2D.setColor(Color.RED);

                float distance = (float) (DrawConfigs.getInstance().getNodeSize() / 1.5);
                float rab = (float) Math.sqrt(Math.pow(from_c.getX() - to_c.getX(), 2) + Math.pow(from_c.getY() - to_c.getY(), 2));
                float k = distance / rab;
                int xc = (int) (from_c.getX() + (to_c.getX() - from_c.getX()) * k);
                int yc = (int) (from_c.getY() + (to_c.getY() - from_c.getY()) * k);
                graphics2D.fillOval(xc - 5, yc - 5, 10, 10);
            }
        }
        private void drawConnection(Node from, Node to) {
            if(from != null && to != null) {
                graphics2D.setColor(Color.BLACK);
                Coordinates from_c = NodeCoordinatesConvertor.getCenter(from);
                Coordinates to_c = NodeCoordinatesConvertor.getCenter(to);
                graphics2D.drawLine(from_c.getX(), from_c.getY(), to_c.getX(), to_c.getY());
            }
        }

        private void drawNodeName(Node node){
            Coordinates text = NodeCoordinatesConvertor.getCenter(node);
            text.setY((int) (text.getY() + DrawConfigs.getInstance().getNodeSize() / 1.5));
            text.setX(text.getX() - 20);
            graphics2D.setColor(Color.BLACK);
            graphics2D.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 40));
            graphics2D.drawString("R" + (node.getID() + 1),
                    text.getX(), text.getY());
        }
        private void drawNode(Node node) throws Exception {
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

                    Coordinates nodeCoord = NodeCoordinatesConvertor.getLeftTopCorner(node);
                    graphics2D.drawImage(node_buffered_image, nodeCoord.getX(), nodeCoord.getY(), null);
                }

            }

        private void drawAllNodes(Network network) throws Exception {
            for (Node node: network.getNodes()){
                drawNode(node);
                drawNodeName(node);
            }
        }

        private void drawAllConnects(Network network){

            List<Pair<Node, Node>> pairList = new ArrayList<>();

            for (Node node: network.getNodes()){
                for(Node connectNode: node.getConnectedNodes()) {
                    if(!pairList.contains(new Pair<>(node, connectNode))
                            && !pairList.contains(new Pair<>(connectNode, node))){
                        pairList.add(new Pair<>(node, connectNode));
                        drawConnection(node, connectNode);
                        drawPointsOnConnection(node, connectNode);
                        drawPointsOnConnection(connectNode, node);
                    }

                }
            }



        }
        private void drawAllPointsOnConnection(Network network){
            for (Node node: network.getNodes()) {
                for (Node connectNode : node.getConnectedNodes()) {
                    drawPointsOnConnection(node, connectNode);
                }
            }
        }
        public void drawNetwork(Network network) throws Exception {
            drawAllConnects(network);
            drawAllNodes(network);
            drawAllPointsOnConnection(network);
        }
        public void drawTopology(Topology topology) throws Exception {
            for (Network n: topology.getNetworks()){
                drawAllConnects(n);
                drawAllPointsOnConnection(n);
            }
            for (Network n: topology.getNetworks()){
                drawAllNodes(n);

            }



        }
        public void saveImage(String imageDirectory) throws IOException {
            ImageIO.write(bufferedImage,"png", new FileOutputStream(imageDirectory));
        }
}
