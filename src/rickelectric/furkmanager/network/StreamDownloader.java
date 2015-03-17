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
import java.security.cert.X509Certificate;

import javax.imageio.ImageIO;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectionKeepAliveStrategy;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import rickelectric.UtilBox;
import rickelectric.furkmanager.utils.ProxySettings;
import rickelectric.furkmanager.utils.SettingsManager;

public class StreamDownloader {

	private static StreamDownloader thisInstance;
	private DefaultHttpClient client;
	private int batchMult;

	public static synchronized StreamDownloader getInstance() {
		if (thisInstance == null) {
			thisInstance = new StreamDownloader();
		}
		return thisInstance;
	}

	private StreamDownloader() {
		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setSoTimeout(params, 18000);
		HttpConnectionParams.setConnectionTimeout(params, 10000);
		HttpConnectionParams.setTcpNoDelay(params, true);
		params.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
				HttpVersion.HTTP_1_1);
		try{
			final SSLContext sslCtx;
			sslCtx = SSLContext.getInstance("SSL");
			CustomTrustManagerHostnameVerifier mgr = new CustomTrustManagerHostnameVerifier();
			sslCtx.init(null, new TrustManager[] { mgr }, null);
	
			X509HostnameVerifier verifier = mgr;
			final SSLSocketFactory socketFactory = new SSLSocketFactory(sslCtx,
					verifier);
			final SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("https", 443, socketFactory));
	
			final PoolingClientConnectionManager cm = new PoolingClientConnectionManager(
					registry);
			cm.setMaxTotal(100);
			cm.setDefaultMaxPerRoute(50);
			client = new DefaultHttpClient(cm,params);
		}catch(Exception e){
			client = new DefaultHttpClient(params);
		}
		
		client.setKeepAliveStrategy(new ConnectionKeepAliveStrategy() {
			@Override
			public long getKeepAliveDuration(HttpResponse hr, HttpContext hc) {
				return 0;
			}
		});
		this.batchMult=4;
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
		return getStringStream(url, 32*1024);
	}

	public String getStringStream(String url, int batchWriteSize)
			throws Exception {
		return getStringStream(url, batchWriteSize, SettingsManager
				.getInstance().timeout());
	}

	public String getStringStream(String url, int batchWriteSize, int timeout)
			throws Exception {
		URL urli = new URL(url);
		
		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);

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

	class CustomTrustManagerHostnameVerifier implements X509TrustManager, X509HostnameVerifier {
		@Override
		public void checkClientTrusted(X509Certificate[] cert, String authType) {
		}

		@Override
		public void checkServerTrusted(X509Certificate[] cert, String authType) {
		}

		@Override
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
		
		@Override
		public void verify(String string, SSLSocket ssls)
				throws IOException {
		}

		@Override
		public void verify(String string, X509Certificate xc)
				throws SSLException {
		}

		@Override
		public void verify(String string, String[] strings,
				String[] strings1) throws SSLException {
		}

		@Override
		public boolean verify(String string, SSLSession ssls) {
			return true;
		}
	}

	public String postMultipartStream(String url, MultipartEntity entity)
			throws Exception {
		URL urli = new URL(url);

		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);
		
		HttpPost post = new HttpPost(urli.toURI());
		post.getParams().setBooleanParameter(
				CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
		post.setEntity(entity);

		System.err.println("Uploading '"
				+ url
				+ "' : Size="
				+ UtilBox.getInstance().byteSizeToString(
						entity.getContentLength()));
		HttpResponse resp = client.execute(post);
		int response = resp.getStatusLine().getStatusCode();

		if (response >= 200 && response <= 299) {
			
		} else if(response>=300 && response<=399){
			Header[] loc = resp.getHeaders("Location");
			if (loc.length > 0)
				System.out.println("New Location = "+loc[0].getValue());
		}else {
			throw new IOException("HTTP Response " + response);
		}

		System.err.println("Downloading '"
				+ url
				+ "' : Size="
				+ UtilBox.getInstance().byteSizeToString(
						resp.getEntity().getContentLength()));
		long i = System.currentTimeMillis();
		// String r = EntityUtils.toString(resp.getEntity());

		String r=readEntityStream(resp.getEntity());
		long t = System.currentTimeMillis();
		System.err.println("Download Complete (time=" + ((t - i) / 1000f)
				+ "s)\n");
		return r;
	}

	private String readEntityStream(HttpEntity entity) throws IllegalStateException, IOException { 
		BufferedInputStream bis = new BufferedInputStream(entity
				.getContent());
		ByteArrayOutputStream os = new ByteArrayOutputStream((int)entity.getContentLength());
		int batchWriteSize = batchMult * 1024;
		byte[] b = new byte[256*1024];
		int bytesRead = bis.read(b, 0, batchWriteSize);
		while (bytesRead != -1) {
			if(batchMult>1&& bytesRead<(batchWriteSize/2)){
				batchMult = batchMult/2;
				batchWriteSize = batchMult * 1024;
			}
			if(batchMult<256&&bytesRead==batchWriteSize){
				batchMult = batchMult*2;
				batchWriteSize = batchMult * 1024;
			}
			System.err.println("Read "+bytesRead+" bytes");
			os.write(b, 0, bytesRead);
			bytesRead = bis.read(b, 0, batchWriteSize);
		}
		bis.close();
		String r = os.toString();
		os.close();
		return r;
		
	}

	public String postStringStream(String urlx, int batchWriteSize)
			throws Exception {
		String[] urli = urlx.split("[?]");
		URL url = new URL(urli[0]);

		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);

		HttpPost post = new HttpPost(url.toURI());

		if (urli.length > 1) {
			post.setHeader("Content-Type", "application/x-www-form-urlencoded");
			post.setEntity(new StringEntity(urli[1]));
		}
		HttpResponse resp = client.execute(post);
		int response = resp.getStatusLine().getStatusCode();

		if (response < 200 || response > 299)
			throw new IOException("HTTP Response {" + urlx + "} = " + response);

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

	public String getFileStreamWithName(String url, File f, int batchWriteSize)
			throws Exception {
		String nextCookie = null, titleRet = null;
		
		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);

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

		} while ((resp.getStatusLine().getStatusCode() / 100) == 3);
		/* 3xx codes are redirections */

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

		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);

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
		
		ProxySettings settings = SettingsManager.getInstance()
				.getProxySettings();
		settings.applyProxyTo(client);

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
