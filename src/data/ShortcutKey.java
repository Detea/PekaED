package data;

public class ShortcutKey {
	public int modifier, mask, key;
	
	public ShortcutKey(int modifier, int mask, int key) {
		this.modifier = modifier;
		this.mask = mask;
		this.key = key;
	}
}
