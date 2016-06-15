package edu.ding.map;

public class Gradient {
	
	public double centerX, centerY;

	public Gradient(double centerX, double centerY){
		this.centerX = centerX;
		this.centerY = centerY;
	}
	
	public double calcDistance(double x, double y){
		double strength;
		
		strength = Math.sqrt(Math.pow(centerX - x, 2) + Math.pow(centerY - y, 2));//TODO add actually dynamic strength formulas
		return strength;
	}
	
}
