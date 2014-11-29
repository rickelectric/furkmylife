package rickelectric.furkmanager.player;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

public class AudioPlayerWin extends JFrame {
	private static final long serialVersionUID = 1L;

	private AudioPlayerPanel contentPane;

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
		contentPane = AudioPlayerPanel.getInstance();
		setContentPane(contentPane);

		pack();
	}

}
