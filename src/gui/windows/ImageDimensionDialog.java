package gui.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import data.Data;
import data.Settings;

public class ImageDimensionDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	
	private String file;
	private BufferedImage img;
	int x, y, w, h;

	double scale;
	
	JPanel panel;
	JScrollPane scrollPane;
	JSlider slider;
	
	JSpinner sx, sy, sw, sh;
	
	JButton okButton;
	
	public ImageDimensionDialog(String file, JSpinner sx, JSpinner sy, JSpinner sw, JSpinner sh) {
		this.sx = sx;
		this.sy = sy;
		this.sw = sw;
		this.sh = sh;
		
		Image img2 = null;
		try {
			img2 = ImageIO.read(getClass().getResource("/pkedit.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		setIconImage(img2);
		
		setMinimumSize(new Dimension(640, 490));
		setResizable(false);
		setPreferredSize(new Dimension(640, 480));
		setTitle("Select image dimensions");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BorderLayout(0, 0));
		{
			scrollPane = new JScrollPane();
			contentPanel.add(scrollPane, BorderLayout.CENTER);
			{
				panel = new JPanel() {
					public void paintComponent(Graphics g) {
						super.paintComponent(g);
						
						Graphics2D g2d = (Graphics2D) g;
						g2d.scale(scale, scale);
						
						g2d.drawImage(img, 0, 0, null);
						
						if (x > 0 && y > 0 && w > 0 && h > 0) {
							g2d.setColor(new Color(0f, 0f, 0f, 0.5f));
							g2d.fillRect(x, y, w, h);
						}
					}
				};
				
				panel.addMouseListener(new MouseListener() {

					@Override
					public void mouseClicked(MouseEvent arg0) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void mouseEntered(MouseEvent arg0) {
						
					}

					@Override
					public void mouseExited(MouseEvent arg0) {
						
					}

					@Override
					public void mousePressed(MouseEvent e) {
						x = (int) (e.getX() / scale);
						y = (int) (e.getY() / scale);
						
						w = 0;
						h = 0;
						
						panel.repaint();
					}

					@Override
					public void mouseReleased(MouseEvent arg0) {
						
					}
					
				});
				
				panel.addMouseMotionListener(new MouseMotionListener() {

					@Override
					public void mouseDragged(MouseEvent e) {
						w = (int) ((e.getX() / scale) - x);
						h = (int) ((e.getY() / scale) - y);
						
						w += 1;
						h += 1;
						
						panel.repaint();
					}

					@Override
					public void mouseMoved(MouseEvent arg0) {
				
					}
		
					
				});
				
				scrollPane.setViewportView(panel);
			}
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton btnReset = new JButton("Reset");
				btnReset.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent arg0) {
						slider.setValue(100);
						
						scale = 1;
					}
				});
				buttonPane.add(btnReset);
			}
			{
				JLabel lblZoom = new JLabel("Zoom:");
				buttonPane.add(lblZoom);
			}
			{
				slider = new JSlider();
				slider.setMinimum(10);
				slider.setMaximum(1000);
				slider.addChangeListener(new ChangeListener() {
					public void stateChanged(ChangeEvent arg0) {
						scale = slider.getValue() / 100f;
						
						panel.setPreferredSize(new Dimension((int) (640 * scale), (int) (480 * scale)));
						scrollPane.getViewport().revalidate();
						
						panel.repaint();
					}
				});
				slider.setMinorTickSpacing(10);
				slider.setValue(100);
				slider.setPreferredSize(new Dimension(150, 26));
				buttonPane.add(slider);
			}
			{
				JSeparator separator = new JSeparator();
				separator.setPreferredSize(new Dimension(300, 0));
				separator.setMinimumSize(new Dimension(50, 50));
				buttonPane.add(separator);
			}
			{
				okButton = new JButton("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						sx.setValue(x);
						sy.setValue(y);
						sw.setValue(w);
						sh.setValue(h);
						
						dispose();
					}
				});
				okButton.setActionCommand("OK");
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
		
		this.file = file;
		
		File imageFile = null;
		
		try {
			if (Data.currentEpisodePath != null) {
				imageFile = new File(Data.currentEpisodePath + "\\sprites\\" + file);
				
				if (!imageFile.exists()) {
					imageFile = new File(Settings.SPRITE_PATH + file);
				}
			}
			
			img = ImageIO.read(imageFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

}
