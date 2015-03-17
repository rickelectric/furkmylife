package rickelectric.furkmanager.models;

import java.awt.Color;
import java.util.Observable;

import rickelectric.UtilBox;

public class APIObject extends Observable{
	
	private String 
		name,
		infoHash;
	
	private long size;
	
	public Color iconColor=null;
	
	public APIObject(String name,String infoHash,long size){
		this.name=name;
		this.infoHash=infoHash;
		this.size=size;
	}
	
	public void stateChanged(){
		setChanged();
		notifyObservers(this);
	}
	
	public String getName(){return name;}
	public String getInfoHash(){return infoHash;}
	
	public long getSize(){return size;}
	public String getSizeString(){
		return UtilBox.getInstance().byteSizeToString(size);
	}

	@Override
	public String toString() {
		return "APIObject [name=" + name + ",size=" + getSizeString() + ",infoHash=" + infoHash + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((infoHash == null) ? 0 : infoHash.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + (int) (size ^ (size >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof APIObject){
			APIObject aio=(APIObject)obj;
			if(aio.infoHash.equals(this.infoHash)) return true;
			return false;
		}
		//System.err.println("Expected an APIObject");
		return false;
	}

}
