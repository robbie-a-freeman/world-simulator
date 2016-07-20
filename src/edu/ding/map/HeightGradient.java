package edu.ding.map;

public class HeightGradient extends Gradient {
	
	private double startingHeight, timeElapsed = 1., strengthDecay;
	private int type; //0 for hotspot, 1 for border activity

	public HeightGradient(double centerX, double centerY, int worldX, int worldY, double startingHeight, int type) {
		super(centerX, centerY, worldX, worldY);
		
		this.startingHeight = startingHeight;
		this.type = type;
		
	}
	
	public double calcNetStrength(double x, double y){
		double strength;
		double erosion;
		
		strengthDecay = 8 * Math.pow(calcDistance(x, y), 2.);
		
		if(type == 1){
			strengthDecay -= timeElapsed * calcDistance(x, y) / 10000;
		}
		
		strength = startingHeight - strengthDecay;
		if(strength <= 0){
			strength = 0;
			erosion = 0;
		}
		erosion = 0;
		strength -= erosion;
		
		return strength;
	}
	
	public void updateTime(){
		timeElapsed++;
	}

	public double getStartingHeight() {
		return startingHeight;
	}

	public void setStartingHeight(double startingHeight) {
		this.startingHeight = startingHeight;
	}

	public double getStrengthDecay() {
		return strengthDecay;
	}

	public void setStrengthDecay(double strengthDecay) {
		this.strengthDecay = strengthDecay;
	}

}
