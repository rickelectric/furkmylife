package rickelectric.furkmanager.views.panels;

import java.awt.Dimension;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import rickelectric.furkmanager.views.swingmods.TranslucentPane;

public class Main_FeedView extends TranslucentPane {
	private static final long serialVersionUID = 1L;

	
	public Main_FeedView() {
		super();
		setAlpha(1);
		setPreferredSize(new Dimension(561, 400));
		setLayout(null);
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(4, 0, 554, 394);
		add(tabbedPane);
		
		JPanel panel_feeds = new JPanel();
		tabbedPane.addTab("All Feeds", null, panel_feeds, null);
		panel_feeds.setLayout(null);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Add New Feed", null, panel, null);
		panel.setLayout(null);
		
	}

}
