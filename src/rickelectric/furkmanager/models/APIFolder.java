package rickelectric.furkmanager.models;

import java.util.ArrayList;
import java.util.List;

import rickelectric.furkmanager.network.API;
import rickelectric.furkmanager.network.APIFolderManager;

public class APIFolder implements MoveableItem{
	
	private ArrayList<MoveableItem> files;
	private FurkLabel fsync;

	public APIFolder(FurkLabel label) {
		this.fsync=label;
		files=new ArrayList<MoveableItem>();
	}
	
	public ArrayList<MoveableItem> getFiles(){
		return files;
	}
	
	public void populate(){
		List<FurkLabel> ll=API.Label.childrenOf(fsync);
		for(FurkLabel l:ll){
			APIFolder tf=new APIFolder(l);
			if(APIFolderManager.register(tf)){
				tf.populate();
				addItem(tf);
			}
		}
		List<FurkFile> files=API.File.childrenOf(fsync);
		for(FurkFile f:files){
			if(APIFolderManager.register(f)){
				addItem(f);
			}
		}
	}
	
	public String getID(){
		return fsync.getID();
	}
	
	public int numItems(){
		return files.size();
	}
	
	public void addItem(MoveableItem f){
		if(!files.contains(f)) files.add(f);
	}
	
	public void removeItem(MoveableItem f){
		files.remove(f);
	}

	@Override
	public String getName(){
		return fsync.getName();
	}
	
	public void setName(String name) {
		fsync.setName(name);
	}
	
	public int compareTo(Object o) {
		if (this.equals(o)) return 0;
		if(o instanceof APIFolder){
			return getName().compareTo(((APIFolder) o).getName());
		}
		if(o instanceof String){
			return getName().compareTo(o.toString());
		}
		if(o instanceof MoveableItem){
			return getName().compareTo(((MoveableItem)o).getName());
		}
		throw new IllegalArgumentException();
	}
	
	public boolean equals(Object o) {
		if (this==o) return true;
		if (o==null) return false;
		if(o instanceof APIFolder){
			if(((APIFolder) o).getName().equals(this.getName())) return true;
		}
		if(o instanceof String){
			if(getName().equals(o.toString())) return true;
		}
		return false;
	}
	
	public String toString(){
		return toString(0);
	}
	
	private String toString(int level){
		String space="";
		for(int i=0;i<level;i++){
			space+=" ";
		}
		String s="\n"+space+"Folder: "+fsync.getName()+"\n";
		for(MoveableItem i:files){
			if(i instanceof APIFolder){
				s+=space+""+((APIFolder) i).toString(level+1);
			}
			else if(i instanceof FurkFile){
				s+=space+""+i.getName()+"\n";
			}
		}
		return s;
	}

	public FurkLabel getLabel() {
		return fsync;
	}

	public boolean contains(MoveableItem obj){
		for(MoveableItem f:files){
			if(f.getID()!=null && obj.getID()!=null)
				if(f.getID().equals(obj.getID())) return true;
		}
		return false;
	}

}
