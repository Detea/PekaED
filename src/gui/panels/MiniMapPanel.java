package gui.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

import javax.swing.JPanel;

import data.Constants;
import data.Data;
import gui.windows.PekaEDGUI;
import pekkakana.PK2Map;

public class MiniMapPanel extends JPanel implements MouseListener, MouseMotionListener {

	private int vx, vy; // viewport x, y
	private int vw, vh; // viewport width, height
	
	private PekaEDGUI pkg;
	
	private AffineTransform af;
	private AffineTransform is;
	
	public MiniMapPanel() {
		setBackground(Color.lightGray);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		setMinimumSize(new Dimension(PK2Map.MAP_WIDTH, PK2Map.MAP_HEIGHT));
		setPreferredSize(new Dimension(PK2Map.MAP_WIDTH, PK2Map.MAP_HEIGHT));
	}
	
	public void setPekaGUI(PekaEDGUI pkg) {
		this.pkg = pkg;
		
		resizeViewportRect();
	}
	
	public void resizeViewportRect() {
		if (pkg != null) {
			vw = (int) ((pkg.scrollPane2.getViewport().getVisibleRect().width / 32) / Data.scale);
			vh = (int) ((pkg.scrollPane2.getViewport().getVisibleRect().height / 32) / Data.scale);
			
			af = AffineTransform.getScaleInstance(Data.scale, Data.scale);
			
			try {
				is = af.createInverse();
			} catch (NoninvertibleTransformException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			repaint();
		}
	}
	
	public void reposition() {
		vx = (vw / 2) + pkg.scrollPane2.getHorizontalScrollBar().getValue() / 32;
		vy = (vh / 2) + pkg.scrollPane2.getVerticalScrollBar().getValue() / 32;
		
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
		
		repaint();
		
		/*vx = (int) ((vw / 2) + (pkg.scrollPane2.getHorizontalScrollBar().getValue() / 32) * Data.scale);
		vy = (int) ((vh / 2) + (pkg.scrollPane2.getVerticalScrollBar().getValue() / 32) * Data.scale);
		
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
		
		repaint();*/
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (Data.map != null) {
			g.setColor(Color.gray);
			g.fillRect(0, 0, PK2Map.MAP_WIDTH, PK2Map.MAP_HEIGHT);
			
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
			g.drawRect((int) ((vx - (vw / 2)) / Data.scale), (int) ((vy - (vh / 2)) / Data.scale), vw, vh);
		}
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
		
		int px = (int) (((vx - (vw / 2)) * 32) * Data.scale);
		int py = (int) (((vy - (vh / 2)) * 32) * Data.scale);
		
		pkg.scrollPane2.getViewport().setViewPosition(new Point(px, py));
		
		/*
		pkg.scrollPane2.getVerticalScrollBar().setValue((vy - (vh / 2)) * 32);
		pkg.scrollPane2.getHorizontalScrollBar().setValue((vx - (vw / 2)) * 32);
		*/
		
		repaint();
		
		Data.lp.repaint();
		
		/*vx = e.getX();
		vy = e.getY();
		
		if (vx - (vw / 2) < 0) 
			vx = vw / 2;
	
		if (vy - (vh / 2) < 0)
			vy = vh / 2;
		
		if (vx + vw / 2 > PK2Map.MAP_WIDTH)
			vx = PK2Map.MAP_WIDTH - vw / 2;
		
		if (vy + vh / 2 > PK2Map.MAP_HEIGHT)
			vy = PK2Map.MAP_HEIGHT - vh / 2;
		
		pkg.scrollPane2.getViewport().setViewPosition(new Point(((vx - (vw / 2)) * 32), ((vy - (vh / 2)) * 32)));
		
		reposition();
		
//		Data.lp.repaint();*/
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
			vx = PK2Map.MAP_WIDTH - (vw / 2);
		}
		
		if (vy - (vh / 2) < 0) {
			vy = vh / 2;
		} else if (vy + (vh / 2) > PK2Map.MAP_HEIGHT) {
			vy = PK2Map.MAP_HEIGHT - (vh / 2);
		}

		pkg.scrollPane2.getVerticalScrollBar().setValue((vy - (vh / 2)) * 32);
		pkg.scrollPane2.getHorizontalScrollBar().setValue((vx - (vw / 2)) * 32);
		
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
