package edu.ding.map;

import java.awt.Color;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class TectonicPlate {

	private double controlPeak = 100., mass = 10000., centerX, centerY, worldX, worldY;
	private Color color;
	private TectonicPlateControlGradient t;
	private ConvectionCurrent convectionCurrent;
	private List<HeightGradient> heightGradients = new ArrayList<HeightGradient>();
	private List<TectonicPlate> tectonicPlates;
	private List<Border> borders = new ArrayList<Border>();
	private int ID;

	public TectonicPlate(double centerX, double centerY, int worldX, int worldY, int ID){
		this.setID(ID);
		this.setCenterX(centerX);
		this.setCenterY(centerY);
		SecureRandom r = new SecureRandom();
		setColor(new Color(r.nextInt(256), r.nextInt(256), r.nextInt(256))); //give temporary color to see plates
		setT(new TectonicPlateControlGradient(centerX, centerY, controlPeak, worldX, worldY));
		this.worldX = (double) worldX;
		this.worldY = (double) worldY;
	}

	public void calcGradient(){
		//TODO put in ability to adjust gradient
	}

	public void updateBorders(List<TectonicPlate> plates){
		tectonicPlates = plates;

		for(TectonicPlate t: tectonicPlates){ //see if there's a bordering plate
			if(t.getID() != this.ID){
				for(double i = 0; i < worldX; i++){
					for(double z = 0; z < worldY; z++){
						double num1 = this.getT().calcNetStrength(i, z);
						double num2 = t.getT().calcNetStrength(i, z);
						if(num1/num2 > .999 && num1/num2 < 1.001 && num1 + num2 > 4){
							boolean isDuplicate = false;
							for(Border b: borders){
								if(b.getSecondPlateID() == t.getID()){
									isDuplicate = true;
								}
							}
							if(!isDuplicate){
								borders.add(new Border(this.ID, t.getID(), true)); //TODO fix boolean
								borders.get(borders.size() - 1).addCoords(i, z);
							}
						}
					}
				}
			}
		}
		System.out.println(borders.size());
	}

	public void move(){ //moves the stuff on the plate, not the plate itself, about 1 year's worth of movement
		double movement = convectionCurrent.movePlate(mass);

		for(HeightGradient h : heightGradients){
			double x = movement * Math.cos(convectionCurrent.getDirection());
			double y = movement * Math.sin(convectionCurrent.getDirection());

			for(Border b: borders){
				double num1 = this.getT().calcNetStrength(h.getCenterX() + x, h.getCenterY() + y);
				double num2 = tectonicPlates.get(b.getSecondPlateID()).getT().calcNetStrength(h.getCenterX() + x, h.getCenterY() + y);
				if(num2 > num1){
					x = 0;
					y = 0;
					break;
				}
			}
			
			if(h.getCenterY() + y > worldY){
				//x += worldX / 2;
				y -= worldY;
			}
			else if(h.getCenterY() + y < 0){ //TODO FIX
				y += worldY;
			}

			if(h.getCenterX() + x > worldX){
				x -= worldX;
			}
			else if(h.getCenterX() + x < 0){
				x += worldX;
			}
			h.setCenterX(h.getCenterX() + x);

			h.setCenterY(h.getCenterY() + y);
		}
		generateBorderLand();
	}

	public void generateHeatVector(HeatGradient[] heatGradients) {
		double vectorX = 0, vectorY = 0;
		for(HeatGradient h: heatGradients){
			double angle;
			if(centerX < h.getCenterX()){
				angle = 2 * Math.PI - Math.atan2(centerY - h.getCenterY(), centerX - h.getCenterX());
			} else{
				angle = Math.atan2(centerY - h.getCenterY(), centerX - h.getCenterX());
			}
			double strength = h.calcNetStrength(centerX, centerY);

			vectorX += strength * Math.cos(angle);
			vectorY += strength * Math.sin(angle);
		}
		double finalAngle;
		if(vectorX < 0){
			finalAngle = Math.PI - Math.atan2(vectorY, vectorX);
		} else{
			finalAngle = Math.atan2(vectorY, vectorX);
		}

		convectionCurrent = new ConvectionCurrent(Math.sqrt(Math.pow(vectorX, 2.) + Math.pow(vectorY, 2.)), finalAngle);

	}
	
	private void generateBorderLand(){ //TODO WIP
		for(Border b: borders){
			if(b.isConverging()){
				double x = 0;
				for(Double i: b.getxCoords()){
					x += i;
				}
				x /= b.getxCoords().size(); //average x coordinate
				
				double y = 0;
				for(Double i: b.getyCoords()){
					y += i;
				}
				y /= b.getxCoords().size(); //average x coordinate
				
				heightGradients.add(new HeightGradient(x, y, (int) worldX, (int) worldY, 10.));
			}
		}
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

	public int getID() {
		return ID;
	}

	public void setID(int iD) {
		ID = iD;
	}

	public List<HeightGradient> getHeightGradients() {
		return heightGradients;
	}

	public void setHeightGradients(List<HeightGradient> heightGradients) {
		this.heightGradients = heightGradients;
	}

	public List<TectonicPlate> getTectonicPlates() {
		return tectonicPlates;
	}

	public void setTectonicPlates(List<TectonicPlate> tectonicPlates) {
		this.tectonicPlates = tectonicPlates;
	}
}
