package gui.panels;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.Constants;
import data.Data;
import data.Settings;
import gui.windows.PekaEDGUI;

public class TilePanel extends JPanel implements MouseListener, MouseMotionListener {

	BufferedImage tileset;	
	int x, y, w, h, dx, dy;
	private PekaEDGUI pk;
	
	public TilePanel(PekaEDGUI pk) {
		addMouseListener(this);
		addMouseMotionListener(this);
		
		setBackground(Color.lightGray);
		
		this.pk = pk;
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		g.drawImage(tileset, 0, 0, null);
		
		if (Data.multiSelectTiles) {
			g.setColor(Color.black);
			g.drawRect(dx * 32, dy * 32, (Data.sw * 32) - 1, (Data.sh * 32) - 1);
			
			g.setColor(Color.white);
			g.drawRect((dx * 32) + 1, (dy * 32) + 1, (Data.sw * 32) - 3, (Data.sh * 32) - 3);
			
			g.setColor(Color.black);
			g.drawRect((dx * 32) + 2, (dy * 32) + 2, (Data.sw * 32) - 5, (Data.sh * 32) - 5);
		} else if (!Data.multiSelectTiles) {
			g.setColor(Color.BLACK);
			g.drawRect(x, y, 31, 31);

			g.setColor(Color.white);
			g.drawRect(x + 1, y + 1, 29, 29);

			g.setColor(Color.BLACK);
			g.drawRect(x + 2, y + 2, 27, 27);
		}
	}
	
	public void setTileset(String str) {
		if (!Settings.BASE_PATH.isEmpty()) {
			try {
				tileset = ImageIO.read(new File(Settings.TILES_PATH + str));
				BufferedImage result = new BufferedImage(tileset.getWidth(), tileset.getHeight(), BufferedImage.TYPE_INT_ARGB);
				 
			    // make color transparent
			    int oldRGB = new Color(148, 209, 222).getRGB();
			 
			    for (int i = 0; i < tileset.getWidth(); i++) {
			    	for (int j = 0; j < tileset.getHeight(); j++) {
			    		if (tileset.getRGB(i, j) != oldRGB) {
			    			result.setRGB(i, j, tileset.getRGB(i, j));
			    		}
			    	}
			    }
			    
			    tileset = result;
				
				setPreferredSize(new Dimension(tileset.getWidth(), tileset.getHeight()));
				
				x = 0;
				y = 0;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Could'nt read tileset file.\n'" + str + "'", "Error", JOptionPane.OK_OPTION);
				//e.printStackTrace();
			}
		}
		
		repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int mx = (e.getX() / 32);
		int my = (e.getY() / 32);
		
		Data.multiSelectTiles = true;
		Data.multiSelectLevel = false;
		
		if (mx < Data.sx) {
			Data.sw = Data.sx - mx;
			
			dx = mx;
		} else {
			Data.sw = mx - Data.sx;
			
			dx = Data.sx;
		}
		
		if (my < Data.sy) {
			Data.sh = Data.sy - my;
			
			dy = my;
		} else {
			Data.sh = my - Data.sy;
			
			dy = Data.sy;
		}
		
		Data.sw += 1;
		Data.sh += 1;
		
		if (dx + Data.sw > (tileset.getWidth() / 32)) {
			Data.sw = (tileset.getWidth() / 32) - Data.sx;
		}
		
		if (dy + Data.sh > (tileset.getHeight() / 32)) {
			Data.sh = (tileset.getHeight() / 32) - Data.sy;
		}
		
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getY() <= 480) {
			x = (e.getX() / 32);
			y = (e.getY() / 32);
		
			w = 32;
			h = 32;
			
			dx = x;
			dy = y;
			
			Data.multiSelectionForeground.clear();
			Data.multiSelectionBackground.clear();
			Data.multiSelectTiles = false;
			
			Data.sx = x;
			Data.sy = y;
			Data.sw = 0;
			Data.sh = 0;
			
			if (Data.currentLayer == Constants.LAYER_FOREGROUND) {
				Data.selectedTileBackground = 255;
				Data.selectedTileForeground = y * (320 / 32) + x;
			} else if (Data.currentLayer == Constants.LAYER_BACKGROUND) {
				Data.selectedTileForeground = 255;
				Data.selectedTileBackground = y * (320 / 32) + x;
			} else {
				Data.selectedTileForeground = y * (320 / 32) + x;
				Data.selectedTileBackground = y * (320 / 32) + x;
			}
			
			Data.selectedSprite = 255;
			Data.selectedTool = Data.TOOL_BRUSH;
			pk.setToolButton();
			
			x *= 32;
			y *= 32;
			
			repaint();
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (Data.multiSelectTiles) {
			for (int x = dx; x < dx + Data.sw; x++) {
				for (int y = dy; y < dy + Data.sh; y++) {
					if (Data.currentLayer == Constants.LAYER_FOREGROUND || Data.currentLayer == Constants.LAYER_BOTH) {
						if (!Data.multiSelectionForeground.contains(y * (320 / 32) + x)) { // is this line necessary?
							Data.multiSelectionForeground.add(y * (320 / 32) + x);
						}
					}
					
					if (Data.currentLayer == Constants.LAYER_BACKGROUND || Data.currentLayer == Constants.LAYER_BOTH) {
						if (!Data.multiSelectionBackground.contains(y * (320 / 32) + x)) { // is this line necessary?
							Data.multiSelectionBackground.add(y * (320 / 32) + x);
						}
					}
				}
			}
		}
	}

}
