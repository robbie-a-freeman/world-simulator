package edu.ding.map;

public class HeatGradient extends Gradient {

	public HeatGradient(double centerX, double centerY, int worldX, int worldY) {
		super(centerX, centerY, worldX, worldY);
		
	}
	
	public double calcNetStrength(double x, double y){
		double strength;
		
		strength = 200 / calcDistance(x, y);//TODO add actually dynamic strength formulas
		if(strength > 200){
			strength = 200;
		}
		return strength;
	}

}
