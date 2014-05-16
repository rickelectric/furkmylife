package rickelectric.furkmanager.views.iconutil;

import java.util.ArrayList;

import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.views.icons.FileIcon;


public class IconManager {
	
	private static IconArray finished,deleted;
	
	public static void init(){
		finished=new IconArray();
		deleted=new IconArray();
		loadFiles();
	}
	
	public static IconArray getFinished(){
		return finished;
	}
	
	public static IconArray getDeleted(){
		return deleted;
	}
	
	private static void loadFiles(){
		ArrayList<FurkFile> files=API_File.getAllCached();
		if(files==null) return;
		finished.clear();
		for(FurkFile o:files){
			FileIcon i=new FileIcon(o);
			finished.add(i);
		}
	}
	
}
