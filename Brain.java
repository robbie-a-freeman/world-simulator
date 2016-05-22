import java.awt.Dimension;
import java.security.SecureRandom;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.swing.JFrame;
import javax.swing.JScrollPane;


public class Brain {

	public static void main(String[] args)
	{

		Brain b = new Brain();
		b.generateStart();

	}
	
	private void generateStart()
	{
		Map m = new Map();
		World w = new World(m, 60, 500, 500);
		m.setPreferredSize(new Dimension(500,500));
		JFrame j = new JFrame();
		JScrollPane s = new JScrollPane(m);
		s.setPreferredSize(new Dimension(500,500));
		j.add(s);
		j.setVisible(true);
		j.setSize(600,600);
		j.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		//SecureRandom r = new SecureRandom();
		//for(int i = 0; i < 1000; i++){
		//	System.out.println(r.nextInt(3) - 1);
		//}
		
		ScheduledExecutorService e = Executors.newSingleThreadScheduledExecutor();
		
		e.scheduleAtFixedRate(w, 0, 1, TimeUnit.SECONDS);
		e.scheduleAtFixedRate(m, 0, 1, TimeUnit.SECONDS);
	}

}
