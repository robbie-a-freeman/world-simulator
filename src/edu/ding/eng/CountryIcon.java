package edu.ding.eng;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;


public class CountryIcon extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4985410102443599968L;
	private Location loc;
	private Graphics g;
	private Color c;
	
	public CountryIcon(Location loc, Color c)
	{
		this.loc = loc;
		this.c = c;
	}
	
	public void paintComponent(Graphics g)
	{
		this.g = g;
		g.setColor(c);
		System.out.println("Drawing");
		g.drawLine(loc.getX(), loc.getY(), loc.getX(), loc.getY());
	}
}
