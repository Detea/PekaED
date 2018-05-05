package gui.windows.palettewindow;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;

import javax.swing.JPanel;

public class PaletteBoard extends JPanel implements MouseListener {

	private ColorModel cm;
	
	private BufferedImage buffer;
	
	int mx, my;
	Color col;
	
	public PaletteBoard() {
		setBackground(Color.LIGHT_GRAY);
		
		addMouseListener(this);
		
		buffer = new BufferedImage(24 * 15, 24 * 16, BufferedImage.TYPE_INT_ARGB);
		
		setPreferredSize(new Dimension(24 * 15, 24 * 16));
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (buffer != null) {
			g.drawImage(buffer, 0, 0, null);
			
			g.setColor(col);
			g.drawRect(mx, my, 24, 24);
		}
	}
	
	private void createBuffer() {
		Graphics g = buffer.getGraphics(); //C:\Users\Dennis\Downloads\pk2_r2-2\gfx\scenery\field_d3.bmp
		
		int x = 0, y = 0, i = 0;
		
		while (i < 256) {
			g.setColor(new Color(cm.getRGB(i)));
			g.fillRect(x * 24, y * 24, 24, 24);
			
			if (x == 15) {
				y++;
				x = 0;
			} else {
				x++;
			}
			
			i++;
		}
	}
	
	public void setColorModel(ColorModel cm) {
		this.cm = cm;
		
		createBuffer();
		
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	
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
		mx = e.getX() / 24;
		my = e.getY() / 24;
		
		int r, g, b;
		
		r = cm.getRed(mx + my * buffer.getWidth());
		g = cm.getRed(mx + my * buffer.getWidth());
		b = cm.getRed(mx + my * buffer.getWidth());
		
		col = new Color(255 - r, 255 - g, 255 - b);
		
		mx *= 24;
		my *= 24;
		
		repaint();
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}
