package gui.panels;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;

import data.Constants;
import data.Data;
import data.Settings;
import gui.windows.PekaEDGUI;
import pekkakana.PK2Map;

public class LevelPanel extends JPanel implements MouseListener, MouseMotionListener, MouseWheelListener {
	
	private Thread thread;
	
	private int mx, my, layer, mouseButton;
	private int viewX, viewY, viewW, viewH;
	
	private BufferedImage background, tileset;
	public ArrayList<BufferedImage> tiles = new ArrayList<BufferedImage>();
	public ArrayList<BufferedImage> inactiveTiles = new ArrayList<BufferedImage>();

	private BufferedImage lastTile;
	
	private Image bgImg;
	
	private PekaEDGUI pkg;
	
	public boolean drawing = true;

	private int dx, dy;
	private int originX, originY;
	
	// variables for animation
	//private int ani1 = 60, ani2 = 65, ani3 = 70, ani4 = 75; 
	
	BufferedImage buffer;
	Graphics2D gg;
	
	AffineTransform af;
	AffineTransform is;
	
	public LevelPanel() {
		setBackground(Color.LIGHT_GRAY);
		
		addMouseListener(this);
		addMouseMotionListener(this);
		addMouseWheelListener(this);
		
		setPreferredSize(new Dimension(PK2Map.MAP_WIDTH * 32, PK2Map.MAP_HEIGHT * 32));
		
		Data.bgFile = new File(Settings.SCENERY_PATH + "\\" + Settings.DEFAULT_BACKGROUND);
		Data.tilesetFile = new File(Settings.TILES_PATH + "\\" + Settings.DEFAULT_TILESET);
		
		setBackground();
		setTileset();
		
		buffer = new BufferedImage(PK2Map.MAP_WIDTH * 32, PK2Map.MAP_HEIGHT * 32, BufferedImage.TYPE_INT_ARGB);
		gg = (Graphics2D) buffer.getGraphics();
		
		af = AffineTransform.getScaleInstance(1, 1);
		
		try {
			is = af.createInverse();
		} catch (NoninvertibleTransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			is = af.createInverse();
		} catch (NoninvertibleTransformException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setPekaGUI(PekaEDGUI pkg) {
		this.pkg = pkg;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (Data.map != null) {
			viewX = (int) ((pkg.scrollPane2.getViewport().getViewRect().x / Data.scale) / 32);
			viewY = (int) ((pkg.scrollPane2.getViewport().getViewRect().y / Data.scale) / 32);
			viewW = (int) ((pkg.scrollPane2.getViewport().getViewRect().width / Data.scale) / 32);
			viewH = (int) ((pkg.scrollPane2.getViewport().getViewRect().height / Data.scale) / 32);
			
			Graphics2D g2d = (Graphics2D) g;
			//g2d.scale(Data.scale, Data.scale);
			g2d.transform(af);
			
			// Not the best solution, but it works. This should be improved.
			for (int i = 0; i < (PK2Map.MAP_WIDTH * 32) / background.getWidth() + 1; i++) {
				 for (int j = 0; j < (PK2Map.MAP_HEIGHT * 32) / background.getHeight() + 1; j++) {
					 g2d.drawImage(background, i * background.getWidth(), j * background.getHeight(), null);
				 }
			}
			
			if (Data.currentLayer == Constants.LAYER_BACKGROUND || Data.currentLayer == Constants.LAYER_BOTH) {
				for (int i = viewX; i < (viewX + viewW) + 2; i++) {
					for (int j = viewY; j < (viewY + viewH) + 2; j++) {
						if (Data.currentLayer != Constants.LAYER_BOTH) {
							if (Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_FOREGROUND) != 255) {
								g2d.drawImage(inactiveTiles.get(Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_FOREGROUND)), i * 32, j * 32, null);
							}
						}
						
						drawTile(g2d, i * 32, j * 32, Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_BACKGROUND));
					}
				}
			}
			
			if (Data.showSprites) {
				for (int i = viewX; i < (viewX + viewW) + 16; i++) {
					for (int j = viewY; j < (viewY + viewH) + 16; j++) { // 16 is an arbitrary value. This should be the size of the biggest sprites divided by 32
						if ((PK2Map.MAP_WIDTH * i + j) < Data.map.sprites.length && Data.map.sprites[PK2Map.MAP_WIDTH * i + j] != 255) {
							if (!Data.map.spriteList.isEmpty() && Data.map.spriteList.get(Data.map.sprites[PK2Map.MAP_WIDTH * i + j]).image != null) {
								g2d.drawImage(Data.map.spriteList.get(Data.map.sprites[PK2Map.MAP_WIDTH * i + j]).image, ((i * 32) - (Data.map.spriteList.get(Data.map.sprites[PK2Map.MAP_WIDTH * i + j]).image.getWidth() / 2) + 16), ((j * 32) - (Data.map.spriteList.get(Data.map.sprites[PK2Map.MAP_WIDTH * i + j]).image.getHeight() - 32)), null);
							
								if (Data.editMode == Constants.EDIT_MODE_SPRITES && Data.showSpriteRect) {
									g.setColor(Color.white);
									g.drawRect(i * 32, j * 32, 32, 32);
								}
							}
						}
					}
				}
			}
			
			if (Data.currentLayer == Constants.LAYER_FOREGROUND || Data.currentLayer == Constants.LAYER_BOTH) {
				for (int i = viewX; i < (viewX + viewW) + 2; i++) {
					for (int j = viewY; j < (viewY + viewH) + 2; j++) {
						if (Data.currentLayer != Constants.LAYER_BOTH) {
							if (Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_BACKGROUND) != 255) {
								g2d.drawImage(inactiveTiles.get(Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_BACKGROUND)), i * 32, j * 32, null);
							}
						}
						
						drawTile(g2d, i * 32, j * 32, Data.map.getTileAt((i * 32), (j * 32), Constants.LAYER_FOREGROUND));
						
						/*
						 * Animated tiles, not used
						 * 
						if (Data.map.getTileAt((i * 32), (j * 32), Constants.LAYER_FOREGROUND) == 60) {
							drawTile(g2d, i * 32, j * 32, ani1);
						} else if (Data.map.getTileAt((i * 32), (j * 32), Constants.LAYER_FOREGROUND) == 65) {
							drawTile(g2d, i * 32, j * 32, ani2);
						} else if (Data.map.getTileAt((i * 32), (j * 32), Constants.LAYER_FOREGROUND) == 70) {
							drawTile(g2d, i * 32, j * 32, ani3);
						} else if (Data.map.getTileAt((i * 32), (j * 32), Constants.LAYER_FOREGROUND) == 75) {
							drawTile(g2d, i * 32, j * 32, ani4);
						} else {
							drawTile(g2d, i * 32, j * 32, Data.map.getTileAt((i * 32), (j * 32), Constants.LAYER_FOREGROUND));
						}*/
					}
				}
			}
			
			//g2d.drawImage(buffer.getSubimage(0, 0, 1280, 720), 0, 0, (int) (buffer.getWidth() / Data.scale), (int) (buffer.getHeight() / Data.scale), null);
			
			// If the currently selected tool is the brush, draw the selected tiles at the position of the mouse cursor
			if (Data.editMode == Constants.EDIT_MODE_TILES) {
				if (Data.selectedTool == Data.TOOL_BRUSH) {
					if (!Data.multiSelectionForeground.isEmpty() || !Data.multiSelectionBackground.isEmpty()) {
						if (Data.currentLayer == Constants.LAYER_BACKGROUND || Data.currentLayer == Constants.LAYER_BOTH) {
							int x = 0, y = 0, i = 0;
							
							while (i < Data.multiSelectionBackground.size()) {
								drawTile(g, (mx + (x * 32)) - ((Data.sw * 32) / 2), (my + (y * 32)) - ((Data.sh * 32) / 2), Data.multiSelectionBackground.get(i));

								y++;

								if (y >= Data.sh) {
									x++;
									y = 0;
								}

								i++;
							}
						}
						
						if (Data.currentLayer == Constants.LAYER_FOREGROUND || Data.currentLayer == Constants.LAYER_BOTH) {
							int x = 0, y = 0, i = 0;
							
							while (i < Data.multiSelectionForeground.size()) {
								drawTile(g, (mx + (x * 32)) - ((Data.sw * 32) / 2), (my + (y * 32)) - ((Data.sh * 32) / 2), Data.multiSelectionForeground.get(i));

								y++;

								if (y >= Data.sh) {
									x++;
									y = 0;
								}

								i++;
							}
						}
					} else if(!Data.multiSelectLevel && !Data.multiSelectTiles) {
						/*
						 * This draws the selected tiles.
						 * They are both getting drawn, because if the user has selected a transparent tile, the tile behind it
						 * should also be drawn.
						 */
						if (Data.selectedTileBackground != 255) {
							drawTile(g, mx - 16, my - 16, Data.selectedTileBackground);
						}
						
						if (Data.selectedTileForeground != 255) {
							drawTile(g, mx - 16, my - 16, Data.selectedTileForeground);
						}
					}
				}
			} else if (Data.editMode == Constants.EDIT_MODE_SPRITES) {
				if (Data.selectedSprite != 255 && Data.selectedTool == Data.TOOL_BRUSH) {
					if (!Data.map.spriteList.isEmpty()) {
						g.drawImage(Data.map.spriteList.get(Data.selectedSprite).image, mx - (Data.map.spriteList.get(Data.selectedSprite).image.getWidth() / 2), my - (Data.map.spriteList.get(Data.selectedSprite).image.getHeight() / 2), null);
					}
				}
			}
			
			if (Data.dragging) {
				g.setColor(Color.black);
				g.drawRect(dx * 32, dy * 32, Data.sw * 32, Data.sh * 32);
				
				g.setColor(Color.white);
				g.drawRect(dx * 32 + 1, dy * 32 + 1, Data.sw * 32 - 2, Data.sh * 32 - 2);
				
				g.setColor(Color.black);
				g.drawRect(dx * 32 + 2, dy * 32 + 2, Data.sw * 32 - 4, Data.sh * 32 - 4);
			}
		}
	}

	public Dimension getPreferredSize() {
		int w = (int) ((PK2Map.MAP_WIDTH * 32) * Data.scale);
		int h = (int) ((PK2Map.MAP_HEIGHT * 32) * Data.scale);
		
		return new Dimension(w, h);
	}
		
	private void drawTile(Graphics g, int x, int y, int tile) {
		if (tile != 255 && tile != -1) {
			g.drawImage(tiles.get(tile), x, y, null);
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
	
				byte[] tss = ((DataBufferByte) (tileset.getRaster().getDataBuffer())).getData();
			    byte[] rd = ((DataBufferByte) (result.getRaster().getDataBuffer())).getData();
				
			    for (int i = 0; i < tss.length; i++) {
			    	rd[i] = tss[i];
			    }
				
			    tileset = result;
			    
			    result = new BufferedImage(tileset.getWidth(), tileset.getHeight(), BufferedImage.TYPE_INT_ARGB);
			 
			    int[] data = ((DataBufferInt) result.getRaster().getDataBuffer()).getData();
			    
			    for (int i = 0; i < data.length; i++) {
			    	if ((tss[i] & 0xFF) != 255) {
			    		data[i] = tileset.getColorModel().getRGB(tss[i]);
			    	}
			    }
			    
			    tileset = result;
			    
			    BufferedImage ts = new BufferedImage(tileset.getWidth(), tileset.getHeight(), BufferedImage.TYPE_INT_ARGB);
			    Graphics g = ts.getGraphics();
			    g.drawImage(result, 0, 0, null);
			    
			    int[] pixel = ((DataBufferInt) ts.getRaster().getDataBuffer()).getData();
			    for (int i = 0; i < pixel.length; i++) {
			    	pixel[i] &= 0x50FFFFFF; // Set transparency 
			    }
			    
				// chop the tileset up and add the tiles to a list
				tiles.clear(); // Clear the tiles list, in case another level was already loaded
				inactiveTiles.clear();
				
				int x = 0, y = 0;
				while (y < result.getHeight()) {
					tiles.add(result.getSubimage(x, y, 32, 32));
					inactiveTiles.add(ts.getSubimage(x, y, 32, 32));
					
					x += 32;
					
					if (x == tileset.getWidth()) {
						y += 32;
						x = 0;
					}
				}
			} catch (IOException e) {
				//JOptionPane.showMessageDialog(this, "Couldn't read tileset file.\n'" + s + "'", "Error", JOptionPane.OK_OPTION);
			}
		}
	}
	
	public void setBackground() {
		if (!Settings.BASE_PATH.isEmpty()) {
			try {
				background = ImageIO.read(Data.bgFile);
				
				Data.bgPalette = (IndexColorModel) background.getColorModel();
				Data.bgImg = background;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "Couldn't read background file.\n'" + Data.bgFile.getName()+ "'", "Error", JOptionPane.OK_OPTION);
			}
		}
	}
	
	public void setMap() {
		setBackground();
		setTileset();
	}
	
	private void panView(int newX, int newY) {
		int deltaX = originX - newX;
        int deltaY = originY - newY;
	
		JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
        if (viewPort != null) {
            Rectangle view = viewPort.getViewRect();
            view.x += deltaX;
            view.y += deltaY;

            scrollRectToVisible(view);
            
            Data.mmp.reposition();
        }
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if (Data.map != null) {
			Point2D ml = e.getPoint();
			
			is.transform(ml, ml);
			
			mx = (int) ml.getX();
			my = (int) ml.getY();
			
			if (mouseButton == MouseEvent.BUTTON1) {
				Data.fileChanged = true;
				
				switch (Data.selectedTool) {
				case Data.TOOL_BRUSH:
					if (Data.editMode == Constants.EDIT_MODE_TILES) {
						if (!Data.multiSelectionForeground.isEmpty() || !Data.multiSelectionBackground.isEmpty()) {
							if (!Data.multiSelectionForeground.isEmpty() && (Data.currentLayer == Constants.LAYER_FOREGROUND || Data.currentLayer == Constants.LAYER_BOTH)) {
								int x = 0, y = 0, i = 0;
								while (i < Data.multiSelectionForeground.size()) {
									Data.map.setForegroundTile(((mx + 16) + (x * 32)) - ((Data.sw * 32) / 2), ((my + 16) + (y * 32)) - ((Data.sh * 32) / 2), Data.multiSelectionForeground.get(i));
									
									y++;

									if (y >= Data.sh) {
										x++;
										y = 0;
									}

									i++;
								}
							}
							
							if (!Data.multiSelectionBackground.isEmpty() && (Data.currentLayer == Constants.LAYER_BACKGROUND || Data.currentLayer == Constants.LAYER_BOTH)) {
								int x = 0, y = 0, i = 0;
								while (i < Data.multiSelectionBackground.size()) {
									Data.map.setBackgroundTile(((mx + 16) + (x * 32)) - ((Data.sw * 32) / 2), ((my + 16) + (y * 32)) - ((Data.sh * 32) / 2), Data.multiSelectionBackground.get(i));
									
									y++;

									if (y >= Data.sh) {
										x++;
										y = 0;
									}

									i++;
								}
							}
						} else {
							if (Data.selectedTileForeground != 255) {
								Data.map.setForegroundTile(mx, my, Data.selectedTileForeground);
							} else if (Data.selectedTileBackground != 255) {
								Data.map.setBackgroundTile(mx, my, Data.selectedTileBackground);
							}
						}
					} else if (Data.editMode == Constants.EDIT_MODE_SPRITES) {
						if (Data.selectedSprite != 255 && Data.editMode == Constants.EDIT_MODE_SPRITES) {
							Data.map.sprites[PK2Map.MAP_WIDTH * (mx / 32) + (my / 32)] = Data.selectedSprite;
						}
					}
					break;

				case Data.TOOL_ERASER:
					if (Data.editMode == Constants.EDIT_MODE_TILES) {
						Data.map.setTile(mx, my, 255);
					} else {
						Data.map.sprites[PK2Map.MAP_WIDTH * (mx / 32) + (my / 32)] = 255;
					}
					break;
				}
			} else if (mouseButton == MouseEvent.BUTTON3) {
				if (Data.editMode == Constants.EDIT_MODE_TILES) {
					if (Data.selectedTool == Data.TOOL_BRUSH) {
						mx = (int) ((ml.getX() / 32));
						my = (int) ((ml.getY() / 32));
						
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
						
						Data.selectedTile = -1;
						
						Data.sw += 1;
						Data.sh += 1;
						
						Data.multiSelectLevel = true;
						Data.multiSelectTiles = false;
						
						// Needed to know when the user is dragging, so that the program knows to draw the black/white rectangle
						Data.dragging = true;
					}
				}
			} else if (mouseButton == MouseEvent.BUTTON2) {
				panView(e.getX(), e.getY());
			}
			
			repaint();
			
			if (Data.mmp != null) {
				Data.mmp.repaint();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point2D ml = e.getPoint();
		
		is.transform(ml, ml);
		
		mx = (int) ml.getX();
		my = (int) ml.getY();
		
		if (!Data.multiSelectionBackground.isEmpty() || !Data.multiSelectionForeground.isEmpty() || Data.selectedTileBackground != 255 || Data.selectedTileForeground != 255 || Data.selectedSprite != 255) {
			repaint();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (Data.map != null) {
			mouseButton = e.getButton();
			
			Point2D ml = e.getPoint();
			
			is.transform(ml, ml);
			
			mx = (int) ml.getX();
			my = (int) ml.getY();
			
			if (e.getButton() == MouseEvent.BUTTON1) {
				Data.fileChanged = true;
				
				switch (Data.selectedTool) {
				case Data.TOOL_BRUSH:
					if (Data.editMode == Constants.EDIT_MODE_TILES) {
						if (!Data.multiSelectionForeground.isEmpty() || !Data.multiSelectionBackground.isEmpty()) {
							if (!Data.multiSelectionForeground.isEmpty() && (Data.currentLayer == Constants.LAYER_FOREGROUND || Data.currentLayer == Constants.LAYER_BOTH)) {
								int x = 0, y = 0, i = 0;
								while (i < Data.multiSelectionForeground.size()) {
									Data.map.setForegroundTile(((mx + 16) + (x * 32)) - ((Data.sw * 32) / 2), ((my + 16) + (y * 32)) - ((Data.sh * 32) / 2), Data.multiSelectionForeground.get(i));
									
									y++;

									if (y >= Data.sh) {
										x++;
										y = 0;
									}

									i++;
								}
							}
							
							if (!Data.multiSelectionBackground.isEmpty() && (Data.currentLayer == Constants.LAYER_BACKGROUND || Data.currentLayer == Constants.LAYER_BOTH)) {
								int x = 0, y = 0, i = 0;
								while (i < Data.multiSelectionBackground.size()) {
									Data.map.setBackgroundTile(((mx + 16) + (x * 32)) - ((Data.sw * 32) / 2), ((my + 16) + (y * 32)) - ((Data.sh * 32) / 2), Data.multiSelectionBackground.get(i));
									
									y++;

									if (y >= Data.sh) {
										x++;
										y = 0;
									}

									i++;
								}
							}
						} else {
							if (Data.selectedTileForeground != 255) {
								Data.map.setForegroundTile(mx, my, Data.selectedTileForeground);
							} else if (Data.selectedTileBackground != 255) {
								Data.map.setBackgroundTile(mx, my, Data.selectedTileBackground);
							}
						}
					} else if (Data.editMode == Constants.EDIT_MODE_SPRITES) {
						if (Data.selectedSprite != 255 || Data.editMode == Constants.EDIT_MODE_SPRITES) {
							Data.map.sprites[PK2Map.MAP_WIDTH * (mx / 32) + (my / 32)] = Data.selectedSprite;
						}
					}
					break;

				case Data.TOOL_ERASER:
					if (Data.editMode == Constants.EDIT_MODE_TILES) {
						Data.map.setTile(mx, my, 255);
					} else {
						Data.map.sprites[PK2Map.MAP_WIDTH * (mx / 32) + (my / 32)] = 255;
					}
					break;
				}
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				Data.fileChanged = true;
				
				if (Data.editMode == Constants.EDIT_MODE_TILES) {
					/*
					 * If the user doesn't have a sprite selected they can select one or multiple tiles from the level.
					 * That is being handled in the following code.
					 */
					switch (Data.selectedTool) {
						case Data.TOOL_BRUSH:
							int x = 0, y = 0;
							
							x = (int) ((ml.getX() / 32));
							y = (int) ((ml.getY() / 32));
							
							if (Data.currentLayer == Constants.LAYER_FOREGROUND) {
								Data.selectedTileForeground = Data.map.getTileAt(x * 32, y * 32, Constants.LAYER_FOREGROUND);
								Data.selectedTileBackground = 255; // Need to set the other value to 255 (That means no tile), so that the program knows which tiles it should draw under the mouse cursor
							} else if (Data.currentLayer == Constants.LAYER_BACKGROUND) {
								Data.selectedTileBackground = Data.map.getTileAt(x * 32, y * 32, Constants.LAYER_BACKGROUND);
								Data.selectedTileForeground = 255;
							} else { // If neither of the above is selected it is the layer "both".
								Data.selectedTileForeground = Data.map.getTileAt(x * 32, y * 32, Constants.LAYER_FOREGROUND);
								Data.selectedTileBackground = Data.map.getTileAt(x * 32, y * 32, Constants.LAYER_BACKGROUND);
							}
							
							// Resetting the values for multi select, or rather pretty much just canceling multi select
							Data.sx = x;
							Data.sy = y;
							Data.sw = 0;
							Data.sh = 0;
							
							Data.multiSelectionForeground.clear();
							Data.multiSelectionBackground.clear();
							
							Data.multiSelectLevel = false;
							Data.multiSelectTiles = false;
					}
				} else if (Data.editMode == Constants.EDIT_MODE_SPRITES) {
					if (Data.selectedTool == Data.TOOL_BRUSH) {
						Data.selectedSprite = Data.map.sprites[PK2Map.MAP_WIDTH * (mx / 32) + (my / 32)];
					}
				}
			} else if (e.getButton() == MouseEvent.BUTTON2) {
				originX = (int) e.getX();
				originY = (int) e.getY();
			}
			
			repaint();
			
			// Change this
			if (Data.mmp != null) {
				Data.mmp.repaint();
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// The following code is responsible for adding the selected tiles to the respective lists, depending on the current layer.
		if (Data.dragging) {
			// Setting the mouse position variables, because if you don't the selection will appear at 0, 0, until the user moves the mouse
			Point2D ml = e.getPoint();
			
			is.transform(ml, ml);
			
			mx = (int) ml.getX();
			my = (int) ml.getY();
			
			for (int i = dx; i < dx + Data.sw; i++) {
				for (int j = dy; j < dy + Data.sh; j++) {
					switch (Data.currentLayer) {
						case Constants.LAYER_BOTH:
							Data.multiSelectionForeground.add(Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_FOREGROUND));
							Data.multiSelectionBackground.add(Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_BACKGROUND));
							break;
							
						case Constants.LAYER_FOREGROUND:
							Data.multiSelectionForeground.add(Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_FOREGROUND));
							break;
							
						case Constants.LAYER_BACKGROUND:
							Data.multiSelectionBackground.add(Data.map.getTileAt(i * 32, j * 32, Constants.LAYER_BACKGROUND));
							break;
					}
				}
			}
			
			Data.dragging = false; // User is not dragging anymore, no need to draw the black/white selection rectangle.
			
			repaint();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		pkg.scrollPane2.requestFocus();
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}
	
	public void zoom() {
		af = AffineTransform.getScaleInstance(Data.scale, Data.scale);
		
		try {
			is = af.createInverse();
		} catch (NoninvertibleTransformException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		setPreferredSize(new Dimension((int) ((PK2Map.MAP_WIDTH * 32) * Data.scale), (int) ((PK2Map.MAP_HEIGHT * 32) * Data.scale)));

		invalidate();
		revalidate();
		updateUI();
		repaint();
		
		Data.mmp.reposition();
		Data.mmp.resizeViewportRect();
		Data.mmp.repaint();
		
		pkg.scrollPane2.revalidate();
		pkg.scrollPane2.updateUI();
	}

	private void zoomIn(Point ml) {
		Data.scale *= 1.055f;
		Point pos = pkg.scrollPane2.getViewport().getViewPosition();
		
		/*
		int newX = (int) ((pos.x * Data.scale) + ((pkg.scrollPane2.getVisibleRect().width * Data.scale) / 2));
		int newY = (int) ((pos.y * Data.scale) + ((pkg.scrollPane2.getVisibleRect().height * Data.scale) / 2));
		*/
		
		int newX = (int) (ml.x * (1.055f - 1f) + 1.055f * pos.x);
		int newY = (int) (ml.y * (1.055f - 1f) + 1.055f * pos.y);

		JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
        if (viewPort != null) {
            Rectangle view = viewPort.getViewRect();
            view.x = newX;
            view.y = newY;

            viewPort.setViewPosition(new Point(newX, newY));
            
            revalidate();
            repaint();
            
            Data.mmp.reposition();
        }
		
		/* THIS SHIT AIN'T WORK
		 * 
		 * This is really annoying. This is supposed to make it so that the editor zooms in on the mouse cursors position.
		 * It kinda works, but only when the zoom value is above 1.1f or whatever.
		 * It doesn't work at all, when the zoom value is below 1.
		 * 
		  if (Data.scale > 1) {
			newX = (int) (ml.x * (Data.scale - 1) + ((Data.scale * pos.x) - ((Data.scale - 0.05f) * pos.x)));
			newY = (int) (ml.y * (Data.scale - 1) + ((Data.scale * pos.y) - ((Data.scale - 0.05f) * pos.y)));
		} else {
			newX = (int) (ml.x * (Data.scale - 1) + ((Data.scale * pos.x) - ((Data.scale - 0.05f) * pos.x)));
			newY = (int) (ml.y * (Data.scale - 1) + ((Data.scale * pos.y) - ((Data.scale - 0.05f) * pos.y)));
		}
		 */
		
		if (newX < 0) {
			newX = 0;
		}
		
		if (newY < 0) {
			newY = 0;
		}
	
	    pkg.scrollPane2.getViewport().setViewPosition(new Point(newX, newY));

	    pkg.scrollPane2.revalidate();
	    pkg.scrollPane2.repaint();
	    pkg.scrollPane2.updateUI();
	    
	    repaint();
	}
	
	private void zoomOut(MouseEvent e) {
		Point pos = pkg.scrollPane2.getViewport().getViewPosition();
	    Point ml = e.getPoint();
		
		is.transform(ml, ml);
		
		Data.scale -= 0.055;
		
		if (Data.scale < 0.2) {
			Data.scale = 0.2f;
		}
		
		int newX = (int) ((pos.x * Data.scale) + ((pkg.scrollPane2.getVisibleRect().width * Data.scale) / 2));
		int newY = (int) ((pos.y * Data.scale) + ((pkg.scrollPane2.getVisibleRect().height * Data.scale) / 2));
		
		if (newX < 0) {
			newX = 0;
		}
		
		if (newY < 0) {
			newY = 0;
		}
		
	    pkg.scrollPane2.getViewport().setViewPosition(new Point(newX, newY));

	    pkg.scrollPane2.revalidate();
	    pkg.scrollPane2.repaint();
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		if (e.isControlDown()) {
			if (e.getWheelRotation() > 0) {
				if (Math.abs(Data.scale - 0.1) > 0.1) {
					zoomOut(e);
				}
			} else if (e.getWheelRotation() < 0) {
			    Point ml = e.getPoint();
				
				is.transform(ml, ml);
			    
				zoomIn(ml);
			}
			
			Data.zoomSpinner.setValue((float) (Data.scale * 100));
			
			zoom();
		} else {
			if (e.isAltDown()) {
				if (e.getWheelRotation() > 0) {
					pkg.scrollPane2.getHorizontalScrollBar().setValue(pkg.scrollPane2.getHorizontalScrollBar().getValue() + 32);
				} else if (e.getWheelRotation() < 0) {
					pkg.scrollPane2.getHorizontalScrollBar().setValue(pkg.scrollPane2.getHorizontalScrollBar().getValue() - 32);
				}
			} else {
				if (e.getWheelRotation() > 0) {
					pkg.scrollPane2.getVerticalScrollBar().setValue(pkg.scrollPane2.getVerticalScrollBar().getValue() + 32);
				} else if (e.getWheelRotation() < 0) {
					pkg.scrollPane2.getVerticalScrollBar().setValue(pkg.scrollPane2.getVerticalScrollBar().getValue() - 32);
				}
			}
		}
		
		repaint();
	}

	/*
	@Override
	public void run() {
		while (true) {
			try {
				if (Data.lp != null) {
					ani1++;
			
					if (ani1 > 64) {
						ani1 = 60;
					}
					
					ani2++;
					
					if (ani2 > 69) {
						ani2 = 65;
					}
					
					ani3++;
					
					if (ani3 > 74) {
						ani3 = 70;
					}
					
					ani4++;
					
					if (ani4 > 79) {
						ani4 = 75;
					}
					
					Data.lp.repaint();
				}
				
				Thread.sleep(30 / 2 * 10);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}*/
}