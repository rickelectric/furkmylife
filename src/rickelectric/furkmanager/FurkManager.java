package rickelectric.furkmanager;

import java.awt.Color;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.UIManager;

import rickelectric.furkmanager.idownloader.DownloadManager;
import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.network.APIFolderManager;
import rickelectric.furkmanager.network.InstanceConn;
import rickelectric.furkmanager.network.RequestCache;
import rickelectric.furkmanager.network.StreamDownloader;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.network.api.API_Label;
import rickelectric.furkmanager.network.api.API_UserData;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.windows.APIConsole;
import rickelectric.furkmanager.views.windows.AddDownloadFrame;
import rickelectric.furkmanager.views.windows.ImgCacheViewer;
import rickelectric.furkmanager.views.windows.LoginWindow;
import rickelectric.furkmanager.views.windows.MainWindow;

public class FurkManager {
	
	private static boolean tray = false;
	private static String addString = null;

	private static LoginWindow lWin = null;
	private static FurkTrayIcon fIcon = null;
	private static MainWindow mWin = null;
	private static DownloadManager dwm = null;

	private static APIConsole console = null;
	private static ImgCacheViewer cache = null;

	private static JDialog frame;
	private static JLabel load;

	private static enum AlertType {
		MESSAGE, WARNING, ERROR, INFO
	}

	public static final AlertType TRAY_MESSAGE = AlertType.MESSAGE,
			TRAY_WARNING = AlertType.WARNING, TRAY_ERROR = AlertType.ERROR,
			TRAY_INFO = AlertType.INFO;

	public static void trayAlert(AlertType type, String title, String msg,
			Runnable action) {
		if (fIcon == null)
			return;
		if (type == null)
			return;
		if (type == TRAY_ERROR)
			fIcon.popupError(title, msg, action);
		else if (type == TRAY_WARNING)
			fIcon.popupWarning(title, msg, action);
		else if (type == TRAY_INFO)
			fIcon.popupInfo(title, msg, action);
		else
			fIcon.popupMessage(title, msg, action);
	}

	public static void alerts(String s) {
		if (fIcon != null)
			fIcon.popupMessage("Alert", s, null);
		else
			JOptionPane.showMessageDialog(null, s);
	}
	
	public static void LAF(int i){
		try {
			if(i==0) UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			else UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	    } catch (Exception evt) {}
	}
	
	public static boolean loading() {
		if(frame!=null && frame.isShowing()) return false;
		LAF(1);
		frame = new JDialog();
		frame.setLayout(null);
		frame.setSize(420, 100);
		frame.setLocationRelativeTo(null);
		frame.setTitle("FurkManager Starting Up...");
		frame.setIconImage(new ImageIcon(FurkManager.class
				.getResource("img/fr.png")).getImage());
		frame.setResizable(false);
		
		load = new JLabel("Loading...");
		load.setBounds(20, 10, 375, 20);
		load.setHorizontalAlignment(JLabel.CENTER);
		frame.add(load);
		JProgressBar bLoad = new JProgressBar();
		bLoad.setBounds(20, 40, 375, 20);
		bLoad.setForeground(Color.BLUE);
		bLoad.setOpaque(false);// setBackground(Color.GRAY);
		bLoad.setStringPainted(true);
		bLoad.setString("Please Wait...");
		bLoad.setIndeterminate(true);
		frame.add(bLoad);
		frame.setVisible(!tray);
		LAF(0);
		return true;
	}

	public static void main(String[] args) {
		loading();
		frame.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				exit();
			}
		});
		
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

		

		InstanceConn ic;
		ThreadPool.init();
		SettingsManager.init();

		UtilBox.init();
		RequestCache.init();

		try {
			load.setText("Initializing...");
			ic = new InstanceConn();
			load.setText("Initialization Complete");
			if (!ic.appStart()) {
				if (addString != null) {
					load.setText("Opening Torrent...");
					ic.transmit(addString);
					addString = null;
				}
				load.setText("Restoring FurkManager...");
				frame.dispose();
				return;
			}
		} catch (InterruptedException e) {
			load.setText("Fatal Error. Exiting...");
			UtilBox.pause(800);
			e.printStackTrace();
			frame.dispose();
			return;
		}

		UtilBox.pause(400);
		load.setText("Launching FurkManager...");
		UtilBox.pause(400);
		trayRun();
		if (!tray) {
			appRun();
		}

		frame.dispose();
	}

	public static void login() {
		if (lWin != null) {
			if (!lWin.isVisible())
				lWin.setVisible(true);
			lWin.toFront();
			return;
		}
		lWin = new LoginWindow();
		lWin.setLocationRelativeTo(null);
		lWin.setVisible(true);
	}

	public static void appRun() {
		if (APIBridge.key() == null)
			login();
		else {
			try{
				if(loading()){
					frame.setTitle("FurkManager Loading...");
					frame.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
					frame.addWindowListener(new WindowAdapter(){
						public void windowClosing(WindowEvent e){
							load.setText("Aborting...");
							StreamDownloader.interrupt();
						}
					});
					load.setText("Loading User Data...");
					if(!API_UserData.isLoaded())
						API_UserData.loadUserData();
					load.setText("Loading Files...");
					if(API_File.getAllCached()==null)
						API_File.getAllFinished();
					load.setText("Loading Folders...");
					if(API_Label.getAllCached()==null){
						API_Label.getAll();
						load.setText("Folder Manager Initializing...");
						APIFolderManager.init(API_Label.root());
					}
					load.setText("Launching...");
				}
			}catch(Exception e){
				frame.dispose();
				if(load.getText().equals("Aborting...")){
					throw new RuntimeException("Aborted By User");
				}
				throw new RuntimeException(e);
			}
			
			mainWin();
			frame.dispose();
			
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
		mWin = new MainWindow();
		mWin.setLocationRelativeTo(lWin);
		mWin.setVisible(true);
	}

	public static void trayRun() {
		if (fIcon == null)
			fIcon = new FurkTrayIcon();
		else
			trayAlert(TRAY_INFO, "Minimized",
					"I'm Here,\nMinimized To The Tray", null);
	}

	public static void downloader(boolean b) {
		if (dwm == null) {
			if (!b)
				return;
			dwm = new DownloadManager();
			dwm.setLocationRelativeTo(mWin);
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

					//FurkFileView.disposeAll();

					if (dwm != null)
						DownloadManager.persist();
					downloader(false);

					mWin.setEnabled(false);
					APIBridge.userLogout();
					mWin.dispose();
					mWin = null;

					System.gc();
					login();
				} catch (Exception e) {
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

	public static MainWindow getMainWindow() {
		return mWin;
	}

}
