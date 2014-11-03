
package rickelectric.furkmanager.views.swingmods.balloon;

import java.awt.Point;
import java.awt.Rectangle;

import net.java.balloontip.positioners.BalloonTipPositioner;

public class BTipPositioner extends BalloonTipPositioner {
	private int x = 0;	// Current position of the balloon tip
	private int y = 0;

	public void determineAndSetLocation(Rectangle attached) {
		x = attached.x - 23 + attached.width/2;
		y = attached.y - balloonTip.getPreferredSize().height;
		
		balloonTip.setBounds(x, y, balloonTip.getPreferredSize().width, balloonTip.getPreferredSize().height);
		balloonTip.revalidate();
	}

	public Point getTipLocation() {
		return new Point(x + 20, y + balloonTip.getPreferredSize().height);
	}

	protected void onStyleChange() {
		balloonTip.getStyle().setHorizontalOffset(20);
		balloonTip.getStyle().setVerticalOffset(20);
	}
}
