package rickelectric.furkmanager.player;

import javax.swing.JFrame;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.data.DefaultParams;

public class MediaStreamer {

	public static void main(String[] args) {
		FurkManager.LAF(0);
		VideoPlayer.dummy=true;
		DefaultParams.init();
		VideoPlayer.getInstance();
		
		VideoPanel pane = new VideoPanel();
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setContentPane(pane);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		
		VideoPlayer.getInstance().play("");
		
		
	}


}
