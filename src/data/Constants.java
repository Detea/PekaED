package data;

// A good programmer would probably use an enum and not put everything into one thing, but I'm not, so I can do this :D
public class Constants {
	public final static int LAYER_FOREGROUND = 0;
	public final static int LAYER_BACKGROUND = 1;
	public final static int LAYER_BOTH = 2;
	public final static int LAYER_SPRITE = 3;
	
	public final static int MODE_LEGACY = 0;
	public final static int MODE_ENHANCED = 1;
	public final static int MODE_CE = 2;

	public final static int EDIT_MODE_TILES = 0;
	public final static int EDIT_MODE_SPRITES = 1;
	
	public final static int SPRITE_LIMIT = 100;
	
	public final static int LEGACY_LEVEL_LIMIT = 50;
	public static int ENHANCED_LEVEL_LIMIT = 100; // Not really a constant, but I left it in here for convenience
	
	public final static int ACTION_UNDO = 0;
	public final static int ACTION_REDO = 1;
	public final static int DO_TILE = 2;
	public final static int DO_SPRITE = 3;
	
	public final static int FLIP_HORIZONTAL = 0;
	public final static int FLIP_VERTICAL = 1;
}
