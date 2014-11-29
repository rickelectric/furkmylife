package rickelectric.furkmanager.player;

import rickelectric.furkmanager.data.DefaultParams;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.NativeLibrary;

public class GUIStreamer {
	
	private static EmbeddedMediaPlayer player;
	private static MediaPlayerFactory factory;
	
	public static void init(){
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),
				DefaultParams.VLC_PATH);
		factory = new MediaPlayerFactory();
		player=factory.newEmbeddedMediaPlayer();
	}
	
	public static VideoPlayerWin playMedia(String mrl){
		VideoPlayerWin win=new VideoPlayerWin();
		return win;
	}

	public static void main(String[] args) {
		init();
		VideoPlayerWin win=playMedia(
				"C:\\Users\\Ionicle\\My IDM Downloads\\Video\\Forget Samsung and Apple. This is the future...MP4"
		);
		win.setVisible(true);
	}
}
