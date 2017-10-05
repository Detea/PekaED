package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JOptionPane;

import data.Settings;
import gui.PekaEDGUI;
import gui.SetPathDialog;

public class PekaED {
	public static void main(String[] args) {
		File settingsFile = new File("settings");
		
		if (settingsFile.exists()) {
			try {
				BufferedReader r = new BufferedReader(new FileReader(settingsFile));
				
				Settings.BASE_PATH = r.readLine();
				Settings.setPaths();
				
				File pathFile = new File(Settings.BASE_PATH);
				
				if (!pathFile.exists()) {
					new SetPathDialog();
				} else {
					new PekaEDGUI().setup();
				}
				
				r.close();
			} catch (FileNotFoundException e1) {
				JOptionPane.showConfirmDialog(null, "Could'nt find settings file.\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				//e1.printStackTrace();
			} catch (IOException e1) {
				JOptionPane.showConfirmDialog(null, "Could'nt read settings file.\n" + e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
				//e1.printStackTrace();
			}
		} else {
			new SetPathDialog();
		}
	}
}
