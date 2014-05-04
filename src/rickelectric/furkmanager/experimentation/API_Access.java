package rickelectric.furkmanager.experimentation;

import java.io.BufferedReader;
import java.io.FileReader;

import org.json.JSONObject;

import rickelectric.furkmanager.utils.Downloader;

public class API_Access {

	private static String api_key;
	private static String key(){return "api_key="+api_key;}
	
	private static final String api_base="https://www.furk.net/api";
	
	public static void initialize(String api_key){
		API_Access.api_key=api_key;
	}
	
	public static final int
		ADD_URL=100,
		ADD_INFO_HASH=101,
		ADD_FILE=102,
		
		GET_ID=200,
		GET_STATUS=201,
		GET_ALL=202,
		GET_TYPE=203,
		GET_TRASH=204,
		
		LINK_LINK=300,
		LINK_UNLINK=301;
	
	public static String dlAdd(int type,String url){
		String dest=api_base+"/dl/add?"+key();
		if(type==ADD_URL) dest+="&url=";
		else if(type==ADD_INFO_HASH) dest+="&info_hash=";
		else if(type==ADD_FILE){
			//Multipart...
			throw new RuntimeException("File Type Additions Not Implemented");
		}
		else throw new RuntimeException("Invalid Type Parameter");
		dest+=url;
		try{
			String s=Downloader.getStringStream(dest,8);
			return s;
		}catch(Exception e){
			return null;
		}
	}
	
	/**
	 * 
	 * @param get Get a file by ID(<b>API_Access.GET_ID</b>), Status(<b>API_Access.GET_STATUS</b>) or Neither of the Aforementioned(<b>API_Access.GET_ALL</b>)
	 * @param param Pass the ID of the file (if <b>GET_ID</b> selected), Status "active" or "failed" (if <b>GET_STATUS</b> selected) or null (if <b>GET_ALL</b> is selected)
	 * @param lim_ofs An array with the limit and offset as the 1st and 2nd items respectively (For Multi-page results). Leave null if all items are to be returned.
	 * @return A JSON String of results returned by Furk.net
	 */
	public static String dlGet(int get,String param,int[] lim_ofs){
		String dest=api_base+"/dl/get?pretty=1&"+key();
		if(get==GET_ID) dest+="&id="+param;
		else if(get==GET_STATUS) dest+="&dl_status="+param;
		else if(get==GET_ALL);
		else throw new RuntimeException("Invalid GET Method");
		
		if(lim_ofs!=null&&lim_ofs.length==2){
			if(lim_ofs[0]<1) throw new RuntimeException("Invalid Limit");
			if(lim_ofs[1]<0) throw new RuntimeException("Invalid Offset");
			dest+="&limit="+lim_ofs[0]+"&offset="+lim_ofs[1];
		}
		try{
			String s=Downloader.getStringStream(dest,8);
			return s;
		}catch(Exception e){
			return null;
		}
	}
	
	public static String fileInfo(String info_hash){
		if(info_hash==null||info_hash.length()==0) return null;
		String dest=api_base+"/file/info?pretty=1&"+key()+"&info_hash="+info_hash;
		
		try{
			String s=Downloader.getStringStream(dest,8);
			return s;
		}catch(Exception e){
			return null;
		}
	}
	
	public static String fileGet(int get,String param,int[] lim_ofs,String[] sort){
		//TODO Test This
		String dest=api_base+"/file/get?pretty=1&"+key();
		if(get==GET_ID) dest+="&id="+param;
		else if(get==GET_TYPE) dest+="&type="+param;
		else if(get==GET_TRASH) dest+="&unlinked=1";
		else if(get==GET_ALL);
		else throw new RuntimeException("Invalid GET Method");
		
		if(lim_ofs!=null&&lim_ofs.length==2){
			if(lim_ofs[0]<1) throw new RuntimeException("Invalid Limit <-- >=1 -->");
			if(lim_ofs[1]<0) throw new RuntimeException("Invalid Offset <-- >=0 -->");
			dest+="&limit="+lim_ofs[0]+"&offset="+lim_ofs[1];
		}
		if(sort!=null&&sort.length==2){
			if(!(sort[0].equals("name")||sort[0].equals("size")||sort[0].equals("ctime"))) 
				throw new RuntimeException("Invalid Column <-- name / size / ctime -->");
			if(!(sort[1].equals("asc")||sort[1].equals("desc"))) 
					throw new RuntimeException("Invalid Sort Method <-- asc / desc -->");
			dest+="&sort_col="+sort[0]+"&sort_type="+sort[1];
		}
		return jsonGet(dest);
	}
	
	public static String dlUnlink(String[] ids){
		if(ids.length==0) return null;
		String destdl=api_base+"/dl/unlink";
		String jsil="";
		for(int i=0;i<ids.length;i++){
			jsil+="id="+ids[i];
			if(i!=ids.length-1) jsil+="&"; 
		}
		destdl+="?"+key()+"&"+jsil;
		return jsonGet(destdl);
	}
	
	public static String fileLinkstate(int action,String[] ids){
		String destfile=api_base+"/file/";
		if(action==LINK_LINK) destfile+="link";
		else if(action==LINK_UNLINK) destfile+="unlink";
		else throw new RuntimeException("Invalid Link Method");
		String jsil="";
		for(int i=0;i<ids.length;i++){
			jsil+="id="+ids[i];
			if(i!=ids.length-1) jsil+="&"; 
		}
		destfile+="?"+key()+"&"+jsil;
		return jsonGet(destfile);
	}

	//Sets the api key and returns true if logged in sucessfully
	public static boolean userLogin(String username,String password){
		if(api_key!=null) throw new RuntimeException("Already Logged In. Log Out First.");
		String url="http://www.furk.net/api/login/login?login="+username+"&pwd="+password;
		try{
			String json=jsonGet(url);
			JSONObject j=new JSONObject(json);
			if(j.getString("status").equals("error")) return false;
			api_key=j.getString("api_key");
			return true;
		}catch(Exception e){
			throw new RuntimeException("Connection Error");
		}
	}
	
	public static boolean userLogout(){
		if(api_key==null) throw new RuntimeException("Not Logged In.");
		String url="http://www.furk.net/api/login/logout";
		try{
			String json=jsonGet(url);
			JSONObject j=new JSONObject(json);
			if(j.getString("status").equals("error")) return false;
			api_key=null;
			return true;
		}catch(Exception e){
			throw new RuntimeException("Connection Error");
		}
	}
	
	public static String search(String txt){
		return furkSearch(true,txt);
	}
	
	public static String externalSearch(String txt){
		return furkSearch(false,txt);
	}
	
	private static String furkSearch(boolean intOnly,String txt){
		if(txt.length()==0) return null;
		String frag="";
		if(intOnly) frag="search";
		else frag="plugins/metasearch";
		try{
			String url="https://www.furk.net/api/"+frag+"?pretty=1&"+key();
			txt=java.net.URLEncoder.encode(txt,"utf-8");
			url+="&q="+txt;
			return jsonGet(url);
		}catch(Exception e){return null;}
		
	}
	
	public static boolean ping(){
		return ping(api_key);
	}
	
	public static boolean ping(String apiKey){
		String url="https://www.furk.net/api/ping?api_key="+apiKey;
		String stat=jsonGet(url);
		JSONObject j=new JSONObject(stat);
		String s=j.getString("status");
		if(s.equals("ok")) return true;
		else return false;
	}
	
	private static String jsonGet(String dest){
		try{
			Math.abs(2/0);//TODO divide by 0 to invoke exception
			String s=Downloader.postStringStream(dest,4);
			//System.out.println(s);
			return s;
		}catch(Exception e){
			//e.printStackTrace();
			//return null;
			//Return Dummy JSON Result From File
			
			try{
				FileReader f=new FileReader("JSON-FurkFile.txt");
				BufferedReader b=new BufferedReader(f);
				String l="";
				String s=b.readLine();
				while(s!=null){
					l=l+s;
					s=b.readLine();
				}
				b.close();
				f.close();
				return l;
			}catch(Exception e1){return null;}
			//*/
		}
	}
	
	/**
	 * For the File Upload, Multipart Will Add To The Post Request.
	 */
	/*private static void multipart(){
		MultipartEntity entity = new MultipartEntity();
	    entity.addPart("file", new FileBody(file));

	    HttpPost request = new HttpPost(url);
	    request.setEntity(entity);

	    HttpClient client = new DefaultHttpClient();
	    HttpResponse response = client.execute(request);
	}*/

}
