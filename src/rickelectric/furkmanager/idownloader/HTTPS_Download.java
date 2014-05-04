package rickelectric.furkmanager.idownloader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.Authenticator;
import java.net.InetSocketAddress;
import java.net.PasswordAuthentication;
import java.net.Proxy;
import java.net.URL;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;

public class HTTPS_Download extends Download implements Serializable {
	private static final long serialVersionUID = 1L;

	public HTTPS_Download(String name, URL url, String saveDir) {
		super(name, url, saveDir);
	}
	
	public void run(){
		try {apacheDownload();}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void run_() {
		RandomAccessFile file = null;
		InputStream stream = null;
		try {
			UtilBox.applyProxySettings();
			HttpsURLConnection connection = null;
			// Open connection to URL with proxy conditional.
			if (SettingsManager.proxyEnabled()) {
				Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(
						SettingsManager.getProxyHost(),
						Integer.parseInt(SettingsManager.getProxyPort())));
				Authenticator authenticator = new Authenticator() {

					public PasswordAuthentication getPasswordAuthentication() {
						return (new PasswordAuthentication(
								SettingsManager.getProxyUser(), SettingsManager
										.getProxyPassword().toCharArray()));
					}
				};
				Authenticator.setDefault(authenticator);
				connection = (HttpsURLConnection) url.openConnection(proxy);
			} else {
				connection = (HttpsURLConnection) url.openConnection();
			}
			// Specify what portion of file to download.
			connection.setRequestProperty("Range", "bytes=" + downloaded + "-");
			if (lastModified != null)
				connection.setRequestProperty("If-Range", lastModified);

			/*
			if (SettingsManager.proxyEnabled()) {
				String encoded = new String(
						new sun.misc.BASE64Encoder().encode(new String(
								SettingsManager.getProxyUser() + ":"
										+ SettingsManager.getProxyPassword())
								.getBytes()));

				connection.setRequestProperty("Proxy-Authorization", "Basic "
						+ encoded);
			}
			*/
			
			System.out.println("Going to make connection");

			// Connect to server.
			connection.connect();
			System.out.println("Connected!");

			int responseCode = connection.getResponseCode();
			System.out.println("Response code from server=" + responseCode);

			// Make sure response code is in the 200 range.
			// 200 - no partial download
			// 206 - supports resume

			// if (responseCode / 100 != 2) {

			if (responseCode == 200 || responseCode == 206) {
				error();
			}

			lastModified = connection.getHeaderField("Last-Modified");

			// Check for valid content length.
			System.out.println("Content length="
					+ connection.getContentLength());

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
			if(this.name==null) this.name=titleRet;
			stateChanged();
			file = new RandomAccessFile(dwFile, "rw");
			file.seek(downloaded);

			System.out.println("Get InputStream");
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
							public void run() {
								UtilBox.openFileLocation(dwFile);
							}
						});
				stateChanged();
			}
		} catch (Exception e) {
			System.out.println("Error=" + e);
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
