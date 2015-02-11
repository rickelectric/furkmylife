package rickelectric.furkmanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import rickelectric.furkmanager.models.LoginModel;
import rickelectric.furkmanager.network.FurkBridge;

public class SettingsManager implements Serializable {
	private static final long serialVersionUID = 1L;

	private static float version = 1.4f;
	private static SettingsManager sMan = null;

	private static File sFile = null;

	public static synchronized SettingsManager getInstance() {
		if (sMan == null) {
			init();
		}
		return sMan;
	}

	public static final int ENV_MODE = 0, WIN_MODE = 1;

	private int mainWinMode = 0;

	private int localPort = 33250, webSockPort = 33251, timeout = 20000;

	private ProxySettings proxySettings;

	private boolean useTunnel = false;

	private String downloadFolder;
	private boolean askFolderOnDownload;

	private int numCachedImages = 50;
	private int searchResultsPerPage = 20;

	private int downloadBuffer = 1024;

	private boolean idm;
	private String idmPath;

	private LoginModel loginModel;

	private boolean dimEnvironment = true;

	private boolean autoHideEnvrionment = true;

	private boolean slideDashWin = true;

	private SettingsManager() {
		localPort = 33250;
		webSockPort = 33251;
		numCachedImages = 50;
		searchResultsPerPage = 20;
		timeout = 20000;
		downloadBuffer = 1024;

		mainWinMode = ENV_MODE;
		dimEnvironment = true;
		autoHideEnvrionment = true;

		idmPath = checkPaths();
		idm = true;
		if (idmPath == null)
			idm = false;

		proxySettings = new ProxySettings();

		askFolderOnDownload = true;
		downloadFolder = System.getProperty("user.home") + "/FurkDownloads";

		loginModel = null;

		useTunnel = false;
	}

	private static void init() {
		dirSetup();
		sFile = new File("settings/app.furkmanager.settings.db");
		if (!sFile.exists()) {
			try {
				sFile.createNewFile();
				sMan = new SettingsManager();
				save();
			} catch (IOException e) {
			}
		} else {
			sMan = load();
		}
	}

	private static void dirSetup(){
		try{
			File[] folders = new File[]{
					new File("./settings/"),
					new File("./db/")
			};
			for(File f:folders)
				f.mkdir();
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private static SettingsManager load() {
		try {
			FileInputStream i = new FileInputStream(sFile);
			ObjectInputStream ois = new ObjectInputStream(i);
			float version = ois.readFloat();
			if (version == SettingsManager.version) {
				SettingsManager s = (SettingsManager) ois.readObject();
				ois.close();
				i.close();
				return s;
			} else {
				SettingsManager s = new SettingsManager();
				ois.close();
				return s;
			}
		} catch (Exception e) {
			SettingsManager s = new SettingsManager();
			save();
			return s;
		}
	}

	public static boolean save() {
		try {
			FileOutputStream o = new FileOutputStream(sFile);
			ObjectOutputStream oos = new ObjectOutputStream(o);
			oos.writeFloat(version);
			oos.writeObject(sMan);
			oos.close();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public String checkPaths() {
		String[] paths = {
				"C:\\Program Files\\Internet Download Manager\\IDMan.exe",
				"C:\\Program Files (x86)\\Internet Download Manager\\IDMan.exe", };
		for (String s : paths) {
			File f = new File(s);
			if (f.exists())
				return s;
		}
		return null;
	}

	public int timeout() {
		return this.timeout;
	}

	public void timeout(int t) {
		this.timeout = t;
	}

	public int numCachedImages() {
		return this.numCachedImages;
	}

	public void numCachedImages(int num) {
		this.numCachedImages = num;
	}

	public int getLocalPort() {
		return this.localPort;
	}

	public int getWebSockPort() {
		return this.webSockPort;
	}

	public void setPorts(int local, int webSock) {
		this.localPort = local;
		this.webSockPort = webSock;
	}

	public void searchResultsPerPage(int val) {
		this.searchResultsPerPage = val;
	}

	public int searchResultsPerPage() {
		return this.searchResultsPerPage;
	}

	public String getDownloadFolder() {
		return this.downloadFolder;
	}

	public void setDownloadFolder(String downloadFolder) {
		this.downloadFolder = downloadFolder;
	}

	public boolean askFolderOnDownload() {
		return this.askFolderOnDownload;
	}

	public void askFolderOnDownload(boolean askOnDownload) {
		this.askFolderOnDownload = askOnDownload;
	}

	public int downloadBuffer() {
		return this.downloadBuffer;
	}

	public void downloadBuffer(int downloadBuffer) {
		this.downloadBuffer = downloadBuffer;
	}

	public boolean idm() {
		return this.idm;
	}

	public String idmPath() {
		return this.idmPath;
	}

	public void idmPath(String s) {
		if (s == null || s.equals(""))
			this.idm = false;
		else
			this.idm = true;
		this.idmPath = s;
	}

	public void loginModel(LoginModel loginModel) {
		this.loginModel = loginModel;
	}

	public LoginModel loginModel() {
		return this.loginModel;
	}

	public boolean useTunnel() {
		return this.useTunnel&&!FurkBridge.dummy();
	}

	public void useTunnel(boolean useTunnel) {
		this.useTunnel = useTunnel;
	}

	public int getMainWinMode() {
		return this.mainWinMode;
	}

	public void setMainWinMode(int mainWinMode) {
		this.mainWinMode = mainWinMode;
	}

	public boolean dimEnvironment() {
		return this.dimEnvironment;
	}

	public void dimEnvironment(boolean dimEnvironment) {
		this.dimEnvironment = dimEnvironment;
	}

	public ProxySettings getProxySettings() {
		return this.proxySettings;
	}

	public boolean autoHideEnvrionment() {
		return this.autoHideEnvrionment;
	}

	public void autoHideEnvrionment(boolean autoHide) {
		this.autoHideEnvrionment = autoHide;
	}

	public boolean slideDashWin() {
		return this.slideDashWin;
	}

	public void slideDashWin(boolean slide) {
		this.slideDashWin = slide;
	}

}
