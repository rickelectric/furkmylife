package rickelectric.furkmanager.views;

import java.awt.Color;
import java.awt.Dialog.ModalityType;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.awt.image.ImageObserver;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import rickelectric.UtilBox;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.data.HelpData;
import rickelectric.furkmanager.models.LoginModel;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.network.api.API_UserData;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.views.panels.Settings_ProxyPorts;
import rickelectric.furkmanager.views.panels.Settings_UIPanel;
import rickelectric.img.ImageLoader;
import rickelectric.swingmods.JButtonLabel;

public class LoginSplashWindow extends JFrame implements Runnable {
	private static final long serialVersionUID = 1L;
	private Image splashImage;
	private Image loadingImage;

	private JPanel contentPane;
	private JTextField input_username;
	private JPasswordField input_password;
	private JLabel lblUsername;
	private JLabel lblPassword;
	private JButtonLabel btn_X;
	private JButtonLabel btn_min;
	private JButtonLabel button_userlogin;
	private JTextField input_apikey;
	private JButtonLabel button_apilogin;
	private JLabel label_key;
	private boolean loggingIn;
	
	private Thread loginProcessThread=null;

	private Point startPoint = null;
	private boolean moveable = true;

	private String splashText = "";

	private JPanel panel_login;
	private Rectangle label_loading;
	private JCheckBox check_savecreds, check_auto;
	private JDialog sf = null, uf = null;
	private JButtonLabel btn_apihelp, btn_env;

	public LoginSplashWindow() {
		setUndecorated(true);
		setBackground(new Color(255, 255, 255, 0));
		setIconImage(new ImageIcon(ImageLoader.getInstance().getImage("fr.png"))
				.getImage());

		contentPane = new JPanel();
		contentPane.setBackground(getBackground());
		setContentPane(contentPane);
		contentPane.setIgnoreRepaint(true);
		contentPane.setLayout(null);

		btn_X = new JButtonLabel("", new Runnable() {
			@Override
			public void run() {
				setText("Exiting...");
				FurkManager.exit();
			}
		});
		btn_X.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
				"sm/edit_delete.png")));
		btn_X.setBounds(639, 11, 39, 23);
		contentPane.add(btn_X);

		btn_min = new JButtonLabel("", new Runnable() {
			@Override
			public void run() {
				setState(ICONIFIED);
			}
		});
		btn_min.setIcon(new ImageIcon(
				LoginSplashWindow.class
						.getResource("/javax/swing/plaf/metal/icons/ocean/minimize.gif")));
		btn_min.setBounds(590, 11, 39, 23);
		btn_min.setToolTipText("Back (Cancel Login)");
		contentPane.add(btn_min);
		
		btn_back = new JButtonLabel("", new Runnable(){
			public void run(){
				cancelLogin();
			}
		});
		btn_back.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
				"arrow_double_left.png")));
		btn_back.setBounds(541, 11, 39, 23);
		btn_back.setVisible(false);
		contentPane.add(btn_back);

		panel_login = new JPanel();
		panel_login.setOpaque(false);
		panel_login.setBounds(218, 171, 424, 208);
		contentPane.add(panel_login);
		panel_login.setLayout(null);

		label_loading = new Rectangle(420, 207, 298, 208);
		loadingImage = Toolkit.getDefaultToolkit().getImage(
				ImageLoader.class.getResource("ajax-loader-128.gif"));

		lblUsername = new JLabel("Username / Email:");
		lblUsername.setBounds(143, 11, 89, 14);
		panel_login.add(lblUsername);

		input_username = new JTextField();
		input_username.setBounds(143, 25, 269, 20);
		panel_login.add(input_username);
		input_username.setColumns(10);
		input_username.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (input_username.getBackground() == Color.PINK)
					input_username.setBackground(Color.WHITE);
			}
		});
		input_username.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					input_password.requestFocus();
				}
			}
		});
		lblUsername.setLabelFor(input_username);

		lblPassword = new JLabel("Password:");
		lblPassword.setBounds(143, 56, 89, 14);
		panel_login.add(lblPassword);

		input_password = new JPasswordField();
		input_password.setBounds(143, 70, 269, 20);
		panel_login.add(input_password);

		input_password.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e) {
				if (input_password.getBackground() == Color.PINK)
					input_password.setBackground(Color.WHITE);
			}
		});
		input_password.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER) {
					ThreadPool.run(new Runnable() {
						@Override
						public void run() {
							login();
						}
					});
				}
			}
		});

		button_userlogin = new JButtonLabel("User Login", new Runnable() {
			@Override
			public void run() {
				loginProcessThread = new Thread(new Runnable() {
					@Override
					public void run() {
						login();
					}
				});
				loginProcessThread.setDaemon(true);
				loginProcessThread.start();
			}
		});
		button_userlogin.setBounds(284, 101, 128, 20);
		panel_login.add(button_userlogin);

		label_key = new JLabel("API Key:");
		label_key.setBounds(143, 128, 89, 14);
		panel_login.add(label_key);
		label_key.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Remove This Bypass When Complete
				if (e.getButton() == MouseEvent.BUTTON3)
					input_apikey
							.setText("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
			}
		});

		input_apikey = new JTextField();
		input_apikey.setBounds(143, 142, 269, 20);
		panel_login.add(input_apikey);
		input_apikey.setText((String) null);
		input_apikey.setColumns(10);

		button_apilogin = new JButtonLabel("API Login", new Runnable() {
			@Override
			public void run() {
				loginProcessThread = new Thread(new Runnable() {
					@Override
					public void run() {
						apiLogin();
					}
				});
				loginProcessThread.setDaemon(true);
				loginProcessThread.start();

			}
		});
		button_apilogin.setBounds(284, 173, 128, 20);
		panel_login.add(button_apilogin);

		check_savecreds = new JCheckBox("Save Credentials");
		check_savecreds.setOpaque(false);
		check_savecreds.setBounds(10, 170, 123, 23);
		panel_login.add(check_savecreds);

		check_auto = new JCheckBox("Auto-Login");
		check_auto.setOpaque(false);
		check_auto.setBounds(143, 169, 101, 23);
		panel_login.add(check_auto);

		btn_apihelp = new JButtonLabel("  API Key Help", new Runnable() {
			@Override
			public void run() {
				setText("Opening Help. Please Wait...");
				JEditorPane editorPane = new JEditorPane();
				editorPane.setContentType("text/html");
				editorPane.setText(HelpData.apiKey(true, true));
				editorPane.setEditable(false);
				editorPane.addHyperlinkListener(new HyperlinkListener() {
					@Override
					public void hyperlinkUpdate(HyperlinkEvent e) {
						if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
							UtilBox.getInstance().openUrl(
									e.getURL().toExternalForm());
						}
					}
				});
				JOptionPane.showMessageDialog(contentPane, editorPane,
						"API Key Help", JOptionPane.PLAIN_MESSAGE);
				clearText();
			}
		});
		btn_apihelp.setHorizontalAlignment(SwingConstants.LEFT);
		btn_apihelp.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
				"dash/User-16.png")));
		btn_apihelp.setForeground(Color.BLUE);
		btn_apihelp.setBounds(10, 139, 123, 25);
		panel_login.add(btn_apihelp);

		JButtonLabel btn_settings = new JButtonLabel("  Proxy Settings",
				new Runnable() {
					@Override
					public void run() {
						if (sf != null) {
							sf.setVisible(true);
							sf.toFront();
							return;
						}
						sf = new JDialog(LoginSplashWindow.this,
								"Proxy Settings");
						sf.setModal(true);
						sf.setModalityType(ModalityType.APPLICATION_MODAL);
						sf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						Settings_ProxyPorts pp = new Settings_ProxyPorts();
						pp.extDisable();
						pp.closeOnSave(sf);
						sf.getContentPane().add(pp);
						sf.setResizable(false);
						sf.pack();
						sf.setLocationRelativeTo(contentPane);
						sf.setVisible(true);
					}
				});
		btn_settings.setHorizontalAlignment(SwingConstants.LEFT);
		btn_settings.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
				"dash/Settings-16.png")));
		btn_settings.setBounds(10, 67, 123, 25);
		panel_login.add(btn_settings);

		btn_env = new JButtonLabel("  Environment", new Runnable() {
			@Override
			public void run() {
				if (uf != null) {
					uf.setVisible(true);
					uf.toFront();
					return;
				}
				uf = new JDialog(LoginSplashWindow.this, "Environment");
				uf.setModal(true);
				uf.setModalityType(ModalityType.APPLICATION_MODAL);
				uf.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				Settings_UIPanel pp = new Settings_UIPanel();
				pp.closeOnSave(uf);
				uf.getContentPane().add(pp);
				uf.setResizable(false);
				uf.pack();
				uf.setLocationRelativeTo(contentPane);
				uf.setVisible(true);
			}
		});
		btn_env.setHorizontalAlignment(SwingConstants.LEFT);
		btn_apihelp.setForeground(Color.RED);
		btn_env.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
				"sm/menu_expand.png")));
		btn_env.setBounds(10, 25, 123, 25);
		panel_login.add(btn_env);

		splashImage = ImageLoader.getInstance().getImage("Splash_Big.png");
		setSize(splashImage.getWidth(null), splashImage.getHeight(null));

		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		addListeners();

		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	protected void cancelLogin() {
		if(loginProcessThread!=null && loginProcessThread.isAlive()){
			loginProcessThread.interrupt();
		}
	}

	public void run() {
		while (true) {
			if (isShowing()) {
				repaint();
			}
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

	private ImageObserver loadingImageObserver = new ImageObserver() {
		@Override
		public boolean imageUpdate(Image img, int infoflags, int x, int y,
				int width, int height) {
			if (panel_login.isVisible())
				return false;
			repaint(label_loading.x, label_loading.y, width, height);
			return true;
		}
	};
	private ImageObserver frameImageObserver = new ImageObserver() {
		@Override
		public boolean imageUpdate(Image img, int infoflags, int x, int y,
				int width, int height) {
			repaint(x, y, width, height);
			return true;
		}
	};
	private Timer fader;

	public MouseMotionListener pointMotion = new MouseMotionAdapter() {
		@Override
		public synchronized void mouseDragged(MouseEvent e) {
			if (startPoint == null)
				return;
			Point mp = MouseInfo.getPointerInfo().getLocation();
			int x = mp.x - startPoint.x;
			int y = mp.y - startPoint.y;
			LoginSplashWindow.this.setLocation(new Point(x, y));
			repaint();
		}
	};

	public MouseListener pointInit = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			startPoint = null;
			if (moveable)
				startPoint = LoginSplashWindow.this.getMousePosition();
		}

		@Override
		public void mouseReleased(MouseEvent e) {
			startPoint = null;
		}
	};
	private JButtonLabel btn_back;

	public void addListeners() {
		UtilBox.getInstance().addMouseListenerToAll(contentPane, pointInit);
		UtilBox.getInstance().addMouseMotionListenerToAll(
				contentPane,
				pointMotion,
				new Class<?>[] { JButton.class, JTextField.class,
						JPasswordField.class, JButtonLabel.class });
	}

	public void splashMode() {
		panel_login.setVisible(false);
	}

	public void loginMode() {
		panel_login.setVisible(true);
		loadLoginData();
		clearText();
		repaint();
	}

	public void setText(String s) {
		this.splashText = s;
		repaint();
	}

	public void clearText() {
		this.splashText = "";
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		g2d.drawImage(splashImage, 0, 0, this.frameImageObserver);
		if (panel_login.isVisible()) {

		} else {
			g2d.drawImage(loadingImage, label_loading.x, label_loading.y,
					loadingImageObserver);
		}
		contentPane.paint(g);
		g2d.setColor(Color.black);
		g2d.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 20));
		g2d.drawString(splashText, 10, 380);
		g2d.dispose();
	}

	private void login() {
		furkAnimate(4);
		String username = input_username.getText();
		char[] password = input_password.getPassword();

		if (username == null || username.equals("")) {
			setText("Error: Username Is Blank");
			furkAnimate(0);
			return;
		}
		if (password == null || password.length == 0) {
			setText("Error: No Password Entered");
			furkAnimate(0);
			return;
		}

		LoginModel userLogin = new LoginModel(username, password);
		userLogin.save(check_savecreds.isSelected());
		userLogin.autoLogin(check_auto.isSelected());

		try {
			setText("User Login In Progress...");
			boolean loggedIn = API_UserData.login(userLogin);
			if (loggedIn) {
				FurkManager.appRun();
			} else {
				throw new Exception("Login Error!");
			}
		} catch (Exception e) {
			furkAnimate(2);
			FurkManager.trayAlert(FurkManager.TRAY_ERROR, "Login Failed / Cancelled",
					e.getMessage(), null);
			setText("Login Failed: " + e.getMessage());
			loggingIn = false;
			furkAnimate(0);
			return;
		}
	}

	public void keyLogin(String apiKey) {
		input_apikey.setText(apiKey);
		button_apilogin.invoke();
	}

	private void apiLogin() {
		if (loggingIn)
			return;
		loggingIn = true;
		furkAnimate(4);
		String apiKey = input_apikey.getText();
		if (apiKey == null || apiKey.equals("")) {
			setText("Error: Blank API Key");
			furkAnimate(0);
			return;
		}

		LoginModel apiLogin = new LoginModel(apiKey);
		apiLogin.save(check_savecreds.isSelected());
		apiLogin.autoLogin(check_auto.isSelected());

		try {
			setText("API Login In Progress...");
			boolean loggedIn = API_UserData.login(apiLogin);
			if (loggedIn) {
				FurkManager.appRun();
			} else {
				throw new Exception("Error. Invalid API Key.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FurkManager.trayAlert(FurkManager.TRAY_ERROR, "Login Failed",
					e.getMessage(), null);
			setText("Login Failed: " + e.getMessage());
			API.clear();
			loggingIn = false;
			furkAnimate(0);
			return;
		}
	}

	public void loadLoginData() {
		LoginModel savedLogin = SettingsManager.getInstance().loginModel();
		if (savedLogin == null)
			savedLogin = new LoginModel(LoginModel.BLANK);

		input_username.setText(savedLogin.username());
		input_password.setText(UtilBox.getInstance().charToString(
				savedLogin.password()));
		input_apikey.setText(savedLogin.apiKey());

		check_savecreds.setSelected(savedLogin.save());
		check_auto.setSelected(savedLogin.autoLogin());
		loggingIn = false;
	}

	private void furkAnimate(int speed) {
		panel_login.setVisible(speed == 0);
		btn_back.setVisible(speed!=0);
		repaint();
	}

	@Override
	public void setVisible(final boolean b) {
		if (!isVisible() && !b)
			return;
		setOpacity(b ? 0 : 1);
		super.setVisible(true);
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
						LoginSplashWindow.super.setVisible(false);
					((Timer) e.getSource()).stop();
				} else {
					setOpacity(newOpac);
				}
			}
		});
		fader.start();
	}

	public void whiteBackground() {
		splashImage = ImageLoader.getInstance().getImage("Splash_Big_White_Trans.png");
	}
	
	public void regularBackground(){
		splashImage = ImageLoader.getInstance().getImage("Splash_Big.png");
	}
}
