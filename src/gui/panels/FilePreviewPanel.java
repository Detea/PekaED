package gui.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.SimpleDateFormat;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;

import pekkakana.PK2Sprite;

public class FilePreviewPanel extends JPanel implements PropertyChangeListener {

	public static final int BACKGROUND = 0;
	public static final int TILESET = 1;
	public static final int SPRITE = 2;	
	
	double scale = 1;
	
	int preview;
	
	private BufferedImage image;
	private int x, y, width = 256, height = 295, imgWidth = 0, imgHeight = 0;

	JLabel lblSprNameVal, lblSprEnemyVal, lblSprCreationVal, lblSprModifiedVal, lblSprTypeVal;
	
	String[] typeStr = new String[] {"Character Sprite", "Bonus Item Sprite", "Ammo Sprite", "Teleport Sprite", "Background Sprite", "Collectable Sprite", "Checkpoint Sprite", "Exit Sprite"};
	
	public FilePreviewPanel(int preview) {
		this.preview = preview;
		
		JLabel lblSprName = new JLabel(" Name: ");
		JLabel lblSprEnemy = new JLabel(" Enemy: ");
		JLabel lblSprCreation = new JLabel(" Creation: ");
		JLabel lblSprModified = new JLabel(" Modified: ");
		JLabel lblSprType = new JLabel(" Type: ");
		
		lblSprName.setFont(new Font(lblSprName.getFont().getFontName(), Font.BOLD, lblSprName.getFont().getSize()));
		lblSprEnemy.setFont(new Font(lblSprEnemy.getFont().getFontName(), Font.BOLD, lblSprEnemy.getFont().getSize()));
		lblSprCreation.setFont(new Font(lblSprCreation.getFont().getFontName(), Font.BOLD, lblSprCreation.getFont().getSize()));
		lblSprModified.setFont(new Font(lblSprModified.getFont().getFontName(), Font.BOLD, lblSprModified.getFont().getSize()));
		lblSprType.setFont(new Font(lblSprType.getFont().getFontName(), Font.BOLD, lblSprType.getFont().getSize()));
		
		lblSprNameVal = new JLabel();
		lblSprEnemyVal = new JLabel();
		lblSprCreationVal = new JLabel();
		lblSprModifiedVal = new JLabel();
		lblSprTypeVal = new JLabel();
		
		switch (preview) {
			case BACKGROUND:
				width = 640 / 2;
				height = 480 / 2;
				break;
				
			case TILESET:
				width = 320;
				height = 480;
				break;
		
			case SPRITE:
				width = 256;
				height = 200;
				break;
		}
		
		ImagePanel p = new ImagePanel();
		p.setPreferredSize(new Dimension(width, height));
		
		JPanel bottomPanel = new JPanel();
		bottomPanel.setPreferredSize(new Dimension(width, 80));
		bottomPanel.setLayout(new GridLayout(6, 2));
		
		setPreferredSize(new Dimension(width, height + 80));
		
		if (preview == SPRITE) {
			bottomPanel.add(lblSprName);
			bottomPanel.add(lblSprNameVal);
			bottomPanel.add(lblSprEnemy);
			bottomPanel.add(lblSprEnemyVal);
			bottomPanel.add(lblSprType);
			bottomPanel.add(lblSprTypeVal);
			bottomPanel.add(lblSprCreation);
			bottomPanel.add(lblSprCreationVal);
			bottomPanel.add(lblSprModified);
			bottomPanel.add(lblSprModifiedVal);
		}
		
		add(p, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent e) {
		String pn = e.getPropertyName();
		
		if (pn.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
			File selectedFile = (File) e.getNewValue();
			
			String name = "";
			
			if (selectedFile != null) {
				name = selectedFile.getAbsolutePath();
			}
			
			if (!name.isEmpty()) {
				if (name.toLowerCase().endsWith(".bmp")) {
					try {
						image = ImageIO.read(new File(name));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else if (name.toLowerCase().endsWith(".spr") || name.toLowerCase().endsWith(".cespr")) {
					PK2Sprite spr = new PK2Sprite();
					
					if (spr.checkVersion(new File(name)) > -1) {
						spr.loadFile(new File(name));
						spr.loadBufferedImage();
						
						image = spr.image;
						
						if (image != null) {
							if (preview == SPRITE) {
								
								if (image.getWidth() > width || image.getHeight() > height) {
									imgWidth = 
									
									x = (width / 2) - (imgWidth / 2);
									y = (height / 2) - (imgHeight / 2);
								} else {
									imgWidth = image.getWidth();
									imgHeight = image.getHeight();
								}
								
								if (image.getWidth() > width) {
									imgWidth = width;
									imgHeight = (imgWidth * image.getHeight()) / image.getWidth();
								}
								
								if (image.getHeight() > height) {
									imgHeight = height;
									imgWidth = (imgHeight* image.getWidth()) / image.getHeight();
								}
								
								x = (width / 2) - (imgWidth / 2);
								y = (height / 2) - (imgHeight / 2);
							}
						}
						
						lblSprNameVal.setText(spr.getName());
						
						if (spr.enemy) {
							lblSprEnemyVal.setText("Yes");
						} else {
							lblSprEnemyVal.setText("No");
						}
						
						lblSprTypeVal.setText(typeStr[spr.type - 1]);
						
						try {
							Path f = Paths.get(selectedFile.getPath());
							BasicFileAttributes a = Files.readAttributes(f, BasicFileAttributes.class);
						
							SimpleDateFormat df = new SimpleDateFormat("HH:mm:ss dd.MM.yy");
							
							lblSprCreationVal.setText(df.format(a.creationTime().toMillis()));
							lblSprModifiedVal.setText(df.format(a.lastModifiedTime().toMillis()));
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				}

				if (preview == BACKGROUND) {
					scale = 0.5f;

					x = 0;
					y = (int) ((image.getHeight() * 0.5f) / 2) - (480 / 8);
				} else if (preview == TILESET) {
					x = 0;
					y = 0;
				}
				
				repaint();
			}
		}
	}

	private class ImagePanel extends JPanel {
		public ImagePanel() {
			setBackground(Color.LIGHT_GRAY);
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g;
			
			g.setColor(Color.black);
			g.drawRect(0, 0, width - 1, height - 1);
			
			g2d.scale(scale, scale);
			
			if (image != null) {
				if (imgWidth != 0 || imgHeight != 0) {
					g2d.drawImage(image, x, y, imgWidth, imgHeight, null);
				} else {
					g2d.drawImage(image, x, y, null);
				}
			}
		}
	}
}
