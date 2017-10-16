package gui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
import pekkakana.PK2Map;
import pekkakana.PK2Sprite;

public class PekaEDGUI {
	private JFrame frame;
	
	private LevelPanel lp;
	private TilePanel tp;
	
	private MapSettingsPanel msp;
	private SpritePanel sp;
	private EpisodePanel ep;
	
	private JScrollPane scrollPane2;
	
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
		tp = new TilePanel();
		
		msp = new MapSettingsPanel();
		sp = new SpritePanel();
		
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
				JFileChooser fc = new JFileChooser();
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					Data.currentEpisodeFile = episodeFile;
					
					ep.newEpisode(fc.getSelectedFile().getName());
					
					tabbedPane.setSelectedIndex(2);
				}
			}
		
		});
		
		mifOpenEpisode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Load an episode");
				
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
		JMenuItem mieAbout = new JMenuItem("About");
		JMenuItem mieHelp = new JMenuItem("Help");
		
		mieSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new SettingsDialog(); // The user can open the settings dialog multiple times, may not want that
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
		
		bNewMap.setToolTipText("New Level");
		bLoadMap.setToolTipText("Load Level");
		bSaveMap.setToolTipText("Save Level");
		bSaveAsMap.setToolTipText("Save Level As...");
        bTestLevel.setToolTipText("Test Level");
	
		bNewMap.setIcon(new ImageIcon(getClass().getResource("/document-new.png")));
		bLoadMap.setIcon(new ImageIcon(getClass().getResource("/document-open.png")));
		bSaveMap.setIcon(new ImageIcon(getClass().getResource("/document-save.png")));
		bSaveAsMap.setIcon(new ImageIcon(getClass().getResource("/document-save-as.png")));
        bTestLevel.setIcon(new ImageIcon(getClass().getResource("/play.png")));
		
		bNewMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (Data.fileChanged) {
					if (showSaveWarning()) {
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
					if (showSaveWarning()) {
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
                
		bTestLevel.addActionListener(new ActionListener(){
                    
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(Data.currentFile != null){
					if (Data.fileChanged) {
						if (showSaveWarning()) {
							saveLevel(Data.currentFile);
							
							Data.fileChanged = false;
						} else return;
					}

					testLevel();
				} else {
					if (showSaveWarning()) {
						saveLevel(Data.currentFile);
					} else return;

					testLevel();
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
		
		cbLayers.setMaximumSize(new Dimension(100, 25));
		cbLayers.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				switch (cbLayers.getSelectedIndex()) {
					case 0:
						Data.currentLayer = 2;
						break;
						
					case 1:
						Data.currentLayer = 0;
						break;
						
					case 2:
						Data.currentLayer = 1;
						break;
				}
			}
			
		});
		
		JLabel lblLayer = new JLabel("Layer:  ");
		
		toolbar.addSeparator();
		
		JToggleButton btBrush = new JToggleButton("Brush");
		JToggleButton btFloodFill = new JToggleButton("FloodFil");
		JToggleButton btEraser = new JToggleButton("Eraser");
		
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
		
		JLabel lblMode = new JLabel("Mode:  ");
		
		Vector<String> modeList = new Vector<String>();
		modeList.addElement("Legacy");
		modeList.addElement("Enhanced");
		JComboBox<String> cbMode = new JComboBox<String>(modeList);
		cbMode.setMaximumSize(new Dimension(100, 25));

		if (Settings.startInEnhancedMode) {
			setEditorMode(1);
			cbMode.setSelectedIndex(1);
		} else {
			setEditorMode(0);
			cbMode.setSelectedIndex(0);
		}
		
		cbMode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				setEditorMode(cbMode.getSelectedIndex());
			}
			
		});
		
		toolbar.add(btShowSprites);
		toolbar.addSeparator();
		toolbar.add(lblLayer); 
		toolbar.add(cbLayers);
		toolbar.addSeparator(new Dimension(10, 0));
		toolbar.add(lblMode);
		toolbar.add(cbMode);
		
		tabbedPane.addTab("Properties", msp);
		tabbedPane.addTab("Sprites", sp);
		tabbedPane.addTab("Episode", ep);
		
		tabbedPane.setPreferredSize(new Dimension(256, 600));
		
		JScrollPane scrollPane1 = new JScrollPane(tp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scrollPane2 = new JScrollPane(lp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane1, scrollPane2);
	
		splitPane.setDividerLocation(320);
		
		frame.add(toolbar, BorderLayout.NORTH);
		frame.add(splitPane, BorderLayout.CENTER);
		frame.add(tabbedPane, BorderLayout.EAST);
		
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
			}
			
		});
		
		actionMap.put("layerAction2", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				cbLayers.setSelectedIndex(1);
				Data.currentLayer = 0;
			}
			
		});
		
		actionMap.put("layerAction3", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				cbLayers.setSelectedIndex(2);
				Data.currentLayer = 1;
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
		
		InputMap keyMap = new ComponentInputMap((JComponent) frame.getContentPane());
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK), "saveAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK), "loadAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK), "newAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK), "addSpriteAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK | Event.SHIFT_MASK), "saveAsAction");
		keyMap.put(KeyStroke.getKeyStroke("1"), "layerAction1");
		keyMap.put(KeyStroke.getKeyStroke("2"), "layerAction2");
		keyMap.put(KeyStroke.getKeyStroke("3"), "layerAction3");
		
		keyMap.put(KeyStroke.getKeyStroke("E"), "selectBrush");
		keyMap.put(KeyStroke.getKeyStroke("R"), "selectEraser");
		keyMap.put(KeyStroke.getKeyStroke("S"), "showSprites");
		
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
				
				if (Data.fileChanged) {
					quit = showSaveWarning();
				} else {
					quit = true;
				}
				
				if (Data.episodeChanged) {
					int res = JOptionPane.showConfirmDialog(frame, "Episode has changed. Do you want to save the changes?", "Save episode?", JOptionPane.YES_NO_CANCEL_OPTION);
					
					if (res == JOptionPane.YES_OPTION) {
						ep.saveEpisode();
						quit = true;
						
					} else if (res == JOptionPane.NO_OPTION) {
						quit = true;
					} else {
						quit = false;
					}
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
		
		newLevel();
		
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
							setFrameTitle(Data.episodeFiles.get(Data.episodeFiles.size() - 1).getAbsolutePath());
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
		}
		
		frame.setIconImage(img);
		
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.pack();
		frame.setVisible(true);
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
		System.out.println(Data.currentFile.getParentFile().getName() + File.separatorChar + Data.currentFile.getName());
		
		String cmd =  Settings.BASE_PATH + File.separatorChar + "pk2.exe";
		String args = "dev test " + Data.currentFile.getParentFile().getName() + "///" + Data.currentFile.getName();
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
		
		tp.setTileset(Data.map.getTileset());
		lp.setMap();
		msp.setMap();
		sp.setMap();
		
		Rectangle r = Data.map.calculateUsedArea(Data.map.layers[Constants.LAYER_BACKGROUND]);
		
		scrollPane2.getVerticalScrollBar().setValue(r.y * 32);
		scrollPane2.getHorizontalScrollBar().setValue(r.x * 32);
		
		Data.fileChanged = false;

		setFrameTitle(Data.currentFile.getName());
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
	
	public void createEmptyLevel() {
		Data.map = new PK2Map();
		msp.setMap();
		sp.setMap();
		
		tp.setTileset(Data.map.getTileset());
		lp.setMap();
		
		setFrameTitle("Untitled");
	}
	
	public void newLevel() {
		Data.fileChanged = false;
		
		PK2Sprite psprite = new PK2Sprite("rooster.spr");
		Data.map = new PK2Map();
		Data.map.addSprite(psprite, psprite.filename);
		Data.map.levelNumber = Data.episodeFiles.size() + 1;
		sp.setList();
		
		Data.currentFile = null;
		
		Data.selectedTile = 0;
		
		msp.setMap();
		sp.setMap();
		
		tp.setTileset(Data.map.getTileset());
		lp.setMap();
		
		if (Data.currentEpisodeFile != null) {
			int res = JOptionPane.showConfirmDialog(frame, "Do you want to add this file to the episode '" + ep.currentEpisode + "'?", "Add file to episode?", JOptionPane.YES_NO_OPTION);
			
			if (res == JOptionPane.YES_OPTION) {
				if (showAddToEpisodeSave()) {
					Data.map.saveFile();
					ep.importLevel(Data.currentFile);
					ep.setSelectedLevel(Data.episodeFiles.size() - 1);
					
					setFrameTitle(Data.episodeFiles.get(Data.episodeFiles.size() - 1).getAbsolutePath());
				} else {
					setFrameTitle("Untitled");
				}
			}
		}
		
		
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
		
		setFrameTitle(file.getAbsolutePath());
	}
	
	private boolean showSaveWarning() {
		int op = JOptionPane.showConfirmDialog(frame, "File has changed. Do you want to save?", "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		boolean quit = false;
		
		if (op == JOptionPane.YES_OPTION) {
			if (Data.currentFile == null) {
				showSaveDialog();
			} else {
				saveLevel(Data.currentFile);
			}
			
			quit = true;
		} else if (op == JOptionPane.NO_OPTION) {
			quit = true;
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
	
	public void setFrameTitle(String title) {
		frame.setTitle(title + " - PekaED");
	}
}
