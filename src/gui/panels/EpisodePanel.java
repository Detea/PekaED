package gui.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileFilter;

import data.Data;
import data.Settings;
import gui.windows.PekaEDGUI;
import pekkakana.PK2Map;

public class EpisodePanel extends JPanel {
	
	private DefaultListModel dfm;
	private JLabel lblEpisodeName;
	
	private JList list;
	
	private PekaEDGUI pkg;
	
	public EpisodePanel(PekaEDGUI pkg) {
		this.pkg = pkg;
		
		dfm = new DefaultListModel();
		
		list = new JList();
		list.setModel(dfm);
		
		JScrollPane scrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (Data.currentEpisodeFile != null && !dfm.isEmpty()) {
						if (Data.currentFile != null & Data.fileChanged) {
							Data.map.saveFile();
						}
						
						pkg.loadLevel((String) Data.currentEpisodePath + File.separatorChar + dfm.getElementAt(list.getSelectedIndex()));
						pkg.setFrameTitle();
					}
				}
			}
		});
		
		JLabel lblEpisode = new JLabel("Episode: ");
		lblEpisodeName = new JLabel("");
		
		JButton btnImport = new JButton("Import Level");
		JButton btnRemove = new JButton("Remove");
		JButton btnUp = new JButton("Up");
		JButton btnDown = new JButton("Down");
		
		btnImport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.currentEpisodeFile != null) {
					JFileChooser fc = new JFileChooser(Settings.EPISODES_PATH);
					fc.setDialogTitle("Import a level into an episode...");
					fc.setMultiSelectionEnabled(true);
					
					fc.setFileFilter(new FileFilter() {

						@Override
						public boolean accept(File f) {
							return f.isDirectory() || f.getName().endsWith("map");
						}

						@Override
						public String getDescription() {
							return "Pekka Kana 2 level file";
						}
						
					});
					
					int res = fc.showOpenDialog(null);
					
					if (res == JFileChooser.APPROVE_OPTION) {
						for (File f : fc.getSelectedFiles()) {
							importLevel(f);
						}
					}
				}
			}
			
		});
		
		btnRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int res = JOptionPane.showConfirmDialog(null, "Delete file from disk?", "Remove level", JOptionPane.YES_NO_OPTION);
				
				int index = list.getSelectedIndex();
				
				if (res == JOptionPane.YES_OPTION) {
					removeLevel(index);
				}
			}
			
		});
		
		JPanel topPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.add(lblEpisode);
		topPanel.add(lblEpisodeName);
		
		btnImport.setBounds(10, 20, 80, 25);
		btnRemove.setBounds(10, 550, 80, 25);
		
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.add(btnImport);
		bottomPanel.add(btnRemove);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(topPanel);
		add(scrollPane);
		add(bottomPanel);
	}
	
	public void newEpisode(String name) {
		Data.episodeFiles.clear();
		
		dfm.clear();
		
		Data.currentEpisodeName = name;
		Data.currentEpisodeFile = new File(name + ".episode");
		Data.episodeChanged = false;
		
		lblEpisodeName.setText(Data.currentEpisodeName);
	}
	
	private void removeLevel(int index) {
		if (index != -1) {
			Data.episodeFiles.get(index).delete();
			
			Data.episodeFiles.remove(index);
			dfm.remove(index);
			
			for (int i = index; i < Data.episodeFiles.size(); i++) {
				PK2Map map = new PK2Map(Data.episodeFiles.get(i).getAbsolutePath());
				map.levelNumber--;
				map.saveFile();
			}
			
			if (!Data.episodeFiles.isEmpty() && list.getSelectedIndex() > 0) {
				if (Data.episodeFiles.size() - 1 >= 0) {
					Data.currentFile = Data.episodeFiles.get(Data.episodeFiles.size() - 1);
					pkg.loadLevel(Data.currentFile.getAbsolutePath());
					list.setSelectedIndex(Data.episodeFiles.size() - 1);
					
					if (Data.episodeFiles.size() == 0) {
						pkg.newLevel();
					}
				}
			}
			
			Data.episodeChanged = true;
		}
	}
	
	public void setSelectedLevel(int index) {
		if (index >= 0) {
			list.setSelectedIndex(index);
		}
	}
	
	public void saveEpisode() {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(Data.currentEpisodePath + File.separatorChar + Data.currentEpisodeFile.getName()));
			
			w.write(Data.currentEpisodeName + "\n");
			w.write(Data.currentEpisodePath + "\n");
			
			for (File f : Data.episodeFiles) {
				w.write(f.getAbsolutePath() + "\n");
			}
			
			w.flush();
			w.close();
			
			Data.episodeChanged = false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadEpisode(File file) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(file));
			
			Data.currentEpisodeFile = file;
			
			Data.currentEpisodeName = r.readLine();
			lblEpisodeName.setText(Data.currentEpisodeName);
			
			Data.currentEpisodePath = r.readLine();
			
			String[] filepath;
			String read = null;
			
			while ((read = r.readLine()) != null) {
				Data.episodeFiles.add(new File(read)); // Todo: Error handling
				
				filepath = read.split("\\\\"); // Is this platform independent?
				
				dfm.addElement(filepath[filepath.length - 1]);
			}
		
			// Sorting the list
			/*
			PK2Map lastMap = null;
			for (int j = 0; j < 4; j++) {
				for (int i = 1; i < files.size(); i++) {
					lastMap = files.get(i - 1);
					
					if (lastMap.levelNumber > files.get(i).levelNumber) {
						files.set(i - 1, files.get(i));
						files.set(i, lastMap);
					}
				}
			}*/
			
			r.close();
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(null, "Could'nt find episode file '" + file.getName() + "'.", "Error", JOptionPane.ERROR_MESSAGE);
			
			FileWriter w;
			try {
				w = new FileWriter("lastepisode");
				w.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			Data.currentEpisodeFile = null;
			
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void importLevel(File file) {
		if (Data.episodeFiles.size() < Data.EPISODE_LEVEL_LIMIT) {
			if (file.getParentFile().getPath() != Data.currentEpisodePath) {
				try {
					Files.copy(file.toPath(), (new File(Data.currentEpisodePath + File.separatorChar + file.getName()).toPath()), StandardCopyOption.REPLACE_EXISTING);
					
					file = new File(Data.currentEpisodePath + File.separatorChar + file.getName());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			Data.episodeFiles.add(file);
			
			dfm.addElement(file.getName());
			
			Data.episodeChanged = true;
		}
	}
}
