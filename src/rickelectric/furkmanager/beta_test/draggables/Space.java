package rickelectric.furkmanager.beta_test.draggables;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.RepaintManager;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import rickelectric.UtilBox;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.beta_test.draggables.menus.ItemContextMenu;
import rickelectric.furkmanager.beta_test.draggables.menus.SpaceContextMenu;
import rickelectric.furkmanager.beta_test.draggables.models.FileItem;
import rickelectric.furkmanager.beta_test.draggables.models.FolderDescriptor;
import rickelectric.furkmanager.beta_test.draggables.models.FolderItem;
import rickelectric.furkmanager.beta_test.draggables.models.Item;
import rickelectric.furkmanager.models.LoginModel;
import rickelectric.furkmanager.network.api_new.FileAPI;
import rickelectric.furkmanager.network.api_new.FurkAPI;
import rickelectric.furkmanager.network.api_new.LabelAPI;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.views.LoadingCircle;
import rickelectric.furkmanager.views.windows.FurkFileView;
import rickelectric.img.ImageLoader;
import rickelectric.swingmods.JButtonLabel;

public class Space extends JFrame implements KeyListener, MouseListener,
		MouseMotionListener, Runnable {
	private static final long serialVersionUID = 1L;

	private DraggablesManager mgr;
	private Point startPoint, prevPoint;

	private static Point spawnPoint;
	private Item str;

	private ArrayList<Item> allItems;

	private JScrollPane scroller;
	private DraggableItemPane contentPane;
	private LoadingCircle circ;

	private Point screenPoint, vPoint;

	private JPanel rootPane;

	private JPanel navBar;

	private JButtonLabel nav_back, nav_reload, nav_align;

	private Point zero;
	private JPanel statusPanel;
	private JProgressBar actionProgress;
	private JLabel lblReady;

	public static void main(String... strings) {
		FurkManager.LAF(0);
		FurkManager.init();
		FileAPI.dummy = true;
		LabelAPI.dummy = true;
		LoginModel lm = new LoginModel(
				"5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		try {
			boolean logged = FurkAPI.getInstance().login(lm);
			System.out.println("Logged In: " + logged);
			if (logged) {
				Space s = new Space();
				s.setSize(1024, 700);
				s.setVisible(true);
				s.setLocationRelativeTo(null);
				s.loadItems();
				s.loadItemsInCurrentFolder();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadItems() {
		actionProgress.setIndeterminate(true);
		mgr.loadItems();
		actionProgress.setIndeterminate(false);
	}

	public void loadItemsInCurrentFolder() {
		allItems.removeAll(allItems);
		allItems.addAll(mgr.getItemsInCurrentFolder());

		nav_back.setEnabled(!mgr.getCurrentFolder().equals(
				mgr.getTree().getRoot()));

		sizeContentPane();
		repaint();
	}

	private boolean sizeContentPane() {
		Dimension sz = contentPane.getSize();
		boolean newSz = false;
		for (Item i : allItems) {
			if (i.x + i.width > sz.width) {
				newSz = true;
				sz.width = i.x + i.width + 10;
			}
			if (i.y + i.height > sz.height) {
				newSz = true;
				sz.height = i.y + i.height + 20;
			}
		}
		contentPane.setPreferredSize(sz);
		position(zero);
		validate();
		return newSz;
	}

	public Space() {
		setTitle("Furk File-Spaces");
		setIconImage(ImageLoader.getInstance().getImage("fr.png"));
		setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				close();
			}
		});

		zero = new Point(0, 0);
		mgr = new DraggablesManager();
		allItems = new ArrayList<Item>();

		rootPane = new JPanel();
		rootPane.setLayout(new BorderLayout());
		setContentPane(rootPane);

		navBar = new JPanel();
		navBar.setLayout(new FlowLayout(FlowLayout.LEFT));
		navBar.setPreferredSize(new Dimension(10, 48));
		rootPane.add(navBar, BorderLayout.NORTH);

		nav_back = new JButtonLabel("Back", new Runnable() {
			public void run() {
				back();
			}
		});
		nav_back.setPreferredSize(new Dimension(80, 34));
		nav_back.setEnabled(false);
		nav_back.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
				"spaces/prev-32.png")));
		navBar.add(nav_back);

		nav_reload = new JButtonLabel("Reload", new Runnable() {
			public void run() {
				reload();
			}
		});
		nav_reload.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
				"dash/Reload-32.png")));
		nav_reload.setPreferredSize(new Dimension(80, 34));
		navBar.add(nav_reload);

		nav_align = new JButtonLabel("Align", new Runnable() {
			public void run() {
				JPopupMenu menu = new JPopupMenu();
				Point sc = nav_align.getLocation(Space.this.getContentPane()
						.getLocation());
				// menu.setLocation(sc.x,sc.y+nav_align.getHeight());

				final JMenuItem grid = new JMenuItem("To Grid");
				menu.add(grid);

				final JMenuItem radial = new JMenuItem("To Square");
				menu.add(radial);

				final JMenuItem triangle = new JMenuItem("To Triangle");
				menu.add(triangle);

				ActionListener listener = new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						Object src = e.getSource();
						if (src.equals(grid)) {
							alignGrid();
						} else if (src.equals(radial)) {
							alignRadial();
						} else if (src.equals(triangle)) {
							alignRadialTriangle();
						}
					}
				};
				grid.addActionListener(listener);
				radial.addActionListener(listener);
				triangle.addActionListener(listener);
				menu.show(Space.this.getContentPane(), sc.x,
						sc.y + nav_align.getHeight());
			}
		});
		nav_align.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
				"sm/menu_expand.png")));
		nav_align.setPreferredSize(new Dimension(80, 34));
		navBar.add(nav_align);

		scroller = new JScrollPane();

		contentPane = new DraggableItemPane(allItems);
		contentPane.setLayout(null);
		circ = new LoadingCircle();
		enableDoubleBuffering(contentPane);
		scroller.setViewportView(contentPane);
		scroller.getVerticalScrollBar().setUnitIncrement(30);

		contentPane.addMouseListener(this);
		contentPane.addMouseMotionListener(this);

		addKeyListener(this);
		contentPane.addKeyListener(this);

		rootPane.add(scroller, BorderLayout.CENTER);

		statusPanel = new JPanel();
		statusPanel.setPreferredSize(new Dimension(10, 23));
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		rootPane.add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setLayout(new BorderLayout(0, 0));

		actionProgress = new JProgressBar();
		actionProgress.setPreferredSize(new Dimension(200, 14));
		actionProgress.setIndeterminate(true);
		actionProgress.setBorder(new BevelBorder(BevelBorder.RAISED, null,
				null, null, null));
		statusPanel.add(actionProgress, BorderLayout.WEST);

		lblReady = new JLabel("Ready");
		lblReady.setHorizontalAlignment(SwingConstants.CENTER);
		statusPanel.add(lblReady, BorderLayout.CENTER);

		setVisible(true);
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}

	public void loading(boolean b) {
		if (b) {
			Dimension size = vpSize();
			Point vloc = position();
			circ.setLocation(vloc.x + (size.width / 2 - circ.getWidth() / 2),
					vloc.y + (size.height / 2 - circ.getHeight() / 2));
			contentPane.add(circ);
		} else {
			contentPane.remove(circ);
		}
		repaint();
	}

	public void close() {
		mgr.save();
		dispose();
		System.gc();
		System.exit(0);
	}

	public Dimension vpSize() {
		return scroller.getViewport().getExtentSize();
	}

	private Point position() {
		return scroller.getViewport().getViewPosition();
	}

	private void position(Point p) {
		scroller.getViewport().setViewPosition(p);
		scroller.repaint();
	}

	private Item getSelectedItem(Point p) {
		Item str = null;
		boolean scanComplete = false;
		for (Item s : allItems()) {
			if(s==null) continue;
			if (s.contains(p)) {
				str = s;
				scanComplete = true;
				break;
			}
			if (scanComplete)
				break;
		}
		return str;
	}

	private ArrayList<Item> getItemsBelow(Point p) {
		ArrayList<Item> str = new ArrayList<Item>();
		for (Item s : allItems()) {
			if (s!=null && s.contains(p)) {
				str.add(s);
			}
		}
		return str;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1)
			return;
		spawnPoint = new Point(e.getX(), e.getY());
		str = getSelectedItem(spawnPoint);
		allItems.remove(str);
		allItems.add(0, str);
		if (str == null) {
			screenPoint = e.getLocationOnScreen();
			vPoint = position();
			return;
		}

		prevPoint = new Point(str.getLocation());
		startPoint = new Point(spawnPoint);
		startPoint.x -= str.getLocation().x;
		startPoint.y -= str.getLocation().y;

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1)
			return;
		if (str == null) {
			str = null;
			screenPoint = null;
			vPoint = null;
			return;
		}
		if (e.getPoint().equals(spawnPoint))
			return;
		if (e.getPoint().getX() < 0 || e.getPoint().getY() < 0) {
			str.setLocation(prevPoint);
		} else {
			mgr.updateItemLocation(str);
			sizeContentPane();
			if (str.getX() < this.vpSize().width
					&& str.getY() < vpSize().height) {
				position(zero);
			} else {
				position(str.getLocation());
			}
		}
		prevPoint = null;
		startPoint = null;
		str = null;

		repaint();
	}

	private void updateStr(Point p) {
		if (str == null)
			return;
		int x = p.x - startPoint.x;
		int y = p.y - startPoint.y;
		str.setLocation(new Point(x, y));
		repaint();
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if (screenPoint != null) {
			Point np = e.getLocationOnScreen();
			int difx = vPoint.x + (screenPoint.x - np.x);
			int dify = vPoint.y + (screenPoint.y - np.y);
			if (difx < 0)
				difx = 0;
			if (dify < 0)
				dify = 0;
			if (difx + vpSize().width > contentPane.getWidth())
				difx = contentPane.getWidth() - vpSize().width;
			if (dify + vpSize().height > contentPane.getHeight())
				dify = contentPane.getHeight() - vpSize().height;
			position(new Point(difx, dify));
			return;
		}
		if (startPoint == null)
			return;

		updateStr(e.getPoint());
	}

	public void mouseClicked(MouseEvent e) {
		final ArrayList<Item> all = getItemsBelow(e.getPoint());
		deselectAll();
		if (all == null || all.size() == 0) {
			if (e.getButton() == MouseEvent.BUTTON3) {
				// Context Menu For Blank Space
				spawnPoint = new Point(e.getX(), e.getY());
				SpaceContextMenu mnSpace = new SpaceContextMenu(this);
				mnSpace.show(e.getComponent(), e.getX(), e.getY());
			}
		} else {
			final Item spr = all.get(0);
			selectItem(spr);
			if (e.getButton() == MouseEvent.BUTTON3) {
				// Context Menu For Item
				ItemContextMenu m = new ItemContextMenu(this, spr);
				m.show(e.getComponent(), e.getX(), e.getY());
			} else if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.getClickCount() == 2) {
					// Open Action For Item
					if (spr instanceof FolderItem) {
						mgr.setCurrentFolder(((FolderItem) spr).getDescriptor());
						loadItemsInCurrentFolder();
					} else if (spr instanceof FileItem) {
						// TODO Open FurkFile (As TFiles or As FileView)
						new FurkFileView(((FileItem) spr).getDescriptor()
								.getFileObject());
					}
				} else if (e.getClickCount() == 1) {
					// Select Item
					selectItem(spr);
				}
			}
		}
	}

	private void selectItem(Item spr) {
		spr.setSelected(true);
		repaint();
	}

	private void deselectAll() {
		for (Item i : allItems) {
			if(i==null) continue;
			i.setSelected(false);
			i.setHovered(false);
		}
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		if (str == null) {
			for (Item i : allItems) {
				if(i==null) continue;
				i.setHovered(false);
			}
			Item i = getSelectedItem(e.getPoint());
			if (i != null) {
				contentPane.setToolTipText(i.getToolTip());
				i.setHovered(true);
			} else {
				contentPane.setToolTipText(null);
			}
			validate();
			repaint();
		}
	}

	public Item getSelectedItem() {
		return str;
	}

	public ArrayList<Item> allItems() {
		return allItems;
	}

	public static Point getSpawnPoint() {
		return spawnPoint;
	}

	public void exportImage() {
		BufferedImage image = new BufferedImage(getWidth(), getHeight(),
				BufferedImage.TYPE_INT_ARGB);
		contentPane.printAll(image.getGraphics());
		String s = UtilBox.getInstance().openFile("Image");
		if (s != null) {
			File f = new File(s);
			try {
				Thread.sleep(2000);
				ImageIO.write(image, "png", f);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void disableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(false);
	}

	public static void enableDoubleBuffering(Component c) {
		RepaintManager currentManager = RepaintManager.currentManager(c);
		currentManager.setDoubleBufferingEnabled(true);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			back();
		}
	}

	protected void reload() {
		new Thread(new Runnable() {
			public void run() {

				FolderDescriptor currentFolder = mgr.getCurrentFolder();
				loadItems();
				mgr.setCurrentFolder(currentFolder);
				loadItemsInCurrentFolder();
			}
		}).start();
	}

	protected void alignGrid() {
		new Thread(new Runnable() {
			public void run() {
				contentPane.alignToGrid();
				for (Item item : allItems) {
					mgr.updateItemLocation(item);
				}
			}
		}).start();
	}

	protected void alignRadial() {
		new Thread(new Runnable() {
			public void run() {
				contentPane.alignRadial();
				for (Item item : allItems) {
					mgr.updateItemLocation(item);
				}
			}
		}).start();
	}

	protected void alignRadialTriangle() {
		new Thread(new Runnable() {
			public void run() {
				contentPane.alignRadialTriangle();
				for (Item item : allItems) {
					mgr.updateItemLocation(item);
				}
			}
		}).start();
	}

	private void back() {
		Thread t = ThreadPool.getThread(new Runnable() {
			public void run() {
				if (mgr.getCurrentFolder().getParent() != null) {
					mgr.setCurrentFolder(mgr.getCurrentFolder().getParent());
					loadItemsInCurrentFolder();
				}
			}
		});
		t.setDaemon(true);
		t.start();
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void keyTyped(KeyEvent e) {
	}

	public void keyPressed(KeyEvent e) {
	}

	public DraggablesManager getManager() {
		return mgr;
	}

	@Override
	public void run() {
		while (true) {
			repaint();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
				break;
			}
		}
	}
}
