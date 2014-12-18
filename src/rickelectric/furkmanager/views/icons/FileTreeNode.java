package rickelectric.furkmanager.views.icons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import rickelectric.UtilBox;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.idownloader.DownloadManager;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.network.APIFolderManager;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.panels.TFileTreePanel;
import rickelectric.furkmanager.views.windows.FurkFileView;
import rickelectric.img.ImageLoader;

public class FileTreeNode extends DefaultMutableTreeNode implements
		FurkTreeNode {

	private static final long serialVersionUID = 1L;

	private static boolean action;
	private boolean isAction;
	private FurkFile file;

	public boolean isBusy() {
		return isAction;
	}

	public boolean expanded = false;

	@Override
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

	@Override
	public JPopupMenu popupMenu() {
		return new ContextMenu(file);
	}

	@Override
	public void action() {

	}

	public void action(final Runnable r) {
		if (action)
			return;
		new Thread(new Runnable() {
			@Override
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
			view.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
					"sm/arrow_expand.png")));
			view.addActionListener(this);

			fview = new JMenuItem("Open Download Page");
			fview.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
					"sm/web_view.png")));
			fview.addActionListener(this);

			cp = new JMenuItem("Copy Download Page Link");
			cp.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
					"sm/edit_icon.png")));
			cp.addActionListener(this);

			JMenu download = new JMenu("Download File Using...");
			download.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
					"sm/download_gr.png")));
			{
				idm = new JMenuItem("Internet Download Manager");
				idm.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
						"sm/idm.png")));
				idm.addActionListener(this);
				if (SettingsManager.getInstance().idm()) {
					download.add(idm);
				}

				internal = new JMenuItem("Internal Downloader");
				internal.setIcon(new ImageIcon(ImageLoader.getInstance()
						.getImage("sm/file_download.png")));
				internal.addActionListener(this);
				if (cFile.getSize() > 104857600L) {
					internal.setEnabled(false);
					internal.setToolTipText("For files <100MB in size");
				}
				download.add(internal);

				browser = new JMenuItem("Browser");
				browser.setIcon(new ImageIcon(ImageLoader.getInstance()
						.getImage("sm/internet_alt.png")));
				browser.addActionListener(this);
				download.add(browser);

				link = new JMenuItem("Link URL");
				link.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
						"sm/edit_icon.png")));
				link.addActionListener(this);
				download.add(link);
			}

			recycle = new JMenuItem("Send To Recycle Bin");
			recycle.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
					"sm/recycler.png")));
			recycle.addActionListener(this);

			add(view);
			add(fview);
			add(cp);
			add(download);
			add(recycle);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (action)
				return;
			Thread t = new Thread(new Runnable() {
				@Override
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
							UtilBox.getInstance().openUrl("https://www.furk.net"
									+ cFile.getUrlPage());
						}
						if (src.equals(cp)) {
							UtilBox.getInstance().sendToClipboard("https://www.furk.net"
									+ cFile.getUrlPage());
						}
						if (src.equals(browser)) {
							// Browser Download
							UtilBox.getInstance().openUrl(cFile.getUrlDl());
						}
						if (src.equals(idm)) {
							try {
								String path = SettingsManager.getInstance()
										.idmPath();
								Runtime.getRuntime().exec(
										new String[] {
												path,
												"-d",
												cFile.getUrlDl(),
												"/p",
												SettingsManager.getInstance()
														.getDownloadFolder() });
							} catch (Exception e) {
								FurkManager
										.trayAlert(
												FurkManager.TRAY_ERROR,
												"IDM Error",
												"Could Not Find IDM Download Manager.\nIDM Will Be Disabled.",
												null);
								SettingsManager.getInstance().idmPath(null);
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
							UtilBox.getInstance().sendToClipboard(cFile.getUrlDl());
							FurkManager.trayAlert(FurkManager.TRAY_INFO,
									"Link Copied",
									"Download link to '" + cFile.getName()
											+ "' Copied To Clipboard", null);
						}
						if (src.equals(recycle)) {
							// Recycle
							String id = cFile.getID();
							if(API_File.unlink(new String[] { id }))
								APIFolderManager.getModel().removeNodeFromParent(FileTreeNode.this);
						}
					} catch (Exception e) {
					}
					action = false;
				}
			});
			t.start();
		}
	}

	// public void parentRef(boolean hard) {
	// if (SettingsManager.getInstance().getMainWinMode() ==
	// SettingsManager.ENV_MODE)
	// ((File_FolderView) ((Main_FileView) MainEnvironment.getInstance()
	// .getView(1)).getTabContent(2)).refreshMyFolders(false);
	// }

	@Override
	public boolean draggable() {
		return true;
	}

	@Override
	public boolean droppable() {
		return false;
	}

}