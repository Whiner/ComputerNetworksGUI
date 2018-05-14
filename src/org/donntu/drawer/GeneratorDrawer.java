package org.donntu.drawer;

import javafx.util.Pair;
import org.donntu.databaseworker.StudentTask;
import org.donntu.generator.*;
import org.donntu.drawer.other.ColorComparator;
import org.donntu.drawer.other.Coordinates;
import org.donntu.drawer.other.NodeCoordinatesConvertor;
import org.donntu.generator.field.Field;
import org.donntu.generator.field.Section;

import java.awt.image.RenderedImage;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class GeneratorDrawer {
    private static BufferedImage tempBufferedImage;
    private static Graphics2D tempGraphics2D;
    private static List<Color> usedColors = new ArrayList<>();
    private static DrawConfig config = DrawConfig.getInstance();


    private GeneratorDrawer(){}

    private static void refresh(){
        tempBufferedImage = new BufferedImage(config.getImageWidth(), config.getImageHeight(), BufferedImage.TYPE_INT_ARGB);
        tempGraphics2D = (Graphics2D) tempBufferedImage.getGraphics();
        fillBackground(Color.WHITE);
    }

    private static void clear(){
        tempBufferedImage.getGraphics().dispose();
        usedColors.clear();
    }

    public static void setConfig(DrawConfig config) {
        if (config != null) {
            GeneratorDrawer.config = config;
            refresh();
        }
    }

    private static void drawCells() {
        tempGraphics2D.setColor(Color.PINK);
        for (int i = 0; i < Field.getInstance().getCellsCountX() * 2 + 1; i++) {
            tempGraphics2D.drawLine(i * DrawConfig.getInstance().getNodeWidth(),
                    0,
                    i * DrawConfig.getInstance().getNodeWidth(),
                    config.getImageHeight());
        }
        for (int i = 0; i < Field.getInstance().getCellsCountY() * 2 + 1; i++) {
            tempGraphics2D.drawLine(0,
                    i * DrawConfig.getInstance().getNodeHeight(),
                    config.getImageWidth(),
                    i * DrawConfig.getInstance().getNodeHeight());
        }
    }

    private static void drawSections() {
        Section section = Field.getInstance().getWanSection();
        int y = section.getCells_Count_Y() * DrawConfig.getInstance().getNodeHeight() * 2;
        tempGraphics2D.setColor(Color.BLACK);
        tempGraphics2D.drawLine(0, y,config.getImageWidth(), y);
        tempGraphics2D.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 50));
        tempGraphics2D.drawString(section.getName(),
                config.getImageWidth()
                        - DrawConfig.getInstance().getNodeWidth(),
                DrawConfig.getInstance().getNodeHeight() / 2);

        for (Section t : Field.getInstance().getLanSections()) {
            Color color;
            int cycleProtect = 0;
            do {
                color = new Color(ThreadLocalRandom.current().nextInt(0, 256),
                        ThreadLocalRandom.current().nextInt(0, 256),
                        ThreadLocalRandom.current().nextInt(0, 256), 25);
                cycleProtect++;
            }
            while (ColorComparator.isContainLikeTone(color, usedColors) && cycleProtect < 100);
            usedColors.add(color);
            tempGraphics2D.setColor(color);
            int x = t.getBeginCell_X() * DrawConfig.getInstance().getNodeWidth() * 2;
            tempGraphics2D.fillRect(x,
                    t.getBeginCell_Y() * DrawConfig.getInstance().getNodeHeight() * 2,
                    DrawConfig.getInstance().getNodeWidth() * t.getCells_Count_X() * 2,
                    DrawConfig.getInstance().getNodeHeight() * t.getCells_Count_Y() * 2);
            tempGraphics2D.setColor(Color.BLACK);
            tempGraphics2D.drawString(t.getName(),
                    x + DrawConfig.getInstance().getNodeWidth()
                            * t.getCells_Count_X() * 2 - DrawConfig.getInstance().getNodeWidth(),
                    t.getBeginCell_Y() * DrawConfig.getInstance().getNodeHeight() * 2 + DrawConfig.getInstance().getNodeHeight() / 2);
        }
        usedColors.clear();
    }

    private static void fillBackground(Color color) {
        if (color != null) {
            tempGraphics2D.setColor(color);
            tempGraphics2D.fillRect(0, 0, config.getImageWidth(), config.getImageHeight());
        }
    }

    private static void drawPointOnConnection(Node from, Node to) {
        if (from != null && to != null) {
            Coordinates from_c = NodeCoordinatesConvertor.getCenter(from);
            Coordinates to_c = NodeCoordinatesConvertor.getCenter(to);
            tempGraphics2D.setColor(Color.RED);

            float distance = (float) (Math.max(
                    DrawConfig.getInstance().getNodeWidth(),
                    DrawConfig.getInstance().getNodeHeight() )
                    / 1.5); // пересмотреть тут
            float rab = (float) Math.sqrt(Math.pow(from_c.getX() - to_c.getX(), 2) + Math.pow(from_c.getY() - to_c.getY(), 2));
            float k = distance / rab;
            int xc = (int) (from_c.getX() + (to_c.getX() - from_c.getX()) * k);
            int yc = (int) (from_c.getY() + (to_c.getY() - from_c.getY()) * k);
            tempGraphics2D.fillOval(xc - 5, yc - 5, 10, 10);
        }
    }

    private static void drawConnection(Node from, Node to) {
        if (from != null && to != null) {
            tempGraphics2D.setColor(Color.BLACK);
            Coordinates from_c = NodeCoordinatesConvertor.getCenter(from);
            Coordinates to_c = NodeCoordinatesConvertor.getCenter(to);
            tempGraphics2D.drawLine(from_c.getX(), from_c.getY(), to_c.getX(), to_c.getY());
        }
    }

    private static void drawNodeName(Node node) {
        Coordinates text = NodeCoordinatesConvertor.getCenter(node);
        text.setY((int) (text.getY() + DrawConfig.getInstance().getNodeHeight() / 1.5));
        text.setX(text.getX() - 20);
        tempGraphics2D.setColor(Color.BLACK);
        tempGraphics2D.setFont(new Font(Font.DIALOG_INPUT, Font.BOLD, 40));
        tempGraphics2D.drawString("R" + (node.getID() + 1),
                text.getX(), text.getY());
    }

    private static void drawNode(Node node) throws Exception {
        if (node != null) {

            java.awt.Image node_image = DrawConfig.getInstance().getNodeImage();
            if (node_image == null) {
                throw new Exception("Set node image to DrawConfig");
            }
            BufferedImage node_buffered_image = new BufferedImage(
                    node_image.getWidth(null),
                    node_image.getHeight(null),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics bGr = node_buffered_image.createGraphics();

            bGr.drawImage(node_image, 0, 0, null);
            bGr.dispose();

            Coordinates nodeCoord = NodeCoordinatesConvertor.getLeftTopCorner(node);
            tempGraphics2D.drawImage(node_buffered_image, nodeCoord.getX(), nodeCoord.getY(), null);
        }

    }

    private static void drawAllNodes(Network network) throws Exception {
        for (Node node : network.getNodes()) {
            drawNode(node);
            drawNodeName(node);
        }
    }

    private static void drawAllConnections(Network network) {
        tempGraphics2D.setColor(Color.BLACK);
        List<Pair<Node, Node>> pairList = network.getUniqueConnections();

        for (Pair<Node, Node> pair : pairList) {
            drawConnection(pair.getKey(), pair.getValue());
            drawPointOnConnection(pair.getKey(), pair.getValue());
            drawPointOnConnection(pair.getValue(), pair.getKey());
        }

    }

    private static void drawNetworksConnections(Topology topology){
        tempGraphics2D.setColor(Color.BLACK);
        final List<NetworksConnection> uniqueNetworksConnections = topology.getUniqueNetworksConnections();
        for (NetworksConnection connection: uniqueNetworksConnections){
            drawConnection(connection.getFromNetworkNode(), connection.getToNetworkNode());
            drawPointOnConnection(connection.getFromNetworkNode(), connection.getToNetworkNode());
            drawPointOnConnection(connection.getToNetworkNode(), connection.getFromNetworkNode());
        }
    }

    private static void drawAllPointsOnConnection(Network network) {
        for (Node node : network.getNodes()) {
            for (Node connectNode : node.getConnectedNodes()) {
                drawPointOnConnection(node, connectNode);
            }
        }
    }

    private static void drawTopology(Topology topology) throws Exception {
        //drawCells();
        drawSections();
        tempGraphics2D.setColor(Color.BLACK);
        drawNetworksConnections(topology);
        for (Network n : topology.getNetworks()) {
            drawAllConnections(n);
            drawAllPointsOnConnection(n);
        }
        for (Network n : topology.getNetworks()) {
            drawAllNodes(n);
        }
    }

    private static boolean createDirectory(String directory){
        Path path = Paths.get(directory);
        if(!Files.exists(path)){
            try {
                Files.createDirectory(path);
            } catch (IOException e) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    public static Image drawStudentTask(StudentTask studentTask) throws Exception {
        Field.getInstance().autoFilling(studentTask.getTopology());
        refresh();
        BufferedImage studentTaskBufferedImage = new BufferedImage(config.getImageWidth(), config.getImageHeight() + 500, BufferedImage.TYPE_INT_ARGB);
        Graphics2D studentTaskGraphics2D = (Graphics2D) studentTaskBufferedImage.getGraphics();
        studentTaskGraphics2D.setColor(Color.WHITE);
        studentTaskGraphics2D.fillRect(0, 0, studentTaskBufferedImage.getWidth(), studentTaskBufferedImage.getHeight());
        drawTopology(studentTask.getTopology());
        studentTaskGraphics2D.drawImage(tempBufferedImage, 0, 300, null);

        studentTaskGraphics2D.setFont(new Font(Font.DIALOG_INPUT, Font.ITALIC, 50));
        studentTaskGraphics2D.setColor(Color.BLACK);
        studentTaskGraphics2D.drawString(studentTask.getCreationDate().toString(), 5, 50);
        studentTaskGraphics2D.drawString(studentTask.getSurname() + " " + studentTask.getName() + " " + studentTask.getGroup(), 5, 150);

        final List<Network> networks = studentTask.getTopology().getNetworks();
        int x = 20;
        int step = Field.getInstance().getCellsCountX() / (networks.size() - 1);
        for (Network network : networks) {
            if (network.getType() == NetworkType.LAN) {
                studentTaskGraphics2D.drawString(network.getIp().toString(), x, 300 + tempBufferedImage.getHeight() - 5);
                x += step * DrawConfig.getInstance().getNodeWidth() * 2;
            } else {
                studentTaskGraphics2D.drawString(network.getIp().toString(), 50, 300 - 15);
            }
        }
        clear();
        return studentTaskBufferedImage;
    }

    public static void saveImage(String imageDirectory, String imageName, Image image) throws IOException {
        createDirectory(imageDirectory);
        ImageIO.write((RenderedImage) image, "png",
                new FileOutputStream(
                        imageDirectory + "/" + imageName + ".png"));
        /*+ studentTask.getSurname() + " "
                + studentTask.getName() + " "
                + studentTask.getGroup() + " "
                + new GregorianCalendar().toString()*/
    }
}
