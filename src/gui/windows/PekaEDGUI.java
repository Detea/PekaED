package gui.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.Box;
import javax.swing.ComponentInputMap;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
import helpers.EpisodeExtractor;
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
	private JComboBox comboBox;
	private final JPanel statusBar = new JPanel();
	
	private ActionMap actionMap;
	
	/**
	 * @wbp.parser.entryPoint
	 */
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
		
		tp.setPreferredSize(new Dimension(320, 480));
		
		mmp = new MiniMapPanel();
		Data.mmp = mmp;
		
		msp = new MapSettingsPanel();
		sp = new SpritePanel(this);
		
		settingsDialog = new SettingsDialog(this);
		
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
		JMenuItem mifExportEpisode = new JMenuItem("Export Episode");
		
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
		mFile.add(mifExportEpisode);
		
		Image img = null;
		try {
			img = ImageIO.read(getClass().getResource("/pkedit.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		JDialog exportDialog = new JDialog();
		exportDialog.setIconImage(img);
		exportDialog.setAlwaysOnTop(true);
		exportDialog.setLocationRelativeTo(frame);
		
		exportDialog.setTitle("Exporting episode...");
		JLabel edLbl = new JLabel("Writing files...");
		edLbl.setFont(new Font(edLbl.getFont().getFontName(), Font.PLAIN, 14));
		edLbl.setHorizontalAlignment(JLabel.CENTER);
		
		exportDialog.getContentPane().add(edLbl);
		
		exportDialog.setSize(new Dimension(300, 100));
		
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
						JOptionPane.showMessageDialog(null, "Couldn't create episode file!\n" + e.getMessage(), "Couldn't create episode file!", JOptionPane.ERROR_MESSAGE);
						
						e.printStackTrace();
					}
					
					Data.currentEpisodeFile = episodeFile;
					
					ep.newEpisode(fc.getSelectedFile().getName());
					
					setFrameTitle();
					
					tabbedPane.setSelectedIndex(2);
				}
			}
		
		});
		
		mifExportEpisode.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (!Data.currentEpisodeName.isEmpty()) {
					if (Data.fileChanged) {
						showSaveWarning();
					}
					
					if (Data.episodeChanged) {
						int res = showEpisodeSaveWarning();
						
						if (res != 2) {
							if (res == 0) {
								ep.saveEpisode();
							}
						} else {
							return;
						}
					}
					
					edLbl.setText("Writing files...");
					
					JFileChooser fc = new JFileChooser();
					
					fc.setFileFilter(new FileFilter() {

						@Override
						public boolean accept(File f) {
							return f.getName().toLowerCase().endsWith("zip");
						}

						@Override
						public String getDescription() {
							return "ZIP compressed archive (.zip)";
						}
						
					});
					
					fc.setSelectedFile(new File(Data.currentEpisodeName + ".zip"));
					
					if (fc.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
						Thread t = new Thread(new Runnable() {
							@Override
							public void run() {
								exportDialog.setVisible(true);
								
								File f = fc.getSelectedFile();
								
								if (!f.getName().endsWith("zip")) {
									f = new File(fc.getSelectedFile().getAbsolutePath() + ".zip");
								}
								
								boolean done = EpisodeExtractor.extract(f);
								
								if (done) {
									edLbl.setText(EpisodeExtractor.doneMessage);
								}
								
								try {
									Thread.sleep(17);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							
						});
						
						t.start();
					}
				} else {
					JOptionPane.showMessageDialog(frame, "Load an episode, before exporting it.", "No episode loaded", JOptionPane.ERROR_MESSAGE);
				}
			};
			
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
				
				int res = fc.showOpenDialog(frame);
				
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
				
				tabbedPane.setSelectedIndex(2);
			}
			
		});
		
		JMenu mExtras = new JMenu("Extras");
		JMenuItem mieSettings = new JMenuItem("Settings");
		JMenuItem mieAbout = new JMenuItem("About");
		JMenuItem mieHelp = new JMenuItem("Help");
		
		mieSettings.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				settingsDialog.showDialog();
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
				
				Data.lp.repaint();
			}
			
		});
		
		JToggleButton btnHighlightSprites = new JToggleButton("Highlight Sprites");
		btnHighlightSprites.setSelected(Data.showSpriteRect);
		
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
		
		JToggleButton btnShowTileNr = new JToggleButton("Show Tile Number");
		btnShowTileNr.setSelected(Data.showTileNr);
		btnShowTileNr.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.showTileNr) {
					Data.showTileNr = false;
				} else {
					Data.showTileNr = true;
				}
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
		toolbar.add(btnShowTileNr);
		toolbar.addSeparator();
		toolbar.add(lblLayer); 
		toolbar.add(cbLayers);
		toolbar.addSeparator(new Dimension(5, 0));
		toolbar.add(lblEditMode);
		toolbar.add(cbEditMode);
		
		btnHighlightSprites.setFocusable(false);
		cbEditMode.setFocusable(false);
		bNewMap.setFocusable(false);
		bLoadMap.setFocusable(false);
		bSaveAsMap.setFocusable(false);
		bTestLevel.setFocusable(false);
		btEraser.setFocusable(false);
		cbLayers.setFocusable(false);
		btShowSprites.setFocusable(false);
		bSaveMap.setFocusable(false);
		btBrush.setFocusable(false);
		
		tabbedPane.addTab("Properties", msp);
		tabbedPane.addTab("Sprites", sp);
		tabbedPane.addTab("Episode", ep);
		
		tabbedPane.setMinimumSize(new Dimension(280, 400));
		tabbedPane.setPreferredSize(new Dimension(280, 400));
		
		Data.lp = lp;
		
		JScrollPane scrollPane1 = new JScrollPane(tp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JPanel infoPanel = new JPanel();
		JLabel lblTileNr = new JLabel("Foreground tile number: ");
		JLabel lblTileNrVal = new JLabel("");
		
		JLabel lblTileBgNr = new JLabel("Background tile number: ");
		JLabel lblTileBgNrVal = new JLabel("");
		
		JLabel lblSprFile = new JLabel("Sprite file:");
		JLabel lblSprFileVal = new JLabel("");
		
		GridLayout gl = new GridLayout(0, 2);

		infoPanel.setLayout(gl);
		infoPanel.add(lblTileNr);
		infoPanel.add(lblTileNrVal);
		infoPanel.add(lblTileBgNr);
		infoPanel.add(lblTileBgNrVal);
		infoPanel.add(lblSprFile);
		infoPanel.add(lblSprFileVal);
		
		Data.lblTileNrVal = lblTileNrVal;
		Data.lblTileBgNrVal = lblTileBgNrVal;
		Data.lblSprFileVal = lblSprFileVal;
		
		JPanel sPanelWest = new JPanel();
		sPanelWest.add(scrollPane1, BorderLayout.CENTER);
		sPanelWest.add(infoPanel, BorderLayout.SOUTH);
		
		scrollPane2 = new JScrollPane(Data.lp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, sPanelWest, scrollPane2);
		
		scrollPane2.getVerticalScrollBar().setUnitIncrement(32);
		scrollPane2.getHorizontalScrollBar().setUnitIncrement(32);
		
		JScrollBar sb = scrollPane2.getVerticalScrollBar();
		sb.setUnitIncrement(32);
		
		InputMap im = sb.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		im.put(KeyStroke.getKeyStroke("DOWN"), "positiveUnitIncrement");
		im.put(KeyStroke.getKeyStroke("UP"), "negativeUnitIncrement");
		
		JScrollBar sbh = scrollPane2.getHorizontalScrollBar();
		InputMap imh = sbh.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		imh.put(KeyStroke.getKeyStroke("RIGHT"), "positiveUnitIncrement");
		imh.put(KeyStroke.getKeyStroke("LEFT"), "negativeUnitIncrement");
		
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
		
		frame.getContentPane().add(toolbar, BorderLayout.NORTH);
		
		toolbar.addSeparator();
		
		JLabel lblZoom = new JLabel("Zoom:");
		toolbar.add(lblZoom);
		
		JSpinner spinner = new JSpinner();
		spinner.setMaximumSize(new Dimension(50, 20));
		spinner.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Data.scale = (float) spinner.getValue() / 100;
				
				JViewport vp = scrollPane2.getViewport();
				Point p = vp.getViewPosition();
				Point p2 = new Point(p.x + vp.getWidth() / 2, p.y + vp.getHeight() / 2);
				
				Rectangle vr = scrollPane2.getViewport().getViewRect();
				
				vr.x = p2.x - vr.width / 2;
				vr.y = p2.y - vr.height / 2;
				
				vr.x *= Data.scale;
				vr.y *= Data.scale;

				Data.lp.zoom();
				
				scrollPane2.revalidate();
				scrollPane2.repaint();
			}
		});
		spinner.setModel(new SpinnerNumberModel(new Float(100), new Float(20), null, new Float(1)));
		toolbar.add(spinner);
		
		Data.zoomSpinner = spinner;
		
		JButton btnReset = new JButton("Reset");
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Data.scale = 1;
				
				spinner.setValue(100f);
				
				Data.lp.zoom();
			}
		});
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		horizontalStrut.setMaximumSize(new Dimension(5, 32767));
		horizontalStrut.setMinimumSize(new Dimension(5, 0));
		toolbar.add(horizontalStrut);
		toolbar.add(btnReset);
		
		toolbar.addSeparator();
		
		comboBox = new JComboBox();
		
		JLabel lblMode = new JLabel("Rules:");
		toolbar.add(lblMode);
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if (comboBox.getSelectedIndex() == 1) {
					Data.mode = Constants.MODE_ENHANCED;
				} else {
					Data.mode = Constants.MODE_LEGACY;
				}
			}
		});
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		horizontalStrut_1.setMaximumSize(new Dimension(3, 32767));
		toolbar.add(horizontalStrut_1);
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Legacy", "Enhanced"}));
		comboBox.setMaximumSize(new Dimension(100, 25));
		toolbar.add(comboBox);
		
		comboBox.setSelectedIndex(Data.mode);
		
		frame.getContentPane().add(splitPane, BorderLayout.CENTER);
		frame.getContentPane().add(sidePanel, BorderLayout.EAST);
		
		actionMap = new ActionMapUIResource();
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
				
				Data.lp.zoom();
				Data.lp.repaint();
			}
			
		});
		
		actionMap.put("zoomOutAction", new AbstractAction() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.scale - 0.1 > 0.3) {
					Data.scale -= 0.1;
					
					Data.mmp.resizeViewportRect();
					Data.mmp.repaint();
					
					//Dimension d = Data.lp.getPreferredSize();
					
					//scrollPane2.getViewport().setViewSize(new Dimension((int) (d.width / Data.scale), (int) (d.height / Data.scale)));
					
					scrollPane2.revalidate();
					
					Data.lp.zoom();
					Data.lp.repaint();
				}
			}
			
		});
		
		actionMap.put("resetZoom", new AbstractAction() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				Data.scale = 1;
				
				spinner.setValue(100f);
				
				Data.lp.zoom();
			}
			
		});
		
		actionMap.put("testLevel", new AbstractAction() {

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

		setShortcuts();
		
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
							JOptionPane.showMessageDialog(null, "Couldn't store information about current episode.\nThis means it won't be loaded on start up next time.\n" + e1.getMessage(), "Couldn't store information!", JOptionPane.ERROR_MESSAGE);
							
							e1.printStackTrace();
						}
					}
					
					saveSettings();
					
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
	
		Data.tp = tp;
		
		frame.setIconImage(img);
		
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		frame.pack();
		frame.setVisible(true);
		
		frame.setSize(new Dimension(1280, 720));
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		lp.setPekaGUI(this);
		mmp.setPekaGUI(this);
		
		JLabel lblDsf = new JLabel("dsf");
		
		lblDsf.setHorizontalAlignment(SwingConstants.CENTER);
		
		Data.statusLabel = lblDsf;
		
		// Very lazy solution, but it's too fucking warm in my country, right now, it's fucked
		if (Settings.showStatusbar) {
			frame.getContentPane().add(statusBar, BorderLayout.SOUTH);
			statusBar.setLayout(new BorderLayout(0, 0));
			
			statusBar.setPreferredSize(new Dimension(0, 25));
			
			JPanel panel = new JPanel();
			statusBar.add(panel, BorderLayout.EAST);
			
			Component horizontalStrut_2 = Box.createHorizontalStrut(10);
			panel.add(horizontalStrut_2);
			
			panel.add(lblDsf);
		}
		
		scrollPane2.getHorizontalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				if (Data.mmp != null) {
					Data.mmp.reposition();
				}
			}
			
		});
		
		scrollPane2.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener() {

			@Override
			public void adjustmentValueChanged(AdjustmentEvent arg0) {
				if (Data.mmp != null) {
					Data.mmp.reposition();
				}
			}
			
		});
		
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
		
		//scrollPane2.getViewport().setSize(new Dimension((int) (scrollPane2.getViewport().getWidth() * 0.6), (int) (scrollPane2.getViewport().getHeight() * 0.6)));
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
	
	public void setShortcuts() {
		InputMap keyMap = new ComponentInputMap((JComponent) frame.getContentPane());
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("saveLevel").key, Settings.shortcuts.get("saveLevel").modifier), "saveAction");
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("openLevel").key, Settings.shortcuts.get("openLevel").modifier), "loadAction");
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("createLevel").key, Settings.shortcuts.get("createLevel").modifier), "newAction");
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("addSprite").key, Settings.shortcuts.get("addSprite").modifier), "addSpriteAction");
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("saveLevelAs").key, Settings.shortcuts.get("saveLevelAs").modifier), "saveAsAction");
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("bothLayer").key, Settings.shortcuts.get("bothLayer").modifier), "layerAction1");
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("foregroundLayer").key, Settings.shortcuts.get("foregroundLayer").modifier), "layerAction2");
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("backgroundLayer").key, Settings.shortcuts.get("backgroundLayer").modifier), "layerAction3");
		
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("zoomIn").key, Settings.shortcuts.get("zoomIn").modifier), "zoomInAction");
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("zoomOut").key, Settings.shortcuts.get("zoomOut").modifier), "zoomOutAction");
	
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("testLevel").key, Settings.shortcuts.get("testLevel").modifier), "testLevel");
		
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("brushTool").key, Settings.shortcuts.get("brushTool").modifier), "selectBrush");
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("eraserTool").key, Settings.shortcuts.get("eraserTool").modifier), "selectEraser");
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("showSprites").key, Settings.shortcuts.get("showSprites").modifier), "showSprites");

		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("highlightSprites").key, Settings.shortcuts.get("highlightSprites").modifier), "showSpriteRect");
		
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("tileMode").key, Settings.shortcuts.get("tileMode").modifier), "editModeTiles");
		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("spriteMode").key, Settings.shortcuts.get("spriteMode").modifier), "editModeSprites");

		keyMap.put(KeyStroke.getKeyStroke(Settings.shortcuts.get("zoomReset").key, Settings.shortcuts.get("zoomReset").modifier), "resetZoom");
		
		SwingUtilities.replaceUIInputMap((JComponent) frame.getContentPane(),  JComponent.WHEN_IN_FOCUSED_WINDOW, keyMap);
		SwingUtilities.replaceUIActionMap((JComponent) frame.getContentPane(), actionMap);
	}
	
	private void testLevel() {
		String cmd = Settings.BASE_PATH + File.separatorChar + "pk2.exe";
		String args = "dev test \"" + Data.currentFile.getParentFile().getName() + "/" + Data.currentFile.getName() + "\"";
		try{
			Runtime runTime = Runtime.getRuntime();
			Process process = runTime.exec(cmd + " " + args);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Can't test level!\n" + e.getMessage(), "Can't test level!", JOptionPane.ERROR_MESSAGE);
			
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
		PK2Map oldMap = Data.map;
		
		Data.map = new PK2Map();
		
		boolean ok = Data.map.loadFile(file);
		
		if (ok) {
			Data.currentFile = new File(file);
			
			if (new File(Data.currentFile.getParentFile().getAbsolutePath() + "\\" + Data.map.getBackground()).exists()) {
				Data.bgFile = new File(Data.currentFile.getParentFile().getAbsolutePath() + "\\" + Data.map.getBackground());
			} else {
				Data.bgFile = new File(Settings.SCENERY_PATH + "\\" + Data.map.getBackground());
			}
			
			if (new File(Data.currentFile.getParentFile().getAbsolutePath() + "\\" + Data.map.getTileset()).exists()) {
				Data.tilesetFile = new File(Data.currentFile.getParentFile().getAbsolutePath() + "\\" + Data.map.getTileset());
			} else {
				Data.tilesetFile = new File(Settings.TILES_PATH + "\\" + Data.map.getTileset());
			}
			
			lp.setMap();
			tp.setTileset();
			msp.setMap();
			sp.setMap();
			
			Data.scale = 1f;
			Data.zoomSpinner.setValue(100f);
			Data.lp.zoom();
			
			lp.repaint();
			mmp.repaint();
			
			Rectangle r = Data.map.calculateUsedArea(Data.map.layers[Constants.LAYER_BACKGROUND], "background2");
			
			scrollPane2.getVerticalScrollBar().setValue(r.y * 32);
			scrollPane2.getHorizontalScrollBar().setValue(r.x * 32);
			
			Data.mmp.reposition();
			Data.mmp.resizeViewportRect();
			
			Data.fileChanged = false;
			
			setFrameTitle();
			
			Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	 
			Data.statusLabel.setText("Level loaded! (" + sdf.format(cal.getTime()) + ")");
		} else {
			Data.map = oldMap;
		}
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
		Data.map.addSprite(psprite, "rooster.spr");
		Data.map.levelNumber = Data.episodeFiles.size() + 1;
		sp.setList();
		
		Data.currentFile = null;
		
		setFrameTitle();
		
		Data.selectedTile = 0;
		
		Data.bgFile = new File(Settings.SCENERY_PATH + "\\" + Settings.DEFAULT_BACKGROUND);
		Data.tilesetFile = new File(Settings.TILES_PATH + "\\" + Settings.DEFAULT_TILESET);
		
		msp.setMap();
		sp.setMap();
		
		lp.setMap();
		tp.setTileset();
		
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
		
		Data.statusLabel.setText("New level created");
		
		scrollPane2.getViewport().setViewPosition(new Point(0, 0));
	}
	
	private void saveLevel(File file) {
		if (file != null) {
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
			
			Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	 
			Data.statusLabel.setText("Level saved! (Last save: " + sdf.format(cal.getTime()) + ")");
		} else {
			showSaveDialog();
		}
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
	
	private int showEpisodeSaveWarning() {
		int op = JOptionPane.showConfirmDialog(frame, "Episode has changed. Do you want to save?", "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		int quit = 0;
		
		if (op == JOptionPane.YES_OPTION) {
			quit = 0;
		} else if (op == JOptionPane.NO_OPTION) {
			quit = 1;
		} else {
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
	
	private void exportEpisode(File target) {
		try {
			ZipOutputStream zou = new ZipOutputStream(new FileOutputStream(target));
			
			zou.putNextEntry(new ZipEntry("episodes/"));
			zou.putNextEntry(new ZipEntry("episodes/" + Data.currentEpisodeName + "/"));
			
			for (File f : Data.episodeFiles) {
				zou.putNextEntry(new ZipEntry("episodes/" + Data.currentEpisodeName + "/" + f));
			}
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Can't create file '" + target.getName() + "'!", "Error", JOptionPane.ERROR_MESSAGE);
			
			e.printStackTrace();
		}
	}
	
	private void saveSettings() {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream("settings"));
			
			dos.writeUTF(Settings.BASE_PATH);
			dos.writeBoolean(Settings.loadEpisodeOnStartup);
			dos.writeBoolean(Settings.startInEnhancedMode);
			dos.writeInt(Constants.ENHANCED_LEVEL_LIMIT);
			dos.writeBoolean(Settings.showStatusbar);

			dos.writeBoolean(Settings.spritePreview);
			dos.writeBoolean(Settings.tilesetPreview);
			dos.writeBoolean(Settings.bgPreview);
			
			dos.writeBoolean(Data.showSpriteRect);
			dos.writeBoolean(Data.showTileNr);
			
			int i = 0;
			for (String s : Settings.shortcuts.keySet()) {
				dos.writeUTF(s);
				dos.writeInt(Settings.shortcuts.get(s).modifier);
				dos.writeInt(Settings.shortcutKeyCodes[i]);
				
				i++;
			}
			
			dos.flush();
			dos.close();
		} catch (IOException e1) {
			JOptionPane.showMessageDialog(null, "Couldn't save save file!\n" + e1.getMessage(), "Couldn't save save file", JOptionPane.ERROR_MESSAGE);
			
			e1.printStackTrace();
		}
	}
}
