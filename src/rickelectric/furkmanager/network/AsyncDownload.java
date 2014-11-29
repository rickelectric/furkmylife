package rickelectric.furkmanager.network;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.Observable;

import javax.imageio.ImageIO;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import rickelectric.furkmanager.models.APIRequest;
import rickelectric.furkmanager.models.ImgRequest;
import rickelectric.furkmanager.utils.ProxySettings;
import rickelectric.furkmanager.utils.SettingsManager;

public class AsyncDownload extends Observable implements Runnable, Serializable {
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

	private MultipartEntity postData;

	public AsyncDownload(URL url) {
		this.url = url;
		result = null;
		size = -1;
		downloaded = 0;
		status = IDLE;
		postData = null;
	}

	public AsyncDownload(URL url, MultipartEntity data) {
		this(url);
		this.postData = data;
	}

	@Override
	public void run() {
		try {
			if (RequestCache.APIR.get(getUrl()) != null
					|| RequestCache.ImageR.get(getUrl()) != null) {
				status = COMPLETE;
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
		} catch (Exception e) {
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

	public void apacheDownload() throws Exception {
		stateChanged();

		DefaultHttpClient client = new DefaultHttpClient();

		ProxySettings settings = SettingsManager.getInstance().getProxySettings();
		settings.applyProxyTo(client);
		
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 12000);

		HttpResponse resp;
		HttpUriRequest request;

		if (postData == null) {// GET Method
			request = new HttpGet(url.toURI());

		} else {// Post Multipart
			request = new HttpPost(url.toURI());
			
			((HttpPost) request).setEntity(postData);
		}

		if (result != null) {
			request.addHeader("Range", "bytes=" + downloaded + "-");
		}

		resp = client.execute(request);

		if (resp.getStatusLine().getStatusCode() != 200) {
			throw new FileNotFoundException("Server returned "
					+ resp.getStatusLine().getStatusCode());
		}
		
		size = (int)resp.getEntity().getContentLength();

		result = new byte[size];
		InputStream stream = new BufferedInputStream(resp.getEntity()
				.getContent());

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
			if (downloaded % MAX_BUFFER_SIZE == 0){
				stateChanged();
				Thread.sleep(10);
			}
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

	@Override
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

}
