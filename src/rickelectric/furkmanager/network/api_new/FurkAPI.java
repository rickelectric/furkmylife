package rickelectric.furkmanager.network.api_new;

import rickelectric.furkmanager.exception.APIException;
import rickelectric.furkmanager.exception.LoginException;
import rickelectric.furkmanager.models.LoginModel;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.network.api.FileUpdater;

public class FurkAPI {

	private static FurkAPI thisInstance = null;

	public static synchronized FurkAPI getInstance() {
		return thisInstance == null ? thisInstance = new FurkAPI()
				: thisInstance;
	}

	private FileAPI file;
	private DownloadAPI dl;
	private LabelAPI label;
	private FeedAPI feed;
	private LoginAPI login;
	private UserAccountAPI account;
	private PluginAPI plugins;

	private FurkAPI() {
		login = new LoginAPI();
		// TODO Others created on successful login
	}
	
	public boolean login(LoginModel loginModel) throws LoginException {
		if(login.isLoggedIn())
			throw new LoginException("Already Logged In. Please Log Out First.");
		try {
			int x=1;
			//TODO Undo when complete;
			if(/*login.login(loginModel)*/true){
				API.init(loginModel.apiKey());
				FileUpdater.destroyInstance();
				account = new UserAccountAPI(loginModel.apiKey());
				file=new FileAPI(loginModel.apiKey());
				dl=new DownloadAPI(loginModel.apiKey());
				label=new LabelAPI(loginModel.apiKey());
				feed=new FeedAPI(loginModel.apiKey());
				plugins = new PluginAPI(loginModel.apiKey());
				account.start();
				file.start();
				dl.start();
				feed.start();
				plugins.start();
				return true;
			}
//		} catch (LoginException e) {
//			e.printStackTrace();
		} catch(APIException e){
			e.printStackTrace();
		}
		return false;
	}

	public FileAPI file() {
		return file;
	}

	public DownloadAPI dl() {
		return dl;
	}
	
	public LabelAPI label(){
		return label;
	}

	public FeedAPI feed() {
		return feed;
	}

	public UserAccountAPI account() {
		return account;
	}
	
	public PluginAPI plugins(){
		return plugins;
	}

}
