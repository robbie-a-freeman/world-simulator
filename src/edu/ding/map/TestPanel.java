package edu.ding.map;

import javax.swing.*;
import java.awt.*;

// JPanel that is generated and drawn in for testing purposes
public class TestPanel extends JPanel {

    private Drawable obj;

    public TestPanel(Drawable obj) {
        super();

        this.obj = obj;

        setLayout(null);
        this.setSize(500, 500);
        this.setLocation(100, 100);
        this.repaint();
        this.revalidate();
        this.setVisible(true);
    }

    public void paintComponent(Graphics g) {
        //g.drawRect(100, 100, 500, 500); // borders

        obj.draw(g);
    }
}
