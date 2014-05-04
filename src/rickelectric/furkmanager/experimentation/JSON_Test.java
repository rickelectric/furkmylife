import java.util.ArrayList;

/*import java.io.*;
import java.util.*;

import org.json.JSONArray;
import org.json.JSONObject;

*/
public class JSON_Test {

	public static void main(String[] args){
		
		API.init("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		ArrayList<FurkDownload> fdl=API.Download.getAll(API.Download.STATUS_ALL);
		
		for(FurkDownload d:fdl){
			System.out.println(d);
		}
		
		/*FileReader f=new FileReader("JSON-FurkFile.txt");
		BufferedReader b=new BufferedReader(f);
		String l="";
		String s=b.readLine();
		while(s!=null){
			l=l+s;
			s=b.readLine();
		}
		b.close();
		f.close();
		*/
		
		//Start The API Interface With My API Key
		/*
		String l=API_Access.fileGet(API_Access.GET_TYPE, "video",null,new String[]{"ctime","asc"});
		
		JSONObject j=new JSONObject(l);
		String status=j.getString("status");
		if(status.equals("error")){
			throw new RuntimeException("Error!");
		}
		JSONArray files=j.getJSONArray("files");
		
		ArrayList<FurkFile> f=FurkFile.jsonFiles(files);
		for(FurkFile i:f){
			//
			new FurkFileView(i);
		}
		
		/*
		JSONObject j=new JSONObject(l);
		String status=j.getString("status");
		if(status.equals("error")){
			throw new RuntimeException("Error!");
		}
		
		Object o=j.get("files");
		JSONArray a=(JSONArray)o;
		for(int i=0;i<a.length();i++){
			JSONObject x=(JSONObject)a.get(i);
			String id="",name="",info_hash="",type="";
			Object tid=x.get("id");
			if(tid!=JSONObject.NULL){
				@SuppressWarnings("unchecked")
				Iterator<String> keys=x.keys();
				while(keys.hasNext()){
					String t=keys.next();
					if(t.equals("id")) id=(String)x.get(t);
					if(t.equals("name")) name=(String)x.get(t);
					if(t.equals("info_hash")) info_hash=(String)x.get(t);
					if(t.equals("type")) type=(String)x.get(t);
					//if(t.equals("id")||t.equals("name")||t.equals("url_page")||t.equals("info_hash")||
					//   t.equals("url_dl")||t.equals("type")||t.equals("size")||t.equals("ctime"))
					//System.out.println(t+": "+x.get(t));
				}
				FurkItem fi=new FurkItem(id,name,info_hash,type);
				System.out.println(fi);
			}
			System.out.println("------------------------------------------------------------------------------------------");
		}*/
	}
	
	
}
