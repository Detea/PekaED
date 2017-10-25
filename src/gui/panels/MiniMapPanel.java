package gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JPanel;

import data.Constants;
import data.Data;
import gui.windows.PekaEDGUI;
import pekkakana.PK2Map;

public class MiniMapPanel extends JPanel implements MouseListener, MouseMotionListener {

	private int vx, vy; // viewport x, y
	private int vw, vh; // viewport width, height
	
	private PekaEDGUI pkg;
	
	public MiniMapPanel(PekaEDGUI pkg) {
		setBackground(Color.DARK_GRAY);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		setPreferredSize(new Dimension(PK2Map.MAP_WIDTH, PK2Map.MAP_HEIGHT));
		
		this.pkg = pkg;
		
		vw = 48;
		vh = 32;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		for (int i = 0; i < PK2Map.MAP_WIDTH; i++) {
			for (int j = 0; j < PK2Map.MAP_HEIGHT; j++) {
				if (Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_BACKGROUND) != 255) {
					g.setColor(new Color(Data.lp.tiles.get(Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_BACKGROUND)).getRGB(0, 0)));
					g.fillRect(i, j, 1, 1);
				}
				
				if (Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_FOREGROUND) != 255) {
					g.setColor(new Color(Data.lp.tiles.get(Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_FOREGROUND)).getRGB(0, 0)));
					g.fillRect(i, j, 1, 1);
				}
			}
		}
		
		g.setColor(Color.white);
		g.drawRect(vx - (vw / 2), vy - (vh / 2), vw, vh);
		
		/*
		if (Data.map.getTileAt(mx * 32, my * 32, Constants.LAYER_FOREGROUND) != 255) {
			g.setColor(new Color(0xFFFFFFFF - Data.lp.tiles.get(Data.map.getTileAt(mx * 32, my * 32, Constants.LAYER_FOREGROUND)).getRGB(0, 0)));
			g.drawRect(mx - 24, my - 16, 48, 32);
		} else {
			g.setColor(Color.white);
			g.drawRect(mx - 24, my - 16, 48, 32);
		}*/
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		vx = e.getX();
		vy = e.getY();
		
		if (vx - (vw / 2) < 0) {
			vx = vw / 2;
		} else if (vx + (vw / 2) > PK2Map.MAP_WIDTH) {
			vx = PK2Map.MAP_WIDTH - (vw / 2);
		}
		
		if (vy - (vh / 2) < 0) {
			vy = vh / 2;
		} else if (vy + (vh / 2) > PK2Map.MAP_HEIGHT) {
			vy = PK2Map.MAP_HEIGHT - (vh / 2);
		}
		
		pkg.scrollPane2.getVerticalScrollBar().setValue((vy - (vh / 2)) * 32);
		pkg.scrollPane2.getHorizontalScrollBar().setValue((vx - (vw / 2)) * 32);
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		vx = e.getX();
		vy = e.getY();
		
		if (vx - (vw / 2) < 0) {
			vx = vw / 2;
		} else if (vx + (vw / 2) > PK2Map.MAP_WIDTH) {
			vx = 226 - (vw / 2);
		}
		
		if (vy - (vh / 2) < 0) {
			vy = vh / 2;
		} else if (vy + (vh / 2) > PK2Map.MAP_HEIGHT) {
			vy = 226 - (vh / 2);
		}

		pkg.scrollPane2.getVerticalScrollBar().setValue((vy - (vh / 2)) * 32);
		pkg.scrollPane2.getHorizontalScrollBar().setValue((vx - (vw / 2)) * 32);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
