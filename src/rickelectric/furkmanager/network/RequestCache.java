package rickelectric.furkmanager.network;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import rickelectric.furkmanager.models.APIRequest;
import rickelectric.furkmanager.models.ImgRequest;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.windows.ImgCacheViewer;

public class RequestCache {

	public static void init() {
		APIR.requests = new ArrayList<APIRequest>();
		ImageR.images = new ArrayList<ImgRequest>();
	}

	public static class APIR {

		private static ArrayList<APIRequest> requests;

		public static APIRequest get(String url){
			if(APIBridge.key()==null) return null;
			try{
				if (requests == null)
					return null;
				Collections.sort(requests);
				int pos = Collections.binarySearch(requests, url);
				if (pos < 0)
					return null;
				APIRequest ar = requests.get(pos);
				if (ar.getUrl().equals(url))
					return ar;
				return null;
			}catch(Exception e){
				flush();
				return null;
			}
		}

		public static boolean add(String url, String json, boolean permanent) {
			if(APIBridge.key()==null) return false;
			if(url==null||url.equals("")||json==null||json.equals("")) return false;
			if (requests == null)
				requests = new ArrayList<APIRequest>();
			APIRequest exist = get(url);
			if (exist != null) {
				exist.setJSON(json);
				if(permanent) exist.setTime(0);
				else exist.setTime(System.currentTimeMillis());
				return true;
			}
			boolean added = requests.add(new APIRequest(url, json, System
					.currentTimeMillis()));
			return added;
		}
		
		public static String find(String url){
			if(APIBridge.key()==null) return null;
			if(url==null) return null;
			APIRequest r=RequestCache.APIR.get(url);
			if(r!=null){
				long time=System.currentTimeMillis();
				if(time==0) return r.getJSON();
				if((time-29000)<r.getTime())
					return r.getJSON();
			}
			return null;
		}

		public static void flush() {
			requests.removeAll(requests);
		}

		public static Iterator<APIRequest> iterate() {
			if(APIBridge.key()==null) return null;
			if (requests == null)
				return null;
			return requests.iterator();
		}

	}

	public static class ImageR {

		private static ArrayList<ImgRequest> images;

		public static ImgRequest get(String url) {
			if(APIBridge.key()==null) return null;
			if (images == null)
				return null;
			ImgRequest.compareCriterion(ImgRequest.URL);
			Collections.sort(images);
			int pos = Collections.binarySearch(images, url);
			if (pos < 0)
				return null;
			ImgRequest im = images.get(pos);
			if (im.getUrl().equals(url))
				return im;
			return null;
		}

		public static boolean add(String url, BufferedImage image) {
			if(APIBridge.key()==null) return false;
			if (images == null)
				images = new ArrayList<ImgRequest>();
			if(images.size()>SettingsManager.numCachedImages()){
				ImgRequest.compareCriterion(ImgRequest.SIZE);
				Collections.sort(images);
				int n=images.size()-5;
				for(int i=0;i<5;i++){
					images.remove(n);
				}
				System.gc();
			}
			ImgRequest exist = get(url);
			if (exist != null) {
				exist.setImage(image);
				exist.setTime(System.currentTimeMillis());
				ImgCacheViewer.modelTable();
				return true;
			}
			boolean added = images.add(new ImgRequest(url, image, System
					.currentTimeMillis()));
			ImgCacheViewer.modelTable();
			return added;
		}

		public static void flush() {
			Iterator<ImgRequest> i=images.iterator();
			while(i.hasNext()){
				ImgRequest ii=i.next();
				ii.setImage(null);
				i.remove();
			}
			System.gc();
			ImgCacheViewer.modelTable();
		}

		public static Iterator<ImgRequest> iterate() {
			if(APIBridge.key()==null) return null;
			if (images == null)
				return null;
			return images.iterator();
		}

		public static void remove(ImgRequest im) {
			if(images==null) return;
			images.remove(im);

		}

	}

}
