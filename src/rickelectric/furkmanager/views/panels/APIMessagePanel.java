package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import rickelectric.furkmanager.models.APIMessage;
import rickelectric.furkmanager.network.api.API;

public class APIMessagePanel extends JPanel {
	private static final long serialVersionUID = 1L;
	private ArrayList<APIMessage> messages;
	private int msgNum;
	private int xWidth;

	public APIMessagePanel() {
		setBackground(new Color(241,241,241));
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (messages == null)
			return;
		xWidth = getWidth() - 10;
		msgNum = 0;
		messages = API.getMessages();
		Graphics2D g2d = (Graphics2D) g.create();

		g2d.setColor(Color.black);
		for (APIMessage msg : messages) {
			g2d.setColor(g2d.getColor().equals(Color.black) ? Color.red
					: Color.black);
			drawMessage(g2d, msg);
		}

		g2d.dispose();
	}

	private void drawMessage(Graphics2D g2d, APIMessage msg) {
		g2d.setColor(Color.black);
		int yStart = 5 + msgNum * 100;

		g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 16));
		g2d.drawString(msg.getType().toUpperCase(), 10, yStart + 20);
		FontMetrics metrics = g2d.getFontMetrics();
		ArrayList<String> str = new ArrayList<String>();
		String curr = msg.getText();
		int i = 1;
		while (i < curr.length()) {
			if (metrics.stringWidth(curr.substring(0, i)) > xWidth - 20) {
				str.add(curr.substring(0, i));
				curr = curr.substring(i, curr.length());
				i = 0;
			}
			i++;
		}
		str.add(curr);
		int height = 20 * str.size();
		Rectangle2D box = new Rectangle2D.Double(5, yStart, xWidth, height + 10);
		g2d.fill(box);
		g2d.setColor(Color.white);
		for (i = 1; i <= str.size(); i++) {
			g2d.drawString(str.get(i - 1), 10, yStart + 5 + (20 * i));
		}
	}

	public void update() {
		repaint();
	}

}
