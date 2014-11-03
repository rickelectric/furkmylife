package rickelectric.furkmanager.views;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JComponent;

public class CircleButton extends JComponent {
	private static final long serialVersionUID = 1L;

	private String popup;
	private BufferedImage icon;

	public CircleButton(String popup, URL icon) {
		this.setPopup(popup);
		setLocation(0,0);
		try {
			this.icon = ImageIO.read(icon);
			int wh = 5 + (int) Math.ceil(Math.sqrt(Math.pow(
					this.icon.getWidth(), 2)
					+ Math.pow(this.icon.getHeight(), 2)));
			setSize(wh,wh);
		} catch (IOException e) {
		}
	}

	public String getPopup() {
		return popup;
	}

	public void setPopup(String popup) {
		this.popup = popup;
	}

	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D)g;
		Color old = g2d.getColor();
		Stroke s = g2d.getStroke();
		
		g2d.setColor(Color.darkGray);
		g2d.fill(new Ellipse2D.Double(getX(), getY(), getWidth(), getHeight()));
		
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(8));
		g2d.draw(new Ellipse2D.Double(getX()+1, getY()+1, getWidth()-2, getHeight()-2));
		
		g2d.drawImage(icon, (int) (getX() + (getWidth() / 2 - icon.getWidth() / 2)),
				(int) (getY() + (getHeight() / 2 - icon.getHeight() / 2)), null);
		g2d.setColor(old);
		g2d.setStroke(s);
	}

}
