package rickelectric.furkmanager.beta;

import rickelectric.furkmanager.network.API;
import rickelectric.furkmanager.network.APIFolderManager;
import rickelectric.furkmanager.network.RequestCache;
import rickelectric.furkmanager.utils.SettingsManager;

public class LabelFix {

	public static void main(String[] args){
		SettingsManager.init();
		RequestCache.init();
		API.init("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		
		API.Label.getAll();
		APIFolderManager.init(API.Label.root());
		
	}

}
