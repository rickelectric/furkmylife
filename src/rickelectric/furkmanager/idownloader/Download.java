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

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.xerces.impl.dv.util.Base64;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;

public class Download extends Observable implements Runnable, Serializable {
	private static final long serialVersionUID = 1L;

	protected static final int MAX_BUFFER_SIZE = 8192;

	public static final String STATUSES[] = { "Downloading", "Paused",
			"Complete", "Cancelled", "Error", "Suspended" , "Idle" };

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

	public Download(String name, URL url, String saveDir) {
		this.url = url;
		this.saveDir = saveDir;
		this.name = name;

		size = -1;
		downloaded = 0;
		status = IDLE;

		errCount = 0;
	}

	public void run() {
		try {
			apacheDownload();
		} catch (IOException e) {
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

	public void apacheDownload() throws IOException {
		String fileURL = url.toExternalForm();
		RandomAccessFile file = null;
		FileChannel channel;
		FileLock lock;
		InputStream stream = null;
		OutputStream out = new ByteArrayOutputStream();
		GetMethod get = new GetMethod(fileURL);
		HttpClient client = new HttpClient();
		HttpClientParams params = client.getParams();
		params.setSoTimeout((int) (8000));
		client.setParams(params);

		if (SettingsManager.proxyEnabled()) {
			client.getHostConfiguration().setProxy(
					SettingsManager.getProxyHost(),
					Integer.parseInt(SettingsManager.getProxyPort()));
			String encoded = new String(
					Base64.encode(new String(
							SettingsManager.getProxyUser() + ":"
									+ SettingsManager.getProxyPassword())
							.getBytes()));

			get.addRequestHeader("Proxy-Authorization", "Basic " + encoded);
		}

		if(dwFile!=null && dwFile.exists()){
			get.addRequestHeader("Range", "bytes=" + downloaded + "-");
			if (lastModified != null)
				get.addRequestHeader("If-Range", lastModified);
		}
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
		
		try{lastModified = get.getResponseHeader("Last-Modified").getValue();}catch(Exception e){lastModified=null;}

		String titleRet = null;
		Header[] name = get.getResponseHeaders("Content-Disposition");
		if (name != null) {
			for (Header h : name) {
				String s = h.getValue();
				if (s.contains("filename="))
					titleRet = s.split("=\"")[1].split("\"")[0];
			}
		}

		int contentLength = (int) get.getResponseContentLength();
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
		channel=file.getChannel();
		lock=channel.lock();
		
		file.seek(downloaded);

		// System.out.println("Get InputStream");
		stream = get.getResponseBodyAsStream();
		status = DOWNLOADING;

		while (status == DOWNLOADING){
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
			FurkManager.trayAlert(FurkManager.TRAY_INFO, "Download Complete",
					"Your download of file '" + dwFile.getName()
							+ "' is complete", new Runnable() {
						public void run() {
							UtilBox.openFileLocation(dwFile);
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
	
	public JPopupMenu popup(){
		return new ContextMenu();
	}

	class ContextMenu extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;

		public JMenuItem pause, start, resume, cancel,clear,open,dir;

		public ContextMenu() {
			super();
			
			start = new JMenuItem("Start");
			start.addActionListener(this);
			
			open = new JMenuItem("Open File");
			open.addActionListener(this);
			
			dir=new JMenuItem("Open File Location");
			dir.addActionListener(this);

			pause = new JMenuItem("Pause");
			pause.addActionListener(this);

			resume = new JMenuItem("Resume");
			resume.addActionListener(this);

			cancel = new JMenuItem("Cancel");
			cancel.addActionListener(this);
			
			clear = new JMenuItem("Clear");
			clear.addActionListener(this);

			if(getStatus()==IDLE) add(start);
			if(getStatus()==PAUSED) add(resume);
			if(getStatus()==DOWNLOADING) add(pause);
			if(getStatus()!=CANCELLED&&getStatus()!=COMPLETE) add(cancel);
			else add(clear);
			
			if(getStatus()==COMPLETE){
				add(open);
				add(dir);
				add(clear);
			}
		}

		public void actionPerformed(ActionEvent e) {
			Object src=e.getSource();
			if(src==start||src==resume) resume();
			else if(src==pause) pause();
			else if(src==cancel) cancel();
			else if(src==clear) DownloadManager.actionClear(Download.this);
			else if(src==open){
				try {
					Desktop.getDesktop().open(getFile());
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			else if(src==dir){
				UtilBox.openFileLocation(dwFile);
			}
		}

	}

}
