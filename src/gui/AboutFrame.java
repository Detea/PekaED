package gui;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.border.EmptyBorder;

import data.Settings;

public class AboutFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public AboutFrame() {
		setTitle("About");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 235);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblPekkaKana = new JLabel("Pekka Kana 2 Editor v.1.1");
		lblPekkaKana.setFont(new Font("Tahoma", Font.PLAIN, 18));
		lblPekkaKana.setBounds(104, 11, 254, 26);
		contentPane.add(lblPekkaKana);
		
		JLabel lblProgrammedByX = new JLabel("Github:");
		lblProgrammedByX.setBounds(193, 60, 91, 14);
		contentPane.add(lblProgrammedByX);
		
		JLabel lblAllRights = new JLabel("Pekka Kana 2 belongs to pistegamez.");
		lblAllRights.setBounds(117, 116, 182, 14);
		contentPane.add(lblAllRights);
		
		JLabel lblSupportThemHere = new JLabel("Play their games at:");
		lblSupportThemHere.setBounds(155, 135, 123, 14);
		contentPane.add(lblSupportThemHere);
		
		JLabel lblPistegamez = new JLabel("http://www.pistegamez.net");
		lblPistegamez.setForeground(Color.BLUE);
		lblPistegamez.setBounds(137, 160, 182, 14);
		contentPane.add(lblPistegamez);
		
		lblPistegamez.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(new URI("http://www.pistegamez.net"));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		JSeparator separator = new JSeparator();
		separator.setBounds(-13, 44, 467, 5);
		contentPane.add(separator);
		
		JLabel lblGithub = new JLabel("https://github.com/Detea/PekaED");
		lblGithub.setForeground(Color.BLUE);
		lblGithub.setBounds(131, 85, 168, 14);
		contentPane.add(lblGithub);
		
		lblPistegamez.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (Desktop.isDesktopSupported()) {
						try {
							Desktop.getDesktop().browse(new URI("https://github.com/Detea/PekaED"));
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		Image img = null;
		
		try {
			img = ImageIO.read(getClass().getResource("/pkedit.png"));
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		setIconImage(img);
		
		setVisible(true);
	}
}
