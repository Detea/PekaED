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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import data.Constants;
import data.Data;
import data.Settings;
import gui.windows.SetMapPositionDialog;
import pekkakana.PK2Map;

public class MapSettingsPanel extends JPanel {
	private JTextField textField;
	private JTextField textField_1;
	private JTextField textField_2;
	private JTextField textField_3;
	private JTextField textField_4;
	
	JSpinner spinner, spinner_1, spinner_2, spinner_3;
	JComboBox comboBox, comboBox_1, comboBox_2;

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
		
		textField_1 = new JTextField();
		textField_1.setBounds(71, 36, 108, 20);
		add(textField_1);
		textField_1.setColumns(10);
		
		JLabel lblTileset = new JLabel("Tileset:");
		lblTileset.setBounds(10, 61, 46, 14);
		add(lblTileset);
		
		textField_2 = new JTextField();
		textField_2.setBounds(71, 61, 108, 20);
		add(textField_2);
		textField_2.setColumns(10);
		
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
				
				int res = fc.showOpenDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					textField_2.setText(fc.getSelectedFile().getName());
					
					Data.tilesetFile = fc.getSelectedFile();
					
					Data.map.setTileset(fc.getSelectedFile().getName());
					
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
				
				int res = fc.showOpenDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					textField_3.setText(fc.getSelectedFile().getName());
					
					Data.bgFile = fc.getSelectedFile();
					
					Data.map.setBackground(fc.getSelectedFile().getName());
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
						
						if (Data.mode == Constants.MODE_ENHANCED) {
							acceptCondition |= e.getName().toLowerCase().endsWith(".ogg") | e.getName().toLowerCase().endsWith(".mp3");
						}
						
						return e.isDirectory() | acceptCondition && e.getName().length() < 13;
					}

					@Override
					public String getDescription() {
						String fileExtensions = ".xm | .mod | .it | .s3m";
						
						if (Data.mode == Constants.MODE_ENHANCED) {
							fileExtensions += " | .ogg | .mp3";
						}
						
						return "Music file (" + fileExtensions + ")";
					}
					
				});
				
				int res = fc.showOpenDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					textField_4.setText(fc.getSelectedFile().getName());
					
					Data.map.setMusic(fc.getSelectedFile().getName());
					
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
		lblMapPosition.setBounds(10, 253, 66, 14);
		add(lblMapPosition);
		
		JLabel lblX = new JLabel("X:");
		lblX.setBounds(10, 277, 29, 14);
		add(lblX);
		
		spinner_2 = new JSpinner();
		spinner_2.setBounds(27, 274, 49, 20);
		add(spinner_2);
		
		JLabel lblY = new JLabel("Y:");
		lblY.setBounds(86, 277, 29, 14);
		add(lblY);
		
		spinner_3 = new JSpinner();
		spinner_3.setBounds(106, 274, 49, 20);
		add(spinner_3);
		
		JButton btnNewButton = new JButton("Set position");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new SetMapPositionDialog(spinner_2, spinner_3, comboBox_2);
			}
		});
		btnNewButton.setBounds(10, 302, 145, 23);
		add(btnNewButton);
		
		//setMap();
	}
	
	public void saveChanges() {
		Data.map.setMapName(textField.getText());
		Data.map.setAuthor(textField_1.getText());
		Data.map.setTileset(textField_2.getText());
		Data.map.setBackground(textField_3.getText());
		Data.map.setMusic(textField_4.getText());
		
		Data.map.levelNumber = (int) spinner.getValue();
		Data.map.time = (int) spinner_1.getValue();
		
		Data.map.background = comboBox.getSelectedIndex();
		
		Data.map.weather = comboBox_1.getSelectedIndex();
		Data.map.icon = comboBox_2.getSelectedIndex();
		
		Data.map.x = (int) spinner_2.getValue();
		Data.map.y = (int) spinner_3.getValue();
	}
	
	public void setMap() {
		textField.setText(Data.map.getTitle());
		textField_1.setText(Data.map.getCreator());
		textField_2.setText(Data.map.getTileset());
		textField_3.setText(Data.map.getBackground());
		textField_4.setText(Data.map.getMusic());
		
		spinner.setValue(Data.map.levelNumber);
		spinner_1.setValue(Data.map.time);
		
		comboBox.setSelectedIndex(Data.map.background);
		comboBox_1.setSelectedIndex(Data.map.weather);
		comboBox_2.setSelectedIndex(Data.map.icon);
		
		spinner_2.setValue(Data.map.x);
		spinner_3.setValue(Data.map.y);
	}
}
