package main;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JOptionPane;

import data.Constants;
import data.Settings;
import gui.PekaEDGUI;
import gui.SetPathDialog;

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
				
				File pathFile = new File(Settings.BASE_PATH);
				
				dis.close();
				
				if (!pathFile.exists()) {
					new SetPathDialog();
				} else {
					new PekaEDGUI().setup();
				}
			} catch (FileNotFoundException e1) {
				//JOptionPane.showMessageDialog(null, "Could'nt find settings file.\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				//e1.printStackTrace();
				
				new SetPathDialog();
			} catch (IOException e1) {
				//JOptionPane.showMessageDialog(null, "Could'nt read settings file.\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				//e1.printStackTrace();
				
				new SetPathDialog();
			}
		} else {
			new SetPathDialog();
		}
	}
}
