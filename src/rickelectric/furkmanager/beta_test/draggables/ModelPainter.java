package rickelectric.furkmanager.beta_test.draggables;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;

import rickelectric.furkmanager.beta_test.draggables.models.FolderItem;
import rickelectric.furkmanager.beta_test.draggables.models.Item;

public class ModelPainter {

	private static final Font defFont = new Font(Font.DIALOG, Font.BOLD, 13);
	private static final Font hvFont = new Font(Font.DIALOG, Font.BOLD, 14);
	private static final Font hiFont = new Font(Font.DIALOG, Font.BOLD
			| Font.ITALIC, 15);

	private static final Color selColor = Color.red;
	private static final Color hovColor = Color.black;
	private static final Color defColor = Color.black;

	private static void paint(Graphics2D g2d, Item i) {
		int code = i.randCode() % 3;
		Color drawColor = code == 0 ? new Color(0, 0, 255, 127)
				: code == 1 ? new Color(0, 255, 0, 127) : new Color(255, 0, 0,
						127);
		boolean target = (i instanceof FolderItem && ((FolderItem) i)
				.isTarget());
		if (i.isSelected() || target) {
			Color c = g2d.getColor();
			g2d.setColor(drawColor);
			if (i.isSelected())
				g2d.fillOval(i.x - 15, i.y - 15, i.width + 30, i.height + 30);
			else
				g2d.drawOval(i.x - 15, i.y - 15, i.width + 30, i.height + 30);
			g2d.setColor(c);
		}
		g2d.drawImage(i.getIcon(), i.x, i.y, null);

		if (i instanceof FolderItem || i.isHovered() || i.isSelected()) {
			int w = g2d.getFontMetrics().stringWidth(i.getName());
			w = (i.width / 2) - (w / 2);
			g2d.setColor(i.isSelected() ? selColor : i.isHovered() ? hovColor
					: defColor);
			g2d.drawString(i.getName(), (i.x + w) < 1 ? 1 : (i.x + w), i.y
					+ i.height
					+ (i.isSelected() ? 41 : i.isHovered() ? 27 : 13));
		} else {
			String s = i.getName();
			int limit = i.width + 25;
			int w = g2d.getFontMetrics().stringWidth(s);
			if (w > limit) {
				for (int x = 0; x < s.length(); x++) {
					s = s.substring(0, s.length() - 2);
					w = g2d.getFontMetrics().stringWidth(s);
					if (w <= limit) {
						s += "...";
						break;
					}
				}
				s += "...";
				w = g2d.getFontMetrics().stringWidth(s);
			} else {

			}
			w = ((i.width / 2) - (w / 2));
			g2d.drawString(s, i.x + w, i.y + i.height + 13);
		}

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
			if (i == null)
				continue;
			if (i.isSelected())
				g2d.setFont(hiFont);
			else if (i.isHovered())
				g2d.setFont(hvFont);
			else
				g2d.setFont(defFont);
			g2d.setColor(Color.black);
			paint(g2d, i);
		}
		g2d.dispose();
	}

}
