package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import javax.swing.JTabbedPane;

import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.swingmods.TranslucentPane;

public class Main_DownloadView extends TranslucentPane {
	private static final long serialVersionUID = 1L;

	private Download_MyDownloads pane_active, pane_failed;

	private SearchPanel pane_metasearch;

	public Main_DownloadView() {
		super();
		setAlpha(1);
		setDoubleBuffered(true);
		setPreferredSize(new Dimension(561, 400));
		setLayout(null);
		if (SettingsManager.getMainWinMode() == SettingsManager.ENV_MODE) {
			setBackground(Color.cyan);
			setPreferredSize(new Dimension(561,
					GraphicsEnvironment.getLocalGraphicsEnvironment()
							.getMaximumWindowBounds().height - 200));
		}

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(4, 0, 554, getPreferredSize().height - 8);
		add(tabbedPane);

		pane_active = new Download_MyDownloads(Download_MyDownloads.ACTIVE);
		tabbedPane.addTab("Active Downloads", null, pane_active.resultScroller, null);

		pane_failed = new Download_MyDownloads(Download_MyDownloads.FAILED);
		tabbedPane.addTab("Failed Downloads", null, pane_failed.resultScroller, null);
		pane_failed.setLayout(null);

		pane_metasearch = new SearchPanel(SearchPanel.METASEARCH);
		tabbedPane.addTab("Search The Web", null, pane_metasearch, null);
		pane_metasearch.setLayout(null);

	}

	public void refreshMyDownloads(final boolean hardReload) {
		pane_active.refreshMyDownloads(hardReload);
		pane_failed.refreshMyDownloads(hardReload);
	}
}
