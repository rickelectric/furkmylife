package rickelectric.furkmanager.views.windows;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.Window;

import javax.swing.JComponent;
import javax.swing.JPanel;

import rickelectric.UtilBox;
import rickelectric.desktop.views.windows.MainEnvironment;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.views.panels.Main_DownloadView;
import rickelectric.furkmanager.views.panels.Main_FeedView;
import rickelectric.furkmanager.views.panels.Main_FileView;
import rickelectric.furkmanager.views.panels.Main_SettingsView;
import rickelectric.furkmanager.views.panels.Main_UserView;
import rickelectric.img.ImageLoader;
import rickelectric.media.AudioPanel;
import rickelectric.media.AudioPlayer;
import rickelectric.media.VideoPanel;
import rickelectric.media.VideoPlayer;
import rickelectric.swingmods.CircleButton;
import rickelectric.swingmods.TranslucentPane;

public class MainEnv implements PrimaryEnv {

	private static MainEnv thisInstance = null;

	public static synchronized MainEnv getInstance() {
		if (thisInstance == null) {
			thisInstance = new MainEnv();
		}
		return thisInstance;
	}

	public static synchronized void destroyInstance() {
		thisInstance = null;
		MainEnvironment.destroyInstance();
		System.gc();
	}

	private CircleButton[] buttons;
	private CircleButton[] fnButtons;
	private CircleButton[] refreshButtons;

	//private BalloonTip[] balloons;
	//private BalloonTip[] fnBalloons;

	private TranslucentPane[] sections;
	private JComponent[] fnSections;

	
	private int bHeight;

	private boolean mediaReady;

	// TODO Animation Implementation Of Button AND Balloon
	// Show/Hide/Pop-In/Pop-Out

	int darkness = 40;

	private MainEnv() {
		super();

		mediaReady = false;

		buttons = new CircleButton[5];
		//balloons = new BalloonTip[5];

		// Audio & Video Player
		fnButtons = new CircleButton[2];
		//fnBalloons = new BalloonTip[2];

		refreshButtons = new CircleButton[2];

		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setLocationRelativeTo(null);

		bHeight = getHeight()
				- (UtilBox.getInstance().getTaskbarOrientation() == UtilBox.BOTTOM ? UtilBox
						.getInstance().getTaskbarHeight() : 0) - 100;
		
		generateButtonsAndBalloons();
		generateFnButtons();
		generateRefreshButtons();
		
		fnBalloonConstruct();

		mediaActiveCheck();
	}

	private void setSize(Dimension d) {
		MainEnvironment.getInstance().setSize(d);
	}

	public void mediaActiveCheck() {
		if (!FurkManager.mediaEnabled())
			return;
		if (AudioPlayer.getInstance().isActive()) {
			if (!mediaReady) {
				fnBalloonConstruct();
			}
			fnButtons[0].setVisible(true);
			//fnBalloons[0].setVisible(true);
		}
		if (VideoPlayer.getInstance().isActive()) {
			if (!mediaReady) {
				fnBalloonConstruct();
			}
			fnButtons[1].setVisible(true);
			//fnBalloons[1].setVisible(true);
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
			if (AudioPlayer.getInstance().play(mrl)) {
				fnButtons[0].setVisible(true);
				//fnBalloons[0].setVisible(true);
				repaint();
			}
		} else if (mediaType == VIDEO) {
			fnButtons[1].setVisible(true);
			//fnBalloons[1].setVisible(true);
			repaint();
			VideoPlayer.getInstance().play(mrl);
		}
	}

	private void repaint() {
		MainEnvironment.getInstance().repaint();
	}

	private void generateButtonsAndBalloons() {
		int xLoc = 50, inc = 120;
		buttons[0] = new CircleButton("My Files", ImageLoader.getInstance()
				.getImage("dash/Files-64.png"));
		buttons[0].setLocation(xLoc, bHeight);
		xLoc += inc;

		buttons[1] = new CircleButton("My Downloads", ImageLoader.getInstance()
				.getImage("dash/Download-64.png"));
		buttons[1].setLocation(xLoc, bHeight);
		xLoc += inc;

//		buttons[2] = new CircleButton("RSS Feeds", ImageLoader.getInstance()
//				.getImage("dash/RSS-64.png"));
//		buttons[2].setLocation(xLoc, bHeight);
//		xLoc += inc;

		buttons[3] = new CircleButton("Furk App Settings", ImageLoader
				.getInstance().getImage("dash/Settings-64.png"));
		buttons[3].setLocation(xLoc, bHeight);
		xLoc += inc;

		buttons[4] = new CircleButton("Furk User Options", ImageLoader
				.getInstance().getImage("dash/User-64.png"));
		buttons[4].setLocation(xLoc, bHeight);
		
		sections = new TranslucentPane[] { new Main_FileView(),
				new Main_DownloadView(), new Main_FeedView(),
				new Main_SettingsView(), new Main_UserView() };

		for (int i = 0; i < sections.length; i++) {
			if(buttons[i]!=null)
			MainEnvironment.getInstance().addItemPair(
					"extensions.furkmanager." + buttons[i].getPopup(),
					buttons[i], sections[i]);
		}
		
	}

	private void generateFnButtons() {
		if (!FurkManager.mediaEnabled())
			return;
		int xLoc = 700, inc = 120;
		int yLoc = 555;

		fnButtons[1] = new CircleButton("Video Player", ImageLoader
				.getInstance().getImage("tree/video-48.png"));
		fnButtons[1].setLocation(xLoc, yLoc);
		fnButtons[1].setSize(buttons[0].getSize());
		fnButtons[1].setVisible(false);
		// contentPane.add(fnButtons[1]);

		xLoc += inc;

		fnButtons[0] = new CircleButton("Music Player", ImageLoader
				.getInstance().getImage("tree/audio-48.png"));
		fnButtons[0].setLocation(xLoc, yLoc);
		fnButtons[0].setSize(buttons[0].getSize());
		fnButtons[0].setVisible(false);
		// contentPane.add(fnButtons[0]);

	}

	private void generateRefreshButtons() {
		refreshButtons[0] = new CircleButton("Refresh Files", ImageLoader
				.getInstance().getImage("dash/Reload-32.png"));
		refreshButtons[0].setStrokeSize(3);
		Point loc0 = buttons[0].getLocation();
		Dimension sz0 = buttons[0].getSize();
		refreshButtons[0].setLocation(
				loc0.x + sz0.width - refreshButtons[0].getWidth(), loc0.y
						+ sz0.height - refreshButtons[0].getHeight());

		refreshButtons[1] = new CircleButton("Refresh Downloads", ImageLoader
				.getInstance().getImage("dash/Reload-32.png"));
		refreshButtons[1].setStrokeSize(3);
		Point loc1 = buttons[1].getLocation();
		Dimension sz1 = buttons[1].getSize();
		refreshButtons[1].setLocation(
				loc1.x + sz1.width - refreshButtons[1].getWidth(), loc1.y
						+ sz1.height - refreshButtons[1].getHeight());
		
		Runnable[] refreshRunners = new Runnable[]{
				new Runnable(){//File Refresh
					public void run(){
						((Main_FileView)sections[0]).refreshActive(true);
					}
				},
				new Runnable(){//Dls Refresh
					public void run(){
						((Main_DownloadView)sections[1]).refreshActive(true);
					}
				}
		};
		
		for (int i = 0; i < refreshButtons.length; i++) {
			if(refreshButtons[i]!=null){
				buttons[i].tetherButton(refreshButtons[i]);
				MainEnvironment.getInstance().addItemPair(
						"extensions.furkmanager." + refreshButtons[i].getPopup(),
						refreshButtons[i], null);
				MainEnvironment.getInstance().addRunner(
						"extensions.furkmanager." + refreshButtons[i].getPopup(),
						refreshRunners[i]
				);
			}
		}
	}

	private void fnBalloonConstruct() {
		if (!FurkManager.mediaEnabled())
			return;
		fnSections = new JPanel[] { new AudioPanel(), new VideoPanel() };

		for (int i = 0; i < fnSections.length; i++) {
			MainEnvironment.getInstance().addItemPair(
					"extensions.media." + fnButtons[i].getPopup(),
					fnButtons[i], (JPanel) fnSections[i]);
		}
		mediaReady = true;
	}

	@Override
	public void loadMessages() {
//		try {
//			ArrayList<APIMessage> messages = API.getMessages();
//			if (messages == null || messages.size() == 0)
//				message.setText("");
//			String text = "";
//			for (APIMessage msg : messages) {
//				text += msg.getType().toUpperCase() + ": \n" + msg.getText()
//						+ "\n\n";
//			}
//			message.setText(text);
//		} catch (Exception e) {
//		}
	}

	@Override
	public void main() {
		MainEnvironment.getInstance().invokeButton("extensions.furkmanager.My Files");
	}

	@Override
	public void settings() {
		MainEnvironment.getInstance().invokeButton("extensions.furkmanager.Furk App Settings");
	}

	@Override
	public void userSettings() {
		MainEnvironment.getInstance().invokeButton("extensions.furkmanager.Furk User Options");
	}

	@Override
	public void setStatus(String string) {

	}

	public void refreshBgc() {
		MainEnvironment.getInstance().refreshBgc();
	}

	public int popupHeight() {
		return getHeight() - (buttons[0].getY() - 20);
	}

	private int getHeight() {
		return MainEnvironment.getInstance().getHeight();
	}

	@Override
	public void setVisible(boolean b) {
		MainEnvironment.getInstance().setVisible(b);
	}

	@Override
	public Window getWindow() {
		return MainEnvironment.getInstance();
	}

	public TranslucentPane getView(int i) {
		return sections[i];
	}

	@Override
	public boolean isVisible() {
		return MainEnvironment.getInstance().isVisible();
	}

	@Override
	public void toFront() {
		MainEnvironment.getInstance().toFront();
		
	}

	@Override
	public void setLocationRelativeTo(Component c) {
		MainEnvironment.getInstance().setLocationRelativeTo(c);
	}

	@Override
	public void setEnabled(boolean b) {
		MainEnvironment.getInstance().setEnabled(b);
	}

	@Override
	public void dispose() {
		MainEnvironment.getInstance().dispose();
	}

	@Override
	public Component getContentPane() {
		return MainEnvironment.getInstance().getContentPane();
	}

	@Override
	public boolean isAlwaysOnTop() {
		return MainEnvironment.getInstance().isAlwaysOnTop();
	}

	@Override
	public void setAlwaysOnTop(boolean b) {
		MainEnvironment.getInstance().setAlwaysOnTop(b);
	}
}
