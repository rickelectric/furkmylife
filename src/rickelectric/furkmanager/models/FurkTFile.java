package rickelectric.furkmanager.models;

public class FurkTFile{
	
	private String name,urlDl,urlPls,
		path,contentType,player,
		thumbURL,length,bitrate;
	
	private int width,height,thumbHeight;
	
	private long size;

	public FurkTFile(String name, long size, String urlDl, 
			String urlPls, String path,String contentType) {
		this(name, size,urlDl, urlPls);
		this.path=path;
		this.contentType=contentType;
	}
	
	private FurkTFile(String name,long size, String urlDl,String urlPls){
		this.name=name;
		this.size=size;
		this.urlDl=urlDl;
		this.urlPls=urlPls;
	}
	
	public void mediaInfo(String player,String thumbURL,int width,int height,int thumbHeight){
		this.player=player;
		this.thumbURL=thumbURL;
		this.width=width;
		this.height=height;
		this.thumbHeight=thumbHeight;
		
	}

	public String getName() {return name;}
	
	public String getUrlDl(){return urlDl;}
	public String getUrlPls(){return urlPls;}
	
	public long getSize() {return size;}
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

	public String getContentType(){return contentType;}
	public void setContentType(String contentType){this.contentType=contentType;}

	public String getLength(){return length;}
	public void setLength(String length){this.length=length;}
	
	public String getBitrate(){return bitrate;}
	public void setBitrate(String bitrate){this.bitrate=bitrate;}

	public String getPath(){return path;}

	public String getPlayer(){return player;}

	public String getThumbURL(){return thumbURL;}

	public int getWidth(){return width;}

	public int getHeight(){return height;}

	public int getThumbHeight(){return thumbHeight;}

	@Override
	public String toString() {
		return super.toString()+" ==> FurkTFile ["
				+ (path != null ? "path=" + path + ", " : "")
				+ (contentType != null ? "contentType=" + contentType + ", "
						: "")
				+ (player != null ? "player=" + player + ", " : "")
				+ (thumbURL != null ? "thumbURL=" + thumbURL + ", " : "")
				+ (length != null ? "length=" + length + ", " : "")
				+ (bitrate != null ? "bitrate=" + bitrate + ", " : "")
				+ "width=" + width + ", height=" + height + ", thumbHeight="
				+ thumbHeight + "]";
	}

}
