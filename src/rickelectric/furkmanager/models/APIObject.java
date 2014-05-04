package rickelectric.furkmanager.models;

import java.awt.Color;

public class APIObject {
	
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
	
	public String getName(){return name;}
	public String getInfoHash(){return infoHash;}
	
	public long getSize(){return size;}
	public String getSizeString(){
		float sz=size;
		String[] reps=new String[]{"bytes","kB","MB","GB","TB"};
		int i=0;
		while(sz>1024&&i<=4){
			sz=sz/1024f;
			i++;
		}
		String[] szs=(""+sz).split("\\.");
		if(szs[1].length()>2) szs[1]=szs[1].substring(0, 2);
		return szs[0]+"."+szs[1]+" "+reps[i];
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
