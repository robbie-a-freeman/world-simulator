package edu.ding.eng;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MapWindow extends JFrame {

    public MapWindow(Map m) {
        super();
        m.setSize(500,500);
        this.add(m);

        setLayout(null);
        JButton normalMode = new JButton("normal");
        normalMode.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                m.setMapMode(0);
                m.repaint();
                m.revalidate();
            }

        });
        normalMode.setBounds(550, 50, 100, 50);
        add(normalMode);

        JButton tectonicMode = new JButton("tectonic");
        tectonicMode.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                m.setMapMode(1);
                m.repaint();
                m.revalidate();
            }

        });

        tectonicMode.setBounds(550, 110, 100, 50);
        add(tectonicMode);

        JButton heatMode = new JButton("heat");
        heatMode.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                m.setMapMode(2);
                m.repaint();
                m.revalidate();
            }

        });

        heatMode.setBounds(550, 170, 100, 50);
        add(heatMode);

        JButton heightMode = new JButton("height");
        heightMode.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                m.setMapMode(3);
                m.repaint();
                m.revalidate();
            }

        });

        heightMode.setBounds(550, 230, 100, 50);
        add(heightMode);

        JButton surfaceTempMode = new JButton("surface temp");
        surfaceTempMode.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent arg0) {
                m.setMapMode(4);
                m.repaint();
                m.revalidate();
            }

        });

        surfaceTempMode.setBounds(550, 290, 100, 50);
        add(surfaceTempMode);

        this.setVisible(true);
        this.setSize(700,600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}
