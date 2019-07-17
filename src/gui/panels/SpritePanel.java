package gui.panels;


import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.CellEditorListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellEditor;

import data.Constants;
import data.Data;
import data.Settings;
import gui.windows.PekaEDGUI;
import pekkakana.PK2Sprite;

public class SpritePanel extends JPanel {

	DefaultTableModel dfm;
	JTable table;
	PekaEDGUI pkg;
	
	public SpritePanel(PekaEDGUI pkg) {
		this.pkg = pkg;
		
		dfm = new DefaultTableModel();
		
		table = new JTable(dfm);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowGrid(false);
		table.setDefaultEditor(Object.class, new TableCellEditor() {

			@Override
			public void addCellEditorListener(CellEditorListener arg0) {
				
			}

			@Override
			public void cancelCellEditing() {
				// TODO Auto-generated method stub
				
			}

			@Override
			public Object getCellEditorValue() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean isCellEditable(EventObject arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void removeCellEditorListener(CellEditorListener arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean shouldSelectCell(EventObject arg0) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public boolean stopCellEditing() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public Component getTableCellEditorComponent(JTable arg0, Object arg1, boolean arg2, int arg3, int arg4) {
				// TODO Auto-generated method stub
				return null;
			}
			
		});
		
		table.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (table.getSelectedRow() != -1) {
					Data.selectedTile = 255;
					Data.selectedTileForeground = 255;
					Data.selectedTileBackground = 255;
					Data.multiSelectLevel = false;
					Data.selectedSprite = table.getSelectedRow();
					
					pkg.setEditMode(Constants.EDIT_MODE_SPRITES);
					Data.lp.repaint();
				}
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
			}
			
		});
	
		dfm.addColumn("Name:");
		dfm.addColumn("Player?:");
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		
		JButton btnAdd = new JButton("Add");
		JButton btnRemove = new JButton("Remove");
		JButton btnSetP = new JButton("Set Player");
		
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.map.spriteList.size() < 100) {
					addSprite();
				} else {
					JOptionPane.showMessageDialog(null, "Sprite limit, of 100 sprites, reached!", "Reached sprite limit", JOptionPane.INFORMATION_MESSAGE);
				}
			}
			
		});
		
		btnRemove.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (table.getRowCount() - 1 >= 0 && table.getSelectedRow() != -1) {
					int sr = table.getSelectedRow();
					
					Data.map.removeSprite(sr);
					dfm.removeRow(sr);
					
					dfm.fireTableDataChanged();
				
					if (sr - 1 >= 0) {
						table.setRowSelectionInterval(sr - 1, sr - 1);
						Data.selectedSprite = table.getSelectedRow();
					} else {
						Data.selectedSprite = 255;
					}
					
					Data.fileChanged = true;
					
					Data.lp.repaint();
				}
			}
			
		});
		
		btnSetP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.map.spriteList.get(table.getSelectedRow()).type == 1) {
					if (Data.map.playerSprite < Data.map.spriteList.size()) {
						table.getModel().setValueAt(false, Data.map.playerSprite, 1);
					}
					
					table.getModel().setValueAt(true, table.getSelectedRow(), 1);
					
					Data.map.playerSprite = table.getSelectedRow();
				}
			}
			
		});
		
		buttonPanel.add(btnAdd);
		buttonPanel.add(btnRemove);
		buttonPanel.add(btnSetP);

		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		
		JScrollPane tblScroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		add(tblScroll);
		add(buttonPanel);
	}
	
	public void setMap() {
		setList();
		
		dfm.fireTableDataChanged();
	}
	
	public void addSprite() {
		if (Data.map.spriteList.size() < Constants.SPRITE_LIMIT || Data.mode == Constants.MODE_CE) {
			JFileChooser fc = new JFileChooser(Settings.BASE_PATH + File.separatorChar + "sprites");
			
			fc.setDialogTitle("Select a sprite to load");
			fc.setFileFilter(new FileNameExtensionFilter("Pekka Kana 2 Sprite file", "spr", "cespr"));
			
			if (Settings.spritePreview) {
				FilePreviewPanel fpp = new FilePreviewPanel(FilePreviewPanel.SPRITE);
				
				fc.setAccessory(fpp);
				fc.addPropertyChangeListener(fpp);
			}
			
			int res = fc.showOpenDialog(null);
			
			if (res == JFileChooser.APPROVE_OPTION) {
				if (fc.getSelectedFile().exists()) {
					if (fc.getSelectedFile().getName().length() >= 13 && Data.mode != Constants.MODE_CE) {
						JOptionPane.showMessageDialog(null, "Filename is too long! (" + fc.getSelectedFile().getName().length() + " characters)\nMaximum characters allowed is 12!", "Filename too long", JOptionPane.ERROR_MESSAGE);
					} else {
						PK2Sprite s = new PK2Sprite();
						
						if (s.checkVersion(fc.getSelectedFile()) > -1) {
							s.loadFile(fc.getSelectedFile());
							s.loadBufferedImage();
							
							Vector v = new Vector();
							v.addElement(" " + s.getName() + " (" + fc.getSelectedFile().getName() + ")");

							dfm.addRow(v);

							Data.map.addSprite(s, fc.getSelectedFile().getName());

							table.setRowSelectionInterval(dfm.getRowCount() - 1, dfm.getRowCount() - 1);

							pkg.setEditMode(Constants.EDIT_MODE_SPRITES);

							Data.selectedSprite = dfm.getRowCount() - 1;
							Data.selectedTile = 255;
							Data.selectedTileForeground = 255;
							Data.selectedTileBackground = 255;
						} else {
							JOptionPane.showMessageDialog(null, "Only sprites version 1.2, 1.3, 1.4 allowed!", "Wrong Sprite", JOptionPane.ERROR_MESSAGE);
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "Coulnd't find file \"" + fc.getSelectedFile().getAbsolutePath() + "\".", "Error", JOptionPane.OK_OPTION);
				}
			}
		}
	}
	
	public void setList() {
		for (int i = 0; i < dfm.getRowCount(); i++) {
			dfm.removeRow(i);
		}
		
		dfm.getDataVector().clear();
		
		for (int i = 0; i < Data.map.spriteList.size(); i++) {
			if (Data.map.spriteList.get(i) != null) {
				Vector<String> v = new Vector<String>();
				v.addElement(" " + Data.map.spriteList.get(i).getName() + " (" + Data.map.spriteList.get(i).filename + ")");
				
				if (Data.map.spriteList.get(i).type == 1 && Data.map.playerSprite == i) {
					v.addElement("true");
				}
				
				dfm.addRow(v);
			}
		}
	}
}
