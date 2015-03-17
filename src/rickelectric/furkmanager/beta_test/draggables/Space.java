package rickelectric.furkmanager.beta_test.draggables;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
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
import java.util.Collections;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.RepaintManager;

import rickelectric.UtilBox;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.beta_test.draggables.menus.ItemContextMenu;
import rickelectric.furkmanager.beta_test.draggables.menus.SpaceContextMenu;
import rickelectric.furkmanager.beta_test.draggables.models.FileItem;
import rickelectric.furkmanager.beta_test.draggables.models.FolderDescriptor;
import rickelectric.furkmanager.beta_test.draggables.models.FolderItem;
import rickelectric.furkmanager.beta_test.draggables.models.Item;
import rickelectric.furkmanager.models.LoginModel;
import rickelectric.furkmanager.network.api_new.FurkAPI;
import rickelectric.furkmanager.views.LoadingCircle;
import rickelectric.furkmanager.views.windows.FurkFileView;
import rickelectric.img.ImageLoader;
import rickelectric.swingmods.JButtonLabel;
import javax.swing.border.BevelBorder;
import javax.swing.JProgressBar;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Space extends JFrame implements KeyListener, MouseListener,
		MouseMotionListener, Runnable {
	private static final long serialVersionUID = 1L;

	private DraggablesManager mgr;
	private Point startPoint, prevPoint;

	private static Point spawnPoint;
	private Item str;

	private ArrayList<Item> allItems;

	private JScrollPane scroller;
	private JPanel contentPane;
	private LoadingCircle circ;

	private Point screenPoint, vPoint;

	private JPanel rootPane;

	private JPanel navBar;

	private JButtonLabel nav_back;

	private Point zero;
	private JButtonLabel btnlblReload;
	private JPanel statusPanel;
	private JProgressBar actionProgress;
	private JLabel lblReady;

	public static void main(String... strings) {
		FurkManager.init();
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
		setSize(getSize().width,getSize().height+1);
		setSize(getSize().width,getSize().height-1);
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
				"folder/prev-32.png")));
		navBar.add(nav_back);
		
		btnlblReload = new JButtonLabel("Reload",new Runnable(){
			public void run(){
				reload();
			}
		});
		btnlblReload.setIcon(new ImageIcon(Space.class.getResource("/rickelectric/img/dash/Reload-32.png")));
		btnlblReload.setPreferredSize(new Dimension(80, 34));
		navBar.add(btnlblReload);

		scroller = new JScrollPane();

		contentPane = new JPanel() {
			private static final long serialVersionUID = 1L;

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
		};
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
		statusPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		rootPane.add(statusPanel, BorderLayout.SOUTH);
		statusPanel.setLayout(new BorderLayout(0, 0));
		
		actionProgress = new JProgressBar();
		actionProgress.setIndeterminate(true);
		actionProgress.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
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
			if (s.contains(p)) {
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
		if(e.getPoint().getX()<0 || e.getPoint().getY()<0){
			str.setLocation(prevPoint);
		}else{
			mgr.updateItemLocation(str);
			sizeContentPane();
			position(prevPoint);
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
				spawnPoint = new Point(e.getX(), e.getY());
				SpaceContextMenu mnSpace = new SpaceContextMenu(this);
				mnSpace.show(e.getComponent(), e.getX(), e.getY());
			}
		} else {
			final Item spr = all.get(0);
			selectItem(spr);
			if (e.getButton() == MouseEvent.BUTTON3) {
				ItemContextMenu m = new ItemContextMenu(this, spr);
				m.show(e.getComponent(), e.getX(), e.getY());
			} else if (e.getButton() == MouseEvent.BUTTON1) {
				if (e.getClickCount() == 2) {
					if (spr instanceof FolderItem) {
						mgr.setCurrentFolder(((FolderItem) spr).getDescriptor());
						loadItemsInCurrentFolder();
					} else if (spr instanceof FileItem) {
						// TODO Open FurkFile (As TFiles or As FileView)
						new FurkFileView(((FileItem) spr).getDescriptor()
								.getFileObject());
					}
				} else if (e.getClickCount() == 1) {
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
			i.setSelected(false);
		}
		repaint();
	}

	public void mouseMoved(MouseEvent e) {
		Item i = getSelectedItem(e.getPoint());
		if (i != null) {
			contentPane.setToolTipText(i.getToolTip());
		} else
			contentPane.setToolTipText(null);
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
		new Thread(new Runnable(){
			public void run(){
				
				FolderDescriptor currentFolder = mgr.getCurrentFolder();
				loadItems();
				mgr.setCurrentFolder(currentFolder);
				loadItemsInCurrentFolder();
			}
		}).start();
	}

	private void back() {
		if (mgr.getCurrentFolder().getParent() != null) {
			mgr.setCurrentFolder(mgr.getCurrentFolder().getParent());
			loadItemsInCurrentFolder();
		}
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
