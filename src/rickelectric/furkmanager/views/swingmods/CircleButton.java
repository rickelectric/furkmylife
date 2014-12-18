package rickelectric.furkmanager.views.swingmods;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;

import javax.swing.JComponent;

import net.java.balloontip.BalloonTip;

public class CircleButton extends JComponent {
	private static final long serialVersionUID = 1L;

	private String popup;
	private Image icon;

	private float strokeSize;

	private Ellipse2D.Double btnBackground;

	private Ellipse2D.Double rim;

	public CircleButton(String popup, Image icon) {
		if (icon.getHeight(null) > 64 || icon.getWidth(null) > 64)
			throw new IllegalArgumentException(
					"Icon Dimensions Must Not Exceed 64px");
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

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		Color old = g2d.getColor();
		Stroke s = g2d.getStroke();

		g2d.setColor(Color.darkGray);
		btnBackground.setFrame(getX(), getY(), getWidth(), getHeight());
		g2d.fill(btnBackground);

		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(strokeSize));
		rim.setFrame(getX() + 1, getY() + 1, getWidth() - 2, getHeight() - 2);
		g2d.draw(rim);

		g2d.drawImage(icon,
				getX() + (getWidth() / 2 - icon.getWidth(null) / 2), getY()
						+ (getHeight() / 2 - icon.getHeight(null) / 2), null);
		g2d.setColor(old);
		g2d.setStroke(s);
	}

}
