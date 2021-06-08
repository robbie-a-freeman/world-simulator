package edu.ding.map;

public class HeatGradient extends Gradient {
	
	private int tectonicHost = -1; //if it's -1, that is an error

	public HeatGradient(double centerX, double centerY) {
		super(centerX, centerY);
		
	}
	
	public double calcNetStrength(double x, double y){
		double strength;
		
		strength = 200 / calcDistance(x, y);//TODO add actually dynamic strength formulas
		if(strength > 200){
			strength = 200;
		}
		return strength;
	}

	public int getTectonicHost() {
		return tectonicHost;
	}

	public void setTectonicHost(int tectonicHost) {
		this.tectonicHost = tectonicHost;
	}

}
