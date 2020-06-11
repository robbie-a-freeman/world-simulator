package edu.ding.eng;

import java.awt.Dimension;
import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import edu.ding.map.World;


public class Brain {

	public static void main(String[] args)
	{
		Brain b = new Brain();
		b.generateStart();
	}
	
	private void generateStart()
	{
		Map m = new Map();
		edu.ding.eng.World w = new edu.ding.eng.World(m, 10, 500, 500);
		w.importWorld(new edu.ding.map.World(500, 500, "Dual Convergent").getLocations());
		w.build();
		MapWindow j = new MapWindow(m);
		
		ScheduledExecutorService e = Executors.newSingleThreadScheduledExecutor();
		
		e.scheduleAtFixedRate(w, 0, 1, TimeUnit.SECONDS);
		e.scheduleAtFixedRate(m, 0, 1, TimeUnit.SECONDS);
	}

}
