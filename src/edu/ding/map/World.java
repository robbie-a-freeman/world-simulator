package edu.ding.map;

import java.awt.Dimension;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import edu.ding.eng.Location;
import edu.ding.eng.Map;

public class World {

	private List<HeightGradient> heightGradients; //List<Type> list3 = new ArrayList<Type>(list1);
	private Atmosphere atmosphere;
	private List<TectonicPlate> tectonicPlates;
	private List<Border> borders;
	private int[] worldSize;
	private List<ConvectionCurrent> convectionCurrents;
	private Mantle mantle;
	private Core core;
	private List<HeatGradient> heatGradients;

	public World(int sizeX, int sizeY){
		worldSize = new int[2];
		worldSize[0] = sizeX;
		worldSize[1] = sizeY;

		tectonicPlates = new ArrayList<TectonicPlate>();

		SecureRandom r = new SecureRandom(); //generate tectonic plates
		for(int x = 0; x < 100; x++){
			tectonicPlates.add(new TectonicPlate((double) r.nextInt(worldSize[0]), (double) r.nextInt(worldSize[1]), worldSize[0], worldSize[1], x));
		}

		borders = new ArrayList<Border>();

		/*for(int x = 0; x < 100; x++){ //calculate borders TODO get border algorithm
			for(int i = 0; i < 100; i++){
				borders.add(new Border(x, ));
			}
		}*/

		int x = r.nextInt(6) + 5; //generate mantle heat gradients
		heatGradients = new ArrayList<HeatGradient>();
		for(int i = 0; i < x; i++){
			heatGradients.add(new HeatGradient((double) r.nextInt(worldSize[0]), (double) r.nextInt(worldSize[1]), worldSize[0], worldSize[1]));
		}

		core = new Core();
		mantle = new Mantle();

		run();

	}

	private void movePlates(){
		for(int x = 0; x < tectonicPlates.size(); x++){
			tectonicPlates.get(x).move();
		}
	}

	private void changeAtmosphere(){

	}

	private boolean isMapDone(){
		return false;
	}

	public Location[] generateMap(int locationNumber){
		Location[] locations = new Location[locationNumber]; //update with actual spaces
		int y = 0;
		for(int x = 0; x < locations.length; x++){
			if(x % worldSize[0] == 0){
				y++;
			}
			TectonicPlate t = null;
			double maxControl = 0; //nearest neighbor interpolation with tectonic plate gradients
			for(int i = 0; i < tectonicPlates.size(); i++){
				if(tectonicPlates.get(i).getT().calcNetStrength(x - (y - 1) * worldSize[0], y) > maxControl){
					maxControl = tectonicPlates.get(i).getT().calcNetStrength(x - (y - 1) * worldSize[0], y);
					t = tectonicPlates.get(i);
				}
			}
			locations[x] = new Location(x - (y - 1) * worldSize[0], y);
			locations[x].setColor(t.getColor());

			double heat = 0; //nearest neighbor interpolation with heat gradients
			for(int i = 0; i < heatGradients.size(); i++){
				if(x == heatGradients.get(1).centerX + (heatGradients.get(1).centerY - 1) * worldSize[0]){
					System.out.println(heat);
				}
				heat += heatGradients.get(i).calcNetStrength(x - (y - 1) * worldSize[0], y);
			}
			locations[x] = new Location(x - (y - 1) * worldSize[0], y);
			locations[x].setColor(t.getColor());
			
			if(heat >= 200.0){ // TODO expand and fix - creates land from hot spots
				locations[x].setLand(true);
			}
			locations[x].setMantleTemperature(heat);
		}
		return locations;
	}

	public void printFrame(){
		Map m = new Map();
		m.updateLocations(generateMap(worldSize[0] * worldSize[1]), null);
		m.setPreferredSize(new Dimension(worldSize[0], worldSize[1]));
		JFrame j = new JFrame();
		JScrollPane s = new JScrollPane(m);
		s.setPreferredSize(new Dimension(worldSize[0], worldSize[1]));
		j.add(s);
		j.setVisible(true);
		j.setSize(700,600);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public void run(){
		movePlates();

		printFrame();
	}

}
