package gui.panels;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;

import data.Constants;
import data.Data;
import data.Settings;
import gui.windows.SetMapPositionDialog;
import pekkakana.PK2Map;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MapSettingsPanel extends JPanel {
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	
	JSpinner spinner, spinner_1, spinner_2, spinner_3, spinner_4, spinner_5, spinner_6, spinner_7, spinner_8, spinner_9;
	JComboBox comboBox, comboBox_1, comboBox_2, comboBox_3;

	PK2Map map;
	
	public MapSettingsPanel() {
		setLayout(null);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setBounds(10, 11, 46, 14);
		add(lblName);
		
		JLabel lblCreator = new JLabel("Creator:");
		lblCreator.setBounds(10, 36, 46, 14);
		add(lblCreator);
		
		textField = new JTextField();
		textField.setBounds(71, 11, 108, 20);
		add(textField);
		textField.setColumns(10);
		
		textField.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
			
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				Data.fileChanged = true;
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				Data.fileChanged = true;
			}
			
		});
		
		textField_1 = new JTextField();
		textField_1.setBounds(71, 36, 108, 20);
		add(textField_1);
		textField_1.setColumns(10);
		
		textField_1.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
			
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				Data.fileChanged = true;
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				Data.fileChanged = true;
			}
			
		});
		
		
		JLabel lblTileset = new JLabel("Tileset:");
		lblTileset.setBounds(10, 61, 46, 14);
		add(lblTileset);
		
		textField_2 = new JTextField();
		textField_2.setBounds(71, 61, 108, 20);
		add(textField_2);
		textField_2.setColumns(10);
		
		textField_2.getDocument().addDocumentListener(new DocumentListener() {

			@Override
			public void changedUpdate(DocumentEvent arg0) {
			
			}

			@Override
			public void insertUpdate(DocumentEvent arg0) {
				Data.fileChanged = true;
			}

			@Override
			public void removeUpdate(DocumentEvent arg0) {
				Data.fileChanged = true;
			}
			
		});
		
		
		JButton btnLoad = new JButton("Select");
		btnLoad.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser(Settings.TILES_PATH);
				fc.setDialogTitle("Select a tileset...");
				fc.setAcceptAllFileFilterUsed(false);
				
				fc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File e) {
						return e.isDirectory() | e.getName().toLowerCase().endsWith(".bmp") | e.getName().toLowerCase().endsWith(".pcx") && e.getName().length() < 13;
					}

					@Override
					public String getDescription() {
						return "Tileset image file (.bmp | .pcx)";
					}
					
				});
				
				if (Settings.tilesetPreview) {
					FilePreviewPanel fpp = new FilePreviewPanel(FilePreviewPanel.TILESET);
					
					fc.setAccessory(fpp);
					fc.addPropertyChangeListener(fpp);
				}
				
				
				int res = fc.showOpenDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					textField_2.setText(fc.getSelectedFile().getName());
					
					Data.tilesetFile = fc.getSelectedFile();
					
					Data.map.tilesetImageFile = fc.getSelectedFile().getName();
					
					// It is important to update the level panel before the tile panel, because the level panel loads the background image palette
					Data.lp.setTileset();
					Data.tp.setTileset();
					
					Data.fileChanged = true;
				}
			}
		});
		btnLoad.setBounds(183, 60, 66, 23);
		add(btnLoad);
		
		JLabel lblBackground = new JLabel("Background:");
		lblBackground.setBounds(10, 86, 66, 20);
		add(lblBackground);
		
		textField_3 = new JTextField();
		textField_3.setBounds(71, 86, 108, 20);
		add(textField_3);
		textField_3.setColumns(10);
		
		JButton btnSelect = new JButton("Select");
		btnSelect.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(Settings.SCENERY_PATH);
				fc.setDialogTitle("Select a background...");
				fc.setAcceptAllFileFilterUsed(false);
				
				fc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File e) {
						return e.isDirectory() | e.getName().toLowerCase().endsWith(".bmp") | e.getName().toLowerCase().endsWith(".pcx") && e.getName().length() < 13;
					}

					@Override
					public String getDescription() {
						return "Background image file (.bmp | .pcx)";
					}
					
				});
				
				if (Settings.bgPreview) {
					FilePreviewPanel fpp = new FilePreviewPanel(FilePreviewPanel.BACKGROUND);
					
					fc.setAccessory(fpp);
					fc.addPropertyChangeListener(fpp);
				}
				
				int res = fc.showOpenDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					textField_3.setText(fc.getSelectedFile().getName());
					
					Data.bgFile = fc.getSelectedFile();
					
					Data.map.backgroundImageFile = fc.getSelectedFile().getName();
					Data.lp.setBackground();
					Data.lp.setTileset();
					
					Data.lp.repaint();
					
					Data.tp.setTileset();
					
					Data.fileChanged = true;
				}
			}
		});
		btnSelect.setBounds(183, 85, 66, 23);
		add(btnSelect);
		
		JLabel lblMusic = new JLabel("Music:");
		lblMusic.setBounds(10, 114, 46, 14);
		add(lblMusic);
		
		textField_4 = new JTextField();
		textField_4.setBounds(71, 111, 108, 20);
		add(textField_4);
		textField_4.setColumns(10);
		
		JButton btnSelect_1 = new JButton("Select");
		btnSelect_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser(Settings.MUSIC_PATH);
				fc.setDialogTitle("Select a music file...");
				fc.setAcceptAllFileFilterUsed(false);
				
				fc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File e) {
						boolean acceptCondition = e.getName().toLowerCase().endsWith(".xm") | e.getName().toLowerCase().endsWith(".mod") | e.getName().toLowerCase().endsWith(".it") | e.getName().toLowerCase().endsWith(".s3m");
						
						if (Data.mode == Constants.MODE_ENHANCED || Data.mode == Constants.MODE_CE) {
							acceptCondition |= e.getName().toLowerCase().endsWith(".ogg") | e.getName().toLowerCase().endsWith(".mp3");
						}
						
						return e.isDirectory() | acceptCondition && e.getName().length() < 13;
					}

					@Override
					public String getDescription() {
						String fileExtensions = ".xm | .mod | .it | .s3m";
						
						if (Data.mode == Constants.MODE_ENHANCED || Data.mode == Constants.MODE_CE) {
							fileExtensions += " | .ogg | .mp3";
						}
						
						return "Music file (" + fileExtensions + ")";
					}
					
				});
				
				int res = fc.showOpenDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					textField_4.setText(fc.getSelectedFile().getName());
					
					Data.map.musicFile = fc.getSelectedFile().getName();
					
					Data.fileChanged = true;
				}
			}
		});
		
		btnSelect_1.setBounds(183, 110, 66, 23);
		add(btnSelect_1);
		
		JLabel lblLevelNr = new JLabel("Level nr.:");
		lblLevelNr.setBounds(10, 139, 46, 14);
		add(lblLevelNr);
		
		spinner = new JSpinner();
		spinner.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		spinner.setBounds(71, 136, 46, 20);
		add(spinner);
		
		spinner.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				Data.fileChanged = true;
			}
			
		});
		
		JLabel lblTime = new JLabel("Time (sec):");
		lblTime.setBounds(123, 139, 60, 14);
		add(lblTime);
		
		spinner_1 = new JSpinner();
		spinner_1.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		spinner_1.setBounds(183, 136, 66, 20);
		add(spinner_1);
		
		spinner_1.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				Data.fileChanged = true;
			}
			
		});
		
		JLabel lblScrolling = new JLabel("Scrolling:");
		lblScrolling.setBounds(10, 164, 46, 14);
		add(lblScrolling);
		
		comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"Static", "Vertical", "Horizontal", "Vertical & Horizontal"}));
		comboBox.setBounds(71, 161, 108, 20);
		add(comboBox);
		
		comboBox.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Data.fileChanged = true;
			}
			
		});
		
		JLabel lblSpecial = new JLabel("Special:");
		lblSpecial.setBounds(10, 193, 46, 14);
		add(lblSpecial);
		
		comboBox_1 = new JComboBox();
		comboBox_1.setModel(new DefaultComboBoxModel(new String[] {"Normal", "Rain", "Leaves", "Rain/Leaves", "Snow"}));
		comboBox_1.setBounds(71, 190, 108, 20);
		add(comboBox_1);
		
		comboBox_1.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Data.fileChanged = true;
			}
			
		});
		
		JLabel lblIcon = new JLabel("Icon:");
		lblIcon.setBounds(10, 221, 46, 14);
		add(lblIcon);
		
		comboBox_2 = new JComboBox();
		comboBox_2.setModel(new DefaultComboBoxModel(new String[] {"Question mark", "Forest Hill", "Forest Hill at night", "Deep Forest", "Deep Forest at Night", "Field", "Field at night", "Mountains", "Castle", "Red Castle", "Cave", "Boss Battle", "Factory", "Custom icon #14", "Custom icon #15", "Custom icon #16", "Custom icon #17", "Custom icon #18", "Custom icon #19", "Custom icon #20", "Custom icon #21", "Custom icon #22"}));
		comboBox_2.setBounds(71, 218, 108, 20);
		add(comboBox_2);
		
		comboBox_2.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				Data.fileChanged = true;
			}
			
		});
		
		JLabel lblMapPosition = new JLabel("Position:");
		lblMapPosition.setBounds(10, 380, 66, 14);
		add(lblMapPosition);
		
		JLabel lblX = new JLabel("X:");
		lblX.setBounds(10, 404, 29, 14);
		add(lblX);
		
		spinner_2 = new JSpinner();
		spinner_2.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		spinner_2.setBounds(27, 401, 49, 20);
		add(spinner_2);
		
		JLabel lblY = new JLabel("Y:");
		lblY.setBounds(86, 404, 29, 14);
		add(lblY);
		
		spinner_3 = new JSpinner();
		spinner_3.setModel(new SpinnerNumberModel(new Integer(0), new Integer(0), null, new Integer(1)));
		spinner_3.setBounds(106, 401, 49, 20);
		add(spinner_3);
		
		JButton btnNewButton = new JButton("Set position");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SetMapPositionDialog(spinner_2, spinner_3, comboBox_2);
			}
		});
		btnNewButton.setBounds(10, 429, 145, 23);
		add(btnNewButton);
		
		JLabel lblSecretLevelNr = new JLabel("Secret Level nr.:");
		lblSecretLevelNr.setBounds(10, 262, 86, 14);
		add(lblSecretLevelNr);
		
		JLabel lblUnlockedBy = new JLabel("Unlocked by:");
		lblUnlockedBy.setBounds(10, 290, 66, 14);
		add(lblUnlockedBy);
		
		comboBox_3 = new JComboBox();
		comboBox_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Data.fileChanged = true;
			}
		});
		comboBox_3.setModel(new DefaultComboBoxModel(new String[] {"Score", "Collectable"}));
		comboBox_3.setBounds(86, 287, 93, 20);
		add(comboBox_3);
		
		spinner_4 = new JSpinner();
		spinner_4.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				Data.fileChanged = true;
			}
		});
		spinner_4.setBounds(106, 259, 73, 20);
		add(spinner_4);
		
		JLabel lblUnlockScore = new JLabel("Unlock score:");
		lblUnlockScore.setBounds(10, 318, 86, 14);
		add(lblUnlockScore);
		
		JLabel lblUnlockCollectables = new JLabel("Unlock collectables:");
		lblUnlockCollectables.setBounds(10, 349, 93, 14);
		add(lblUnlockCollectables);
		
		spinner_5 = new JSpinner();
		spinner_5.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Data.fileChanged = true;
			}
		});
		spinner_5.setBounds(106, 346, 73, 20);
		add(spinner_5);
		
		spinner_6 = new JSpinner();
		spinner_6.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Data.fileChanged = true;
			}
		});
		spinner_6.setBounds(106, 315, 73, 20);
		add(spinner_6);
		
		JLabel lblSwitch = new JLabel("Switch 1:");
		lblSwitch.setBounds(10, 463, 46, 14);
		add(lblSwitch);
		
		spinner_7 = new JSpinner();
		spinner_7.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Data.fileChanged = true;
			}
		});
		spinner_7.setBounds(71, 460, 84, 20);
		add(spinner_7);
		
		JLabel lblSwitch_1 = new JLabel("Switch 2:");
		lblSwitch_1.setBounds(10, 488, 46, 14);
		add(lblSwitch_1);
		
		spinner_8 = new JSpinner();
		spinner_8.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Data.fileChanged = true;
			}
		});
		spinner_8.setBounds(71, 485, 84, 20);
		add(spinner_8);
		
		JLabel lblSwitch_2 = new JLabel("Switch 3:");
		lblSwitch_2.setBounds(10, 513, 46, 14);
		add(lblSwitch_2);
		
		spinner_9 = new JSpinner();
		spinner_9.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				Data.fileChanged = true;
			}
		});
		spinner_9.setBounds(71, 510, 84, 20);
		add(spinner_9);
		
		//setMap();
	}
	
	public void saveChanges() {
		Data.map.mapName = textField.getText();
		Data.map.authorName = textField_1.getText();
		Data.map.tilesetImageFile = textField_2.getText();
		Data.map.backgroundImageFile = textField_3.getText();
		Data.map.musicFile = textField_4.getText();
		
		Data.map.levelNumber = (int) spinner.getValue();
		Data.map.time = (int) spinner_1.getValue();
		
		Data.map.background = comboBox.getSelectedIndex();
		
		Data.map.weather = comboBox_1.getSelectedIndex();
		Data.map.icon = comboBox_2.getSelectedIndex();
		
		Data.map.x = (int) spinner_2.getValue();
		Data.map.y = (int) spinner_3.getValue();
		
		Data.map.levelNumberSecret = (int) spinner_4.getValue();
		Data.map.unlock_collectables = (int) spinner_5.getValue();
		Data.map.unlock_score = (int) spinner_6.getValue();
		Data.map.unlock_type = comboBox_3.getSelectedIndex();
		
		Data.map.switch1Time = (int) spinner_7.getValue();
		Data.map.switch2Time = (int) spinner_8.getValue();
		Data.map.switch3Time = (int) spinner_9.getValue();
	}
	
	public void setMap() {
		textField.setText(Data.map.mapName);
		textField_1.setText(Data.map.authorName);
		textField_2.setText(Data.map.tilesetImageFile);
		textField_3.setText(Data.map.backgroundImageFile);
		textField_4.setText(Data.map.musicFile);
		
		spinner.setValue(Data.map.levelNumber);
		spinner_1.setValue(Data.map.time);
		
		comboBox.setSelectedIndex(Data.map.background);
		comboBox_1.setSelectedIndex(Data.map.weather);
		comboBox_2.setSelectedIndex(Data.map.icon);
		
		spinner_2.setValue(Data.map.x);
		spinner_3.setValue(Data.map.y);
		
		spinner_4.setValue(Data.map.levelNumberSecret);
		spinner_5.setValue(Data.map.unlock_collectables);
		spinner_6.setValue(Data.map.unlock_score);
		comboBox_3.setSelectedIndex(Data.map.unlock_type);
		
		spinner_7.setValue(Data.map.switch1Time);
		spinner_8.setValue(Data.map.switch2Time);
		spinner_9.setValue(Data.map.switch3Time);
	}
}
