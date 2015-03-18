package rickelectric.furkmanager.beta_test.draggables;

import java.awt.Point;

public class Geometry {

	public static Point findCentre(Point A, Point B) {
		return new Point((A.x + B.x) / 2, (A.y + B.y) / 2);
	}

	public static Point getPointOnCircle(Point center, double radius,
			double degreesAngle) {
		double radiansAngle = Math.toRadians(degreesAngle);

		double x = center.getX() + (radius * Math.cos(radiansAngle));
		double y = center.getY() + (radius * Math.sin(radiansAngle));
		Point mark = new Point();
		mark.setLocation(x, y);
		return mark;
	}

	public static double getDegreesAngleBetween(Point a, Point b) {
		double dx = b.x - a.x, dy = b.y - a.y, bearing;
		if (dx > 0) {
			bearing = 90 - Math.atan2(dy, dx);
		} else if (dx < 0) {
			bearing = 270 - Math.atan2(dy, dx);
		} else {
			if (dy > 0)
				bearing = 0;
			else if (dy < 0)
				bearing = 180;
			else
				bearing = 0;
		}
		return bearing;
	}
}
