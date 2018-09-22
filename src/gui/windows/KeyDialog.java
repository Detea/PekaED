package gui.windows;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTable;

import data.Data;

public class KeyDialog extends JDialog implements KeyListener {

	JTable table;
	int row, col;
	
	public KeyDialog(JTable table, int row) {
		addKeyListener(this);
		
		this.table = table;
		
		JLabel lblKey = new JLabel("    Press a key...");
		lblKey.setFont(new Font(lblKey.getFont().getName(), Font.PLAIN, 14));
		
		add(lblKey, BorderLayout.CENTER);
		
		setLocation(200, 200);
		setSize(150, 100);
		setVisible(true);
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		Data.key = e.getKeyCode();
		
		dispose();
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	

}
