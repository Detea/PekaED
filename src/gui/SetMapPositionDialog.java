package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import data.Settings;

public class SetMapPositionDialog extends JDialog {

	JSpinner sx, sy;
	JComboBox ci;
	
	public SetMapPositionDialog(JSpinner sx, JSpinner sy, JComboBox ci) {
		this.sx = sx;
		this.sy = sy;
		this.ci = ci;
		
		setup();
	}
	
	private class MapPanel extends JPanel implements MouseListener {

		BufferedImage bg, iconSheet;
		ArrayList<BufferedImage> iconList = new ArrayList<BufferedImage>();
		
		JSpinner sx, sy;
		JComboBox ci;
		
		public MapPanel(JSpinner sx, JSpinner sy, JComboBox ci) {
			this.sx = sx;
			this.sy = sy;
			this.ci = ci;
			
			addMouseListener(this);
			
			// ...wtf
			ci.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					repaint();
				}
				
			});
			
			try {
				bg = ImageIO.read(new File(Settings.BASE_PATH + File.separatorChar + "gfx" + File.separatorChar + "MAP.bmp"));
				iconSheet = ImageIO.read(new File(Settings.BASE_PATH + File.separatorChar + "gfx" + File.separatorChar + "PK2STUFF.bmp"));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			for (int i = 0; i < 22; i++) {
				iconList.add(iconSheet.getSubimage((i * 28) + 1, iconSheet.getHeight() - 28, 27, 27));
			}
			
			for (int i = 0; i < iconList.size(); i++) {
				BufferedImage bb = iconList.get(i);
				BufferedImage b2 = new BufferedImage(27, 27, BufferedImage.TYPE_INT_ARGB);
				
				int oldRGB = new Color(155, 232, 224).getRGB();

				for (int x = 0; x < bb.getWidth(); x++) {
					for (int y = 0; y < bb.getHeight(); y++) {
						if (bb.getRGB(x, y) != oldRGB) {
							b2.setRGB(x, y, bb.getRGB(x, y));
						}
					}
				}
			    
			    iconList.set(i, b2);
			}
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			g.drawImage(bg, 0, 0, null);
			g.drawImage(iconList.get(ci.getSelectedIndex()), (int) sx.getValue(), (int) sy.getValue(), null);
		}
		
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
			sx.setValue(e.getX() - (27 / 2));
			sy.setValue(e.getY() - (27 / 2));
			
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public void setup() {
		add(new MapPanel(sx, sy, ci), BorderLayout.CENTER);
		
		setSize(new Dimension(640, 480));
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle("Set position on map");
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}
}
