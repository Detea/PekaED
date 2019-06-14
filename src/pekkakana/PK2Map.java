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
import java.util.List;

import javax.swing.JOptionPane;

import data.Constants;
import data.Data;
import data.Settings;

public class PK2Map {
	public static final int MAP_WIDTH = 256;
	public static final int MAP_HEIGHT = 224;
	public static final int MAP_SIZE = MAP_WIDTH * (MAP_HEIGHT + 32);
	public static final int MAP_MAX_PROTOTYPES = 100;
	
	public char[] version13 = {0x31, 0x2E, 0x33, 0x00, 0xCD};
	public char[] version14 = {0x31, 0x2E, 0x34, 0x00, 0xCD};
	public char[] version = new char[version13.length];

	public String tilesetImageFile;
	public String backgroundImageFile;
	public String musicFile;
	public String mapName;
	public String authorName;
	
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
	
	public int levelNumberSecret;
	public int unlock_type;
	public int unlock_score;
	public int unlock_collectables;
	
	int startX;
	public int startY;
	
	public int[][] layers = new int[2][MAP_SIZE];
	public int[] sprites = new int[MAP_SIZE];
	
	// The program should really only use prototypesList, but that would mean that I'd have to rewrite a lot of code and might break a ton of things, so... this works lol
	//public char[][] prototypes = new char[MAP_MAX_PROTOTYPES][13];
	public List<String> prototypesList = new ArrayList<String>();

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
		reset();
	}
	
	private void reset() {
		spriteList.clear();
		
		tilesetImageFile = Settings.DEFAULT_TILESET;
		backgroundImageFile = Settings.DEFAULT_BACKGROUND;
		musicFile = Settings.DEFAULT_MUSIC;
		
		mapName = "untitled";
		authorName = "unknown";
		
		for (int i = 0; i < MAP_SIZE; i++) {
			layers[0][i] = 255;
			layers[1][i] = 255;
			sprites[i] = 255;
		}
		
		prototypesList.clear();
		
		time = 0;
		levelNumber = 1;
		weather = 0;
		scrollType = 0;
		extra = 0;
		background = 0;
		
		x = 0;
		y = 0;
		icon = 0;
		
		version = new char[] {0x31, 0x2E, 0x33, 0x00, 0xCD};
	}
	
	private void clear() {
		spriteList.clear();
		
		tilesetImageFile = "";
		backgroundImageFile = "";
		musicFile = "";
		
		mapName = "";
		authorName = "";
		
		for (int i = 0; i < MAP_SIZE; i++) {
			layers[0][i] = 255;
			layers[1][i] = 255;
			sprites[i] = 255;
		}
		
		prototypesList.clear();
		
		time = 0;
		levelNumber = 1;
		weather = 0;
		scrollType = 0;
		extra = 0;
		background = 0;
		
		x = 0;
		y = 0;
		icon = 0;
		
		version = new char[] {0x31, 0x2E, 0x33, 0x00, 0xCD};
	}
	
	public void loadIconData(String file) {
		try {
			RandomAccessFile r = new RandomAccessFile(file, "r");
			
			r.skipBytes(0xC4);
			
			char[] amount = new char[8];
			
			for (int i = 0; i < amount.length; i++) {
				amount[i] = (char) r.readByte();
			}
			
			x = Integer.parseInt(cleanString(amount));
			
			for (int i = 0; i < amount.length; i++) {
				amount[i] = (char) r.readByte();
			}
			
			y = Integer.parseInt(cleanString(amount));
			
			for (int i = 0; i < amount.length; i++) {
				amount[i] = (char) r.readByte();
			}
			
			icon = Integer.parseInt(cleanString(amount));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int checkVersion(File filename) {
		DataInputStream dis = null;
		
		int ret = -1;
		
		try {
			dis = new DataInputStream(new FileInputStream(filename));
			
			readAmount(version, dis);
			
			// very lazy and hacky solution
			if (version[2] == '3') {
				ret = 3;
			} else if (version[2] == '4') {
				ret = 4;
			}
		
			dis.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Couldn't find file '" + filename + "' to check version!", "Couldn't find file", JOptionPane.ERROR_MESSAGE);
			
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Couldn't check file '" + filename + "'!", "Couldn't check file", JOptionPane.ERROR_MESSAGE);
			
			e.printStackTrace();
		} finally {
			try {
				dis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	/*
	 * Loading a Pekka Kana 2 level file
	 * 
	 * Note:
	 * (int) (dis.readByte() & 0xFF) is done, because Java doesn't support unsigned bytes. This converts the read byte to an unsigned one, stored in an integer.
	 */
	public boolean loadFile(String file) {
		boolean ok = true;
		
		this.file = new File(file);
		
		int version = checkVersion(new File(file));
		
		try {
			DataInputStream dis = new DataInputStream(new FileInputStream(file));
			
			Data.currentEpisodeName = this.file.getParent();
			
			if (version == 3) {
				prototypesList.clear();
				
				ok = loadVersion13(dis);				
			} else if (version == 4) {
				prototypesList.clear();
				
				ok = loadVersion14(dis);				
			} else {
				JOptionPane.showMessageDialog(null, "File has to be a Pekka Kana 2 map version 1.3 or 1.4!", "Couldn't load file", JOptionPane.ERROR_MESSAGE);

				ok = false;
			}
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, e.getCause(), "Unable to load map file", JOptionPane.ERROR_MESSAGE);
		}
			
		return ok;
	}
	
	private boolean loadVersion14(DataInputStream dis) {
		boolean ok = false;
		
		clear();
		
		try {
			for (int i = 0; i < MAP_SIZE; i++) {
				layers[Constants.LAYER_BACKGROUND][i] = 255;
				layers[Constants.LAYER_FOREGROUND][i] = 255;
				sprites[i] = 255;
				
				Data.spriteAmount = 0;
			}
			
			readAmount(version, dis);

			tilesetImageFile = readString(dis);
			backgroundImageFile = readString(dis);
			musicFile = readString(dis);
			mapName = readString(dis);
			authorName = readString(dis);

			levelNumber = Integer.reverseBytes(dis.readInt());

			weather = Integer.reverseBytes(dis.readInt());

			switch1Time = Integer.reverseBytes(dis.readInt());
			switch2Time = Integer.reverseBytes(dis.readInt());
			switch3Time = Integer.reverseBytes(dis.readInt());

			time = Integer.reverseBytes(dis.readInt());

			playerSprite = Integer.reverseBytes(dis.readInt());

			x = Integer.reverseBytes(dis.readInt());
			y = Integer.reverseBytes(dis.readInt());

			icon = Integer.reverseBytes(dis.readInt());

			int protAmount = Integer.reverseBytes(dis.readInt());

			for (int i = 0; i < protAmount; i++) {
				prototypesList.add(readString(dis));
			}

			int width, height;

			startX = Integer.reverseBytes(dis.readInt());
			startY = Integer.reverseBytes(dis.readInt());
			width = Integer.reverseBytes(dis.readInt());
			height = Integer.reverseBytes(dis.readInt());

			for (int y = startY; y <= startY + height; y++) {
				for (int x = startX; x <= startX + width; x++) {
					layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * x + y] = (int) (dis.readByte() & 0xFF);
				}
			}

			startX = Integer.reverseBytes(dis.readInt());
			startY = Integer.reverseBytes(dis.readInt());
			width = Integer.reverseBytes(dis.readInt());
			height = Integer.reverseBytes(dis.readInt());

			for (int y = startY; y <= startY + height; y++) {
				for (int x = startX; x <= startX + width; x++) {
					layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * x + y] = (int) (dis.readByte() & 0xFF);
				}
			}

			startX = Integer.reverseBytes(dis.readInt());
			startY = Integer.reverseBytes(dis.readInt());
			width = Integer.reverseBytes(dis.readInt());
			height = Integer.reverseBytes(dis.readInt());

			for (int y = startY; y <= startY + height; y++) {
				for (int x = startX; x <= startX + width; x++) {
					setSpriteAt(x * 32, y * 32, (int) (dis.readByte() & 0xFF));
				}
			}
			
			levelNumberSecret = Integer.reverseBytes(dis.readInt());
			unlock_type = Integer.reverseBytes(dis.readInt());
			unlock_score = Integer.reverseBytes(dis.readInt());
			unlock_collectables = Integer.reverseBytes(dis.readInt());
					
			dis.close();
			
			loadSpriteList();
			
			ArrayList<Integer> indexList = new ArrayList<Integer>();
			ArrayList<Integer> valueList = new ArrayList<Integer>();
			
			boolean confirmed = false, contAsk = true;
			
			int val = -1;
			
			for (int i = 0; i < sprites.length; i++) {
				if (sprites[i] != 255 && sprites[i] >= spriteList.size() && !confirmed && contAsk) {
					int res = JOptionPane.showConfirmDialog(null, "PekaED detected the use of a sprite that isn't actually used.\nDo you want to remove it?", "Faulty sprite detected", JOptionPane.YES_NO_OPTION);

					if (res == JOptionPane.YES_OPTION) {
						confirmed = true;
						
						val = sprites[i];
					} else {
						contAsk = false;
					}
				}
				
				if (confirmed && sprites[i] == val) {
					indexList.add(i);
					valueList.add(val);
				}
			}
			
			for (int i = 0; i < indexList.size(); i++) {
				sprites[indexList.get(i)] = 255;
			}
			
			ok = true;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File '" + file + "' not found.\n" + e.getMessage(), "Error", JOptionPane.OK_OPTION);

			ok = false;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Something went wrong while trying to read file '" + file + "\n" + e.getMessage(), "Error", JOptionPane.OK_OPTION);

			ok = false;
		}
		
		return ok;
	}
	
	private boolean loadVersion13(DataInputStream dis) {
		boolean ok = false;
		
		clear();
		
		try {
			for (int i = 0; i < MAP_SIZE; i++) {
				layers[Constants.LAYER_BACKGROUND][i] = 255;
				layers[Constants.LAYER_FOREGROUND][i] = 255;
				sprites[i] = 255;
				
				Data.spriteAmount = 0;
			}
			
			readAmount(version, dis);

			char[] tmp = new char[13];
			
			readAmount(tmp, dis);
			tilesetImageFile = cleanString(tmp);

			readAmount(tmp, dis);
			backgroundImageFile = cleanString(tmp);

			readAmount(tmp, dis);
			musicFile = cleanString(tmp);

			tmp = new char[40];
			
			readAmount(tmp, dis);
			mapName = cleanString(tmp);

			readAmount(tmp, dis);
			authorName = cleanString(tmp);
	
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
				//prototypes[i] = protNames;
				
				prototypesList.add(cleanString(protNames));
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
					setSpriteAt(x * 32, y * 32, (int) (dis.readByte() & 0xFF));
				}
			}
			
			dis.close();
			
			loadSpriteList();
			
			ArrayList<Integer> indexList = new ArrayList<Integer>();
			ArrayList<Integer> valueList = new ArrayList<Integer>();
			
			boolean confirmed = false, contAsk = true;
			
			int val = -1;
			
			for (int i = 0; i < sprites.length; i++) {
				if (sprites[i] != 255 && sprites[i] >= spriteList.size() && !confirmed && contAsk) {
					int res = JOptionPane.showConfirmDialog(null, "PekaED detected the use of a sprite that isn't actually used.\nDo you want to remove it?", "Faulty sprite detected", JOptionPane.YES_NO_OPTION);

					if (res == JOptionPane.YES_OPTION) {
						confirmed = true;
						
						val = sprites[i];
					} else {
						contAsk = false;
					}
				}
				
				if (confirmed && sprites[i] == val) {
					indexList.add(i);
					valueList.add(val);
				}
			}
			
			for (int i = 0; i < indexList.size(); i++) {
				sprites[indexList.get(i)] = 255;
			}
			
			ok = true;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "File '" + file + "' not found.\n" + e.getMessage(), "Error", JOptionPane.OK_OPTION);

			ok = false;
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Something went wrong while trying to read file '" + file + "\n" + e.getMessage(), "Error", JOptionPane.OK_OPTION);

			ok = false;
		}
		
		return ok;
	}
	
	public void saveFile() {
		try {
			DataOutputStream dos = new DataOutputStream(new FileOutputStream(file));
			
			if (Data.mode == Constants.MODE_CE) {
				if (!file.getName().endsWith("cemap")) {
					file = new File(file.getAbsolutePath() + ".cemap");
				}
				
				saveFile14(dos);
			} else {
				if (!file.getName().endsWith("map")) {
					file = new File(file.getAbsolutePath() + ".map");
				}
				
				saveFile13(dos);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	private void saveFile14(DataOutputStream dos) {
		try {
			writeArray(version14, dos);
			
			writeString(tilesetImageFile, dos);
			writeString(backgroundImageFile, dos);
			writeString(musicFile, dos);
			writeString(mapName, dos);
			writeString(authorName, dos);
			
			dos.writeInt(Integer.reverseBytes(levelNumber));
			dos.writeInt(Integer.reverseBytes(weather));
			dos.writeInt(Integer.reverseBytes(switch1Time));
			dos.writeInt(Integer.reverseBytes(switch2Time));
			dos.writeInt(Integer.reverseBytes(switch3Time));
			dos.writeInt(Integer.reverseBytes(time));
			dos.writeInt(Integer.reverseBytes(playerSprite));
			dos.writeInt(Integer.reverseBytes(x));
			dos.writeInt(Integer.reverseBytes(y));
			dos.writeInt(Integer.reverseBytes(icon));
			
			dos.writeInt(Integer.reverseBytes(prototypesList.size()));
			
			for (String s : prototypesList) {
				writeString(s, dos);
			}
			
			Rectangle r = calculateUsedArea(layers[Constants.LAYER_BACKGROUND], "Background");
			
			int width = r.width - r.x;
			int height = r.height - r.y;
			
			int start_x = r.x;
			int start_y = r.y;
			
			dos.writeInt(Integer.reverseBytes(start_x));
			dos.writeInt(Integer.reverseBytes(start_y));
			dos.writeInt(Integer.reverseBytes(width));
			dos.writeInt(Integer.reverseBytes(height));
	
			for (int y = start_y; y <= start_y + height; y++ ) {
				for (int x = start_x; x <= start_x + width; x++) {
					dos.writeByte((byte) layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * x + y]);
				}
			}
			
			r = calculateUsedArea(layers[Constants.LAYER_FOREGROUND], "Foreground");
			
			width = r.width - r.x;
			height = r.height - r.y;
			
			start_x = r.x;
			start_y = r.y;
			
			dos.writeInt(Integer.reverseBytes(start_x));
			dos.writeInt(Integer.reverseBytes(start_y));
			dos.writeInt(Integer.reverseBytes(width));
			dos.writeInt(Integer.reverseBytes(height));
			
			for (int y = start_y; y <= start_y + height; y++ ) {
				for (int x = start_x; x <= start_x + width; x++) {
					dos.writeByte((byte) layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * x + y]);
				}
			}
			
			r = calculateUsedArea(sprites, "Sprites");
			
			width = r.width - r.x;
			height = r.height - r.y;
			
			start_x = r.x;
			start_y = r.y;
			
			dos.writeInt(Integer.reverseBytes(start_x));
			dos.writeInt(Integer.reverseBytes(start_y));
			dos.writeInt(Integer.reverseBytes(width));
			dos.writeInt(Integer.reverseBytes(height));
			
			for (int y = start_y; y <= start_y + height; y++ ) {
				for (int x = start_x; x <= start_x + width; x++) {
					dos.writeByte((byte) sprites[MAP_WIDTH * x + y]);
				}
			}
			
			dos.writeInt(Integer.reverseBytes(levelNumberSecret));
			dos.writeInt(Integer.reverseBytes(unlock_type));
			dos.writeInt(Integer.reverseBytes(unlock_score));
			dos.writeInt(Integer.reverseBytes(unlock_collectables));
			
			int cs = 0;
			for (int i = 0; i < spriteList.size(); i++) {
				// TYPE_COLLECTABLE
				if (spriteList.get(i).type == 5) {
					cs = i;
					
					break;
				}
			}
			
			int collectables = 0;
			
			for (int i = 0; i < sprites.length; i++) {
				if (sprites[i] == cs) {
					collectables++;
				}
			}
			
			dos.writeInt(collectables);
			
			dos.flush();
			dos.close();
			
			Data.fileChanged = false;
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Can't access file '" + file.getName() + "'!\n" + e.getMessage(), "Error", JOptionPane.OK_OPTION);
			
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Something went wrong while trying to write file '" + file.getName() + "\n" + e.getMessage(), "Error", JOptionPane.OK_OPTION);
			
			e.printStackTrace();
		}
	}
	
	private void saveFile13(DataOutputStream dos) {
		try {
			writeArray(version13, dos);
			
			writeArray(tilesetToPK2String(), dos);
			writeArray(backgroundToPK2String(), dos);
			writeArray(musicToPK2String(), dos);
			writeArray(mapNameToPK2String(), dos);
			writeArray(authorToPK2String(), dos);
			
			char[] ca = new char[8];
			setAndWrite(ca, String.copyValueOf(getLevelNumberAsChar(levelNumber)), dos);
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
			
			setAndWrite(ca, Integer.toString(prototypesList.size()), dos);
			
			for (int i = 0; i < prototypesList.size(); i++) {
				writeArray(prototypeToPK2String(prototypesList.get(i)), dos);
			}
			
			Rectangle r = calculateUsedArea(layers[Constants.LAYER_BACKGROUND], "Background");
			
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
			
			r = calculateUsedArea(layers[Constants.LAYER_FOREGROUND], "Foreground");
			
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
			
			r = calculateUsedArea(sprites, "Sprites");
			
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
			JOptionPane.showMessageDialog(null, "Can't access file '" + file.getName() + "'!\n" + e.getMessage(), "Error", JOptionPane.OK_OPTION);
			
			e.printStackTrace();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "Something went wrong while trying to write file '" + file.getName() + "\n" + e.getMessage(), "Error", JOptionPane.OK_OPTION);
			
			e.printStackTrace();
		}
	}
		
	private void loadSpriteList() {
		for (int i = 0; i < prototypesList.size(); i++) {
			File fi = null;
			
			if (Data.mode != Constants.MODE_CE && new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\" + prototypesList.get(i)).exists()) {
				fi = new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\" + prototypesList.get(i));
			} else if (Data.mode == Constants.MODE_CE && new File(file.getParent() + "\\sprites\\" + prototypesList.get(i)).exists()) {
				fi = new File(file.getParent() + "\\sprites\\" + prototypesList.get(i));
			} else {
				fi = new File(Settings.SPRITE_PATH + "\\" + prototypesList.get(i));
			}
			
			if (fi.exists()) {
				spriteList.add(new PK2Sprite(prototypesList.get(i)));
			} else {
				removeSprite(i);
			}
		}
	}
	
	public void addSprite(PK2Sprite spr, String filename) {
		if (spriteList.size() < MAP_MAX_PROTOTYPES || Data.mode == Constants.MODE_CE) {
			spriteList.add(spr);
			
			prototypesList.add(filename);
		}
	}
	
	private void writeString(String s, DataOutputStream dos) throws IOException {
		for (int i = 0; i < s.length(); i++) {
			dos.writeByte(s.charAt(i));
		}
		
		dos.writeByte(0);
	}
	
	private String readString(DataInputStream dos) throws IOException {
		StringBuilder sb = new StringBuilder();
		
		char c;
		while ((c = (char) dos.readByte()) != 0) {
			sb.append(c);
		}
		
		return sb.toString();
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
		
		for (int i = 0; i < array.length; i++) {
			if (array[i] >= 0xCC || array[i] == 0x0)
				break;
			
			sb.append(array[i]);
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
		if ((MAP_WIDTH * (x / 32) + (y / 32)) < MAP_SIZE && (MAP_WIDTH * (x / 32) + (y / 32)) >= 0) {
			if (Data.currentLayer == Constants.LAYER_FOREGROUND) {
				layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = Data.selectedTileForeground;
			} else if (Data.currentLayer == Constants.LAYER_BACKGROUND) {
				layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = Data.selectedTileBackground;
			} else if (Data.currentLayer == Constants.LAYER_BOTH){
				layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = Data.selectedTileForeground;
				layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = Data.selectedTileBackground;
			}
		}
	}

	public int getTileAt(int x, int y, int layer) {
		if ((MAP_WIDTH * (x / 32) + (y / 32)) >= 0 && (MAP_WIDTH * (x / 32) + (y / 32)) < MAP_SIZE) {
			if (layer == Constants.LAYER_FOREGROUND) {
				return layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * (x / 32) + (y / 32)];
			} else if (layer == Constants.LAYER_BACKGROUND) {
				return layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * (x / 32) + (y / 32)];
			}
		}
		
		return 255;
	}
	
	public int getSpriteAt(int x, int y) {
		int spr = 255;
		
		if (PK2Map.MAP_WIDTH * (x / 32) + (y / 32) >= 0 && PK2Map.MAP_WIDTH * (x / 32) + (y / 32) < PK2Map.MAP_SIZE) {
			spr = sprites[PK2Map.MAP_WIDTH * (x / 32) + (y / 32)];
		}
		
		return spr;
	}
	
	public void setSpriteAt(int x, int y, int sprite) {
		if (Data.showSpriteWarning && Data.spriteAmount >= 800 && Data.mode != Constants.MODE_CE) {
			int res = JOptionPane.showConfirmDialog(null, "The sprite limit of 800 sprites was reached!\nIf you continue placing sprites, you're doing this at your own risk.\nDo you want to proceed?", "Sprite limit reached!", JOptionPane.ERROR_MESSAGE);
			
			if (res == JOptionPane.YES_OPTION) {
				Data.showSpriteWarning = false;
			}
		} else {
			if (PK2Map.MAP_WIDTH * (x / 32) + (y / 32) >= 0 && PK2Map.MAP_WIDTH * (x / 32) + (y / 32) < PK2Map.MAP_SIZE) {
				if (sprites[PK2Map.MAP_WIDTH * (x / 32) + (y / 32)] != 255 && sprite == 255) {
					Data.spriteAmount--;
				} else if (sprite != 255) {
					if (sprites[PK2Map.MAP_WIDTH * (x / 32) + (y / 32)] == 255) {
						Data.spriteAmount++;
					}
				}
				
				sprites[PK2Map.MAP_WIDTH * (x / 32) + (y / 32)] = sprite;
			}
		}
	}
	
	public String getSprite(int i) {
		return prototypesList.get(i);
	}
	
	/*
	public String getTileset() {
		return cleanString(tilesetImageFile);
	}
	
	public String getBackground() {
		return cleanString(backgroundImageFile);
	}
	
	public String getTitle() {
		return cleanString(mapName);
	}*/
	
	/*
	 * These are kind of ugly, but they ensure that the produced files are VERY similar to the original editors files
	 */
	
	private char[] toPK2String(String s, int size) {
		char[] tmp = new char[size];

		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = 0xCD; // This ensures that the files created with PekaED are somewhat identical with the original editor
		}
		
		int len = 0;
		
		if (s.length() > tmp.length - 1) {
			len = tmp.length - 1;
		} else {
			len = s.length();
		}
		
		for (int i = 0; i < len; i++) {
			tmp[i] = s.charAt(i);
		}
		
		tmp[len] = 0x0;
		
		return tmp;
	}
	
	private char[] prototypeToPK2String(String spr) {
		char[] tmp = new char[13];
		
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = 0xCD; // This ensures that the files created with PekaED are somewhat identical with the original editor
		}
		
		int len = 0;
		
		if (spr.length() > tmp.length - 1) {
			len = tmp.length - 1;
		} else {
			len = spr.length();
		}
		
		for (int i = 0; i < len; i++) {
			tmp[i] = spr.charAt(i);
		}
		
		tmp[len] = 0x0;
		
		return tmp;
	}
	
	public char[] tilesetToPK2String() {
		char[] tmp = new char[13];
		
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = 0x0; // This ensures that the files created with PekaED are somewhat identical with the original editor
		}
		
		int len = 0;
		
		if (this.tilesetImageFile.length() > tmp.length - 1) {
			len = tmp.length - 1;
		} else {
			len = this.tilesetImageFile.length();
		}
		
		for (int i = 0; i < len; i++) {
			tmp[i] = tilesetImageFile.charAt(i);
		}
		
		tmp[len] = 0x0;
		
		return tmp;
	}
	
	public char[] backgroundToPK2String() {
		char[] tmp = new char[13];
		
		for (int i = 0; i < tmp.length; i++) {
			tmp[i] = 0x0;
		}
		
		int len = 0;
		
		if (backgroundImageFile.length() > tmp.length - 1) {
			len = tmp.length - 1;
		} else {
			len = backgroundImageFile.length();
		}
		
		for (int i = 0; i < len; i++) {
			tmp[i] = backgroundImageFile.charAt(i);
		}
		
		return tmp;
	}
	
	public char[] musicToPK2String() {
		char[] musicFile = new char[13];
		
		for (int i = 0; i < musicFile.length; i++) {
			musicFile[i] = 0x0;
		}
		
		int len = 0;
		
		if (this.musicFile.length() > musicFile.length - 1) {
			len = musicFile.length - 1;
		} else {
			len = this.musicFile.length();
		}
		
		for (int i = 0; i < len; i++) {
			musicFile[i] = this.musicFile.charAt(i);
		}
		
		return musicFile;
	}
	
	// TODO Restore original weird Level Editor file format
	public char[] mapNameToPK2String() {
		char[] mapName = new char[40];
		
		for (int i = 0; i < mapName.length; i++) {
			mapName[i] = 0xCD;
		}
		
		int len = 0;
		
		if (this.mapName.length() > mapName.length - 1) {
			len = mapName.length - 1;
		} else {
			len = this.mapName.length();
		}
		
		for (int i = 0; i < len; i++) {
			mapName[i] = this.mapName.charAt(i);
		}
		
		mapName[mapName.length - 1] = 0x0;
		
		return mapName;
	}
	
	public char[] authorToPK2String() {
		char[] authorName = new char[40];
		
		for (int i = 0; i < authorName.length; i++) {
			authorName[i] = 0xCC;
		}
		
		int len = 0;
		
		if (this.authorName.length() >= 28) {
			len = 28;
		} else {
			len = this.authorName.length();
		}
		
		for (int i = 0; i < len; i++) {
			authorName[i] = this.authorName.charAt(i);
		}
		
		authorName[29] = 0x0;
		
		for (int i = 30; i < authorName.length; i++) {
			authorName[i] = 0xCD;
		}
		
		return authorName;
	}
	
	public char[] getLevelNumberAsChar(int number) {
		char[] c = new char[8];
		
		for (int i = 0; i < c.length; i++) {
			c[i] = 0xCC;
		}
		
		String s = Integer.toString(number);
		
		int len = 0;
		if (s.length() > 7) {
			len = 7;
		} else {
			len = s.length();
		}
		
		for (int i = 0; i < len; i++) {
			c[i] = s.charAt(i);
		}
		
		c[len] = 0x0;
		
		return c;
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
		/*for (int i = spr + 1; i < prototypes.length; i++) {
			prototypes[i - 1] = prototypes[i];
		}*/
		
		prototypesList.remove(spr);
		
		if (spr < spriteList.size()) {
			spriteList.remove(spr);
		}
	}
	
	/*
	public String getCreator() {
		return cleanString(authorName);
	}
	
	public String getMusic() {
		return cleanString(musicFile);
	}*/
	
	/*
	 * This methods looks for the space where tiles are placed.
	 * That way you can store only the placed tiles and don't have to save the whole map.
	 */
	public Rectangle calculateUsedArea(int[] array, String layer) {
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
						if (x < map_left) {
							map_left = x;
						}
							
						if (y < map_upper) {
							map_upper = y;
						}
						
						if (x > map_right) {
							map_right = x;
						}
						
						if (y > map_lower) {
							map_lower = y;
						}
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
		
		r.x = map_left;
		r.y = map_upper;
		r.width = map_right;
		r.height = map_lower;
		
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
		if ((MAP_WIDTH * (x / 32) + (y / 32)) >= 0 && (MAP_WIDTH * (x / 32) + (y / 32)) < MAP_SIZE) { // check if x & y > 0 && < width/height
			if (tile != -256 || tile >= 0) {
				layers[Constants.LAYER_FOREGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = tile;
			}
		}
	}
	
	public void setBackgroundTile(int x, int y, int tile) {
		if ((MAP_WIDTH * (x / 32) + (y / 32)) >= 0 && (MAP_WIDTH * (x / 32) + (y / 32)) < MAP_SIZE) { // check if x & y > 0 && < width/height
			if (tile != -256 || tile >= 0) {
				layers[Constants.LAYER_BACKGROUND][MAP_WIDTH * (x / 32) + (y / 32)] = tile;
			}
		}
	}
}