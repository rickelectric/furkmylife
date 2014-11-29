package rickelectric.furkmanager.player;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.data.DefaultParams;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.swingmods.JButtonLabel;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;

public class AudioPlayerPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static AudioPlayerPanel thisInstance = null;
	private String playerString;

	private static MediaPlayerFactory factory = null;
	private static EmbeddedMediaPlayer player = null;

	public static boolean dummy = false;

	private long time;

	private boolean active;

	private JLabel statusBar;

	private JButtonLabel btn_playpause;
	private JButtonLabel btn_stop;

	private JSlider slider_position;
	private JSlider slider_volume;

	private JPanel panel_controls;

	private AudioPlayerWin audioWin;

	public static synchronized AudioPlayerPanel getInstance() {
		if (thisInstance == null) {
			try {
				factory = DefaultParams.getMediaPlayerFactory();
				if(factory==null) return null;
				player = factory.newEmbeddedMediaPlayer();
				thisInstance = new AudioPlayerPanel();
			} catch (Exception e) {
				e.printStackTrace();
				FurkManager.trayAlert(FurkManager.TRAY_WARNING, "VLC Error",
						"Unable To Find Suitable VLC Version On Your PC", null);
			} catch (Error e) {
				e.printStackTrace();
				FurkManager
						.trayAlert(
								FurkManager.TRAY_ERROR,
								"VLC Not Found",
								"Unable To Find Suitable VLC Version On Your PC. \nYou Will Be Unable To Stream Music & Video. \nPlease Install VLC Media Player ("
										+ System.getProperty("os.arch")
										+ ") If You Wish To Use These Features.",
								null);
			}
		}
		return thisInstance;
	}

	public static void destroyInstance() {
		if (player.isPlaying())
			player.stop();
		player.release();
		player = null;
		factory.release();
		factory = null;
		thisInstance.setVisible(false);
		thisInstance = null;
		System.gc();
	}

	public boolean play(String mrl) {
		if (AudioPlayerPanel.dummy)
			mrl = "C:\\Users\\Ionicle\\NewItems Temp Folder\\Russell Allen - The Great Divide (2014)\\10 - Bittersweet.mp3";
		if (thisInstance == null)
			getInstance();
		if (player.isPlaying())
			player.stop();
		String[] mediaOptions = SettingsManager.getInstance().getProxySettings().toVlcjArgs();
		return active = player.playMedia(mrl, mediaOptions);
	}

	private AudioPlayerPanel() {
		setBorder(new BevelBorder(BevelBorder.RAISED));
		// setBounds(0, 0, 467, 108);
		setPreferredSize(new Dimension(467, 115));
		setSize(getPreferredSize());

		setLayout(new BorderLayout(0, 0));

		playerString = "";
		active = false;

		slider_position = new JSlider();
		add(slider_position, BorderLayout.CENTER);

		time = 100;
		slider_position.setMaximum((int) time);

		slider_position.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				Point p = e.getPoint();
				float position = 1.0f * p.x / slider_position.getWidth();
				slider_position.setValue((int) (position * time));

				int pos = slider_position.getValue();
				float pm = 1.0f * pos / slider_position.getMaximum();
				player.setPosition(pm);
			}

		});

		statusBar = new JLabel("0:00:00");
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(statusBar, BorderLayout.SOUTH);

		panel_controls = new JPanel();
		panel_controls.setBorder(new BevelBorder(BevelBorder.RAISED));

		FlowLayout flowLayout = (FlowLayout) panel_controls.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel_controls, BorderLayout.NORTH);

		btn_playpause = new JButtonLabel("", new Runnable() {
			@Override
			public void run() {
				if (player.isPlaying()) {
					player.pause();
				} else {
					player.play();
				}
			}
		});
		btn_playpause.setIcon(new ImageIcon(getClass().getResource(
				"media_play.png")));
		panel_controls.add(btn_playpause);

		btn_stop = new JButtonLabel("", new Runnable() {
			@Override
			public void run() {
				player.stop();

			}
		});
		btn_stop.setIcon(new ImageIcon(getClass().getResource("media_stop.png")));
		panel_controls.add(btn_stop);

		Component glue = Box.createGlue();
		glue.setPreferredSize(new Dimension(180, 0));
		panel_controls.add(glue);

		JLabel lblVolume = new JLabel("Volume: ");
		panel_controls.add(lblVolume);

		slider_volume = new JSlider();
		slider_volume.setMaximum(200);
		slider_volume.setMinimum(0);
		slider_volume.setValue(100);
		slider_volume.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int val = slider_volume.getValue();
				player.setVolume(val);
			}
		});
		slider_volume.setPreferredSize(new Dimension(100, 16));
		panel_controls.add(slider_volume);

		addMediaListener();

	}

	private void addMediaListener() {
		player.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void buffering(MediaPlayer p, float newCache) {
				statusBar.setText("Buffering: " + (int) newCache + "%");
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
				playerString = mediaPlayer.getMediaMeta().getArtist() + " - "
						+ mediaPlayer.getMediaMeta().getTitle();
			}

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				btn_playpause.setIcon(new ImageIcon(getClass().getResource(
						"media_pause.png")));
				active = true;
			}

			@Override
			public void paused(MediaPlayer mediaPlayer) {
				btn_playpause.setIcon(new ImageIcon(getClass().getResource(
						"media_play.png")));
				active = true;
			}

			@Override
			public void stopped(MediaPlayer mediaPlayer) {
				btn_playpause.setIcon(new ImageIcon(getClass().getResource(
						"media_play.png")));
				active = false;
			}

			@Override
			public void finished(MediaPlayer mediaPlayer) {
				btn_playpause.setIcon(new ImageIcon(getClass().getResource(
						"media_play.png")));
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
				statusBar.setText("Streaming: " + ps);
			}

			@Override
			public void positionChanged(MediaPlayer mediaPlayer,
					float newPosition) {
				slider_position.setValue((int) (newPosition * time));
			}

			@Override
			public void lengthChanged(MediaPlayer mediaPlayer, long newLength) {
				time = newLength;
				slider_position.setMaximum((int) time);
			}

			@Override
			public void error(MediaPlayer mediaPlayer) {
				statusBar.setText("Stream Connection Error");
				mediaPlayer.stop();
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

	public synchronized JFrame getAudioWin() {
		if (audioWin == null)
			audioWin = new AudioPlayerWin();
		audioWin.contentPaneSet();
		return audioWin;
	}

}

class AudioPlayerWin extends JFrame {
	private static final long serialVersionUID = 1L;

	public AudioPlayerWin() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				AudioPlayerPanel.getInstance().stop();
				dispose();
			}
		});
		setResizable(false);
		setBounds(400, 100, 490, 160);

		setContentPane(AudioPlayerPanel.getInstance());

		pack();
	}

	public void contentPaneSet() {
		setContentPane(AudioPlayerPanel.getInstance());
	}

}
