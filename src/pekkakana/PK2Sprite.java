package pekkakana;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import data.Settings;

public class PK2Sprite {
	/*
	 * THIS IS NOT A FULL IMPLEMENTATION OF THE PK2 SPRITE FILE FORMAT!
	 * It's only partially implemented, because the editor doesn't need to load the whole file.
	 */
	
	public int type;
	public char[] imageFile = new char[100];
	public char[][] soundFiles = new char[7][100];
	
	private char[] version13 = {'1', '.', '3', '\0'};
	private char[] version12 = {'1', '.', '2', '\0'};
	private char[] version11 = {'1', '.', '1', '\0'};
	
	HashMap<Integer, Color> cl = new HashMap<Integer, Color>();
	
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
	
	public char[] name = new char[33];
	public int width, height;
	
	double weight;
	
	boolean enemy;
	
	int energy;
	int damage;
	
	int damageType;
	int immunity;
	int score;
	
	int[] AI = new int[10];
	
	int color;
	
	public BufferedImage image;
	
	public PK2Sprite(String file) {
		filename = file;

		loadFile(file);
		loadBufferedImage();
	}
	
	public void loadFile(String file) {
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(new File(Settings.SPRITE_PATH + file)));
			
			char[] version = {'1', '.', '3', '\0'};
			
			for (int i = 0; i < version.length; i++) {
				version[i] = (char) dis.readByte();
			}
			
			type = Integer.reverseBytes(dis.readInt());
			
			if (version[2] == '2' || version[2] == '1') {
				imageFile = new char[13];
				soundFiles = new char[7][13];
			}
			
			for (int i = 0; i < imageFile.length; i++) {
				imageFile[i] = (char) (dis.readByte() & 0xFF);
			}
			
			for (int i = 0; i < soundFiles.length; i++) {
				char[] amount = new char[soundFiles[0].length];
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
			//readAmount(name, dis);
			for (int i = 0; i < 32; i++) {
				name[i] = (char) dis.readByte();
			}
			
			char[] ch = new char[0x5C]; // Hacky, but it works...
			readAmount(ch, dis);
			
			color = dis.readByte() & 0xFF;
		} catch (FileNotFoundException e) {
			//System.out.println("Can't find file: " + file);
			//e.printStackTrace();
		} catch (IOException e) {
			//e.printStackTrace();
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
			
			try {
				image = tilesheet.getSubimage(frameX, frameY, frameWidth, frameHeight);
			} catch (Exception ex) {
				System.out.println(filename + " - " + frameX + ", " + frameY + " - " + frameWidth + " - " + frameHeight);
			}
				
			BufferedImage result = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
			 
		    // make color transparent
		    int oldRGB = new Color(148, 209, 222).getRGB();
		    int oldRGB2 = new Color(128, 205, 214).getRGB();
		    int oldRGB3 = new Color(155, 232, 224).getRGB();
		    int oldRGB4 = new Color(114, 200, 228).getRGB();
		 
		    for (int i = 0; i < image.getWidth(); i++) {
		    	for (int j = 0; j < image.getHeight(); j++) {
		    		if (image.getRGB(i, j) != oldRGB && image.getRGB(i, j) != oldRGB2 && image.getRGB(i, j) != oldRGB3 && image.getRGB(i, j) != oldRGB4) {
		    			result.setRGB(i, j, image.getRGB(i, j));
		    		}
		    	}
		    }
		    
		   if (color != 255) {
			  /*
			   * if (this->vari != VARI_NORMAALI){ //Change sprite colors
					PisteDraw2_Image_GetSize(bufferi,w,h);
			
					PisteDraw2_DrawImage_Start(bufferi,*&buffer,leveys);
			
					for (x=0;x<w;x++)
						for (y=0;y<h;y++)
							if ((vari = buffer[x+y*leveys]) != 255){
								vari %= 32;
								vari += this->vari;
								buffer[x+y*leveys] = vari;
							}
			
					PisteDraw2_DrawImage_End(bufferi);
				}
				
				TODO Tint sprite
			   */
		   }
		    
		    image = result;
		} catch (IOException e) {
			//e.printStackTrace();
		}
	}
	
	private String cleanString(char[] array) {
		StringBuilder sb = new StringBuilder();
		
		try {
			int i = 0;
			while (array[i] != 0x0) {
				sb.append(array[i]);
				
				i++;
			}
		} catch (Exception ex) {
			System.out.println(filename);
		}
		
		return sb.toString();
	}
}
