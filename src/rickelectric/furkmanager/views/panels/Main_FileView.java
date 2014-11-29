package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.swingmods.TranslucentPane;

public class Main_FileView extends TranslucentPane {
	private static final long serialVersionUID = 1L;

	private File_FolderView pane_folders;

	private File_MyFiles pane_myfiles;

	private File_MyFiles pane_recycler;

	public Main_FileView() {
		super();
		setAlpha(1);
		setPreferredSize(new Dimension(561, 400));
		setLayout(null);
		if (SettingsManager.getInstance().getMainWinMode() == SettingsManager.ENV_MODE) {
			setBackground(Color.darkGray);
			setPreferredSize(new Dimension(561,
					GraphicsEnvironment.getLocalGraphicsEnvironment()
							.getMaximumWindowBounds().height - 200));
		}

		JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setBounds(4, 0, 554, getPreferredSize().height - 8);
		add(tabbedPane);

		pane_myfiles = new File_MyFiles(File_MyFiles.MYFILES, false);

		tabbedPane.addTab(
				"My Files",
				new ImageIcon(FurkManager.class
						.getResource("img/dash/Files-16.png")), pane_myfiles.resultScroller,
				"All My Files");
		pane_myfiles.setLayout(null);

		pane_folders = File_FolderView.getInstance();
		tabbedPane.addTab(
				"Folders (Labels)",
				new ImageIcon(FurkManager.class
						.getResource("img/tree/folder-blue-16.png")),
				pane_folders.scroller, "Organize Files In Folders");
		pane_folders.setLayout(null);

		JPanel pane_find = new SearchPanel(SearchPanel.FILESEARCH);
		tabbedPane.addTab(
				"Find In My Files",
				new ImageIcon(FurkManager.class
						.getResource("img/sm/web_view.png")), pane_find, null);
		pane_find.setLayout(null);

		JPanel pane_search = new SearchPanel(SearchPanel.FURKSEARCH);
		tabbedPane.addTab("Search Furk", null, pane_search, null);
		pane_search.setLayout(null);

		pane_recycler = new File_MyFiles(File_MyFiles.RECYCLER, true);
		tabbedPane.addTab(
				"Recycle Bin",
				new ImageIcon(FurkManager.class
						.getResource("img/sm/recycler.png")), pane_recycler.resultScroller,
				null);

	}

	public void refreshMyFiles(boolean hardReload) {
		pane_myfiles.refreshMyFiles(hardReload);
		pane_recycler.refreshMyFiles(hardReload);
	}

}
