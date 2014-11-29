package rickelectric.furkmanager.network;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;

import javax.imageio.ImageIO;
import javax.net.ssl.HttpsURLConnection;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpHost;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.xerces.impl.dv.util.Base64;

import rickelectric.furkmanager.models.ImgRequest;
import rickelectric.furkmanager.utils.SettingsManager;

public class StreamDownloader {

	private static boolean interrupt = false;

	public static String fileToString(String filepath) throws IOException {
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

	public static String postDataPartStream(String fileURL, Part[] parts)
			throws IOException {
		OutputStream out = new ByteArrayOutputStream();
		URLConnection conn = null;
		URL url = new URL(fileURL);

		int batchWriteSize = SettingsManager.downloadBuffer();

		if (SettingsManager.proxyEnabled()) {
			Proxy proxy = new Proxy(SettingsManager.getProxyType(),
					new InetSocketAddress(SettingsManager.getProxyHost(),
							Integer.parseInt(SettingsManager.getProxyPort())));

			Authenticator authenticator = new Authenticator() {

				@Override
				public PasswordAuthentication getPasswordAuthentication() {
					return (new PasswordAuthentication(
							SettingsManager.getProxyUser(), SettingsManager
									.getProxyPassword().toCharArray()));
				}
			};
			Authenticator.setDefault(authenticator);

			conn = url.openConnection(proxy);
		} else {
			conn = url.openConnection();
		}

		HttpMethodParams params = new HttpMethodParams();
		MultipartRequestEntity reqEntity = new MultipartRequestEntity(parts,
				params);
		conn.setUseCaches(false);
		conn.setDoOutput(true);

		if (conn instanceof HttpsURLConnection)
			((HttpsURLConnection) conn).setRequestMethod("POST");
		else
			((HttpURLConnection) conn).setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.addRequestProperty("Content-length", reqEntity.getContentLength()
				+ "");
		conn.addRequestProperty("Content-Type", reqEntity.getContentType());

		OutputStream os = conn.getOutputStream();
		reqEntity.writeRequest(conn.getOutputStream());
		os.flush();
		os.close();

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
			throw new IOException("Stream Empty");
		}

		BufferedInputStream bis = new BufferedInputStream(conn.getInputStream());
		if (interrupt) {
			interrupt = false;
			return null;
		}

		byte[] b = new byte[batchWriteSize];
		int bytesRead = bis.read(b, 0, batchWriteSize);
		while (bytesRead != -1) {
			if (interrupt) {
				out.close();
				interrupt = false;
				return null;
			}
			out.write(b, 0, bytesRead);
			bytesRead = bis.read(b, 0, batchWriteSize);
		}
		bis.close();
		String s = out.toString();
		out.flush();
		out.close();
		return s;
	}

	private static void proxySettings(HttpClient client, HttpMethod method) {
		if (SettingsManager.proxyEnabled()) {
			client.getParams().setParameter(
					"Proxy",
					new HttpHost(SettingsManager.getProxyHost(), Integer
							.parseInt(SettingsManager.getProxyPort())));
			String encoded = new String(Base64.encode(new String(
					SettingsManager.getProxyUser() + ":"
							+ SettingsManager.getProxyPassword()).getBytes()));

			method.addRequestHeader("Proxy-Authorization", "Basic " + encoded);
		}
	}
	
	public static String getStringStream(String url) throws IOException{
		URL obj = new URL(url);
		HttpsURLConnection con = null;
		if (SettingsManager.proxyEnabled()) {
			Proxy proxy = new Proxy(SettingsManager.getProxyType(),
					new InetSocketAddress(SettingsManager.getProxyHost(),
							Integer.parseInt(SettingsManager.getProxyPort())));
			System.out.println("Proxy Enabled.\nConnecting To: "
					+ SettingsManager.getProxyHost() + ": "
					+ Integer.parseInt(SettingsManager.getProxyPort()));
			Authenticator authenticator = new Authenticator() {

				@Override
				public PasswordAuthentication getPasswordAuthentication() {
					return (new PasswordAuthentication(
							SettingsManager.getProxyUser(), SettingsManager
									.getProxyPassword().toCharArray()));
				}
			};
			Authenticator.setDefault(authenticator);
			con = (HttpsURLConnection) obj.openConnection(proxy);
		} else {
			con = (HttpsURLConnection) obj.openConnection();
		}
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (interrupt) {
			interrupt = false;
			return null;
		}
		
		int responseCode = con.getResponseCode();
		if (responseCode < 200 || responseCode > 299)
			throw new IOException("Server Error: Code " + responseCode);

		if (interrupt) {
			interrupt = false;
			return null;
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			if (interrupt) {
				in.close();
				interrupt = false;
				return null;
			}
			response.append(inputLine);
			response.append("\n");
		}
		in.close();

		return response.toString();
	}

	public static String getStringStream(String fileURL, int batchWriteSize)
			throws IOException {
		OutputStream out = new ByteArrayOutputStream();
		GetMethod get = new GetMethod(fileURL);
		HttpClient client = new HttpClient();
		HttpClientParams params = client.getParams();
		params.setSoTimeout((8000));
		client.setParams(params);
		
		if (SettingsManager.proxyEnabled()) {
			client.getHostConfiguration().setProxy(SettingsManager.getProxyHost(),Integer.parseInt(SettingsManager.getProxyPort()));
			String encoded = new String(
					Base64.encode(new String(
							SettingsManager.getProxyUser() + ":"
									+ SettingsManager.getProxyPassword())
							.getBytes()));

			get.addRequestHeader("Proxy-Authorization", "Basic "
					+ encoded);
		}

		try {
			client.executeMethod(get);
		} catch (ConnectException e) {
			out.close();
			throw new IOException("ConnectionException trying to GET "
					+ fileURL, e);
		}

		if (get.getStatusCode() < 200||get.getStatusCode()>299) {
			out.close();
			throw new FileNotFoundException("Server returned "
					+ get.getStatusCode());
		}
		BufferedInputStream bis = new BufferedInputStream(
				get.getResponseBodyAsStream());

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

	
	
	public static String postStringStream(String urlx, int batchWriteSize)
			throws Exception {
		String[] urli = urlx.split("[?]");
		URL obj = new URL(urli[0]);
		HttpsURLConnection con = null;
		if (SettingsManager.proxyEnabled()) {
			Proxy proxy = new Proxy(SettingsManager.getProxyType(),
					new InetSocketAddress(SettingsManager.getProxyHost(),
							Integer.parseInt(SettingsManager.getProxyPort())));
			System.out.println("Proxy Enabled.\nConnecting To: "
					+ SettingsManager.getProxyHost() + ": "
					+ Integer.parseInt(SettingsManager.getProxyPort()));
			Authenticator authenticator = new Authenticator() {

				@Override
				public PasswordAuthentication getPasswordAuthentication() {
					return (new PasswordAuthentication(
							SettingsManager.getProxyUser(), SettingsManager
									.getProxyPassword().toCharArray()));
				}
			};
			Authenticator.setDefault(authenticator);
			con = (HttpsURLConnection) obj.openConnection(proxy);
		} else {
			con = (HttpsURLConnection) obj.openConnection();
		}
		con.setRequestMethod("POST");
		con.setRequestProperty("User-Agent", "Mozilla/5.0");
		con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
		if (interrupt) {
			interrupt = false;
			return null;
		}

		// Send post request
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		if (interrupt) {
			interrupt = false;
			return null;
		}
		wr.writeBytes(urli[1]);
		wr.flush();
		wr.close();
		int responseCode = con.getResponseCode();
		if (responseCode < 200 || responseCode > 299)
			throw new Exception("Server Error: Code " + responseCode);

		if (interrupt) {
			interrupt = false;
			return null;
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(
				con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			if (interrupt) {
				in.close();
				interrupt = false;
				return null;
			}
			response.append(inputLine);
			response.append("\n");
		}
		in.close();

		return response.toString();

	}

	public static String getFileStreamWithName(String url, File f,
			int batchWriteSize) throws Exception {
		int responseCode = 1;
		URL obj;
		HttpURLConnection con;
		String nextCookie = null, titleRet = null;
		obj = new URL(url);
		do {
			if (SettingsManager.proxyEnabled()) {
				Proxy proxy = new Proxy(
						SettingsManager.getProxyType(),
						new InetSocketAddress(
								SettingsManager.getProxyHost(),
								Integer.parseInt(SettingsManager.getProxyPort())));
				Authenticator authenticator = new Authenticator() {

					@Override
					public PasswordAuthentication getPasswordAuthentication() {
						return (new PasswordAuthentication(
								SettingsManager.getProxyUser(), SettingsManager
										.getProxyPassword().toCharArray()));
					}
				};
				Authenticator.setDefault(authenticator);
				con = (HttpURLConnection) obj.openConnection(proxy);
			} else {
				con = (HttpURLConnection) obj.openConnection();
			}
			con.setRequestMethod("GET");
			con.setRequestProperty("User-Agent", "Mozilla/5.0");
			con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
			if (nextCookie != null)
				con.setRequestProperty("Cookie",
						"JSESSIONID=" + URLEncoder.encode(nextCookie, "UTF-8"));
			con.setInstanceFollowRedirects(false);

			con.setConnectTimeout(8000);
			con.connect();

			url = con.getHeaderField("Location");
			List<String> cookies = con.getHeaderFields().get("Set-Cookie");
			for (String s : cookies) {
				if (s.split("=")[0].equalsIgnoreCase("JSESSIONID"))
					nextCookie = s.split("=")[1];

			}
			List<String> name = con.getHeaderFields()
					.get("Content-Disposition");
			if (name != null) {
				for (String s : name) {
					if (s.contains("filename="))
						titleRet = s.split("=\"")[1].split("\"")[0];
				}
			}

			/*
			 * open a new connection and get the content for the URL
			 * newLocationHeader
			 */
			/* ... */

			responseCode = con.getResponseCode();
			/* do it until you get some code that is not a redirection */

		} while ((responseCode / 100) == 3);/* codes 3XX are redirections */

		if (responseCode < 200 || responseCode > 299)
			throw new Exception("Server Error: Code " + responseCode);

		InputStream in = con.getInputStream();
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

	public static BufferedImage getImageStream(String fileURL,
			int batchWriteSize) throws IOException {
		ImgRequest img = RequestCache.ImageR.get(fileURL);
		if (img != null)
			return img.getImage();

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		GetMethod get = new GetMethod(fileURL);
		HttpClient client = new HttpClient();
		HttpClientParams params = client.getParams();
		params.setSoTimeout((8000));
		client.setParams(params);

		proxySettings(client, get);

		try {
			client.executeMethod(get);
		} catch (ConnectException e) {
			out.close();
			throw new IOException("ConnectionException trying to GET "
					+ fileURL, e);
		}

		if (get.getStatusCode() < 200 || get.getStatusCode() > 299) {
			out.close();
			throw new FileNotFoundException("Server returned "
					+ get.getStatusCode());
		}
		BufferedInputStream bis = new BufferedInputStream(
				get.getResponseBodyAsStream());

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
			RequestCache.ImageR.add(fileURL, image);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return image;
	}

	public static void getFileStream(String fileURL, File f, int batchWriteSize)
			throws IOException {
		OutputStream out = new FileOutputStream(f);
		GetMethod get = new GetMethod(fileURL);
		HttpClient client = new HttpClient();
		HttpClientParams params = client.getParams();
		params.setSoTimeout((8000));
		client.setParams(params);

		proxySettings(client, get);

		try {
			client.executeMethod(get);
		} catch (ConnectException e) {
			out.close();
			throw new IOException("ConnectionException trying to GET "
					+ fileURL, e);
		}

		if (get.getStatusCode() < 200 || get.getStatusCode() > 299) {
			out.close();
			throw new FileNotFoundException("Server returned "
					+ get.getStatusCode());
		}
		BufferedInputStream bis = new BufferedInputStream(
				get.getResponseBodyAsStream());

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

	public static void interrupt() {
		interrupt = true;
	}
}
