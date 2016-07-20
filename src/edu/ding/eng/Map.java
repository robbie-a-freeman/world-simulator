package edu.ding.eng;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

	}

	public void updateTectonicPlates(List<TectonicPlate> tectonicPlates){
		this.tectonicPlates = tectonicPlates;
	}

	private void generateMap()
	{

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

	private void updateSurfaceTempGradients(Graphics g) 
	{
		for(int i = 0; i < locations.length; i++){ //retrieves heights and projects them onto the map TODO FINISH
			double temp = locations[i].getTemperature();
			double red = temp;
			double green = 0;
			double blue = 0;
			if(red > 255){
				red = 255;
			} else if(red < 0){
				red = 0;
			}
			g.setColor(new Color((int) red, (int) green, (int) blue));
			g.drawLine(locations[i].getX() + 1, locations[i].getY() + 1, locations[i].getX() + 1, locations[i].getY() + 1);
		}
	}

	private void updateCountryPoints(Graphics g)
	{
		for(int i = 0; i < countryLocations.length; i++){ //retrieves country colors and draws them on map
			if(countryLocations[i] != null){
				g.setColor(countryLocations[i][0].getColor());
				for(int x = 0; x < countryLocations[i].length; x++){
					if(countryLocations[i][x] != null){
						g.drawLine(countryLocations[i][x].getX(), countryLocations[i][x].getY(), countryLocations[i][x].getX(), countryLocations[i][x].getY());
					}
				}
			}
		}
	}

	private void updateNormalPoints(Graphics g)
	{
		for(int i = 0; i < locations.length; i++){ //retrieves country colors and draws them on map
			if(locations[i] != null){
				if(locations[i].isLand()){
					g.setColor(Color.GREEN);
					g.drawLine(locations[i].getX(),locations[i].getY(),locations[i].getX(),locations[i].getY());
				}
				else{
					g.setColor(Color.BLUE);
					g.drawLine(locations[i].getX(),locations[i].getY(),locations[i].getX(),locations[i].getY());
					//System.out.println(locations[i].getX() + ", " + locations[i].getY());
				}
			}
		}
	}

	private void updateTectonicPlates(Graphics g)
	{
		for(int i = 0; i < locations.length; i++){ //retrieves plate colors and draws them on map
			g.setColor(locations[i].getColor()); //TODO Clean up and fix
			g.drawLine(locations[i].getX() + 1, locations[i].getY() + 1, locations[i].getX() + 1, locations[i].getY() + 1);
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
			double temp = locations[i].getMantleTemperature();
			if(temp > 255){
				temp = 255;
			}
			g.setColor(new Color((int) temp, 0, 0));
			g.drawLine(locations[i].getX() + 1, locations[i].getY() + 1, locations[i].getX() + 1, locations[i].getY() + 1);
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
				blue = 255 + 255 / 50000 * height; //NOTE: height will always be negative here
			}
			g.setColor(new Color((int) red, (int) green, (int) blue));
			g.drawLine(locations[i].getX() + 1, locations[i].getY() + 1, locations[i].getX() + 1, locations[i].getY() + 1);
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
