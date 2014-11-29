package rickelectric.furkmanager.player;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import rickelectric.furkmanager.data.DefaultParams;
import rickelectric.furkmanager.network.APIBridge;

public class VideoPlayerWin extends JFrame {
	private static final long serialVersionUID = 1L;

	public static void main(String[] args) {
		DefaultParams.init();
		APIBridge.dummy = true;
		VideoPlayerPanel.getInstance();
		VideoPlayerPanel.getVideoWin().setVisible(true);
		VideoPlayerPanel.getInstance().play("");
	}

	private VideoPlayerPanel contentPane;

	public VideoPlayerWin() {
		setBounds(100, 100, 559, 437);
		contentPane=null;
	}
	
	public void setVisible(boolean b){
		if(!isPopulated()){
			populate();
		}
		super.setVisible(b);
	}
	
	public boolean isPopulated(){
		return contentPane!=null;
	}
	
	public void populate(){
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				VideoPlayerPanel.getInstance().stop();
				dispose();
			}
		});
		contentPane = VideoPlayerPanel.getInstance();
		setContentPane(contentPane);

		pack();
	}

}
