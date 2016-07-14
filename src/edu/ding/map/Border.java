package edu.ding.map;

import java.awt.List;
import java.util.ArrayList;

public class Border {
	
	private int firstPlateID, secondPlateID;
	private ArrayList<Double> xCoords;
	private ArrayList<Double> yCoords;
	private boolean isConverging;

	public Border(int firstPlateID, int secondPlateID, boolean isConverging){
		
		this.firstPlateID = firstPlateID;
		this.setSecondPlateID(secondPlateID);
		this.setConverging(isConverging);
		xCoords = new ArrayList<Double>();
		yCoords = new ArrayList<Double>();
	}
	
	public void addCoords(double x, double y){
		xCoords.add(x);
		yCoords.add(y);
	}

	public int getSecondPlateID() {
		return secondPlateID;
	}

	public void setSecondPlateID(int secondPlateID) {
		this.secondPlateID = secondPlateID;
	}

	public boolean isConverging() {
		return isConverging;
	}

	public void setConverging(boolean isConverging) {
		this.isConverging = isConverging;
	}

	public ArrayList<Double> getxCoords() {
		return xCoords;
	}

	public void setxCoords(ArrayList<Double> xCoords) {
		this.xCoords = xCoords;
	}

	public ArrayList<Double> getyCoords() {
		return yCoords;
	}

	public void setyCoords(ArrayList<Double> yCoords) {
		this.yCoords = yCoords;
	}
	
}
