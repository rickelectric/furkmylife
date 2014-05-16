package rickelectric.furkmanager.utils;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import javax.swing.JOptionPane;

public class Downloader implements Runnable{
	
	URL url;
	static boolean terminate;
	static ExecutorService e=Executors.newCachedThreadPool();
	
	Runnable term=new Runnable(){

		public void run() {
			
		}
		
	};
	
	public Downloader(String imageUrl) throws MalformedURLException,IOException{
		terminate=false;
		url=new URL(imageUrl);
		HttpURLConnection connection=(HttpURLConnection)url.openConnection();
		downloaded=0;
		completed=false;
		connection.connect();
		if (connection.getResponseCode() / 100 != 2) {
            throw new RuntimeException("URL Connection Error.");
        }
		size=connection.getContentLength();
		if(size<1) throw new RuntimeException("Invalid File");
	}
	
	private boolean downloading=false;
	private boolean completed=false;
	private int size=-1;
	private int downloaded=0;
	
	public int getSize(){
		return size;
	}
	
	public int getDownloaded(){
		return downloaded;
	}
	
	private void isDownloading(boolean is){
		downloading=is;
	}
	
	public boolean isDownloading(){
		return downloading;
	}
	
	public boolean isComplete(){
		return completed;
	}
	
	public static void stop(){
		terminate=true;
	}
	
	private String destinationFile=null;
	public void saveImage(final String destinationFile){
		isDownloading(true);
		this.destinationFile=destinationFile;
		e.execute(this);
	}
	
	public void run(){
		try{
			InputStream is = url.openStream();
			OutputStream os = new FileOutputStream(destinationFile);
	
			byte[] b = new byte[8];
			int length;
			
			while((length=is.read(b))!=-1){
				if(terminate){
					is.close();
					os.close();
					throw new RuntimeException("Cancelled");
				}
				os.write(b, 0, length);
				downloaded+=8;
			}
			completed=true;
			is.close();
			os.close();
			isDownloading(false);
		}catch(IOException e){
			e.printStackTrace();
			isDownloading(false);
			JOptionPane.showMessageDialog(null, "Error: Download Failed. Connection Interrupted", "Error",JOptionPane.ERROR_MESSAGE);
		}
		catch(RuntimeException e){
			if(e.getMessage().equals("Cancelled")) JOptionPane.showMessageDialog(null, "Download Cancelled", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
			isDownloading(false);
		}
	}

}