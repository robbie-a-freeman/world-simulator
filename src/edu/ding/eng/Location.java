package edu.ding.eng;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.Hashtable;


public class Location {

	private double desert, forest, jungle, hills, plains, mountains, temperature, mantleTemperature, elevation;
	private boolean isLand, occupied = false;
	private int ownerID;
	private BigDecimal[] xRange;
	private BigDecimal[] yRange;
	private Color color;

	public static final int NUMBER_OF_SUBLOCATIONS_PER_SIDE = 8;

	// keys are pairs of x, y coordinates represented as BigDecimals (the top left coordinates)
	private Hashtable<BigDecimal[], Location> sublocations;

	public Location(BigDecimal minX, BigDecimal maxX, BigDecimal minY, BigDecimal maxY, int subdivisions){
		this.setxRange(new BigDecimal[]{minX, maxX});
		this.setyRange(new BigDecimal[]{minY, maxY});
		ownerID = -1;
		sublocations = new Hashtable<>();
		createSubLocations(subdivisions - 1);
	}
	public Location(BigDecimal minX, BigDecimal maxX, BigDecimal minY, BigDecimal maxY, int subdivisions, Color color){
		this.setxRange(new BigDecimal[]{minX, maxX});
		this.setyRange(new BigDecimal[]{minY, maxY});
		this.color = color;
		ownerID = -1;
		createSubLocations(subdivisions);
	}

	// creates Location objects that are within this Location square. Makes 64 (8x8) subLocations
	private void createSubLocations(int subdivisions) {
		if (subdivisions <= 0) {
			return;
		}

		BigDecimal lengthX = xRange[1].subtract(xRange[0]).divide(new BigDecimal(NUMBER_OF_SUBLOCATIONS_PER_SIDE));
		BigDecimal lengthY = yRange[1].subtract(yRange[0]).divide(new BigDecimal(NUMBER_OF_SUBLOCATIONS_PER_SIDE));
		BigDecimal minX = xRange[0];
		BigDecimal minY = yRange[0];
		for (int i = 0; i < NUMBER_OF_SUBLOCATIONS_PER_SIDE; i++) {
			for (int j = 0; j < NUMBER_OF_SUBLOCATIONS_PER_SIDE; j++) {
				BigDecimal maxX = minX.add(lengthX);
				BigDecimal maxY = minY.add(lengthY);
				sublocations.put(new BigDecimal[]{minX, minY}, new Location(minX, maxX, minY, maxY, subdivisions));
			}
		}

	}

	// get the avg x and y of this location
	public BigDecimal getX() {
		return (xRange[0].add(xRange[1])).divide(new BigDecimal(2));
	}
	public BigDecimal getY() {
		return (yRange[0].add(yRange[1])).divide(new BigDecimal(2));
	}

	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}

	public BigDecimal[] getxRange() {
		return xRange;
	}

	public void setxRange(BigDecimal[] xRange) {
		this.xRange = xRange;
	}

	public BigDecimal[] getyRange() {
		return yRange;
	}

	public void setyRange(BigDecimal[] yRange) {
		this.yRange = yRange;
	}

	public int getOwnerID() {
		return ownerID;
	}
	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}
	public boolean isOccupied() {
		return occupied;
	}
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}
	public boolean isLand() {
		return isLand;
	}
	public void setLand(boolean isLand) {
		this.isLand = isLand;
	}
	public double getMantleTemperature() {
		return mantleTemperature;
	}
	public void setMantleTemperature(double mantleTemperature) {
		this.mantleTemperature = mantleTemperature;
	}
	public double getElevation() {
		return elevation;
	}
	public void setElevation(double elevation) {
		this.elevation = elevation;
	}
	public double getTemperature() {
		return temperature;
	}
	public void setTemperature(double temperature) {
		this.temperature = temperature;
	}

}
