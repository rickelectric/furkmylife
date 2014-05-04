package org.rickelectric.furkmanager.experimentation;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.rickelectric.furkmanager.data.DefaultParams;

import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer;
import uk.co.caprica.vlcj.player.embedded.videosurface.CanvasVideoSurface;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import com.sun.jna.NativeLibrary;

/**
 * A test for video cropping.
 * <p>
 * The red colouring on the video canvas shows the unused portion of the video
 * surface - the client application can resize the video canvas to reclaim this
 * unused area without affecting the video size/aspect (ordinarily resizing
 * would stretch or compress the video).
 * <p>
 * Any black area shown on the top/left/bottom/right of the video are the black
 * bars present in the source material - this is what we want to crop away.
 * <p>
 * The panel with the white background is simply to provide context.
 */
public class VLCJ_Test extends JFrame {
	private static final long serialVersionUID = 1L;

	/**
	 * The standard crop geometries.
	 */
	private static final String[][] CROP_GEOMETRIES = {
			{ "<choose...>", null }, { "16:10", "16:10" }, { "16:9", "16:9" },
			{ "1.85:1", "185:100" }, { "2.21:1", "221:100" },
			{ "2.35:1", "235:100" }, { "2.39:1", "239:100" }, { "5:3", "5:3" },
			{ "4:3", "4:3" }, { "5:4", "5:4" }, { "1:1", "1:1" } };

	private static final String HELP_TEXT = "<html>Select a standard crop geometry from the list box, or enter a custom geometry and press enter/return.<br/><br/>"
			+ "For the custom geometry, use:<ul>"
			+ "<li>W:H, e.g. 16:9 and the values must be integers</li>"
			+ "<li>WxH+L+T, e.g. 720x511+0+73</li>"
			+ "<li>L+T+R+B, e.g. 10+20+10+20</li>" + "</ul></html>";

	private MediaPlayerFactory factory;
	private EmbeddedMediaPlayer mediaPlayer;
	private CanvasVideoSurface videoSurface;

	private JPanel contentPane;
	private JPanel videoPane;
	private Canvas videoCanvas;
	private JPanel controlsPane;
	private JLabel standardCropLabel;
	private JComboBox<String[]> standardCropComboBox;
	private JLabel customCropLabel;
	private JTextField customCropTextField;
	private JButton pauseButton;

	public static void main(String[] args) throws Exception {

		final String mrl = "C:\\Users\\Ionicle\\My IDM Downloads\\Dick Figures Movie Official Trailer.flv";

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				new VLCJ_Test().start(mrl);
			}
		});
	}
	
	public VLCJ_Test() {
		
		NativeLibrary.addSearchPath(
			RuntimeUtil.getLibVlcLibraryName(),
			DefaultParams.VLC_PATH
		);
		
		factory = new MediaPlayerFactory();
		mediaPlayer = factory.newEmbeddedMediaPlayer();

		videoPane = new JPanel();
		videoPane.setBorder(new CompoundBorder(new LineBorder(Color.black, 2),
				new EmptyBorder(16, 16, 16, 16)));
		videoPane.setLayout(new BorderLayout());
		videoPane.setBackground(Color.white);

		videoCanvas = new Canvas();
		videoCanvas.setBackground(Color.BLACK);
		videoCanvas.setSize(720, 350);

		videoPane.add(videoCanvas, BorderLayout.CENTER);

		videoSurface = factory.newVideoSurface(videoCanvas);

		mediaPlayer.setVideoSurface(videoSurface);

		/*standardCropLabel = new JLabel("Standard Crop:");
		standardCropLabel.setDisplayedMnemonic('s');

		standardCropComboBox = new JComboBox<String[]>(CROP_GEOMETRIES);
		standardCropComboBox.setEditable(false);
		standardCropComboBox.setRenderer(new DefaultListCellRenderer() {
			@Override
			public Component getListCellRendererComponent(JList<?> list,
					Object value, int index, boolean isSelected,
					boolean cellHasFocus) {
				JLabel l = (JLabel) super.getListCellRendererComponent(list,
						value, index, isSelected, cellHasFocus);
				String[] val = (String[]) value;
				l.setText(val[0]);
				return l;
			}
		});

		standardCropLabel.setLabelFor(standardCropComboBox);

		customCropLabel = new JLabel("Custom Crop:");
		customCropLabel.setDisplayedMnemonic('c');

		customCropTextField = new JTextField(10);
		customCropTextField.setFocusAccelerator('c');
		*/
		
		pauseButton = new JButton("Pause");
		pauseButton.setMnemonic('p');

		controlsPane = new JPanel();
		controlsPane.setLayout(new BoxLayout(controlsPane, BoxLayout.X_AXIS));
		controlsPane.add(pauseButton);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(8, 16, 16, 16));
		contentPane.setLayout(new BorderLayout(16, 16));
		contentPane.add(videoPane, BorderLayout.CENTER);
		contentPane.add(controlsPane, BorderLayout.NORTH);

		setTitle("vlcj crop test");
		setIconImage(new ImageIcon(getClass().getResource(
				"/rickelectric/furkmanager/img/fr.png")).getImage());
		setContentPane(contentPane);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();

		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				mediaPlayer.pause();
			}
		});
	}

	private void start(String mrl) {
		setVisible(true);
		mediaPlayer.playMedia(mrl);
	}
}
