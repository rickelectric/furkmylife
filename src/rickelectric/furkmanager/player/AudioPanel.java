package rickelectric.furkmanager.player;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.util.HashMap;
import java.util.Observable;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import rickelectric.furkmanager.views.swingmods.JButtonLabel;

public class AudioPanel extends JPanel implements AudioObserver {
	private static final long serialVersionUID = 1L;

	private HashMap<String, ImageIcon> icons;

	private JLabel statusBar;

	private JButtonLabel btn_playpause;
	private JButtonLabel btn_stop;

	private JSlider slider_position;
	private JSlider slider_volume;

	private JPanel panel_controls;
	private JPanel panel;
	private JLabel infoBar;
	private JLabel disp_vol;

	public AudioPanel() {
		setBorder(new BevelBorder(BevelBorder.RAISED));
		setPreferredSize(new Dimension(467, 151));
		setSize(getPreferredSize());

		setLayout(new BorderLayout(0, 0));

		icons = new HashMap<String, ImageIcon>();
		icons.put("play", new ImageIcon(getClass()
				.getResource("media_play.png")));
		icons.put("pause",
				new ImageIcon(getClass().getResource("media_pause.png")));
		icons.put("stop", new ImageIcon(getClass()
				.getResource("media_stop.png")));

		slider_position = new JSlider();
		slider_position.setMaximum(1);
		slider_position.setValue(0);
		add(slider_position, BorderLayout.CENTER);

		panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 36));
		add(panel, BorderLayout.SOUTH);
		panel.setLayout(null);

		statusBar = new JLabel("-:--:--");
		statusBar.setBounds(0, 18, 463, 18);
		panel.add(statusBar);
		statusBar.setBorder(new BevelBorder(BevelBorder.LOWERED));

		infoBar = new JLabel("----");
		infoBar.setBounds(0, 0, 463, 18);
		panel.add(infoBar);

		panel_controls = new JPanel();
		panel_controls.setPreferredSize(new Dimension(10, 69));
		panel_controls.setBorder(new BevelBorder(BevelBorder.RAISED));
		add(panel_controls, BorderLayout.NORTH);
		panel_controls.setLayout(null);

		btn_playpause = new JButtonLabel("");
		btn_playpause.setBounds(7, 7, 52, 52);
		btn_playpause.setIcon(icons.get("play"));
		panel_controls.add(btn_playpause);

		btn_stop = new JButtonLabel("");
		btn_stop.setBounds(64, 7, 52, 52);
		btn_stop.setIcon(icons.get("stop"));
		panel_controls.add(btn_stop);

		JLabel lblVolume = new JLabel("Volume: ");
		lblVolume.setBounds(337, 7, 41, 20);
		panel_controls.add(lblVolume);

		slider_volume = new JSlider();
		slider_volume.setBounds(337, 38, 116, 21);
		slider_volume.setMaximum(200);
		slider_volume.setMinimum(0);
		slider_volume.setValue(AudioPlayer.getInstance().getVolume());
		slider_volume.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int val = slider_volume.getValue();
				disp_vol.setText("" + val);
				AudioPlayer.getInstance().setVolume(val);
			}
		});
		slider_volume.setPreferredSize(new Dimension(100, 16));
		panel_controls.add(slider_volume);

		disp_vol = new JLabel("" + AudioPlayer.getInstance().getVolume());
		disp_vol.setFont(new Font("Dialog", Font.BOLD, 12));
		disp_vol.setBounds(388, 7, 65, 20);
		panel_controls.add(disp_vol);

		AudioPlayer.getInstance().addAudioObserver(this);
	}

	public void detachObserver() {
		AudioPlayer.getInstance().deleteObserver(this);
	}

	public void reattachObserver() {
		AudioPlayer.getInstance().addAudioObserver(this);
	}

	@Override
	public void update(Observable o, Object param) {
		Object[] args = (Object[]) param;
		int state = (Integer) args[0];
		switch (state) {
		case AudioPlayer.IDLE:
			break;
		case AudioPlayer.PLAYING:
			btn_playpause.setIcon(icons.get("pause"));
			break;
		case AudioPlayer.PAUSED:
			btn_playpause.setIcon(icons.get("play"));
			break;
		case AudioPlayer.POSITION:
			float posTime = (Float) args[1];
			slider_position.setValue((int) posTime);
			break;
		case AudioPlayer.LENGTH:
			long time = (Long) args[1];
			slider_position.setMaximum((int) time);
			break;
		case AudioPlayer.META:
			String meta = (String) args[1];
			infoBar.setText(meta);
			break;
		case AudioPlayer.VOLUME:
			int volume = (Integer) args[1];
			if (slider_volume.getValue() != volume) {
				slider_volume.setValue(volume);
				disp_vol.setText(volume + "");
			}
			break;
		case AudioPlayer.STATUS:
			String status = (String) args[1];
			statusBar.setText(status);
			break;
		case AudioPlayer.STOPPED:
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
}
