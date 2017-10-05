package data;

import java.io.File;
import java.util.ArrayList;

import gui.LevelPanel;
import gui.TilePanel;
import pekkakana.PK2Map;

public class Data {
	public static final int TOOL_BRUSH = 0;
	public static final int TOOL_FLOODFILL = 1;
	public static final int TOOL_ERASER = 2;
	
	public static int selectedTool = 0;
	public static int selectedTile = 0, selectedSprite = 255;
	
	public static int selectedTileForeground = 0, selectedTileBackground = 0;
	
	public static int sx, sy, sw, sh; // SelectionX, SelectionY, SelectionWidth, SelectionHeight
	
	public static int currentLayer = 2;
	
	public static boolean showSprites = true;
	
	public static boolean runThread = true;
	public static boolean multiSelectLevel = false;
	public static boolean multiSelectTiles = false;
	
	public static ArrayList<Integer> multiSelection = new ArrayList<Integer>();
	
	public static File currentFile;
	
	public static PK2Map map;
	
	public static TilePanel tp;
	public static LevelPanel lp;
	
	public static boolean fileChanged = false;
}
