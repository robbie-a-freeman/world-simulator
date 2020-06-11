package edu.ding.map;

import java.awt.Dimension;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import edu.ding.eng.Location;
import edu.ding.eng.Map;
import edu.ding.eng.MapWindow;

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

	private int landSubdivisions = 0;

	public World(int sizeX, int sizeY){
		setupWorld(sizeX, sizeY);
		SecureRandom r = new SecureRandom(); //generate tectonic plates
		for(int x = 0; x < 100; x++){
			tectonicPlates.add(new TectonicPlate((double) r.nextInt(worldSize[0]), (double) r.nextInt(worldSize[1]), worldSize[0], worldSize[1], x));
		}
		int x = r.nextInt(6) + 25; //generate mantle heat gradients
		heatGradients = new HeatGradient[x];
		for(int i = 0; i < x; i++){
			heatGradients[i] = new HeatGradient((double) r.nextInt(worldSize[0]), (double) r.nextInt(worldSize[1]), worldSize[0], worldSize[1]);
		}
	}

	public World(int sizeX, int sizeY, String templateName) {
		switch (templateName) {

			case "Dual Convergent":
				setupWorld(sizeX, sizeY);
				tectonicPlates.add(new TectonicPlate(sizeX / 4, sizeY / 2, sizeX, sizeY, 0));
				tectonicPlates.add(new TectonicPlate(3 * sizeX / 4, sizeY / 2, sizeX, sizeY, 1));

				heatGradients = new HeatGradient[2];
				heatGradients[0] = new HeatGradient(sizeX / 4 - 0.5, sizeY / 2, sizeX, sizeY);
				heatGradients[1] = new HeatGradient(3 * sizeX / 4 + 0.5, sizeY / 2, sizeX, sizeY);
				break;
			case "Dual Divergent":
				setupWorld(sizeX, sizeY);
				tectonicPlates.add(new TectonicPlate(sizeX / 4, sizeY / 2, sizeX, sizeY, 0));
				tectonicPlates.add(new TectonicPlate(3 * sizeX / 4, sizeY / 2, sizeX, sizeY, 1));

				heatGradients = new HeatGradient[2];
				heatGradients[0] = new HeatGradient(sizeX / 4 + 0.5, sizeY / 2, sizeX, sizeY);
				heatGradients[1] = new HeatGradient(3 * sizeX / 4 - 0.5, sizeY / 2, sizeX, sizeY);
			default:
				System.err.println("Unrecognized map template: " + templateName);
				System.exit(1);

		}
	}

	private void setupWorld(int sizeX, int sizeY) {
		worldSize = new int[2];
		worldSize[0] = sizeX;
		worldSize[1] = sizeY;

		tectonicPlates = new ArrayList<TectonicPlate>();

		core = new Core();
		mantle = new Mantle();
	}

	private void movePlates(){
		if(platesChanged){
			System.out.println("moving plates");
			for(int x = 0; x < tectonicPlates.size(); x++){
				tectonicPlates.get(x).generateHeatVector(heatGradients);
			}
			for(int x = 0; x < tectonicPlates.size(); x++){
				tectonicPlates.get(x).updateBorders(tectonicPlates);
			}
			for(int x = 0; x < tectonicPlates.size(); x++){
				for(HeatGradient h: heatGradients){
					double maxControl = 0.; //nearest neighbor interpolation with tectonic plate gradients
					for(int i = 0; i < tectonicPlates.size(); i++){
						if(tectonicPlates.get(i).getT().calcNetStrength(h.centerX, h.centerY) > maxControl){
							maxControl = tectonicPlates.get(i).getT().calcNetStrength(h.centerX, h.centerY);
							h.setTectonicHost(i);
						}
					}
					tectonicPlates.get(h.getTectonicHost()).getHeightGradients().add(new HeightGradient(h.centerX, h.centerY, worldSize[0], worldSize[1], 1, 0)); //TODO change strength according to heat strength
				}
			}
			platesChanged = false;
		}
		for(HeatGradient h: heatGradients){
			tectonicPlates.get(h.getTectonicHost()).getHeightGradients().add(new HeightGradient(h.centerX, h.centerY, worldSize[0], worldSize[1], 1, 0));
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
		System.out.println("strt");
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
			locations[x] = new Location(new BigDecimal(x - (y - 1) * worldSize[0]), new BigDecimal(x - (y - 1) * worldSize[0] + 1), new BigDecimal(y), new BigDecimal(y+1), landSubdivisions);
			locations[x].setColor(t.getColor());

			double heat = 0.; //nearest neighbor interpolation with heat gradients
			for(int i = 0; i < heatGradients.length; i++){
				heat += heatGradients[i].calcNetStrength(x - (y - 1) * worldSize[0], y);
			}
			/*if(heat >= 150. && firstTime){
				t.getHeightGradients().add(new HeightGradient(x - (y - 1) * worldSize[0], y, worldSize[0], worldSize[1], 1000));
			}*/
			double height = -2000.; //nearest neighbor interpolation with heat gradients
			for(int i = 0; i < t.getHeightGradients().size(); i++){
				height += t.getHeightGradients().get(i).calcNetStrength(x - (y - 1) * worldSize[0], y);
			}
			for(int i = 0; i < t.getBorderGradients().size(); i++){
				height += t.getBorderGradients().get(i).calcNetStrength(x - (y - 1) * worldSize[0], y);
			}
			if(height >= 0.){
				locations[x].setLand(true);
				locations[x].setElevation(height);
			}
			locations[x].setMantleTemperature(heat);
			
			double temp = 0; //in kelvins
			temp += worldSize[1] - Math.abs(y - worldSize[1] / 2) / 2;
			if(!locations[x].isLand()){
				double oceanTemp = Math.sqrt(2 * Math.abs(temp - 300.));
				if(temp < 300.){
					temp += oceanTemp;
				} else{
					temp -= oceanTemp;
				}
			}
			else{
				double landTemp = Math.sqrt(5 * Math.abs(temp - 400.));
				temp += landTemp;
			}
			locations[x].setTemperature(temp);
		}
		firstTime = false;
		System.out.println("end");
		return locations;
	}

	public void printFrame(){
		Map m = new Map();
		m.updateLocations(generateMap(worldSize[0] * worldSize[1]), null);
		m.updateTectonicPlates(tectonicPlates);
		MapWindow j = new MapWindow(m);
	}

	public void run(){
		for(int i = 0; i < 5000; i++){
			movePlates();
			if((i + 1) % 1250 == 0){
				System.out.println(i + 1);
				printFrame();
			}
		}
	}
	
	public Location[] getLocations(){
		run();
		return generateMap(worldSize[0] * worldSize[1]);
	}

}
