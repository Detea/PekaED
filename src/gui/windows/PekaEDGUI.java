package gui.windows;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.ComponentInputMap;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.ActionMapUIResource;

import data.Constants;
import data.Data;
import data.Settings;
import gui.panels.EpisodePanel;
import gui.panels.LevelPanel;
import gui.panels.MapSettingsPanel;
import gui.panels.MiniMapPanel;
import gui.panels.SpritePanel;
import gui.panels.TilePanel;
import gui.windows.palettewindow.PaletteFrame;
import pekkakana.PK2Map;
import pekkakana.PK2Sprite;

public class PekaEDGUI {
	private JFrame frame;
	
	private LevelPanel lp;
	private TilePanel tp;
	
	private MapSettingsPanel msp;
	private SpritePanel sp;
	private EpisodePanel ep;
	
	private SettingsDialog settingsDialog;
	
	private JComboBox<String> cbEditMode;
	
	public JScrollPane scrollPane2;
	
	public JToggleButton btBrush, btEraser;
	
	private MiniMapPanel mmp;
	
	public void setup() {
		frame = new JFrame("PekaED");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		lp = new LevelPanel();
		tp = new TilePanel(this);
		
		mmp = new MiniMapPanel();
		Data.mmp = mmp;
		
		msp = new MapSettingsPanel();
		sp = new SpritePanel(this);
		
		settingsDialog = new SettingsDialog();
		
		// Needed for loadLevel(). Could have used the Data class, but this code is already messy enough as is.
		ep = new EpisodePanel(this);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		
		JMenuBar menuBar = new JMenuBar();
		
		JMenu mFile = new JMenu("File");
		JMenuItem mifNewLevel = new JMenuItem("New Level");
		JMenuItem mifOpenLevel = new JMenuItem("Open Level");
		JMenuItem mifSaveLevel = new JMenuItem("Save level");
		JMenuItem mifSaveLevelAs = new JMenuItem("Save Level as...");
		JMenuItem mifNewEpisode = new JMenuItem("New Episode");
		JMenuItem mifOpenEpisode = new JMenuItem("Open Episode");
		JMenuItem mifSaveEpisode = new JMenuItem("Save Episode");
		
		JMenuItem mifImportEpisode = new JMenuItem("Import Episode");
		
		mifNewLevel.setAccelerator(KeyStroke.getKeyStroke('N', KeyEvent.CTRL_DOWN_MASK));
		mifOpenLevel.setAccelerator(KeyStroke.getKeyStroke('O', KeyEvent.CTRL_DOWN_MASK));
		mifSaveLevel.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK));
		mifSaveLevelAs.setAccelerator(KeyStroke.getKeyStroke('S', KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK));
		
		mFile.add(mifNewLevel);
		mFile.add(mifOpenLevel);
		mFile.add(mifSaveLevel);
		mFile.add(mifSaveLevelAs);
		mFile.add(new JSeparator());
		mFile.add(mifNewEpisode);
		mFile.add(mifOpenEpisode);
		mFile.add(mifSaveEpisode);
		mFile.add(new JSeparator());
		mFile.add(mifImportEpisode);
		
		mifNewLevel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				newLevel();
			}
		
		});

		mifOpenLevel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				showLoadDialog();
			}
		
		});
		
		mifSaveLevel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveLevel(Data.currentFile);
			}
		
		});
		
		mifSaveLevelAs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				saveLevelAs();
			}
		
		});
		
		mifNewEpisode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser(Settings.EPISODES_PATH);
				fc.setDialogTitle("Create a new episode...");
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				int res = fc.showSaveDialog(frame);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					fc.getSelectedFile().mkdir();
					
					Data.currentEpisodePath = fc.getSelectedFile().getAbsolutePath();
					
					File episodeFile = new File(fc.getSelectedFile().getAbsolutePath() + File.separatorChar + fc.getSelectedFile().getName() + ".episode");
		
					try {
						BufferedWriter w = new BufferedWriter(new FileWriter(episodeFile));
						
						w.write(fc.getSelectedFile().getName() + "\n");
						w.write(Data.currentEpisodePath + "\n");
						
						w.flush();
						w.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					Data.currentEpisodeFile = episodeFile;
					
					ep.newEpisode(fc.getSelectedFile().getName());
					
					setFrameTitle();
					
					tabbedPane.setSelectedIndex(2);
				}
			}
		
		});
		
		mifOpenEpisode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Load an episode");
				
				fc.setCurrentDirectory(new File(Settings.EPISODES_PATH));
				
				fc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().toLowerCase().endsWith("episode");
					}

					@Override
					public String getDescription() {
						return "Pekka Kana 2 Episode file";
					}
					
				});
				
				int res = fc.showSaveDialog(frame);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					ep.loadEpisode(fc.getSelectedFile());
				
					if (!Data.episodeFiles.isEmpty()) {
						loadLevel(Data.episodeFiles.get(0).getAbsolutePath());
					}
					
					tabbedPane.setSelectedIndex(2);
				}
			}
		
		});
		
		mifSaveEpisode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				ep.saveEpisode();
			}
			
		});

		mifImportEpisode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				importEpisode();
			}
			
		});
		
		JMenu mExtras = new JMenu("Extras");
		JMenuItem mieSettings = new JMenuItem("Settings");
		JMenuItem miePalette = new JMenuItem("Palette");
		JMenuItem mieAbout = new JMenuItem("About");
		JMenuItem mieHelp = new JMenuItem("Help");
		
		mieSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				settingsDialog.showDialog();
			}
		
		});
		
		miePalette.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new PaletteFrame();
			}
		
		});
		
		mieAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AboutFrame();
			}
		
		});
		
		mieHelp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Desktop.isDesktopSupported()) {
					try {
						Desktop.getDesktop().browse(new URI("https://detea.github.io/pekaed"));
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					} catch (URISyntaxException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			
		});
		
		mExtras.add(mieSettings);
		mExtras.add(miePalette);
		mExtras.add(mieAbout);
		mExtras.add(mieHelp);
		
		menuBar.add(mFile);
		menuBar.add(mExtras);
		
		frame.setJMenuBar(menuBar);
		
		JToolBar toolbar = new JToolBar();
		JButton bNewMap = new JButton();
		JButton bLoadMap = new JButton();
		JButton bSaveMap = new JButton();
		JButton bSaveAsMap = new JButton();
        JButton bTestLevel = new JButton();
		
		bNewMap.setToolTipText("New Level (Ctrl+N)");
		bLoadMap.setToolTipText("Load Level (Ctrl+O)");
		bSaveMap.setToolTipText("Save Level (Ctrl+S)");
		bSaveAsMap.setToolTipText("Save Level As... (Ctrl+Shift+S)");
        bTestLevel.setToolTipText("Test Level (F5)");
	
		bNewMap.setIcon(new ImageIcon(getClass().getResource("/document-new.png")));
		bLoadMap.setIcon(new ImageIcon(getClass().getResource("/document-open.png")));
		bSaveMap.setIcon(new ImageIcon(getClass().getResource("/document-save.png")));
		bSaveAsMap.setIcon(new ImageIcon(getClass().getResource("/document-save-as.png")));
        bTestLevel.setIcon(new ImageIcon(getClass().getResource("/play.png")));
		
		bNewMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Data.fileChanged) {
					if (showSaveWarning() != 2) {
						newLevel();
					}
				} else {
					newLevel();
				}
			}
			
		});
		
		bLoadMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Data.fileChanged) {
					if (showSaveWarning() != 2) {
						showLoadDialog();
					}
				} else {
					showLoadDialog();
				}
			}
			
		});
		
		bSaveMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.currentFile == null) {
					saveLevelAs();
				} else {
					saveLevel(Data.currentFile);
				}
			}
			
		});
		
		bSaveAsMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				saveLevelAs();
			}
			
		});
                
		bTestLevel.addActionListener(new ActionListener() {
                    
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(Data.currentFile != null){
					if (Data.fileChanged) {
						int saveRes = showSaveWarning();
						
						if (saveRes == 0) {
							saveLevel(Data.currentFile);
							
							Data.fileChanged = false;
						}
						
						if (saveRes != 2) {
							testLevel();
						}
					} else {
						testLevel();
					}
				} else {
					int saveRes = showSaveWarning();
					
					if (saveRes == 0) {
						saveLevel(Data.currentFile);
					};

					if (saveRes != 2) {
						testLevel();
					}
				}
			}

		});
		
		toolbar.add(bNewMap);
		toolbar.add(bLoadMap);
		toolbar.add(bSaveMap);
		toolbar.add(bSaveAsMap);
		toolbar.add(bTestLevel);
		
		JComboBox<String> cbLayers = new JComboBox<String>();
		cbLayers.addItem("Both");
		cbLayers.addItem("Foreground");
		cbLayers.addItem("Background");
		
		cbLayers.setSelectedIndex(1);
		
		Data.currentLayer = 0;
		
		cbLayers.setMaximumSize(new Dimension(100, 25));
		cbLayers.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch (cbLayers.getSelectedIndex()) {
					case 0:
						Data.currentLayer = 2; // both
						Data.lp.repaint();
						break;
						
					case 1:
						Data.currentLayer = 0; // foreground
						
						if (Data.selectedTileForeground == 255 && Data.selectedTileBackground != 255) {
							Data.selectedTileForeground = Data.selectedTileBackground;
							Data.selectedTileBackground = 255;
						}
						
						if (Data.multiSelectTiles && Data.multiSelectionForeground.isEmpty() && !Data.multiSelectionBackground.isEmpty()) {
							Data.multiSelectionForeground.addAll(Data.multiSelectionBackground);
							Data.multiSelectionBackground.clear();
						}
						
						Data.lp.repaint();
						break;
						
					case 2:
						Data.currentLayer = 1; // background
						
						if (Data.selectedTileBackground == 255 && Data.selectedTileForeground != 255) {
							Data.selectedTileBackground = Data.selectedTileForeground;
							Data.selectedTileForeground = 255;
						}
						
						if (Data.multiSelectTiles && Data.multiSelectionBackground.isEmpty() && !Data.multiSelectionForeground.isEmpty()) {
							Data.multiSelectionBackground.addAll(Data.multiSelectionForeground);
							Data.multiSelectionForeground.clear();
						}
						
						Data.lp.repaint();
						break;
				}
			}
			
		});
		
		JLabel lblLayer = new JLabel("Layer:  ");
		
		toolbar.addSeparator();
		
		btBrush = new JToggleButton("Brush");
		JToggleButton btFloodFill = new JToggleButton("FloodFil");
		btEraser = new JToggleButton("Eraser");
		
		btBrush.setMnemonic('W');
		btEraser.setMnemonic('E');
		
		Data.selectedTool = Data.TOOL_BRUSH;
		btBrush.setSelected(true);
		
		btBrush.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Data.selectedTool = Data.TOOL_BRUSH;
				
				btFloodFill.setSelected(false);
				btEraser.setSelected(false);
			}
			
		});
		
		btFloodFill.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Data.selectedTool = Data.TOOL_FLOODFILL;
				
				btBrush.setSelected(false);
				btEraser.setSelected(false);
			}
			
		});
		
		btEraser.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Data.selectedTool = Data.TOOL_ERASER;
				
				btFloodFill.setSelected(false);
				btBrush.setSelected(false);
			}
			
		});
		
		toolbar.add(btBrush);
		toolbar.add(btEraser);
		//toolbar.add(btFloodFill);
	
		JToggleButton btShowSprites = new JToggleButton("Show Sprites");

		btShowSprites.setSelected(true);
		btShowSprites.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.showSprites) {
					Data.showSprites = false;
				} else {
					Data.showSprites = true;
				}
			}
			
		});
		
		JToggleButton btnHighlightSprites = new JToggleButton("Highlight Sprites");
		btnHighlightSprites.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.showSpriteRect) {
					Data.showSpriteRect = false;
				} else {
					Data.showSpriteRect = true;
				}
				
				Data.lp.repaint();
			}
			
		});
		
		Vector<String> ml = new Vector<String>();
		ml.add("Tile Mode");
		ml.add("Sprite Mode");
		cbEditMode = new JComboBox<String>(ml);
		cbEditMode.setMaximumSize(new Dimension(100, 25));
		
		JLabel lblEditMode = new JLabel("Mode: ");
		
		cbEditMode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (cbEditMode.getSelectedIndex() == 0) {
					Data.editMode = Constants.EDIT_MODE_TILES;
				} else if (cbEditMode.getSelectedIndex() == 1) {
					Data.editMode = Constants.EDIT_MODE_SPRITES;
				}
				
				Data.lp.repaint();
			}
			
		});
		
		toolbar.add(btShowSprites);
		toolbar.add(btnHighlightSprites);
		toolbar.addSeparator();
		toolbar.add(lblLayer); 
		toolbar.add(cbLayers);
		toolbar.addSeparator(new Dimension(5, 0));
		toolbar.add(lblEditMode);
		toolbar.add(cbEditMode);
		
		tabbedPane.addTab("Properties", msp);
		tabbedPane.addTab("Sprites", sp);
		tabbedPane.addTab("Episode", ep);
		
		tabbedPane.setMinimumSize(new Dimension(280, 400));
		tabbedPane.setPreferredSize(new Dimension(280, 400));
		
		JScrollPane scrollPane1 = new JScrollPane(tp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane2 = new JScrollPane(lp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane1, scrollPane2);
		
		//scrollPane2.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		scrollPane2.getVerticalScrollBar().setUnitIncrement(32);
		scrollPane2.getHorizontalScrollBar().setUnitIncrement(32);
		
		splitPane.setDividerLocation(320);

		GridBagLayout gbl = new GridBagLayout();
		
		JPanel sidePanel = new JPanel();
		sidePanel.setLayout(gbl);
		GridBagConstraints c = new GridBagConstraints();
		sidePanel.add(tabbedPane);
		c.fill = GridBagConstraints.VERTICAL;
		c.gridx = 1;
		c.gridy = 0;
		c.weighty = 1.0;
		gbl.setConstraints(tabbedPane, c);
	
		sidePanel.add(mmp);
		c.fill = GridBagConstraints.NONE;
		c.gridx = 1;
		c.gridy = 1;
		c.weighty = 0.5;
		gbl.setConstraints(mmp, c);
		
		frame.add(toolbar, BorderLayout.NORTH);
		frame.add(splitPane, BorderLayout.CENTER);
		frame.add(sidePanel, BorderLayout.EAST);
		
		ActionMap actionMap = new ActionMapUIResource();
		actionMap.put("saveAction", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.currentFile == null) {
					JFileChooser fc = new JFileChooser(Settings.EPISODES_PATH);
					
					fc.setDialogTitle("Save level");
					
					fc.setAcceptAllFileFilterUsed(false);
					
					fc.setFileFilter(new FileFilter() {

						@Override
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith(".map") && f.getName().length() < 39;
						}

						@Override
						public String getDescription() {
							return "Pekka Kana 2 level";
						}
						
					});
					
					int res = fc.showSaveDialog(frame);
					
					if (res == JFileChooser.APPROVE_OPTION) {
						saveLevel(fc.getSelectedFile());
					}
				} else {
					if (Data.fileChanged) {
						saveLevel(Data.currentFile);
					}
				}
			}
			
		});
		
		actionMap.put("saveAsAction", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = null;
				fc = new JFileChooser();
				
				if (Data.currentFile != null) {
					fc.setCurrentDirectory(Data.currentFile.getParentFile());
					fc.setSelectedFile(Data.currentFile);
				}
				
				fc.setDialogTitle("Save level as...");
				
				fc.setAcceptAllFileFilterUsed(false);
				
				fc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						return f.isDirectory() || f.getName().endsWith(".map") && f.getName().length() < 39;
					}

					@Override
					public String getDescription() {
						return "Pekka Kana 2 level";
					}
					
				});
				
				int res = fc.showSaveDialog(frame);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					saveLevel(fc.getSelectedFile());
				}
			}
			
		});
		
		actionMap.put("loadAction", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				showLoadDialog();
			}
		});

		actionMap.put("newAction", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				newLevel();
			}
		});
		
		actionMap.put("layerAction1", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				cbLayers.setSelectedIndex(0);
				Data.currentLayer = 2;
				
				Data.lp.repaint();
			}
			
		});
		
		actionMap.put("layerAction2", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				cbLayers.setSelectedIndex(1);
				Data.currentLayer = 0;
				
				Data.lp.repaint();
			}
			
		});
		
		actionMap.put("layerAction3", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				cbLayers.setSelectedIndex(2);
				Data.currentLayer = 1;
				
				Data.lp.repaint();
			}
			
		});
		
		actionMap.put("addSpriteAction", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				tabbedPane.setSelectedIndex(1);
				sp.addSprite();
			}
			
		});
		
		actionMap.put("selectBrush", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Data.selectedTool = Data.TOOL_BRUSH;
				btEraser.setSelected(false);
				btBrush.setSelected(true);
			}
			
		});
		
		actionMap.put("selectEraser", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Data.selectedTool = Data.TOOL_ERASER;
				btBrush.setSelected(false);
				btEraser.setSelected(true);
			}
			
		});
		
		actionMap.put("showSprites", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.showSprites) {
					Data.showSprites = false;
					btShowSprites.setSelected(false);
				} else {
					Data.showSprites = true;
					btShowSprites.setSelected(true);
				}
			}
			
		});
		
		actionMap.put("zoomInAction", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Data.scale += 0.02;
				
				Data.mmp.resizeViewportRect();
				Data.mmp.repaint();
				
				//Dimension d = Data.lp.getPreferredSize();
				
				//scrollPane2.getViewport().setViewSize(new Dimension((int) (d.width * (Data.scale * -1)), (int) (d.height * (Data.scale * -1))));
				
				scrollPane2.revalidate();
				
				Data.lp.repaint();
			}
			
		});
		
		actionMap.put("zoomOutAction", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.scale - 0.1 > 0.1) {
					Data.scale -= 0.1;
					
					Data.mmp.resizeViewportRect();
					Data.mmp.repaint();
					
					//Dimension d = Data.lp.getPreferredSize();
					
					//scrollPane2.getViewport().setViewSize(new Dimension((int) (d.width / Data.scale), (int) (d.height / Data.scale)));
					
					scrollPane2.revalidate();
					
					Data.lp.repaint();
				}
			}
			
		});
		
		actionMap.put("testLevel", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				testLevel();
			}
			
		});
		
		actionMap.put("editModeTiles", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setEditMode(Constants.EDIT_MODE_TILES);
			}
			
		});
		
		actionMap.put("editModeSprites", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setEditMode(Constants.EDIT_MODE_SPRITES);
			}
			
		});
		
		actionMap.put("showSpriteRect", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.editMode == Constants.EDIT_MODE_SPRITES) {
					if (Data.showSpriteRect) {
						Data.showSpriteRect = false;
					} else {
						Data.showSpriteRect = true;
					}
					
					btnHighlightSprites.setSelected(Data.showSpriteRect);
					
					Data.lp.repaint();
				}
			}
			
		});
		
		InputMap keyMap = new ComponentInputMap((JComponent) frame.getContentPane());
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK), "saveAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK), "loadAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK), "newAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK), "addSpriteAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK), "saveAsAction");
		keyMap.put(KeyStroke.getKeyStroke("1"), "layerAction1");
		keyMap.put(KeyStroke.getKeyStroke("2"), "layerAction2");
		keyMap.put(KeyStroke.getKeyStroke("3"), "layerAction3");
		
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_PLUS, Event.CTRL_MASK), "zoomInAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_MINUS, Event.CTRL_MASK), "zoomOutAction");
		
		keyMap.put(KeyStroke.getKeyStroke("F5"), "testLevel");
		
		keyMap.put(KeyStroke.getKeyStroke("E"), "selectBrush");
		keyMap.put(KeyStroke.getKeyStroke("R"), "selectEraser");
		keyMap.put(KeyStroke.getKeyStroke("S"), "showSprites");

		keyMap.put(KeyStroke.getKeyStroke("H"), "showSpriteRect");
		
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_1, Event.CTRL_MASK), "editModeTiles");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_2, Event.CTRL_MASK), "editModeSprites");
		
		SwingUtilities.replaceUIActionMap((JComponent) frame.getContentPane(), actionMap);
		SwingUtilities.replaceUIInputMap((JComponent) frame.getContentPane(),  JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);
		
		frame.addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosing(WindowEvent e) {
				boolean quit = false;
				
				if (Data.currentEpisodeFile != null) {
					if (Data.episodeChanged) {
						int res = JOptionPane.showConfirmDialog(frame, "Episode has changed. Do you want to save the changes?", "Save episode?", JOptionPane.YES_NO_CANCEL_OPTION);
						
						if (res == JOptionPane.YES_OPTION) {
							ep.saveEpisode();
							
							quit = true;
						} else if (res == JOptionPane.NO_OPTION) {
							quit = true;
						} else if (res == JOptionPane.CANCEL_OPTION) {
							quit = false;
						}
					} else {
						quit = true;
					}
				}
				
				if (Data.fileChanged) {
					if (showSaveWarning() != 2) quit = true;
				} else {
					quit = true;
				}
				
				if (quit) {
					Data.runThread = false;
					
					
					if (Settings.loadEpisodeOnStartup && Data.currentEpisodeFile != null) {
						// Maybe store path to last used episode in settings file?
						
						try {
							DataOutputStream dos = new DataOutputStream(new FileOutputStream("lastepisode"));
							
							dos.writeUTF(Data.currentEpisodePath + File.separatorChar + Data.currentEpisodeFile.getName());
							
							dos.flush();
							dos.close();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					
					System.exit(0);
				}
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		frame.addComponentListener(new ComponentListener() {

			@Override
			public void componentHidden(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentMoved(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void componentResized(ComponentEvent c) {
				mmp.resizeViewportRect();
			}

			@Override
			public void componentShown(ComponentEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		Data.lp = lp;
		Data.tp = tp;
		
		Image img = null;
		try {
			img = ImageIO.read(getClass().getResource("/pkedit.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (Settings.loadEpisodeOnStartup) {
			try {
				if (new File("lastepisode").exists()) {
					DataInputStream dis = new DataInputStream(new FileInputStream("lastepisode"));
					String episodePath = dis.readUTF();
					
					if (!episodePath.isEmpty()) {
						File f = new File(episodePath);
						
						Data.currentEpisodeFile = f;
						Data.currentEpisodePath = f.getParent();
						
						ep.loadEpisode(Data.currentEpisodeFile);
						
						if (!Data.episodeFiles.isEmpty()) {
							loadLevel(Data.episodeFiles.get(Data.episodeFiles.size() - 1).getAbsolutePath());
							
							setFrameTitle();
						}
					}
				}
			} catch (FileNotFoundException e1) {
				// log this
				//e1.printStackTrace();
			} catch (IOException e1) {
				// log this
				//e1.printStackTrace();
			}
		} else {
			newLevel();
		}
		
		frame.setIconImage(img);
		
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.pack();
		frame.setVisible(true);
		
		frame.setSize(new Dimension(1280, 720));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		lp.setPekaGUI(this);
		mmp.setPekaGUI(this);
	}
	
	public void setToolButton() {
		if (Data.selectedTool == Data.TOOL_BRUSH) {
			btBrush.setSelected(true);
			btEraser.setSelected(false);
			btBrush.requestFocus();
		} else if (Data.selectedTool == Data.TOOL_ERASER) {
			btBrush.setSelected(false);
			btEraser.setSelected(true);
		}
	}
	
	private void setEditorMode(int mode) {
		if (mode == 0) {
			Data.mode = Constants.MODE_LEGACY;
			Data.EPISODE_LEVEL_LIMIT = Constants.LEGACY_LEVEL_LIMIT;
		} else if (mode == 1) {
			Data.mode = Constants.MODE_ENHANCED;
			Data.EPISODE_LEVEL_LIMIT = Constants.ENHANCED_LEVEL_LIMIT;
		}
	}
	
	private void testLevel() {
		String cmd = Settings.BASE_PATH + File.separatorChar + "pk2.exe";
		String args = "dev test \"" + Data.currentFile.getParentFile().getName() + "/" + Data.currentFile.getName() + "\"";
		try{
			Runtime runTime = Runtime.getRuntime();
			Process process = runTime.exec(cmd + " " + args);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void importEpisode() {
		JFileChooser fc = new JFileChooser("Import an episode...");
		fc.setCurrentDirectory(new File(Settings.EPISODES_PATH));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int res = fc.showOpenDialog(frame);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			ep.newEpisode(fc.getSelectedFile().getName());
			Data.currentEpisodePath = fc.getSelectedFile().getAbsolutePath();

			for (File f : fc.getSelectedFile().listFiles()) {
				if (f.getName().toLowerCase().endsWith("map")) {
					ep.importLevel(f);
				}
			}
			
			ep.saveEpisode();
		}
	}
	
	public void showLoadDialog() {
		JFileChooser fc = new JFileChooser(Settings.EPISODES_PATH);
		
		fc.setFileFilter(new FileNameExtensionFilter("Pekka Kana 2 Level", "map", "MAP"));
		
		int res = fc.showOpenDialog(frame);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			loadLevel(fc.getSelectedFile().getAbsolutePath());
		}
	}
	
	public void loadLevel(String file) {
		Data.map = new PK2Map(file);
		
		Data.currentFile = new File(file);
		
		lp.setMap();
		tp.setTileset(Data.map.getTileset());
		msp.setMap();
		sp.setMap();
		
		lp.repaint();
		mmp.repaint();
		
		Rectangle r = Data.map.calculateUsedArea(Data.map.layers[Constants.LAYER_BACKGROUND], "background2");
		
		scrollPane2.getVerticalScrollBar().setValue((r.y - (r.height / 2)) * 32);
		scrollPane2.getHorizontalScrollBar().setValue((r.x - (r.width / 2)) * 32);
		
		Data.fileChanged = false;
		
		setFrameTitle();
	}
	
	private void saveLevelAs() {
		JFileChooser fc = new JFileChooser();
		fc.setDialogTitle("Save as...");
		
		fc.setFileFilter(new FileNameExtensionFilter("Pekka Kana 2 Level", "map", "MAP"));
		
		if (Data.currentFile == null) {
			fc.setSelectedFile(new File(Settings.EPISODES_PATH));
		} else {
			fc.setSelectedFile(Data.currentFile);
		}
		
		int res = fc.showSaveDialog(frame);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			saveLevel(fc.getSelectedFile());
		}
	}
	
	public void newLevel() {
		PK2Sprite psprite = new PK2Sprite("rooster.spr");
		Data.map = new PK2Map();
		Data.map.addSprite(psprite, psprite.filename);
		Data.map.levelNumber = Data.episodeFiles.size() + 1;
		sp.setList();
		
		Data.currentFile = null;
		
		setFrameTitle();
		
		Data.selectedTile = 0;
		
		msp.setMap();
		sp.setMap();
		
		lp.setMap();
		tp.setTileset(Data.map.getTileset());
		
		lp.repaint();
		mmp.repaint();
		
		if (Data.currentEpisodeFile != null) {
			int res = JOptionPane.showConfirmDialog(frame, "Do you want to add this file to the episode '" + Data.currentEpisodeName + "'?", "Add file to episode?", JOptionPane.YES_NO_OPTION);
			
			if (res == JOptionPane.YES_OPTION) {
				if (showAddToEpisodeSave()) {
					Data.map.saveFile();
					ep.importLevel(Data.currentFile);
					ep.setSelectedLevel(Data.episodeFiles.size() - 1);
					Data.currentFile = new File(Data.episodeFiles.get(Data.episodeFiles.size() - 1).getAbsolutePath());
					
					setFrameTitle();
				} else {
					setFrameTitle();
				}
			}
		}
		
		Data.fileChanged = false;
	}
	
	private void saveLevel(File file) {
		if (file.exists())
			file.delete();
		
		if (!file.getName().endsWith("map")) {
			file = new File(file.getAbsolutePath() +  ".map");
		}
		
		msp.saveChanges();
		
		Data.map.file = file;
		Data.map.saveFile();
		
		Data.currentFile = file;
		
		setFrameTitle();
	}
	
	private int showSaveWarning() {
		int op = JOptionPane.showConfirmDialog(frame, "File has changed. Do you want to save?", "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		// Replacing the numbers with constants would be cleaner
		
		int quit = 0;
		
		if (op == JOptionPane.YES_OPTION) {
			if (Data.currentFile == null) {
				showSaveDialog();
			} else {
				saveLevel(Data.currentFile);
			}
			
			quit = 0;
		} else if (op == JOptionPane.NO_OPTION) {
			quit = 1;
		} else if (op == JOptionPane.CANCEL_OPTION) {
			quit = 2;
		}
		
		return quit;
	}
	
	private boolean showAddToEpisodeSave() {
		JFileChooser fc = new JFileChooser("Add level to current episode...");
		
		if (Data.currentEpisodeFile != null) {
			fc.setCurrentDirectory(new File(Data.currentEpisodePath));
		}
		
		fc.setDialogTitle("Save level");
		
		fc.setAcceptAllFileFilterUsed(false);
		
		fc.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith("map") && f.getName().length() < 39;
			}

			@Override
			public String getDescription() {
				return "Pekka Kana 2 level";
			}
			
		});
		
		int res = fc.showSaveDialog(frame);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			saveLevel(fc.getSelectedFile());
			
			return true;
		}
		
		return false;
	}
	
	private boolean showSaveDialog() {
		JFileChooser fc = new JFileChooser(Settings.EPISODES_PATH);
		
		fc.setDialogTitle("Save level");
		
		fc.setAcceptAllFileFilterUsed(false);
		
		fc.setFileFilter(new FileFilter() {

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith("map") && f.getName().length() < 39;
			}

			@Override
			public String getDescription() {
				return "Pekka Kana 2 level";
			}
			
		});
		
		int res = fc.showSaveDialog(frame);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			Data.currentFile = fc.getSelectedFile();
			saveLevel(fc.getSelectedFile());
			
			return true;
		}
		
		return false;
	}
	
	public void setFrameTitle() {
		if (Data.currentEpisodeFile != null && Data.currentFile != null) {
			frame.setTitle(Data.currentEpisodeName + " - " + Data.currentFile.getName() + " - PekaED");
		} else if (Data.currentFile != null) {
			frame.setTitle(Data.currentFile.getName() + " - PekaED");
		} else {
			frame.setTitle("Untitled - PekaED");
		}
	}

	public void setEditMode(int editMode) {
		Data.editMode = editMode;
		
		if (editMode == Constants.EDIT_MODE_TILES) {
			cbEditMode.setSelectedIndex(0);
		} else if (editMode == Constants.EDIT_MODE_SPRITES) {
			cbEditMode.setSelectedIndex(1);
		}
	}
}
