package rickelectric.furkmanager.models;

import java.util.Arrays;

import rickelectric.furkmanager.models.enums.FileType;

public class FurkFile extends APIObject implements MoveableItem {

	private String[] screenshotThumbs, screenshots, idLabels;
	private String idFeeds;

	private String id, ctime, status;
	private String avResult, avInfo, avTime;
	private int numAudioFiles, numVideoFiles, numImageFiles;
	private String videoInfo;

	private String urlDl, urlPls, urlPage;
	private String deletedReason;

	private FileType type;

	private int isLinked, isReady, isProtected;

	public FurkFile(String name, String infoHash, long size, String id,
			String urlDl, FileType type, String status, String urlPage,
			int isLinked, int isReady) {
		super(name, infoHash, size);
		this.id = id;
		this.urlDl = urlDl;
		this.type = type;
		this.status = status;
		this.urlPage = urlPage;
		this.isLinked = isLinked;
		this.isReady = isReady;
	}

	@Override
	public String getID() {
		return id;
	}

	public FileType getType() {
		return type == null ? FileType.DEFAULT : type;
	}

	public String[] getIdLabels() {
		return idLabels;
	}

	public String getParentID() {
		if (idLabels == null || idLabels.length == 0)
			return "0";
		return idLabels[0];
	}

	public void setParentID(String labelID) {
		idLabels = new String[] { labelID };
	}

	public void setIdLabels(String[] idLabels) {
		this.idLabels = idLabels;
	}

	public String getIdFeeds() {
		return idFeeds;
	}

	public void setIdFeeds(String idFeeds) {
		this.idFeeds = idFeeds;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrlPage() {
		return urlPage;
	}

	public void setUrlPage(String url_page) {
		this.urlPage = url_page;
	}

	public String getDeletedReason() {
		return deletedReason;
	}

	public void setDeletedReason(String deletedReason) {
		this.deletedReason = deletedReason;
	}

	public boolean isLinked() {
		return isLinked == 1;
	}

	public void setLinked(boolean isLinked) {
		this.isLinked = isLinked ? 1 : 0;
	}

	public boolean isReady() {
		return isReady == 1;
	}

	public void setReady(boolean ready) {
		this.isReady = ready ? 1 : 0;
	}

	public boolean isProtected() {
		return isProtected == 1;
	}

	public void setProtected(int isProtected) {
		this.isProtected = isProtected;
	}

	public void setAntivirus(String result, String info, String time) {
		this.avResult = result;
		this.avInfo = info;
		this.avTime = time;
	}

	public String getAvResult() {
		return avResult;
	}

	public String getAvInfo() {
		return avInfo;
	}

	public String getAvTime() {
		return avTime;
	}

	public void fileCount(int audioFiles, int videoFiles, int imageFiles) {
		numAudioFiles = audioFiles;
		numVideoFiles = videoFiles;
		numImageFiles = imageFiles;
	}

	public int getNumAudioFiles() {
		return numAudioFiles;
	}

	public int getNumVideoFiles() {
		return numVideoFiles;
	}

	public int getNumImageFiles() {
		return numImageFiles;
	}

	public String[] getThumbs() {
		return screenshotThumbs;
	}

	public void setThumbs(String[] ss_urls_tn) {
		this.screenshotThumbs = ss_urls_tn;
	}

	public String[] getScreenshots() {
		return screenshots;
	}

	public void setScreenshots(String[] ss_urls) {
		this.screenshots = ss_urls;
	}

	public String getUrlDl() {
		return urlDl;
	}

	public void setUrlDl(String urlDl) {
		this.urlDl = urlDl;
	}

	public String getUrlPls() {
		return urlPls;
	}

	public void setUrlPls(String urlPls) {
		this.urlPls = urlPls;
	}

	public void setVideoInfo(String videoInfo) {
		this.videoInfo = videoInfo;
	}

	public String getVideoInfo() {
		return videoInfo;
	}

	public void overwrite(FurkFile f) {
		screenshotThumbs = f.screenshotThumbs;
		screenshots = f.screenshots;
		idLabels = f.idLabels;

		id = f.id;
		ctime = f.ctime;
		type = f.type;
		status = f.status;
		avResult = f.avResult;
		avInfo = f.avInfo;
		avTime = f.avTime;
		urlDl = f.urlDl;
		urlPls = f.urlPls;
		urlPage = f.urlPage;
		numVideoFiles = f.numVideoFiles;
		numAudioFiles = f.numAudioFiles;
		numImageFiles = f.numImageFiles;
		deletedReason = f.deletedReason;

		isLinked = f.isLinked;
		isReady = f.isReady;
		isProtected = f.isProtected;

		stateChanged();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((avInfo == null) ? 0 : avInfo.hashCode());
		result = prime * result
				+ ((avResult == null) ? 0 : avResult.hashCode());
		result = prime * result + ((avTime == null) ? 0 : avTime.hashCode());
		result = prime * result + ((ctime == null) ? 0 : ctime.hashCode());
		result = prime * result
				+ ((deletedReason == null) ? 0 : deletedReason.hashCode());
		result = prime * result + Arrays.hashCode(idLabels);
		result = prime * result + isLinked;
		result = prime * result + isProtected;
		result = prime * result + isReady;
		result = prime * result + numAudioFiles;
		result = prime * result + numImageFiles;
		result = prime * result + numVideoFiles;

		result = prime * result + Arrays.hashCode(screenshotThumbs);
		result = prime * result + Arrays.hashCode(screenshots);
		result = prime * result + ((status == null) ? 0 : status.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result + ((urlPage == null) ? 0 : urlPage.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return super.toString()
				+ " ==> FurkFile ["
				+ (screenshotThumbs != null ? "screenshotThumbs="
						+ Arrays.toString(screenshotThumbs) + ", " : "")
				+ (screenshots != null ? "screenshots="
						+ Arrays.toString(screenshots) + ", " : "")
				+ (idLabels != null ? "idLabels=" + Arrays.toString(idLabels)
						+ ", " : "")
				+ (ctime != null ? "ctime=" + ctime + ", " : "")
				+ (type != null ? "type=" + type + ", " : "")
				+ (status != null ? "status=" + status + ", " : "")
				+ (avResult != null ? "avResult=" + avResult + ", " : "")
				+ (avInfo != null ? "avInfo=" + avInfo + ", " : "")
				+ (avTime != null ? "avTime=" + avTime + ", " : "")
				+ (urlPage != null ? "urlPage=" + urlPage + ", " : "")
				+ "numAudioFiles="
				+ numAudioFiles
				+ ", "
				+ "numVideoFiles="
				+ numVideoFiles
				+ ", "
				+ "numImageFiles="
				+ numImageFiles
				+ ", "
				+ (deletedReason != null ? "deletedReason=" + deletedReason
						+ ", " : "") + "isLinked=" + isLinked + ", isReady="
				+ isReady + ", isProtected=" + isProtected + "]";
	}

	@Override
	public int compareTo(Object o) {
		if (o instanceof MoveableItem) {
			return getName().compareTo(((MoveableItem) o).getName());
		}
		if (o instanceof String) {
			return getName().compareTo(o.toString());
		}
		throw new IllegalArgumentException("Expected MoveableItem or String");
	}

}
