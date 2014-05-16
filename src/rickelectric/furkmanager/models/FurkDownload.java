package rickelectric.furkmanager.models;

/**
 * Representation of a <i>dl</i> API Object. <br/>
 * (A currently downloading/failed Download to Furk.net servers) 
 * @author Rick Lewis
 * @version 1.0 
 */
public class FurkDownload extends APIObject {
	
	private int
		peers,//Number Of Peers (Integer)
		seeders,//No. Of Seeders (Integer)
		is_ready;//null if not ready
		
	
	private long 
		downSpeed,//Download Speed (Bytes per second [/1024 for kB and again for MB) : speed
		upSpeed,//Upload Speed (Bytes per second) :up_speed
		bytes,//Bytes Downloaded (Bytes,Long)
		upBytes;//Bytes Uploaded (Bytes,Long)
		
		
	private String 
		id,
		activeStatus,//"leeching","seeding","adding","starting"...
		dlStatus,//"active","finished","failed" :dl_status
		postProcStatus,//Post Processing Status (Rendering Video/Enqueuing/Taking Snapshots...) :post_proc_status
		failReason,//If failed: The error is here: fail_reason
		have,//Percentage Downloaded (Float,%.2f)
		mtime,//Date|Time (yyyy-mm-dd hh:mm:ss) The Download Status Was Updated
		dateAdded,//DateTime Added: adding_st
		dateCompleted;//DateTime Completed : finish_dt
	
	public FurkDownload(String id,String name, String infoHash, long size,
			String dateAdded){
		super(name, infoHash, size);
		this.id=id;
		this.dateAdded = dateAdded;
	}
	
	public String getId(){return id;}
	
	public void dlStatus(String active_status,int peers,int seeders,long upSpeed,long downSpeed,String havePercent){
		this.activeStatus=active_status;
		this.peers=peers;
		this.seeders=seeders;
		this.upSpeed=upSpeed;
		this.downSpeed=downSpeed;
		this.have=havePercent;
	}
	
	public void bytes(long downBytes,long upBytes){
		this.bytes=downBytes;
		this.upBytes=upBytes;
	}
	
	public long getDownBytes(){return bytes;}
	public long getUpBytes(){return upBytes;}

	public String getHave(){return have;}
	public int getPeers(){return peers;}
	public int getSeeders(){return seeders;}

	public long getDownSpeed(){return downSpeed;}
	public long getUpSpeed(){return upSpeed;}

	public String getActiveStatus(){return activeStatus;}

	public String getDlStatus() {return dlStatus;}
	public void setDlStatus(String dl_status){this.dlStatus=dl_status;}

	public String getPostProcStatus(){return postProcStatus;}
	public void setPostProcStatus(String post_proc_status){this.postProcStatus = post_proc_status;}

	public String getFailReason(){return failReason;}
	public void setFailReason(String fail_reason){this.failReason=fail_reason;}
	
	public String getMtime(){return mtime;}

	public void setMtime(String mtime){this.mtime=mtime;}

	public String getDateAdded(){return dateAdded;}
	public void setDateAdded(String adding_dt){this.dateAdded=adding_dt;}
	
	public String getDateCompleted(){return dateCompleted;}
	public void setDateCompleted(String finish_dt){this.dateCompleted=finish_dt;}

	public boolean isReady(){return is_ready==1;}
	public void setReady(boolean is_ready){this.is_ready=is_ready?1:0;}

	@Override
	public String toString() {
		return super.toString()+" ==> FurkDownload [peers="
				+ peers
				+ ", seeders="
				+ seeders
				+ ", is_ready="
				+ is_ready
				+ ", downSpeed="
				+ downSpeed
				+ ", upSpeed="
				+ upSpeed
				+ ", bytes="
				+ bytes
				+ ", upBytes="
				+ upBytes
				+ ", "
				+ (id != null ? "id=" + id + ", " : "")
				+ (activeStatus != null ? "activeStatus=" + activeStatus + ", "
						: "")
				+ (dlStatus != null ? "dlStatus=" + dlStatus + ", " : "")
				+ (postProcStatus != null ? "postProcStatus=" + postProcStatus
						+ ", " : "")
				+ (failReason != null ? "failReason=" + failReason + ", " : "")
				+ (have != null ? "have=" + have + ", " : "")
				+ (mtime != null ? "mtime=" + mtime + ", " : "")
				+ (dateAdded != null ? "dateAdded=" + dateAdded + ", " : "")
				+ (dateCompleted != null ? "dateCompleted=" + dateCompleted
						: "") + "]";
	}
	
	
}
