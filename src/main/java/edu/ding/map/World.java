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
	public static final int[] WORLD_SIZE = new int[]{50, 50};

	private Atmosphere atmosphere;
	private List<TectonicPlate> tectonicPlates;
	private List<TectonicPlateBorder> borders;
	private List<ConvectionCurrent> convectionCurrents;
	private Mantle mantle;
	private Core core;
	private HeatGradient[] heatGradients;
	private boolean platesChanged = true, firstTime = true;

    private Location[] allLocations;
	private int landSubdivisions = 0;

	public World(){
		setupWorld();
		SecureRandom r = new SecureRandom(); //generate tectonic plates
		for(int x = 0; x < 100; x++){
			tectonicPlates.add(new TectonicPlate(r.nextInt(x)));
		}
		int x = r.nextInt(6) + 25; //generate mantle heat gradients
		heatGradients = new HeatGradient[x];
		for(int i = 0; i < x; i++){
			heatGradients[i] = new HeatGradient(r.nextInt(WORLD_SIZE[0]), r.nextInt(WORLD_SIZE[1]));
		}
		platesChanged = true;
	}

	public World(String templateName) {
		switch (templateName) {

			case "Dual Convergent":
				setupWorld();

				createTectonicPlates(2, 50, 50);

				heatGradients = new HeatGradient[2];
				heatGradients[0] = new HeatGradient(WORLD_SIZE[0] / 4 - 0.5, WORLD_SIZE[1] / 2);
				heatGradients[1] = new HeatGradient(3 * WORLD_SIZE[0] / 4 + 0.5, WORLD_SIZE[1] / 2);
				platesChanged = true;
				break;
			case "Dual Divergent":
				setupWorld();

				createTectonicPlates(2, 100, 100);

				heatGradients = new HeatGradient[2];
				heatGradients[0] = new HeatGradient(WORLD_SIZE[0] / 4 + 0.5, WORLD_SIZE[1] / 2);
				heatGradients[1] = new HeatGradient(3 * WORLD_SIZE[0] / 4 - 0.5, WORLD_SIZE[1] / 2);
				platesChanged = true;
				break;
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
		TectonicCellNetwork network = new TectonicCellNetwork(cellWidth, cellHeight);
		network.distributeCells(n); // distribute cells to n plates
		network.getPlates().forEach(p -> p.calculateCenter());

		allLocations = createLocationArray();
		network.assignParentPlates(allLocations);
		tectonicPlates = network.getPlates();
		System.out.println("plates list: " + tectonicPlates);

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

	public Location[] generateMap(Location[] locations){
		System.out.println("strt");
		int y = 0;
		for(int x = 0; x < locations.length; x++){

			double heat = 0.; //nearest neighbor interpolation with heat gradients
			for(int i = 0; i < heatGradients.length; i++){
				heat += heatGradients[i].calcNetStrength(x - (y - 1) * WORLD_SIZE[0], y);
			}

			TectonicPlate t = locations[x].getParentPlate();

			/*if(heat >= 150. && firstTime){
				t.getHeightGradients().add(new HeightGradient(x - (y - 1) * WORLD_SIZE[0], y, WORLD_SIZE[0], WORLD_SIZE[1], 1000));
			}*/
			double height = -2000.; //nearest neighbor interpolation with heat gradients
            //System.out.println("heights: " + t.getHeightGradients());
            //System.out.println("borders: " + t.getBorderGradients());
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

	private Location[] createLocationArray() {
	    Location[] locList = new Location[WORLD_SIZE[0] * WORLD_SIZE[1]];

        int y = 0;
        for(int x = 0; x < locList.length; x++) {
            if (x % WORLD_SIZE[0] == 0) {
                y++;
            }
            locList[x] = new Location(new BigDecimal(x - (y - 1) * WORLD_SIZE[0]), new BigDecimal(x - (y - 1) * WORLD_SIZE[0] + 1), new BigDecimal(y), new BigDecimal(y + 1), landSubdivisions);
        }

        return locList;
    }

	public void printFrame(){
		Map m = new Map();
		m.updateLocations(generateMap(allLocations), null);
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
		return generateMap(allLocations);
	}

}
