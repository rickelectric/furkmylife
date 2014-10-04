package rickelectric.furkmanager.views.icons;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.idownloader.DownloadManager;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.panels.File_FolderView;
import rickelectric.furkmanager.views.panels.TFileTreePanel;
import rickelectric.furkmanager.views.windows.FurkFileView;

public class FileTreeNode extends DefaultMutableTreeNode implements
		FurkTreeNode {

	private static final long serialVersionUID = 1L;

	private static boolean action;
	private boolean isAction;
	private FurkFile file;

	private JTree parentTree;

	public boolean isBusy() {
		return isAction;
	}

	public void setParent(JTree parent) {
		this.parentTree = parent;
	}

	public boolean expanded = false;

	public FurkFile getUserObject() {
		return file;
	}

	public FileTreeNode(FurkFile file) {
		super(file.getName());
		this.file = file;
	}

	@Override
	public String toString() {
		return file.getName();
	}

	public JPopupMenu popupMenu() {
		return new ContextMenu(file);
	}

	public void action() {

	}

	public void action(final Runnable r) {
		if (action)
			return;
		new Thread(new Runnable() {
			public void run() {
				isAction = true;
				action = true;
				if (r != null)
					r.run();
				if (!expanded) {
					try {
						DefaultMutableTreeNode rfs = TFileTreePanel
								.getTFileTree(file);
						setAllowsChildren(true);

						int numChildren = rfs.getChildCount();

						for (int i = 0; i < numChildren; i++) {
							FileTreeNode.this.add(((DefaultMutableTreeNode) rfs
									.getChildAt(0)));
						}
						expanded = true;
					} catch (Exception e) {
						e.printStackTrace();
						FurkManager.trayAlert(FurkManager.TRAY_ERROR,
								"Connection Error",
								"Could not expand " + file.getName()
										+ " due to a connection error.", null);
					}
				}
				action = false;
				isAction = false;
				r.run();
			}
		}).start();

	}

	private class ContextMenu extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;

		private FurkFile cFile = null;

		private JMenuItem view, fview, link;
		private JMenuItem browser, idm, internal, cp;
		private JMenuItem recycle;

		public ContextMenu(FurkFile cFile) {
			super();
			this.cFile = cFile;

			view = new JMenuItem("View Details");
			view.setIcon(new ImageIcon(FurkManager.class
					.getResource("img/sm/arrow_expand.png")));
			view.addActionListener(this);

			fview = new JMenuItem("Open Download Page");
			fview.setIcon(new ImageIcon(FurkManager.class
					.getResource("img/sm/web_view.png")));
			fview.addActionListener(this);

			cp = new JMenuItem("Copy Download Page Link");
			cp.setIcon(new ImageIcon(FurkManager.class
					.getResource("img/sm/edit_icon.png")));
			cp.addActionListener(this);

			JMenu download = new JMenu("Download File Using...");
			download.setIcon(new ImageIcon(FurkManager.class
					.getResource("img/sm/download_gr.png")));
			{
				idm = new JMenuItem("Internet Download Manager");
				idm.setIcon(new ImageIcon(FurkManager.class
						.getResource("img/sm/idm.png")));
				idm.addActionListener(this);
				if (SettingsManager.idm()) {
					download.add(idm);
				}

				internal = new JMenuItem("Internal Downloader");
				internal.setIcon(new ImageIcon(FurkManager.class
						.getResource("img/sm/file_download.png")));
				internal.addActionListener(this);
				if (cFile.getSize() > 104857600L) {
					internal.setEnabled(false);
					internal.setToolTipText("For files <100MB in size");
				}
				download.add(internal);

				browser = new JMenuItem("Browser");
				browser.setIcon(new ImageIcon(FurkManager.class
						.getResource("img/sm/internet_alt.png")));
				browser.addActionListener(this);
				download.add(browser);

				link = new JMenuItem("Link URL");
				link.setIcon(new ImageIcon(FurkManager.class
						.getResource("img/sm/edit_icon.png")));
				link.addActionListener(this);
				download.add(link);
			}

			recycle = new JMenuItem("Send To Recycle Bin");
			recycle.setIcon(new ImageIcon(FurkManager.class
					.getResource("img/sm/recycler.png")));
			recycle.addActionListener(this);

			add(view);
			add(fview);
			add(cp);
			add(download);
			add(recycle);
		}

		public void actionPerformed(final ActionEvent e) {
			if (action)
				return;
			Thread t = new Thread(new Runnable() {
				public void run() {
					action = true;
					try {
						Object src = e.getSource();
						if (src.equals(view)) {
							// View
							new FurkFileView(cFile);
						}
						if (src.equals(fview)) {
							// Furk View
							UtilBox.openUrl("https://www.furk.net"
									+ cFile.getUrlPage());
						}
						if (src.equals(cp)) {
							UtilBox.sendToClipboard("https://www.furk.net"
									+ cFile.getUrlPage());
						}
						if (src.equals(browser)) {
							// Browser Download
							UtilBox.openUrl(cFile.getUrlDl());
						}
						if (src.equals(idm)) {
							try {
								String path = SettingsManager.idmPath();
								Runtime.getRuntime().exec(
										new String[] {
												path,
												"-d",
												cFile.getUrlDl(),
												"/p",
												SettingsManager
														.getDownloadFolder() });
							} catch (Exception e) {
								FurkManager
										.trayAlert(
												FurkManager.TRAY_ERROR,
												"IDM Error",
												"Could Not Find IDM Download Manager.\nIDM Will Be Disabled.",
												null);
								SettingsManager.idmPath(null);
							}
						}
						if (src.equals(internal)) {
							boolean dwld = DownloadManager.addDownload(
									cFile.getName(), cFile.getUrlDl(),
									DownloadManager.actionSaveTo(true), true);
							if (dwld)
								FurkManager.trayAlert(
										FurkManager.TRAY_INFO,
										"Downloading...",
										"File "
												+ cFile.getName()
												+ " is being downloaded uing the internal downloader.",
										null);
						}
						if (src.equals(link)) {
							// Show Link
							UtilBox.sendToClipboard(((FurkFile) cFile)
									.getUrlDl());
							FurkManager.trayAlert(FurkManager.TRAY_INFO,
									"Link Copied",
									"Download link to '" + cFile.getName()
											+ "' Copied To Clipboard", null);
						}
						if (src.equals(recycle)) {
							// Recycle
							String id = cFile.getID();
							API_File.unlink(new String[] { id });
							parentRef(true);
						}
					} catch (Exception e) {
					}
					action = false;
				}
			});
			t.start();
		}
	}

	public void parentRef(boolean hard) {
		Container parent = parentTree.getParent();
		try {
			while (!(parent instanceof File_FolderView)) {
				parent = parent.getParent();
			}
			((File_FolderView) parent).refreshMyFolders(hard);
		} catch (Exception e) {
			System.err.println("Could Not Find Proper Parent");
			e.printStackTrace();
		}
	}

	public boolean draggable() {
		return true;
	}

	public boolean droppable() {
		return false;
	}

}