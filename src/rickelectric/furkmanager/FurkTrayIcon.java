package rickelectric.furkmanager;

import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import rickelectric.furkmanager.network.FurkBridge;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.windows.AddDownloadFrame;

public class FurkTrayIcon {

	public static void main(String[] a) {
		new FurkTrayIcon();
	}

	private static TrayIcon trayIcon = null;

	private ActionListener defAction = new ActionListener() {
		@Override
		public void actionPerformed(ActionEvent e) {
			ThreadPool.run(new Runnable() {
				@Override
				public void run() {
					FurkManager.appRun();
				}
			});
		}
	};

	class ContextMenu extends PopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;
		private MenuItem showApp;
		private MenuItem addDl;
		private MenuItem dlsView;
		private MenuItem anim;
		private MenuItem hardExitApp;

		public ContextMenu(boolean isLoggedIn) {
			setFont(new Font("Dialog", Font.BOLD, 12));

			showApp = new MenuItem("Show Main Window");
			showApp.addActionListener(defAction);
			add(showApp);
			
			trayIcon.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e){
					if(e.getButton()==MouseEvent.BUTTON1){
						if(e.getClickCount()==1 && API.key()!=null){
							FurkManager.trayBox();
						}
					}
				}
			});

			if (FurkBridge.key() != null) {
				addDl = new MenuItem("Add Furk Download");
				addDl.addActionListener(this);
				add(addDl);

				dlsView = new MenuItem("Show Internal Downloader");
				dlsView.addActionListener(this);
				add(dlsView);
			}
			anim = new MenuItem("Test Loading Animation");
			anim.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					loading();
					UtilBox.pause(2000);
					stopLoading();
				}
			});
			add(anim);

			hardExitApp = new MenuItem("Exit");
			hardExitApp.addActionListener(this);
			add(hardExitApp);
		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			ThreadPool.run(new Runnable() {
				@Override
				public void run() {
					if (e.getSource().equals(addDl))
						new AddDownloadFrame().setVisible(true);
					if (e.getSource().equals(dlsView))
						FurkManager.downloader(true);
					if (e.getSource().equals(hardExitApp)) {
						if (FurkBridge.key() != null) {
							popupMessage("Exiting",
									"Logging out. Please wait.", null);
							try {
								Thread.sleep(1000);
							} catch (Exception e) {
							}
						}
						FurkManager.exit();
					}
				}
			});
		}

	}

	public FurkTrayIcon() {

		if (trayIcon != null)
			return;

		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage(
					FurkManager.class.getResource("img/fr.png"));

			ActionListener exitAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if (FurkBridge.key() != null)
						popupMessage("Exiting", "Logging out. Please wait.",
								null);
					FurkManager.exit();
				}
			};

			final PopupMenu popup = new PopupMenu();
			// create menu item for the default action
			MenuItem showApp = new MenuItem("Show Main Window");
			showApp.addActionListener(defAction);
			popup.add(showApp);

			MenuItem addDl = new MenuItem("Add Furk Download");
			addDl.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					new AddDownloadFrame().setVisible(true);
				}
			});
			if (FurkBridge.key() != null)
				popup.add(addDl);

			MenuItem dlsView = new MenuItem("Show File Download Manager");
			dlsView.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FurkManager.downloader(true);
				}
			});
			if (FurkBridge.key() != null)
				popup.add(dlsView);
			popup.setFont(new Font("Dialog", Font.BOLD, 12));

			MenuItem anim = new MenuItem("Test Loading Animation");
			anim.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					loading();
					UtilBox.pause(2000);
					stopLoading();
				}
			});
			popup.add(anim);

			MenuItem hardExitApp = new MenuItem("Exit");
			hardExitApp.addActionListener(exitAction);
			popup.add(hardExitApp);

			// construct a TrayIcon
			trayIcon = new TrayIcon(image, "FurkManager", popup);
			trayIcon.addMouseListener(new MouseAdapter(){
				@Override
				public void mouseClicked(MouseEvent e){
					if(e.getButton()==MouseEvent.BUTTON1){
						if(e.getClickCount()==1){
							FurkManager.trayBox();
						}
					}
				}
			});
			trayIcon.setImageAutoSize(true);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}

		} else {
			throw new RuntimeException("");
		}
	}

	Image currIcon = null;

	public void loading() {
		final Toolkit def = Toolkit.getDefaultToolkit();
		currIcon = trayIcon.getImage();
		trayIcon.setImage(def.getImage(FurkManager.class
				.getResource("img/ajax-loader.gif")));
	}

	public void stopLoading() {
		if (currIcon == null)
			return;
		trayIcon.setImage(currIcon);
	}

	public void actionRemove() {
		for (ActionListener a : trayIcon.getActionListeners()) {
			trayIcon.removeActionListener(a);
		}
	}

	public void popupMessage(final String title, final String text,
			final Runnable clickAction) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				actionRemove();
				trayIcon.displayMessage(title, text, TrayIcon.MessageType.NONE);
				if (clickAction != null)
					trayIcon.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							clickAction.run();
							actionRemove();
							trayIcon.addActionListener(defAction);
						}
					});
				else
					trayIcon.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							trayIcon.addActionListener(defAction);
						}
					});
				try {
					Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

	public void popupInfo(final String title, final String text,
			final Runnable clickAction) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				actionRemove();
				trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
				if (clickAction != null) {
					trayIcon.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							clickAction.run();
							actionRemove();
							trayIcon.addActionListener(defAction);
						}
					});
				} else
					trayIcon.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							trayIcon.addActionListener(defAction);
						}
					});
				try {
					Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

	public void popupWarning(final String title, final String text,
			final Runnable clickAction) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				actionRemove();
				trayIcon.displayMessage(title, text,
						TrayIcon.MessageType.WARNING);
				if (clickAction != null) {
					trayIcon.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							clickAction.run();
							actionRemove();
							trayIcon.addActionListener(defAction);
						}
					});
				} else
					trayIcon.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							trayIcon.addActionListener(defAction);
						}
					});
				try {
					Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

	public void popupError(final String title, final String text,
			final Runnable clickAction) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				actionRemove();
				trayIcon.displayMessage(title, text, TrayIcon.MessageType.ERROR);
				if (clickAction != null) {
					trayIcon.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							clickAction.run();
							actionRemove();
							trayIcon.addActionListener(defAction);
						}
					});
				} else
					trayIcon.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							trayIcon.addActionListener(defAction);
						}
					});
				try {
					Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				} catch (InterruptedException e) {
				}
			}
		}).start();
	}

}
