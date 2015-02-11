package rickelectric.furkmanager.views.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.views.windows.AddDownloadFrame;

public class Main_PopupMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;

	private JMenuItem mi_addfdownload;
	private JMenuItem mi_logout;

	private JMenuItem mi_exit;

	private JMenuItem mi_downloadman;

	private JMenuItem mi_imgcache;

	private JMenuItem mi_apiconsole;

	private JMenuItem mi_topics;

	private JMenuItem mi_about;

	public Main_PopupMenu() {
		JMenu mnFile = new JMenu("File");
		add(mnFile);

		mi_addfdownload = new JMenuItem("Add Furk Download");
		mi_addfdownload.addActionListener(this);
		mnFile.add(mi_addfdownload);

		mi_logout = new JMenuItem("Log Out");
		mi_logout.addActionListener(this);
		mi_logout.setEnabled(false);
		mnFile.add(mi_logout);

		mi_exit = new JMenuItem("Exit");
		mi_exit.addActionListener(this);
		mi_exit.setEnabled(false);
		mnFile.add(mi_exit);

		JMenu mnTools = new JMenu("Tools");
		add(mnTools);

		mi_downloadman = new JMenuItem("File Download Manager");
		mi_downloadman.addActionListener(this);
		mnTools.add(mi_downloadman);

		mi_imgcache = new JMenuItem("Image Cache Viewer");
		mi_imgcache.addActionListener(this);
		mnTools.add(mi_imgcache);

		mi_apiconsole = new JMenuItem("API Console");
		mi_apiconsole.addActionListener(this);
		mnTools.add(mi_apiconsole);

		JMenu mnHelp = new JMenu("Help");
		add(mnHelp);

		mi_topics = new JMenuItem("Topics");
		mi_topics.addActionListener(this);
		mnHelp.add(mi_topics);

		mi_about = new JMenuItem("About");
		mi_about.addActionListener(this);
		mnHelp.add(mi_about);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src.equals(mi_addfdownload)) {
			new AddDownloadFrame().setVisible(true);
		}
		if (src.equals(mi_logout)) {
			FurkManager.logout();
		}
		if (src.equals(mi_exit)) {
			FurkManager.exit();
		}
		if (src.equals(mi_downloadman)) {
			FurkManager.downloader(true);
		}
		if (src.equals(mi_apiconsole)) {
			FurkManager.showConsole(true);
		}
		if (src.equals(mi_imgcache)) {
			FurkManager.showImgCache(true);
		}
		if (src.equals(mi_topics)) {

		}
		if (src.equals(mi_about)) {

		}

	}

}
