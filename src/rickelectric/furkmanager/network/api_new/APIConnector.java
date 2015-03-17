package rickelectric.furkmanager.network.api_new;

import java.util.ArrayList;

import org.apache.http.entity.mime.MultipartEntity;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.exception.LoginException;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.LoginModel;
import rickelectric.furkmanager.network.RequestCache;
import rickelectric.furkmanager.network.StreamDownloader;

public class APIConnector {

	public static final String API_BASE = "https://www.furk.net/api";

	private static APIConnector thisInstance;

	public static synchronized APIConnector getInstance() {
		if (thisInstance == null)
			thisInstance = new APIConnector();
		return thisInstance;
	}

	private StreamDownloader sInst;

	public static void main(String... args) {
		FurkManager.init();
		LoginModel lm = new LoginModel(
				"5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		try {
			boolean logged = FurkAPI.getInstance().login(lm);
			System.out.println("Logged In: " + logged);
			if (logged) {
				ArrayList<FurkFile> files = FurkAPI.getInstance().file()
						.getAllFinished();
				System.out.println("\nFILES:::Count=" + files.size());
				for (FurkFile f : files) {
					System.out.println(f.getName() + " [size=\""
							+ f.getSizeString() + "\", infoHash="
							+ f.getInfoHash() + "]");
				}
			}

			if (logged) {
				ArrayList<FurkFile> files = FurkAPI.getInstance().file()
						.getAllFinished();
				System.out.println("\nFILES:::Count=" + files.size());
				for (FurkFile f : files) {
					System.out.println(f.getName() + " [size=\""
							+ f.getSizeString() + "\", infoHash="
							+ f.getInfoHash() + "]");
				}
			}
		} catch (LoginException e) {
			e.printStackTrace();
		}

	}

	private APIConnector() {
		sInst = StreamDownloader.getInstance();
	}

	public synchronized String jsonGet(String url, boolean cacheCheck,
			boolean perm) {
		url = API_BASE + url;
		try {
			String s = sInst.getStringStream(url);
			if (s != null)
				RequestCache.APIR.add(url, s, perm);
			return s;
		} catch (Exception e) {
			// e.printStackTrace();
			throw new RuntimeException("Connection Error");
		}
	}

	public synchronized String jsonPost(String url, boolean cacheCheck,
			boolean perm) {
		url = API_BASE + url;
		try {
			String s = sInst.postStringStream(url, 512);
			if (s != null)
				RequestCache.APIR.add(url, s, perm);
			return s;
		} catch (Exception e) {
			// e.printStackTrace();
			throw new RuntimeException("Connection Error");
		}
	}

	/**
	 * Posts the parameters to the specified API Sub-URL and return the JSON
	 * result.
	 * 
	 * @param url
	 *            Furk API Sub-URL
	 * @param parts
	 *            API call parameters wrapped in a MultipartEntity
	 * @return
	 */
	public String jsonPost(String url, MultipartEntity parts) {
		url = API_BASE + url;
		try {
			String s = sInst.postMultipartStream(url, parts);
			return s;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
