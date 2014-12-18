package rickelectric.furkmanager.idownloader;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.net.ConnectException;
import java.net.URL;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.Observable;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import rickelectric.UtilBox;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.utils.ProxySettings;
import rickelectric.furkmanager.utils.SettingsManager;

public class FileDownload extends Observable implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;

	protected static final int MAX_BUFFER_SIZE = 8192;

	public static final String STATUSES[] = { "Downloading", "Paused",
			"Complete", "Cancelled", "Error", "Suspended", "Idle" };

	public static final int DOWNLOADING = 0;
	public static final int PAUSED = 1;
	public static final int COMPLETE = 2;
	public static final int CANCELLED = 3;
	public static final int ERROR = 4;
	public static final int SUSPENDED = 5;
	public static final int IDLE = 6;

	protected URL url; // download URL
	protected String saveDir; // dir to save
	protected int size; // size of download in bytes
	protected int downloaded; // number of bytes downloaded
	protected int status; // current status of download

	protected File dwFile = null;
	protected String lastModified = null;

	protected int errCount;

	protected String name;

	public FileDownload(String name, URL url, String saveDir) {
		this.url = url;
		this.saveDir = saveDir;
		this.name = name;

		size = -1;
		downloaded = 0;
		status = IDLE;

		errCount = 0;
	}

	@Override
	public void run() {
		try {
			apacheDownload();
		} catch (IOException e) {
		} catch (Exception e) {
		}
	}

	public File getFile() {
		return dwFile;
	}

	public String getName() {
		return name == null ? "N/A" : name;
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

	public void suspend() {
		status = SUSPENDED;
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
		if (errCount < 2)
			resume();
	}

	private void download() {
		Thread thread = new Thread(this);
		thread.start();
	}

	// Get file name portion of URL.
	public String getFileName(URL url) {
		String fileName = url.getFile();
		return fileName.substring(fileName.lastIndexOf('/') + 1);
	}

	protected void stateChanged() {
		setChanged();
		notifyObservers();
	}

	public void apacheDownload() throws Exception {
		stateChanged();
		RandomAccessFile file = null;
		FileChannel channel;
		FileLock lock;
		InputStream stream = null;
		OutputStream out = new ByteArrayOutputStream();

		DefaultHttpClient client = new DefaultHttpClient();

		ProxySettings settings = SettingsManager.getInstance().getProxySettings();
		settings.applyProxyTo(client);
		
		client.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 8000);
		client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 12000);

		HttpResponse resp;
		HttpUriRequest request = new HttpGet(url.toURI());

		if (dwFile != null && dwFile.exists()) {
			request.addHeader("Range", "bytes=" + downloaded + "-");
			if (lastModified != null) {
				request.addHeader("If-Range", lastModified);
			}
		}
		try {
			resp = client.execute(request);
		} catch (ConnectException e) {
			e.printStackTrace();
			out.close();
			throw new IOException("ConnectionException trying to GET "
					+ url.toExternalForm(), e);
		}

		if (resp.getStatusLine().getStatusCode() / 100 != 2) {
			throw new FileNotFoundException("Server returned "
					+ resp.getStatusLine().getStatusCode());
		}

		try {
			lastModified = resp.getHeaders("Last-Modified")[0].getValue();
		} catch (Exception e) {
			lastModified = null;
		}

		String titleRet = null;
		Header[] name = resp.getHeaders("Content-Disposition");
		if (name.length > 0) {
			for (Header n : name) {
				if (n.getValue().contains("filename=")) {
					titleRet = n.getValue().split("=\"")[1].split("\"")[0];
					break;
				}
			}
		}

		int contentLength = (int) resp.getEntity().getContentLength();
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
		channel = file.getChannel();
		lock = channel.lock();

		if (downloaded > 0) {
			file.seek(downloaded);
		}

		stream = resp.getEntity().getContent();
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
			Thread.sleep(10);
		}

		/*
		 * Change status to complete if this point was reached because
		 * downloading has finished.
		 */
		if (status == DOWNLOADING) {
			status = COMPLETE;
			FurkManager.trayAlert(FurkManager.TRAY_INFO, "Download Complete",
					"Your download of file '" + dwFile.getName()
							+ "' is complete", new Runnable() {
						@Override
						public void run() {
							UtilBox.getInstance().openFileLocation(dwFile);
						}
					});
			stateChanged();
		}
		// Close file.
		if (file != null) {
			try {
				// Complete the file
				lock.release();
				channel.close();
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

	public JPopupMenu popup() {
		return new ContextMenu();
	}

	class ContextMenu extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;

		public JMenuItem pause, start, resume, cancel, clear, open, dir;

		public ContextMenu() {
			super();

			start = new JMenuItem("Start");
			start.addActionListener(this);

			open = new JMenuItem("Open File");
			open.addActionListener(this);

			dir = new JMenuItem("Open File Location");
			dir.addActionListener(this);

			pause = new JMenuItem("Pause");
			pause.addActionListener(this);

			resume = new JMenuItem("Resume");
			resume.addActionListener(this);

			cancel = new JMenuItem("Cancel");
			cancel.addActionListener(this);

			clear = new JMenuItem("Clear");
			clear.addActionListener(this);

			if (getStatus() == IDLE)
				add(start);
			if (getStatus() == PAUSED)
				add(resume);
			if (getStatus() == DOWNLOADING)
				add(pause);
			if (getStatus() != CANCELLED && getStatus() != COMPLETE)
				add(cancel);
			else
				add(clear);

			if (getStatus() == COMPLETE) {
				add(open);
				add(dir);
				add(clear);
			}
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src == start || src == resume)
				resume();
			else if (src == pause)
				pause();
			else if (src == cancel)
				cancel();
			else if (src == clear)
				DownloadManager.actionClear(FileDownload.this);
			else if (src == open) {
				try {
					Desktop.getDesktop().open(getFile());
				} catch (IOException e1) {
					FurkManager.trayAlert(FurkManager.TRAY_ERROR,
							"File Not Found",
							"The Downloaded File Has Been Moved Or Deleted",
							null);
				}
			} else if (src == dir) {
				UtilBox.getInstance().openFileLocation(dwFile);
			}
		}

	}

}
