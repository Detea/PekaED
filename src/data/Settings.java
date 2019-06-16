package data;

import java.awt.Event;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.LinkedHashMap;

public class Settings {
	public static String BASE_PATH = "";
	public static String EPISODES_PATH = "";
	public static String TILES_PATH = "";
	public static String SCENERY_PATH = "";
	public static String SPRITE_PATH = "";
	public static String MUSIC_PATH = "";
	public static String GFX_PATH = "";
	
	public static String DEFAULT_TILESET = "TILES01.bmp";
	public static String DEFAULT_BACKGROUND = "CASTLE.bmp";
	public static String DEFAULT_MUSIC = "song01.xm";

	public static boolean loadEpisodeOnStartup = false;
	public static boolean startInEnhancedMode = false;
	public static boolean startInCEMode = false;
	
	public static boolean showStatusbar = true;
	
	public static boolean loadLastLevel = false;
	
	public static boolean spritePreview = true;
	public static boolean tilesetPreview = true;
	public static boolean bgPreview = true;
	
	public static boolean useDevMode = true;
	
	public static boolean autoSwitchModes = true;
	
	public static String parameters = "pk2ce.exe dev test %level%";
	
	public static int doLimit = 100;
	
	public static LinkedHashMap<String, ShortcutKey> shortcuts = new LinkedHashMap<String, ShortcutKey>();
	public static int[] shortcutKeyCodes = new int[21];
	
	public static void resetShortcuts() {
		shortcuts.put("createLevel", new ShortcutKey(Event.CTRL_MASK, 0, KeyEvent.VK_N));
		shortcuts.put("openLevel", new ShortcutKey(Event.CTRL_MASK, 0, KeyEvent.VK_O));
		shortcuts.put("saveLevel", new ShortcutKey(Event.CTRL_MASK, 0, KeyEvent.VK_S));
		shortcuts.put("saveLevelAs", new ShortcutKey(Event.CTRL_MASK, Event.SHIFT_MASK, KeyEvent.VK_S));
		shortcuts.put("testLevel", new ShortcutKey(0, 0, KeyEvent.VK_F5));
		shortcuts.put("brushTool", new ShortcutKey(0, 0, KeyEvent.VK_E));
		shortcuts.put("eraserTool", new ShortcutKey(0, 0, KeyEvent.VK_R));
		shortcuts.put("showSprites", new ShortcutKey(0, 0, KeyEvent.VK_S));
		shortcuts.put("highlightSprites", new ShortcutKey(0, 0, KeyEvent.VK_H));
		shortcuts.put("bothLayer", new ShortcutKey(0, 0, KeyEvent.VK_1));
		shortcuts.put("foregroundLayer", new ShortcutKey(0, 0, KeyEvent.VK_2));
		shortcuts.put("backgroundLayer", new ShortcutKey(0, 0, KeyEvent.VK_3));
		shortcuts.put("zoomIn", new ShortcutKey(0, 0, KeyEvent.VK_PLUS));
		shortcuts.put("zoomOut", new ShortcutKey(0, 0, KeyEvent.VK_MINUS));
		shortcuts.put("zoomReset", new ShortcutKey(0, 0, KeyEvent.VK_SPACE));
		shortcuts.put("tileMode", new ShortcutKey(Event.CTRL_MASK, 0, KeyEvent.VK_1));
		shortcuts.put("spriteMode", new ShortcutKey(Event.CTRL_MASK, 0, KeyEvent.VK_2));
		shortcuts.put("addSprite", new ShortcutKey(Event.CTRL_MASK, 0, KeyEvent.VK_A));
		shortcuts.put("undoAction", new ShortcutKey(Event.CTRL_MASK, 0, KeyEvent.VK_Z));
		shortcuts.put("redoAction", new ShortcutKey(Event.CTRL_MASK, 0, KeyEvent.VK_Y));
		shortcuts.put("flipVertically", new ShortcutKey(0, 0, KeyEvent.VK_V));
		
		int i = 0;
		for (String s : shortcuts.keySet()) {
			shortcutKeyCodes[i] = shortcuts.get(s).key;
			
			i++;
		}
	}
	
	// Using File.separatorChar so that this program is platform independent
	public static void setPaths() {
		EPISODES_PATH = BASE_PATH + File.separatorChar + "episodes" + File.separatorChar;
		TILES_PATH = BASE_PATH  + File.separatorChar + "gfx" + File.separatorChar + "tiles" + File.separatorChar;
		SCENERY_PATH = BASE_PATH + File.separatorChar + "gfx" + File.separatorChar + "scenery" + File.separatorChar;
		SPRITE_PATH = BASE_PATH + File.separatorChar + "sprites" + File.separatorChar;
		MUSIC_PATH = BASE_PATH + File.separatorChar + "music" + File.separatorChar;
		GFX_PATH = BASE_PATH + File.separatorChar + "gfx" + File.separatorChar;
	}
}
