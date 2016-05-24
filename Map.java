import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;


public class Map extends JPanel implements Runnable{

	private int mapX = 500;
	private int mapY = 500;
	private Location locations[];
	private Location countryLocations[][];
	private int[] landTilesX = new int[mapX * mapY];
	private int[] landTilesY = new int[mapX * mapY];
	private Graphics g;

	public Map()
	{
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

		updateCountryPoints(g);

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
