package rickelectric.furkmanager.views.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsEnvironment;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.styles.RoundedBalloonStyle;
import rickelectric.furkmanager.models.APIMessage;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.CircleButton;
import rickelectric.furkmanager.views.panels.Main_DownloadView;
import rickelectric.furkmanager.views.panels.Main_FeedView;
import rickelectric.furkmanager.views.panels.Main_FileView;
import rickelectric.furkmanager.views.panels.Main_SettingsView;
import rickelectric.furkmanager.views.panels.Main_UserView;
import rickelectric.furkmanager.views.swingmods.balloon.BTipPositioner;

public class MainEnvironment extends JDialog implements PrimaryEnv {
	private static final long serialVersionUID = 1L;

	private CircleButton[] buttons;
	private BalloonTip[] balloons;
	private String[] names;

	private JPanel contentPane;
	private Color bgc;
	private JLabel message;

	private JComponent[] sections;

	public MainEnvironment() {
		super();
		getContentPane().setLayout(null);
		setUndecorated(true);
		bgc = new Color(Color.darkGray.getRed(), Color.darkGray.getBlue(),
				Color.darkGray.getGreen(), 0);
		setBackground(bgc);
		setAlwaysOnTop(true);

		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paint(Graphics g) {
				// super.paint(g);
				g.setColor(Color.lightGray);
				for (int i = 0; i < buttons.length; i++) {
					if (buttons[i] != null) {
						buttons[i].paint(g);
						if (balloons[i].isVisible()
								&& SettingsManager.dimEnvironment()) {
							g.setColor(Color.black);
							g.setFont(new Font(Font.SERIF, Font.BOLD
									| Font.ITALIC, 38));
							g.drawString(names[i], balloons[i].getX(),
									balloons[i].getY() - 10);
						}
					}
				}
			}
		};
		contentPane.setLayout(null);
		contentPane.setBackground(getBackground());
		setContentPane(contentPane);

		// setDefaultCloseOperation(EXIT_ON_CLOSE);

		buttons = new CircleButton[5];
		balloons = new BalloonTip[5];
		names = new String[5];

		setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds());

		message = new JLabel();
		message.setSize(getWidth() - 20, 50);
		message.setLocation(10, 10);
		message.setOpaque(false);
		getContentPane().add(message);

		buttons[0] = new CircleButton("Files", getClass().getResource(
				"/rickelectric/furkmanager/img/dash/Files-64.png"));
		buttons[0].setLocation(50, getHeight() - 110);
		contentPane.add(buttons[0]);

		buttons[1] = new CircleButton("Downloads", getClass().getResource(
				"/rickelectric/furkmanager/img/dash/Download-64.png"));
		buttons[1].setLocation(170, getHeight() - 110);
		contentPane.add(buttons[1]);

		buttons[2] = new CircleButton("Feeds", getClass().getResource(
				"/rickelectric/furkmanager/img/dash/RSS-64.png"));
		buttons[2].setLocation(290, getHeight() - 110);
		contentPane.add(buttons[2]);

		buttons[3] = new CircleButton("Application Settings", getClass()
				.getResource(
						"/rickelectric/furkmanager/img/dash/Settings-64.png"));
		buttons[3].setLocation(430, getHeight() - 110);
		contentPane.add(buttons[3]);

		buttons[4] = new CircleButton("Furk User Account", getClass()
				.getResource("/rickelectric/furkmanager/img/dash/User-64.png"));
		buttons[4].setLocation(570, getHeight() - 110);
		contentPane.add(buttons[4]);

		btnConstruct();
	}

	private void btnConstruct() {
		sections = new JComponent[] { new Main_FileView(),
				new Main_DownloadView(), new Main_FeedView(),
				new Main_SettingsView(), new Main_UserView() };
		names = new String[] { "My Files", "My Downloads", "RSS Feeds",
				"Application Settings", "User Account Settings" };

		for (int i = 0; i < balloons.length; i++) {
			balloons[i] = new BalloonTip(buttons[i], sections[i],
					new RoundedBalloonStyle(10, 10,
							sections[i].getBackground(), Color.BLUE),
					new BTipPositioner(), null);

			balloons[i].setVisible(false);
		}

		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON1) {
					boolean active = false;
					for (int i = 0; i < balloons.length; i++) {
						if (e.getX() > buttons[i].getX()
								&& e.getX() < buttons[i].getX()
										+ buttons[i].getWidth()
								&& e.getY() > buttons[i].getY()
								&& e.getY() < buttons[i].getY()
										+ buttons[i].getHeight()) {
							balloons[i].setVisible(!balloons[i].isVisible());
							bgc = new Color(Color.darkGray.getRed(),
									Color.darkGray.getBlue(),
									Color.darkGray.getGreen(),
									balloons[i].isVisible() ? SettingsManager
											.dimEnvironment() ? 40 : 0 : 0);
							setBackground(bgc);
							active = balloons[i].isVisible();
							MainEnvironment.this.repaint();
						} else {
							balloons[i].setVisible(false);
						}
					}
					bgc = new Color(Color.darkGray.getRed(),
							Color.darkGray.getBlue(),
							Color.darkGray.getGreen(),
							active ? SettingsManager.dimEnvironment() ? 40 : 0
									: 0);
					setBackground(bgc);
				}

				if (e.getButton() == MouseEvent.BUTTON3) {

				}
			}
		});
	}

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

	public void settings() {
		changeViewSection(3);
	}

	private void changeViewSection(int i) {
		for (int x = 0; x < balloons.length; x++) {
			balloons[x].setVisible(false);
		}
		balloons[i].setVisible(true);
		bgc = new Color(Color.darkGray.getRed(), Color.darkGray.getBlue(),
				Color.darkGray.getGreen(),
				SettingsManager.dimEnvironment() ? 40 : 0);
		setBackground(bgc);
		MainEnvironment.this.repaint();
	}

	public void userSettings() {
		changeViewSection(4);
	}

	/**
	 * Show The Buttons
	 */
	public void slideIn() {

	}

	/**
	 * Hide The Buttons
	 */
	public void slideOut() {

	}

	public static void main(String[] args) {
		SettingsManager.init();
		new MainEnvironment().setVisible(true);
	}

	public void setStatus(String string) {

	}

	public void refreshBgc() {
		bgc = new Color(Color.darkGray.getRed(), Color.darkGray.getBlue(),
				Color.darkGray.getGreen(),
				SettingsManager.dimEnvironment() ? 40 : 0);
		setBackground(bgc);
		MainEnvironment.this.repaint();
	}

	public int popupHeight() {
		return getHeight() - (buttons[0].getY() - 20);
	}

}
