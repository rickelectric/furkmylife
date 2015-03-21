package rickelectric.furkmanager.beta_test.draggables.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import rickelectric.UtilBox;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.beta_test.draggables.Space;
import rickelectric.furkmanager.beta_test.draggables.models.FileItem;
import rickelectric.furkmanager.beta_test.draggables.models.FolderDescriptor;
import rickelectric.furkmanager.beta_test.draggables.models.FolderItem;
import rickelectric.furkmanager.beta_test.draggables.models.Item;
import rickelectric.furkmanager.idownloader.DownloadManager;
import rickelectric.furkmanager.network.api_new.FurkAPI;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.windows.FurkFileView;
import rickelectric.img.ImageLoader;

public class ItemContextMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;

	private Item space;

	private JMenuItem folder_delete, folder_delete_cascade;
	private JMenuItem folder_rename, folder_colorchange;
	private JMenuItem folder_anchor;

	private JMenuItem push_to_parent;

	private JMenuItem view, fview, link;
	private JMenuItem browser, idm, internal, copylink;
	private JMenuItem recycle;

	private Space parent;

	public ItemContextMenu(Space parent, Item spr) {
		this.parent = parent;
		this.space = spr;

		push_to_parent = new JMenuItem("Push To Parent Folder");
		push_to_parent.setIcon(new ImageIcon(ImageLoader.getInstance()
				.getImage("sm/file_upload.png")));
		push_to_parent.addActionListener(this);

		if (spr instanceof FolderItem) {
			if (((FolderItem) spr).getDescriptor().isOrphaned()) {
				JMenuItem o = new JMenuItem("--Orphaned Folder--");
				o.setEnabled(false);
				add(o);

				folder_anchor = new JMenuItem("Anchor Folder Here");
				folder_anchor.addActionListener(this);
				folder_anchor.setIcon(new ImageIcon(ImageLoader.getInstance()
						.getImage("sm/anchor-18.png")));
				add(folder_anchor);
				addSeparator();
			}

			folder_rename = new JMenuItem("Rename Folder");
			folder_rename.addActionListener(this);
			folder_rename.setIcon(new ImageIcon(ImageLoader.getInstance()
					.getImage("sm/edit_icon.png")));
			add(folder_rename);

			if (!space.getDescriptor().getParent()
					.equals(parent.getManager().getTree().getRoot())) {
				add(push_to_parent);
			}

			folder_delete = new JMenuItem("Delete Folder");
			folder_delete.addActionListener(this);
			folder_delete.setIcon(new ImageIcon(ImageLoader.getInstance()
					.getImage("sm/edit_delete.png")));
			add(folder_delete);

			folder_delete_cascade = new JMenuItem(
					"Delete Folder & It's Contents");
			folder_delete_cascade.addActionListener(this);
			folder_delete_cascade.setIcon(new ImageIcon(ImageLoader
					.getInstance().getImage("sm/remove.png")));
			add(folder_delete_cascade);
		} else if (spr instanceof FileItem) {
			view = new JMenuItem("View Details");
			view.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
					"sm/arrow_expand.png")));
			view.addActionListener(this);

			fview = new JMenuItem("Open Download Page");
			fview.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
					"sm/web_view.png")));
			fview.addActionListener(this);

			copylink = new JMenuItem("Copy Download Page Link");
			copylink.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
					"sm/edit_icon.png")));
			copylink.addActionListener(this);

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

				internal = new JMenuItem("J-Downloader (Internal)");
				internal.setIcon(new ImageIcon(ImageLoader.getInstance()
						.getImage("sm/file_download.png")));
				internal.addActionListener(this);
				if (((FileItem) spr).getDescriptor().getFileObject().getSize() > 104857600L) {
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

			if (!space.getDescriptor().getParent()
					.equals(parent.getManager().getTree().getRoot())) {
				add(push_to_parent);
			}

			recycle = new JMenuItem("Send To Recycle Bin");
			recycle.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
					"sm/recycler.png")));
			recycle.addActionListener(this);

			add(view);
			add(fview);
			add(copylink);
			add(download);
			add(recycle);
		}

	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Object src = e.getSource();
					if (src.equals(view)) {
						// View
						new FurkFileView(((FileItem) space).getDescriptor()
								.getFileObject());
					}
					if (src.equals(fview)) {
						// Furk View
						UtilBox.getInstance().openUrl(
								"https://www.furk.net"
										+ ((FileItem) space).getDescriptor()
												.getFileObject().getUrlPage());
					}
					if (src.equals(copylink)) {
						UtilBox.getInstance().sendToClipboard(
								"https://www.furk.net"
										+ ((FileItem) space).getDescriptor()
												.getFileObject().getUrlPage());
					}
					if (src.equals(browser)) {
						// Browser Download
						UtilBox.getInstance().openUrl(
								((FileItem) space).getDescriptor()
										.getFileObject().getUrlDl());
					}
					if (src.equals(idm)) {
						try {
							String path = SettingsManager.getInstance()
									.idmPath();
							Runtime.getRuntime()
									.exec(new String[] {
											path,
											"-d",
											((FileItem) space).getDescriptor()
													.getFileObject().getUrlDl(),
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
								((FileItem) space).getDescriptor()
										.getFileObject().getName(),
								((FileItem) space).getDescriptor()
										.getFileObject().getUrlDl(),
								DownloadManager.actionSaveTo(true), true);
						if (dwld)
							FurkManager
									.trayAlert(
											FurkManager.TRAY_INFO,
											"Downloading...",
											"File "
													+ ((FileItem) space)
															.getDescriptor()
															.getFileObject()
															.getName()
													+ " is being downloaded uing the internal downloader.",
											null);
					}
					if (src.equals(link)) {
						// Show Link
						UtilBox.getInstance().sendToClipboard(
								((FileItem) space).getDescriptor()
										.getFileObject().getUrlDl());
						FurkManager.trayAlert(FurkManager.TRAY_INFO,
								"Link Copied", "Download link to '"
										+ ((FileItem) space).getDescriptor()
												.getFileObject().getName()
										+ "' Copied To Clipboard", null);
					}
					if (src.equals(recycle)) {
						// Recycle
						String id = ((FileItem) space).getDescriptor()
								.getFileObject().getID();
						if (FurkAPI.getInstance().file()
								.unlinkFiles(new String[] { id })) {
							space.getDescriptor().getParent()
									.removeChild(space.getDescriptor());
							parent.loadItemsInCurrentFolder();
						}
					}
					
					if(src==push_to_parent){
						FolderDescriptor parentFolder = space.getDescriptor().getParent().getParent();
						if(parent==null) return;
						int resp = JOptionPane.showConfirmDialog(
								parent.getContentPane(),
								"Push Folder '"+space.getName()+"' To Parent '"+parentFolder.getName()+"'?",
								"Move Folder", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (resp == JOptionPane.YES_OPTION) {
							if (parent.getManager().getTree().move(space.getDescriptor(),parentFolder)) {
								parent.loadItemsInCurrentFolder();
							}
						}
					}
					
					if (src == folder_colorchange) {
						// TODO Change Color, Update The Label
					}
					if (src == folder_anchor) {
						FolderDescriptor folder = ((FolderItem) space)
								.getDescriptor();
						int resp = JOptionPane.showConfirmDialog(
								parent.getContentPane(),
								"Are You Sure You Want To Anchor This Orphaned Folder?",
								"Anchor Folder", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (resp == JOptionPane.YES_OPTION) {
							if (parent.getManager().getTree().move(folder,parent.getManager().getTree().getRoot())) {
								parent.loadItemsInCurrentFolder();
							}
						}
					}
					if (src == folder_rename) {
						FolderDescriptor folder = ((FolderItem) space)
								.getDescriptor();
						JTextField f = new JTextField();
						f.setColumns(40);
						f.setText(folder.getName());
						f.setSelectionStart(0);
						f.setSelectionEnd(folder.getName().length());
						int resp = JOptionPane.showConfirmDialog(
								parent.getContentPane(), f, "Rename",
								JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (resp == JOptionPane.OK_OPTION) {
							String name = f.getText();
							try {
								boolean renamed = parent
										.getManager()
										.getTree()
										.rename(((FolderItem) space)
												.getDescriptor(), name);
								if (!renamed)
									throw new RuntimeException("Rename Failed");
								parent.loadItemsInCurrentFolder();
							} catch (Exception e) {
								JOptionPane.showMessageDialog(
										parent.getContentPane(), e.getMessage());
							}
						}
					}

					if (src == folder_delete) {
						FolderDescriptor folder = ((FolderItem) space)
								.getDescriptor();
						int resp = JOptionPane.showConfirmDialog(
								parent.getContentPane(),
								"Are You Sure You Want To Delete This Folder?\n"
										+ "(This Operation Is Permanent)",
								"Delete", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						if (resp == JOptionPane.YES_OPTION) {
							if (parent.getManager().getTree().delete(folder)) {
								parent.loadItemsInCurrentFolder();
							}
						}
					}
				} catch (Exception e) {
				}
			}
		});
		t.setDaemon(true);
		t.start();

	}
}