package helpers;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import data.Data;
import data.Settings;
import pekkakana.PK2Map;
import pekkakana.PK2Sprite;

public final class EpisodeExtractor {
	
	private static ArrayList<ZipEntry> ze = new ArrayList<ZipEntry>();
	private static ArrayList<File> files = new ArrayList<File>();
	private static ArrayList<File> tmpFiles = new ArrayList<File>();
	private static ArrayList<File> tmpSprFiles = new ArrayList<File>();
	
	private static ArrayList<String> trackList = new ArrayList<String>();
	
	private static ArrayList<String> sprites = new ArrayList<String>();
	
	public static final boolean extract(File file) {
		ArrayList<String> tiles = new ArrayList<String>();
		ArrayList<String> scenery = new ArrayList<String>();
		ArrayList<String> music = new ArrayList<String>();
		
		boolean done = false;
		
		try {
			BufferedReader r = new BufferedReader(new FileReader("vanillaFiles/tiles"));
			
			String l;
			
			while ((l = r.readLine()) != null) {
				tiles.add(l.toLowerCase());
			}
			
			r.close();
			
			r = new BufferedReader(new FileReader("vanillaFiles/scenery"));
			
			while ((l = r.readLine()) != null) {
				scenery.add(l.toLowerCase());
			}
			
			r.close();
			
			r = new BufferedReader(new FileReader("vanillaFiles/sprites"));
			
			while ((l = r.readLine()) != null) {
				sprites.add(l.toLowerCase());
			}
			
			r.close();
			
			r = new BufferedReader(new FileReader("vanillaFiles/music"));
			
			while ((l = r.readLine()) != null) {
				music.add(l.toLowerCase());
			}
			
			r.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		File ef = new File(Settings.EPISODES_PATH + Data.currentEpisodeName);
		
		for (File f : ef.listFiles()) {
			if (f.getName().endsWith("map")) {
				ze.add(new ZipEntry("episodes\\" + Data.currentEpisodeName + "\\" + f.getName()));
				
				files.add(f);
			}
		}

		for (File fs : files) {
			PK2Map m = new PK2Map(fs.getAbsolutePath());

			addToFiles(m.getBackground(), Settings.SCENERY_PATH, scenery);
			addToFiles(m.getTileset(), Settings.TILES_PATH, tiles);
			addToFiles(m.getMusic(), Settings.MUSIC_PATH, music);
		
			for (PK2Sprite s : m.spriteList) {
				addSpriteToFile(s.filename.getName());
				
				tmpFiles.addAll(tmpSprFiles);
				tmpSprFiles.clear();
			}
		}
		
		files.addAll(tmpFiles);
		
		if (new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\map.bmp").exists()) {
			ze.add(new ZipEntry("episodes\\" + Data.currentEpisodeName + "\\map.bmp"));
			
			files.add(new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\map.bmp"));
		}
		
		if (new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\readme.txt").exists()) {
			ze.add(new ZipEntry("readme.txt"));
			
			files.add(new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\readme.txt"));
		}
		
		if (new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\pk2stuff.bmp").exists()) {
			ze.add(new ZipEntry("gfx\\pk2stuff.bmp"));
			
			files.add(new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\pk2stuff.bmp"));
		}
		
		ZipOutputStream zop = null;
		
		try {
			zop = new ZipOutputStream(new FileOutputStream(file));
			
			for (int i = 0; i < ze.size(); i++) {	
				FileInputStream fis = new FileInputStream(files.get(i));
				
				byte[] buffer = new byte[1048];
				
				zop.putNextEntry(ze.get(i));
				
				int size;
				while ((size = fis.read(buffer)) > 0) {
					zop.write(buffer, 0, size);
				}
				
				zop.closeEntry();
				
				fis.close();
			}
			
			done = true;
			
			zop.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				zop.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		ze.clear();
		files.clear();
		tmpFiles.clear();
		
		return done;
	}
	
	private static void addToFiles(String filename, String folder, ArrayList<String> list) {
		if (!isVanilla(filename, list) && !trackList.contains(getPath(filename, folder).getAbsolutePath())) {
			File entry = getPath(filename, folder);
			
			ze.add(new ZipEntry("episodes\\" + Data.currentEpisodeName + "\\" + entry.getName()));
			
			if (entry != null) {
				tmpFiles.add(entry);
			}
			
			trackList.add(entry.getAbsolutePath());
		}
	}
	
	private static void addSpriteToFile(String filename) {
		if (!isVanilla(filename, sprites) && !trackList.contains(getSpritePath(filename).getAbsolutePath())) {
			PK2Sprite spr = new PK2Sprite(filename);
			spr.loadFile();
			
			File sprPath = getSpritePath(filename);
			ze.add(new ZipEntry("episodes\\" + Data.currentEpisodeName + "\\" + filename));
			tmpSprFiles.add(sprPath);
			
			trackList.add(sprPath.getAbsolutePath());
			
			for (int i = 0; i < spr.soundFiles.length - 2; i++) { // Only 5 of the 7 sounds are actually used, the last 2 are always empty
				if (!spr.cleanString(spr.soundFiles[i]).isEmpty()) {
					if (!isVanilla(spr.cleanString(spr.soundFiles[i]), sprites)) {
						ze.add(new ZipEntry("episodes\\" + Data.currentEpisodeName + "\\" + spr.cleanString(spr.soundFiles[i])));
						
						tmpSprFiles.add(getSpritePath(spr.cleanString(spr.soundFiles[i])));

						trackList.add(getSpritePath(spr.cleanString(spr.soundFiles[i])).getAbsolutePath());
					}
				}
			}
			
			if (!isVanilla(spr.cleanString(spr.imageFile).toLowerCase(), sprites) && !trackList.contains((sprPath.getParentFile().getAbsolutePath() + "\\" + spr.cleanString(spr.imageFile)))) {
				ze.add(new ZipEntry("episodes\\" + Data.currentEpisodeName + "\\" + spr.cleanString(spr.imageFile)));
				tmpSprFiles.add(new File(sprPath.getParentFile().getAbsolutePath() + "\\" + spr.cleanString(spr.imageFile)));
				
				trackList.add(sprPath.getParentFile().getAbsolutePath() + "\\" + spr.cleanString(spr.imageFile));
			}
			
			addDependendSprites(spr.atkSprite1, spr);
			addDependendSprites(spr.atkSprite2, spr);
			addDependendSprites(spr.transformationSprite, spr);
			addDependendSprites(spr.bonusSprite, spr);
		}
	}
	
	private static void addDependendSprites(char[] sprite, PK2Sprite spr) {
		if (!spr.cleanString(sprite).isEmpty() && !isVanilla(spr.cleanString(sprite), sprites)) {
			addSpriteToFile(spr.cleanString(sprite));
		}
	}
	
	private static File getPath(String file, String folder) {
		File f = null;
		
		if (new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\" + file).exists()) {
			f = new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\" + file);
		} else {
			f = new File(folder + "\\" + file);
		}
		
		return f;
	}
	
	private static File getSpritePath(String file) {
		File f = null;
		
		if (new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\" + file).exists()) {
			f = new File(Settings.EPISODES_PATH + Data.currentEpisodeName + "\\" + file);
		} else {
			f = new File(Settings.SPRITE_PATH + "\\" + file);
		}
		
		return f;
	}
	
	private static boolean isVanilla(String file, ArrayList<String> list) {
		return list.contains(file.toLowerCase());
	}
	
	private static void writeToZip(File file, ZipEntry ze, ZipOutputStream zop) throws IOException {
		FileInputStream fis = new FileInputStream(file);
		
		byte[] buffer = new byte[2048];
		
		zop.putNextEntry(ze);
		
		int size;
		
		while ((size = fis.read(buffer)) > 0) {
			zop.write(buffer, 0, size);
		}
		
		zop.closeEntry();
		
		fis.close();
	}
}
