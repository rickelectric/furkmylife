package rickelectric.furkmanager.player;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.HashMap;
import java.util.Observable;

import javax.swing.Box;
import javax.swing.ImageIcon;
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
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.swingmods.JButtonLabel;

public class VideoPanel extends JPanel implements VideoObserver, Runnable {
	private static final long serialVersionUID = 1L;

	private JPanel videoPane;
	private JLabel statusBar;

	private Canvas videoSlave;

	private HashMap<String, ImageIcon> icons;

	private long time;

	private JLabel loading;
	private JSlider slider_position;
	private JPanel panel_controls;
	private JButtonLabel btn_playpause;
	private JButtonLabel btn_stop;
	private JSlider slider_volume;

	private Thread overThread;

	public VideoPanel() {
		setPreferredSize(new Dimension(559, 437));
		setSize(getPreferredSize());
		setBorder(new EmptyBorder(2, 2, 2, 2));
		setLayout(new BorderLayout(0, 0));

		icons = new HashMap<String, ImageIcon>();
		icons.put("play", new ImageIcon(getClass()
				.getResource("media_play.png")));
		icons.put("pause",
				new ImageIcon(getClass().getResource("media_pause.png")));
		icons.put("stop", new ImageIcon(getClass()
				.getResource("media_stop.png")));

		videoPane = new JPanel();
		videoPane.setBorder(new CompoundBorder(new LineBorder(Color.black, 2),
				new EmptyBorder(4, 4, 4, 4)));
		videoPane.setLayout(new BorderLayout());
		videoPane.setBackground(Color.white);
		videoPane.setOpaque(true);

		videoSlave = new Canvas();
		videoSlave.setBackground(Color.BLACK);
		videoSlave.setSize(720, 350);

		videoPane.add(videoSlave, BorderLayout.CENTER);

		add(videoPane, BorderLayout.CENTER);

		slider_position = new JSlider();
		slider_position.setPaintTicks(true);
		videoPane.add(slider_position, BorderLayout.SOUTH);

		time = 100;
		slider_position.setMaximum((int) time);

		panel_controls = new JPanel();
		panel_controls.setBorder(new BevelBorder(BevelBorder.RAISED));

		FlowLayout flowLayout = (FlowLayout) panel_controls.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		add(panel_controls, BorderLayout.NORTH);

		btn_playpause = new JButtonLabel("");
		panel_controls.add(btn_playpause);
		btn_playpause.setIcon(icons.get("play"));

		btn_stop = new JButtonLabel("");
		panel_controls.add(btn_stop);
		btn_stop.setIcon(icons.get("stop"));

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
				VideoPlayer.getInstance().setVolume(val);
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

		statusBar = new JLabel("-:--:--");
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));
		add(statusBar, BorderLayout.SOUTH);

		VideoPlayer.getInstance().addVideoObserver(this);
		start();
	}

	public void run() {
		while (!isShowing()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		while (VideoPlayer.getInstance().isActive()) {
			try {
				if (!VideoPlayer.getInstance().isFullscreen()) {
					VideoPlayer.getInstance().getOverlay()
							.setLocation(videoSlave.getLocationOnScreen());
					VideoPlayer.getInstance().getOverlay()
							.setSize(videoSlave.getSize());
					if (SettingsManager.getInstance().getMainWinMode() == SettingsManager.WIN_MODE)
						VideoPlayer.getInstance().getOverlay()
								.setVisible(getTopLevelAncestor().isVisible());
				}

				Thread.sleep(20);
			} catch (Exception e) {
			}
		}
		VideoPlayer.getInstance().getOverlay().setVisible(false);
	}

	public void dispose() {
		VideoPlayer.getInstance().getOverlay().setVisible(false);
	}

	public void setVisible(boolean b) {
		VideoPlayer.getInstance().getOverlay().setVisible(b);
		super.setVisible(b);
	}

	@Override
	public void update(Observable o, Object param) {
		Object[] args = (Object[]) param;
		int state = (Integer) args[0];
		if (VideoPlayer.getInstance().isActive()) {
			start();
		}
		switch (state) {
		case VideoPlayer.IDLE:
			break;
		case VideoPlayer.PLAYING:
			if (SettingsManager.getInstance().getMainWinMode() == SettingsManager.ENV_MODE)
				VideoPlayer.getInstance().getOverlay().setVisible(true);
			btn_playpause.setIcon(icons.get("pause"));
			break;
		case VideoPlayer.PAUSED:
			btn_playpause.setIcon(icons.get("play"));
			break;
		case VideoPlayer.POSITION:
			float posTime = (Float) args[1];
			slider_position.setValue((int) posTime);
			break;
		case VideoPlayer.LENGTH:
			long time = (Long) args[1];
			slider_position.setMaximum((int) time);
			break;
		case VideoPlayer.VOLUME:
			int volume = (Integer) args[1];
			if (slider_volume.getValue() != volume) {
				slider_volume.setValue(volume);
			}
			break;
		case VideoPlayer.FRAME:
			break;
		case VideoPlayer.STATUS:
			String status = (String) args[1];
			statusBar.setText(status);
			break;
		case VideoPlayer.STOPPED:
			btn_playpause.setIcon(icons.get("play"));
			break;
		}
	}

	@Override
	public JButtonLabel getPlayButton() {
		return btn_playpause;
	}

	@Override
	public JButtonLabel getStopButton() {
		return btn_stop;
	}

	@Override
	public JSlider getPositionSlider() {
		return slider_position;
	}

	@Override
	public void detachObserver() {
		VideoPlayer.getInstance().deleteObserver(this);
	}

	@Override
	public void reattachObserver() {
		VideoPlayer.getInstance().addVideoObserver(this);
	}

	public void start() {
		if (overThread == null || !overThread.isAlive()) {
			overThread = new Thread(this);
			overThread.start();
		}
	}
}
