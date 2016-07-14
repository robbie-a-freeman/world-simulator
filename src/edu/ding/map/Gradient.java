package edu.ding.map;

public class Gradient {
	
	public double centerX, centerY, worldX, worldY;

	public Gradient(double centerX, double centerY, int worldX, int worldY){
		this.centerX = centerX;
		this.centerY = centerY;
		this.worldX = (double) worldX;
		this.worldY = (double) worldY;
	}
	
	public double calcDistance(double x, double y){
		double distance;
		
		double distanceX;
		if(Math.abs(centerX - x) > worldX / 2){
			distanceX = worldX - (centerX - x);
		}
		else{
			distanceX = centerX - x;
		}
		distance = Math.sqrt(Math.pow(distanceX, 2.) + Math.pow(centerY - y, 2.));//TODO add actually dynamic strength formulas
		return distance;
	}

	public double getCenterX() {
		return centerX;
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public double getCenterY() {
		return centerY;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}
	
}
