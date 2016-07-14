package edu.ding.map;

public class HeightGradient extends Gradient {
	
	private double startingHeight, timeElapsed = 0;

	public HeightGradient(double centerX, double centerY, int worldX, int worldY, double startingHeight) {
		super(centerX, centerY, worldX, worldY);
		
		this.startingHeight = startingHeight;
	}
	
	public double calcNetStrength(double x, double y){
		double strength;
		double erosion;
		
		strength = startingHeight - 50 * Math.pow(calcDistance(x, y), 2.);
		if(strength <= 0){
			strength = 0;
			erosion = 0;
		}
		erosion = .2 * timeElapsed;
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

}
