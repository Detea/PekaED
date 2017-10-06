package gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import data.Data;
import data.Settings;

public class EpisodePanel extends JPanel {
	
	DefaultListModel dfm;
	JLabel lblEpisodeName;
	
	String currentEpisode;
	
	PekaEDGUI pkg;
	
	public EpisodePanel(PekaEDGUI pkg) {
		this.pkg = pkg;
		
		dfm = new DefaultListModel();
		
		JList list = new JList();
		list.setModel(dfm);
		
		JScrollPane scrollPane = new JScrollPane(list, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		list.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					if (Data.currentEpisodeFile != null && !dfm.isEmpty()) {
						pkg.loadLevel((String) Data.currentEpisodePath + File.separatorChar + dfm.getElementAt(list.getSelectedIndex()));
						pkg.setFrameTitle((String) Data.currentEpisodePath + File.separatorChar + dfm.getElementAt(list.getSelectedIndex()));
					}
				}
			}
		});
		
		scrollPane.setPreferredSize(new Dimension(250, 500));
		
		JLabel lblEpisode = new JLabel("Episode: ");
		lblEpisodeName = new JLabel("");
		
		JButton btnImport = new JButton("Import Level");
		JButton btnRemove = new JButton("Remove");
		
		btnImport.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser();
				fc.setDialogTitle("Import a level into an episode...");
				
				int res = fc.showOpenDialog(null);
				
				if (res == JFileChooser.APPROVE_OPTION) {
					importLevel(fc.getSelectedFile());
				}
			}
			
		});
		
		btnRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				int res = JOptionPane.showConfirmDialog(null, "Delete file from disk?", "Remove level", JOptionPane.YES_NO_OPTION);
				
				if (res == JOptionPane.YES_OPTION) {
					Data.episodeFiles.get(list.getSelectedIndex()).delete();
				}
				
				Data.episodeFiles.remove(list.getSelectedIndex());
				dfm.remove(list.getSelectedIndex());
				
				Data.episodeChanged = true;
			}
			
		});
		
		JPanel topPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		
		topPanel.setLayout(new BoxLayout(topPanel, BoxLayout.X_AXIS));
		topPanel.add(lblEpisode);
		topPanel.add(lblEpisodeName);
		
		bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
		bottomPanel.add(btnImport);
		bottomPanel.add(btnRemove);
		
		add(topPanel, BorderLayout.NORTH);
		add(scrollPane, BorderLayout.CENTER);
		add(bottomPanel, BorderLayout.SOUTH);
	}
	
	public void newEpisode(String name) {
		Data.episodeFiles.clear();
		
		dfm.clear();
		
		currentEpisode = name;
		Data.episodeChanged = false;
		
		lblEpisodeName.setText(currentEpisode);
	}
	
	public void saveEpisode() {
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(Data.currentEpisodeFile));
			
			w.write(currentEpisode + "\n");
			w.write(Data.currentEpisodePath + "\n");
			
			for (File f : Data.episodeFiles) {
				w.write(f.getAbsolutePath() + "\n");
			}
			
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loadEpisode(File file) {
		try {
			BufferedReader r = new BufferedReader(new FileReader(file));
			
			Data.currentEpisodeFile = file;
			
			currentEpisode = r.readLine();
			lblEpisodeName.setText(currentEpisode);
			
			Data.currentEpisodePath = r.readLine();
			
			String[] filepath;
			String read = null;
			
			while ((read = r.readLine()) != null) {
				Data.episodeFiles.add(new File(read)); // Todo: Error handling
				
				filepath = read.split("\\\\"); // Is this platform independent?
				
				dfm.addElement(filepath[filepath.length - 1]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
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
