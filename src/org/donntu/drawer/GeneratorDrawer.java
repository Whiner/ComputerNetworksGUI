package org.donntu.drawer;

import javafx.util.Pair;
import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.*;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.donntu.drawer.other.ColorComparator;
import org.donntu.drawer.other.Coordinates;
import org.donntu.drawer.other.NodeCoordinatesConvertor;
import org.donntu.generator.field.Field;
import org.donntu.generator.field.Section;

import java.io.FileNotFoundException;
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


    public Image getImage() {
        return SwingFXUtils.toFXImage(bufferedImage, null);
    }

    public GeneratorDrawer(int height, int width, boolean cells) throws Exception {
        DrawConfigs.getInstance().calcNodeSize();
        this.height = height;
        this.width = width;
        bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        graphics2D = (Graphics2D) bufferedImage.getGraphics();
        fillBackground(Color.WHITE);
        if (cells) {
            drawCells();
        }
        drawSections();
    }

    public GeneratorDrawer(Graphics graphics, int width, int height){
        graphics2D = (Graphics2D) graphics;
        this.width = width;
        this.height = height;
    }

    private void drawCells() {
        graphics2D.setColor(Color.PINK);
        for (int i = 0; i < Field.getInstance().getCellsCountX() * 2 + 1; i++) {
            graphics2D.drawLine(i * DrawConfigs.getInstance().getNodeWidth(),
                    0,
                    i * DrawConfigs.getInstance().getNodeWidth(),
                    Field.getInstance().getHeight());
        }
        for (int i = 0; i < Field.getInstance().getCellsCountY() * 2 + 1; i++) {
            graphics2D.drawLine(0,
                    i * DrawConfigs.getInstance().getNodeHeight(),
                    Field.getInstance().getWidth(),
                    i * DrawConfigs.getInstance().getNodeHeight());
        }
    }

    private void drawSections() {

        Section section = Field.getInstance().getWanSection();
        int y = section.getCells_Count_Y() * DrawConfigs.getInstance().getNodeHeight() * 2;
        graphics2D.setColor(Color.BLACK);
        graphics2D.drawLine(0, y, Field.getInstance().getWidth(), y);
        graphics2D.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 50));
        graphics2D.drawString(section.getName(),
                Field.getInstance().getWidth()
                        - DrawConfigs.getInstance().getNodeWidth(),
                DrawConfigs.getInstance().getNodeHeight() / 2);

        for (Section t : Field.getInstance().getLanSections()) {
            Color color;
            do { //
                color = new Color(ThreadLocalRandom.current().nextInt(0, 256),
                        ThreadLocalRandom.current().nextInt(0, 256),
                        ThreadLocalRandom.current().nextInt(0, 256), 25);
            }
            while (ColorComparator.isContainLikeTone(color, usedColors));
            usedColors.add(color);
            graphics2D.setColor(color);
            int x = t.getBeginCell_X() * DrawConfigs.getInstance().getNodeWidth() * 2;
            graphics2D.fillRect(x,
                    t.getBeginCell_Y() * DrawConfigs.getInstance().getNodeHeight() * 2,
                    DrawConfigs.getInstance().getNodeWidth() * t.getCells_Count_X() * 2,
                    DrawConfigs.getInstance().getNodeHeight() * t.getCells_Count_Y() * 2);
            graphics2D.setColor(Color.BLACK);
            graphics2D.drawString(t.getName(),
                    x + DrawConfigs.getInstance().getNodeWidth()
                            * t.getCells_Count_X() * 2 - DrawConfigs.getInstance().getNodeWidth(),
                    t.getBeginCell_Y() * DrawConfigs.getInstance().getNodeHeight() * 2 + DrawConfigs.getInstance().getNodeHeight() / 2);
        }

    }

    private void fillBackground(Color color) {
        if (color != null) {
            graphics2D.setColor(color);
            graphics2D.fillRect(0, 0, width, height);
        }
    }

    private void drawPointOnConnection(Node from, Node to) {
        if (from != null && to != null) {
            Coordinates from_c = NodeCoordinatesConvertor.getCenter(from);
            Coordinates to_c = NodeCoordinatesConvertor.getCenter(to);
            graphics2D.setColor(Color.RED);

            float distance = (float) (Math.max(
                    DrawConfigs.getInstance().getNodeWidth(),
                    DrawConfigs.getInstance().getNodeHeight() )
                    / 1.5); // пересмотреть тут
            float rab = (float) Math.sqrt(Math.pow(from_c.getX() - to_c.getX(), 2) + Math.pow(from_c.getY() - to_c.getY(), 2));
            float k = distance / rab;
            int xc = (int) (from_c.getX() + (to_c.getX() - from_c.getX()) * k);
            int yc = (int) (from_c.getY() + (to_c.getY() - from_c.getY()) * k);
            graphics2D.fillOval(xc - 5, yc - 5, 10, 10);
        }
    }

    private void drawConnection(Node from, Node to) {
        if (from != null && to != null) {
            graphics2D.setColor(Color.BLACK);
            Coordinates from_c = NodeCoordinatesConvertor.getCenter(from);
            Coordinates to_c = NodeCoordinatesConvertor.getCenter(to);
            graphics2D.drawLine(from_c.getX(), from_c.getY(), to_c.getX(), to_c.getY());
        }
    }

    private void drawNodeName(Node node) {
        Coordinates text = NodeCoordinatesConvertor.getCenter(node);
        text.setY((int) (text.getY() + DrawConfigs.getInstance().getNodeHeight() / 1.5));
        text.setX(text.getX() - 20);
        graphics2D.setColor(Color.BLACK);
        graphics2D.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 40));
        graphics2D.drawString("R" + (node.getID() + 1),
                text.getX(), text.getY());
    }

    private void drawNode(Node node) throws Exception {
        if (node != null) {

            java.awt.Image node_image = DrawConfigs.getInstance().getNodeImage();
            if (node_image == null) {
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
        for (Node node : network.getNodes()) {
            drawNode(node);
            drawNodeName(node);
        }
    }

    private void drawAllConnections(Network network) {
        graphics2D.setColor(Color.BLACK);
        List<Pair<Node, Node>> pairList = network.getUniqueConnections();

        for (Pair<Node, Node> pair : pairList) {
            drawConnection(pair.getKey(), pair.getValue());
            drawPointOnConnection(pair.getKey(), pair.getValue());
            drawPointOnConnection(pair.getValue(), pair.getKey());
        }

    }

    private void drawNetworksConnections(Topology topology){
        graphics2D.setColor(Color.BLACK);
        final List<NetworksConnection> uniqueNetworksConnections = topology.getUniqueNetworksConnections();
        for (NetworksConnection connection: uniqueNetworksConnections){
            drawConnection(connection.getFromNetworkNode(), connection.getToNetworkNode());
            drawPointOnConnection(connection.getFromNetworkNode(), connection.getToNetworkNode());
            drawPointOnConnection(connection.getToNetworkNode(), connection.getFromNetworkNode());
        }
    }
    private void drawAllPointsOnConnection(Network network) {
        for (Node node : network.getNodes()) {
            for (Node connectNode : node.getConnectedNodes()) {
                drawPointOnConnection(node, connectNode);
            }
        }
    }

    private void drawNetwork(Network network) throws Exception {
        drawAllConnections(network);
        drawAllNodes(network);
        drawAllPointsOnConnection(network);
    }

    private void drawTopology(Topology topology) throws Exception {
        graphics2D.setColor(Color.BLACK);
        drawNetworksConnections(topology);
        for (Network n : topology.getNetworks()) {
            drawAllConnections(n);
            drawAllPointsOnConnection(n);
        }
        for (Network n : topology.getNetworks()) {
            drawAllNodes(n);
        }
    }

    public void drawAndSaveStudentTask(StudentTask studentTask, String imageDirectory, String imageName) throws Exception {

        BufferedImage studentTaskBufferedImage = new BufferedImage(width, height + 500, BufferedImage.TYPE_INT_ARGB);
        Graphics2D studentTaskGraphics2D = (Graphics2D) studentTaskBufferedImage.getGraphics();
        studentTaskGraphics2D.setColor(Color.WHITE);
        studentTaskGraphics2D.fillRect(0, 0, studentTaskBufferedImage.getWidth(), studentTaskBufferedImage.getHeight());
        drawTopology(studentTask.getTopology());
        studentTaskGraphics2D.drawImage(bufferedImage, 0, 300, null);

        studentTaskGraphics2D.setFont(new Font(Font.DIALOG_INPUT, Font.ITALIC, 50));
        studentTaskGraphics2D.setColor(Color.BLACK);
        studentTaskGraphics2D.drawString(studentTask.getCreationDate().toString(), 5, 50);
        studentTaskGraphics2D.drawString(studentTask.getName() + " " + studentTask.getSurname() + " \"" + studentTask.getGroup() + "\"", 5, 150);

        final List<Network> networks = studentTask.getTopology().getNetworks();
        int x = 20;
        int step = Field.getInstance().getCellsCountX() / (networks.size() - 1);
        for (Network network : networks) {
            if (network.getType() == NetworkType.LAN) {
                studentTaskGraphics2D.drawString(network.getIp().toString(), x, 300 + bufferedImage.getHeight() - 5);
                x += step * DrawConfigs.getInstance().getNodeWidth() * 2;
            } else {
                studentTaskGraphics2D.drawString(network.getIp().toString(), 50, 300 - 15);
            }
        }


        ImageIO.write(studentTaskBufferedImage, "png", new FileOutputStream(imageDirectory + "/" + imageName + ".png"));
    }

    public void saveImage(String imageDirectory) throws IOException {
        ImageIO.write(bufferedImage, "png", new FileOutputStream(imageDirectory));
    }
}
