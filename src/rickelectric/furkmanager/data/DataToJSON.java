package rickelectric.furkmanager.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.network.API;
import rickelectric.furkmanager.network.InstanceConn;
import rickelectric.furkmanager.network.RequestCache;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.utils.UtilBox;

public class DataToJSON {
	
	public static void main(String[] args) throws Exception{
		ThreadPool.init();
		SettingsManager.init();
		UtilBox.init();
		RequestCache.init();
		
		API.init("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		
		InstanceConn.main(args);
		System.out.println("Started");
		
	}
	
	public static String getFiles(int limit, int offset){
		JSONObject root=new JSONObject();
		if(limit==0){
			root.put("status", "error");
			root.put("error", "Limit is Zero");
		}
		ArrayList<APIObject> files = API.File.getAllCached();
		if(files==null) files = API.File.getAllFinished();
		if (files == null){
			root.put("status", "error");
			root.put("error", "Error Obtaining Files.");
			return root.toString();
		}
		if (offset>=files.size()){
			root.put("status", "error");
			root.put("error", "Incrrect Offset");
			return root.toString();
		}
		JSONArray file=new JSONArray();
		for(int i=offset;i<offset+limit;i++){
			if(i>=files.size()) break;
			FurkFile ffile=(FurkFile)files.get(i);
			JSONObject o=new JSONObject();
			String urlPage=ffile.getUrlPage();
			if(!urlPage.contains("furk.net")) urlPage="https://www.furk.net"+urlPage;
			o.put("url", urlPage);
			o.put("name",ffile.getName());
			o.put("size",ffile.getSizeString());
			o.put("type",ffile.getType());
			o.put("infoHash",ffile.getInfoHash());
			file.put(o);
		}
		root.put("status",JSONObject.NULL);
		root.put("status", "ok");
		root.put("num_files", files.size());
		root.put("files", file);
		return root.toString();
	}

	public static String search(String term){
		ArrayList<APIObject> files = API.search(term, API.METASEARCH);
		JSONObject root=new JSONObject();
		if (files == null){
			root.put("status", "error");
			root.put("error", "null_pointer_error");
			return root.toString();
		}
		JSONArray file=new JSONArray();
		for(APIObject ffile:files){
			JSONObject o=new JSONObject();
			if(ffile instanceof FurkFile){
				String urlPage=((FurkFile)ffile).getUrlPage();
				if(!urlPage.contains("furk.net")) urlPage="https://www.furk.net"+urlPage;
				o.put("url", urlPage);
				o.put("type",((FurkFile)ffile).getType());
			}
			else{
				o.put("url", "");
				o.put("type","");
			}
			o.put("name",ffile.getName());
			o.put("size",ffile.getSizeString());
			o.put("infoHash",ffile.getInfoHash());
			file.put(o);
		}
		root.put("status",JSONObject.NULL);
		root.put("status", "ok");
		root.put("files", file);
		return root.toString();
	}

}
