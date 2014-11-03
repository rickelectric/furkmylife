package rickelectric.furkmanager.network;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.Observable;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.xerces.impl.dv.util.Base64;
import org.json.JSONObject;

import rickelectric.furkmanager.models.APIRequest;
import rickelectric.furkmanager.models.ImgRequest;
import rickelectric.furkmanager.network.RequestCache;
import rickelectric.furkmanager.utils.SettingsManager;

public class ProxDownload extends Observable implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;
	
	//System.setProperty("socksProxyHost", "127.0.0.1");
	//System.setProperty("socksProxyPort", "9150");
	//TODO ^^<<-- This Works For Socks
	//private int socks;//Here to generate annoying, OCD inducing warning.

	protected static final int MAX_BUFFER_SIZE = 1;

	public static final String STATUSES[] = { "Downloading", "Paused",
			"Complete", "Cancelled", "Error", "Idle / Connecting" };

	public static final int DOWNLOADING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;
	public static final int IDLE = 5;

	protected URL url; // download URL
	protected byte[] result;
	protected int size; // size of download in bytes
	protected int downloaded; // number of bytes downloaded
	protected int status; // current status of download

	private Part[] postData;

	public ProxDownload(URL url) {
		this.url = url;
		result = null;
		size = -1;
		downloaded = 0;
		status = IDLE;
		postData = null;
	}

	public ProxDownload(URL url, Part[] data) {
		this(url);
		this.postData = data;
	}

	public void run() {
		try {
			if (RequestCache.APIR.get(getUrl()) != null
					|| RequestCache.ImageR.get(getUrl()) != null) {
				status = COMPLETE;
				stateChanged();
				return;
			}
			httpDownload();
		} catch (IOException e) {
			e.printStackTrace();
			error();
		} catch (InterruptedException e) {
			e.printStackTrace();
			error();
		}
	}

	// Get this download's URL.
	public String getUrl() {
		return url.toString();
	}

	// Get this download's size.
	public int getSize() {
		return size;
	}

	public String getSizeString() {
		float sz = size;
		String[] reps = new String[] { "B", "kB", "MB", "GB", "TB" };
		int i = 0;
		while (sz > 1024 && i <= 4) {
			sz = sz / 1024f;
			i++;
		}
		String[] szs = ("" + sz).split("\\.");
		if (szs[1].length() > 2)
			szs[1] = szs[1].substring(0, 2);
		return szs[0] + "." + szs[1] + " " + reps[i];
	}

	// Get this download's progress.
	public float getProgress() {
		return ((float) downloaded / size) * 100;
	}

	public int getStatus() {
		return status;
	}

	public void pause() {
		status = PAUSED;
		stateChanged();
	}

	public void resume() {
		status = DOWNLOADING;
		stateChanged();
		download();
	}

	public void cancel() {
		status = CANCELLED;
		stateChanged();
	}

	protected void error() {
		status = ERROR;
		stateChanged();
	}

	public void download() {
		Thread thread = new Thread(this);
		thread.start();
	}

	// Get file name portion of URL.
	public String getFileName(URL url) {
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/') + 1);
	}

	public String getFileName() {
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/') + 1);
	}

	protected void stateChanged() {
		setChanged();
		notifyObservers();
	}

	public void httpDownload() throws IOException, InterruptedException {
		stateChanged();

		URLConnection conn = null;

		Proxy proxy = Proxy.NO_PROXY;
		if (SettingsManager.proxyEnabled()) {
			proxy = new Proxy(SettingsManager.getProxyType(), new InetSocketAddress(
					SettingsManager.getProxyUser(),
					Integer.parseInt(SettingsManager.getProxyPort())));

			Authenticator authenticator = new Authenticator() {

				public PasswordAuthentication getPasswordAuthentication() {
					return (new PasswordAuthentication(
							SettingsManager.getProxyUser(), SettingsManager
									.getProxyPassword().toCharArray()));
				}
			};
			Authenticator.setDefault(authenticator);

		}
		conn = url.openConnection(proxy);

		if (postData == null) {// GET Method
			if(url.getProtocol().toLowerCase().contains("https"))
				((HttpsURLConnection)conn).setRequestMethod("GET");
			else
				((HttpURLConnection)conn).setRequestMethod("GET");
			conn.setDoOutput(false);
		} else {// Post Multipart
			HttpMethodParams params = new HttpMethodParams();
			MultipartRequestEntity reqEntity = new MultipartRequestEntity(
					postData, params);
			conn.setUseCaches(false);
			conn.setDoOutput(true);

			if(url.getProtocol().toLowerCase().contains("https"))
				((HttpsURLConnection)conn).setRequestMethod("POST");
			else
				((HttpURLConnection)conn).setRequestMethod("POST");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.addRequestProperty("Content-length",
					reqEntity.getContentLength() + "");
			conn.addRequestProperty("Content-Type", reqEntity.getContentType());

			OutputStream os = conn.getOutputStream();
			reqEntity.writeRequest(conn.getOutputStream());
			os.flush();
			os.close();
		}

		if (result != null) {
			conn.addRequestProperty("Range", "bytes=" + downloaded + "-");
		}

		int responseCode;
		if (conn instanceof HttpsURLConnection) {
			responseCode = ((HttpsURLConnection) conn).getResponseCode();
			if (responseCode < 200 || responseCode > 299) {
				((HttpsURLConnection) conn).disconnect();
				throw new FileNotFoundException("Server returned "
						+ responseCode);
			}
		} else {
			responseCode = ((HttpURLConnection) conn).getResponseCode();
			if (responseCode < 200 || responseCode > 299) {
				((HttpURLConnection) conn).disconnect();
				throw new FileNotFoundException("Server returned "
						+ responseCode);
			}
		}

		int contentLength = conn.getContentLength();
		if (contentLength < 1) {
			error();
			return;
		}

		// Set the size for this download if it hasn't been already set.
		if (size == -1) {
			size = contentLength;
			stateChanged();
		}

		result = new byte[size];
		InputStream stream = new BufferedInputStream(conn.getInputStream());

		status = DOWNLOADING;

		while (status == DOWNLOADING) {
			if (Thread.interrupted()) {
				if (stream != null) {
					try {
						stream.close();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				error();
				throw new InterruptedException("Interrupted By User");
			}
			int read = stream.read();
			if (read == -1)
				break;

			result[downloaded] = (byte) read;
			downloaded++;
			if (downloaded % MAX_BUFFER_SIZE == 0)
				stateChanged();
		}

		if (status == DOWNLOADING) {
			status = COMPLETE;
			stateChanged();
		}

		// Close connection to server.
		if (stream != null) {
			try {
				stream.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public int getDownloaded() {
		return downloaded;
	}

	public String toString() {
		if (status == COMPLETE) {
			APIRequest r = RequestCache.APIR.get(getUrl());
			if (r != null)
				return r.getJSON();
			try {
				String s = new String(result);
				RequestCache.APIR.add(getUrl(), s, false);
				return s;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public BufferedImage toImage() {
		if (status == COMPLETE) {
			ImgRequest ir = RequestCache.ImageR.get(getUrl());
			if (ir != null)
				return ir.getImage();
			try {
				ByteArrayInputStream bis = new ByteArrayInputStream(result);
				BufferedImage img = ImageIO.read(bis);
				RequestCache.ImageR.add(getUrl(), img);
				return img;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		SettingsManager.init();
		SettingsManager.enableProxy(true);
		SettingsManager.setProxy(Proxy.Type.SOCKS,"127.0.0.1", "9150", "", "");
		final BufferedImage img = ImageIO.read(new File(
				"C:\\Users\\Ionicle\\Pictures\\Friends\\Courtney and I.jpg"));
		new Thread(new Runnable() {
			public void run() {
				reKogFaceDetect(img);
			}
		}).start();
	}

	private static String postImage(String key, String secret, String jobs,
			BufferedImage img) throws IOException, InterruptedException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
		ImageIO.write(img, "jpg", baos);
		baos.flush();

		String base64String = Base64.encode(baos.toByteArray());
		baos.close();

		Part[] parts = { new StringPart("api_key", key),
				new StringPart("api_secret", secret),
				new StringPart("jobs", jobs),
				new StringPart("base64", base64String) };

		URL rapi = new URL("http://rekognition.com/func/api/");
		ProxDownload d = new ProxDownload(rapi, parts);
		d.download();
		while (d.getStatus() != COMPLETE && d.getStatus() != ERROR) {
			Thread.sleep(200);
		}
		return d.toString();
	}

	public static String REKOGNITION_API_KEY = "HKLIfOQk250gDoFh",
			REKOGNITION_API_SECRET = "rrvbnfINoPyA8MKs";

	public static void reKogFaceDetect(BufferedImage img) {
		try {
			String key = REKOGNITION_API_KEY;
			String secret = REKOGNITION_API_SECRET;
			String jobs = "face_part_gender_emotion_race_age_mouth_open_wide_eye_closed";
			String json = postImage(key, secret, jobs, img);
			JSONObject o = new JSONObject(json);
			System.out.println(o.toString(4));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
