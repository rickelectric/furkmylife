package rickelectric.furkmanager.views.panels;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class APIMessagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTable table;
	private JLabel lblNewLabel;

	public APIMessagePanel() {
		setLayout(new BorderLayout(0, 0));

		table = new JTable();
		table.setModel(new DefaultTableModel(
			new Object[][] {
			},
			new String[] {
				"ID", "Subject", "Message"
			}
		));
		add(table, BorderLayout.CENTER);

		lblNewLabel = new JLabel("New label");
		lblNewLabel.setPreferredSize(new Dimension(46, 25));
		add(lblNewLabel, BorderLayout.NORTH);
	}

	public void update() {
		repaint();
	}

}
