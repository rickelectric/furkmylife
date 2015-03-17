package rickelectric.furkmanager.views.panels;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.DropMode;
import javax.swing.ImageIcon;
import javax.swing.JLayeredPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.network.APIFolderManager;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.views.icons.FileTreeNode;
import rickelectric.furkmanager.views.icons.FurkTreeNode;
import rickelectric.furkmanager.views.iconutil.FolderTreeRenderer;
import rickelectric.furkmanager.views.iconutil.FolderTreeTransferHandler;
import rickelectric.img.ImageLoader;
import rickelectric.swingmods.JFadeLabel;
import rickelectric.swingmods.OpacEffects;

public class File_FolderView extends JLayeredPane implements MouseListener,
		KeyListener, Observer {
	private static final long serialVersionUID = 1L;

	private Thread thisRun;

	private JTree folder_tree;
	private JScrollPane scroller;
	private TransferHandler transferer;
	private TreeCellRenderer renderer;
	private JFadeLabel loading;
	private JPopupMenu menu;

	public File_FolderView() {
		setBorder(new EmptyBorder(5, 5, 5, 5));
		setLayout(new BorderLayout(0, 0));

		scroller = new JScrollPane(folder_tree);

		add(scroller, BorderLayout.CENTER);

		loading = new JFadeLabel(new ImageIcon(
				ImageLoader.class.getResource("ajax-loader.gif")));
		loading.setAlpha(1.0f);
		setLayer(loading, 10);
		add(loading);

		transferer = new FolderTreeTransferHandler();
		renderer = new FolderTreeRenderer();

		APIFolderManager.addObserver(this);
		API_File.addObserver(this);
		refreshMyFolders(false);
	}

	@Override
	public void repaint() {
		if (scroller != null)
			scroller.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
		super.repaint();
	}

	public JScrollPane getScroller() {
		return scroller;
	}

	private DefaultTreeModel getTreeModel(boolean hardReload) {
		if (hardReload)
			APIFolderManager.refresh();
		return APIFolderManager.getModel();
	}

	protected void refreshMyFolders(final boolean hardReload) {
		thisRun = new Thread(new Runnable() {
			@Override
			public void run() {
				loading.setVisible(true);
				loading.setAlpha(1.0f);
				if (folder_tree == null) {
					folder_tree = new JTree();

					folder_tree.addMouseListener(File_FolderView.this);
					folder_tree.addKeyListener(File_FolderView.this);

					folder_tree.setDragEnabled(true);
					folder_tree.setDropMode(DropMode.ON_OR_INSERT);

					folder_tree.setTransferHandler(transferer);
					folder_tree.setCellRenderer(renderer);

					folder_tree.putClientProperty("JTree.lineStyle", "Angled");
					folder_tree.setFont(new Font("Segoe UI", Font.PLAIN, 13));
					folder_tree.getSelectionModel().setSelectionMode(
							TreeSelectionModel.SINGLE_TREE_SELECTION);

					scroller.setViewportView(folder_tree);
				}
				folder_tree.setEnabled(false);
				try {
					folder_tree.setModel(getTreeModel(hardReload));

					OpacEffects.fadeOut(loading, 30);
					folder_tree.setEnabled(true);
					folder_tree.requestFocus();
				} catch (Exception e) {
					OpacEffects.fadeOut(loading, 30);
					folder_tree.setEnabled(true);
				}
				repaint();
			}
		});
		thisRun.start();
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		int row = folder_tree.getRowForLocation(e.getX(), e.getY());
		if (row == -1) {
			folder_tree.clearSelection();
			return;
		}

		TreePath tp = folder_tree.getPathForLocation(e.getX(), e.getY());

		if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
			menuAction(tp, e, 1);
		} else if (e.getButton() == MouseEvent.BUTTON3
				&& e.getClickCount() == 1) {
			menuAction(tp, e, 2);
		}
	}

	/**
	 * 
	 * @param tp
	 *            TreePath of the item whose menu will be opened or action will
	 *            be executed
	 * @param e
	 *            Point to open the ContextMenu relative to the JTree's location
	 * @param action
	 *            Action to perform (Node Action / Context Menu 1:Execute
	 *            <code>action()</code>, 2:Open Context Menu
	 */
	private void menuAction(TreePath tp, Object e, int action) {

		folder_tree.setSelectionPath(tp);

		DefaultMutableTreeNode tpp = (DefaultMutableTreeNode) tp
				.getPathComponent(tp.getPathCount() - 1);
		if (!(tpp instanceof FurkTreeNode))
			return;

		FurkTreeNode src = (FurkTreeNode) tpp;

		if (action == 1) {
			if (src instanceof FileTreeNode) {
				((FileTreeNode) src).action(new Runnable() {
					@Override
					public void run() {
						folder_tree.repaint();
					}
				});
			} else {
				src.action();
			}
		} else if (action == 2) {
			menu = src.popupMenu();
			if (e instanceof MouseEvent) {
				menu.show(((MouseEvent) e).getComponent(),
						((MouseEvent) e).getX(), ((MouseEvent) e).getY());
			} else if (e instanceof Point) {
				menu.show(folder_tree, ((Point) e).x, ((Point) e).y);
			}

		}

	}

	private void statusPath(TreePath tp) {
		String pathString = tp.toString();
		String ps = pathString.substring(1, pathString.length() - 1);

		String[] sp = ps.split(", ");
		ps = "furk:/";
		for (String s : sp) {
			ps += "/" + s;
		}
		FurkManager.getMainWindow().setStatus(ps);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_CONTEXT_MENU) {
			TreePath tp = folder_tree.getSelectionPath();
			Point p = folder_tree.getRowBounds(folder_tree.getRowForPath(tp))
					.getLocation();
			menuAction(tp, p, 2);
		} else if (e.getKeyCode() == KeyEvent.VK_ENTER
				&& (menu == null || !menu.isShowing())) {
			TreePath tp = folder_tree.getSelectionPath();
			Point p = folder_tree.getRowBounds(folder_tree.getRowForPath(tp))
					.getLocation();
			menuAction(tp, p, 1);
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		TreePath tp = folder_tree.getSelectionPath();
		if (tp != null) {
			statusPath(tp);
		}
		if (e.getKeyCode() == KeyEvent.VK_F5) {
			refreshMyFolders(e.isShiftDown());
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void update(Observable o, Object arg1) {
		if (o instanceof API_File.FileObservable) {
			if (arg1 == API_File.FINISHED)
				refreshMyFolders(false);
		} else {
			if (arg1 == APIFolderManager.UpdateSource.INIT)
				refreshMyFolders(false);
			else if (arg1 instanceof APIFolderManager.UpdateSource)
				refreshMyFolders(true);
		}
	}

}
