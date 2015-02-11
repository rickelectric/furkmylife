package rickelectric.swingmods;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JComponent;

import net.java.balloontip.BalloonTip;

public class CircleButton extends JComponent implements
		Comparable<CircleButton>, Observer {
	private static final long serialVersionUID = 1L;

	private String popup;
	private Image icon;

	private float strokeSize;
	private double scale;

	private Ellipse2D.Double btnBackground;

	private Ellipse2D.Double rim;

	public CircleButton(String popup, Image icon) {
		if (icon.getHeight(null) > 64 || icon.getWidth(null) > 64)
			throw new IllegalArgumentException(
					"Icon Dimensions Must Not Exceed 64px");
		observable = new ButtonObservable();
		tethered = false;
		this.setPopup(popup);
		setLocation(0, 0);
		this.icon = icon;
		strokeSize = 5;
		int wh = (int) (strokeSize)
				+ (int) Math.ceil(Math.sqrt(Math.pow(this.icon.getWidth(null),
						2) + Math.pow(this.icon.getHeight(null), 2)));
		setSize(wh, wh);
		btnBackground = new Ellipse2D.Double(getX(), getY(), getWidth(),
				getHeight());
		rim = new Ellipse2D.Double(getX() + 1, getY() + 1, getWidth() - 2,
				getHeight() - 2);
		setBackground(Color.darkGray);
		setForeground(Color.black);

		

		scale = 1.0;
	}

	private ButtonObservable observable;

	private boolean tethered;

	public void tetherButton(CircleButton b) {
		Point offset = new Point(b.getX() - getX(), b.getY() - getY());
		b.setTethered(true);
		observable.addObserver(b, offset);
	}

	private void setTethered(boolean b) {
		this.tethered = b;
	}
	
	public boolean isTethered(){
		return tethered;
	}

	class ButtonObservable extends Observable {

		private ArrayList<CircleButton> attached;
		private HashMap<CircleButton, Point> offsets;

		public ButtonObservable() {
			offsets = new HashMap<CircleButton, Point>();
			attached = new ArrayList<CircleButton>();
		}
		
		public ArrayList<CircleButton> getAttached(){
			return attached;
		}

		public void addObserver(CircleButton b, Point offset) {
			super.addObserver(b);
			attached.add(b);
			offsets.put(b, offset);
		}

		public void stateChanged() {
			setChanged();
			notifyObservers(offsets);
		}

		public CircleButton getMaster() {
			return CircleButton.this;
		}
	}

	public double getDistanceFromOrigin() {
		double dist = Math.sqrt((getX() * getX()) + (getY() * getY()));
		return dist;
	}

	public int compareTo(CircleButton b) {
		return (int) (getDistanceFromOrigin() - b.getDistanceFromOrigin());
	}

	public void setStrokeSize(int strokeSize) {
		this.strokeSize = strokeSize;
	}

	public BalloonTip getAttachedBalloon() {
		for (Component c : getComponents()) {
			if (c instanceof BalloonTip) {
				return (BalloonTip) c;
			}
		}
		return null;
	}

	public String getPopup() {
		return popup;
	}

	public void setPopup(String popup) {
		this.popup = popup;
	}

	public void setScale(double scale) {
		this.scale = scale;
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2d = (Graphics2D) g;
		Color old = g2d.getColor();
		Stroke s = g2d.getStroke();
		g2d.scale(scale, scale);

		super.paint(g2d);
		g2d.setColor(getBackground());
		btnBackground.setFrame(getX(), getY(), getWidth(), getHeight());
		g2d.fill(btnBackground);

		g2d.setColor(getForeground());
		g2d.setStroke(new BasicStroke(strokeSize));
		rim.setFrame(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2);
		g2d.draw(rim);

		g2d.drawImage(icon,
				getX() + (getWidth() / 2 - icon.getWidth(null) / 2), getY()
						+ (getHeight() / 2 - icon.getHeight(null) / 2), null);
		g2d.setColor(old);
		g2d.setStroke(s);
		
		for(CircleButton b:observable.getAttached()){
			b.paint(g2d);
		}

		// g2d.scale(1 / scale, 1 / scale);
	}

	@Override
	public void setLocation(int x, int y) {
		super.setLocation(x, y);
		observable.stateChanged();
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Observable arg0, Object arg1) {
		HashMap<CircleButton, Point> offsets = (HashMap<CircleButton, Point>) arg1;
		Point offset = offsets.get(this);
		CircleButton master = ((ButtonObservable) arg0).getMaster();
		this.setLocation(master.getX() + offset.x, master.getY() + offset.y);
	}

}
