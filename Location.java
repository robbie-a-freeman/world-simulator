package edu.ding.eng;

import java.awt.Color;


public class Location {

	private double desert, forest, jungle, hills, plains, mountains, temperature;
	private boolean occupied = false;
	private int x, y, ownerID;
	private Color color;

	public Location(int x, int y){
		this.setX(x);
		this.setY(y);
		ownerID = -1;
	}
	public Location(int x, int y, Color color){
		this.setX(x);
		this.setY(y);
		this.color = color;
		ownerID = -1;
	}

	public Color getColor() {
		return color;
	}
	public void setColor(Color color) {
		this.color = color;
	}
	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	public int getOwnerID() {
		return ownerID;
	}
	public void setOwnerID(int ownerID) {
		this.ownerID = ownerID;
	}
	public boolean isOccupied() {
		return occupied;
	}
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

}
