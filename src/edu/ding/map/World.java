package edu.ding.map;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import edu.ding.eng.Location;
import edu.ding.eng.Map;
import edu.ding.eng.MapWindow;

public class World {
	
	// world constants
	public static final int[] WORLD_SIZE = new int[]{500, 500};

	private Atmosphere atmosphere;
	private List<TectonicPlate> tectonicPlates;
	private List<TectonicPlateBorder> borders;
	private List<ConvectionCurrent> convectionCurrents;
	private Mantle mantle;
	private Core core;
	private HeatGradient[] heatGradients;
	private boolean platesChanged = true, firstTime = true;

	private int landSubdivisions = 0;

	public World(){
		setupWorld();
		SecureRandom r = new SecureRandom(); //generate tectonic plates
		for(int x = 0; x < 100; x++){
			tectonicPlates.add(new TectonicPlate(r.nextInt(WORLD_SIZE[0]), r.nextInt(WORLD_SIZE[1]), WORLD_SIZE[0], WORLD_SIZE[1], x));
		}
		int x = r.nextInt(6) + 25; //generate mantle heat gradients
		heatGradients = new HeatGradient[x];
		for(int i = 0; i < x; i++){
			heatGradients[i] = new HeatGradient(r.nextInt(WORLD_SIZE[0]), r.nextInt(WORLD_SIZE[1]), WORLD_SIZE[0], WORLD_SIZE[1]);
		}
	}

	public World(String templateName) {
		switch (templateName) {

			case "Dual Convergent":
				setupWorld();

				createTectonicPlates(10, 100, 100);

				//tectonicPlates.add(new TectonicPlate(WORLD_SIZE[0] / 4, WORLD_SIZE[1] / 2, WORLD_SIZE[0], WORLD_SIZE[1], 0));
				//tectonicPlates.add(new TectonicPlate(3 * WORLD_SIZE[0] / 4, WORLD_SIZE[1] / 2, WORLD_SIZE[0], WORLD_SIZE[1], 1));

				heatGradients = new HeatGradient[2];
				heatGradients[0] = new HeatGradient(WORLD_SIZE[0] / 4 - 0.5, WORLD_SIZE[1] / 2, WORLD_SIZE[0], WORLD_SIZE[1]);
				heatGradients[1] = new HeatGradient(3 * WORLD_SIZE[0] / 4 + 0.5, WORLD_SIZE[1] / 2, WORLD_SIZE[0], WORLD_SIZE[1]);
				break;
			case "Dual Divergent":
				setupWorld();
				tectonicPlates.add(new TectonicPlate(WORLD_SIZE[0] / 4, WORLD_SIZE[1] / 2, WORLD_SIZE[0], WORLD_SIZE[1], 0));
				tectonicPlates.add(new TectonicPlate(3 * WORLD_SIZE[0] / 4, WORLD_SIZE[1] / 2, WORLD_SIZE[0], WORLD_SIZE[1], 1));

				heatGradients = new HeatGradient[2];
				heatGradients[0] = new HeatGradient(WORLD_SIZE[0] / 4 + 0.5, WORLD_SIZE[1] / 2, WORLD_SIZE[0], WORLD_SIZE[1]);
				heatGradients[1] = new HeatGradient(3 * WORLD_SIZE[0] / 4 - 0.5, WORLD_SIZE[1] / 2, WORLD_SIZE[0], WORLD_SIZE[1]);
			default:
				System.err.println("Unrecognized map template: " + templateName);
				System.exit(1);

		}
	}

	private void createTectonicPlates(int n, int resX, int resY) {

		if (n < 1) {
			throw new IllegalArgumentException();
		}

		// divide the world grid into n equal rectangular pieces
		BigDecimal cellWidth = new BigDecimal(WORLD_SIZE[0] / (double) resX);
		BigDecimal cellHeight = new BigDecimal(WORLD_SIZE[1] / (double) resY);
		TectonicCellNetwork network = new TectonicCellNetwork(WORLD_SIZE, cellWidth, cellHeight);
		network.distributeCells(n); // distribute cells to n plates
		network.getPlates().forEach(p -> p.calculateCenter());
		tectonicPlates = network.getPlates();

		// randomly jostle the points around to give a bit of randomness to it
		/* double randomnessFactor = 1; todo
		for (TectonicCell c : cells) {
			c.jostlePoints(randomnessFactor);
		} */

		// apply noise to the borders of the plates using https://www.redblobgames.com/maps/noisy-edges/ TODO
	}

	private void setupWorld() {
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
					tectonicPlates.get(h.getTectonicHost()).getHeightGradients().add(new HeightGradient(h.centerX, h.centerY, 1, 0)); //TODO change strength according to heat strength
				}
			}
			platesChanged = false;
		}
		for(HeatGradient h: heatGradients){
			tectonicPlates.get(h.getTectonicHost()).getHeightGradients().add(new HeightGradient(h.centerX, h.centerY, 1, 0));
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
			if(x % WORLD_SIZE[0] == 0){
				y++;
			}
			TectonicPlate t = null;
			double maxControl = 0.; //nearest neighbor interpolation with tectonic plate gradients
			for(int i = 0; i < tectonicPlates.size(); i++){
				if(tectonicPlates.get(i).getT().calcNetStrength(x - (y - 1) * WORLD_SIZE[0], y) > maxControl){
					maxControl = tectonicPlates.get(i).getT().calcNetStrength(x - (y - 1) * WORLD_SIZE[0], y);
					t = tectonicPlates.get(i);
				}
			}
			locations[x] = new Location(new BigDecimal(x - (y - 1) * WORLD_SIZE[0]), new BigDecimal(x - (y - 1) * WORLD_SIZE[0] + 1), new BigDecimal(y), new BigDecimal(y+1), landSubdivisions);
			locations[x].setColor(t.getColor());

			double heat = 0.; //nearest neighbor interpolation with heat gradients
			for(int i = 0; i < heatGradients.length; i++){
				heat += heatGradients[i].calcNetStrength(x - (y - 1) * WORLD_SIZE[0], y);
			}
			/*if(heat >= 150. && firstTime){
				t.getHeightGradients().add(new HeightGradient(x - (y - 1) * WORLD_SIZE[0], y, WORLD_SIZE[0], WORLD_SIZE[1], 1000));
			}*/
			double height = -2000.; //nearest neighbor interpolation with heat gradients
			for(int i = 0; i < t.getHeightGradients().size(); i++){
				height += t.getHeightGradients().get(i).calcNetStrength(x - (y - 1) * WORLD_SIZE[0], y);
			}
			for(int i = 0; i < t.getBorderGradients().size(); i++){
				height += t.getBorderGradients().get(i).calcNetStrength(x - (y - 1) * WORLD_SIZE[0], y);
			}
			if(height >= 0.){
				locations[x].setLand(true);
				locations[x].setElevation(height);
			}
			locations[x].setMantleTemperature(heat);
			
			double temp = 0; //in kelvins
			temp += WORLD_SIZE[1] - Math.abs(y - WORLD_SIZE[1] / 2) / 2;
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
		m.updateLocations(generateMap(WORLD_SIZE[0] * WORLD_SIZE[1]), null);
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
		return generateMap(WORLD_SIZE[0] * WORLD_SIZE[1]);
	}

}
