package edu.ding.map;

import java.awt.Color;
import java.security.SecureRandom;

public class TectonicPlate {
	
	private double controlPeak = 100;
	private double centerX, centerY;
	private Color color;
	private TectonicPlateControlGradient t;
	
	public TectonicPlate(double centerX, double centerY){
		this.setCenterX(centerX);
		this.setCenterY(centerY);
		SecureRandom r = new SecureRandom();
		setColor(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256))); //give temporary color to see plates
		setT(new TectonicPlateControlGradient(centerX, centerY, controlPeak));
	}
	
	public void calcGradient(){
		//TODO put in ability to adjust gradient
	}
	
	public void move(){
		//TODO actually do this
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public TectonicPlateControlGradient getT() {
		return t;
	}

	public void setT(TectonicPlateControlGradient t) {
		this.t = t;
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
