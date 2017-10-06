package gui;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.EventObject;
import java.util.Vector;

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

import data.Data;
import data.Settings;
import pekkakana.PK2Map;
import pekkakana.PK2Sprite;

public class SpritePanel extends JPanel {

	DefaultTableModel dfm;
	JTable table;
	
	public SpritePanel() {
		dfm = new DefaultTableModel();
		
		table = new JTable(dfm);
		
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setShowGrid(false);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
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
			public void mousePressed(MouseEvent arg0) {
				if (table.getSelectedRow() != -1) {
					Data.selectedTile = 255;
					Data.selectedTileForeground = 255;
					Data.selectedTileBackground = 255;
					Data.multiSelectLevel = false;
					Data.selectedSprite = table.getSelectedRow();
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
		buttonPanel.setLayout(new GridLayout(1, 3));
		
		JButton btnAdd = new JButton("Add");
		JButton btnRemove = new JButton("Remove");
		JButton btnSetP = new JButton("Set Player");
		
		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				addSprite();
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
				}
			}
			
		});
		
		btnSetP.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (Data.map.spriteList.get(table.getSelectedRow()).type == 1) {
					table.getModel().setValueAt(false, Data.map.playerSprite, 1);
					table.getModel().setValueAt(true, table.getSelectedRow(), 1);
					
					Data.map.playerSprite = table.getSelectedRow();
				}
			}
			
		});
		
		buttonPanel.add(btnAdd);
		buttonPanel.add(btnRemove);
		buttonPanel.add(btnSetP);
		
		table.setPreferredSize(new Dimension(260, 400));
		table.setPreferredScrollableViewportSize(new Dimension(250, 400));
		
		JScrollPane tblScroll = new JScrollPane(table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		add(tblScroll, BorderLayout.CENTER);
		add(buttonPanel, BorderLayout.SOUTH);
	}
	
	public void setMap() {
		setList();
		
		dfm.fireTableDataChanged();
	}
	
	public void addSprite() {
		JFileChooser fc = new JFileChooser(Settings.BASE_PATH + File.separatorChar + "sprites");
		
		fc.setDialogTitle("Select a sprite to load");
		fc.setFileFilter(new FileNameExtensionFilter("Pekka Kana 2 Sprite file", "spr"));
		
		int res = fc.showOpenDialog(null);
		
		if (res == JFileChooser.APPROVE_OPTION) {
			if (fc.getSelectedFile().exists()) {
				PK2Sprite s = new PK2Sprite(fc.getSelectedFile().getName());
				
				Vector v = new Vector();
				v.addElement(" " + s.getName() + " (" + fc.getSelectedFile().getName() + ")");
				
				dfm.addRow(v);
				
				Data.map.addSprite(s, fc.getSelectedFile().getName());
				
				table.setRowSelectionInterval(dfm.getRowCount() - 1, dfm.getRowCount() - 1);
				
				Data.selectedSprite = table.getSelectedRow();
				Data.selectedTile = 255;
				Data.selectedTileForeground = 255;
				Data.selectedTileBackground = 255;
			} else {
				JOptionPane.showMessageDialog(null, "Coulnd't find file '" + fc.getSelectedFile().getName() + "'.", "Error", JOptionPane.OK_OPTION);
			}
		}
	}
	
	public void setList() {
		for (int i = 0; i < dfm.getRowCount(); i++) {
			dfm.removeRow(i);
		}
		
		dfm.getDataVector().clear();
		
		for (int i = 0; i < Data.map.spriteList.size(); i++) {
			Vector<String> v = new Vector<String>();
			v.addElement(" " + Data.map.spriteList.get(i).getName() + " (" + Data.map.spriteList.get(i).filename + ")");
			
			if (Data.map.spriteList.get(i).type == 1 && Data.map.playerSprite == i) {
				v.addElement("true");
			}
			
			dfm.addRow(v);
		}
	}
}
