package rickelectric.furkmanager.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.swingmods.JFadeLabel;
import rickelectric.furkmanager.views.swingmods.JFadeLayeredPane;

public class SplashLogin extends JWindow {
	private static final long serialVersionUID = 1L;

	private JFadeLayeredPane contentPane;
	private JFadeLabel splashImage;

	private JFadeLabel dispLine;

	private Timer fader;
	private JButton button_X;
	private JButton button_info;

	public static void main(String[] args) {
		SettingsManager.init();
		FurkManager.LAF(2);
		new SplashLogin().setVisible(true);
	}

	private Point[] imgPts = new Point[] { new Point(17, 135),
			new Point(84, 97), new Point(158, 131), new Point(175, 211),
			new Point(123, 278), new Point(45, 278), new Point(6, 211) };

	private int[] currPts = { 1, 4, 6 };

	public SplashLogin() {
		setAlwaysOnTop(true);

		setSize(683, 416);

		contentPane = new JFadeLayeredPane();
		contentPane.setAlpha(1.0f);
		contentPane.setOpaque(true);
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new CompoundBorder(new BevelBorder(
				BevelBorder.RAISED, null, null, null, null), new LineBorder(
				new Color(0, 0, 0), 5)));
		setContentPane(contentPane);
		setLocationRelativeTo(null);

		splashImage = new JFadeLabel();
		splashImage.setIcon(new ImageIcon(Splash.class
				.getResource("/rickelectric/furkmanager/img/Splash_Big.png")));
		splashImage.setHorizontalAlignment(SwingConstants.CENTER);
		splashImage.setBounds(7, 7, 670, 401);
		contentPane.add(splashImage);

		dispLine = new JFadeLabel("Loading...");
		contentPane.setLayer(dispLine, 8);
		dispLine.setFont(new Font("Dialog", Font.BOLD, 14));
		dispLine.setBounds(7, 378, 670, 20);
		dispLine.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(dispLine);

		button_X = new JButton("");
		button_X.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(onClose).start();
			}
		});
		button_X.setIcon(new ImageIcon(
				Splash.class
						.getResource("/rickelectric/furkmanager/img/sm/edit_delete.png")));
		contentPane.setLayer(button_X, 22);
		button_X.setBounds(644, 8, 32, 32);
		contentPane.add(button_X);

		button_info = new JButton("");
		button_info
				.setIcon(new ImageIcon(
						Splash.class
								.getResource("/rickelectric/furkmanager/img/sm/Information24.gif")));
		contentPane.setLayer(button_info, 22);
		button_info.setBounds(609, 8, 32, 32);
		contentPane.add(button_info);

		dragger = new JFadeLabel();
		dragger.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				Point loc = e.getLocationOnScreen();
				Point mpt = MouseInfo.getPointerInfo().getLocation();
				// System.out.println("DragEvent :\n"+loc+"\n"+mpt+"\n");
				int xdiff = mpt.x - loc.x;
				int ydiff = mpt.y - loc.y;

				SplashLogin.this.setLocation(mpt.x - xdiff, mpt.y - ydiff);
			}
		});
		contentPane.setLayer(dragger, 20);
		dragger.setBounds(0, 0, 677, 40);
		contentPane.add(dragger);

		audio = new JFadeLabel("");
		audio.setIcon(new ImageIcon(SplashLogin.class
				.getResource("/rickelectric/furkmanager/img/audio-48.png")));
		contentPane.setLayer(audio, 10);
		audio.setBounds(17, 135, 48, 48);
		contentPane.add(audio);

		video = new JFadeLabel("");
		video.setIcon(new ImageIcon(SplashLogin.class
				.getResource("/rickelectric/furkmanager/img/video-48.png")));
		contentPane.setLayer(video, 10);
		video.setBounds(175, 211, 48, 48);
		contentPane.add(video);

		image = new JFadeLabel("");
		image.setIcon(new ImageIcon(SplashLogin.class
				.getResource("/rickelectric/furkmanager/img/image-48.png")));
		contentPane.setLayer(image, 10);
		image.setBounds(45, 278, 48, 48);
		contentPane.add(image);
		
		onClose(null);
		rotate();
	}

	private Runnable defClose = new Runnable() {
		public void run() {
			setVisible(false);
			while (isVisible()) {
				SplashLogin.this.setText("Exiting...");
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
			}
			FurkManager.exit();
		}
	};

	private Runnable onClose = null;
	private JFadeLabel dragger,audio,video,image;

	public void onClose(Runnable closeAction) {
		if (closeAction == null)
			onClose = defClose;
		else
			onClose = closeAction;
	}

	@Override
	public void setVisible(final boolean b) {
		if (!isVisible() && !b)
			return;
		super.setVisible(true);
		setOpacity(b ? 0 : 1);
		fader = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				float newOpac = getOpacity() + (b ? 0.05f : -0.05f);
				if (newOpac > 1) {
					setOpacity(1);
					((Timer) e.getSource()).stop();
				} else if (newOpac < 0) {
					setOpacity(0);
					if (!b)
						setVisible(false);
					((Timer) e.getSource()).stop();
				} else {
					setOpacity(newOpac);
				}
			}
		});
		fader.start();
	}

	public void rotate() {
		new Thread(new Runnable() {
			public void run() {
				long time=500;
				while(!Thread.interrupted()){
					audio.setLocation(imgPts[currPts[0]]);
					video.setLocation(imgPts[currPts[1]]);
					image.setLocation(imgPts[currPts[2]]);
					for (int i = 0; i < 3; i++) {
						currPts[i]++;
						currPts[i] %= 7;
					}
					try {
						Thread.sleep(time);
					} catch (Exception e) {
					}
				}
			}
		}).start();
	}

	@Override
	public void setOpacity(float opacity) {
		super.setOpacity(opacity);
		this.contentPane.setAlpha(opacity);
	}

	@Override
	public boolean isVisible() {
		if (getOpacity() == 0)
			return false;
		return super.isVisible();
	}

	public void setText(String s) {
		dispLine.setText(s);
	}

	public String getText() {
		return dispLine.getText();
	}
}
