package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.swingmods.TranslucentPane;

public class Main_DownloadView extends TranslucentPane {
	private static final long serialVersionUID = 1L;

	private Download_MyDownloads pane_active, pane_failed;

	private SearchPanel pane_metasearch;

	private JTabbedPane tabbedPane;

	public Main_DownloadView() {
		super();
		setAlpha(1);
		setDoubleBuffered(true);
		setPreferredSize(new Dimension(561, 400));
		setLayout(null);
		if (SettingsManager.getInstance().getMainWinMode() == SettingsManager.ENV_MODE) {
			setBackground(Color.cyan);
			setPreferredSize(new Dimension(561,
					GraphicsEnvironment.getLocalGraphicsEnvironment()
							.getMaximumWindowBounds().height - 200));
		}

		tabbedPane = new JTabbedPane(SwingConstants.TOP);
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

	public void refreshActive(boolean hardReload) {
		int selectedTab = tabbedPane.getSelectedIndex();
		if (selectedTab == 0) {
			pane_active.refreshMyDownloads(hardReload);
		} else if (selectedTab == 1) {
			pane_failed.refreshMyDownloads(hardReload);
		}
	}
}
