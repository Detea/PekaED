package data;

import java.util.ArrayList;

import pekkakana.PK2Map;

public class DoManager {
	public static void undo(ArrayList<DoAction> al) {
		for (DoAction a : al) {
			//System.out.println("undoing: X: " + (a.x / 32) + " - Y: " + (a.y / 32) +  " - Tile now: " + a.value + " - Tile before: " + a.lastValue);
			//System.out.println("undoing: X: " + (a.x / 32) + " - Y: " + (a.y / 32) + " - Tool: " + a.action + " - Layer: " + a.layer + " - " +  "Tile: " + a.value + " - Last: " + a.lastValue);
			
			switch (a.layer) {
				case Constants.LAYER_BACKGROUND:
					Data.map.layers[Constants.LAYER_BACKGROUND][PK2Map.MAP_WIDTH * (a.x / 32) + (a.y / 32)] = a.value;
					break;
					
				case Constants.LAYER_FOREGROUND:
					Data.map.layers[Constants.LAYER_FOREGROUND][PK2Map.MAP_WIDTH * (a.x / 32) + (a.y / 32)] = a.value;
					break;
					
				case Constants.LAYER_SPRITE:
					Data.map.sprites[PK2Map.MAP_WIDTH * (a.x / 32) + (a.y / 32)] = a.value;
					break;
			}
		}
	}
	
	public static void redo(ArrayList<DoAction> al) {
		for (DoAction a : al) {
			switch (a.layer) {
				case Constants.LAYER_BACKGROUND:
					Data.map.layers[Constants.LAYER_BACKGROUND][PK2Map.MAP_WIDTH * (a.x / 32) + (a.y / 32)] = a.lastValue;
					break;
					
				case Constants.LAYER_FOREGROUND:
					Data.map.layers[Constants.LAYER_FOREGROUND][PK2Map.MAP_WIDTH * (a.x / 32) + (a.y / 32)] = a.lastValue;
					break;
					
				case Constants.LAYER_SPRITE:
					Data.map.sprites[PK2Map.MAP_WIDTH * (a.x / 32) + (a.y / 32)] = a.lastValue;
					break;
			}
		}
	}
}
