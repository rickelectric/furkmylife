package rickelectric.furkmanager.idownloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;

public class HTTP_Download extends FileDownload implements Serializable {
	private static final long serialVersionUID = 1L;

	public HTTP_Download(String name, URL url, String saveDir) {
		super(name, url, saveDir);
	}

	@Override
	public void run() {
		try {
			apacheDownload();
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}

	// Download file.
	public void run_() {
		RandomAccessFile file = null;
		InputStream stream = null;
		HttpURLConnection connection = null;
		try {
			UtilBox.applyProxySettings();

			// Open connection to URL with proxy conditional.
			if (SettingsManager.proxyEnabled()) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
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
				connection = (HttpURLConnection) url.openConnection(proxy);
			} else {
				connection = (HttpURLConnection) url.openConnection();
			}

			// Specify what portion of file to download.
			connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
			if (lastModified != null)
				connection.setRequestProperty("If-Range", lastModified);
			/*
			 * if (SettingsManager.proxyEnabled()) { String encoded = new
			 * String( new sun.misc.BASE64Encoder().encode(new String(
			 * SettingsManager.getProxyUser() + ":" +
			 * SettingsManager.getProxyPassword()) .getBytes()));
			 * 
			 * connection.setRequestProperty("Proxy-Authorization", "Basic " +
			 * encoded); }
			 */

			// System.out.println("Going to make connection");

			// Connect to server.
			connection.connect();

			int responseCode = connection.getResponseCode();

			if (responseCode == 200 || responseCode == 206) {
				error();
			}

			lastModified = connection.getHeaderField("Last-Modified");

			String titleRet = null;
			List<String> name = connection.getHeaderFields().get(
					"Content-Disposition");
			if (name != null) {
				for (String s : name) {
					if (s.contains("filename="))
						titleRet = s.split("=\"")[1].split("\"")[0];
				}
			}

			int contentLength = connection.getContentLength();
			if (contentLength < 1) {
				error();
			}

			// Set the size for this download if it hasn't been already set.
			if (size == -1) {
				size = contentLength;
				stateChanged();
			}

			// Open file and seek to the end of it.
			if (dwFile == null)
				dwFile = new File(saveDir, titleRet == null ? getFileName(url)
						: titleRet);
			if (this.name == null)
				this.name = titleRet;
			file = new RandomAccessFile(dwFile, "rw");
			file.seek(downloaded);

			// System.out.println("Get InputStream");
			stream = connection.getInputStream();
			status = DOWNLOADING;

			while (status == DOWNLOADING) {
				byte buffer[];
				float finalSize = size - downloaded;
				if (finalSize > MAX_BUFFER_SIZE) {
					buffer = new byte[(int) finalSize];
				} else {
					buffer = new byte[MAX_BUFFER_SIZE];
				}
				int read = stream.read(buffer);
				if (read == -1)
					break;

				file.write(buffer, 0, read);
				downloaded += read;
				stateChanged();
			}

			/*
			 * Change status to complete if this point was reached because
			 * downloading has finished.
			 */
			if (status == DOWNLOADING) {
				status = COMPLETE;
				FurkManager.trayAlert(FurkManager.TRAY_INFO,
						"Download Complete",
						"Your download of file '" + dwFile.getName()
								+ "' is complete", new Runnable() {
							@Override
							public void run() {
								UtilBox.openFileLocation(dwFile);
							}
						});
				stateChanged();
			}
		} catch (Exception e) {
			// System.out.println("Error=" + e);
			e.printStackTrace();
			error();
		} finally {
			// Close file.
			if (file != null) {
				try {
					// Complete the file
					file.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
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
	}

}
