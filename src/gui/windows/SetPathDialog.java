package gui.windows;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import data.Constants;
import data.Settings;

public class SetPathDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JTextField textField;

	/**
	 * Create the dialog.
	 */
	public SetPathDialog() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (InstantiationException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IllegalAccessException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (UnsupportedLookAndFeelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		setBounds(100, 100, 435, 159);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		Image img = null;
		
		try {
			img = ImageIO.read(getClass().getResource("/pkedit.png"));
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		setIconImage(img);
		
		JLabel lblSetThePath = new JLabel("Set the path to your Pekka Kana 2 installation:");
		lblSetThePath.setBounds(10, 11, 232, 14);
		contentPanel.add(lblSetThePath);
		
		textField = new JTextField();
		textField.setBounds(10, 36, 301, 20);
		contentPanel.add(textField);
		textField.setColumns(10);
		
		File file = new File("C:\\Program Files (x86)\\Pekka Kana 2");
		if (file.exists()) {
			textField.setText(file.getAbsolutePath());
		}
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				File file = new File("C:\\Program Files (x86)\\Pekka Kana 2");
				
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				
				if (file.exists()) {
					fc.setCurrentDirectory(file);
				}
				
				int res = fc.showSaveDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					textField.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		btnBrowse.setBounds(321, 35, 89, 23);
		contentPanel.add(btnBrowse);
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						File f = new File(textField.getText());
						
						if (f.exists()) {
							File f2 = new File("settings");
							
							try {
								if (f2.exists()) {
									f2.delete();
								}
								
								Settings.BASE_PATH = textField.getText();
								
								DataOutputStream dos = new DataOutputStream(new FileOutputStream("settings"));
								
								dos.writeUTF(Settings.BASE_PATH);
								dos.writeBoolean(false);
								dos.writeBoolean(false);
								dos.writeInt(100);
								
								dos.flush();
								dos.close();
								
								Settings.setPaths();
								dispose();
								
								new PekaEDGUI().setup();
							} catch (IOException e1) {
								JOptionPane.showMessageDialog(null, "Could'nt create settings file.\n" + e1.getMessage(), "Error", JOptionPane.OK_OPTION);
								
								e1.printStackTrace();
							}
						} else {
							JOptionPane.showMessageDialog(null, "Path is invalid", "Invalid path", JOptionPane.OK_OPTION);
						}
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
		}
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}
