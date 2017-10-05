package gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import data.Constants;
import data.Data;
import data.Settings;
import pekkakana.PK2Map;

public class LevelPanel extends JPanel implements MouseListener, MouseMotionListener, Runnable {
	
	Thread thread;
	
	ArrayList<Rectangle> level = new ArrayList<Rectangle>();
	
	Stack<Rectangle> redoStack = new Stack<Rectangle>();
	Stack<Rectangle> undoStack = new Stack<Rectangle>();
	
	int mx, my, layer, mouseButton;
	
	BufferedImage background, tileset;
	ArrayList<BufferedImage> tiles = new ArrayList<BufferedImage>();
	
	public LevelPanel() {
		setBackground(Color.LIGHT_GRAY);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		
		setPreferredSize(new Dimension(PK2Map.MAP_WIDTH * 32, PK2Map.MAP_HEIGHT * 32));
		
		setTileset(Settings.DEFAULT_TILESET);
		setBackground(Settings.DEFAULT_BACKGROUND);
		
		thread = new Thread(this);
		thread.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (Data.map != null) {
			for (int i = 0; i < (PK2Map.MAP_WIDTH * 32) / background.getWidth() + 1; i++) {
				for (int j = 0; j < (PK2Map.MAP_HEIGHT * 32) / background.getHeight() + 1; j++) {
					g.drawImage(background, i * background.getWidth(), j * background.getHeight(), null);
				}
			}
			
			if (Data.currentLayer == Constants.LAYER_BACKGROUND || Data.currentLayer == Constants.LAYER_BOTH) {
				for (int i = 0; i < PK2Map.MAP_WIDTH; i++) {
					for (int j = 0; j < PK2Map.MAP_HEIGHT; j++) {
						drawTile(g, i * 32, j * 32, Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_BACKGROUND));
					}
				}
			}
			
			if (Data.showSprites) {
				for (int i = 0; i < PK2Map.MAP_WIDTH; i++) {
					for (int j = 0; j < PK2Map.MAP_HEIGHT; j++) {
						if ((PK2Map.MAP_WIDTH * i + j) < Data.map.sprites.length && Data.map.sprites[PK2Map.MAP_WIDTH * i + j] != 255) {
							if (!Data.map.spriteList.isEmpty()) {
								g.drawImage(Data.map.spriteList.get(Data.map.sprites[PK2Map.MAP_WIDTH * i + j]).image, ((i * 32) - (Data.map.spriteList.get(Data.map.sprites[PK2Map.MAP_WIDTH * i + j]).image.getWidth() / 2) + 16), ((j * 32) - (Data.map.spriteList.get(Data.map.sprites[PK2Map.MAP_WIDTH * i + j]).image.getHeight() - 32)), null);
							}
						}
					}
				}
			}
			
			if (Data.currentLayer == Constants.LAYER_FOREGROUND || Data.currentLayer == Constants.LAYER_BOTH) {
				for (int i = 0; i < PK2Map.MAP_WIDTH; i++) {
					for (int j = 0; j < PK2Map.MAP_HEIGHT; j++) {
						drawTile(g, i * 32, j * 32, Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_FOREGROUND));
					}
				}
			}
				
			if (Data.selectedTool == Data.TOOL_BRUSH) {
				if (Data.currentLayer != Constants.LAYER_BOTH) {
					if (!Data.multiSelectTiles) {
						drawTile(g, mx - 16, my - 16, Data.selectedTile);
					} else {
						for (int x = Data.sx; x < Data.sw; x++) {
							for (int y = Data.sy; y < Data.sh; x++) {
								drawTile(g, mx, my, Data.multiSelection.get(Data.sw * x + y));
							}
						}
					}
				} else {
					if (Data.selectedTileForeground != 255) {
						drawTile(g, mx - 16, my - 16, Data.selectedTileForeground);
					} else if (Data.selectedTileBackground != 255) {
						drawTile(g, mx - 16, my - 16, Data.selectedTileBackground);
					}
				}
				
				if (Data.selectedTile == 255 && Data.selectedSprite != 255) {
					if (!Data.map.spriteList.isEmpty()) {
						g.drawImage(Data.map.spriteList.get(Data.selectedSprite).image, mx - (Data.map.spriteList.get(Data.selectedSprite).image.getWidth() / 2), my - (Data.map.spriteList.get(Data.selectedSprite).image.getHeight() / 2), null);
					}
				}
			}
			/*
			 * if (Data.multiSelection.size() > 0) {
				for (int x = 0; x < Data.sw; x++) {
					for (int y = 0; y < Data.sh; y++) {
						drawTile(g, x * 32, y * 32, Data.multiSelection.get(Data.sw * x + y));
					}
				}
			}
			 */
		}
	}

	
	private void drawTile(Graphics g, int x, int y, int tile) {
		if (tile != 255 && tile != -1) {
			g.drawImage(tiles.get(tile), x, y, null);
		}
	}
	
	public void setTileset(String s) {
		if (!Settings.BASE_PATH.isEmpty()) {
			try {
				tileset = ImageIO.read(new File(Settings.TILES_PATH + s));
				
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
			    
				int x = 0, y = 0;
				
				tiles.clear();
				while (y < result.getHeight()) {
					tiles.add(result.getSubimage(x, y, 32, 32));
					
					x += 32;
					
					if (x == tileset.getWidth()) {
						y += 32;
						x = 0;
					}
				}
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Could'nt read tileset file.\n'" + s + "'", "Error", JOptionPane.OK_OPTION);
				
				//e.printStackTrace();
			}
		}
	}
	
	public void setBackground(String str) {
		if (!Settings.BASE_PATH.isEmpty()) {
			try {
				background = ImageIO.read(new File(Settings.SCENERY_PATH + str));
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Could'nt read background file.\n'" + str + "'", "Error", JOptionPane.OK_OPTION);
				
				//e.printStackTrace();
			}
		}
	}
	
	public void setMap() {
		setTileset(Data.map.getTileset());
		setBackground(Data.map.getBackground());
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (Data.map != null) {
			mx = e.getX();
			my = e.getY();
			
			if (mouseButton == MouseEvent.BUTTON1) {
				Data.fileChanged = true;
				
				if (Data.selectedTile != 255) {
					switch (Data.selectedTool) {
						case Data.TOOL_BRUSH:
							Data.map.setTile(e.getX(), e.getY(), Data.selectedTile);
							break;
	
						case Data.TOOL_ERASER:
							Data.map.setTile(e.getX(), e.getY(), 255);
							break;
					}
				} else if (Data.selectedSprite != 255) {
					Data.map.sprites[PK2Map.MAP_WIDTH * (mx / 32) + (my / 32)] = Data.selectedSprite;
				}
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mx = e.getX();
		my = e.getY();
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
		if (Data.map != null) {
			mouseButton = e.getButton();
			
			if (e.getButton() == MouseEvent.BUTTON1) {
				Data.fileChanged = true;
				
				switch (Data.selectedTool) {
				case Data.TOOL_BRUSH:
					if (Data.selectedTile != 255) {
						Data.map.setTile(e.getX(), e.getY(), Data.selectedTile);
					} else if (Data.selectedSprite != 255) {
						Data.map.sprites[PK2Map.MAP_WIDTH * (mx / 32) + (my / 32)] = Data.selectedSprite;
					}
					break;

				case Data.TOOL_ERASER:
					if (Data.selectedTile != 255) {
						Data.map.setTile(e.getX(), e.getY(), 255);
					} else if (Data.selectedSprite != 255) {
						Data.map.sprites[PK2Map.MAP_WIDTH * (mx / 32) + (my / 32)] = 255;
					}
					break;
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				Data.fileChanged = true;
				
				if (Data.selectedSprite == 255) {
					switch (Data.selectedTool) {
						case Data.TOOL_BRUSH:
							if (Data.currentLayer == Constants.LAYER_BOTH) {
								Data.selectedTileForeground = Data.map.getTileAt(e.getX(), e.getY(), Constants.LAYER_FOREGROUND);
								Data.selectedTileBackground = Data.map.getTileAt(e.getX(), e.getY(), Constants.LAYER_BACKGROUND);
							} else {
								Data.selectedTile = Data.map.getTileAt(e.getX(), e.getY(), Data.currentLayer);
							}
							
							Data.sx = (e.getX() / 32) * 32;
							Data.sy = (e.getY() / 32) * 32;
							Data.sw = 0;
							Data.sh = 0;
					}
				} else {
					Data.selectedSprite = Data.map.sprites[PK2Map.MAP_WIDTH * (mx / 32) + (my / 32)];
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void run() {
		while (true) {
			repaint();
			
			try {
				Thread.sleep(17);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
