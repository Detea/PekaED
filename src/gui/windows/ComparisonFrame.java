package gui.windows;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JList;

public class ComparisonFrame extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ComparisonFrame frame = new ComparisonFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ComparisonFrame() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1174, 762);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Level", null, panel, null);
		panel.setLayout(new BorderLayout(0, 0));
		
		JSplitPane splitPane = new JSplitPane();
		panel.add(splitPane);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Tileset", null, panel_1, null);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Background", null, panel_2, null);
		
		JPanel panel_3 = new JPanel();
		tabbedPane.addTab("Properties", null, panel_3, null);
		
		JPanel panel_4 = new JPanel();
		tabbedPane.addTab("Sprite list", null, panel_4, null);
		panel_4.setLayout(null);
		
		JLabel lblLevelFile = new JLabel("Level file 1");
		lblLevelFile.setBounds(10, 11, 92, 14);
		panel_4.add(lblLevelFile);
		
		JList list = new JList();
		list.setBounds(10, 37, 206, 616);
		panel_4.add(list);
		
		JLabel lblLevelFile_1 = new JLabel("Level file 2");
		lblLevelFile_1.setBounds(397, 11, 75, 14);
		panel_4.add(lblLevelFile_1);
		
		JList list_1 = new JList();
		list_1.setBounds(397, 37, 206, 616);
		panel_4.add(list_1);
	}
}
