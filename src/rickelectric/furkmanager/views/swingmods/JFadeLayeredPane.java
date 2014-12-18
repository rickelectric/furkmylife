package rickelectric.furkmanager.views.swingmods;

import java.awt.AlphaComposite;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JLayeredPane;
import javax.swing.Timer;

import rickelectric.UtilBox;
import rickelectric.furkmanager.utils.ThreadPool;

public class JFadeLayeredPane extends JLayeredPane implements Opacible{
	private static final long serialVersionUID = 1L;
	private float alpha;
	
	public JFadeLayeredPane() {
		alpha = 0;
		setOpaque(false);
		setDoubleBuffered(true);
	}

	@Override
	public void setAlpha(float value) {
		if (value > 1 || value < 0)
			return;
		if (value < 0.05f)
			setVisible(false);
		else
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
		// Paint overridden to make sure that the entire paint chain is now
		// using the new alpha composite, including borders and all child components
		Graphics2D g2 = (Graphics2D) g.create();
		g2.setComposite(AlphaComposite.SrcOver.derive(getAlpha()));
		super.paint(g2);
		g2.dispose();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setComposite(AlphaComposite.SrcOver.derive(getAlpha()));
		g2d.setColor(getBackground());
		g2d.fillRect(0, 0, getWidth(), getHeight());
	}

	private boolean oscillation;
	public int oscSpeed;

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

	public void fadeIn(final int msec) {
		ThreadPool.run(new Runnable() {
			@Override
			public void run() {
				while (getAlpha() < 1f) {
					setAlpha(alpha + 0.02f);
					UtilBox.getInstance().wait(1000 / msec);
				}
				setAlpha(1f);
			}
		});
	}

	public void fadeOut(final int msec) {
		ThreadPool.run(new Runnable() {
			@Override
			public void run() {
				while (getAlpha() > 0f) {
					setAlpha(alpha - 0.02f);
					UtilBox.getInstance().wait(1000 / msec);
				}
				setAlpha(0f);
			}
		});
	}

	public boolean isSliding = false;

	public boolean isSliding() {
		return t==null?false:t.isRunning();//isSliding;
	}

	public void setSliding(boolean b) {
		isSliding = b;
	}

	private Thread slidingThread = null;

	public void setSlidingThread(Thread t) {
		while (t.isAlive())
			;
		t.interrupt();
		slidingThread = t;
	}

	public Thread getSlidingThread() {
		return slidingThread;
	}

	Timer t;

	public void setSlidingTimer(Timer t) {
		this.t = t;

	}

}
