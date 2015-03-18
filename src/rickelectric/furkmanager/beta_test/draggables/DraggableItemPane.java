package rickelectric.furkmanager.beta_test.draggables;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JPanel;

import rickelectric.UtilBox;
import rickelectric.furkmanager.beta_test.draggables.models.Item;

public class DraggableItemPane extends JPanel {
	private static final long serialVersionUID = 1L;

	private static final int ITEM_SPACING = 50;

	private ArrayList<Item> allItems;

	public DraggableItemPane(ArrayList<Item> allItems) {
		this.allItems = allItems;
	}

	public synchronized void alignToGrid() {
		if (getTopLevelAncestor() == null)
			return;
		int w = getTopLevelAncestor().getWidth() - ITEM_SPACING;
		int numUnitsW = (int) Math.ceil(w / (64 + ITEM_SPACING));

		int start = ITEM_SPACING / 2;

		ArrayList<Item> items = new ArrayList<Item>();
		for (Item i : allItems)
			items.add(i);
		Collections.sort(items);
		int i = 0, j = 0;
		while (!items.isEmpty()) {
			Item item = items.remove(0);
			if (item != null) {
				item.setLocation(start + j * (64 + ITEM_SPACING), start + i
						* (64 + ITEM_SPACING));

				j++;
				if (j >= numUnitsW) {
					j = 0;
					i++;
				}
			}
			repaint();
			UtilBox.getInstance().wait(80);
		}
		getTopLevelAncestor().validate();
		getTopLevelAncestor().repaint();
	}

	public void alignRadialTriangle() {
		if (getTopLevelAncestor() == null)
			return;
		int start = ITEM_SPACING / 2;

		ArrayList<Item> items = new ArrayList<Item>();
		for (Item i : allItems)
			items.add(i);
		Collections.sort(items);
		ArrayList<Thread> workers = new ArrayList<Thread>();
		int i = 0, j = 0, k = 0, rb = 0;
		while (k < items.size()) {
			Item item = items.get(k);
			if (item != null) {
				Point dest = new Point(start + j * (64 + ITEM_SPACING), start
						+ i * (64 + ITEM_SPACING));
				Thread wk = moveToLocation(item, dest);
				if (wk != null) {
					workers.add(wk);
				}

				if (i == j) {
					rb++;
					i = rb;
					j = 0;
				} else if (i > j) {
					i = j;
					j = rb;
				} else {
					j = i + 1;
					i = rb;
				}
				repaint();
				UtilBox.getInstance().wait(100);
			}
			k++;
		}
		for (Thread t : workers)
			if (t.isAlive())
				try {
					t.join();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		getTopLevelAncestor().validate();
		getTopLevelAncestor().repaint();
	}

	@SuppressWarnings("unused")
	private void moveToLocationOld(final Item item, final Point destPoint) {
		if (item.getLocation().equals(destPoint))
			return;
		Thread t = new Thread(new Runnable() {
			public void run() {
				Point center = Geometry.findCentre(item.getLocation(),
						destPoint);

				double radius = Math.sqrt(Math.pow(center.x + destPoint.x, 2)
						+ Math.pow(center.y + destPoint.y, 2));
				System.out.println("Item: " + item.getName());

				System.out.println("Source,Destination = " + item.getLocation()
						+ "," + destPoint);

				System.out.println("Center = (" + center.x + "," + center.y
						+ ")");
				double srcAngle = Geometry.getDegreesAngleBetween(center,
						item.getLocation());
				srcAngle %= 360;
				double destAngle = srcAngle + 180;

				double space = 180;
				double inc = 2;
				double proc = 0;
				int direction = Math.random() > 0.5 ? 1 : -1;

				while (proc < space) {
					proc += inc;
					System.out.println("Proc = " + proc);
					Point pt = Geometry.getPointOnCircle(center, radius,
							srcAngle + (direction * proc));
					item.setLocation(pt);

					repaint();
					UtilBox.getInstance().wait(40);
				}
				Point pt = Geometry.getPointOnCircle(center, radius, destAngle);
				item.setLocation(pt);
			}
		});
		t.setDaemon(true);
		t.start();
	}

	private Thread moveToLocation(final Item item, final Point dest) {
		if (item.getLocation().equals(dest))
			return null;
		Thread t = new Thread(new Runnable() {
			public void run() {
				int wait = 15;
				Point src = item.getLocation();
				if (dest.x == src.x) {
					int inc = 5 * (int) Math.ceil(Math.signum(dest.y - src.y));
					int pt = src.y;
					while (Math.abs(pt - dest.y) > 2) {
						pt = pt + inc;
						item.setLocation(src.x, pt);
						repaint();
						UtilBox.getInstance().wait(wait);
					}
				} else if (dest.y == src.y) {
					int inc = 5 * (int) Math.ceil(Math.signum(dest.x - src.x));
					int pt = src.x;
					while (Math.abs(pt - dest.x) > 2) {
						pt = pt + inc;
						item.setLocation(pt, src.y);
						repaint();
						UtilBox.getInstance().wait(wait);
					}
				} else {
					// Equations y=mx+c
					double m = (dest.y - src.y) / (dest.x - src.x);
					double c = dest.y - m * dest.x;

					if (Math.abs(m) < 1) {
						int inc = 5 * (int) Math.signum(dest.y - src.y);
						double y = src.y, x = (y - c) / m;
						while (Math.abs(y - dest.y) > 2) {
							y += inc;
							x = (y - c) / m;
							item.setLocation((int) x, (int) y);
							repaint();
							UtilBox.getInstance().wait(wait);
						}
					} else {
						int inc = (int) Math.signum(dest.x - src.x);
						double x = src.x, y = m * x + c;
						while (Math.abs(x - dest.x) > 2) {
							x += inc;
							y = m * x + c;
							item.setLocation((int) x, (int) y);
							repaint();
							UtilBox.getInstance().wait(wait);
						}
					}

				}
				item.setLocation(dest.x, dest.y);
				repaint();
			}
		});
		t.setDaemon(true);
		t.start();
		return t;
	}

	public void alignRadial() {
		int w = getTopLevelAncestor().getWidth() - ITEM_SPACING;
		int numUnitsW = (int) Math.ceil(w / (64 + ITEM_SPACING));

		int start = ITEM_SPACING / 2;

		ArrayList<Item> items = new ArrayList<Item>();
		for (Item i : allItems)
			items.add(i);
		Collections.sort(items);
		int i = 0, j = 0, base = 0;
		while (!items.isEmpty()) {
			Item item = items.remove(0);
			if (item != null) {
				item.setLocation(start + j * (64 + ITEM_SPACING), start + i
						* (64 + ITEM_SPACING));
				if (i == 0) {
					base++;
					i = base;
					j = 0;
				} else if (i <= j) {
					i--;
				} else {
					if (j == numUnitsW) {
						base++;
						i = base;
						j = 0;
					} else {
						j++;
					}
				}
				repaint();
				UtilBox.getInstance().wait(50);
			}
		}
		validate();
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.white);
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setColor(Color.black);
		@SuppressWarnings("unchecked")
		ArrayList<Item> cloned = (ArrayList<Item>) allItems.clone();
		Collections.reverse(cloned);

		ModelPainter.paint(g, cloned);

		cloned.removeAll(cloned);
		cloned = null;
	}

	@Override
	public void paintAll(Graphics g) {
		paintComponent(g);
	}

}
