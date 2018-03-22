package pekkakana;
import java.awt.Rectangle;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import data.Constants;
import data.Data;
import data.Settings;

public class PK2Map {
	public static final int MAP_WIDTH = 256;
	public static final int MAP_HEIGHT = 224;
	public static final int MAP_SIZE = MAP_WIDTH * (MAP_HEIGHT + 32);
	public static final int MAP_MAX_PROTOTYPES = 100;
	
	public char[] version = {0x31, 0x2E, 0x33, 0x00, 0xCD};
	public char[] tilesetImageFile = new char[13];
	public char[] backgroundImageFile = new char[13];
	public char[] musicFile = new char[13];
	public char[] mapName = new char[40];
	public char[] authorName = new char[40];
	
	public int levelNumber;
	public int weather;
	public int time;
	public int scrollType;
	
	public int extra;
	public int background;
	
	public int switch1Time = 2000;
	public int switch2Time = 2000;
	public int switch3Time = 2000;
	public int playerSprite;
	
	int startX;
	public int startY;
	
	public int[] backgroundTiles = new int[MAP_SIZE];
	public int[] foregroundTiles = new int[MAP_SIZE];
	public int[][] layers = new int[2][MAP_SIZE];
	public int[] sprites = new int[MAP_SIZE];
	
	public char[][] prototypes = new char[MAP_MAX_PROTOTYPES][13];
	boolean[] edges = new boolean[MAP_SIZE];
	
	public int x, y;	// The maps coordinates on the overworld map
	public int icon;	// The maps icon on the overworld map
	
	public File file;
	
	public ArrayList<PK2Sprite> spriteList = new ArrayList<PK2Sprite>();
	
	public PK2Map(String file) {
		loadFile(file);
		loadSpriteList();
		
		this.file = new File(file);
	}
	
	public PK2Map() {
		setTileset(Settings.DEFAULT_TILESET);
		setBackground(Settings.DEFAULT_BACKGROUND);
		setMusic(Settings.DEFAULT_MUSIC);
		
		setCharString(mapName, "untitled");
		setCharString(authorName, "unknown");
		
		for (int i = 0; i < MAP_SIZE; i++) {
			layers[0][i] = 255;
			layers[1][i] = 255;
			sprites[i] = 255;
		}
		
		spriteList.clear();
		
		time = 0;
		levelNumber = 1;
		weather = 0;
		scrollType = 0;
		extra = 0;
		background = 0;
		
		x = 0;
		y = 0;
		icon = 0;
	}
	
	/*
	 * Loading a Pekka Kana 2 level file
	 * 
	 * Note:
	 * (int) (dis.readByte() & 0xFF) is done, because Java doesn't support unsigned bytes. This converts the read byte to an unsigned one, stored in an integer.
	 */
	public void loadFile(String file) {
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
		
			for (int i = 0; i < MAP_SIZE; i++) {
				layers[Constants.LAYER_BACKGROUND][i] = 255;
				layers[Constants.LAYER_FOREGROUND][i] = 255;
				sprites[i] = 255;
			}
			
			readAmount(version, dis);
			
			readAmount(tilesetImageFile, dis);
			readAmount(backgroundImageFile, dis);
			readAmount(musicFile, dis);
			readAmount(mapName, dis);
			readAmount(authorName, dis);
			
			char[] amount = new char[8];
			
			readAmount(amount, dis);
			levelNumber = Integer.parseInt(cleanString(amount));
			
			readAmount(amount, dis);
			weather = Integer.parseInt(cleanString(amount));
			
			readAmount(amount, dis);
			switch1Time = Integer.parseInt(cleanString(amount));
			
			readAmount(amount, dis);
			switch2Time = Integer.parseInt(cleanString(amount));
			
			readAmount(amount, dis);
			switch3Time = Integer.parseInt(cleanString(amount));
			
			readAmount(amount, dis);
			time = Integer.parseInt(cleanString(amount));
			
			readAmount(amount, dis);
			extra = Byte.parseByte(cleanString(amount));
			
			readAmount(amount, dis);
			background = Byte.parseByte(cleanString(amount));
			
			readAmount(amount, dis);
			playerSprite = Integer.parseInt(cleanString(amount));
			
			readAmount(amount, dis);
			x = Integer.parseInt(cleanString(amount));
			
			readAmount(amount, dis);
			y = Integer.parseInt(cleanString(amount));
			
			readAmount(amount, dis);
			icon = Integer.parseInt(cleanString(amount));
			
			readAmount(amount, dis);
			int protAmount = Integer.parseInt(cleanString(amount));
		
			for (int i = 0; i < protAmount; i++) {
				char[] protNames = new char[13];
				readAmount(protNames, dis);
				prototypes[i] = protNames;
			}
			
			int width, height;
			
			startX = readCleanConvert(amount, dis);
			startY = readCleanConvert(amount, dis);
			width = readCleanConvert(amount, dis);
			height = readCleanConvert(amount, dis);
			
			for (int y = startY; y <= startY + height; y++) {
				for (int x = startX; x <= startX + width; x++) {
					layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * x + y] = (int) (dis.readByte() & 0xFF);
				}
			}
			
			startX = readCleanConvert(amount, dis);
			startY = readCleanConvert(amount, dis);
			width = readCleanConvert(amount, dis);
			height = readCleanConvert(amount, dis);
		
			for (int y = startY; y <= startY + height; y++) {
				for (int x = startX; x <= startX + width; x++) {
					layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * x + y] = (int) (dis.readByte() & 0xFF);
				}
			}
			
			startX = readCleanConvert(amount, dis);
			startY = readCleanConvert(amount, dis);
			width = readCleanConvert(amount, dis);
			height = readCleanConvert(amount, dis);
			
			for (int y = startY; y <= startY + height; y++) {
				for (int x = startX; x <= startX + width; x++) {
					sprites[MAP_WIDTH * x + y] = (int) (dis.readByte() & 0xFF);
				}
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File '" + file + "' not found.", "Error", JOptionPane.OK_OPTION);
			//e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Something went wrong while trying to read file '" + file + "\nError: " + e.getMessage(), "Error", JOptionPane.OK_OPTION);
			
			//e.printStackTrace();
		}
	}
	
	public void saveFile() {
		try {
			if (!file.getName().endsWith("map")) {
				file = new File(file.getAbsolutePath() + ".map");
			}
			
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
			
			writeArray(version, dos);
			
			writeArray(tilesetImageFile, dos);
			writeArray(backgroundImageFile, dos);
			writeArray(musicFile, dos);
			writeArray(mapName, dos);
			writeArray(authorName, dos);
			
			char[] ca = new char[8];
			setAndWrite(ca, Integer.toString(levelNumber), dos);
			setAndWrite(ca, Integer.toString(weather), dos);
			setAndWrite(ca, Integer.toString(switch1Time), dos);
			setAndWrite(ca, Integer.toString(switch2Time), dos);
			setAndWrite(ca, Integer.toString(switch3Time), dos);
			setAndWrite(ca, Integer.toString(time), dos);
			setAndWrite(ca, Integer.toString(extra), dos);
			setAndWrite(ca, Integer.toString(background), dos);
			setAndWrite(ca, Integer.toString(playerSprite), dos);
			setAndWrite(ca, Integer.toString(x), dos);
			setAndWrite(ca, Integer.toString(y), dos);
			setAndWrite(ca, Integer.toString(icon), dos);
			
			int prototypeAmount = 0;
			for (int i = 0; i < prototypes.length; i++) {
				if (prototypes[i][0] != 0x0) {
					prototypeAmount++;
				}
			}
			
			setAndWrite(ca, Integer.toString(prototypeAmount), dos);
			
			for (int i = 0; i < prototypes.length; i++) {
				if (prototypes[i][0] != 0x0) {
					writeArray(prototypes[i], dos);
				}
			}
			
			Rectangle r = calculateUsedArea(backgroundTiles);
			
			int width = r.width - r.x;
			int height = r.height - r.y;
			
			int start_x = r.x;
			int start_y = r.y;
				
			setAndWrite(ca, Integer.toString(start_x), dos);
			setAndWrite(ca, Integer.toString(start_y), dos);
			setAndWrite(ca, Integer.toString(width), dos);
			setAndWrite(ca, Integer.toString(height), dos);
			
			for (int y = start_y; y <= start_y + height; y++ ) {
				for (int x = start_x; x <= start_x + width; x++) {
					dos.writeByte((byte) layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * x + y]);
				}
			}
			
			r.setRect(0, 0, 0, 0);
			r = calculateUsedArea(foregroundTiles);
			
			width = r.width - r.x;
			height = r.height - r.y;
			
			start_x = r.x;
			start_y = r.y;
			
			setAndWrite(ca, Integer.toString(start_x), dos);
			setAndWrite(ca, Integer.toString(start_y), dos);
			setAndWrite(ca, Integer.toString(width), dos);
			setAndWrite(ca, Integer.toString(height), dos);
			
			for (int y = start_y; y <= start_y + height; y++ ) {
				for (int x = start_x; x <= start_x + width; x++) {
					dos.writeByte((byte) layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * x + y]);
				}
			}
			
			r = calculateUsedArea(sprites);
			
			width = r.width - r.x;
			height = r.height - r.y;
			
			start_x = r.x;
			start_y = r.y;
			
			setAndWrite(ca, Integer.toString(start_x), dos);
			setAndWrite(ca, Integer.toString(start_y), dos);
			setAndWrite(ca, Integer.toString(width), dos);
			setAndWrite(ca, Integer.toString(height), dos);
			
			for (int y = start_y; y <= start_y + height; y++ ) {
				for (int x = start_x; x <= start_x + width; x++) {
					dos.writeByte((byte) sprites[MAP_WIDTH * x + y]);
				}
			}
			
			dos.flush();
			dos.close();
			
			Data.fileChanged = false;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File '" + file.getName() + "' not found.", "Error", JOptionPane.OK_OPTION);
			
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Something went wrong while trying to write file '" + file.getName() + "\nError: " + e.getMessage(), "Error", JOptionPane.OK_OPTION);
			
			e.printStackTrace();
		}
	}
	
	private void loadSpriteList() {
		for (int i = 0; i < prototypes.length; i++) {
			if (prototypes[i][0] != 0x0) {
				spriteList.add(new PK2Sprite(cleanString(prototypes[i])));
			}
		}
	}
	
	public void addSprite(PK2Sprite spr, String filename) {
		if (spriteList.size() < MAP_MAX_PROTOTYPES) {
			spriteList.add(spr);
			
			setCharString(prototypes[spriteList.size() - 1], filename);
		}
	}
	
	private void readAmount(char[] array, DataInputStream dis) throws IOException {
		for (int i = 0; i < array.length; i++) {
			array[i] = (char) dis.readByte();
		}
	}
	
	private int readCleanConvert(char[] array, DataInputStream dis) throws IOException {
		readAmount(array, dis);

		int in = 0;
		
		if (array[0] != 0x0) {
			String str = cleanString(array);
			
			if (!str.isEmpty()) {
				in = Integer.parseInt(str);
			}
		}
		
		return in;
	}
	
	public String cleanString(char[] array) {
		StringBuilder sb = new StringBuilder();
		
		int i = 0;
		while (array[i] != 0x0) {
			if (array[i] > 0xCC)
				break;
			
			sb.append(array[i]);
			
			i++;
		}
		
		return sb.toString();
	}
	
	public void setTile(int x, int y, int tile) {
		if (Data.currentLayer == Constants.LAYER_FOREGROUND) {
			layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = tile;
		} else if (Data.currentLayer == Constants.LAYER_BACKGROUND) {
			layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = tile;
		} else if (Data.currentLayer == Constants.LAYER_BOTH){
			if (tile != 255) {
				layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = Data.selectedTileForeground;
				layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = Data.selectedTileBackground;
			} else {
				layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = 255;
				layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = 255;
			}
		}
	}
	
	public void setTile(int x, int y) {
		if (Data.currentLayer == Constants.LAYER_FOREGROUND) {
			layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = Data.selectedTileForeground;
		} else if (Data.currentLayer == Constants.LAYER_BACKGROUND) {
			layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = Data.selectedTileBackground;
		} else if (Data.currentLayer == Constants.LAYER_BOTH){
			layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = Data.selectedTileForeground;
			layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = Data.selectedTileBackground;
		}
	}

	public int getTileAt(int x, int y, int layer) {
		if ((MAP_WIDTH * (x / 32) + (y / 32)) < MAP_SIZE) {
			if (layer == Constants.LAYER_FOREGROUND) {
				return layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * (x / 32) + (y / 32)];
			} else if (layer == Constants.LAYER_BACKGROUND) {
				return layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * (x / 32) + (y / 32)];
			}
		}
		
		return 255;
	}
	
	/*
	public void readLevelNumberFromFile(File file) {
		RandomAccessFile r = null;
		
		try {
			r = new RandomAccessFile(file, "r");
			
			r.skipBytes(124);
			
			char[] ln = new char[8];
			for (int i = 0; i < 8; i++) {
				ln[i] = (char) (r.readByte());
			}
			
			levelNumber = Integer.parseInt(cleanString(ln));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				r.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}*/
	
	public String getTileset() {
		return cleanString(tilesetImageFile);
	}
	
	public String getBackground() {
		return cleanString(backgroundImageFile);
	}
	
	public String getTitle() {
		return cleanString(mapName);
	}
	
	/*
	 * Maybe convert all these methods into one?
	 */
	
	public void setTileset(String s) {
		for (int i = 0; i < tilesetImageFile.length; i++) {
			tilesetImageFile[i] = 0x0;
		}
		
		for (int i = 0; i < s.length(); i++) {
			tilesetImageFile[i] = s.charAt(i);
		}
	}
	
	public void setBackground(String s) {
		for (int i = 0; i < backgroundImageFile.length; i++) {
			backgroundImageFile[i] = 0x0;
		}
		
		for (int i = 0; i < s.length(); i++) {
			backgroundImageFile[i] = s.charAt(i);
		}
	}
	
	public void setMusic(String s) {
		for (int i = 0; i < musicFile.length; i++) {
			musicFile[i] = 0x0;
		}
		
		for (int i = 0; i < s.length(); i++) {
			musicFile[i] = s.charAt(i);
		}
	}
	
	public void setMapName(String s) {
		for (int i = 0; i < mapName.length; i++) {
			mapName[i] = 0xCC;
		}
		
		for (int i = 0; i < s.length(); i++) {
			mapName[i] = s.charAt(i);
		}
	}
	
	public void setAuthor(String s) {
		for (int i = 0; i < authorName.length; i++) {
			authorName[i] = 0xCC;
		}
		
		for (int i = 0; i < s.length(); i++) {
			authorName[i] = s.charAt(i);
		}
	}
	
	public void setChar2dString(char[][] array, String s, int index) {
		for (int i = 0; i < array[index].length; i++) {
			array[index][i] = 0x0;
		}
		
		for (int i = 0; i < s.length(); i++) {
			array[index][i] = s.charAt(i);
		}
	}
	
	public void setCharString(char[] array, String s) {
		for (int i = 0; i < array.length; i++) {
			array[i] = 0x0;
		}
		
		for (int i = 0; i < s.length(); i++) {
			array[i] = s.charAt(i);
		}
	}
	
	public void removeSprite(int spr) {
		// Iterate through all the sprites in the level and decrement their id by one
		for (int i = 0; i < sprites.length; i++) {
			if (sprites[i] == spr) {
				sprites[i] = 255;
			}
			
			if (sprites[i] > spr && sprites[i] != 255) {
				sprites[i]--;
			}
		}
		
		// Same as the sprites, except with their names
		for (int i = spr + 1; i < prototypes.length; i++) {
			prototypes[i - 1] = prototypes[i];
		}
		
		spriteList.remove(spr);
	}
	
	public String getCreator() {
		return cleanString(authorName);
	}
	
	public String getMusic() {
		return cleanString(musicFile);
	}
	
	/*
	 * This methods looks for the space where tiles are placed.
	 * That way you can store only the placed tiles and don't have to save the whole map.
	 */
	public Rectangle calculateUsedArea(int[] array) {
		Rectangle r = new Rectangle(0, 0, 0, 0);
		
		int x, y;
		int map_left = MAP_WIDTH;
		int map_right = 0;
		int map_upper = MAP_HEIGHT;
		int map_lower = 0;
		
		for (y = 0; y < MAP_HEIGHT; y++) {
			for (x = 0; x < MAP_WIDTH; x++) {
				if (MAP_WIDTH * x + y < array.length) {
					if (array[MAP_WIDTH * x + y] != 255) {
						if (x < map_left)
							map_left = x;
						
						if (y < map_upper)
							map_upper = y;
						
						if (x > map_right)
							map_right = x;
						
						if (y > map_lower)
							map_lower = y;
					}
				}
			}
		}
		
		if (map_right < map_left || map_lower < map_upper) {
			map_left = 0;
			map_upper = 0;
			map_lower = 1;
			map_right = 1;
		}
		
		r.setBounds(map_left, map_upper, map_right, map_lower);
		
		return r;
	}
	
	private void setAndWrite(char[] ca, String s, DataOutputStream dos) throws IOException {
		setCharString(ca, s);
		writeArray(ca, dos);
	}
	
	private void writeArray(char[] array, DataOutputStream dos) throws IOException {
		for (int i = 0; i < array.length; i++) {
			dos.writeByte(array[i]);
		}
	}

	public void setForegroundTile(int x, int y, int tile) {
		if ((MAP_WIDTH * (x / 32) + (y / 32)) < MAP_SIZE) { // check if x & y > 0 && < width/height
			layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = tile;
		}
	}
	
	public void setBackgroundTile(int x, int y, int tile) {
		layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = tile;
	}
}