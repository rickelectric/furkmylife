package rickelectric.furkmanager.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SettingsManager implements Serializable {
	private static final long serialVersionUID = 1L;
	
	private static float version=1.3f;

	private static File sFile;
	private static SettingsManager sMan;
	
	private int localPort=33250, webSockPort=33251, timeout = 20000;
	private String apiKey="", username="", password="";
	
	private String proxyHost="",proxyPort="",proxyUser="",proxyPassword="";
	private boolean proxyRequired=false;
	
	private String downloadFolder;
	private boolean askFolderOnDownload;

	private int numCachedImages=50;
	private int searchResultsPerPage=20;
	
	private int downloadBuffer=1024;

	private boolean autoLogin=false;

	private boolean idm;
	private String idmPath;

	public static void init() {
		sFile = new File("settings.db");
		if (!sFile.exists()) {
			try {
				sFile.createNewFile();
				sMan = new SettingsManager();
				save();
			} catch (IOException e) {}
		}
		else{
			sMan=load();
		}
	}
	
	private static SettingsManager load(){
		try{
			FileInputStream i=new FileInputStream(sFile);
			ObjectInputStream ois=new ObjectInputStream(i);
			float version=ois.readFloat();
			if(version==SettingsManager.version){
				SettingsManager s=(SettingsManager)ois.readObject();
				ois.close();
				i.close();
				return s;
			}
			else{
				SettingsManager s=new SettingsManager();
				ois.close();
				return s;
			}
		}catch(Exception e){
			SettingsManager s=new SettingsManager();
			return s;
		}
	}

	public static boolean save() {
		try {
			FileOutputStream o = new FileOutputStream(sFile);
			ObjectOutputStream oos=new ObjectOutputStream(o);
			oos.writeFloat(version);
			oos.writeObject(sMan);
			oos.close();
			return true;
		} catch (Exception e) {return false;}
	}
	
	public SettingsManager(){
		localPort=33250;
		webSockPort=33251;
		numCachedImages=50;
		searchResultsPerPage=20;
		timeout=20000;
		downloadBuffer=1024;
		
		idmPath=checkPaths();
		idm=true;
		if(idmPath==null) idm=false;
		
		proxyRequired=false;
		proxyHost="";
		proxyPort="";
		proxyUser="";
		proxyPassword="";
		
		askFolderOnDownload=true;
		downloadFolder=System.getProperty("user.home");
		
		autoLogin=false;
		apiKey="";
		username="";
		password="";
	}
	
	private String checkPaths() {
		String[] paths={
			"C:\\Program Files\\Internet Download Manager\\IDMan.exe",
			"C:\\Program Files (x86)\\Internet Download Manager\\IDMan.exe",
		};
		for(String s:paths){
			File f=new File(s);
			if(f.exists()) return s;
		}
		return null;
	}

	public static int timeout(){
		return sMan.timeout;
	}
	
	public static void timeout(int t){
		sMan.timeout=t;
	}
	
	public static int numCachedImages(){
		return sMan.numCachedImages;
	}
	
	public static void numCachedImages(int num){
		sMan.numCachedImages=num;
	}

	public static int getLocalPort() {
		return sMan.localPort;
	}

	public static int getWebSockPort() {
		return sMan.webSockPort;
	}

	public static void setPorts(int local, int webSock) {
		sMan.localPort = local;
		sMan.webSockPort = webSock;
	}

	public static String getApiKey() {
		return sMan.apiKey;
	}

	public static void setApiKey(String apiKey) {
		sMan.apiKey = apiKey;
	}

	public static String getUsername() {
		return sMan.username;
	}

	public static String getPassword() {
		return sMan.password;
	}

	public static void setLogin(String username, String password) {
		sMan.username = username;
		sMan.password = password;
	}
	
	public static boolean autoLogin(){
		return sMan.autoLogin;
	}
	
	public static void autoLogin(boolean al){
		sMan.autoLogin=al;
	}

	public static void searchResultsPerPage(int val){
		sMan.searchResultsPerPage=val;
	}
	
	public static int searchResultsPerPage(){
		return sMan.searchResultsPerPage;
	}
	
	public static void enableProxy(boolean on){
		sMan.proxyRequired=on;
	}
	
	public static boolean proxyEnabled(){
		return sMan.proxyRequired;
	}

	public static void setProxy(String host,String port, String user, String pass){
		sMan.proxyHost = host;
		sMan.proxyPort=port;
		sMan.proxyUser=user;
		sMan.proxyPassword=pass;
	}
	
	public static String getProxyHost() {
		return sMan.proxyHost;
	}

	public static String getProxyPort() {
		return sMan.proxyPort;
	}

	public static String getProxyUser() {
		return sMan.proxyUser;
	}

	public static String getProxyPassword() {
		return sMan.proxyPassword;
	}

	public static String getDownloadFolder() {
		return sMan.downloadFolder;
	}

	public static void setDownloadFolder(String downloadFolder) {
		sMan.downloadFolder = downloadFolder;
	}

	public static boolean askFolderOnDownload() {
		return sMan.askFolderOnDownload;
	}

	public static void askFolderOnDownload(boolean askOnDownload) {
		sMan.askFolderOnDownload = askOnDownload;
	}

	public static int downloadBuffer() {
		return sMan.downloadBuffer;
	}

	public static void downloadBuffer(int downloadBuffer) {
		sMan.downloadBuffer = downloadBuffer;
	}

	public static boolean idm(){
		return sMan.idm;
	}
	
	public static String idmPath(){
		return sMan.idmPath;
	}
	
	public static void idmPath(String s){
		if(s==null) sMan.idm=false;
		else sMan.idm=true;
		sMan.idmPath=s;
	}

}
