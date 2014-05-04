package rickelectric.furkmanager.player;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import rickelectric.furkmanager.models.MInputStream;
import rickelectric.furkmanager.network.StreamDownloader;

public class MP3Player implements Runnable {
	
	public static final int IDLE = -1;
	public static final int PLAYING = 0;
	public static final int PAUSED = 1;
	public static final int STOPPED = 2;
	
	private int state=IDLE;
	
	private MP3Player mp3;

	private Player player;
	private InputStream stream;
	private long length;
	private static Thread run;
	
	private PlayerWin win=null;

	public static void main(String[] args) throws Exception {
		MInputStream is = StreamDownloader
				.getInputStream("http://a.tumblr.com/tumblr_msqmufZjda1rswe4ko1.mp3");
		final MP3Player p = new MP3Player(is);
		p.show();
		
	}

	public MP3Player(MInputStream is) throws JavaLayerException {
		if(is==null) return;
		this.stream=is.getIs();
		this.length=is.getLength();
		player = new Player(is.getIs());
		this.state=IDLE;
		mp3=this;
	}
	
	public MP3Player(File f) throws IOException, JavaLayerException{
		FileInputStream is=new FileInputStream(f);
		this.stream=is;
		player=new Player(is);
		this.state=IDLE;
		mp3=this;
	}

	@Override
	public void run() {
		try {
			stream.mark(100000);
			player.play();
			state=PLAYING;
			if(win!=null) win.update();
		} catch (JavaLayerException e) {
			//e.printStackTrace();
		}
	}
	
	public void play() {
		if(run==null) run=new Thread(this);
		if(state==IDLE) run.start();
		else if(state==PAUSED) resume();
	}
	
	public void ff(long bytes){
		try {
			stream.skip(bytes);
			if(win!=null) win.update();
		} catch (IOException e) {}
	}
	
	public void rew(){
		try {
			stream.reset();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("deprecation")
	public void pause() {
		if(run==null) return;
		run.suspend();
		state=PAUSED;
	}
	
	@SuppressWarnings("deprecation")
	public void resume(){
		if(run==null) return;
		run.resume();
		state=PLAYING;
	}

	public int getPosition() {
		return player.getPosition();
	}

	public void stop() {
		try {
			player.close();
			stream.close();
			
		} catch (IOException e) {}
		state=STOPPED;
		win.dispose();
	}
	
	public int getState(){
		return state;
	}
	
	public long getLength(){
		return length;
	}
	
	public void show(){
		new Thread(new Runnable(){
			public void run(){
				if(win==null) win=new PlayerWin(mp3);
				win.setVisible(true);
				win.update();
			}
		}).start();
	}
}
