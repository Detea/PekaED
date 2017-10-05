package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

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
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
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

import data.Data;
import data.Settings;
import pekkakana.PK2Map;
import pekkakana.PK2Sprite;

public class PekaEDGUI {
	JFrame frame;
	
	LevelPanel lp;
	TilePanel tp;
	
	MapSettingsPanel msp;
	SpritePanel sp;
	
	public void setup() {
		frame = new JFrame("PekaED");
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		lp = new LevelPanel();
		tp = new TilePanel();
		
		JToolBar toolbar = new JToolBar();
		JButton bNewMap = new JButton();
		JButton bLoadMap = new JButton();
		JButton bSaveMap = new JButton();
		JButton bSaveAsMap = new JButton();
		JButton bHelp = new JButton();
	
		bNewMap.setIcon(new ImageIcon(getClass().getResource("/document-new.png")));
		bLoadMap.setIcon(new ImageIcon(getClass().getResource("/document-open.png")));
		bSaveMap.setIcon(new ImageIcon(getClass().getResource("/document-save.png")));
		bSaveAsMap.setIcon(new ImageIcon(getClass().getResource("/document-save-as.png")));
		bHelp.setIcon(new ImageIcon(getClass().getResource("/help-browser.png")));
		
		bHelp.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				new AboutFrame();
			}
			
		});
		
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
						loadLevel();
					}
				} else {
					loadLevel();
				}
			}
			
		});
		
		bSaveMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.currentFile == null) {
					JFileChooser fc = new JFileChooser(Settings.EPISODES_PATH);
					
					fc.setDialogTitle("Save as...");
					
					fc.setFileFilter(new FileNameExtensionFilter("Pekka Kana 2 Level", "map", "MAP"));
					
					int res = fc.showSaveDialog(frame);
					
					if (res == JFileChooser.APPROVE_OPTION) {
						saveLevel(fc.getSelectedFile());
					}
				} else {
					saveLevel(Data.currentFile);
				}
			}
			
		});
		
		bSaveAsMap.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
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
			
		});
		
		toolbar.add(bNewMap);
		toolbar.add(bLoadMap);
		toolbar.add(bSaveMap);
		toolbar.add(bSaveAsMap);
		toolbar.add(bHelp);
		toolbar.addSeparator();
		
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
		
		JLabel lblLayer = new JLabel("Layer: ");
		
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
		
		toolbar.add(btShowSprites);
		toolbar.addSeparator();
		toolbar.add(lblLayer); 
		toolbar.add(cbLayers);
		
		msp = new MapSettingsPanel();
		sp = new SpritePanel();
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setPreferredSize(new Dimension(256, 600));
		
		tabbedPane.addTab("Properties", msp);
		tabbedPane.addTab("Sprites", sp);
		
		JScrollPane scrollPane1 = new JScrollPane(tp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		JScrollPane scrollPane2 = new JScrollPane(lp, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
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
							return f.getName().endsWith(".map") && f.getName().length() < 39;
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
		
		actionMap.put("loadAction", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				loadLevel();
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
		
		InputMap keyMap = new ComponentInputMap((JComponent) frame.getContentPane());
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK), "saveAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK), "loadAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK), "newAction");
		keyMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, Event.CTRL_MASK), "addSpriteAction");
		keyMap.put(KeyStroke.getKeyStroke("1"), "layerAction1");
		keyMap.put(KeyStroke.getKeyStroke("2"), "layerAction2");
		keyMap.put(KeyStroke.getKeyStroke("3"), "layerAction3");
		
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
				if (Data.fileChanged) {
					if (showSaveWarning()) {
						Data.runThread = false;
						
						System.exit(0);
					}
				} else {
					Data.runThread = false;
					
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
		
		frame.setIconImage(img);
		
		frame.setSize(1280, 720);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setVisible(true);
	}
	
	private void loadLevel() {
		JFileChooser fc = new JFileChooser(Settings.EPISODES_PATH);
		
		fc.setFileFilter(new FileNameExtensionFilter("Pekka Kana 2 Level", "map", "MAP"));
		
		int res = fc.showOpenDialog(frame);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			Data.map = new PK2Map(fc.getSelectedFile().getAbsolutePath());
			
			Data.currentFile = fc.getSelectedFile();
			
			tp.setTileset(Data.map.getTileset());
			lp.setMap();
			msp.setMap();
			sp.setMap(Data.map);
			
			Data.fileChanged = false;

			setFrameTitle(fc.getSelectedFile().getAbsolutePath());
		}
	}
	
	private void newLevel() {
		Data.fileChanged = false;
		
		PK2Sprite psprite = new PK2Sprite("rooster.spr");
		Data.map = new PK2Map();
		Data.map.addSprite(psprite, psprite.filename);
		sp.setList();
		
		Data.currentFile = null;
		
		Data.selectedTile = 0;
		
		msp.setMap();
		sp.setMap(Data.map);
		
		tp.setTileset(Data.map.getTileset());
		
		lp.setTileset(Data.map.getTileset());
		lp.setMap();
		
		setFrameTitle("Untitled");
	}
	
	private void saveLevel(File file) {
		if (file.exists())
			file.delete();
		
		File f = file;
		
		if (!file.getName().endsWith(".map")) {
			f = new File(file.getAbsolutePath() + ".map");
		}
		
		msp.saveChanges();
		
		Data.map.saveFile(f);
		
		setFrameTitle(Data.currentFile.getAbsolutePath());
	}
	
	private boolean showSaveWarning() {
		int op = JOptionPane.showConfirmDialog(frame, "File has changed. Do you want to save?", "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		
		boolean quit = false;
		
		if (op == JOptionPane.YES_OPTION) {
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
				saveLevel(Data.currentFile);
			}
			
			quit = true;
		} else if (op == JOptionPane.NO_OPTION) {
			quit = true;
		}
		
		return quit;
	}
	
	private void setFrameTitle(String title) {
		frame.setTitle(title + " - PekaED");
	}
}
