package rickelectric.furkmanager.beta_test.draggables;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import rickelectric.furkmanager.beta_test.draggables.models.Item;

public class ModelPainter {

	private static final Font defFont = new Font(Font.DIALOG, Font.BOLD, 13);
	private static final Font hiFont = new Font(Font.DIALOG, Font.BOLD
			| Font.ITALIC, 14);

	private static void paint(Graphics2D g2d, Item i) {
		Color c=g2d.getColor();
		g2d.setColor(Math.random()<0.5?new Color(0,0,255,127):Math.random()<0.5?new Color(0,255,0,127):new Color(255,0,0,127));
		if(i.isSelected()){
			g2d.fillOval(i.x-15, i.y-15, i.width+30, i.height+30);
		}
		g2d.setColor(c);
		g2d.drawImage(i.getIcon(), i.x, i.y, null);

		int w = g2d.getFontMetrics().stringWidth(i.getName());
		w = (i.width / 2) - (w / 2);

		g2d.drawString(i.getName(), i.x + w, i.y + i.height + 13);

		g2d.setColor(i.online());
		if (i.online().equals(Color.red)) {
			g2d.fillRect(i.x + i.width + 2, i.y - 2, 12, 12);
		} else {
			g2d.fillOval(i.x + i.width + 2, i.y - 2, 12, 12);
		}
	}

	public static void paint(Graphics g, ArrayList<Item> allItems) {
		Graphics2D g2d = (Graphics2D) g.create();
		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g2d.setFont(defFont);
		g2d.setComposite(AlphaComposite.SrcOver.derive(0.8f));
		for (Item i : allItems) {
			if(i.isSelected()) g2d.setFont(hiFont);
			else g2d.setFont(defFont);
			g2d.setColor(Color.black);
			paint(g2d, i);
		}
		g2d.dispose();
	}

}
