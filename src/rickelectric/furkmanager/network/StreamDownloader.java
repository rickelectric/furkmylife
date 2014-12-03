package rickelectric.furkmanager.network;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLEncoder;

import javax.imageio.ImageIO;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import rickelectric.furkmanager.utils.ProxySettings;
import rickelectric.furkmanager.utils.SettingsManager;

public class StreamDownloader {

	private static StreamDownloader thisInstance;

	public static synchronized StreamDownloader getInstance() {
		if (thisInstance == null) {
			thisInstance = new StreamDownloader();
		}
		return thisInstance;
	}
	
	private StreamDownloader(){
		
	}

	public String fileToString(String filepath) throws IOException {
		FileReader f = new FileReader(filepath);
		BufferedReader b = new BufferedReader(f);
		String full = "", line = b.readLine();
		while (line != null) {
			full += line + "\n";
			line = b.readLine();
		}
		b.close();
		return full;
	}

	public String getStringStream(String url) throws Exception {
		return getStringStream(url, 1024);
	}

	public String getStringStream(String url, int batchWriteSize)
			throws Exception {
		return getStringStream(url, batchWriteSize, SettingsManager
				.getInstance().timeout());
	}

	public String getStringStream(String url, int batchWriteSize,
			int timeout) throws Exception {
		URL urli = new URL(url);
		DefaultHttpClient client = new DefaultHttpClient();

		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);

		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				timeout);

		HttpGet get = new HttpGet(urli.toURI());
		HttpResponse resp = client.execute(get);

		if (resp.getStatusLine().getStatusCode() != 200) {
			throw new FileNotFoundException("Server returned "
					+ resp.getStatusLine().getStatusCode());
		}

		BufferedInputStream bis = new BufferedInputStream(resp.getEntity()
				.getContent());
		ByteArrayOutputStream out = new ByteArrayOutputStream();

		byte[] b = new byte[batchWriteSize];
		int bytesRead = bis.read(b, 0, batchWriteSize);
		while (bytesRead != -1) {
			out.write(b, 0, bytesRead);
			bytesRead = bis.read(b, 0, batchWriteSize);
		}
		bis.close();
		String s = out.toString();
		out.flush();
		out.close();
		return s;
	}

	public String postMultipartStream(String url, MultipartEntity entity)
			throws Exception {
		URL urli = new URL(url);

		DefaultHttpClient client = new DefaultHttpClient();

		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);

		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 12000);

		HttpPost post = new HttpPost(urli.toURI());
		// post.setHeader("Content-Type", "application/x-www-form-urlencoded");
		post.setEntity(entity);

		HttpResponse resp = client.execute(post);
		int response = resp.getStatusLine().getStatusCode();

		if (response < 200 || response > 299)
			throw new IOException("HTTP Response " + response);

		BufferedInputStream bis = new BufferedInputStream(resp.getEntity()
				.getContent());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] b = new byte[512];
		int bytesRead = bis.read(b, 0, 512);
		while (bytesRead != -1) {
			os.write(b, 0, bytesRead);
			bytesRead = bis.read(b, 0, 512);
		}
		bis.close();
		String re = os.toString();
		os.close();
		return re;
	}

	public String postStringStream(String urlx, int batchWriteSize)
			throws Exception {
		String[] urli = urlx.split("[?]");
		URL url = new URL(urli[0]);
		DefaultHttpClient client = new DefaultHttpClient();

		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);

		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 12000);

		HttpPost post = new HttpPost(url.toURI());
		
		if(urli.length>1){
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(new StringEntity(urli[1]));
		}
		HttpResponse resp = client.execute(post);
		int response = resp.getStatusLine().getStatusCode();

		if (response < 200 || response > 299)
			throw new IOException("HTTP Response {"+urlx+"} = " + response);

		BufferedInputStream bis = new BufferedInputStream(resp.getEntity()
				.getContent());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		byte[] b = new byte[512];
		int bytesRead = bis.read(b, 0, 512);
		while (bytesRead != -1) {
			os.write(b, 0, bytesRead);
			bytesRead = bis.read(b, 0, 512);
		}
		bis.close();
		String re = os.toString();
		os.close();
		return re;
	}

	public String getFileStreamWithName(String url, File f,
			int batchWriteSize) throws Exception {
		String nextCookie = null, titleRet = null;
		DefaultHttpClient client = new DefaultHttpClient();

		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);

		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 12000);

		HttpResponse resp;
		do {
			URL urli = new URL(url);
			HttpGet get = new HttpGet(urli.toURI());

			get.getParams().setParameter("User-Agent", "Mozilla/5.0");
			get.getParams().setParameter("Accept-Language", "en-US,en;q=0.5");

			if (nextCookie != null)
				get.getParams().setParameter("Cookie",
						"JSESSIONID=" + URLEncoder.encode(nextCookie, "UTF-8"));

			resp = client.execute(get);

			Header[] loc = resp.getHeaders("Location");
			if (loc.length > 0)
				url = loc[0].getValue();

			Header[] cookies = resp.getHeaders("Set-Cookie");
			for (Header h : cookies) {
				if (h.getValue().split("=")[0].equalsIgnoreCase("JSESSIONID"))
					nextCookie = h.getValue().split("=")[1];
				break;
			}
			Header[] name = resp.getHeaders("Content-Disposition");
			if (name.length > 0) {
				for (Header n : name) {
					if (n.getValue().contains("filename="))
						titleRet = n.getValue().split("=\"")[1].split("\"")[0];
				}
			}
			/* do it until you get some code that is not a redirection */

		} while ((resp.getStatusLine().getStatusCode() / 100) == 3);/*
																	 * codes 3XX
																	 * are
																	 * redirections
																	 */

		if (resp.getStatusLine().getStatusCode() < 200
				|| resp.getStatusLine().getStatusCode() > 299)
			throw new FileNotFoundException("Server Error: Code "
					+ resp.getStatusLine().getStatusCode());

		InputStream in = resp.getEntity().getContent();
		FileOutputStream out = new FileOutputStream(f);
		byte[] b = new byte[1024];
		int count;

		while ((count = in.read(b)) > 0) {
			out.write(b, 0, count);
		}
		in.close();
		out.close();

		return titleRet;
	}

	public BufferedImage getImageStream(String url, int batchWriteSize)
			throws Exception {
		// ImgRequest img = RequestCache.ImageR.get(fileURL);
		// if (img != null)
		// return img.getImage();

		URL urli = new URL(url);
		DefaultHttpClient client = new DefaultHttpClient();

		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);

		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 12000);

		HttpGet get = new HttpGet(urli.toURI());
		HttpResponse resp = client.execute(get);

		if (resp.getStatusLine().getStatusCode() < 200
				|| resp.getStatusLine().getStatusCode() > 299) {
			throw new FileNotFoundException("Server returned "
					+ resp.getStatusLine().getStatusCode());
		}
		BufferedInputStream bis = new BufferedInputStream(resp.getEntity()
				.getContent());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] b = new byte[batchWriteSize];
		int bytesRead = bis.read(b, 0, batchWriteSize);
		while (bytesRead != -1) {
			out.write(b, 0, bytesRead);
			bytesRead = bis.read(b, 0, batchWriteSize);
		}
		bis.close();
		byte[] data = out.toByteArray();
		ByteArrayInputStream input = new ByteArrayInputStream(data);
		BufferedImage image = ImageIO.read(input);

		out.flush();
		out.close();

		try {
			// RequestCache.ImageR.add(fileURL, image);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	public void getFileStream(String fileURL, File f, int batchWriteSize)
			throws Exception {
		URL urli = new URL(fileURL);
		DefaultHttpClient client = new DefaultHttpClient();

		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);

		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 12000);

		HttpGet get = new HttpGet(urli.toURI());
		HttpResponse resp = client.execute(get);

		if (resp.getStatusLine().getStatusCode() < 200
				|| resp.getStatusLine().getStatusCode() > 299) {
			throw new FileNotFoundException("Server returned "
					+ resp.getStatusLine().getStatusCode());
		}
		BufferedInputStream bis = new BufferedInputStream(resp.getEntity()
				.getContent());
		OutputStream out = new FileOutputStream(f);

		byte[] b = new byte[batchWriteSize];
		int bytesRead = bis.read(b, 0, batchWriteSize);
		while (bytesRead != -1) {
			out.write(b, 0, bytesRead);
			bytesRead = bis.read(b, 0, batchWriteSize);
		}
		bis.close();

		out.flush();
		out.close();
	}
}
