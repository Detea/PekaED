package gui.windows;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JSpinner;

import data.Data;
import data.Settings;
import pekkakana.PK2Map;

public class SetMapPositionDialog extends JDialog {

	JSpinner sx, sy;
	JComboBox ci;
	
	ArrayList<Integer> xPos = new ArrayList<Integer>();
	ArrayList<Integer> yPos = new ArrayList<Integer>();
	ArrayList<Integer> icons = new ArrayList<Integer>();	

	public SetMapPositionDialog(JSpinner sx, JSpinner sy, JComboBox ci) {
		this.sx = sx;
		this.sy = sy;
		this.ci = ci;
		
		PK2Map p = new PK2Map();
		
		for (File f : Data.episodeFiles) {
			p.loadIconData(f.getAbsolutePath());
			
			xPos.add(p.x);
			yPos.add(p.y);
			icons.add(p.icon);
		}
		
		setup();
	}
	
	private class MapPanel extends JPanel implements MouseListener {

		BufferedImage bg, iconSheet;
		ArrayList<BufferedImage> iconList = new ArrayList<BufferedImage>();
		ArrayList<BufferedImage> otherIconList = new ArrayList<BufferedImage>(); // non descriptive name, but it's the half transparent icons
		
		JSpinner sx, sy;
		JComboBox ci;
		
		public MapPanel(JSpinner sx, JSpinner sy, JComboBox ci) {
			this.sx = sx;
			this.sy = sy;
			this.ci = ci;
			
			addMouseListener(this);
			
			ci.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					repaint();
				}
				
			});
			
			try {
				String filePath = Settings.BASE_PATH + File.separatorChar + "gfx" + File.separatorChar + "MAP.bmp";
				
				if (Data.currentFile != null) {
					if (new File(Data.currentFile.getParentFile().getAbsolutePath() + "\\MAP.bmp").exists()) {
						filePath = Data.currentFile.getParentFile().getAbsolutePath() + "\\MAP.bmp";
					}
				}
				
				bg = ImageIO.read(new File(filePath));
				iconSheet = ImageIO.read(new File(Settings.BASE_PATH + File.separatorChar + "gfx" + File.separatorChar + "PK2STUFF.bmp"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			byte[] isData = ((DataBufferByte) iconSheet.getRaster().getDataBuffer()).getData();
			BufferedImage tmpSheet = new BufferedImage(iconSheet.getWidth(), iconSheet.getHeight(), BufferedImage.TYPE_INT_ARGB);
			
			int[] tmps = ((DataBufferInt) tmpSheet.getRaster().getDataBuffer()).getData();
			
			for (int i = 0; i < tmps.length; i++) {
				if ((isData[i] & 0xFF) != 255) {
					tmps[i] = iconSheet.getColorModel().getRGB(isData[i]);
				}
			}
			
			iconSheet = tmpSheet; // Copying the processed image back again
			
			for (int i = 0; i < 22; i++) {
				iconList.add(iconSheet.getSubimage((i * 28) + 1, iconSheet.getHeight() - 28, 27, 27));
			}
			
			for (int i = 0; i < iconList.size(); i++) {
				BufferedImage bb = iconList.get(i);
				BufferedImage b2 = new BufferedImage(27, 27, BufferedImage.TYPE_INT_ARGB);

				for (int x = 0; x < bb.getWidth(); x++) {
					for (int y = 0; y < bb.getHeight(); y++) {
						b2.setRGB(x, y, bb.getRGB(x, y));
					}
				}
			    
			    iconList.set(i, b2);
			}
			
			otherIconList = new ArrayList<BufferedImage>();
			
			for (int i = 0; i < 22; i++) {
				otherIconList.add(iconSheet.getSubimage((i * 28) + 1, iconSheet.getHeight() - 28, 27, 27));
			}
			
			for (int j = 0; j < otherIconList.size(); j++) {
				int[] px = ((DataBufferInt) otherIconList.get(j).getRaster().getDataBuffer()).getData();
				
				for (int i = 0; i < px.length; i++) {
			    	px[i] &= 0x82FFFFFF; // Set transparency 
			    }
			}
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			g.drawImage(bg, 0, 0, null);
			
			for (int i = 0; i < xPos.size(); i++) {
				if (icons.get(i) != ci.getSelectedIndex()) {
					g.drawImage(otherIconList.get(icons.get(i)), xPos.get(i), yPos.get(i), null);
				}
			}
			
			g.drawImage(iconList.get(ci.getSelectedIndex()), (int) sx.getValue(), (int) sy.getValue(), null);
		}
		
		@Override
		public void mouseClicked(MouseEvent arg0) {
		}

		@Override
		public void mouseEntered(MouseEvent arg0) {
		}

		@Override
		public void mouseExited(MouseEvent arg0) {
		}

		@Override
		public void mousePressed(MouseEvent e) {
			sx.setValue(e.getX() - (27 / 2));
			sy.setValue(e.getY() - (27 / 2));
			
			repaint();
		}

		@Override
		public void mouseReleased(MouseEvent arg0) {	
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
