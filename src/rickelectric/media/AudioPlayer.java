package rickelectric.media;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JSlider;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.utils.SettingsManager;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class AudioPlayer extends Observable {

	private static AudioPlayer thisInstance = null;

	/**
	 * Observer Update Flag
	 */
	public static final int IDLE = 0, PLAYING = 1, PAUSED = 2, STOPPED = 3,
			POSITION = 4, VOLUME = 5, LENGTH = 6, META = 7, STATUS = 8;

	public static boolean dummy = false;

	private long time;
	private boolean active;

	private EmbeddedMediaPlayer player;
	private String playerString;

	private MouseListener sliderMouseListener;

	private String statusText;

	private Runnable playAction, stopAction;

	/**
	 * For Future Next/Back Functionality When Multiple Media TFiles Found.
	 */
	@SuppressWarnings("unused")
	private Runnable nextAction, prevAction;

	public static synchronized AudioPlayer getInstance() {
		if (thisInstance == null) {
			if (DefaultParams.getMediaPlayerFactory() != null) {
				thisInstance = new AudioPlayer();
			} else {
				System.err.println("Invalid Or Absent VLC");
				FurkManager
						.trayAlert(
								FurkManager.TRAY_ERROR,
								"VLC Not Found",
								"Suitable version of VLC Media Player was not found on your PC. \n"
										+ "Unable to stream music. \n"
										+ "If you wish to use these features, "
										+ "please install VLC Media Player ("
										+ (System.getProperty("os.arch")
												.contains("64") ? "64-bit version"
												: "32-bit version")
										+ ") and restart the application", null);
			}
		}
		return thisInstance;
	}

	public static synchronized void destroyInstance() {
		if (thisInstance.player.isPlaying())
			thisInstance.player.stop();
		thisInstance.deleteObservers();
		thisInstance.player.release();
		thisInstance = null;
		System.gc();
	}

	public boolean play(String mrl) {
		if (AudioPlayer.dummy)
			mrl = "C:\\Users\\Ionicle\\NewItems Temp Folder\\Russell Allen - The Great Divide (2014)\\10 - Bittersweet.mp3";
		if (thisInstance == null)
			getInstance();
		if (player.isPlaying())
			player.stop();
		String[] mediaOptions = SettingsManager.getInstance()
				.getProxySettings().toVlcjArgs();
		active = player.prepareMedia(mrl, mediaOptions);
		player.parseMedia();
		player.play();
		return active;
	}

	private AudioPlayer() {

		player = DefaultParams.getMediaPlayerFactory().newEmbeddedMediaPlayer();

		playerString = "";
		active = false;
		time = 100;

		sliderMouseListener = new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				Point p = e.getPoint();
				JSlider slider_position = (JSlider) e.getSource();
				float position = 1.0f * p.x / slider_position.getWidth();
				slider_position.setValue((int) (position * time));

				int pos = slider_position.getValue();
				float pm = 1.0f * pos / slider_position.getMaximum();
				player.setPosition(pm);
			}
		};

		statusText = "-:--:--";

		playAction = new Runnable() {
			@Override
			public void run() {
				if (player.isPlaying()) {
					player.pause();
				} else {
					player.play();
				}
			}
		};

		stopAction = new Runnable() {
			@Override
			public void run() {
				player.stop();

			}
		};

		addMediaListener();
	}

	public void setVolume(int volume) {
		if (volume >= 0 && volume <= 200)
			player.setVolume(volume);
		setChanged();
		notifyObservers(new Object[] { VOLUME, volume });
	}

	public void addObserver(Observer o) {
		if (o instanceof AudioObserver)
			addAudioObserver((AudioObserver) o);
	}

	public void addAudioObserver(AudioObserver o) {
		o.getPlayButton().setAction(playAction);
		o.getStopButton().setAction(stopAction);
		o.getPositionSlider().addMouseListener(sliderMouseListener);

		o.update(this, new Object[] { LENGTH, time });
		o.update(this, new Object[] { META, playerString });
		if (player.isPlaying()) {
			o.update(this, new Object[] { PLAYING });
		}
		o.update(this, new Object[] { VOLUME, this.getVolume() });
		if (player.isPlaying())
			o.update(this, new Object[] { META, playerString,
					player.getMediaMeta().getArtwork() });
		super.addObserver(o);
	}

	private void setStatus(String status) {
		statusText = status;
		setChanged();
		notifyObservers(new Object[] { STATUS, statusText });
	}

	private void addMediaListener() {
		player.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void buffering(MediaPlayer p, float newCache) {
				setStatus("Buffering: " + (int) newCache + "%");
				if (newCache == 100.0)
					p.play();
				else {
					if (p.isPlaying())
						p.pause();
				}
				active = true;
			}

			@Override
			public void mediaMetaChanged(MediaPlayer mediaPlayer, int metaType) {
				String artist = mediaPlayer.getMediaMeta().getArtist();
				String title = mediaPlayer.getMediaMeta().getTitle();

				BufferedImage artwork = mediaPlayer.getMediaMeta().getArtwork();

				playerString = (artist == null ? "" : (artist + " - ")) + (title == null ? ""
						: title);
				notifyObservers(new Object[] { META, playerString, artwork });
			}

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				setChanged();
				notifyObservers(new Object[] { VOLUME, getVolume() });
				setChanged();
				notifyObservers(new Object[] { META, playerString });
				setChanged();
				notifyObservers(new Object[] { PLAYING });
				active = true;
			}

			@Override
			public void paused(MediaPlayer mediaPlayer) {
				setChanged();
				notifyObservers(new Object[] { PAUSED });
				setStatus("Paused");
				active = true;
			}

			@Override
			public void stopped(MediaPlayer mediaPlayer) {
				setChanged();
				notifyObservers(new Object[] { STOPPED });
				setStatus("Stopped");
				active = false;
			}

			@Override
			public void finished(MediaPlayer mediaPlayer) {
				mediaPlayer.setPosition(0.0f);
				mediaPlayer.stop();
				active = false;
			}

			@Override
			public void timeChanged(MediaPlayer mediaPlayer, long newTime) {
				String sec = String.format("%02d", (int) (newTime / 1000) % 60);
				String min = String.format("%02d",
						(int) ((newTime / (1000 * 60)) % 60));
				String hr = String.format("%02d",
						(int) ((newTime / (1000 * 60 * 60)) % 24));
				String ps = hr + ":" + min + ":" + sec;
				setStatus("Streaming: " + ps);
			}

			@Override
			public void positionChanged(MediaPlayer mediaPlayer,
					float newPosition) {
				setChanged();
				notifyObservers(new Object[] { POSITION, newPosition * time });
			}

			@Override
			public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
				time = newLength;
				setChanged();
				notifyObservers(new Object[] { LENGTH, time });
			}

			@Override
			public void error(MediaPlayer mediaPlayer) {
				mediaPlayer.stop();
				setStatus("Stream Connection Error");
			}

		});
	}

	public String getPlayerString() {
		return playerString;
	}

	public void setPlayerString(String playerString) {
		this.playerString = playerString;
	}

	public boolean isActive() {
		return player.isPlaying() || active;
	}

	public void stop() {
		player.stop();
	}

	public int getVolume() {
		if (player.isPlaying() || active) {
			return player.getVolume();
		}
		return 100;

	}
}
