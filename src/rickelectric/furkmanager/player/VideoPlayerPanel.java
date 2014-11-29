package rickelectric.furkmanager.player;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.data.DefaultParams;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.swingmods.JButtonLabel;
import uk.co.caprica.vlcj.player.MediaPlayer;
import uk.co.caprica.vlcj.player.MediaPlayerEventAdapter;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.DefaultFullScreenStrategy;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;

public class VideoPlayerPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private static VideoPlayerPanel thisInstance = null;

	private String playerString;

	private static MediaPlayerFactory factory = null;
	private static EmbeddedMediaPlayer player = null;

	public static boolean dummy = false;

	private VideoPlayerWin videoWin;

	private JPanel videoPane;
	private JLabel statusBar;

	private Canvas videoCanvas;

	private boolean active;

	private CanvasVideoSurface videoSurface;

	private long time;

	private JLabel loading;

	private JSlider slider_position;

	private JPanel panel_controls;

	private JLabel btn_playpause;

	private JLabel btn_stop;

	private JSlider slider_volume;

	public static synchronized VideoPlayerPanel getInstance() {
		if (thisInstance == null) {
			try {
				factory = DefaultParams.getMediaPlayerFactory();
				if(factory==null) return null;
				player = factory
						.newEmbeddedMediaPlayer(new DefaultFullScreenStrategy(
								FurkManager.mediaWindow));
				player.setEnableMouseInputHandling(false);
				player.setEnableKeyInputHandling(false);
				thisInstance = new VideoPlayerPanel();
			} catch (Exception e) {
				e.printStackTrace();
				FurkManager.trayAlert(FurkManager.TRAY_WARNING, "VLC Not Found",
						"Unable To Find Suitable VLC Version On Your PC. \nYou Will Be Unable To Stream Music & Video. \nPlease Install VLC Media Player ("+System.getProperty("os.arch")+") If You Wish To Use These Features.", null);
			} catch (Error e) {
				e.printStackTrace();
				FurkManager.trayAlert(FurkManager.TRAY_ERROR, "VLC Not Found",
						"Unable To Find Suitable VLC Version On Your PC. \nYou Will Be Unable To Stream Music & Video. \nPlease Install VLC Media Player ("+System.getProperty("os.arch")+") If You Wish To Use These Features.", null);
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
		if (thisInstance == null) {
			return getInstance().play(mrl);
		}
		if (VideoPlayerPanel.dummy)
			mrl = "C:\\Users\\Ionicle\\" + "My IDM Downloads\\"
					+ "Video\\ReKoMe.mp4";// TODO For Testing
		if (player.isPlaying())
			player.stop();
		String[] mediaOptions = SettingsManager.getInstance().getProxySettings().toVlcjArgs();
		return active = player.playMedia(mrl, mediaOptions);
	}

	private VideoPlayerPanel() {
		setPreferredSize(new Dimension(559, 437));
		setSize(getPreferredSize());
		setBorder(new EmptyBorder(2, 2, 2, 2));
		setLayout(new BorderLayout(0, 0));

		videoPane = new JPanel();
		videoPane.setBorder(new CompoundBorder(new LineBorder(Color.black, 2),
				new EmptyBorder(4, 4, 4, 4)));
		videoPane.setLayout(new BorderLayout());
		videoPane.setBackground(Color.white);
		videoPane.setOpaque(true);

		videoCanvas = new Canvas();
		videoCanvas.setBackground(Color.BLACK);
		videoCanvas.setSize(720, 350);

		videoPane.add(videoCanvas, BorderLayout.CENTER);

		videoSurface = factory.newVideoSurface(videoCanvas);
		videoSurface.canvas().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					if (e.getClickCount() == 2) {
						if (!player.isFullScreen())
							FurkManager.mediaWindow
									.setContentPane(VideoPlayerPanel.this);
						else {
							if (videoWin != null && videoWin.isVisible()) {
								videoWin.contentPaneSet();
								videoWin.repaint();
							} else {
								FurkManager.getMainWindow().mediaNotify();
								FurkManager.mediaWindow
										.setContentPane(new JPanel());
							}
						}
						player.toggleFullScreen();
					}
				}
			}
		});
		videoSurface.canvas().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE) {
					player.pause();
				}
			}
		});

		player.setVideoSurface(videoSurface);

		add(videoPane, BorderLayout.CENTER);

		slider_position = new JSlider();
		slider_position.setPaintTicks(true);
		videoPane.add(slider_position, BorderLayout.SOUTH);

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
		panel_controls.add(btn_playpause);
		btn_playpause.setIcon(new ImageIcon(getClass().getResource(
				"media_pause.png")));

		btn_stop = new JButtonLabel("", new Runnable() {
			@Override
			public void run() {
				player.stop();
			}
		});
		panel_controls.add(btn_stop);
		btn_stop.setIcon(new ImageIcon(getClass().getResource("media_stop.png")));

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

		loading = new JLabel("");
		loading.setIcon(new ImageIcon(FurkManager.class
				.getResource("img/ajax-loader-48.gif")));
		loading.setPreferredSize(new Dimension(49, 49));
		loading.setVisible(false);
		panel_controls.add(loading);

		statusBar = new JLabel("0:00:00");
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(statusBar, BorderLayout.SOUTH);

		setVisible(true);
		addMediaListener();
	}

	private void addMediaListener() {
		player.addMediaPlayerEventListener(new MediaPlayerEventAdapter() {

			@Override
			public void buffering(MediaPlayer p, float newCache) {
				loading.setVisible(newCache < 100.0);
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
				playerString = player.getMediaMeta().getArtist() + " - "
						+ player.getMediaMeta().getTitle();
				if (videoWin != null)
					videoWin.setTitle(playerString);
			}

			@Override
			public void playing(MediaPlayer mediaPlayer) {
				loading.setVisible(false);
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

	public synchronized JFrame getVideoWin() {
		if (videoWin == null)
			videoWin = new VideoPlayerWin();
		videoWin.contentPaneSet();
		return videoWin;
	}

	void reattach() {
		player.setVideoSurface(videoSurface);
		player.attachVideoSurface();

	}

	public boolean isFullscreen() {
		return player.isFullScreen();
	}
}

class VideoPlayerWin extends JFrame {
	private static final long serialVersionUID = 1L;

	public VideoPlayerWin() {
		setBounds(100, 100, 559, 437);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				VideoPlayerPanel.getInstance().stop();
				dispose();
			}
		});

		contentPaneSet();
		pack();
	}

	public void setVisible(boolean b) {
		super.setVisible(b);
		if (b)
			VideoPlayerPanel.getInstance().reattach();
	}

	public void contentPaneSet() {
		setContentPane(VideoPlayerPanel.getInstance());
	}
}
