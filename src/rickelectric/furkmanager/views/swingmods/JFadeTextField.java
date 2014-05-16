package rickelectric.furkmanager.views.swingmods;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JTextField;
import javax.swing.Timer;

import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.utils.UtilBox;

/**
 * 
 * @author Rick Lewis
 *         <p>
 *         Fadable Text Field. Easily Accessible Sliding and Fading Functions.
 *         </p>
 * 
 */
public class JFadeTextField extends JTextField implements Opacible, Slideable {
	private static final long serialVersionUID = 1L;

	private float alpha;

	public JFadeTextField() {
		super();
		init();
	}

	public JFadeTextField(String text) {
		super(text);
		init();
	}

	public JFadeTextField(int columns) {
		super(columns);
		init();
	}

	public JFadeTextField(String text, int columns) {
		super(text, columns);
		init();
	}

	protected void init() {
		alpha = 1;
		setOpaque(false);
		setDoubleBuffered(true);
	}

	private boolean oscillation;
	private int oscSpeed;

	public boolean isOscillating() {
		return oscillation;
	}

	public void setOscillating(boolean oscillation) {
		this.oscillation = oscillation;
	}

	public int getOscSpeed() {
		return oscSpeed;
	}

	public void setOscSpeed(int oscSpeed) {
		this.oscSpeed = oscSpeed;
	}

	public void stopOscillation() {
		oscillation = false;
	}

	public static final int UP = -10, DOWN = 10, LEFT = -20, RIGHT = 20,
			NONE = 0, IN = 1, OUT = 2;

	private boolean isSliding;

	public boolean isSliding() {
		return t.isRunning();
		//return isSliding;
	}

	public void setSliding(boolean b) {
		isSliding = true;
	}

	/**
	 * 
	 * @param distance
	 *            Distance (in pixels) of the object's slide.
	 * @param direction
	 *            Direction
	 *            (JFadeTextField.UP,JFadeTextField.DOWN,JFadeTextField.LEFT or
	 *            JFadeTextField.RIGHT.)
	 * @param speed
	 *            Speed of the slide (in pixels per second.)
	 * @param fadeMode
	 */
	public void slide(final int distance, final int direction, int speed,
			final int fadeMode) {
		if (isSliding)
			return;
		if (distance == 0)
			return;
		if (direction != UP && direction != DOWN && direction != LEFT
				&& direction != RIGHT)
			throw new IllegalArgumentException("Invalid Direction Parameter.");
		if (fadeMode != IN && fadeMode != OUT && fadeMode != NONE)
			throw new IllegalArgumentException("Invalid FadeMode");
		int aResolve = 0;
		if (fadeMode == IN) {
			if (isVisible())
				throw new IllegalArgumentException(
						"Cannot fade in a visible object.");
			aResolve = 1;
			setAlpha(0);
		}
		if (fadeMode == OUT) {
			if (!isVisible())
				throw new IllegalArgumentException(
						"Cannot fade out an invisible object.");
			aResolve = -1;
			setAlpha(1);
		}
		final int time = 1000 / speed;
		final float alphaInc = (float) aResolve / distance;

		ThreadPool.run(new Runnable() {
			public void run() {
				isSliding = true;
				for (int i = 0; i < distance; i++) {
					if (direction == UP || direction == DOWN) {
						setLocation(getX(), getY() + (int) (direction / 10));
					} else {
						setLocation(getX() + (int) (direction / 20), getY());
					}
					if (fadeMode == IN || fadeMode == OUT) {
						setAlpha(getAlpha() + alphaInc);
					}
					repaint();
					UtilBox.pause(time);
				}
				isSliding = false;
			}
		});
	}

	public void setAlpha(float value) {
		if (value > 1 || value < 0)
			return;
		if (alpha > 0.9f)
			alpha = 1;
		if (getAlpha() < 0.05) {
			setVisible(false);
		} else
			setVisible(true);
		if (alpha != value) {
			float old = alpha;
			alpha = value;
			firePropertyChange("alpha", old, alpha);
			repaint();
		}
	}

	public float getAlpha() {
		return alpha;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setComposite(AlphaComposite.SrcOver.derive(alpha));
		super.paint(g2);
		g2.dispose();
	}

	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setColor(getBackground());
		g2.fillRect(0, 0, getWidth(), getHeight());
		super.paintComponent(g2);
		g2.dispose();
	}

	private Thread slidingThread = null;

	public void setSlidingThread(Thread t) {
		slidingThread = t;
	}

	public Thread getSlidingThread() {
		return slidingThread;
	}

	Timer t;

	public void setSlidingTimer(Timer t) {
		// TODO Auto-generated method stub
		this.t = t;
	}
}