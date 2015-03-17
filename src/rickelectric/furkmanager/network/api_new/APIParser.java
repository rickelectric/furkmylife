package rickelectric.furkmanager.network.api_new;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.models.FurkDownload;
import rickelectric.furkmanager.models.FurkFeed;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkLabel;
import rickelectric.furkmanager.models.FurkTFile;
import rickelectric.furkmanager.models.enums.FileType;

public class APIParser {

	public static APIObject parseFile(JSONObject fileObject) {
		if (fileObject == null)
			throw new IllegalArgumentException("input must not be null");
		try {
			String name = fileObject.getString("name");
			String info_hash = fileObject.getString("info_hash");
			long size = fileObject.getLong("size");

			Object id;
			if ((id = fileObject.get("id")) != JSONObject.NULL
					&& !id.equals("")) {
				FileType type = FileType.fromString(fileObject
						.getString("type"));
				String url_dl;
				try {
					url_dl = fileObject.getString("url_dl");
				} catch (Exception e) {
					url_dl = null;
				}
				String url_page = fileObject.getString("url_page");
				String status = fileObject.getString("status");
				int is_linked = fileObject.getInt("is_linked");
				int is_ready = fileObject.getInt("is_ready");

				FurkFile fi = new FurkFile(name, info_hash, size,
						id.toString(), url_dl, type, status, url_page,
						is_linked, is_ready);

				try {
					String url_pls = fileObject.getString("url_pls");
					fi.setUrlPls(url_pls);
				} catch (Exception e) {
				}

				try {
					String id_feeds = fileObject.getString("id_feeds");
					fi.setIdFeeds(id_feeds);
				} catch (Exception e) {
				}

				try {
					String[] id_labels = null;
					JSONArray ja = fileObject.getJSONArray("id_labels");
					id_labels = new String[ja.length()];
					for (int q = 0; q < ja.length(); q++) {
						id_labels[q] = ja.getString(q);
					}
					fi.setIdLabels(id_labels);
				} catch (Exception e) {
				}

				try {
					int files_num_audio = fileObject.getInt("files_num_audio");
					int files_num_image = fileObject.getInt("files_num_image");
					int files_num_video = fileObject.getInt("files_num_video");
					fi.fileCount(files_num_audio, files_num_video,
							files_num_image);
				} catch (Exception e) {
				}

				try {
					String av_info = fileObject.getString("av_info");
					String av_result = fileObject.getString("av_result");
					String av_time = fileObject.getString("av_time");
					fi.setAntivirus(av_result, av_info, av_time);
				} catch (Exception e) {
				}

				String[] thumbs, ss = thumbs = null;
				try {
					JSONArray tn = fileObject.getJSONArray("ss_urls_tn");
					JSONArray ssh = fileObject.getJSONArray("ss_urls");
					thumbs = new String[tn.length()];
					ss = new String[ssh.length()];
					for (int q = 0; q < tn.length(); q++) {
						thumbs[q] = tn.getString(q);
						ss[q] = ssh.getString(q);
					}
					fi.setThumbs(thumbs);
					fi.setScreenshots(ss);
				} catch (Exception e) {
				}
				
				try{
					String video_info=fileObject.getString("video_info");
					fi.setVideoInfo(video_info);
				} catch (Exception e) {
				}

				try {
					String del_reason = fileObject.getString("del_reason");
					fi.setDeletedReason(del_reason);
				} catch (Exception e) {
				}

				return fi;
			}
			APIObject obj = new APIObject(name, info_hash, size);
			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static ArrayList<APIObject> parseFileList(JSONArray files) {
		if (files == null)
			throw new IllegalArgumentException("input must not be null");
		ArrayList<APIObject> fl = new ArrayList<APIObject>();

		for (int i = 0; i < files.length(); i++) {
			JSONObject f = files.getJSONObject(i);
			APIObject obj = parseFile(f);
			if (f != null) {
				fl.add(obj);
			}
		}
		return fl;
	}
	
	public static FurkTFile parseTFile(JSONObject tfileObject){
		if (tfileObject == null)
			throw new IllegalArgumentException("input must not be null");
		String name, dl, pls, path, ct;
		long size;
		try {
			name = tfileObject.getString("name");
		} catch (Exception e) {
			name = "";
		}
		try {
			size = tfileObject.getLong("size");
		} catch (Exception e) {
			size = 0L;
		}
		try {
			dl = tfileObject.getString("url_dl");
		} catch (Exception e) {
			dl = "";
		}
		try {
			pls = tfileObject.getString("url_pls");
		} catch (Exception e) {
			pls = "";
		}
		try {
			path = tfileObject.getString("path");
		} catch (Exception e) {
			path = "";
		}
		try {
			ct = tfileObject.getString("ct");
		} catch (Exception e) {
			ct = "default";
		}

		FurkTFile t = new FurkTFile(name, size, dl, pls, path, ct);

		if (ct.toLowerCase().contains("video")) {
			String player, thumbURL;
			int width, height, thumbHeight;
			try {
				player = tfileObject.getString("player");
			} catch (Exception e) {
				player = "";
			}
			try {
				thumbURL = tfileObject.getString("url_tn");
			} catch (Exception e) {
				thumbURL = "";
			}
			try {
				width = tfileObject.getInt("width");
			} catch (Exception e) {
				width = 0;
			}
			try {
				height = tfileObject.getInt("height");
			} catch (Exception e) {
				height = 0;
			}
			try {
				thumbHeight = tfileObject.getInt("tn_height");
			} catch (Exception e) {
				thumbHeight = 0;
			}
			t.mediaInfo(player, thumbURL, width, height, thumbHeight);
			
		}
		return t;
	}
	
	public static ArrayList<FurkTFile> parseTFileList(JSONArray tfs) {
		if (tfs == null)
			throw new IllegalArgumentException("input must not be null");
		ArrayList<FurkTFile> tfiles = new ArrayList<FurkTFile>();
		for (int i = 0; i < tfs.length(); i++) {
			JSONObject curr = tfs.getJSONObject(i);
			FurkTFile t = parseTFile(curr);

			tfiles.add(t);
		}

		return tfiles;
	}

	public static FurkDownload parseDownload(JSONObject dlObject) {
		if (dlObject == null)
			throw new IllegalArgumentException("input must not be null");
		Object name = dlObject.get("name");
		if (name != JSONObject.NULL && !name.equals("")) {
			FurkDownload d = new FurkDownload(dlObject.getString("id"),
					name.toString(), dlObject.getString("info_hash"),
					dlObject.getLong("size"), dlObject.getString("adding_dt"));

			try {
				d.setDateCompleted(dlObject.getString("finish_dt"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				d.setDlStatus(dlObject.getString("dl_status"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				d.setMtime(dlObject.getString("mtime"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				d.bytes(dlObject.getLong("bytes"), dlObject.getLong("up_bytes"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {

				if (d.getDlStatus().equals("failed"))
					d.setFailReason(dlObject.getString("fail_reason"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				d.setReady(dlObject.get("is_ready") != JSONObject.NULL
						&& dlObject.getInt("is_ready") == 1);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				d.dlStatus(dlObject.getString("active_status"),
						dlObject.getInt("peers"), dlObject.getInt("seeders"),
						dlObject.getLong("up_speed"),
						dlObject.getLong("speed"), dlObject.getString("have"));
			} catch (Exception e) {
			}
			return d;
		}
		return null;
	}

	public static ArrayList<FurkDownload> parseDownloadList(JSONArray dls) {
		if (dls == null)
			throw new IllegalArgumentException("input must not be null");
		int len = dls.length();
		ArrayList<FurkDownload> dlArr = new ArrayList<FurkDownload>();
		for (int i = 0; i < len; i++) {
			JSONObject obj = dls.getJSONObject(i);
			FurkDownload d = parseDownload(obj);
			if (d != null) {
				dlArr.add(d);
			}
		}
		return dlArr;
	}

	public static FurkFeed parseFeed(JSONObject feedObject) {
		if (feedObject == null)
			throw new IllegalArgumentException("input must not be null");
		return null;
	}

	public static FurkLabel parseLabel(JSONObject labelObj) {
		if (labelObj == null)
			throw new IllegalArgumentException("input must not be null");
		Object tid = labelObj.get("id");
		if (tid != null && tid != JSONObject.NULL && !tid.equals("")) {
			String id = tid.toString();
			String name = labelObj.getString("name");
			String parent = labelObj.getString("id_labels");
			String color = labelObj.get("color") == JSONObject.NULL ? null
					: labelObj.getString("color");
			String bg = labelObj.get("bg") == JSONObject.NULL ? null : labelObj
					.getString("bg");
			int sorder = labelObj.getInt("sorder");
			FurkLabel l = new FurkLabel(id, name);
			l.setParentID(parent);
			l.setColor(color);
			l.setBackground(bg);
			l.setSortOrder(sorder);
			return l;
		}
		return null;
	}

	public static ArrayList<FurkLabel> parseLabelList(JSONArray labels) {
		if (labels == null)
			throw new IllegalArgumentException("input must not be null");
		ArrayList<FurkLabel> llist = new ArrayList<FurkLabel>();
		for (int i = 0; i < labels.length(); i++) {
			FurkLabel l = parseLabel(labels.getJSONObject(i));
			if (l != null)
				llist.add(l);
		}
		return llist;
	}

}
