package edu.ding.map;

public abstract class InnerEarth {
	
	public double specificHeat, mass, temperature;
	public int timeElapsed;

	public abstract void calcChanges();
	
	public abstract void publish();
	
}
