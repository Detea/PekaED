package gui.panels;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import data.Constants;
import data.Data;
import data.Settings;
import gui.windows.PekaEDGUI;

public class TilePanel extends JPanel implements MouseListener, MouseMotionListener {

	BufferedImage tileset;	
	public int x, y, w, h, dx, dy, fx, fy, in, xx, yy;
	private PekaEDGUI pk;
	boolean inPanel = false;
	
	private int[][] tileTable = new int[320 / 32][480 / 32]; // shouldn't be hardcoded, in case there will be support for bigger tile sets, but there probably won't
	
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
		
			if (Data.showTileNr) {
				g.setColor(Color.WHITE);
				g.setFont(new Font(g.getFont().getName(), Font.BOLD, 12));
				
				// Ensure that the tile number is always centered
				if (in / 10 < 1) {
					if (inPanel) {
						g.drawString("" + (in + 1), x + 13, y + 20);
					} else {
						g.drawString("" + (in + 1), xx + 13, yy + 20);
					}
				} else if (in / 10 <= 10) {
					if (inPanel) {
						g.drawString("" + (in + 1), x + 6, y + 20);
					} else {
						g.drawString("" + (in + 1), xx + 6, yy + 20);
					}
				} else {
					if (inPanel) {
						g.drawString("" + (in + 1), x + 4, y + 20);
					} else {
						g.drawString("" + (in + 1), xx + 4, yy + 20);
					}
				}
			}
		}
	}
	
	public void setTileset() {
		if (!Settings.BASE_PATH.isEmpty()) {
			try {
				tileset = ImageIO.read(Data.tilesetFile);
				
				byte[] rs = new byte[256];
				byte[] gs = new byte[256];
				byte[] bs = new byte[256];
				
				Data.bgPalette.getReds(rs);
				Data.bgPalette.getGreens(gs);
				Data.bgPalette.getBlues(bs);
				
				IndexColorModel icm = new IndexColorModel(8, 256, rs, gs, bs);
				
				BufferedImage result = new BufferedImage(tileset.getWidth(), tileset.getHeight(), BufferedImage.TYPE_BYTE_INDEXED, icm);
	
				byte[] ts = ((DataBufferByte) (tileset.getRaster().getDataBuffer())).getData();
			    byte[] rd = ((DataBufferByte) (result.getRaster().getDataBuffer())).getData();
				
			    for (int i = 0; i < ts.length; i++) {
			    	rd[i] = ts[i];
			    }
			    
			    tileset = result; // temporarily storing the image
				
			    result = new BufferedImage(tileset.getWidth(), tileset.getHeight(), BufferedImage.TYPE_INT_ARGB);

			    int[] data = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
			    
			    for (int i = 0; i < data.length; i++) {
			    	if ((ts[i] & 0xFF) != 255) {
			    		data[i] = tileset.getColorModel().getRGB(ts[i]);
			    	}
			    }
			    
			    tileset = new BufferedImage(tileset.getWidth(), tileset.getHeight(), BufferedImage.TYPE_INT_ARGB);
			    tileset = result;
			    
				setPreferredSize(new Dimension(tileset.getWidth(), tileset.getHeight()));
				
				x = 0;
				y = 0;
			} catch (IOException e) {
				//JOptionPane.showMessageDialog(this, "Couldn't read tileset file.\n'" + Data.tilesetFile.getName() + "'", "Error", JOptionPane.OK_OPTION);
				if (!new File(Settings.TILES_PATH + Settings.DEFAULT_TILESET).exists()) {
					try {
						tileset = ImageIO.read(getClass().getResource("/missingTileset.png"));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		}
		
		tileTable = new int[tileset.getHeight() / 32][tileset.getWidth() / 32];
		
		int xx = 0, yy = 0, ii = 0;
		while (yy < tileset.getHeight() / 32) {
			tileTable[yy][xx] = ii;
			
			xx++;
			
			if (xx >= tileset.getWidth() / 32) {
				yy++;
				xx = 0;
			}
			
			ii++;
		}
		
		repaint();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		int mx = (e.getX() / 32);
		int my = (e.getY() / 32);
			
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
		
		if (Data.sw > 1 || Data.sh > 1) {
			Data.multiSelectTiles = true;
			Data.multiSelectLevel = false;
		}
		
		repaint();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		x = e.getX() / 32;
		y = e.getY() / 32;
		
		in = tileTable[y][x];
		
		x *= 32;
		y *= 32;
		
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		inPanel = true;
	}

	@Override
	public void mouseExited(MouseEvent e) {
		x = xx;
		y = yy;
		
		in = tileTable[y / 32][x / 32];
		
		inPanel = false;
		
		repaint();
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
			Data.multiSelectLevel = false;
			
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
				Data.selectedTileForeground = y * (320 / 32) + x; // Only foreground?
				Data.selectedTileBackground = 255;
			}
			
			Data.selectedSprite = 255;
			Data.selectedTool = Data.TOOL_BRUSH;
			pk.setToolButton();
			
			x *= 32;
			y *= 32;
			
			xx = x;
			yy = y;
			
			pk.setEditMode(Constants.EDIT_MODE_TILES);
			
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
