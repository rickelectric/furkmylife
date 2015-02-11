package rickelectric.swingmods;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JScrollPane;
import javax.swing.Timer;

public class JFadeScrollPane extends JScrollPane implements Opacible, Slideable {
	private static final long serialVersionUID = 1L;

	private float alpha = 1;

	public JFadeScrollPane() {
		super();
		init();
	}

	protected void init() {
		alpha = 1;
		setOpaque(false);
		setDoubleBuffered(true);
	}

	private boolean oscillation;
	private int oscSpeed;

	@Override
	public boolean isOscillating() {
		return oscillation;
	}

	@Override
	public void setOscillating(boolean oscillation) {
		this.oscillation = oscillation;
	}

	@Override
	public int getOscSpeed() {
		return oscSpeed;
	}

	@Override
	public void setOscSpeed(int oscSpeed) {
		this.oscSpeed = oscSpeed;
	}

	public void stopOscillation() {
		oscillation = false;
	}

	@Override
	public void setAlpha(float value) {
		if (value > 1 || value < 0)
			return;
		if (getAlpha() < 0.02) {
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

	@Override
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

	public boolean isSliding = false;

	@Override
	public boolean isSliding() {
		return t.isRunning();//isSliding;
	}

	@Override
	public void setSliding(boolean b) {
		isSliding = b;
	}

	private Thread slidingThread = null;

	@Override
	public void setSlidingThread(Thread t) {
		while (t.isAlive())
			;
		t.interrupt();
		slidingThread = t;
	}

	@Override
	public Thread getSlidingThread() {
		return slidingThread;
	}

	private Timer t;

	@Override
	public void setSlidingTimer(Timer t) {
		this.t = t;
	}

}
