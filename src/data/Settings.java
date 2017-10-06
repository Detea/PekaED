package data;

import java.io.File;

public class Settings {
	public static final String version = "1.0.1";
	
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
