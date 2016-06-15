package edu.ding.eng;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;


public class Map extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3967835280265684766L;
	private int mapX = 500;
	private int mapY = 500;
	private Location locations[];
	private Location countryLocations[][];
	private int[] landTilesX = new int[mapX * mapY];
	private int[] landTilesY = new int[mapX * mapY];
	private Graphics g;

	public Map()
	{
		setLayout(new BorderLayout());
		JButton test = new JButton("test");
		add(test, BorderLayout.LINE_END);
		generateMap();
		
	}

	private void generateMap()
	{

	}

	public void paintComponent(Graphics g)
	{
		for(int x = 1; x < mapX + 1; x++){
			for(int y = 1; y < mapY + 1; y++){ //Not coloring in the null locations
				this.g = g;
				g.setColor(Color.GREEN);
				g.drawLine(x,y,x,y);
			}
		}

		if(countryLocations != null){ //for when Map is just generating terraforming screens
			updateCountryPoints(g);
		} else{
			updateTectonicPlates(g);
		}
	}

	private void updateCountryPoints(Graphics g)
	{
		for(int i = 0; i < countryLocations.length; i++){ //retrieves country colors and draws them on map
			if(countryLocations[i] != null){
				g.setColor(countryLocations[i][0].getColor());
				for(int x = 0; x < countryLocations[i].length; x++){
					if(countryLocations[i][x] == null){
						break;
					}
					else{
						//	System.out.println(countryLocations[i][x].getX() + ", " + countryLocations[i][x].getY());
						g.drawLine(countryLocations[i][x].getX(), countryLocations[i][x].getY(), countryLocations[i][x].getX(), countryLocations[i][x].getY());
					}
				}
			}
		}
	}

	private void updateTectonicPlates(Graphics g)
	{
		for(int i = 0; i < locations.length; i++){ //retrieves plate colors and draws them on map
			g.setColor(locations[i].getColor());
			System.out.println(locations[i].getX() + " " +  locations[i].getY());
			g.drawLine(locations[i].getX() + 1, locations[i].getY() + 1, locations[i].getX() + 1, locations[i].getY() + 1);

			System.out.println("done one " + i);
		}
		System.out.println("done");
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

	private class Handler implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent arg0) {
			if(arg0.equals("test")){
				System.out.println("TEST");
			}

		}
	}

}
