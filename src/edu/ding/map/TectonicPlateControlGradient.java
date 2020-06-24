package edu.ding.map;

public class TectonicPlateControlGradient extends Gradient {

	public TectonicPlateControlGradient(TectonicPlate p, double controlPeak) {
		super(p);
		
	}
	
	public double calcNetStrength(double x, double y){
		double strength;
		
		strength = 100 / calcDistance(x, y);//TODO add actually dynamic strength formulas
		return strength;
	}

}
