package gui.windows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import data.Constants;
import data.Data;
import data.Settings;

public class SettingsDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;
	/**
	 * Create the dialog.
	 */
	public SettingsDialog() {
		Image img = null;
		
		try {
			img = ImageIO.read(getClass().getResource("/pkedit.png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setIconImage(img);
		
		setTitle("Settings");
		setBounds(100, 100, 549, 219);
		getContentPane().setLayout(new BorderLayout());
		
		JButton okButton = new JButton("OK");
		
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		{
			JLabel lblNewLabel = new JLabel("Path to Pekka Kana 2:");
			lblNewLabel.setBounds(10, 15, 114, 14);
			contentPanel.add(lblNewLabel);
		}
		
		textField = new JTextField();
		textField.setBounds(120, 12, 305, 20);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(435, 11, 89, 23);
		contentPanel.add(btnBrowse);
		
		JLabel lblOnStartup = new JLabel("On startup:");
		lblOnStartup.setBounds(10, 60, 68, 14);
		contentPanel.add(lblOnStartup);
		
		JRadioButton rdbtnLoadLastEpisode = new JRadioButton("Load last episode");
		rdbtnLoadLastEpisode.setBounds(10, 81, 109, 23);
		contentPanel.add(rdbtnLoadLastEpisode);
		
		JRadioButton rdbtnCreateEmptyLevel = new JRadioButton("Create empty level");
		rdbtnCreateEmptyLevel.setBounds(10, 107, 124, 23);
		contentPanel.add(rdbtnCreateEmptyLevel);
		
		ButtonGroup rbGroup = new ButtonGroup();
		rbGroup.add(rdbtnLoadLastEpisode);
		rbGroup.add(rdbtnCreateEmptyLevel);
		
		JLabel lblLevelLimitOf = new JLabel("Level limit per episode:");
		lblLevelLimitOf.setBounds(208, 60, 114, 14);
		contentPanel.add(lblLevelLimitOf);
		
		JSpinner spinner = new JSpinner();
		spinner.setBounds(322, 57, 56, 20);
		contentPanel.add(spinner);
		
		JRadioButton rdbtnEnhancedMode = new JRadioButton("Enhanced mode");
		rdbtnEnhancedMode.setBounds(143, 81, 109, 23);
		contentPanel.add(rdbtnEnhancedMode);
		
		JRadioButton rdbtnLegacyMode = new JRadioButton("Legacy mode");
		rdbtnLegacyMode.setBounds(143, 107, 109, 23);
		
		ButtonGroup bgMode = new ButtonGroup();
		bgMode.add(rdbtnEnhancedMode);
		bgMode.add(rdbtnLegacyMode);
		
		contentPanel.add(rdbtnLegacyMode);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				
				// This could be done under the addActionListener for okButton, but it is really short
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
					}
				});
				
				buttonPane.add(cancelButton);
			}
		}
		
		if (!Settings.BASE_PATH.isEmpty()) {
			textField.setText(Settings.BASE_PATH);
		}
		
		if (Settings.loadEpisodeOnStartup) {
			rdbtnLoadLastEpisode.setSelected(true);
		} else {
			rdbtnCreateEmptyLevel.setSelected(true);
		}
		
		if (Settings.startInEnhancedMode) {
			rdbtnEnhancedMode.setSelected(true);
		} else {
			rdbtnLegacyMode.setSelected(true);
		}
		
		if (Data.mode == Constants.MODE_LEGACY) {
			spinner.setValue(Constants.LEGACY_LEVEL_LIMIT);
			spinner.setEnabled(false);
		} else {
			spinner.setValue(Constants.ENHANCED_LEVEL_LIMIT);
			spinner.setEnabled(true);
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
		
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Settings.BASE_PATH = textField.getText();
				
				Settings.setPaths();
				
				Settings.loadEpisodeOnStartup = rdbtnLoadLastEpisode.isSelected();
				Settings.startInEnhancedMode = rdbtnEnhancedMode.isSelected();
				
				if (Data.mode == Constants.MODE_ENHANCED) {
					Constants.ENHANCED_LEVEL_LIMIT = (int) spinner.getValue();
				}
				
				try {
					DataOutputStream dos = new DataOutputStream(new FileOutputStream("settings"));
					
					dos.writeUTF(Settings.BASE_PATH);
					dos.writeBoolean(Settings.loadEpisodeOnStartup);
					dos.writeBoolean(Settings.startInEnhancedMode);
					dos.writeInt(Constants.ENHANCED_LEVEL_LIMIT);
					
					dos.flush();
					dos.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				
				dispose();
			}
			
		});
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}
