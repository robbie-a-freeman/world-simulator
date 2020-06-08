package edu.ding.eng;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.ding.map.TectonicPlate;


public class Map extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3967835280265684766L;
	private int mapX = 500;
	private int mapY = 500;
	private Location locations[] = new Location[mapX * mapY];
	private Location countryLocations[][] = null;
	private int mapMode = 0; //0 is normal, 1 is tectonic, 2 is heat, 3 is height, 4 is surface temp
	private int[] landTilesX = new int[mapX * mapY];
	private int[] landTilesY = new int[mapX * mapY];
	private List<TectonicPlate> tectonicPlates = new ArrayList<TectonicPlate>();
	private Graphics g;

	private int minXView = 0;
	private int maxXView = 500;
	private int minYView = 0;
	private int maxYView = 500;


	public Map()
	{
		setLayout(null);
		JButton normalMode = new JButton("normal");
		normalMode.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mapMode = 0;
				repaint();
			}

		});
		normalMode.setBounds(550, 50, 100, 50);
		add(normalMode);

		JButton tectonicMode = new JButton("tectonic");
		tectonicMode.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mapMode = 1;
				repaint();
			}

		});

		tectonicMode.setBounds(550, 110, 100, 50);
		add(tectonicMode);

		JButton heatMode = new JButton("heat");
		heatMode.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mapMode = 2;
				repaint();
			}

		});

		heatMode.setBounds(550, 170, 100, 50);
		add(heatMode);

		JButton heightMode = new JButton("height");
		heightMode.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mapMode = 3;
				repaint();
			}

		});

		heightMode.setBounds(550, 230, 100, 50);
		add(heightMode);

		JButton surfaceTempMode = new JButton("surface temp");
		surfaceTempMode.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				mapMode = 4;
				repaint();
			}

		});

		surfaceTempMode.setBounds(550, 290, 100, 50);
		add(surfaceTempMode);

		// listener for zooming in
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int mouseWheelChange = e.getWheelRotation();
				// negative = zoom in, positive = zoom out
				if (mouseWheelChange < 0) {
					zoomIn(e.getX(), e.getY());
					//System.out.println("Zooming in");
					repaint();
				} else {
					zoomOut(e.getX(), e.getY());
					//System.out.println("Zooming out");
					repaint();
				}
			}
		});

		this.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                System.out.println("being dragged");
                translateWindow(e.getX(), e.getY());
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                //System.out.println("unimplemented"); - probably for info popups in the future
            }
        });

	}

	// zooms in, with a focus towards centering on x, y
	private void zoomIn(int x, int y) {

	    if (x < 0) {
	        x = 0;
        } else if (x > mapX) {
	        x = mapX;
        }
        if (y < 0) {
            y = 0;
        } else if (y > mapY) {
            y = mapY;
        }

        // todo
		int centerX = minXView + (maxXView - minXView) * x / mapX;
		int centerY = minYView + (maxYView - minYView) * y / mapY;

		int halfXView = (int) ((maxXView - minXView) / 2 / 1.5);
		int halfYView = (int) ((maxYView - minYView) / 2 / 1.5);

		if (centerX - halfXView < 0) {
			maxXView = halfXView * 2;
			minXView = 0;
		} else if (centerX + halfXView >= mapX) {
			minXView = mapX - halfXView * 2;
			maxXView = mapX - 1;
		} else {
			maxXView = centerX + halfXView;
			minXView = centerX - halfXView;
		}

		if (centerY - halfYView < 0) {
			maxYView = halfYView * 2;
			minYView = 0;
		}  else if (centerY + halfYView >= mapY) {
			minYView = mapY - halfYView * 2;
			maxYView = mapY - 1;
		} else {
			maxYView = centerY + halfYView;
			minYView = centerY - halfYView;
		}

		// todo
		if (minXView < 0) {
		    minXView = 0;
        }
        if (maxXView > mapX - 1) {
		    maxXView = mapX - 1;
        }
        if (minYView < 0) {
            minYView = 0;
        }
        if (maxYView > mapY - 1) {
            maxYView = mapY - 1;
        }

	}

	// zooms out, with a focus from x, y
	private void zoomOut(int x, int y) { //todo wont zoom out if too zoomed in

        if (x < 0) {
            x = 0;
        } else if (x > mapX) {
            x = mapX;
        }
        if (y < 0) {
            y = 0;
        } else if (y > mapY) {
            y = mapY;
        }

        // todo modularize screen coords
        int centerX = minXView + (maxXView - minXView) * x / mapX;
        int centerY = minYView + (maxYView - minYView) * y / mapY;

        int halfXView = (int) ((maxXView - minXView) / 2 * 1.5);
        int halfYView = (int) ((maxYView - minYView) / 2 * 1.5);

        if (centerX - halfXView < 0) {
            maxXView = halfXView * 2;
            minXView = 0;
        } else if (centerX + halfXView >= mapX) {
            minXView = mapX - halfXView * 2;
            maxXView = mapX - 1;
        } else {
            maxXView = centerX + halfXView;
            minXView = centerX - halfXView;
        }

        if (centerY - halfYView < 0) {
            maxYView = halfYView * 2;
            minYView = 0;
        }  else if (centerY + halfYView >= mapY) {
            minYView = mapY - halfYView * 2;
            maxYView = mapY - 1;
        } else {
            maxYView = centerY + halfYView;
            minYView = centerY - halfYView;
        }

        //todo clamp method
        if (minXView < 0) {
            minXView = 0;
        }
        if (maxXView > mapX) {
            maxXView = mapX;
        }
        if (minYView < 0) {
            minYView = 0;
        }
        if (maxYView > mapY) {
            maxYView = mapY;
        }
	}

	private void translateWindow(int x, int y) {
        // todo modularize screen coords
        int prevCenterX = (minXView + maxXView) / 2;
        int prevCenterY = (minYView + maxYView) / 2;

        System.out.println("From " + prevCenterX + ", " + prevCenterY + " to " + x + ", " + y);

        if (x < 0) {
            return;
        } else if (x > mapX - 1) {
            return;
        }
        if (y < 0) {
            return;
        } else if (y > mapY - 1) {
            return;
        }


        // translate
        int deltaX = x - prevCenterX; // TODO scale x and y
        int deltaY = y - prevCenterY;

        int halfXView = (maxXView - minXView) / 2;
        int halfYView = (maxYView - minYView) / 2;

        if (x - halfXView < 0) {
            maxXView = halfXView * 2;
            minXView = 0;
        } else if (x + halfXView >= mapX) {
            minXView = mapX - halfXView * 2;
            maxXView = mapX - 1; //todo
        } else {
            maxXView += deltaX;
            minXView += deltaX;
        }

        if (y - halfYView < 0) {
            maxYView = halfYView * 2;
            minYView = 0;
        }  else if (y + halfYView >= mapY) {
            minYView = mapY - halfYView * 2;
            maxYView = mapY - 1; //todo
        } else {
            maxYView += deltaY;
            minYView += deltaY;
        }

    }

	// paints a location at the given
	private void paintPoint(Graphics g, Color c, Location loc) {

	}

	public void updateTectonicPlates(List<TectonicPlate> tectonicPlates){
		this.tectonicPlates = tectonicPlates;
	}

	public void paintComponent(Graphics g)
	{
		switch(mapMode){
		case 0:
			updateNormalPoints(g);
			if(countryLocations != null){
				updateCountryPoints(g);
			}
			break;
		case 1:
			updateTectonicPlates(g);
			break;
		case 2:
			updateHeatGradients(g);
			break;
		case 3:
			updateHeightGradients(g);
			break;
		case 4:
			updateSurfaceTempGradients(g);
			break;
		default:
			System.out.println("ERROR");
			break;
		}
	}

	private void updateNormalPoints(Graphics g)
	{
		/*for(int i = 0; i < locations.length; i++){ //retrieves country colors and draws them on map
			if(locations[i] != null){
				if(locations[i].isLand()){
					g.setColor(Color.GREEN);
					g.drawLine(locations[i].getX().intValue(),locations[i].getY().intValue(),locations[i].getX().intValue(),locations[i].getY().intValue());
				}
				else{
					g.setColor(Color.BLUE);
					g.drawLine(locations[i].getX().intValue(),locations[i].getY().intValue(),locations[i].getX().intValue(),locations[i].getY().intValue());
					//System.out.println(locations[i].getX() + ", " + locations[i].getY());
				}
			}
		} */

		for(int y = 0; y < mapY; y++){ //retrieves land colors and draws them on map
			for(int x = 0; x < mapX; x++){
				double locX = minXView + (maxXView - minXView) * x / (double) mapX;
				double locY = minYView + (maxYView - minYView) * y / (double) mapY;
				boolean isLand = locations[mapX * (int) locY + (int) locX].isLand();
				Color c = Color.BLUE;
				if (isLand)
					c = Color.GREEN;
				g.setColor(c);
				g.drawLine(x, y, x, y);
			}
		}
	}

	private void updateSurfaceTempGradients(Graphics g) 
	{
		for(int i = 0; i < locations.length; i++){ //retrieves heights and projects them onto the map TODO FINISH
			double temp = locations[i].getTemperature();
			double red = temp * 255/600 - 100;
			double green = 0;
			double blue = 0;
			if(red > 255){
				green = 255; //error shows
				red = 0;
			} else if(red < 0){
				red = 0;
			}
			g.setColor(new Color((int) red, (int) green, (int) blue));
			g.drawLine(locations[i].getX().intValue(), locations[i].getY().intValue(), locations[i].getX().intValue(), locations[i].getY().intValue());
		}
	}

	private void updateCountryPoints(Graphics g)
	{
		for(int i = 0; i < countryLocations.length; i++){ //retrieves country colors and draws them on map
			if(countryLocations[i] != null){
				g.setColor(countryLocations[i][0].getColor());
				for(int x = 0; x < countryLocations[i].length; x++){
					if(countryLocations[i][x] != null){
						g.drawLine( countryLocations[i][x].getX().intValue(), countryLocations[i][x].getY().intValue(), countryLocations[i][x].getX().intValue(), countryLocations[i][x].getY().intValue() );
					}
				}
			}
		}
	}

	private void updateTectonicPlates(Graphics g)
	{
		for(int i = 0; i < locations.length; i++){ //retrieves plate colors and draws them on map
			g.setColor(locations[i].getColor()); //TODO Clean up and fix
			g.drawLine(locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue(), locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue());
		}
		for(TectonicPlate t: tectonicPlates){ //Draw arrows
			g.setColor(Color.BLACK);
			g.drawLine((int) t.getCenterX(), (int) t.getCenterY(), (int) (t.getCenterX() +  30 * Math.cos(t.getConvectionCurrent().getDirection())), (int) (t.getCenterY() +  30 * Math.sin(t.getConvectionCurrent().getDirection())));
			g.drawLine((int) (t.getCenterX() +  30 * Math.cos(t.getConvectionCurrent().getDirection())) + 5, (int) (t.getCenterY() +  30 * Math.sin(t.getConvectionCurrent().getDirection())) + 5, (int) (t.getCenterX() +  30 * Math.cos(t.getConvectionCurrent().getDirection()) - 5), (int) (t.getCenterY() +  30 * Math.sin(t.getConvectionCurrent().getDirection())) - 5);
		}

	}

	private void updateHeatGradients(Graphics g)
	{
		for(int i = 0; i < locations.length; i++){ //retrieves plate temperatures and draws them on map
			double temp = locations[i].getMantleTemperature(); //should be between about 200 and 400, with most around 330
			
			if(temp > 255){
				temp = 255;
			}
			g.setColor(new Color((int) temp, 0, 0));
			g.drawLine(locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue(), locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue());
		}
	}

	private void updateHeightGradients(Graphics g)
	{
		for(int i = 0; i < locations.length; i++){ //retrieves heights and projects them onto the map TODO FINISH
			double height = locations[i].getElevation();
			double red = 0;
			double green = 0;
			double blue = 0;
			if(height > 0){ //above sea level
				red = 102. / 50000. * height;
				if(red > 102){
					red = 102;
				}
				green = 255 - 204 / 50000. * height;
				if(green > 255){
					green = 255;
				} else if(green < 41){
					green = 41;
				}
			}else{ //at or below sea level
				blue = 255 + 255 / 10000 * height; //NOTE: height will always be negative here
			}
			g.setColor(new Color((int) red, (int) green, (int) blue));
			g.drawLine(locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue(), locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue());
		}
	}

	@Override
	public void run()
	{
		repaint();
	}

	public void updateLocations(Location[] locs, Location[][] countryLocs) 
	{
		locations = locs;
		countryLocations = countryLocs;
	}

}
