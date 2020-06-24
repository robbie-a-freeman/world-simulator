package edu.ding.map;

public class Gradient {
	
	public double centerX, centerY; // todo make private
	private HasStrengthGradient obj;

	public Gradient(double centerX, double centerY){
		this.centerX = centerX;
		this.centerY = centerY;
	}

	public Gradient (HasStrengthGradient obj) {
		this.obj = obj;
	}
	
	public double calcDistance(double x, double y){
		double distance;
		
		double distanceX;
		if(Math.abs(centerX - x) > World.WORLD_SIZE[0] / 2){
			distanceX = World.WORLD_SIZE[0] - (centerX - x);
		}
		else{
			distanceX = centerX - x;
		}
		distance = Math.sqrt(Math.pow(distanceX, 2.) + Math.pow(centerY - y, 2.));//TODO add actually dynamic strength formulas
		return distance;
	}

	public double getCenterX() {
		if (obj != null)
			return obj.getCenter()[0];

		return centerX;
	}

	public void setCenterX(double centerX) {
		this.centerX = centerX;
	}

	public double getCenterY() {
		if (obj != null)
			return obj.getCenter()[1];

		return centerY;
	}

	public void setCenterY(double centerY) {
		this.centerY = centerY;
	}
	
}
