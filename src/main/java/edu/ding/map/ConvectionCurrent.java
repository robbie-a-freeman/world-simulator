package edu.ding.map;

public class ConvectionCurrent {
	
	private double force;
	private double direction; //in pi radians
	
	public ConvectionCurrent(double strength, double direction){
		setForce(strength);
		setDirection(direction);
	}
	
	public double movePlate(double mass){
		double movement = force / mass;
		return movement;
	}

	public double getForce() {
		return force;
	}

	public void setForce(double force) {
		this.force = force;
	}

	public double getDirection() {
		return direction;
	}

	public void setDirection(double direction) {
		this.direction = direction;
	}
	
}
