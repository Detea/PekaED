package gui.windows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import data.Data;

public class EpisodeSettingsDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField txtPkstuffbmp;
	private JTextField textField;
	
	private JSpinner spinner, spinner_1, spinner_2, spinner_3, spinner_4, spinner_5, spinner_6, spinner_7;

	private JCheckBox chckbxShowOnMap, chckbxShowInGame;
	
	public EpisodeSettingsDialog() {
		DataInputStream dis = null;
		
		setTitle("Episode settings");
		setBounds(100, 100, 476, 272);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		JLabel lblCollectables = new JLabel("Collectables:");
		lblCollectables.setBounds(10, 11, 61, 14);
		contentPanel.add(lblCollectables);
		
		chckbxShowOnMap = new JCheckBox("Show on map");
		chckbxShowOnMap.setBounds(10, 32, 97, 23);
		contentPanel.add(chckbxShowOnMap);
		
		chckbxShowInGame = new JCheckBox("Show in game");
		chckbxShowInGame.setBounds(109, 32, 97, 23);
		contentPanel.add(chckbxShowInGame);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(0, 62, 464, 137);
		contentPanel.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Map", null, panel, null);
		panel.setLayout(null);
		
		JLabel lblFile = new JLabel("File:");
		lblFile.setBounds(20, 14, 35, 14);
		panel.add(lblFile);
		
		txtPkstuffbmp = new JTextField();
		txtPkstuffbmp.setBounds(45, 11, 178, 20);
		panel.add(txtPkstuffbmp);
		txtPkstuffbmp.setColumns(10);
		
		Image img = null;
		try {
			img = ImageIO.read(getClass().getResource("/pkedit.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		setIconImage(img);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				
				if (!Data.currentEpisodePath.isEmpty()) {
					fc.setCurrentDirectory(new File(Data.currentEpisodePath));
				}
				
				fc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File e) {
						return e.getAbsolutePath().endsWith("bmp") || e.isDirectory();
					}

					@Override
					public String getDescription() {
						return "Windows Bitmap (*.bmp)";
					}
					
				});
				
				int res = fc.showOpenDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					txtPkstuffbmp.setText(fc.getSelectedFile().getName());
				}
			}
		});
		btnBrowse.setBounds(233, 10, 89, 23);
		panel.add(btnBrowse);
		
		JLabel lblX = new JLabel("X:");
		lblX.setBounds(20, 46, 23, 14);
		panel.add(lblX);
		
		spinner = new JSpinner();
		spinner.setBounds(45, 43, 54, 20);
		panel.add(spinner);
		
		JLabel lblY = new JLabel("Y:");
		lblY.setBounds(20, 72, 23, 14);
		panel.add(lblY);
		
		spinner_1 = new JSpinner();
		spinner_1.setBounds(45, 69, 54, 20);
		panel.add(spinner_1);
		
		spinner_2 = new JSpinner();
		spinner_2.setBounds(169, 42, 54, 20);
		panel.add(spinner_2);
		
		JLabel lblWidth = new JLabel("Width:");
		lblWidth.setBounds(128, 45, 46, 14);
		panel.add(lblWidth);
		
		JLabel lblHeight = new JLabel("Height:");
		lblHeight.setBounds(128, 71, 46, 14);
		panel.add(lblHeight);
		
		spinner_3 = new JSpinner();
		spinner_3.setBounds(169, 68, 54, 20);
		panel.add(spinner_3);
		
		JButton btnSet = new JButton("Select");
		btnSet.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new ImageDimensionDialog(txtPkstuffbmp.getText(), spinner, spinner_1, spinner_2, spinner_3);
			}
		});
		btnSet.setBounds(233, 42, 89, 23);
		panel.add(btnSet);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("In Game", null, panel_1, null);
		panel_1.setLayout(null);
		
		JLabel lblFile_1 = new JLabel("File:");
		lblFile_1.setBounds(10, 15, 23, 14);
		panel_1.add(lblFile_1);
		
		textField = new JTextField();
		textField.setBounds(43, 12, 209, 20);
		panel_1.add(textField);
		textField.setColumns(10);
		
		JLabel lblX_1 = new JLabel("X:");
		lblX_1.setBounds(10, 43, 46, 14);
		panel_1.add(lblX_1);
		
		spinner_4 = new JSpinner();
		spinner_4.setBounds(43, 43, 54, 20);
		panel_1.add(spinner_4);
		
		JLabel lblY_1 = new JLabel("Y:");
		lblY_1.setBounds(10, 71, 28, 14);
		panel_1.add(lblY_1);
		
		spinner_5 = new JSpinner();
		spinner_5.setBounds(43, 68, 54, 20);
		panel_1.add(spinner_5);
		
		JLabel lblWidth_1 = new JLabel("Width:");
		lblWidth_1.setBounds(155, 43, 46, 14);
		panel_1.add(lblWidth_1);
		
		spinner_6 = new JSpinner();
		spinner_6.setBounds(198, 43, 54, 20);
		panel_1.add(spinner_6);
		
		spinner_7 = new JSpinner();
		spinner_7.setBounds(198, 71, 54, 20);
		panel_1.add(spinner_7);
		
		JLabel lblHeight_1 = new JLabel("Height:");
		lblHeight_1.setBounds(155, 71, 46, 14);
		panel_1.add(lblHeight_1);
		
		JButton button = new JButton("Browse");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				
				if (!Data.currentEpisodePath.isEmpty()) {
					fc.setCurrentDirectory(new File(Data.currentEpisodePath));
				}
				
				fc.setFileFilter(new FileFilter() { 

					@Override
					public boolean accept(File e) {
						return e.getAbsolutePath().endsWith("bmp") || e.isDirectory();
					}

					@Override
					public String getDescription() {
						return "Windows Bitmap (*.bmp)";
					}
					
				});
				
				int res = fc.showOpenDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					textField.setText(fc.getSelectedFile().getName());
				}
			}
		});
		button.setBounds(262, 11, 89, 23);
		panel_1.add(button);
		
		JButton button_1 = new JButton("Select");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				new ImageDimensionDialog(textField.getText(), spinner_4, spinner_5, spinner_6, spinner_7);
			}
		});
		button_1.setBounds(262, 39, 89, 23);
		panel_1.add(button_1);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						File file = new File(Data.currentEpisodePath + "/data/episode.dat");
						
						if (!new File(Data.currentEpisodePath + "/data").exists()) {
							new File(Data.currentEpisodePath + "/data").mkdir();
						}
						
						if (Data.currentEpisodePath != null) {
							saveFile(file);
						} else {
							JFileChooser fc = new JFileChooser();
							fc.setDialogTitle("Choose episode's data folder...");
							
							fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
							
							int res = fc.showSaveDialog(null);
							
							if (res == JFileChooser.APPROVE_OPTION) {
								file = new File(fc.getSelectedFile().getAbsolutePath() + "/episode.dat");
								
								saveFile(file);
							}
						}
						
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						//TODO reset shit
						
						dispose();
					}
				});
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		if (new File(Data.currentEpisodePath + "/data/episode.dat").exists()) {
			try {
				dis = new DataInputStream(new FileInputStream(Data.currentEpisodePath + "/data/episode.dat"));
				
				chckbxShowOnMap.setSelected(dis.readBoolean());
				chckbxShowInGame.setSelected(dis.readBoolean());
				
				int len = Integer.reverseBytes(dis.readInt());
				String tmp = "";
				
				for (int i = 0; i < len; i++) {
					tmp += (char) dis.readByte();
				}
				
				textField.setText(tmp);
				
				len = Integer.reverseBytes(dis.readInt());
				tmp = "";
				
				for (int i = 0; i < len; i++) {
					tmp += (char) dis.readByte();
				}
				
				txtPkstuffbmp.setText(tmp);
				
				spinner.setValue(Integer.reverseBytes(dis.readInt()));
				spinner_1.setValue(Integer.reverseBytes(dis.readInt()));
				spinner_2.setValue(Integer.reverseBytes(dis.readInt()));
				spinner_3.setValue(Integer.reverseBytes(dis.readInt()));
				spinner_4.setValue(Integer.reverseBytes(dis.readInt()));
				spinner_5.setValue(Integer.reverseBytes(dis.readInt()));
				spinner_6.setValue(Integer.reverseBytes(dis.readInt()));
				spinner_7.setValue(Integer.reverseBytes(dis.readInt()));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				try {
					dis.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
	
	private void saveFile(File file) {
		try {
			spinner.commitEdit();
			spinner_1.commitEdit();
			spinner_2.commitEdit();
			spinner_3.commitEdit();
			spinner_4.commitEdit();
			spinner_5.commitEdit();
			spinner_6.commitEdit();
			spinner_7.commitEdit();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		boolean showMap = chckbxShowOnMap.isSelected();
		boolean showInGame = chckbxShowInGame.isSelected();
		
		String[] tmp = textField.getText().split("\\\\");
		
		String mapImage = tmp[tmp.length - 1];
		
		tmp = txtPkstuffbmp.getText().split("\\\\");
		String inGame = tmp[tmp.length - 1];
		
		int mx = (int) spinner.getValue();
		int my = (int) spinner_1.getValue();
		int mw = (int) spinner_2.getValue();
		int mh = (int) spinner_3.getValue();
		int gx = (int) spinner_4.getValue();
		int gy = (int) spinner_5.getValue();
		int gw = (int) spinner_6.getValue();
		int gh = (int) spinner_7.getValue();
		
		DataOutputStream dis = null;

		try {
			dis = new DataOutputStream(new FileOutputStream(file));

			dis.writeBoolean(showMap);
			dis.writeBoolean(showInGame);

			dis.writeInt(Integer.reverseBytes(mapImage.length()));

			for (int i = 0; i < mapImage.length(); i++) {
				dis.writeByte((byte) mapImage.charAt(i));
			}

			dis.writeInt(Integer.reverseBytes(inGame.length()));

			for (int i = 0; i < inGame.length(); i++) {
				dis.writeByte((byte) inGame.charAt(i));
			}

			dis.writeInt(Integer.reverseBytes(mx));
			dis.writeInt(Integer.reverseBytes(my));
			dis.writeInt(Integer.reverseBytes(mw));
			dis.writeInt(Integer.reverseBytes(mh));
			dis.writeInt(Integer.reverseBytes(gx));
			dis.writeInt(Integer.reverseBytes(gy));
			dis.writeInt(Integer.reverseBytes(gw));
			dis.writeInt(Integer.reverseBytes(gh));

			dis.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}