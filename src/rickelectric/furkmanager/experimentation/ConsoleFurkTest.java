package rickelectric.furkmanager.beta;

import rickelectric.furkmanager.data.DefaultParams;
import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.network.RequestCache;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.utils.UtilBox;

public class ConsoleFurkTest {

	public static void main(String[] args) {
		DefaultParams.init();
		ThreadPool.init();
		SettingsManager.getInstance();

		UtilBox.init();
		RequestCache.init();
		
		APIBridge.initialize("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		String json = APIBridge.dlAdd(APIBridge.DL_ADD_TORRENT,UtilBox.openFile("Torrent"));
		System.out.println(json);
	}
}
