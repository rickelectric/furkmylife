package rickelectric.furkmanager.idownloader;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Observable;

import javax.imageio.ImageIO;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethodBase;
import org.apache.commons.httpclient.ProxyHost;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.xerces.impl.dv.util.Base64;

import rickelectric.furkmanager.models.APIRequest;
import rickelectric.furkmanager.models.ImgRequest;
import rickelectric.furkmanager.network.RequestCache;
import rickelectric.furkmanager.utils.SettingsManager;

public class SDownload extends Observable implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;

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

	private HttpMethodBase method;

	public SDownload(URL url) {
		this.url = url;
		result = null;
		size = -1;
		downloaded = 0;
		status = IDLE;
		postData = null;
		method = null;
	}

	public SDownload(URL url, Part[] data) {
		this(url);
		this.postData = data;
	}

	public void run() {
		try {
			if(RequestCache.APIR.get(getUrl())!=null || RequestCache.ImageR.get(getUrl())!=null){
				status=COMPLETE;
				stateChanged();
				return;
			}
			apacheDownload();
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
		if (method != null && !method.isAborted()) {
			method.abort();
		}
		stateChanged();
	}

	protected void error() {
		status = ERROR;
		if (method != null && !method.isAborted()) {
			method.abort();
		}
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

	public void apacheDownload() throws IOException, InterruptedException {
		stateChanged();
		String fileURL = url.toExternalForm();
		InputStream stream = null;
		OutputStream out = new ByteArrayOutputStream();

		HttpClient client = new HttpClient();
		HttpClientParams params = client.getParams();
		params.setSoTimeout((int) (20000));
		client.setParams(params);
		stateChanged();

		if (postData == null) {// GET Method
			method = new GetMethod(fileURL);
		} else {// Post Multipart
			method = new PostMethod(fileURL);
			((PostMethod) method).setRequestEntity(new MultipartRequestEntity(
					postData, method.getParams()));
		}

		if (SettingsManager.proxyEnabled()) {
			ProxyHost pHost = new ProxyHost(SettingsManager.getProxyHost(),
					Integer.parseInt(SettingsManager.getProxyPort()));
			client.getHostConfiguration().setProxyHost(pHost);
			String encoded = new String(Base64.encode(new String(
					SettingsManager.getProxyUser() + ":"
							+ SettingsManager.getProxyPassword()).getBytes()));

			method.addRequestHeader("Proxy-Authorization", "Basic " + encoded);
		}

		if (result != null) {
			method.addRequestHeader("Range", "bytes=" + downloaded + "-");
		}
		try {
			stateChanged();
			client.executeMethod(method);
		} catch (Exception e) {
			out.close();
			throw new IOException("ConnectionException trying to connect to "
					+ fileURL, e);
		}

		if (method.getStatusCode() < 200 || method.getStatusCode() > 299) {
			out.close();
			throw new FileNotFoundException("Server returned "
					+ method.getStatusCode());
		}

		int contentLength = (int) method.getResponseContentLength();
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
		stream = method.getResponseBodyAsStream();

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
				out.close();
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

		/*
		 * Change status to complete if this point was reached because
		 * downloading has finished.
		 */
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

	public String string() {
		if (status == COMPLETE) {
			APIRequest r = RequestCache.APIR.get(getUrl());
			if(r!=null) return r.getJSON();
			try {
				String s = new String(result);
				RequestCache.APIR.add(getUrl(), s, false);
				return s;
			} catch (Exception e) {
			}
		}
		return null;
	}

	public BufferedImage image() {
		if (status == COMPLETE) {
			ImgRequest ir = RequestCache.ImageR.get(getUrl());
			if(ir!=null) return ir.getImage();
			try {
				ByteArrayInputStream bis = new ByteArrayInputStream(result);
				BufferedImage img=ImageIO.read(bis);
				RequestCache.ImageR.add(getUrl(), img);
				return img;
			} catch (Exception e) {
			}
		}
		return null;
	}

}
