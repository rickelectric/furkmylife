package rickelectric.furkmanager.data;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.IndexPair;
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.network.api.API_File;

public class DataToJSON {
	
	public static String getFiles(int limit, int offset){
		JSONObject root=new JSONObject();
		if(limit==0){
			root.put("status", "error");
			root.put("error", "Limit is Zero");
		}
		IndexPair[] files = API_File.getFileIDs(API_File.FINISHED);
		if (files == null){
			root.put("status", "error");
			root.put("error", "Error Obtaining Files.");
			return root.toString();
		}
		if (offset>=files.length){
			root.put("status", "error");
			root.put("error", "Incrrect Offset");
			return root.toString();
		}
		JSONArray file=new JSONArray();
		for(int i=offset;i<offset+limit;i++){
			if(i>=files.length) break;
			FurkFile ffile=API_File.getFile(API_File.FINISHED, files[i].getKey());
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
		root.put("num_files", files.length);
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
		root.put("status", "ok");
		root.put("num_results", file.length());
		root.put("results", file);
		return root.toString();
	}

}
