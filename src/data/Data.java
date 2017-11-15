package data;

import java.io.File;
import java.util.ArrayList;

import javax.swing.JScrollPane;

import gui.panels.LevelPanel;
import gui.panels.MiniMapPanel;
import gui.panels.TilePanel;
import pekkakana.PK2Map;

public class Data {
	public static final int TOOL_BRUSH = 0;
	public static final int TOOL_FLOODFILL = 1;
	public static final int TOOL_ERASER = 2;
	
	public static int selectedTool = 0;
	public static int selectedTile = 0, selectedSprite = 255;
	
	public static double scale = 1;
	
	public static int selectedTileForeground = 0, selectedTileBackground = 0;
	
	public static int sx, sy, sw, sh; // SelectionX, SelectionY, SelectionWidth, SelectionHeight
	
	// The amount of levels an episode can have. This depends on the current mode.
	public static int EPISODE_LEVEL_LIMIT = 50;
	
	public static int currentLayer = 2;
	
	public static boolean showSprites = true;
	
	public static boolean runThread = true;
	public static boolean multiSelectLevel = false;
	public static boolean multiSelectTiles = false;
	public static boolean dragging = false;
	
	public static ArrayList<Integer> multiSelectionForeground = new ArrayList<Integer>();
	public static ArrayList<Integer> multiSelectionBackground = new ArrayList<Integer>();
	
	public static ArrayList<File> episodeFiles = new ArrayList<File>();
	
	public static File currentFile, currentEpisodeFile;
	
	public static String currentEpisodePath;
	
	public static int mode = Constants.MODE_LEGACY;
	
	public static PK2Map map;
	
	public static TilePanel tp;
	public static LevelPanel lp;
	public static MiniMapPanel mmp;
	
	public static boolean fileChanged = false, episodeChanged = false;
}
