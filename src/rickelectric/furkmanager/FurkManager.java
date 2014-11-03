package rickelectric.furkmanager;

import java.awt.Component;

import javax.swing.JOptionPane;
import javax.swing.UIManager;

import rickelectric.furkmanager.idownloader.DownloadManager;
import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.network.APIFolderManager;
import rickelectric.furkmanager.network.InstanceConn;
import rickelectric.furkmanager.network.RequestCache;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.network.api.API_Label;
import rickelectric.furkmanager.network.api.API_UserData;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.FMTrayBox;
import rickelectric.furkmanager.views.LoginSplashWindow;
import rickelectric.furkmanager.views.windows.APIConsole;
import rickelectric.furkmanager.views.windows.AddDownloadFrame;
import rickelectric.furkmanager.views.windows.ImgCacheViewer;
import rickelectric.furkmanager.views.windows.MainEnvironment;
import rickelectric.furkmanager.views.windows.MainWindow;
import rickelectric.furkmanager.views.windows.PrimaryEnv;

public class FurkManager {

	private static boolean tray = false;
	private static String addString = null;

	private static FurkTrayIcon trayIcon = null;
	private static FMTrayBox trayBox = null;

	private static LoginSplashWindow lWin = null;
	private static PrimaryEnv mWin = null;
	private static DownloadManager dwm = null;

	private static APIConsole console = null;
	private static ImgCacheViewer cache = null;

	private static enum AlertType {
		MESSAGE, WARNING, ERROR, INFO
	}

	public static final AlertType TRAY_MESSAGE = AlertType.MESSAGE,
			TRAY_WARNING = AlertType.WARNING, TRAY_ERROR = AlertType.ERROR,
			TRAY_INFO = AlertType.INFO;

	public static void trayAlert(AlertType type, String title, String msg,
			Runnable action) {
		if (trayIcon == null)
			return;
		if (type == null)
			return;
		if (type == TRAY_ERROR)
			trayIcon.popupError(title, msg, action);
		else if (type == TRAY_WARNING)
			trayIcon.popupWarning(title, msg, action);
		else if (type == TRAY_INFO)
			trayIcon.popupInfo(title, msg, action);
		else
			trayIcon.popupMessage(title, msg, action);
	}

	public static void alerts(String s) {
		if (trayIcon != null)
			trayIcon.popupMessage("Alert", s, null);
		else
			JOptionPane.showMessageDialog(null, s);
	}

	public static void LAF(int i) {
		try {
			if (i == 0)
				UIManager.setLookAndFeel(UIManager
						.getSystemLookAndFeelClassName());
			else if (i == 1)
				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());
			else if (i == 2)
				UIManager
						.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
		} catch (Exception evt) {
		}
	}

	public static boolean loadingSplash(boolean btns) {
		if (lWin != null) {
			if (lWin.isShowing()) {
				return false;
			}
		}
		lWin = new LoginSplashWindow();
		lWin.splashMode();
		lWin.setVisible(true);
		return true;
	}

	public static void main(String[] args) {
		// Show Loading Splash, Exit Application On Close

		LAF(0);
		/**
		 * Remove
		 */
		//APIBridge.dummy = true;

		loadingSplash(true);

		ThreadPool.init();
		SettingsManager.init();

		UtilBox.init();
		RequestCache.init();

		// Parse Arguments
		int len = args.length;
		int iter = 0;
		while (iter < len) {
			if (args[iter].equals("-tray")) {
				tray = true;
			}
			if (args[iter].contains(".torrent")) {
				addString = args[iter];
			}
			iter++;
		}

		try {
			lWin.setText("Initializing...");
			InstanceConn ic = new InstanceConn();
			lWin.setText("Initialization Complete");
			if (!ic.appStart()) {
				lWin.setText("Restoring FurkManager...");
				Thread.sleep(2000);
				if (addString != null) {
					lWin.setText("Opening Torrent...");
					ic.transmit(addString);
					addString = null;
				}
				System.exit(0);
			}
		} catch (InterruptedException e) {
			lWin.setText("Fatal Error. Exiting...");
			UtilBox.pause(800);
			e.printStackTrace();
			System.exit(0);
		}

		UtilBox.pause(400);
		lWin.setText("Switching To Login...");
		UtilBox.pause(400);
		trayBox = new FMTrayBox();
		trayBox.setMoveable(true);
		trayRun();
		if (tray) {
			lWin.setVisible(false);
		} else {
			lWin.loginMode();
		}
	}

	public static void appRun() {
		if (APIBridge.key() == null)
			lWin.loginMode();
		else {
			lWin.setText("Loading User Data...");
			if (!API_UserData.isLoaded())
				API_UserData.loadUserData();
			lWin.setText("Loading Files...");
			if (API_File.getAllCached() == null)
				API_File.getAllFinished();
			lWin.setText("Loading Folders...");
			if (API_Label.getAllCached() == null) {
				API_Label.getAll();
				lWin.setText("Folder Manager Initializing...");
				APIFolderManager.init(API_Label.root());
			}
			lWin.setText("Launching...");
			mainWin();
			lWin.setVisible(false);

			dwm = new DownloadManager();
			dwm.setVisible(false);

			if (addString != null) {
				new AddDownloadFrame(addString);
				addString = null;
			}
		}
	}

	private static void mainWin() {
		if (mWin != null) {
			if (mWin.isVisible()) {
				mWin.toFront();
			} else
				mWin.setVisible(true);
			return;
		}
		if (SettingsManager.getMainWinMode() == SettingsManager.ENV_MODE)
			mWin = new MainEnvironment();
		else
			mWin = new MainWindow();
		mWin.setLocationRelativeTo(lWin);
		mWin.setVisible(true);
	}

	public static void trayRun() {
		if (trayIcon == null)
			trayIcon = new FurkTrayIcon();
		else
			trayAlert(TRAY_INFO, "Minimized", "Looking For Me? I'm Down Here.",
					null);
	}

	public static void trayBox() {
		if (API.key() == null || mWin == null)
			return;
		if (!trayBox.isShowing())
			trayBox.position();
		trayBox.setVisible(!trayBox.isShowing());
	}

	public static void downloader(boolean b) {
		if (dwm == null) {
			if (!b)
				return;
			dwm = new DownloadManager();
			dwm.setLocationRelativeTo((Component) mWin);
		}
		dwm.setVisible(b);
		if (b)
			dwm.toFront();
	}

	public static void showImgCache(boolean b) {
		if (cache == null) {
			if (!b)
				return;
			cache = new ImgCacheViewer();
		}
		cache.setVisible(b);
		if (b)
			cache.toFront();
	}

	public static void showConsole(boolean b) {
		if (console == null) {
			if (!b)
				return;
			console = new APIConsole();
		}
		console.setVisible(b);
		if (b)
			console.toFront();
	}

	/**
	 * Saves Everything That Needs Saving.<br/>
	 * Flushes All Caches.<br/>
	 * Sends a logout request to the Furk API.
	 */
	public static void logout() {
		Thread t = new Thread() {
			public void run() {
				try {
					trayAlert(TRAY_MESSAGE, "Logging Out",
							"Logging Out. Please Wait...", null);

					showConsole(false);
					RequestCache.APIR.flush();
					showImgCache(false);
					RequestCache.ImageR.flush();

					// RequestCache.init();
					API.flushAll();
					if (dwm != null)
						DownloadManager.persist();
					downloader(false);

					mWin.setEnabled(false);
					try {
						APIBridge.userLogout();
					} catch (Exception e) {
						// TODO Log Errors
					}
					mWin.dispose();
					mWin = null;

					System.gc();
					lWin.loginMode();
					lWin.setText("User Logged Out");
					lWin.setVisible(true);
				} catch (Exception e) {
					if (mWin != null)
						mWin.setEnabled(true);
				}
			}
		};
		t.start();
	}

	public static void exit() {
		Thread t = new Thread() {
			public void run() {
				try {
					DownloadManager.persist();
					if (lWin.isShowing()) {
						lWin.setVisible(false);
						while (lWin.isVisible()) {
							Thread.sleep(100);
						}
					}
					System.exit(0);
				} catch (Exception e) {
					System.exit(e.hashCode());
				}
			}
		};
		t.start();
	}

	public static void log(String str) {
		System.err.println(str);
	}

	public static PrimaryEnv getMainWindow() {
		return mWin;
	}

	public static void mWinModeChanged() {
		mWin.dispose();
		mWin = null;
		mainWin();
	}

}
