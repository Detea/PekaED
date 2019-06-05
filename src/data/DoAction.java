package data;

public class DoAction {
	public int x, y, value, lastValue, action, layer;
	
	public DoAction(int x, int y, int value, int lastValue, int action, int layer) {
		this.x = (x / 32) * 32;
		this.y = (y / 32) * 32;
		this.value = value;
		this.lastValue = lastValue;
		this.action = action;
		this.layer = layer;
	}
}
