package pekkakana;

public class PK2SpriteAnimation {
	final int ANIMATION_MAX_SEQUENCES = 10;
	
	byte[] sequence = new byte[ANIMATION_MAX_SEQUENCES];
	int frames; // amount of frames
	boolean loop; // wether the animations loops, or not
	
	public PK2SpriteAnimation(byte[] sq, int frames, boolean loop) {
		this.sequence = sq;
		this.frames = frames;
		this.loop = loop;
	}
}
