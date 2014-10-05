package rickelectric.furkmanager.network;

import org.apache.commons.httpclient.methods.multipart.Part;

import rickelectric.furkmanager.utils.SettingsManager;

public class FurkBridge {

	public static String key() {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.key()
				: APIBridge.key();
	}

	public static void initialize(String api_key) {
		if(SettingsManager.useTunnel())
			TunneledAPIBridge.initialize(api_key);
		else
			APIBridge.initialize(api_key);
	}

	private static boolean overrideCache = false;
	public static boolean dummy = false;

	public static boolean overrideCache() {
		return overrideCache;
	}

	public static void overrideCache(boolean o) {
		overrideCache = o;
	}

	public static final int DL_ADD_URL = 100, DL_ADD_INFO_HASH = 101,
			DL_ADD_TORRENT = 102,

			GET_ID = 200, GET_STATUS = 201, GET_ALL = 202, GET_TYPE = 203,
			GET_TRASH = 204,

			LINK_LINK = 300, LINK_UNLINK = 301, LINK_CLEAR = 302;

	public static String dlAdd(int type, String link) {
		return SettingsManager.useTunnel() ? TunneledAPIBridge
				.dlAdd(type, link) : APIBridge.dlAdd(type, link);

	}
	
	public static String dlGet(int get, String param, int[] lim_ofs,
			boolean checkCache) {

		return SettingsManager.useTunnel() ? TunneledAPIBridge.dlGet(get,
				param, lim_ofs, checkCache) : APIBridge.dlGet(get, param,
				lim_ofs, checkCache);
	}

	public static String fileInfo(String info_hash) {
		return SettingsManager.useTunnel() ? TunneledAPIBridge
				.fileInfo(info_hash) : APIBridge.fileInfo(info_hash);
	}

	public static String TFileInfo(String fileID) {
		return SettingsManager.useTunnel() ? TunneledAPIBridge
				.TFileInfo(fileID) : APIBridge.TFileInfo(fileID);
	}

	public static String fileGet(int get, String param, int[] lim_ofs,
			String[] sort, boolean checkCache) {
		return SettingsManager.useTunnel() ? TunneledAPIBridge.fileGet(get,
				param, lim_ofs, sort, checkCache) : APIBridge.fileGet(get,
				param, lim_ofs, sort, checkCache);
	}

	public static String dlUnlink(String[] ids) {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.dlUnlink(ids)
				: APIBridge.dlUnlink(ids);
	}

	public static String fileLinkstate(int action, String[] ids) {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.fileLinkstate(action, ids)
				: APIBridge.fileLinkstate(action, ids);
	}

	public static String msgGet(boolean checkCache) {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.msgGet(checkCache)
				: APIBridge.msgGet(checkCache);
	}

	public static String labelGet(boolean checkCache) {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.labelGet(checkCache)
				: APIBridge.labelGet(checkCache);
	}

	public static String labelAdd(String name) {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.labelAdd(name)
				: APIBridge.labelAdd(name);
	}

	public static String labelSave(String id, String name, String color,
			String sorder, String id_labels) {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.labelSave(id, name, color, sorder, id_labels)
				: APIBridge.labelSave(id, name, color, sorder, id_labels);
	}

	public static String userLogin(String username, String password) {
		if(SettingsManager.useTunnel())
			throw new RuntimeException(
				"Cannot User Login Over The Tunnel. Please Use API Login Instead.");
		return APIBridge.userLogin(username, password);
	}

	public static String userLoad() {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.userLoad()
				: APIBridge.userLoad();
	}

	public static boolean userLogout() {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.userLogout()
				: APIBridge.userLogout();
	}
	
	public static void logout(){
		if(SettingsManager.useTunnel()) 
			TunneledAPIBridge.logout();
		else APIBridge.logout();
	}

	public static String searchFiles(String txt, boolean checkCache) {
		return furkSearch(0, txt, checkCache);
	}

	public static String searchFurk(String txt, boolean checkCache) {
		return furkSearch(1, txt, checkCache);
	}

	public static String searchWeb(String txt, boolean checkCache) {
		return furkSearch(2, txt, checkCache);
	}

	private static String furkSearch(int sMode, String txt, boolean checkCache) {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.furkSearch(sMode, txt, checkCache)
				: APIBridge.furkSearch(sMode, txt, checkCache);
	}

	public static boolean ping() {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.ping()
				: APIBridge.ping();
	}

	public static boolean ping(String apiKey) {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.ping(apiKey)
				: APIBridge.ping(apiKey);
	}

	public static String jsonGet(String dest, boolean cacheCheck, boolean perm) {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.jsonGet(dest, cacheCheck, perm)
				: APIBridge.jsonGet(dest, cacheCheck, perm);
	}

	public static String jsonPost(String dest, boolean cacheCheck, boolean perm) {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.jsonPost(dest, cacheCheck, perm)
				: APIBridge.jsonPost(dest, cacheCheck, perm);
	}

	public static String jsonPost(String url, Part[] parts) {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.jsonPost(url, parts)
				: APIBridge.jsonPost(url, parts);
	}

	public static String getKey() {
		return SettingsManager.useTunnel() 
				? TunneledAPIBridge.getKey()
				: APIBridge.getKey();
	}

}
