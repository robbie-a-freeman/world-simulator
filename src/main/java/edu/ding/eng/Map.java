package edu.ding.eng;

import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.MouseInputListener;

import edu.ding.map.TectonicPlate;


public class Map extends JPanel implements Runnable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3967835280265684766L;
	private int mapX = 50;
	private int mapY = 50;
	private Location locations[] = new Location[mapX * mapY];
	private Location countryLocations[][] = null;
	private int[] landTilesX = new int[mapX * mapY];
	private int[] landTilesY = new int[mapX * mapY];
	private List<TectonicPlate> tectonicPlates = new ArrayList<TectonicPlate>();
	private int mapMode = 0; //0 is normal, 1 is tectonic, 2 is heat, 3 is height, 4 is surface temp
	private int zoomLevel = 1;
	private Graphics g;

	private int minXView = 0;
	private int maxXView = 50;
	private int minYView = 0;
	private int maxYView = 50;


	public Map()
	{
		super();
		this.setPreferredSize(new Dimension(500,500));
		setLayout(null);

		// listener for zooming in
		this.addMouseWheelListener(new MouseWheelListener() {
			@Override
			public void mouseWheelMoved(MouseWheelEvent e) {
				int mouseWheelChange = e.getWheelRotation();
				// negative = zoom in, positive = zoom out
				if (mouseWheelChange < 0) {
					zoomIn(e.getX(), e.getY());
					//System.out.println("Zooming in");
					repaint();
				} else {
					zoomOut(e.getX(), e.getY());
					//System.out.println("Zooming out");
					repaint();
				}
			}
		});

		MouseInputListener m = new MouseInputListener() {
			boolean isBeingDragged = false;
			int dragPointX = -1;
			int dragPointY = -1;

			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {
				//System.out.println("mouse press");
				isBeingDragged = true;
				dragPointX = e.getX();
				dragPointY = e.getY();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				//System.out.println("mouse release");
				isBeingDragged = false;
				dragPointX = -1;
				dragPointY = -1;
			}

			@Override
			public void mouseEntered(MouseEvent e) {

			}

			@Override
			public void mouseExited(MouseEvent e) {

			}

			@Override
			public void mouseDragged(MouseEvent e) {
				//probably for info popups in the future
				if (isBeingDragged) {
					translateWindow(dragPointX, dragPointY, e.getX(), e.getY());
					dragPointX = e.getX();
					dragPointY = e.getY();
					repaint();
					revalidate();
				}
			}

			@Override
			public void mouseMoved(MouseEvent e) {

			}
		};

		this.addMouseMotionListener(m);
		this.addMouseListener(m);


		this.setVisible(true);
	}

	// zooms in, with a focus towards centering on x, y
	private void zoomIn(int x, int y) {

	    if (x < 0) {
	        x = 0;
        } else if (x > mapX) {
	        x = mapX;
        }
        if (y < 0) {
            y = 0;
        } else if (y > mapY) {
            y = mapY;
        }

        // todo
		int centerX = minXView + (maxXView - minXView) * x / mapX;
		int centerY = minYView + (maxYView - minYView) * y / mapY;

		int halfXView = (int) ((maxXView - minXView) / 2 / 1.5);
		int halfYView = (int) ((maxYView - minYView) / 2 / 1.5);

		if (centerX - halfXView < 0) {
			maxXView = halfXView * 2;
			minXView = 0;
		} else if (centerX + halfXView >= mapX) {
			minXView = mapX - halfXView * 2;
			maxXView = mapX;
		} else {
			maxXView = centerX + halfXView;
			minXView = centerX - halfXView;
		}

		if (centerY - halfYView < 0) {
			maxYView = halfYView * 2;
			minYView = 0;
		}  else if (centerY + halfYView >= mapY) {
			minYView = mapY - halfYView * 2;
			maxYView = mapY;
		} else {
			maxYView = centerY + halfYView;
			minYView = centerY - halfYView;
		}

		// todo
		if (minXView < 0) {
		    minXView = 0;
        }
        if (maxXView > mapX - 1) {
		    maxXView = mapX - 1;
        }
        if (minYView < 0) {
            minYView = 0;
        }
        if (maxYView > mapY - 1) {
            maxYView = mapY - 1;
        }

		// calculate the zoomLevel
		int widthTilesVisible = (int) Math.pow(2, zoomLevel - 1) * (maxXView - minXView);
		if (widthTilesVisible < mapX) {
			zoomLevel++;
			System.out.println("zoom lvl: " + zoomLevel);
		}

	}

	// zooms out, with a focus from x, y
	private void zoomOut(int x, int y) { //todo wont zoom out if too zoomed in

        if (x < 0) {
            x = 0;
        } else if (x > mapX) {
            x = mapX;
        }
        if (y < 0) {
            y = 0;
        } else if (y > mapY) {
            y = mapY;
        }

        // todo modularize screen coords
        int centerX = minXView + (maxXView - minXView) * x / mapX;
        int centerY = minYView + (maxYView - minYView) * y / mapY;

        int halfXView = (int) Math.ceil(((maxXView - minXView) / 2 * 1.5));
        int halfYView = (int) Math.ceil(((maxYView - minYView) / 2 * 1.5));

        if (halfXView <= 1)
        	halfXView = 2;
		if (halfYView <= 1)
			halfYView = 2;

        if (centerX - halfXView < 0) {
            maxXView = halfXView * 2;
            minXView = 0;
        } else if (centerX + halfXView >= mapX) {
            minXView = mapX - halfXView * 2;
            maxXView = mapX;
        } else {
            maxXView = centerX + halfXView;
            minXView = centerX - halfXView;
        }

        if (centerY - halfYView < 0) {
            maxYView = halfYView * 2;
            minYView = 0;
        }  else if (centerY + halfYView >= mapY) {
            minYView = mapY - halfYView * 2;
            maxYView = mapY;
        } else {
            maxYView = centerY + halfYView;
            minYView = centerY - halfYView;
        }

        //todo clamp method
        if (minXView < 0) {
            minXView = 0;
        }
        if (maxXView > mapX) {
            maxXView = mapX;
        }
        if (minYView < 0) {
            minYView = 0;
        }
        if (maxYView > mapY) {
            maxYView = mapY;
        }

		// calculate the zoomLevel
		int widthTilesVisible = (int) Math.pow(2, zoomLevel - 1) * (maxXView - minXView);
		if (widthTilesVisible >= mapX) {
			zoomLevel--;
			System.out.println("zoom lvl: " + zoomLevel);
		}
	}

	private void translateWindow(int oldX, int oldY, int x, int y) {
        // todo modularize screen coords
		int prevCenterX = minXView + (maxXView - minXView) * oldX / mapX;
		int prevCenterY = minYView + (maxYView - minYView) * oldY / mapY;
		int centerX = minXView + (maxXView - minXView) * x / mapX;
		int centerY = minYView + (maxYView - minYView) * y / mapY;

        //System.out.println("From " + prevCenterX + ", " + prevCenterY + " to " + centerX + ", " + centerY);

        if (oldX < 0 || x < 0) {
            return;
        } else if (oldX > mapX - 1 || x > mapX - 1) {
            return;
        }
        if (oldY < 0 || y < 0) {
            return;
        } else if (oldY > mapY - 1 || y > mapY - 1) {
            return;
        }


        // translate
        int deltaX = prevCenterX - centerX;
        int deltaY = prevCenterY - centerY;

        int halfXView = (maxXView - minXView) / 2;
        int halfYView = (maxYView - minYView) / 2;

        if (centerX - halfXView < 0) {
            maxXView = halfXView * 2;
            minXView = 0;
        } else if (centerX + halfXView >= mapX) {
            minXView = mapX - halfXView * 2;
            maxXView = mapX; //todo
        } else {
            maxXView += deltaX;
            minXView += deltaX;
        }

        if (centerY - halfYView < 0) {
            maxYView = halfYView * 2;
            minYView = 0;
        }  else if (centerY + halfYView >= mapY) {
            minYView = mapY - halfYView * 2;
            maxYView = mapY; //todo
        } else {
            maxYView += deltaY;
            minYView += deltaY;
        }

    }

	// paints a location at the given
	private void paintPoint(Graphics g, Color c, Location loc) {

	}

	public void updateTectonicPlates(List<TectonicPlate> tectonicPlates){
		this.tectonicPlates = tectonicPlates;
	}

	public void paintComponent(Graphics g) {
		switch(mapMode){
		case 0:
			updateNormalPoints(g);
			if(countryLocations != null){
				updateCountryPoints(g);
			}
			break;
		case 1:
			updateTectonicPlates(g);
			break;
		case 2:
			updateHeatGradients(g);
			break;
		case 3:
			updateHeightGradients(g);
			break;
		case 4:
			updateSurfaceTempGradients(g);
			break;
		default:
			System.err.println("ERROR");
			break;
		}
	}

	private void updateNormalPoints(Graphics g)	{

		for(int y = 0; y < mapY; y++){ //retrieves land colors and draws them on map
			for(int x = 0; x < mapX; x++){
				double locX = minXView + (maxXView - minXView) * x / (double) mapX;
				double locY = minYView + (maxYView - minYView) * y / (double) mapY;
				boolean isLand = locations[mapX * (int) locY + (int) locX].isLand();
				Color c = Color.BLUE;
				if (isLand)
					c = Color.GREEN;
				g.setColor(c);
				g.drawLine(x, y, x, y);
			}
		}
	}

	private void updateSurfaceTempGradients(Graphics g) {
		for(int i = 0; i < locations.length; i++){ //retrieves heights and projects them onto the map TODO FINISH
			double temp = locations[i].getTemperature();
			double red = temp * 255/600 - 100;
			double green = 0;
			double blue = 0;
			if(red > 255){
				green = 255; //error shows
				red = 0;
			} else if(red < 0){
				red = 0;
			}
			g.setColor(new Color((int) red, (int) green, (int) blue));
			g.drawLine(locations[i].getX().intValue(), locations[i].getY().intValue(), locations[i].getX().intValue(), locations[i].getY().intValue());
		}
	}

	private void updateCountryPoints(Graphics g) {
		for(int i = 0; i < countryLocations.length; i++){ //retrieves country colors and draws them on map
			if(countryLocations[i] != null){
				g.setColor(countryLocations[i][0].getColor());
				for(int x = 0; x < countryLocations[i].length; x++){
					if(countryLocations[i][x] != null){
						g.drawLine( countryLocations[i][x].getX().intValue(), countryLocations[i][x].getY().intValue(), countryLocations[i][x].getX().intValue(), countryLocations[i][x].getY().intValue() );
					}
				}
			}
		}
	}

	private void updateTectonicPlates(Graphics g) {
		for(int i = 0; i < locations.length; i++){ // retrieves plate colors and draws them on map
			g.setColor(locations[i].getParentPlate().getColor());
			g.drawLine(locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue(), locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue());
		}
		for(TectonicPlate t: tectonicPlates){ //Draw arrows
			g.setColor(Color.BLACK);
			//g.drawLine((int) t.getCenterX(), (int) t.getCenterY(), (int) (t.getCenterX() +  30 * Math.cos(t.getConvectionCurrent().getDirection())), (int) (t.getCenterY() +  30 * Math.sin(t.getConvectionCurrent().getDirection())));
			//g.drawLine((int) (t.getCenterX() +  30 * Math.cos(t.getConvectionCurrent().getDirection())) + 5, (int) (t.getCenterY() +  30 * Math.sin(t.getConvectionCurrent().getDirection())) + 5, (int) (t.getCenterX() +  30 * Math.cos(t.getConvectionCurrent().getDirection()) - 5), (int) (t.getCenterY() +  30 * Math.sin(t.getConvectionCurrent().getDirection())) - 5);

			double[] orientation = new double[]{Math.cos(t.getConvectionCurrent().getDirection()), Math.sin(t.getConvectionCurrent().getDirection())};
			int arrowHeadX = (int) ((orientation[0] * 30) + t.getCenterX());
			int arrowHeadY = (int) ((orientation[1] * 30) + t.getCenterY());
			g.drawLine((int) t.getCenterX(), (int) t.getCenterY(), arrowHeadX, arrowHeadY);
			double[] arrowHeadSide1 = new double[2];
			double[] arrowHeadSide2 = new double[2];

			double[] arrowMidpoint = new double[] {(int) ((arrowHeadX - t.getCenterX()) / 2 + t.getCenterX()), (int) ((arrowHeadY - t.getCenterY()) / 2 + t.getCenterY())};
			double[] perpOrientation = new double[] {-1 * orientation[1], orientation[0]};
			arrowHeadSide1[0] = (perpOrientation[0] * 5) + arrowMidpoint[0];
			arrowHeadSide1[1] = (perpOrientation[1] * 5) + arrowMidpoint[1];
			arrowHeadSide2[0] = (perpOrientation[0] * -5) + arrowMidpoint[0];
			arrowHeadSide2[1] = (perpOrientation[1] * -5) + arrowMidpoint[1];


			g.drawLine(arrowHeadX, arrowHeadY, (int) arrowHeadSide1[0], (int) arrowHeadSide1[1]);
			g.drawLine(arrowHeadX, arrowHeadY, (int) arrowHeadSide2[0], (int) arrowHeadSide2[1]);

		}

	}

	private void updateHeatGradients(Graphics g) {
		for(int i = 0; i < locations.length; i++){ //retrieves plate temperatures and draws them on map
			double temp = locations[i].getMantleTemperature(); //should be between about 200 and 400, with most around 330
			
			if(temp > 255){
				temp = 255;
			}
			g.setColor(new Color((int) temp, 0, 0));
			g.drawLine(locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue(), locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue());
		}
	}

	private void updateHeightGradients(Graphics g) {
		for(int i = 0; i < locations.length; i++){ //retrieves heights and projects them onto the map TODO FINISH
			double height = locations[i].getElevation();
			double red = 0;
			double green = 0;
			double blue = 0;
			if (height > 0) { //above sea level
				red = 102. / 50000. * height;
				if(red > 102){
					red = 102;
				}
				green = 255 - 204 / 50000. * height;
				if(green > 255){
					green = 255;
				} else if(green < 41){
					green = 41;
				}
			} else { //at or below sea level
				blue = 255 + 255 / 10000. * height; //NOTE: height will always be negative here
			}
			g.setColor(new Color((int) red, (int) green, (int) blue));
			g.drawLine(locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue(), locations[i].getX().add(new BigDecimal(1)).intValue(), locations[i].getY().add(new BigDecimal(1)).intValue());
		}
	}

	@Override
	public void run() {
		repaint();
		revalidate();
	}

	public void updateLocations(Location[] locs, Location[][] countryLocs) {
		locations = locs;
		countryLocations = countryLocs;
	}

	public void setMapMode(int mode) {
		mapMode = mode;
	}

}
