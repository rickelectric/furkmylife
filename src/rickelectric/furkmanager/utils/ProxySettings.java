package rickelectric.furkmanager.utils;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.auth.params.AuthPNames;
import org.apache.http.client.params.AuthPolicy;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeSocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import rickelectric.furkmanager.data.DefaultParams;

public class ProxySettings implements Serializable {

	private static final long serialVersionUID = 53441695L;

	public enum Type {
		NONE, HTTP, SOCKS, NTLM;
	}

	public Type proxyType;

	public String HOST = "127.0.0.1";
	public int PORT = 9150;
	public String DOMAIN = "";
	public String USER = null;
	public String PASS = null;

	protected ProxySettings() {
		proxyType = Type.NONE;
	}

	public void applyProxyTo(DefaultHttpClient client) {
		if (proxyType != Type.NONE) {
			switch (proxyType) {
			case NTLM:
				HttpHost proxy = new HttpHost(HOST,PORT);
				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);

				List<String> authpref = new ArrayList<String>();
				authpref.add(AuthPolicy.NTLM);
				client.getParams().setParameter(AuthPNames.TARGET_AUTH_PREF, authpref);
				NTCredentials creds = new NTCredentials(USER,PASS,
						"localhost", DOMAIN);
				client.getCredentialsProvider().setCredentials(AuthScope.ANY, creds);
				break;
			case HTTP:
				HttpHost host = new HttpHost(HOST, PORT);
				if (USER != null) {
					client.getCredentialsProvider().setCredentials(
							new AuthScope(HOST, PORT),
							new UsernamePasswordCredentials(USER, PASS));
				}
				client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
						host);
				break;
			case SOCKS:
				client.getParams().setParameter("socks.host", HOST);
				client.getParams().setParameter("socks.port", PORT);
				client.getConnectionManager()
						.getSchemeRegistry()
						.register(
								new Scheme("http", 80,
										new MySchemeSocketFactory()));
				client.getConnectionManager()
						.getSchemeRegistry()
						.register(
								new Scheme("https", 80,
										new MySchemeSocketFactory()));
				try {
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (USER != null) {
					client.getCredentialsProvider().setCredentials(
							new AuthScope(HOST, PORT),
							new UsernamePasswordCredentials(USER, PASS));
				}
				break;
			default:
				System.out.println("No Proxy Applied");
				break;
			}

		}
	}

	public String[] toVlcjArgs() {
		String defArg = DefaultParams.DEF_MEDIA_OPTIONS;
		ArrayList<String> args = new ArrayList<String>();
		args.add(defArg);
		switch (proxyType) {
		case HTTP:
			args.add("--http-proxy=http://" + (USER != null && !USER.equals("") ? USER + "@" : "")
					+ HOST + ":" + PORT + "/");
			args.add("--https-proxy=http://" + (USER != null  && !USER.equals("") ? USER + "@" : "")
					+ HOST + ":" + PORT + "/");
			if (USER != null  && !USER.equals(""))
				args.add("--http-proxy-pwd=" + PASS);
			break;
		case SOCKS:
			args.add("--socks=" + HOST + ":" + PORT);
			if (USER != null && !USER.equals("")) {
				args.add("--socks-user=" + USER);
				args.add("--socks-pwd=");
			}
			break;
		default:
			break;
		}
		return args.toArray(new String[]{});
	}
}

class SocksSSLSocketFactory extends SSLSocketFactory {

	public SocksSSLSocketFactory(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {
		super(truststore);
	}

	public static final String SOCKS_PROXY_HOST = "socks.proxyHost";
	public static final String SOCKS_PROXY_PORT = "socks.proxyPort";

	@Override
	public Socket createSocket(final HttpParams params) {
		String proxyHost = (String) params.getParameter(SOCKS_PROXY_HOST);
		int proxyPort = (Integer) params.getParameter(SOCKS_PROXY_PORT);

		InetSocketAddress socksAddr = new InetSocketAddress(proxyHost,
				proxyPort);
		Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksAddr);
		Socket socket = new Socket(proxy);

		return socket;
	}

}

class MySchemeSocketFactory implements SchemeSocketFactory {

	public Socket createSocket(final HttpParams params) throws IOException {
		if (params == null) {
			throw new IllegalArgumentException(
					"HTTP parameters may not be null");
		}
		String proxyHost = (String) params.getParameter("socks.host");
		Integer proxyPort = (Integer) params.getParameter("socks.port");

		InetSocketAddress socksaddr = new InetSocketAddress(proxyHost,
				proxyPort);
		Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
		return new Socket(proxy);
	}

	public Socket connectSocket(final Socket socket,
			final InetSocketAddress remoteAddress,
			final InetSocketAddress localAddress, final HttpParams params)
			throws IOException, UnknownHostException, ConnectTimeoutException {
		if (remoteAddress == null) {
			throw new IllegalArgumentException("Remote address may not be null");
		}
		if (params == null) {
			throw new IllegalArgumentException(
					"HTTP parameters may not be null");
		}
		Socket sock;
		if (socket != null) {
			sock = socket;
		} else {
			sock = createSocket(params);
		}
		if (localAddress != null) {
			sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
			sock.bind(localAddress);
		}
		int timeout = HttpConnectionParams.getConnectionTimeout(params);
		try {
			sock.connect(remoteAddress, timeout);
		} catch (SocketTimeoutException ex) {
			throw new ConnectTimeoutException("Connect to "
					+ remoteAddress.getHostName() + "/"
					+ remoteAddress.getAddress() + " timed out");
		}
		return sock;
	}

	public boolean isSecure(final Socket sock) throws IllegalArgumentException {
		return false;
	}

}

class MySSLSchemeSocketFactory extends SSLSocketFactory {

	public MySSLSchemeSocketFactory() {
		super(ctx(), SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

	}

	private static SSLContext ctx() {
		try {
			SSLContext ctx = SSLContext.getInstance("TLS");
			X509TrustManager tm = new X509TrustManager() {

				@Override
				public void checkClientTrusted(X509Certificate[] arg0,
						String arg1)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public void checkServerTrusted(X509Certificate[] arg0,
						String arg1)
						throws java.security.cert.CertificateException {
					// TODO Auto-generated method stub

				}

				@Override
				public X509Certificate[] getAcceptedIssuers() {
					// TODO Auto-generated method stub
					return null;
				}

			};
			ctx.init(null, new TrustManager[] { tm }, null);
			return ctx;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Socket createSocket(final HttpParams params) throws IOException {
		if (params == null) {
			throw new IllegalArgumentException(
					"HTTP parameters may not be null");
		}
		String proxyHost = (String) params.getParameter("socks.host");
		Integer proxyPort = (Integer) params.getParameter("socks.port");

		InetSocketAddress socksaddr = new InetSocketAddress(proxyHost,
				proxyPort);
		Proxy proxy = new Proxy(Proxy.Type.SOCKS, socksaddr);
		return new Socket(proxy);
	}

	public Socket connectSocket(final Socket socket,
			final InetSocketAddress remoteAddress,
			final InetSocketAddress localAddress, final HttpParams params)
			throws IOException, UnknownHostException, ConnectTimeoutException {
		if (remoteAddress == null) {
			throw new IllegalArgumentException("Remote address may not be null");
		}
		if (params == null) {
			throw new IllegalArgumentException(
					"HTTP parameters may not be null");
		}
		Socket sock;
		if (socket != null) {
			sock = socket;
		} else {
			sock = createSocket(params);
		}
		if (localAddress != null) {
			sock.setReuseAddress(HttpConnectionParams.getSoReuseaddr(params));
			sock.bind(localAddress);
		}
		int timeout = HttpConnectionParams.getConnectionTimeout(params);
		try {
			sock.connect(remoteAddress, timeout);
		} catch (SocketTimeoutException ex) {
			throw new ConnectTimeoutException("Connect to "
					+ remoteAddress.getHostName() + "/"
					+ remoteAddress.getAddress() + " timed out");
		}
		return sock;
	}

	public boolean isSecure(final Socket sock) throws IllegalArgumentException {
		return false;
	}

}