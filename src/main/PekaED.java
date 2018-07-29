package main;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.SwingUtilities;

import data.Constants;
import data.Data;
import data.Settings;
import data.ShortcutKey;
import gui.windows.PekaEDGUI;
import gui.windows.SetPathDialog;

public class PekaED {
	public static void main(String[] args) {
		File settingsFile = new File("settings");
		
		if (settingsFile.exists()) {
			try {
				DataInputStream dis = new DataInputStream(new FileInputStream("settings"));
				
				Settings.BASE_PATH = dis.readUTF();
				Settings.setPaths();
				
				Settings.loadEpisodeOnStartup = dis.readBoolean();
				Settings.startInEnhancedMode = dis.readBoolean();
				Constants.ENHANCED_LEVEL_LIMIT = dis.readInt();
				Settings.showStatusbar = dis.readBoolean();
				
				Settings.spritePreview = dis.readBoolean();
				Settings.tilesetPreview = dis.readBoolean();
				Settings.bgPreview = dis.readBoolean();
				
				Data.showSpriteRect = dis.readBoolean();
				Data.showTileNr = dis.readBoolean();
				
				int key, mod;
				String action;
				
				// Load shortcuts, shouldn't hardcode size
				for (int i = 0; i < 18; i++) {
					action = dis.readUTF();
					mod = dis.readInt();
					key = dis.readInt();
					Settings.shortcuts.put(action, new ShortcutKey(mod, key));
					
					Settings.shortcutKeyCodes[i] = key;
				}
				
				Data.mode = Constants.MODE_ENHANCED;
				
				File pathFile = new File(Settings.BASE_PATH);
				
				dis.close();
				
				if (!pathFile.exists()) {
					new SetPathDialog();
				} else {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							new PekaEDGUI().setup();
						}
					});
				}
				
			} catch (FileNotFoundException e1) {
				//JOptionPane.showMessageDialog(null, "Could'nt find settings file.\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
				
				new SetPathDialog();
			} catch (IOException e1) {
				//JOptionPane.showMessageDialog(null, "Could'nt read settings file.\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				e1.printStackTrace();
				
				new SetPathDialog();
			}
		} else {
			new SetPathDialog();
		}
	}
}
