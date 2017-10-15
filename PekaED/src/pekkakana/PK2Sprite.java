package pekkakana;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.Settings;

public class PK2Sprite {
	public int type;
	public char[] imageFile = new char[100];
	public char[][] soundFiles = new char[7][100];
	
	public String filename = "";
	
	int[] sounds = new int[7];
	
	int frames; // number of frames
	
	public PK2SpriteAnimation[] animation = new PK2SpriteAnimation[20];
	
	public int animations; // number of animations
	public int frameRate;
	
	public int frameX, frameY;
	
	public int frameWidth;
	public int frameHeight;
	public int frameDistance;
	
	public char[] name = new char[30];
	public int width, height;
	
	double weight;
	
	boolean enemy;
	
	int energy;
	int damage;
	
	int damageType;
	int immunity;
	int score;
	
	int[] AI = new int[10];
	
	public BufferedImage image;
	
	public PK2Sprite(String file) {
		loadFile(file);
		
		filename = file;
		
		loadBufferedImage();
	}
	
	public void loadFile(String file) {
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(new File(Settings.SPRITE_PATH + file)));
			
			char[] version = {'1', '.', '3', '\0'};
			boolean correctVersion = false;
			
			for (int i = 0; i < version.length; i++) {
				if (version[i] == dis.readByte()) {
					correctVersion = true;
				} else {
					correctVersion = false;
				}
			}
			
			if (correctVersion) {
				type = Integer.reverseBytes(dis.readInt());
				
				for (int i = 0; i < imageFile.length; i++) {
					imageFile[i] = (char) dis.readByte();
				}
				
				for (int i = 0; i < soundFiles.length; i++) {
					char[] amount = new char[100];
					readAmount(amount, dis);
					soundFiles[i] = amount;
				}
				

				// read data that isn't needed, but is yet present
				for (int i = 0; i < sounds.length; i++) {
					sounds[i] = dis.readInt();
				}
				
				frames = (int) (dis.readByte() & 0xFF);
				
				/*
				 	int[] sequence = new int[ANIMATION_MAX_SEQUENCES];
					int frames; // amount of frames
					boolean loop; // wether the animations loops, or not
				 */
				
				for (int i = 0; i < animation.length; i++) {
					int[] sequence = new int[10];
					int frames = 0;
					boolean loop = false;
					
					for (int j = 0; j < sequence.length; j++) {
						sequence[j] = (int) (dis.readByte() & 0xFF);
					}
					
					frames = (int) (dis.readByte() & 0xFF);
					loop = dis.readBoolean();
					
					animation[i] = new PK2SpriteAnimation(sequence, frames, loop);
				}
				
				animations = (int) (dis.readByte() & 0xFF);
				frameRate = (int) (dis.readByte() & 0xFF);
				
				dis.readByte(); //Padding? not documented, though
				
				frameX = Integer.reverseBytes(dis.readInt());
				frameY = Integer.reverseBytes(dis.readInt());
				
				frameWidth = Integer.reverseBytes(dis.readInt());
				frameHeight = Integer.reverseBytes(dis.readInt());
				frameDistance = Integer.reverseBytes(dis.readInt());
				
				// read the sprite's name
				readAmount(name, dis);
				
				// undocumented padding, again?!
				dis.readShort();
				
				width = Integer.reverseBytes(dis.readInt());
				height = Integer.reverseBytes(dis.readInt());
			} else {
				System.out.println("Sprite file wrong version");
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void readAmount(char[] array, DataInputStream dis) throws IOException {
		for (int i = 0; i < array.length; i++) {
			array[i] = (char) dis.readByte();
		}
	}
	
	public String getName() {
		return cleanString(name);
	}
	
	private void loadBufferedImage() {
		try {
			BufferedImage tilesheet = ImageIO.read(new File(Settings.SPRITE_PATH + cleanString(imageFile)));
			
			image = tilesheet.getSubimage(frameX, frameY, frameWidth, frameHeight);
			
			BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			 
		    // make color transparent
		    int oldRGB = new Color(148, 209, 222).getRGB();
		    int oldRGB2 = new Color(128, 205, 214).getRGB();
		    int oldRGB3 = new Color(155, 232, 224).getRGB();
		 
		    for (int i = 0; i < image.getWidth(); i++) {
		    	for (int j = 0; j < image.getHeight(); j++) {
		    		if (image.getRGB(i, j) != oldRGB && image.getRGB(i, j) != oldRGB2 && image.getRGB(i, j) != oldRGB3) {
		    			result.setRGB(i, j, image.getRGB(i, j));
		    		}
		    	}
		    }
		    
		    image = result;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String cleanString(char[] array) {
		StringBuilder sb = new StringBuilder();
		
		int i = 0;
		while (array[i] != 0x0) {		
			sb.append(array[i]);
			
			i++;
		}
		
		return sb.toString();
	}
}
