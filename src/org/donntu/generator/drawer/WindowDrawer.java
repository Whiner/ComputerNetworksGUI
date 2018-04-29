package org.donntu.generator.drawer;

import org.donntu.generator.Network;

import javax.swing.*;
import java.awt.*;

public class WindowDrawer extends JFrame {
    private Network network;
    private GeneratorDrawer drawer;

    @Override
    public void paint(Graphics g) {

        if(drawer == null){
            drawer = new GeneratorDrawer(g, 2000, 2000);
        }
        try {
            drawer.drawNetwork(network);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.paint(g);
    }
    public void setNetwork(Network network){
        this.network = network;
        //paint();
    }
    public WindowDrawer()
    {
        super("Test");
        JFrame.setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        pack();
        setSize(2000, 2000);
        setVisible(true);
    }
}
