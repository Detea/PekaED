package gui.windows.palettewindow;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;

import data.Data;
import data.Settings;

import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import java.awt.Component;
import javax.swing.Box;
import java.awt.GridLayout;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

public class PaletteFrame extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	
	public PaletteFrame() {
		setResizable(false);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException
				| UnsupportedLookAndFeelException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
		
		Image img = null;
		try {
			img = ImageIO.read(getClass().getResource("/pkedit.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		setTitle("Palette");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 522, 479);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBounds(5, 5, 513, 20);
		contentPane.add(panel);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		
		JLabel lblFile = new JLabel("File:");
		panel.add(lblFile);
		
		PaletteBoard panel_3 = new PaletteBoard();
		panel_3.setBounds(0, 0, 416, 418);
		
		textField = new JTextField();
		panel.add(textField);
		textField.setColumns(10);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		panel.add(horizontalStrut);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBounds(436, 25, 82, 412);
		contentPane.add(panel_1);
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.Y_AXIS));
		
		JPanel panel_4 = new JPanel();
		panel_1.add(panel_4);
		panel_4.setLayout(null);
		
		JButton btnBrowse = new JButton("Browse");
		btnBrowse.setBounds(0, 0, 80, 23);
		panel_4.add(btnBrowse);
		btnBrowse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser fc = new JFileChooser();
				
				fc.setDialogTitle("Browse for background image...");
				
				fc.setFileFilter(new FileFilter() {

					@Override
					public boolean accept(File f) {
						return f.getName().toLowerCase().endsWith("bmp") | f.isDirectory();
					}

					@Override
					public String getDescription() {
						return "BMP Image file";
					}
					
				});
				
				int res = fc.showOpenDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					textField.setText(fc.getSelectedFile().getAbsolutePath());
				}
			}
		});
		
		JButton btnNewButton = new JButton("Open");
		btnNewButton.setBounds(0, 23, 80, 23);
		panel_4.add(btnNewButton);
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					BufferedImage img = ImageIO.read(new File(textField.getText()));
					
					panel_3.setColorModel(img.getColorModel());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		JButton btnSave = new JButton("Save");
		btnSave.setBounds(0, 46, 80, 23);
		panel_4.add(btnSave);
		
		Component verticalStrut = Box.createVerticalStrut(20);
		panel_1.add(verticalStrut);
		
		JLabel lblR = new JLabel("Red:");
		panel_1.add(lblR);
		
		JLabel lblNewLabel = new JLabel("New label");
		panel_1.add(lblNewLabel);
		
		JLabel lblG = new JLabel("Green:");
		panel_1.add(lblG);
		
		JLabel lblNewLabel_1 = new JLabel("New label");
		panel_1.add(lblNewLabel_1);
		
		JLabel lblB = new JLabel("Blue:");
		panel_1.add(lblB);
		
		JLabel lblNewLabel_2 = new JLabel("New label");
		panel_1.add(lblNewLabel_2);
		
		JPanel panel_2 = new JPanel();
		panel_2.setBounds(5, 25, 364, 388);
		contentPane.add(panel_2);
		panel_2.setLayout(null);
		
		panel_2.add(panel_3);
		
		if (Data.map != null) {
			textField.setText(Settings.BASE_PATH + "\\gfx\\scenery\\" + Data.map.getBackground());
			
			BufferedImage img2;
			try {
				img2 = ImageIO.read(new File(textField.getText()));
				
				panel_3.setColorModel(img2.getColorModel());
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		setVisible(true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	}
}
