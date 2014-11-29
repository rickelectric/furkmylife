package rickelectric.furkmanager.views.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.RoundedBalloonStyle;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.APIMessage;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.player.AudioPlayerPanel;
import rickelectric.furkmanager.player.VideoPlayerPanel;
import rickelectric.furkmanager.utils.MouseActivity;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.CircleButton;
import rickelectric.furkmanager.views.panels.Main_DownloadView;
import rickelectric.furkmanager.views.panels.Main_FeedView;
import rickelectric.furkmanager.views.panels.Main_FileView;
import rickelectric.furkmanager.views.panels.Main_SettingsView;
import rickelectric.furkmanager.views.panels.Main_UserView;
import rickelectric.furkmanager.views.swingmods.balloon.BTipPositioner;

public class MainEnvironment extends JDialog implements PrimaryEnv, Runnable,
		MouseListener {
	private static final long serialVersionUID = 1L;

	private CircleButton[] buttons;
	private CircleButton[] fnButtons;

	private BalloonTip[] balloons;
	private BalloonTip[] fnBalloons;

	private JComponent[] sections;
	private JComponent[] fnSections;

	private JPanel contentPane;
	private Color bgc;
	private JLabel message;

	private boolean showButtons;

	private int bHeight, currHeight;

	private boolean audioPlayerActive;
	private boolean videoPlayerActive;

	private boolean mediaReady;

	// TODO Animation Implementation Of Button AND Balloon
	// Show/Hide/Pop-In/Pop-Out

	public class EnvPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			g.setColor(Color.lightGray);

			// Paint Special Function Buttons & Balloons Below
			if (mediaReady && FurkManager.mediaEnabled())
				for (int i = 0; i < fnButtons.length; i++) {
					if (fnButtons[i] != null && fnButtons[i].isVisible()) {
						fnButtons[i].paint(g);
						if (i < fnBalloons.length
								&& fnBalloons[i].isVisible()
								&& SettingsManager.getInstance()
										.dimEnvironment()) {
							int w1;
							g.setColor(Color.yellow);
							g.setFont(new Font(Font.SERIF, Font.BOLD
									| Font.ITALIC, 42));
							w1 = g.getFontMetrics().stringWidth(
									fnButtons[i].getPopup());
							g.drawString(
									fnButtons[i].getPopup(),
									fnBalloons[i].getX()
											+ (fnBalloons[i].getWidth() / 2 - w1 / 2),
									fnBalloons[i].getY() - 10);
							g.setColor(Color.black);
							g.drawString(
									fnButtons[i].getPopup(),
									fnBalloons[i].getX()
											+ (fnBalloons[i].getWidth() / 2 - w1 / 2)
											- 2, fnBalloons[i].getY() - 12);
						}
					}
				}

			// Paint Primary Buttons & Balloons On Top
			for (int i = 0; i < buttons.length; i++) {
				if (buttons[i] != null) {
					buttons[i].paint(g);
					if (i < balloons.length && balloons[i].isVisible()
							&& SettingsManager.getInstance().dimEnvironment()) {
						int w1;
						g.setColor(Color.white);
						g.setFont(new Font(Font.SERIF, Font.BOLD | Font.ITALIC,
								42));
						w1 = g.getFontMetrics().stringWidth(
								buttons[i].getPopup());
						g.drawString(buttons[i].getPopup(), balloons[i].getX()
								+ (balloons[i].getWidth() / 2 - w1 / 2),
								balloons[i].getY() - 10);
						g.setColor(Color.black);
						g.drawString(buttons[i].getPopup(), balloons[i].getX()
								+ (balloons[i].getWidth() / 2 - w1 / 2) - 2,
								balloons[i].getY() - 12);
					}
				}
			}
		}
	}

	int darkness = 40;

	public MainEnvironment() {
		super();
		setUndecorated(true);
		setTitle("Furk Desktop Environment");
		setIconImage(new ImageIcon(FurkManager.class.getResource("img/fr.png"))
				.getImage());

		bgc = new Color(Color.darkGray.getRed(), Color.darkGray.getBlue(),
				Color.darkGray.getGreen(), 0);
		setBackground(bgc);

		contentPane = new EnvPanel();
		contentPane.setLayout(null);
		contentPane.setBackground(getBackground());
		setContentPane(contentPane);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		showButtons = true;
		audioPlayerActive = videoPlayerActive = false;
		mediaReady = false;

		buttons = new CircleButton[6];
		balloons = new BalloonTip[5];

		// Audio & Video Player
		fnButtons = new CircleButton[2];
		fnBalloons = new BalloonTip[2];

		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setLocationRelativeTo(null);

		// TODO Let G2Paint Handle This
		message = new JLabel();
		message.setSize(getWidth() - 20, 50);
		message.setLocation(10, 10);
		message.setOpaque(false);
		getContentPane().add(message);

		bHeight = getHeight()
				- (UtilBox.getTaskbarOrientation() == UtilBox.BOTTOM ? UtilBox
						.getTaskbarHeight() : 0) - 100;
		currHeight = bHeight;

		generateButtons();
		generateFnButtons();

		balloonConstruct();
		fnBalloonConstruct();

		mediaActiveCheck();

		addMouseListener(this);
		new Thread(this).start();
	}

	public void mediaNotify() {
		if (VideoPlayerPanel.getInstance().isFullscreen()) {

		}
	}

	public void dispose() {
		if (mediaReady && FurkManager.mediaEnabled()) {
			fnBalloons[0].remove(AudioPlayerPanel.getInstance());
			contentPane.remove(fnButtons[0]);
			fnBalloons[0].remove(VideoPlayerPanel.getInstance());
			contentPane.remove(fnButtons[1]);
		}
		super.dispose();
		MouseActivity.destroyInstance();

	}

	public void mediaActiveCheck() {
		if (!FurkManager.mediaEnabled())
			return;
		if (AudioPlayerPanel.getInstance().isActive()) {
			if (!mediaReady) {
				fnBalloonConstruct();
			}
			audioPlayerActive = true;
			fnButtons[0].setVisible(true);
			fnBalloons[0].setVisible(true);
		}
		if (VideoPlayerPanel.getInstance().isActive()) {
			if (!mediaReady) {
				fnBalloonConstruct();
			}
			videoPlayerActive = true;
			fnButtons[1].setVisible(true);
			fnBalloons[1].setVisible(true);
		}
		repaint();

	}

	@Override
	public void mediaCall(int mediaType, String mrl) {
		if (!FurkManager.mediaEnabled())
			return;
		if (!mediaReady) {
			fnBalloonConstruct();
		}
		if (mediaType == AUDIO) {
			if (AudioPlayerPanel.getInstance().play(mrl)) {
				audioPlayerActive = true;
				fnButtons[0].setVisible(true);
				fnBalloons[0].setVisible(true);
				repaint();
			}
		} else if (mediaType == VIDEO) {
			videoPlayerActive = true;
			fnButtons[1].setVisible(true);
			fnBalloons[1].setVisible(true);
			repaint();
			VideoPlayerPanel.getInstance().play(mrl);
		}
	}

	private void generateButtons() {
		int xLoc = 50, inc = 120;
		buttons[0] = new CircleButton("My Files", getClass().getResource(
				"/rickelectric/furkmanager/img/dash/Files-64.png"));
		buttons[0].setLocation(xLoc, bHeight);
		contentPane.add(buttons[0]);
		xLoc += inc;

		buttons[1] = new CircleButton("My Downloads", getClass().getResource(
				"/rickelectric/furkmanager/img/dash/Download-64.png"));
		buttons[1].setLocation(xLoc, bHeight);
		contentPane.add(buttons[1]);
		xLoc += inc;

		buttons[2] = new CircleButton("RSS Feeds", getClass().getResource(
				"/rickelectric/furkmanager/img/dash/RSS-64.png"));
		buttons[2].setLocation(xLoc, bHeight);
		contentPane.add(buttons[2]);
		xLoc += inc;

		buttons[3] = new CircleButton("Application Settings", getClass()
				.getResource(
						"/rickelectric/furkmanager/img/dash/Settings-64.png"));
		buttons[3].setLocation(xLoc, bHeight);
		contentPane.add(buttons[3]);
		xLoc += inc;

		buttons[4] = new CircleButton("Furk User Options", getClass()
				.getResource("/rickelectric/furkmanager/img/dash/User-64.png"));
		buttons[4].setLocation(xLoc, bHeight);
		contentPane.add(buttons[4]);
		// xLoc += inc;

		buttons[5] = new CircleButton("Exit", getClass().getResource(
				"/rickelectric/furkmanager/img/remove.png"));
		buttons[5].setSize(buttons[4].getSize());
		buttons[5]
				.setLocation(getWidth() - buttons[5].getWidth() - 10, bHeight);
		contentPane.add(buttons[5]);
	}

	private void generateFnButtons() {
		if (!FurkManager.mediaEnabled())
			return;
		int xLoc = 700, inc = 120;
		int yLoc = 555;

		fnButtons[1] = new CircleButton("Video Player", getClass().getResource(
				"/rickelectric/furkmanager/img/tree/video-48.png"));
		fnButtons[1].setLocation(xLoc, yLoc);
		fnButtons[1].setSize(buttons[0].getSize());
		fnButtons[1].setVisible(false);
		contentPane.add(fnButtons[1]);

		xLoc += inc;

		fnButtons[0] = new CircleButton("Music Player", getClass().getResource(
				"/rickelectric/furkmanager/img/tree/audio-48.png"));
		fnButtons[0].setLocation(xLoc, yLoc);
		fnButtons[0].setSize(buttons[0].getSize());
		fnButtons[0].setVisible(false);
		contentPane.add(fnButtons[0]);

	}

	private void balloonConstruct() {
		sections = new JComponent[] { new Main_FileView(),
				new Main_DownloadView(), new Main_FeedView(),
				new Main_SettingsView(), new Main_UserView() };

		for (int i = 0; i < balloons.length; i++) {
			balloons[i] = new BalloonTip(buttons[i], sections[i],
					new RoundedBalloonStyle(10, 10,
							sections[i].getBackground(), Color.BLUE),
					new BTipPositioner(), null);

			balloons[i].setVisible(false);
		}
	}

	private void fnBalloonConstruct() {
		if (!FurkManager.mediaEnabled())
			return;
		fnSections = new JComponent[] { AudioPlayerPanel.getInstance(),
				VideoPlayerPanel.getInstance() };

		for (int i = 0; i < fnBalloons.length; i++) {
			fnBalloons[i] = new BalloonTip(fnButtons[i], fnSections[i],
					new RoundedBalloonStyle(10, 10, fnSections[i]
							.getBackground(), Color.red),
					new BTipPositioner(), null);

			fnBalloons[i].setVisible(false);
		}
		mediaReady = true;
	}

	@Override
	public void loadMessages() {
		try {
			ArrayList<APIMessage> messages = API.getMessages();
			if (messages == null || messages.size() == 0)
				message.setText("");
			String text = "";
			for (APIMessage msg : messages) {
				text += msg.getType().toUpperCase() + ": \n" + msg.getText()
						+ "\n\n";
			}
			message.setText(text);
		} catch (Exception e) {
		}
	}

	@Override
	public void main() {
		changeViewSection(0);
	}

	@Override
	public void settings() {
		changeViewSection(3);
	}

	@Override
	public void userSettings() {
		changeViewSection(4);
	}

	private void changeViewSection(int i) {
		for (int x = 0; x < balloons.length; x++) {
			balloons[x].setVisible(false);
		}
		balloons[i].setVisible(true);
		bgc = new Color(Color.darkGray.getRed(), Color.darkGray.getBlue(),
				Color.darkGray.getGreen(), SettingsManager.getInstance()
						.dimEnvironment() ? darkness : 0);
		setBackground(bgc);
		slideButtons(true);
		MainEnvironment.this.repaint();
	}

	@Override
	public void setStatus(String string) {

	}

	public void refreshBgc() {
		requestDimEnvironment(true);
		MainEnvironment.this.repaint();
	}

	public int popupHeight() {
		return getHeight() - (buttons[0].getY() - 20);
	}

	/**
	 * 
	 * @param showButtons
	 *            true: Slide In, false: Slide Out
	 */
	public void slideButtons(boolean showButtons) {
		this.showButtons = showButtons;
	}

	@Override
	public void setVisible(boolean b) {
		if (b)
			MouseActivity.getInstance();
		else
			MouseActivity.destroyInstance();
		super.setVisible(b);
	}

	@Override
	public void run() {
		long sleepTime = 40;
		long lastInTime = System.currentTimeMillis();
		long timeout = 3000;
		while (API.key() != null) {
			try {
				if (MainEnvironment.this.isVisible()) {
					sleepTime = 100;
					if (MouseActivity.getInstance().getMouseY() < (UtilBox
							.getTaskbarOrientation() != UtilBox.NONEXISTANT ? getHeight()
							- UtilBox.getTaskbarHeight()
							: bHeight)
							&& !balloonIsShowing())
						showButtons = (System.currentTimeMillis() - lastInTime < timeout) ? true
								: false;
					else {
						lastInTime = System.currentTimeMillis();
						if (balloonIsShowing()) {
							showButtons = true;
						} else if (MouseActivity.getInstance().getMouseX() < 200
								|| MouseActivity.getInstance().getMouseX() > MainEnvironment.this
										.getSize().width - 200) {
							showButtons = true;
						}
					}

					if (showButtons) {
						if (currHeight > bHeight) {
							// Slide Down
							currHeight -= 8;
							if (currHeight <= bHeight) {
								currHeight = bHeight;
							}
							update();
							if (currHeight <= bHeight) {
								if (FurkManager.mediaEnabled()) {
									if (audioPlayerActive) {
										fnButtons[0].setVisible(true);
									}

									if (videoPlayerActive) {
										fnButtons[1].setVisible(true);
									}
								}
							}
							sleepTime = 40;
							repaint();
						}
					} else if (System.currentTimeMillis() - lastInTime >= timeout) {
						if (FurkManager.mediaEnabled()) {
							fnButtons[0].setVisible(false);
							fnButtons[1].setVisible(false);
						}
						if (currHeight < MainEnvironment.this.getHeight()) {
							currHeight += 8;
							if (currHeight > MainEnvironment.this.getHeight() + 4) {
								currHeight = MainEnvironment.this.getHeight() + 4;
							}
							update();
							sleepTime = 40;
							repaint();
						}
					}
				} else if (balloonIsShowing()) {
					showButtons = true;
					sleepTime = 20;
				} else {
					sleepTime = 400;
				}
				MainEnvironment.this.setAlwaysOnTop(!MainEnvironment.this
						.balloonIsShowing());
				MainEnvironment.this.repaint();
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
			}

		}
	}

	private boolean balloonIsShowing() {
		for (BalloonTip bt : balloons) {
			if (bt.isVisible()) {
				return true;
			}
		}
		if (mediaReady && FurkManager.mediaEnabled())
			for (BalloonTip bt : fnBalloons) {
				if (bt.isVisible()) {
					return true;
				}
			}
		return false;
	}

	private void update() {
		for (int i = 0; i < buttons.length; i++) {
			buttons[i].setLocation(buttons[i].getX(), currHeight);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			boolean active = false;

			/**
			 * Check Regular Buttons & Balloons
			 */
			for (int i = 0; i < balloons.length; i++) {
				if (contains(e, buttons[i])) {
					balloons[i].setVisible(!balloons[i].isVisible());
					// requestDimEnvironment(balloons[i].isVisible());

					active = balloons[i].isVisible();

				} else {
					balloons[i].setVisible(false);
				}
			}
			if (contains(e, buttons[balloons.length])) {
				MainEnvironment.this.setVisible(false);
			}

			/**
			 * Check Special-Functions Buttons & Balloons
			 */
			if (mediaReady && FurkManager.mediaEnabled())
				for (int i = 0; i < fnBalloons.length; i++) {
					if (contains(e, fnButtons[i])) {
						fnBalloons[i].setVisible(!fnBalloons[i].isVisible());
						// requestDimEnvironment(fnBalloons[i].isVisible());
						if (!active)
							active = fnBalloons[i].isVisible();
					} else {
						fnBalloons[i].setVisible(false);
					}
				}
			mediaCheck();

			requestDimEnvironment(balloonIsShowing());
		}

		if (e.getButton() == MouseEvent.BUTTON3) {

		}
	}

	private void mediaCheck() {
		if (!FurkManager.mediaEnabled())
			return;
		if (!mediaReady)
			return;
		if (fnBalloons[0].isVisible()
				|| AudioPlayerPanel.getInstance().isActive())
			return;
		fnButtons[0].setVisible(false);
		audioPlayerActive = false;

		if (fnBalloons[1].isVisible()
				|| VideoPlayerPanel.getInstance().isActive())
			return;
		fnButtons[1].setVisible(false);
		videoPlayerActive = false;
	}

	private void requestDimEnvironment(boolean dark) {
		bgc = new Color(Color.darkGray.getRed(), Color.darkGray.getBlue(),
				Color.darkGray.getGreen(), dark ? SettingsManager.getInstance()
						.dimEnvironment() ? darkness : 0 : 0);
		setBackground(bgc);
		MainEnvironment.this.repaint();
	}

	private boolean contains(MouseEvent e, CircleButton button) {
		return (e.getX() > button.getX()
				&& e.getX() < button.getX() + button.getWidth()
				&& e.getY() > button.getY() && e.getY() < button.getY()
				+ button.getHeight());
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public Window getWindow() {
		return MainEnvironment.this;
	}

	public boolean isAudioPlayerActive() {
		return audioPlayerActive;
	}

	public void setAudioPlayerActive(boolean audioPlayerActive) {
		this.audioPlayerActive = audioPlayerActive;
	}
}
