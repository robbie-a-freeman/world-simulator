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

	private Atmosphere atmosphere;
	private List<TectonicPlate> tectonicPlates;
	private List<Border> borders;
	private int[] worldSize;
	private List<ConvectionCurrent> convectionCurrents;
	private Mantle mantle;
	private Core core;
	private HeatGradient[] heatGradients;
	private boolean platesChanged = true, firstTime = true;

	public World(int sizeX, int sizeY){
		worldSize = new int[2];
		worldSize[0] = sizeX;
		worldSize[1] = sizeY;

		tectonicPlates = new ArrayList<TectonicPlate>();

		SecureRandom r = new SecureRandom(); //generate tectonic plates
		for(int x = 0; x < 100; x++){
			tectonicPlates.add(new TectonicPlate((double) r.nextInt(worldSize[0]), (double) r.nextInt(worldSize[1]), worldSize[0], worldSize[1], x));
		}

		int x = r.nextInt(6) + 5; //generate mantle heat gradients
		heatGradients = new HeatGradient[x];
		for(int i = 0; i < x; i++){
			heatGradients[i] = new HeatGradient((double) r.nextInt(worldSize[0]), (double) r.nextInt(worldSize[1]), worldSize[0], worldSize[1]);
		}

		core = new Core();
		mantle = new Mantle();

		run();

	}

	private void movePlates(){
		if(platesChanged){
			for(int x = 0; x < tectonicPlates.size(); x++){
				tectonicPlates.get(x).generateHeatVector(heatGradients);
				tectonicPlates.get(x).updateBorders(tectonicPlates);
				for(HeatGradient h: heatGradients){
					double maxControl = 0.; //nearest neighbor interpolation with tectonic plate gradients
					for(int i = 0; i < tectonicPlates.size(); i++){
						if(tectonicPlates.get(i).getT().calcNetStrength(h.centerX, h.centerY) > maxControl){
							maxControl = tectonicPlates.get(i).getT().calcNetStrength(h.centerX, h.centerY);
							h.setTectonicHost(i);
						}
					}
					tectonicPlates.get(h.getTectonicHost()).getHeightGradients().add(new HeightGradient(h.centerX, h.centerY, worldSize[0], worldSize[1], 10)); //TODO change strength according to heat strength
				}
			}
			platesChanged = false;
		}
		for(HeatGradient h: heatGradients){
			tectonicPlates.get(h.getTectonicHost()).getHeightGradients().add(new HeightGradient(h.centerX, h.centerY, worldSize[0], worldSize[1], 10));
		}
		for(int x = 0; x < tectonicPlates.size(); x++){
			tectonicPlates.get(x).move();
		}
		double heat = 0.; //create height gradients from the heat
		for(HeatGradient h: heatGradients){
			//TODO match the heightGradients with the appropriate tectonicPlates
			
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
			double maxControl = 0.; //nearest neighbor interpolation with tectonic plate gradients
			for(int i = 0; i < tectonicPlates.size(); i++){
				if(tectonicPlates.get(i).getT().calcNetStrength(x - (y - 1) * worldSize[0], y) > maxControl){
					maxControl = tectonicPlates.get(i).getT().calcNetStrength(x - (y - 1) * worldSize[0], y);
					t = tectonicPlates.get(i);
				}
			}
			locations[x] = new Location(x - (y - 1) * worldSize[0], y);
			locations[x].setColor(t.getColor());

			double heat = 0.; //nearest neighbor interpolation with heat gradients
			for(int i = 0; i < heatGradients.length; i++){
				heat += heatGradients[i].calcNetStrength(x - (y - 1) * worldSize[0], y);
			}
			/*if(heat >= 150. && firstTime){
				t.getHeightGradients().add(new HeightGradient(x - (y - 1) * worldSize[0], y, worldSize[0], worldSize[1], 1000));
			}*/
			locations[x] = new Location(x - (y - 1) * worldSize[0], y);
			locations[x].setColor(t.getColor());
			double height = -1000.; //nearest neighbor interpolation with heat gradients
			for(int i = 0; i < t.getHeightGradients().size(); i++){
				height += t.getHeightGradients().get(i).calcNetStrength(x - (y - 1) * worldSize[0], y);
			}
			if(height > -999){
				System.out.println(height);				
			}
			if(height >= 0.){
				locations[x].setLand(true);
				locations[x].setElevation(height);
			}
			locations[x].setMantleTemperature(heat);
		}
		firstTime = false;
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
		for(int i = 0; i < 10000; i++){
			movePlates();
			if((i + 1) % 2000 == 0){
				printFrame();			
			}
		}


	}

}
