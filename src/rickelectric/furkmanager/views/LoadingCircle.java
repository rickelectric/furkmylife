package rickelectric.furkmanager.views;

import java.awt.Font;
import java.awt.Graphics;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import rickelectric.img.ImageLoader;

public class LoadingCircle extends JLabel {
	private static final long serialVersionUID = 1L;

	private String str = "";

	public LoadingCircle() {
		setFont(new Font("Dialog", Font.BOLD, 12));
		setIcon(new ImageIcon(ImageLoader.class.getResource("ajax-loader-128.gif")));
		setHorizontalAlignment(SwingConstants.CENTER);
		setVerticalAlignment(SwingConstants.CENTER);
	}

	public void setString(String s) {
		this.str = s;
		repaint();
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		int w = g.getFontMetrics().stringWidth(str);
		w = (getWidth() / 2) - (w / 2);
		g.drawString(str, (this.getHeight()/2) - 6, this.getWidth() + w);
	}
}
