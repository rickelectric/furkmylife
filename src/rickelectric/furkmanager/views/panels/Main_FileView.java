package rickelectric.furkmanager.views.panels;

import java.awt.Dimension;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.swingmods.TranslucentPane;

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

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(4, 0, 554, 394);
		add(tabbedPane);

		pane_myfiles = new File_MyFiles(File_MyFiles.MYFILES);

		tabbedPane.addTab("My Files", new ImageIcon(FurkManager.class.getResource("img/dash/Files-16.png")),
				pane_myfiles, "All My Files");
		pane_myfiles.setLayout(null);

		pane_folders = new File_FolderView();
		tabbedPane.addTab("Folders (Labels)", null, pane_folders,
				"Organize Files In Folders");
		pane_folders.setLayout(null);

		JPanel pane_find = new SearchPanel(SearchPanel.FILESEARCH);
		tabbedPane.addTab("Find In My Files", null, pane_find, null);
		pane_find.setLayout(null);

		JPanel pane_search = new SearchPanel(SearchPanel.FURKSEARCH);
		tabbedPane.addTab("Search Furk", null, pane_search, null);
		pane_search.setLayout(null);

		pane_recycler = new File_MyFiles(File_MyFiles.RECYCLER);
		tabbedPane.addTab("Recycle Bin", null, pane_recycler, null);
		
	}
	
	public void refreshMyFiles(boolean hardReload){
		pane_myfiles.refreshMyFiles(hardReload);
		pane_recycler.refreshMyFiles(hardReload);
	}

}
