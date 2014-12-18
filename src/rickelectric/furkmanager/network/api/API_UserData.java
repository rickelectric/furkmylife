package rickelectric.furkmanager.network.api;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.UtilBox;
import rickelectric.furkmanager.models.FurkUserData;
import rickelectric.furkmanager.models.LoginModel;
import rickelectric.furkmanager.models.URI_Enums;
import rickelectric.furkmanager.models.URI_Enums.Prefs_Flags;
import rickelectric.furkmanager.network.FurkBridge;
import rickelectric.furkmanager.utils.SettingsManager;

public class API_UserData extends API {

	private static LoginModel currentLogin = null;
	private static boolean isLoaded = false;

	public static void loadUserData() {
		String json = null;
		int numFails = 0;
		while (json == null)
			try {
				json = FurkBridge.userLoad();
			} catch (RuntimeException e) {
				numFails++;
				if (numFails > 5)
					throw e;
			}

		JSONObject user = new JSONObject(json);
		if (user.getString("status").equals("error"))
			throw new RuntimeException("Error Obtaining User Data");

		Object usage = user.get("dls_usage");
		if (usage != null && usage != JSONObject.NULL) {
			FurkUserData.DownloadsUsage.load((JSONObject) usage);
		}

		JSONObject juser = user.getJSONObject("user");
		FurkUserData.User.load(juser);

		JSONArray bwStats = user.getJSONArray("bw_stats");
		JSONArray subnet = user.getJSONArray("net_stats");
		FurkUserData.BandwidthStats.load(bwStats, subnet);

		isLoaded = true;
	}

	/**
	 * Should Only Be Called From Within The <i>FurkUserData</i> class, and when
	 * all enum values are updated.
	 */
	public static void saveUserPrefs() {
		Prefs_Flags flag = FurkUserData.User.flags;
		String prefs_flags = "prefs_flags="
				+ URI_Enums.Prefs_Flags.getValue(flag);

		String prefs_json = "prefs_json=" + FurkUserData.User.jsonPreferences;
		String dl_uri_scheme = "dl_uri_scheme="
				+ URI_Enums.URI_Scheme.getValue(FurkUserData.User.dlScheme);
		String dl_uri_host = "dl_uri_host="
				+ URI_Enums.URI_Host.getValue(FurkUserData.User.dlHost);
		String dl_uri_port = "dl_uri_port="
				+ URI_Enums.URI_Port.getValue(FurkUserData.User.dlPort);
		String dl_uri_key = "dl_uri_key=" + FurkUserData.User.uriKey;

		String saveString = "";
		saveString += "&" + prefs_flags;
		saveString += "&" + prefs_json;
		saveString += "&" + dl_uri_scheme;
		saveString += "&" + dl_uri_host;
		saveString += "&" + dl_uri_port;
		saveString += "&" + dl_uri_key;
		if (SettingsManager.getInstance().useTunnel()) {
			saveString = FurkBridge.API_BASE()
					+ "/?object=account&function=save_prefs&api_key="
					+ FurkBridge.key() + "&pretty=1" + saveString;
		} else {
			saveString = FurkBridge.API_BASE() + "/account/save_prefs?"
					+ FurkBridge.key() + "&pretty=1" + saveString;
		}

		FurkBridge.jsonPost(saveString, false, false);
	}

	public static boolean isLoaded() {
		return isLoaded;
	}

	public static boolean login(LoginModel login) {
		boolean isValid = false;
		switch (login.mode()) {
		case LoginModel.APIKEY:
			isValid = FurkBridge.ping(login.apiKey());
			if (isValid) {
				API.init(login.apiKey());
			}
			break;
		case LoginModel.CREDENTIALS:
			String key = FurkBridge.userLogin(login.username(),
					UtilBox.getInstance().charToString(login.password()));
			isValid = (key!=null);
			if (isValid) {
				API.init(key);
				login.setApiKey(key);
			}
			break;
		case LoginModel.BLANK:
			return false;
		default:
			return false;
		}
		if (isValid) {
			if (login.save()) {
				SettingsManager.getInstance().loginModel(login);
				currentLogin = login;
				SettingsManager.save();
			}
			return true;
		}
		return false;
	}

	public static LoginModel currentLoginModel() {
		return currentLogin;
	}

	public static void flush() {
		logout();
		currentLogin = null;
		isLoaded = false;
	}

	private static void logout() {
		FurkBridge.userLogout();
	}

}
