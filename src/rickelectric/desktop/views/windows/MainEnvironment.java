package rickelectric.desktop.views.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.java.balloontip.BalloonTip;
import net.java.balloontip.BalloonTip.AttachLocation;
import net.java.balloontip.BalloonTip.Orientation;
import net.java.balloontip.styles.BalloonTipStyle;
import net.java.balloontip.styles.RoundedBalloonStyle;
import rickelectric.UtilBox;
import rickelectric.furkmanager.utils.MouseActivity;
import rickelectric.img.ImageLoader;
import rickelectric.swingmods.CircleButton;

public class MainEnvironment extends JDialog implements Runnable,
		MouseListener, MouseMotionListener {
	private static final long serialVersionUID = 1L;

	private static MainEnvironment thisInstance = null;

	public static synchronized MainEnvironment getInstance() {
		if (thisInstance == null) {
			thisInstance = new MainEnvironment();
		}
		return thisInstance;
	}

	public static synchronized void destroyInstance() {
		if (thisInstance == null)
			return;
		thisInstance = null;
		System.gc();
	}

	private ArrayList<String> keys;
	private HashMap<String, CircleButton> buttonMap;
	private HashMap<String, PopupBalloonTip> balloonMap;
	private HashMap<String, JPopupMenu> menuMap;
	private HashMap<String, Runnable> runMap;

	public void addItemPair(String key, CircleButton button,
			JPanel balloonContents) {
		if (key == null || button == null)
			return;
		addButton(key, button);
		addBalloon(key, balloonContents);
		contentPane.repaint();
	}

	public void addRunner(String key, Runnable runner) {
		if (key == null)
			return;
		runMap.put(key, runner);
	}

	public void attachMenu(String key, JPopupMenu menu) {
		if (key == null)
			return;
		menuMap.put(key, menu);
	}

	public void removeItem(String key) {
		if (keys.contains(key)) {
			balloonMap.remove(key).closeBalloon();
			menuMap.remove(key);
			runMap.remove(key);
			contentPane.remove(buttonMap.remove(key));
			keys.remove(key);
			repaint();
		}
	}

	private void addButton(String key, CircleButton button) {
		if (keys.contains(key))
			throw new RuntimeException("Button With This Key Already Exists.");
		buttonMap.put(key, button);
		contentPane.add(button);
		keys.add(key);
	}

	private void addBalloon(String key, JPanel balloonContents) {
		CircleButton attachTo = buttonMap.get(key);
		if (attachTo == null)
			throw new RuntimeException(
					"Could Not Find A Button With The Provided Key. "
							+ "Please Attach A Button With This Key First.");

		if (balloonContents != null) {
			PopupBalloonTip balloon = constructBalloon(balloonContents,
					attachTo);
			balloonMap.put(key, balloon);
		}
	}

	private void reAttachBalloon(String key) {
		CircleButton cb = buttonMap.get(key);
		PopupBalloonTip b = balloonMap.get(key);
		if (cb == null || b == null)
			return;
		JPanel cPanel = (JPanel) b.getContents();
		boolean ib = b.isVisible();
		b.superVisible(false);
		b.closeBalloon();
		balloonMap.remove(key);
		b = constructBalloon(cPanel, cb);
		b.superVisible(ib);
		balloonMap.put(key, b);
	}

	private class PopupBalloonTip extends BalloonTip {
		private static final long serialVersionUID = 1L;

		private double scaler = 0.05;
		private boolean animating = false;

		public PopupBalloonTip(CircleButton src, JPanel contents,
				BalloonTipStyle style, Orientation orientation,
				AttachLocation attachLocation, int horizontalOffset,
				int verticalOffset, boolean useCloseButton) {
			super(src, contents, style, orientation, attachLocation,
					horizontalOffset, verticalOffset, useCloseButton);
		}

		@Override
		public void setVisible(boolean b) {
			if (b) {
				appear();
				super.setVisible(true);
			} else {
				disappear();
			}
			// super.setVisible(b);
		}

		@Override
		public void setLocation(int x, int y) {
			if (getY() > getAttachedComponent().getY()) {
				super.setLocation(getX(), getY()
						- getAttachedComponent().getHeight() / 2);
			} else {
				super.setLocation(getX(), getY()
						+ getAttachedComponent().getHeight() / 2);
			}
		}

		@Override
		public void paint(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.scale(scaler, scaler);
			try {
				super.paint(g2);
			} catch (Exception e) {
			}
			g2.dispose();
		}

		private void disappear() {
			if (scaler <= 0.05) {
				superVisible(false);
				return;
			}
			if (animating)
				return;
			animating = true;
			new Thread(new Runnable() {
				public void run() {
					scaler = 1.0;
					double dec = 0.005;
					while (scaler > 0.05) {
						scaler -= dec;
						dec += 0.003;
						repaint();
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
						}
					}
					superVisible(false);
					animating = false;
					requestDimEnvironment(balloonIsShowing());
				}

			}).start();
		}

		public void superVisible(boolean b) {
			scaler = b ? 1 : 0.05;
			super.setVisible(b);
		}

		public void appear() {
			if (scaler >= 1.0)
				return;
			if (animating)
				return;
			animating = true;
			new Thread(new Runnable() {
				public void run() {
					scaler = 0.05;
					double inc = 0.005;
					while (scaler < 1.0) {
						scaler += inc;
						inc += 0.003;
						if (scaler > 1.0)
							scaler = 1.0;
						repaint();
						try {
							Thread.sleep(20);
						} catch (InterruptedException e) {
						}
					}
					animating = false;
					requestDimEnvironment(balloonIsShowing());
				}
			}).start();
		}
	}

	private PopupBalloonTip constructBalloon(JPanel contents, CircleButton src) {
		/**
		 * To Adjust Depending On Button X And Y
		 */
		Orientation orientation;
		AttachLocation attachLocation;
		int median = getWidth() / 2;
		if (src.getY() + src.getHeight() < (getHeight() - (20 + contents
				.getPreferredSize().height))) {
			orientation = src.getX() < median ? Orientation.LEFT_BELOW
					: Orientation.RIGHT_BELOW;
			attachLocation = AttachLocation.SOUTH;
		} else {
			orientation = src.getX() < median ? Orientation.LEFT_ABOVE
					: Orientation.RIGHT_ABOVE;
			attachLocation = AttachLocation.NORTH;
		}

		int horizontalOffset = 40;
		int verticalOffset = 30;
		boolean useCloseButton = false;

		BalloonTipStyle style = new RoundedBalloonStyle(10, 10,
				contents.getBackground(), Color.blue);

		PopupBalloonTip bt = new PopupBalloonTip(src, contents, style,
				orientation, attachLocation, horizontalOffset, verticalOffset,
				useCloseButton);
		bt.superVisible(false);

		return bt;
	}

	private JPanel contentPane;
	private Color bgc;

	private boolean showButtons;

	private int bHeight, currHeight;

	public class EnvPanel extends JPanel {
		private static final long serialVersionUID = 1L;

		public EnvPanel() {
			setLayout(null);
			setBackground(new Color(255, 255, 255, 0));
		}

		@Override
		public void paint(Graphics g) {
			// super.paint(g);
			g.setColor(Color.lightGray);

			// Paint Primary Buttons & Balloons On Top

			for (int i = keys.size() - 1; i >= 0; i--) {
				String key = keys.get(i);
				CircleButton btn = buttonMap.get(key);
				if (btn.isTethered())
					continue;
				if (btn != null && btn.isVisible()) {
					Graphics2D g1 = (Graphics2D) g;
					g1.scale(scaler, scaler);
					// super.paint(g);
					btn.paint(g1);
					// g1.scale(1 / scaler, 1 / scaler);
					BalloonTip b = balloonMap.get(key);
					if (b != null && b.isVisible()) {
						int w1;

						g.setFont(new Font(Font.SERIF, Font.BOLD | Font.ITALIC,
								42));
						w1 = g.getFontMetrics().stringWidth(btn.getPopup());

						g.setColor(Color.blue);
						g.fillRoundRect(b.getX() + (b.getWidth() / 2 - w1 / 2)
								- 5, b.getY() - 44, w1 + 10, 46, 5, 5);

						g.setColor(Color.white);
						g.drawString(btn.getPopup(), b.getX()
								+ (b.getWidth() / 2 - w1 / 2), b.getY() - 10);
						g.setColor(Color.black);
						g.drawString(btn.getPopup(), b.getX()
								+ (b.getWidth() / 2 - w1 / 2) - 2,
								b.getY() - 12);
					}
				}
			}
		}
	}

	int darkness = 40;

	private long lastInTime;

	private boolean animating;

	protected double scaler;

	public void setTitle(String title) {
		if (title == null) {
			super.setTitle("R-Desktop");
			return;
		}
		super.setTitle(title + " - R-Desktop");
	}

	private MainEnvironment() {
		super();
		animating = false;
		scaler = 1.0;
		setUndecorated(true);

		setModalExclusionType(ModalExclusionType.APPLICATION_EXCLUDE);

		setTitle(null);
		setIconImage(new ImageIcon(ImageLoader.getInstance().getImage("fr.png"))
				.getImage());

		bgc = new Color(Color.darkGray.getRed(), Color.darkGray.getBlue(),
				Color.darkGray.getGreen(), 0);
		setBackground(bgc);

		keys = new ArrayList<String>();
		buttonMap = new HashMap<String, CircleButton>();
		balloonMap = new HashMap<String, PopupBalloonTip>();
		menuMap = new HashMap<String, JPopupMenu>();
		runMap = new HashMap<String, Runnable>();

		contentPane = new EnvPanel();
		setContentPane(contentPane);

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);

		showButtons = true;

		setSize(Toolkit.getDefaultToolkit().getScreenSize());
		setLocationRelativeTo(null);

		bHeight = getHeight()
				- (UtilBox.getInstance().getTaskbarOrientation() == UtilBox.BOTTOM ? UtilBox
						.getInstance().getTaskbarHeight() : 0) - 100;
		currHeight = bHeight;

		generate();

		addMouseListener(this);
		addMouseMotionListener(this);
		setAlwaysOnTop(true);
		MouseActivity.getInstance();
		new Thread(this).start();
	}

	public void dispose() {
		MouseActivity.destroyInstance();
		super.dispose();
	}

	private void generate() {
		CircleButton btn;

		btn = new CircleButton("Exit", ImageLoader.getInstance().getImage(
				"remove.png"));
		btn.setLocation(getWidth() - btn.getWidth() - 10, 60);
		addButton(null, btn);
		runMap.put(null, new Runnable() {
			public void run() {
				setVisible(false);
			}
		});
	}

	public void refreshBgc() {
		requestDimEnvironment(true);
		MainEnvironment.this.repaint();
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
		if (b) {
			MouseActivity.getInstance();
			appear();
			super.setVisible(true);
		} else {
			MouseActivity.destroyInstance();
			disappear();
		}
		// super.setVisible(b);
	}

	@Override
	public void run() {
		long sleepTime = 40;
		lastInTime = System.currentTimeMillis();
		long timeout = 3000;
		while (isVisible()) {
			if (true)
				try {
					if (MainEnvironment.this.isVisible()) {
						sleepTime = 100;
						if (MouseActivity.getInstance().getMouseY() < (UtilBox
								.getInstance().getTaskbarOrientation() != UtilBox.NONEXISTANT ? getHeight()
								- UtilBox.getInstance().getTaskbarHeight()
								: 20)
								&& !balloonIsShowing())
							showButtons = (System.currentTimeMillis()
									- lastInTime < timeout) ? true : false;
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
								sleepTime = 40;
								repaint();
							}
						} else if (System.currentTimeMillis() - lastInTime >= timeout) {
							if (currHeight < MainEnvironment.this.getHeight()) {
								currHeight += 8;
								if (currHeight > MainEnvironment.this
										.getHeight() + 4) {
									currHeight = MainEnvironment.this
											.getHeight() + 4;
								}
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
		boolean showing = false;
		for (String key : keys) {
			BalloonTip bt = balloonMap.get(key);
			if (bt != null && bt.isShowing()) {
				buttonMap.get(key).setForeground(Color.white);
				buttonMap.get(key).setBackground(Color.green);
				setTitle(buttonMap.get(key).getPopup());
				if (showing == false)
					showing = true;
			}
		}
		return showing;
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (dragged) {
			reAttachBalloon(selectedKey);
			// RDesktop.getExtensionSettings().setLocation(selectedKey,
			// selected.getX(), selected.getY());
			selectedKey = null;
			selected = null;
			offset = null;
			dragged = false;
			requestDimEnvironment(balloonIsShowing());
			return;
		}
		selectedKey = null;
		selected = null;
		offset = null;
		dragged = false;
		if (e.getButton() == MouseEvent.BUTTON1) {
			/**
			 * Check Regular Buttons & Balloons
			 */
			for (String key : keys) {
				CircleButton btn = buttonMap.get(key);
				BalloonTip b = balloonMap.get(key);
				Runnable r = runMap.get(key);
				if (contains(e, btn)) {
					if (b != null) {
						b.setVisible(!b.isVisible());
					}
					if (r != null) {
						new Thread(r).start();
					}
				} else {
					if (b != null) {
						b.setVisible(false);
					}
				}

			}

			requestDimEnvironment(balloonIsShowing());
		} else if (e.getButton() == MouseEvent.BUTTON3) {
			String key = getBtnKeyAt(e.getPoint());
			if (key == null || key.equals("---"))
				return;
			JPopupMenu menu = menuMap.get(key);
			if (menu != null) {
				menu.show(this, e.getX(), e.getY());
				repaint();
			}
		}
	}

	private String getBtnKeyAt(Point p) {
		for (String key : keys) {
			CircleButton btn = buttonMap.get(key);
			if (contains(p, btn))
				return key == null ? "---" : key;
		}
		return null;
	}

	private void disappear() {
		if (animating)
			return;
		animating = true;
		new Thread(new Runnable() {
			public void run() {
				scaler = 1.0;
				double dec = (0.01/buttonMap.size());
				while (scaler > 0.05) {
					scaler -= dec;
					dec += 0.001;
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							repaint();
						}
					});
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}
				}
				System.out.println("Final Dec = "+dec);
				MainEnvironment.super.setVisible(false);
				animating = false;
			}
		}).start();
	}

	public void appear() {
		if (scaler >= 1.0)
			return;
		if (animating)
			return;
		animating = true;
		new Thread(new Runnable() {
			public void run() {
				scaler = 0.05;
				double inc = 0.05;
				int incDir=-1;
				while (scaler < 1.0) {
					scaler += inc;
					inc += (incDir)*0.002;
					if(inc<0.002){
						inc=0.002;
						incDir=-incDir;
					}
					if(inc>0.05){
						inc=0.05;
						incDir=-incDir;
					}
					if (scaler > 1.0)
						scaler = 1.0;
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							repaint();
						}
					});
					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
					}
				}
				animating = false;
			}
		}).start();
	}

	private void requestDimEnvironment(boolean dark) {
		bgc = new Color(Color.darkGray.getRed(), Color.darkGray.getBlue(),
				Color.darkGray.getGreen(), dark ? darkness : 0);
		setBackground(bgc);
		MainEnvironment.this.repaint();
	}

	private boolean contains(MouseEvent e, CircleButton b) {
		return contains(e.getPoint(), b);
	}

	private boolean contains(Point e, CircleButton button) {
		return (e.getX() > button.getX()
				&& e.getX() < button.getX() + button.getWidth()
				&& e.getY() > button.getY() && e.getY() < button.getY()
				+ button.getHeight());
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	CircleButton selected = null;
	String selectedKey = null;
	Point offset = null;
	boolean dragged = false;

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON1) {
			String key = getBtnKeyAt(e.getPoint());
			if (key == null)
				return;
			if (key.equals("---"))
				key = null;
			keys.remove(key);
			keys.add(0, key);
			selected = buttonMap.get(key);
			if (selected.isTethered()) {
				selected = null;
				return;
			}
			sortKeys();
			selectedKey = key;
			offset = new Point(e.getX() - selected.getX(), e.getY()
					- selected.getY());
			// requestDimEnvironment(true);
		}
	}

	class KVP implements Comparable<KVP> {
		int i;
		double val;

		public KVP(int i, double val) {
			this.i = i;
			this.val = val;
		}

		@Override
		public int compareTo(KVP o) {
			return -(int) (Math.floor(this.val) - Math.ceil(o.val));
		}
	};

	private void sortKeys() {
		ArrayList<String> tmp = new ArrayList<String>();
		ArrayList<KVP> tv = new ArrayList<KVP>();
		for (Iterator<String> i = keys.iterator(); i.hasNext();) {
			String s = i.next();
			tmp.add(s);
			i.remove();
		}
		for (int i = 0; i < tmp.size(); i++) {
			String s = tmp.get(i);
			double val = buttonMap.get(s).getDistanceFromOrigin();
			tv.add(new KVP(i, val));
		}
		Collections.sort(tv);
		for (KVP t : tv) {
			keys.add(tmp.get(t.i));
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		lastInTime = System.currentTimeMillis();
	}

	@Override
	public void mouseExited(MouseEvent e) {
		lastInTime = System.currentTimeMillis();
		for (String key : keys) {
			CircleButton btn = buttonMap.get(key);
			btn.setBackground(Color.darkGray);
			btn.setForeground(Color.black);
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (selected != null) {
			dragged = true;
			selected.setLocation(e.getLocationOnScreen().x - offset.x,
					e.getLocationOnScreen().y - offset.y);
			if (balloonMap.get(selectedKey) != null
					&& balloonMap.get(selectedKey).isVisible())
				reAttachBalloon(selectedKey);
			repaint();
		}
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		for (String key : keys) {
			CircleButton btn = buttonMap.get(key);
			if (contains(e, btn)) {
				btn.setBackground(Color.orange);
				btn.setForeground(Color.red);
			} else {
				btn.setBackground(Color.darkGray);
				btn.setForeground(Color.black);
			}
		}
		balloonIsShowing();
	}

	public HashMap<String, JPopupMenu> menus() {
		return menuMap;
	}

	public CircleButton getButton(String key) {
		return buttonMap.get(key);
	}
	
	public JComponent getBalloonContents(String key){
		PopupBalloonTip b = balloonMap.get(key);
		return b==null?null:b.getContents();
	}

	public void invokeButton(String key) {
		if(key==null) return;
		PopupBalloonTip b = balloonMap.get(key);
		Runnable run = runMap.get(key);
		if(b!=null)
			b.setVisible(true);
		if(run!=null){
			Thread t = new Thread(run);
			t.setDaemon(true);
			t.start();
		}
	}
}
