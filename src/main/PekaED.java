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
				// TODO Read new options
				
				DataInputStream dis = new DataInputStream(new FileInputStream("settings"));
				
				byte[] v = { '1', '.', '4'};
				byte[] version = new byte[3];
				dis.read(version);
				
				for (int i = 0; i < 3; i++) {
					if (version[i] != v[i]) {
						dis.close();
						
						settingsFile.delete();
						
						new SetPathDialog();
						
						return;
					}
				}
				
				Settings.BASE_PATH = dis.readUTF();
				Settings.setPaths();
				
				Settings.loadEpisodeOnStartup = dis.readBoolean();
				Settings.startInEnhancedMode = dis.readBoolean();
				Settings.startInCEMode = dis.readBoolean();
				Constants.ENHANCED_LEVEL_LIMIT = dis.readInt();
				Settings.showStatusbar = dis.readBoolean();
				
				Settings.autoSwitchModes = dis.readBoolean();
				Settings.useDevMode = dis.readBoolean();
				
				Settings.spritePreview = dis.readBoolean();
				Settings.tilesetPreview = dis.readBoolean();
				Settings.bgPreview = dis.readBoolean();
				
				Data.showSpriteRect = dis.readBoolean();
				Data.showTileNr = dis.readBoolean();
				
				Settings.doLimit = dis.readInt();
				
				Settings.parameters = dis.readUTF();
				
				int key, mod, mask;
				String action;
				
				for (int i = 0; i < 21; i++) {
					action = dis.readUTF();
					mod = dis.readInt();
					mask = dis.readInt();
					key = dis.readInt();
					Settings.shortcuts.put(action, new ShortcutKey(mod, mask, key));
					
					Settings.shortcutKeyCodes[i] = key;
				}
				
				Settings.loadLastLevel = dis.readBoolean();
				Data.lastLevel = dis.readUTF();
				
				if (Settings.startInEnhancedMode) {
					Data.mode = Constants.MODE_ENHANCED;
				} else if (Settings.startInCEMode) {
					Data.mode = Constants.MODE_CE;
				} else {
					Data.mode = Constants.MODE_LEGACY;
				}
				
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
