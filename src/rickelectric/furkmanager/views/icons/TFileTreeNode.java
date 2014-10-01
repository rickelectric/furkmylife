package rickelectric.furkmanager.views.icons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.DefaultMutableTreeNode;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.idownloader.DownloadManager;
import rickelectric.furkmanager.idownloader.SDownload;
import rickelectric.furkmanager.models.FurkTFile;
import rickelectric.furkmanager.player.MediaStreamer;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.ImageViewer;
import rickelectric.furkmanager.views.panels.ScreenshotViewPanel;

public class TFileTreeNode extends DefaultMutableTreeNode implements
		FurkTreeNode {
	private static final long serialVersionUID = 1L;

	private static boolean action;
	private boolean isAction;
	private FurkTFile tfile;

	public boolean isBusy() {
		return isAction;
	}

	public FurkTFile getUserObject() {
		return tfile;
	}

	public TFileTreeNode(FurkTFile tfile) {
		super(tfile.getName());
		isAction = false;
		this.tfile = tfile;
	}

	public String toString() {
		return tfile.getName();
	}

	private class ContextMenu extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;

		private JMenuItem browser, internal, idm, link, play, vplay, view,
				screenshots;

		public ContextMenu() {
			super();

			view = new JMenuItem("View Image");
			view.addActionListener(this);
			if (tfile.getContentType().toLowerCase().contains("image")) {
				view.setIcon(new ImageIcon(FurkManager.class
						.getResource("img/sm/arrow_expand.png")));
				add(view);
			}

			play = new JMenuItem("Stream Audio (Experimental)");
			play.addActionListener(this);
			if (tfile.getContentType().toLowerCase().contains("audio")) {
				play.setIcon(new ImageIcon(FurkManager.class
						.getResource("img/sm/play.png")));
				add(play);
			}

			vplay = new JMenuItem("Stream Video (Experimental)");
			vplay.addActionListener(this);
			if (tfile.getContentType().toLowerCase().contains("video")) {
				vplay.setIcon(new ImageIcon(FurkManager.class
						.getResource("img/sm/play.png")));
				add(vplay);
			}

			JMenu download = new JMenu("Download Using...");
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
				if (tfile.getSize() > 104857600L) {
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
			if (tfile.getUrlDl() != null && !tfile.getUrlDl().equals(""))
				add(download);

			screenshots = new JMenuItem("View Screenshots");
			screenshots.addActionListener(this);

			if (tfile.getContentType().toLowerCase().contains("video")
					&& tfile.getThumbURL() != null
					&& !tfile.getThumbURL().equals("")) {
				screenshots.setIcon(new ImageIcon(FurkManager.class
						.getResource("img/sm/web_view.png")));
				add(screenshots);
			}

		}

		public void actionPerformed(final ActionEvent e) {
			if (action)
				return;
			Thread t = new Thread(new Runnable() {
				public void run() {
					action = true;
					isAction = true;
					try {
						Object src = e.getSource();
						if (src.equals(play)) {
							MediaStreamer.init();
							MediaStreamer.playAudio(tfile.getUrlDl());
						}
						if (src.equals(vplay)) {
							MediaStreamer.init();
							MediaStreamer.playVideo(tfile.getUrlDl());
						}
						if (src.equals(view)) {

							SDownload d = new SDownload(new URL(tfile.getUrlDl()));
							new ImageViewer(d).setVisible(true);

							/*
							 * BufferedImage img = StreamDownloader
							 * .getImageStream(tfile.getUrlDl(), 8); if (img !=
							 * null) JOptionPane.showMessageDialog(null, new
							 * JLabel( new ImageIcon(img)), tfile.getName(),
							 * JOptionPane.PLAIN_MESSAGE); else FurkManager
							 * .trayAlert( FurkManager.TRAY_ERROR,
							 * "Cannot Access File",
							 * "Server Error. Unable To Access Image File For Viewing."
							 * , null);
							 */
						}
						if (src.equals(screenshots)) {
							new ScreenshotViewPanel(tfile);
						}
						if (src.equals(browser)) {
							// Browser Download
							UtilBox.openUrl(tfile.getUrlDl());
						}
						if (src.equals(idm)) {
							String path = SettingsManager.idmPath();
							Runtime.getRuntime()
									.exec(new String[] { path, "-d",
											(tfile).getUrlDl(), "/p",
											SettingsManager.getDownloadFolder() });
						}
						if (src.equals(internal)) {
							String savePath = DownloadManager
									.actionSaveTo(true);
							boolean dwld = false;
							if (savePath != null && !savePath.equals(""))
								dwld = DownloadManager.addDownload(
										tfile.getName(), tfile.getUrlDl(),
										savePath, true);
							if (dwld)
								FurkManager.trayAlert(
										FurkManager.TRAY_INFO,
										"Downloading...",
										"File "
												+ tfile.getName()
												+ " is being downloaded uing the internal downloader.",
										null);
						}
						if (src.equals(link)) {
							// Show Link
							UtilBox.sendToClipboard(tfile.getUrlDl());
							FurkManager.trayAlert(FurkManager.TRAY_INFO,
									"Link Copied",
									"Download link to '" + tfile.getName()
											+ "' Copied To Clipboard", null);
						}
					} catch (Exception e) {
					}
					isAction = false;
					action = false;

				}
			});
			t.start();
		}
	}

	public JPopupMenu popupMenu() {
		return new ContextMenu();
	}

	public void action() {
		isAction = true;
		action = true;
		new Thread(new Runnable() {
			public void run() {
				try {
					if (tfile.getContentType().toLowerCase().contains("image")) {
						SDownload d = new SDownload(new URL(tfile.getUrlDl()));
						new ImageViewer(d).setVisible(true);
					}

					if (tfile.getContentType().toLowerCase().contains("audio")) {
						MediaStreamer.init();
						MediaStreamer.playAudio(tfile.getUrlDl());
					}

					if (tfile.getContentType().toLowerCase().contains("video")) {
						MediaStreamer.init();
						MediaStreamer.playVideo(tfile.getUrlDl());
					}
				} catch (Exception e) {
				}
				isAction = false;
				action = false;
			}
		}).start();
	}

	public boolean draggable() {
		return false;
	}

	public boolean droppable() {
		return false;
	}

}
