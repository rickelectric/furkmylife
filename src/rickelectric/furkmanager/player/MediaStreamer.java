package rickelectric.furkmanager.player;

import rickelectric.furkmanager.data.DefaultParams;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.NativeLibrary;

public class MediaStreamer {

	//private static AudioMediaPlayerComponent audioPlayer = null;
	private static EmbeddedMediaPlayer player = null;

	private static MediaPlayerFactory factory = null;
	//private static EmbeddedMediaPlayer vplayer = null;

	private static AudioPlayerWin audioWin;
	private static VideoPlayerWin videoWin;

	public static void main(String[] args) {
		MediaStreamer.init();
		MediaStreamer.playVideo("http://am38oja80gso26tq7ppsf2mpiqvh2ig7r7a52o0.gcdn.biz:30084/d/M/rbm8fH3v5jCR_osfb1H7GmmX46w0pNy_JN0NysEKYW8uSpSF37NkV78RSgfZ1FFgB3JorVMio7HxjegnUo0UN2IEX32H-7eSDN-zjP4eGa2_EUoH2dRRYA/Once-Upon-a-Time-S03E18-HDTV-x264-LOL-mp4");
	}
	
	public static void init() {
		NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(),
				DefaultParams.VLC_PATH);
		
		if (factory == null)
			factory = new MediaPlayerFactory();
		
		player = factory.newEmbeddedMediaPlayer();
		audioWin = new AudioPlayerWin(player);
		audioWin.setVisible(false);

	}

	public static void playVideo(String mrl) {
		if(videoWin!=null){
			player.stop();
			videoWin.dispose();
		}if (audioWin != null) {
			player.stop();
			audioWin.dispose();
		}

		videoWin = new VideoPlayerWin(mrl,player,factory);
		
	}

	public static void playAudio(String mrl) {
		if(videoWin!=null){
			player.stop();
			videoWin.dispose();
		}if (audioWin != null) {
			player.stop();
			audioWin.dispose();
		}

		player.playMedia(mrl,"network-caching=4000");

		try {
			while (!player.isPlaying())
				Thread.sleep(200);
		} catch (Exception e) {}

		audioWin = new AudioPlayerWin(player);
		//audioWin.setVisible(true);
		
	}

	public static AudioPlayerWin getAudioWin() {
		return audioWin;
	}
	
	public static VideoPlayerWin getVideoWin() {
		return videoWin;
	}

}
