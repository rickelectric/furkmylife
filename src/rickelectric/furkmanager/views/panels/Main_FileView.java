package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.swingmods.TranslucentPane;
import rickelectric.img.ImageLoader;

public class Main_FileView extends TranslucentPane {
	private static final long serialVersionUID = 1L;

	private File_FolderView pane_folders;

	private File_MyFiles pane_myfiles;

	private File_MyFiles pane_recycler;

	private JTabbedPane tabbedPane;

	public Main_FileView() {
		super();
		setAlpha(1);
		setLayout(null);

		setPreferredSize(new Dimension(561, 400));
		if (SettingsManager.getInstance().getMainWinMode() == SettingsManager.ENV_MODE) {
			setBackground(Color.darkGray);
			setPreferredSize(new Dimension(561,
					GraphicsEnvironment.getLocalGraphicsEnvironment()
							.getMaximumWindowBounds().height - 200));
		}

		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setBounds(4, 0, 554, getPreferredSize().height - 8);
		add(tabbedPane);

		pane_myfiles = new File_MyFiles(File_MyFiles.MYFILES, false);

		tabbedPane.addTab(
				"My Files",
				new ImageIcon(ImageLoader.getInstance().getImage("dash/Files-16.png")),
				pane_myfiles.resultScroller, "All My Files");
		pane_myfiles.setLayout(null);

		pane_folders = new File_FolderView();
		tabbedPane.addTab(
				"Folders (Labels)",
				new ImageIcon(ImageLoader.getInstance().getImage("tree/folder-blue-16.png")),
				pane_folders, "Organize Files In Folders");
		//pane_folders.setLayout(null);

		JPanel pane_find = new SearchPanel(SearchPanel.FILESEARCH);
		tabbedPane.addTab(
				"Find In My Files",
				new ImageIcon(ImageLoader.getInstance().getImage("sm/web_view.png")), pane_find, null);
		pane_find.setLayout(null);

		JPanel pane_search = new SearchPanel(SearchPanel.FURKSEARCH);
		tabbedPane.addTab("Search Furk", null, pane_search, null);
		pane_search.setLayout(null);

		pane_recycler = new File_MyFiles(File_MyFiles.RECYCLER, true);
		tabbedPane.addTab(
				"Recycle Bin",
				new ImageIcon(ImageLoader.getInstance().getImage("sm/recycler.png")),
				pane_recycler.resultScroller, null);

	}

	public void refreshMyFiles(boolean hardReload) {
		pane_myfiles.refreshMyFiles(hardReload);
		pane_recycler.refreshMyFiles(hardReload);
	}

	public void refreshActive(boolean hardReload) {
		int selectedTab = tabbedPane.getSelectedIndex();
		if (selectedTab == 0) {
			pane_myfiles.refreshMyFiles(hardReload);
		} else if (selectedTab == 1) {
			pane_folders.refreshMyFolders(hardReload);
		} else if (selectedTab == tabbedPane.getTabCount() - 1) {
			pane_recycler.refreshMyFiles(hardReload);
		}
	}

	public void switchPrepare() {
		getParent().remove(this);
	}

	public Component getTabContent(int i) {
		return tabbedPane.getTabComponentAt(i);
	}

}
