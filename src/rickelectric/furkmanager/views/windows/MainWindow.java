package rickelectric.furkmanager.views.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.Window;
import java.awt.Dialog.ModalExclusionType;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import rickelectric.UtilBox;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.APIMessage;
import rickelectric.furkmanager.network.FurkBridge;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.menus.Main_TopMenuBar;
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
import rickelectric.swingmods.OpacEffects;
import rickelectric.swingmods.Opacible;
import rickelectric.swingmods.Slideable;
import rickelectric.swingmods.TranslucentPane;

public class MainWindow extends AppFrameClass implements PrimaryEnv {
	private static final long serialVersionUID = 1L;

	private static MainWindow thisInstance = null;

	public static synchronized MainWindow getInstance() {
		if (thisInstance == null) {
			thisInstance = new MainWindow();
		}
		return thisInstance;
	}

	public static synchronized void destroyInstance() {
		if (thisInstance == null)
			return;
		thisInstance = null;
		System.gc();
	}

	private JPanel contentPane;
	private JFrame audioWin, videoWin;

	public static void main(String[] args) {
		JFrame f = new MainWindow();
		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}

	private JLabel[][] dashArray;
	private TranslucentPane[] currPanel;
	private int cpNum = 0;

	private MainWindow() {
		windowClose();

		Main_TopMenuBar bar = new Main_TopMenuBar();
		setJMenuBar(bar);

		contentPane = new JPanel();
		contentPane.setBackground(SystemColor.inactiveCaption);
		contentPane
				.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		super.setContentPane(contentPane);
		contentPane.setLayout(null);
		setSize(595, 620);
		setLocationRelativeTo(null);

		currPanel = new TranslucentPane[5];

		JPanel iconDashPanel = new JPanel();
		iconDashPanel.setBackground(Color.LIGHT_GRAY);
		iconDashPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null,
				null, null, null));
		iconDashPanel.setBounds(10, 10, 563, 103);
		contentPane.add(iconDashPanel);
		iconDashPanel.setLayout(null);

		dashArray = new JLabel[2][5];

		dashArray[0][0] = new JLabel("Files");
		dashArray[0][0].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[0][0].setFont(new Font("Tahoma", Font.BOLD, 12));
		dashArray[0][0].setBounds(10, 6, 78, 16);
		dashArray[0][0].setOpaque(true);
		dashArray[0][0].setBorder(new BevelBorder(BevelBorder.RAISED, null,
				null, null, null));
		iconDashPanel.add(dashArray[0][0]);

		dashArray[0][1] = new JLabel("Downloads");
		dashArray[0][1].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[0][1].setFont(new Font("Tahoma", Font.BOLD, 12));
		dashArray[0][1].setBounds(122, 6, 78, 16);
		iconDashPanel.add(dashArray[0][1]);

		dashArray[0][2] = new JLabel("RSS Feeds");
		dashArray[0][2].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[0][2].setFont(new Font("Tahoma", Font.BOLD, 12));
		dashArray[0][2].setBounds(240, 6, 78, 16);
		iconDashPanel.add(dashArray[0][2]);

		dashArray[0][3] = new JLabel("Settings");
		dashArray[0][3].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[0][3].setFont(new Font("Tahoma", Font.BOLD, 12));
		dashArray[0][3].setBounds(358, 6, 78, 16);
		iconDashPanel.add(dashArray[0][3]);

		dashArray[0][4] = new JLabel("User");
		dashArray[0][4].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[0][4].setFont(new Font("Tahoma", Font.BOLD, 12));
		dashArray[0][4].setBounds(475, 7, 78, 16);
		iconDashPanel.add(dashArray[0][4]);

		MouseAdapter iconEffect = new MouseAdapter() {

			static final int IN = 1, OUT = 2, CLICKED = 3;

			int borderState = OUT;

			@Override
			public void mouseEntered(MouseEvent e) {
				borderState = IN;
				JLabel src = (JLabel) e.getSource();
				src.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
						null, null));
				src.setHorizontalAlignment(SwingConstants.LEADING);

			}

			@Override
			public void mouseExited(MouseEvent e) {
				borderState = OUT;
				JLabel src = (JLabel) e.getSource();
				src.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
				src.setHorizontalAlignment(SwingConstants.CENTER);
			}

			@Override
			public void mousePressed(MouseEvent e) {
				borderState = CLICKED;
				JLabel src = (JLabel) e.getSource();
				src.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
						null, null));
				if (e.getButton() == MouseEvent.BUTTON1) {
					for (int i = 0; i < 5; i++) {
						if (e.getSource().equals(dashArray[1][i])) {
							changeViewSection(i);
						}
					}
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if (borderState != CLICKED)
					return;
				JLabel src = (JLabel) e.getSource();
				src.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
						null, null));
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (paneChanging)
					return;
				if (e.getButton() == MouseEvent.BUTTON1) {
					for (int i = 0; i < 5; i++) {
						// changeViewSection(i);
						if (e.getSource().equals(dashArray[1][i])) {
							if (e.getClickCount() > 1 && cpNum == i) {
								if (e.getClickCount() == 3) {
									new Thread(new Runnable() {
										@Override
										public void run() {
											FurkBridge.overrideCache(true);
											UtilBox.getInstance().wait(500);
											FurkBridge.overrideCache(false);
										}
									}).start();
								}
								if (i == 0) {
									((Main_FileView) currPanel[0])
											.refreshMyFiles(true);
								} else if (i == 1) {
									((Main_DownloadView) currPanel[1])
											.refreshMyDownloads(true);
								}
							}
						}
					}
				}
			}
		};

		dashArray[1][0] = new JLabel("");
		dashArray[1][0].addMouseListener(iconEffect);
		dashArray[1][0].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[1][0].setIcon(new ImageIcon(ImageLoader.getInstance()
				.getImage("dash/Files-64.png")));
		dashArray[1][0].setBorder(new EtchedBorder(EtchedBorder.RAISED, null,
				null));
		dashArray[1][0].setBounds(10, 24, 78, 68);
		iconDashPanel.add(dashArray[1][0]);

		dashArray[1][1] = new JLabel("");
		dashArray[1][1].addMouseListener(iconEffect);
		dashArray[1][1].setIcon(new ImageIcon(ImageLoader.getInstance()
				.getImage("dash/Download-64.png")));
		dashArray[1][1].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[1][1].setBorder(new EtchedBorder(EtchedBorder.RAISED, null,
				null));
		dashArray[1][1].setBounds(122, 24, 78, 68);
		iconDashPanel.add(dashArray[1][1]);

		dashArray[1][2] = new JLabel("");
		dashArray[1][2].addMouseListener(iconEffect);
		dashArray[1][2].setIcon(new ImageIcon(ImageLoader.getInstance()
				.getImage("dash/RSS-64.png")));
		dashArray[1][2].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[1][2].setBorder(new EtchedBorder(EtchedBorder.RAISED, null,
				null));
		dashArray[1][2].setBounds(240, 24, 78, 68);
		iconDashPanel.add(dashArray[1][2]);

		dashArray[1][3] = new JLabel("");
		dashArray[1][3].addMouseListener(iconEffect);
		dashArray[1][3].setIcon(new ImageIcon(ImageLoader.getInstance()
				.getImage("dash/Settings-64.png")));
		dashArray[1][3].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[1][3].setBorder(new EtchedBorder(EtchedBorder.RAISED, null,
				null));
		dashArray[1][3].setBounds(358, 24, 78, 68);
		iconDashPanel.add(dashArray[1][3]);

		dashArray[1][4] = new JLabel("");
		dashArray[1][4].addMouseListener(iconEffect);
		dashArray[1][4].setIcon(new ImageIcon(ImageLoader.getInstance()
				.getImage("dash/User-64.png")));
		dashArray[1][4].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[1][4].setBorder(new EtchedBorder(EtchedBorder.RAISED, null,
				null));
		dashArray[1][4].setBounds(475, 24, 78, 68);
		iconDashPanel.add(dashArray[1][4]);

		TranslucentPane curr;
		curr = new Main_FileView();
		curr.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		curr.setBounds(10, 124, 563, 400);
		curr.setAlpha(1);
		contentPane.add(curr);
		currPanel[0] = curr;

		curr = new Main_DownloadView();
		curr.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		curr.setBounds(10, 124, 563, 400);
		curr.setAlpha(1);
		curr.setVisible(false);
		contentPane.add(curr);
		currPanel[1] = curr;

		curr = new Main_FeedView();
		curr.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		curr.setBounds(10, 124, 563, 400);
		curr.setAlpha(1);
		curr.setVisible(false);
		contentPane.add(curr);
		currPanel[2] = curr;

		curr = new Main_SettingsView();
		curr.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		curr.setBounds(10, 124, 563, 400);
		curr.setAlpha(1);
		curr.setVisible(false);
		contentPane.add(curr);
		currPanel[3] = curr;

		curr = new Main_UserView();
		curr.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		curr.setBounds(10, 124, 563, 400);
		curr.setAlpha(1);
		curr.setVisible(false);
		contentPane.add(curr);
		currPanel[4] = curr;

		message = new JLabel();
		loadMessages();
		message.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (message.getText().equals(""))
					return;
				JOptionPane.showMessageDialog(null,
						message.getText().replace(". ", ". \n"), "Message",
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		message.setVerticalAlignment(SwingConstants.TOP);
		message.setHorizontalAlignment(SwingConstants.CENTER);
		message.setFont(new Font("Dialog", Font.BOLD, 14));
		message.setBounds(10, 530, 563, 32);
		contentPane.add(message);

		mediaCheck();

		addConsole();
		addImgCacheViewer();
		setResizable(false);
		// setVisible(true);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (FurkManager.mediaEnabled()) {
			if (audioWin != null && audioWin.isVisible()) {
				audioWin.dispose();
			}
			if (videoWin != null && videoWin.isVisible()) {
				videoWin.dispose();
			}
		}

	}

	private void mediaCheck() {
		if (!FurkManager.mediaEnabled())
			return;
		try {
			if (AudioPlayer.getInstance().isActive()) {
				constructAudioWindow();
				audioWin.setVisible(true);
			}
			if (VideoPlayer.getInstance().isActive()) {
				constructVideoWindow();
				videoWin.setVisible(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
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

	private boolean paneChanging = false;
	private JLabel message;

	private void changeViewSection(final int sec) {
		// TODO Invoke Panel Fade & Slide Out & In Change Depending On Current
		// sec
		if (cpNum == sec)
			return;
		if (paneChanging || currPanel[cpNum].isSliding())
			return;
		paneChanging = true;

		if (SettingsManager.getInstance().slideDashWin()) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					Point loc = currPanel[cpNum].getLocation();
					if (!currPanel[cpNum].isVisible())
						throw new RuntimeException("Invisible Current Panel");
					try {
						int direction = Slideable.LEFT;
						int locIn = 25;
						if (sec < cpNum) {
							direction = Slideable.RIGHT;
							locIn = -5;
						}
						OpacEffects.slide(currPanel[cpNum], 15, direction, 50,
								Opacible.OUT);
						UtilBox.getInstance().wait(100);
						while (currPanel[cpNum].isSliding())
							;

						currPanel[sec].setLocation(locIn, 124);
						OpacEffects.slide(currPanel[sec], 15, direction, 18,
								Opacible.IN);
						cpNum = sec;
						paneChanging = false;
						dashUpdate();
					} catch (RuntimeException e) {
						e.printStackTrace();
						currPanel[cpNum].setLocation(loc);
						currPanel[cpNum].setAlpha(1f);

						currPanel[sec].setVisible(false);
						currPanel[sec].setAlpha(0f);
						cpNum = sec;
						paneChanging = false;
						throw e;
					}
					paneChanging = false;
				}
			}).start();
		} else {
			if (cpNum != sec) {
				currPanel[sec].setLocation(currPanel[cpNum].getLocation());
				currPanel[cpNum].setVisible(false);
				currPanel[cpNum].setAlpha(0f);
				currPanel[sec].setAlpha(1f);
				currPanel[sec].setVisible(true);
				cpNum = sec;
			}
			paneChanging = false;
			dashUpdate();
		}
	}

	protected void dashUpdate() {
		for (int i = 0; i < 5; i++) {
			dashArray[0][i].setOpaque(false);
			dashArray[0][i].setBorder(null);
		}
		dashArray[0][cpNum].setOpaque(true);
		dashArray[0][cpNum].setBorder(new BevelBorder(BevelBorder.RAISED, null,
				null, null, null));
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

	@Override
	public void mediaCall(int mediaType, String mrl) {
		boolean me = false;
		try {
			Class<?> c = Class.forName("rickelectric.media.AudioPlayer");
			if (c != null)
				me = true;
		} catch (Exception e) {
		}
		if (!FurkManager.mediaEnabled() && !me)
			return;
		if (mediaType == AUDIO) {
			constructAudioWindow();
			audioWin.setVisible(true);
			AudioPlayer.getInstance().play(mrl);
		} else if (mediaType == VIDEO) {
			VideoPlayer.getInstance().play(mrl);
			constructVideoWindow();
			videoWin.setVisible(true);
		}
	}

	private void constructAudioWindow() {
		if (audioWin == null) {
			audioWin = new JFrame("Furk Music Player");
			audioWin.setIconImage(ImageLoader.getInstance().getImage(
					"tree/audio-48.png"));
			audioWin.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
			audioWin.setContentPane(new AudioPanel());
			audioWin.setResizable(false);
			audioWin.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					AudioPlayer.getInstance().stop();
				}
			});
			audioWin.pack();
			audioWin.setLocationRelativeTo(this);
			audioWin.setLocation(this.getWidth(), this.getY());
		}
	}

	private void constructVideoWindow() {
		if (videoWin == null) {
			videoWin = new JFrame("Furk Music Player");
			videoWin.setIconImage(ImageLoader.getInstance().getImage(
					"tree/video-48.png"));
			videoWin.setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
			videoWin.setContentPane(new VideoPanel());
			// videoWin.setResizable(false);
			videoWin.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					VideoPlayer.getInstance().stop();
				}
			});
			videoWin.setMinimumSize(videoWin.getContentPane()
					.getPreferredSize());
			videoWin.pack();
			videoWin.setLocationRelativeTo(this);
			videoWin.setLocation(this.getWidth(), this.getY());
		}
	}

	@Override
	public Window getWindow() {
		return MainWindow.this;
	}

	@Override
	public void setVisible(boolean b) {
		super.setVisible(b);
		mediaCheck();
	}

	public TranslucentPane getView(int i) {
		return currPanel[i];
	}
}