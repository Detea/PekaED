package gui.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import data.Constants;
import data.Data;
import data.Settings;

public class SettingsDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	private JRadioButton rdbtnLoadLastEpisode, rdbtnCreateEmptyLevel;
	private JRadioButton rdbtnEnhancedMode, rdbtnLegacyMode, rdbtnCeMode, rdbtnLoadLastLevel;
	private JCheckBox chckbxShowStatusBar, chckbxAutomaticallySwitchModes;	
	
	private JSpinner spinner;
	
	private JSlider slider;
	
	private int lastEpisodeLimit = 100;
	private JTable table;
	
	private PekaEDGUI pkg;
	
	private boolean changedKeys = false;
	private JTextField txtPkceexeTestlevel;
	
	@SuppressWarnings("serial")
	public SettingsDialog(PekaEDGUI pkg) {
		Image img = null;
		
		this.pkg = pkg;
		
		try {
			img = ImageIO.read(getClass().getResource("/pkedit.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setIconImage(img);
		
		setTitle("Settings");
		setBounds(100, 100, 441, 562);
		getContentPane().setLayout(null);
		contentPanel.setBounds(0, 0, 433, 500);
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel);
		
		ButtonGroup rbGroup = new ButtonGroup();
		
		ButtonGroup bgMode = new ButtonGroup();
		contentPanel.setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setPreferredSize(new Dimension(300, 500));
		tabbedPane.setBounds(5, 5, 428, 487);
		contentPanel.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		panel.setMinimumSize(new Dimension(300, 500));
		panel.setPreferredSize(new Dimension(300, 500));
		tabbedPane.addTab("General", null, panel, null);
		panel.setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(10, 39, 314, 20);
		panel.add(textField);
		textField.setColumns(10);
		{
			JLabel lblNewLabel = new JLabel("Path to Pekka Kana 2:");
			lblNewLabel.setBounds(10, 14, 114, 14);
			panel.add(lblNewLabel);
		}
		
		JLabel lblLevelLimitOf = new JLabel("Level limit per episode:");
		lblLevelLimitOf.setBounds(10, 70, 114, 25);
		panel.add(lblLevelLimitOf);
		
		spinner = new JSpinner();
		spinner.setBounds(126, 72, 60, 20);
		panel.add(spinner);
		spinner.setModel(new SpinnerNumberModel(new Integer(1), new Integer(1), null, new Integer(1)));
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(334, 38, 89, 23);
		panel.add(btnBrowse);
		
		chckbxShowStatusBar = new JCheckBox("Show status bar");
		chckbxShowStatusBar.setSelected(true);
		chckbxShowStatusBar.setBounds(10, 102, 114, 23);
		panel.add(chckbxShowStatusBar);
		
		JCheckBox chckbxShowSpritePreview = new JCheckBox("Show sprite preview");
		chckbxShowSpritePreview.setBounds(261, 102, 138, 23);
		panel.add(chckbxShowSpritePreview);
		
		chckbxShowSpritePreview.setSelected(Settings.spritePreview);
		
		JCheckBox chckbxShowTilesetPreview = new JCheckBox("Show tileset preview");
		chckbxShowTilesetPreview.setBounds(261, 129, 124, 23);
		panel.add(chckbxShowTilesetPreview);
		
		chckbxShowTilesetPreview.setSelected(Settings.tilesetPreview);
		
		JCheckBox chckbxShowBackgroundPreview = new JCheckBox("Show background preview");
		chckbxShowBackgroundPreview.setBounds(261, 155, 151, 23);
		panel.add(chckbxShowBackgroundPreview);
		
		chckbxShowBackgroundPreview.setSelected(Settings.bgPreview);
		
		JLabel lblUndoLimit = new JLabel("Undo limit:");
		lblUndoLimit.setBounds(10, 195, 67, 14);
		panel.add(lblUndoLimit);
		
		JLabel label = new JLabel("0");
		label.setBounds(72, 195, 46, 14);
		panel.add(label);
		
		slider = new JSlider();
		slider.setMinimum(50);
		slider.setMinorTickSpacing(100);
		slider.setMajorTickSpacing(100);
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				label.setText(Integer.toString(slider.getValue()));
			}
		});
		slider.setPaintLabels(true);
		slider.setSnapToTicks(true);
		slider.setValue(100);
		slider.setMaximum(1000);
		slider.setBounds(10, 218, 403, 40);
		slider.createStandardLabels(100);
		
		panel.add(slider);
		
		JLabel lblOnStartup = new JLabel("On startup:");
		lblOnStartup.setBounds(10, 283, 68, 14);
		panel.add(lblOnStartup);
		
		rdbtnLoadLastEpisode = new JRadioButton("Load last episode");
		rdbtnLoadLastEpisode.setBounds(10, 304, 114, 23);
		panel.add(rdbtnLoadLastEpisode);
		rbGroup.add(rdbtnLoadLastEpisode);
		
		rdbtnLegacyMode = new JRadioButton("Legacy mode");
		rdbtnLegacyMode.setBounds(143, 330, 109, 23);
		panel.add(rdbtnLegacyMode);
		bgMode.add(rdbtnLegacyMode);
		
		rdbtnEnhancedMode = new JRadioButton("Enhanced mode");
		rdbtnEnhancedMode.setBounds(143, 304, 109, 23);
		panel.add(rdbtnEnhancedMode);
		bgMode.add(rdbtnEnhancedMode);
		
		chckbxAutomaticallySwitchModes = new JCheckBox("Automatically switch modes");
		chckbxAutomaticallySwitchModes.setBounds(10, 128, 189, 23);
		panel.add(chckbxAutomaticallySwitchModes);
		
		rdbtnCeMode = new JRadioButton("C.E. mode");
		rdbtnCeMode.setBounds(261, 304, 109, 23);
		bgMode.add(rdbtnCeMode);
		panel.add(rdbtnCeMode);
		
		rdbtnLoadLastLevel = new JRadioButton("Load last level");
		rdbtnLoadLastLevel.setBounds(10, 330, 109, 23);
		panel.add(rdbtnLoadLastLevel);
		rbGroup.add(rdbtnLoadLastLevel);
		
		rdbtnCreateEmptyLevel = new JRadioButton("Create empty level");
		rdbtnCreateEmptyLevel.setBounds(10, 356, 124, 23);
		panel.add(rdbtnCreateEmptyLevel);
		rbGroup.add(rdbtnCreateEmptyLevel);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Shortcuts", null, panel_1, null);
		panel_1.setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 418, 315);
		panel_1.add(scrollPane);
		
		String[] modifierVals = new String[] { "None", "CTRL", "ALT", "META", "SHIFT" };
		
		class CustomComboBoxRenderer extends JComboBox implements TableCellRenderer{
			public CustomComboBoxRenderer(String[] items) {
				super(items);
			}
			
			@Override
			public Component getTableCellRendererComponent(JTable t, Object val, boolean isSelected, boolean hasFocus,
					int row, int col) {
				if (isSelected) {
					setForeground(Color.BLACK);
					super.setBackground(t.getSelectionBackground());
				} else {
					setForeground(t.getForeground());
					setBackground(t.getBackground());
				}
				
				/*
				if (((String) val).equals((String) getSelectedItem())) {
					changedKeys = true;
				}*/
				
				setSelectedItem(val);
				
				return this;
			}
		}
		
		class CustomComboBoxEditor extends DefaultCellEditor {
			public CustomComboBoxEditor(String[] items) {
				super(new JComboBox(items));
			}
			
			public JComboBox getComponent() {
				return (JComboBox) getComponent();	
			}
		}
		
		table = new JTable();
		
		table.setFillsViewportHeight(true);
		scrollPane.setViewportView(table);
		table.setModel(new DefaultTableModel(
			new Object[][] {
				{"Create new Level", null, null, null},
				{"Open level", null, null, null},
				{"Save level", null, null, null},
				{"Save level as...", new Integer(0), new Integer(0), null},
				{"Test level", new Integer(0), new Integer(0), null},
				{"Brush tool", new Integer(0), new Integer(0), null},
				{"Eraser tool", new Integer(0), new Integer(0), null},
				{"Show/hide sprites", new Integer(0), new Integer(0), null},
				{"Toggle sprite highlighting", new Integer(0), new Integer(0), null},
				{"Select layer \"Both\"", new Integer(0), new Integer(0), null},
				{"Select layer \"Foreground\"", new Integer(0), new Integer(0), null},
				{"Select layer \"Background\"", new Integer(0), new Integer(0), null},
				{"Zoom in", new Integer(0), new Integer(0), null},
				{"Zoom out", new Integer(0), new Integer(0), null},
				{"Reset zoom", new Integer(0), new Integer(0), null},
				{"Tile mode", new Integer(0), new Integer(0), null},
				{"Sprite mode", new Integer(0), new Integer(0), null},
				{"Add sprite", new Integer(0), new Integer(0), null},
				{"Undo", new Integer(0), new Integer(0), null},
				{"Redo", new Integer(0), new Integer(0), null},
				{"Flip selection vertically", new Integer(0), new Integer(0), null},
			},
			new String[] {
				"Function", "Modifier", "Mask", "Key"
			}
		) {
			Class[] columnTypes = new Class[] {
				String.class, Object.class, Object.class, Object.class
			};
			
			public Class getColumnClass(int columnIndex) {
				return columnTypes[columnIndex];
			}
			
			boolean[] columnEditables = new boolean[] {
					false, true, true, false
			};
			
			public boolean isCellEditable(int row, int column) {
				return columnEditables[column];
			}
		});
		
		table.getColumnModel().getColumn(0).setPreferredWidth(135);
		
		JButton btnReset = new JButton("Reset");
		btnReset.setBounds(324, 326, 89, 23);
		panel_1.add(btnReset);
		btnReset.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Settings.resetShortcuts();
				
				int i = 0;
				for (String s : Settings.shortcuts.keySet()) {
					String str = "None", mask = "None";
					
					switch (Settings.shortcuts.get(s).modifier) {
						case Event.CTRL_MASK:
							str = "CTRL";
							break;
							
						case Event.SHIFT_MASK:
							str = "SHIFT";
							break;
							
						case Event.META_MASK:
							str = "META";
							break;
							
						case Event.ALT_MASK:
							str = "ALT";
							break;
					}
					
					switch (Settings.shortcuts.get(s).mask) {
						case Event.CTRL_MASK:
							mask = "CTRL";
							break;
							
						case Event.SHIFT_MASK:
							mask = "SHIFT";
							break;
							
						case Event.META_MASK:
							mask = "META";
							break;
							
						case Event.ALT_MASK:
							mask = "ALT";
							break;
					}
					
					table.getModel().setValueAt(str, i, 1);
					table.getModel().setValueAt(mask, i, 2);
					table.getModel().setValueAt(KeyEvent.getKeyText(Settings.shortcuts.get(s).key), i, 3);
					
					i++;
				}
			}
		});
		
		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int col = table.columnAtPoint(e.getPoint());
					int row = table.rowAtPoint(e.getPoint());
					
					if (col == 3) {
						KeyDialog kd = new KeyDialog(table, row);
					
						Data.run = true;
						
						Thread thread = new Thread(new Runnable() {

							@Override
							public void run() {
								while (Data.run) {
									if (!kd.isVisible()) {
										table.setValueAt(KeyEvent.getKeyText(Data.key), row, 3);
										Settings.shortcutKeyCodes[row] = Data.key;
										
										Data.run = false;
									}
									
									try {
										Thread.sleep(30);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
							
						});
						
						changedKeys = true;
						
						thread.start();
					}
				}
			}
		});
		
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		TableColumn col = table.getColumnModel().getColumn(1);
		col.setCellEditor(new CustomComboBoxEditor(modifierVals));
		col.setCellRenderer(new CustomComboBoxRenderer(modifierVals));
		
		TableColumn col2 = table.getColumnModel().getColumn(2);
		col2.setCellEditor(new CustomComboBoxEditor(modifierVals));
		col2.setCellRenderer(new CustomComboBoxRenderer(modifierVals));
		
		int i = 0;
		for (String s : Settings.shortcuts.keySet()) {
			String str = "None", mask = "None";
			
			switch (Settings.shortcuts.get(s).modifier) {
				case Event.CTRL_MASK:
					str = "CTRL";
					break;
					
				case Event.SHIFT_MASK:
					str = "SHIFT";
					break;
					
				case Event.META_MASK:
					str = "META";
					break;
					
				case Event.ALT_MASK:
					str = "ALT";
					break;
			}
			
			switch (Settings.shortcuts.get(s).mask) {
				case Event.CTRL_MASK:
					mask = "CTRL";
					break;
					
				case Event.SHIFT_MASK:
					mask = "SHIFT";
					break;
					
				case Event.META_MASK:
					mask = "META";
					break;
					
				case Event.ALT_MASK:
					mask = "ALT";
					break;
			}
			
			table.getModel().setValueAt(str, i, 1);
			table.getModel().setValueAt(mask, i, 2);
			table.getModel().setValueAt(KeyEvent.getKeyText(Settings.shortcutKeyCodes[i]), i, 3);
			
			i++;
		}
		
		btnBrowse.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Set Pekka Kana 2 path");
				
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				int res = fc.showOpenDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					textField.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
			
		});
		
		JButton okButton = new JButton("OK");
		okButton.setBounds(303, 499, 47, 23);
		getContentPane().add(okButton);
		{
			getRootPane().setDefaultButton(okButton);
		}
		{
			JButton cancelButton = new JButton("Cancel");
			cancelButton.setBounds(360, 499, 65, 23);
			getContentPane().add(cancelButton);
			
			JLabel lblParameters = new JLabel("Parameters:");
			lblParameters.setBounds(10, 391, 65, 14);
			panel.add(lblParameters);
			
			txtPkceexeTestlevel = new JTextField();
			txtPkceexeTestlevel.setText("pk2ce.exe dev test %level%");
			txtPkceexeTestlevel.setBounds(10, 416, 403, 20);
			panel.add(txtPkceexeTestlevel);
			txtPkceexeTestlevel.setColumns(10);
			
			JButton btnReset_1 = new JButton("Reset");
			btnReset_1.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (Data.mode == Constants.MODE_ENHANCED || Data.mode == Constants.MODE_LEGACY) {
						txtPkceexeTestlevel.setText("pk2.exe dev test %level%");
					} else if (Data.mode == Constants.MODE_CE) {
						txtPkceexeTestlevel.setText("pk2ce.exe dev test %level%");
					}
				}
			});
			btnReset_1.setBounds(324, 387, 89, 23);
			panel.add(btnReset_1);
			
			// This could be done under the addActionListener for okButton, but it is really short
			cancelButton.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					spinner.setValue(lastEpisodeLimit);
					
					dispose();
				}
			});
		}
		
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.BASE_PATH = textField.getText();
				
				Settings.setPaths();
				
				if (chckbxShowStatusBar.isSelected() != Settings.showStatusbar) {
					JOptionPane.showMessageDialog(null, "A restart is required, to remove/show the status bar!", "Restart required", JOptionPane.INFORMATION_MESSAGE);
				}
				
				Settings.loadEpisodeOnStartup = rdbtnLoadLastEpisode.isSelected();
				Settings.startInEnhancedMode = rdbtnEnhancedMode.isSelected();
				Settings.startInCEMode = rdbtnCeMode.isSelected();
				
				if (rdbtnLegacyMode.isSelected()) {
					Settings.startInCEMode = false;
					Settings.startInEnhancedMode = false;
				}
				
				Settings.showStatusbar = chckbxShowStatusBar.isSelected();
				
				Settings.autoSwitchModes = chckbxAutomaticallySwitchModes.isSelected();
				
				Settings.spritePreview = chckbxShowSpritePreview.isSelected();
				Settings.tilesetPreview = chckbxShowTilesetPreview.isSelected();
				Settings.bgPreview = chckbxShowBackgroundPreview.isSelected();
				
				Settings.parameters = txtPkceexeTestlevel.getText();
				
				Settings.doLimit = slider.getValue();
				
				Settings.loadLastLevel = rdbtnLoadLastLevel.isSelected();
				
				int mod = 0, key, i = 0, mask = 0;
				
				for (String s : Settings.shortcuts.keySet()) {
					switch ((String) table.getModel().getValueAt(i, 1)) {
						case "None":
							mod = 0;
							break;
							
						case "CTRL":
							mod = Event.CTRL_MASK;
							break;
							
						case "META":
							mod = Event.META_MASK;
							break;
							
						case "ALT":
							mod = Event.ALT_MASK;
							break;
							
						case "SHIFT":
							mod = Event.SHIFT_MASK;
							break;
					}
					
					switch ((String) table.getModel().getValueAt(i, 2)) {
						case "None":
							mask = 0;
							break;
							
						case "CTRL":
							mask = Event.CTRL_MASK;
							break;
							
						case "META":
							mask = Event.META_MASK;
							break;
							
						case "ALT":
							mask = Event.ALT_MASK;
							break;
							
						case "SHIFT":
							mask = Event.SHIFT_MASK;
							break;
					}
					
					key = Settings.shortcutKeyCodes[i];
					
					Settings.shortcuts.get(s).modifier = mod;
					Settings.shortcuts.get(s).mask = mask;
					Settings.shortcuts.get(s).key = key;
					
					i++;
				}
				
				if (Data.mode == Constants.MODE_ENHANCED || Data.mode == Constants.MODE_CE) {
					Constants.ENHANCED_LEVEL_LIMIT = (int) spinner.getValue();
				}
				
				try {
					DataOutputStream dos = new DataOutputStream(new FileOutputStream("settings"));
					
					byte[] version = { '1', '.', '4'};
					
					dos.write(version);
					
					dos.writeUTF(Settings.BASE_PATH);
					dos.writeBoolean(Settings.loadEpisodeOnStartup);
					dos.writeBoolean(Settings.startInEnhancedMode);
					dos.writeBoolean(Settings.startInCEMode);
					
					dos.writeInt(Constants.ENHANCED_LEVEL_LIMIT);
					dos.writeBoolean(Settings.showStatusbar);
					
					dos.writeBoolean(Settings.autoSwitchModes);
					dos.writeBoolean(Settings.useDevMode);
					
					dos.writeBoolean(Settings.spritePreview);
					dos.writeBoolean(Settings.tilesetPreview);
					dos.writeBoolean(Settings.bgPreview);
					
					dos.writeBoolean(Data.showSpriteRect);
					dos.writeBoolean(Data.showTileNr);
					
					dos.writeInt(Settings.doLimit);
					
					dos.writeUTF(Settings.parameters);
					
					int j = 0;
					for (String s : Settings.shortcuts.keySet()) {
						dos.writeUTF(s);
						dos.writeInt(Settings.shortcuts.get(s).modifier);
						dos.writeInt(Settings.shortcuts.get(s).mask);
						dos.writeInt(Settings.shortcutKeyCodes[j]);
						
						j++;
					}
					
					dos.flush();
					dos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				if (changedKeys) {
					JOptionPane.showMessageDialog(null, "A restart is required, before the new shortcuts can be used!", "Restart required!", JOptionPane.INFORMATION_MESSAGE);
				}
				
				dispose();
			}
			
		});
		
		if (!Settings.BASE_PATH.isEmpty()) {
			textField.setText(Settings.BASE_PATH);
		}
		
		if (Settings.loadEpisodeOnStartup) {
			rdbtnLoadLastEpisode.setSelected(true);
		} else if (Settings.loadLastLevel) {
			rdbtnLoadLastLevel.setSelected(true);
		} else {
			rdbtnCreateEmptyLevel.setSelected(true);
		}
		
		if (Settings.startInEnhancedMode) {
			rdbtnEnhancedMode.setSelected(true);
		} else if (Settings.startInCEMode) {
			rdbtnCeMode.setSelected(true);
		} else {
			rdbtnLegacyMode.setSelected(true);
		}
		
		if (!Settings.parameters.isEmpty()) {			
			txtPkceexeTestlevel.setText(Settings.parameters);
		}
		
		addWindowListener(new WindowListener() {

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent arg0) {
				
			}

			@Override
			public void windowClosing(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
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
				
			}
			
		});
		
		setResizable(false);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
	}
	
	public void showDialog() {
		textField.setText(Settings.BASE_PATH);
		
		if (Settings.startInEnhancedMode) {
			rdbtnEnhancedMode.setSelected(true);
		} else if (Settings.startInCEMode) {
			rdbtnCeMode.setSelected(true);
		} else {
			rdbtnLegacyMode.setSelected(true);
		}
		
		if (Settings.loadEpisodeOnStartup) {
			rdbtnLoadLastEpisode.setSelected(true);
		} else if (Settings.loadLastLevel) {
			rdbtnLoadLastLevel.setSelected(true);
		} else {
			rdbtnCreateEmptyLevel.setSelected(true);
		}
		
		if (Data.mode == Constants.MODE_LEGACY) {
			spinner.setValue(Constants.LEGACY_LEVEL_LIMIT);
			spinner.setEnabled(false);
		} else {
			spinner.setValue(Constants.ENHANCED_LEVEL_LIMIT);
			spinner.setEnabled(true);
		}
		
		chckbxShowStatusBar.setSelected(Settings.showStatusbar);
		chckbxAutomaticallySwitchModes.setSelected(Settings.autoSwitchModes);
		
		slider.setValue(Settings.doLimit);
		
		revalidate();
		setVisible(true);
	}
}
