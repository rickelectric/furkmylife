package rickelectric.furkmanager.views.panels;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkTFile;
import rickelectric.furkmanager.network.api.API_TFile;
import rickelectric.furkmanager.views.Statable;
import rickelectric.furkmanager.views.icons.FurkTreeNode;
import rickelectric.furkmanager.views.icons.TFileTreeNode;
import rickelectric.furkmanager.views.iconutil.NodeImageObserver;

public class TFileTreePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	DefaultMutableTreeNode root;
	DefaultTreeModel model;
	JTree jtree;
	JTextField jtf;

	boolean ready = false;

	public boolean isReady() {
		return ready;
	}

	public TFileTreePanel(FurkFile ff) {
		root = getTFileTree(ff);
		init();
	}

	public static DefaultMutableTreeNode getTFileTree(FurkFile ff) {
		ArrayList<FurkTFile> files = API_TFile.getFrom(ff);
		if (ff == null)
			return null;

		String rootname = ff.getName();

		DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootname);

		for (FurkTFile f : files) {
			String path = f.getPath() + f.getName();
			DefaultMutableTreeNode currentParent = root;
			String[] pathComponents = path.split("/");
			for (String comp : pathComponents) {
				DefaultMutableTreeNode child = findChild(currentParent, comp);
				if (child == null) {
					if (!comp.equals(pathComponents[pathComponents.length - 1])) {
						child = new DefaultMutableTreeNode(comp);
						currentParent.add(child);
						currentParent = child;
						break;
					}
				} else
					currentParent = child;
			}
			if (!(currentParent instanceof FurkTreeNode)) {
				currentParent.add(new TFileTreeNode(f));
			}
		}
		
		return root;
	}

	public void init() {

		setLayout(new BorderLayout());

		model = new DefaultTreeModel(root);
		jtree = new JTree(model);

		jtree.setCellRenderer(new DefaultTreeCellRenderer() {
			private static final long serialVersionUID = 1L;

			private Toolkit tk = Toolkit.getDefaultToolkit();

			private ImageIcon folderY = new ImageIcon(tk
					.getImage(FurkManager.class
							.getResource("img/tree/folder-yellow-16.png")));

			private ImageIcon folderB = new ImageIcon(tk
					.getImage(FurkManager.class
							.getResource("img/tree/folder-blue-16.png")));

			private ImageIcon audio = new ImageIcon(tk
					.getImage(FurkManager.class
							.getResource("img/tree/audio-16.png")));

			private ImageIcon video = new ImageIcon(tk
					.getImage(FurkManager.class
							.getResource("img/tree/video-16.png")));

			private ImageIcon image = new ImageIcon(tk
					.getImage(FurkManager.class
							.getResource("img/tree/image-16.png")));

			private ImageIcon text = new ImageIcon(tk
					.getImage(FurkManager.class
							.getResource("img/tree/text-16.png")));

			private ImageIcon pdf = new ImageIcon(tk.
					getImage(FurkManager.class
							.getResource("img/tree/pdf-16.png")));

			private ImageIcon def = new ImageIcon(tk.
					getImage(FurkManager.class
							.getResource("img/fr-16.png")));

			private ImageIcon loading = new ImageIcon(tk
					.getImage(FurkManager.class
							.getResource("img/tree/loading-16.gif")));

			@Override
			public Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean selected, boolean expanded,
					boolean isLeaf, int row, boolean focused) {

				Component c = super.getTreeCellRendererComponent(tree, value,
						selected, expanded, isLeaf, row, focused);

				if (value instanceof TFileTreeNode) {
					TFileTreeNode val = (TFileTreeNode) value;
					if (val.isBusy()) {
						setIcon(loading);
						def.setImageObserver(new NodeImageObserver(tree, val));
					} else {
						FurkTFile curr = val.getUserObject();
						String type = curr.getContentType();
						if (type.contains("audio")) {
							setIcon(audio);
						} else if (type.contains("video")) {
							setIcon(video);
						} else if (type.contains("image")) {
							setIcon(image);
						} else if (type.contains("msword")
								|| type.contains("text")) {
							setIcon(text);
						} else if (type.contains("pdf")) {
							setIcon(pdf);
						} else
							setIcon(def);
					}
				} else if (value instanceof DefaultMutableTreeNode) {
					if (selected)
						setIcon(folderY);
					else
						setIcon(folderB);
				}
				return c;
			}
		});

		int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;
		int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;

		JScrollPane jsp = new JScrollPane(jtree, v, h);
		add(jsp, BorderLayout.CENTER);

		jtree.addMouseListener(ml);
	}

	private static DefaultMutableTreeNode findChild(
			DefaultMutableTreeNode parent, String s) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent
					.getChildAt(i);
			if (s.equals(child.getUserObject()))
				return child;
		}
		return null;
	}

	MouseListener ml = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			int row = jtree.getRowForLocation(e.getX(), e.getY());
			if (row == -1) {
				jtree.clearSelection();
				return;
			}
			TreePath tp = jtree.getPathForLocation(e.getX(), e.getY());

			jtree.setSelectionPath(tp);
			Object tpp = tp.getPathComponent(tp.getPathCount() - 1);
			if (!(tpp instanceof FurkTreeNode))
				return;
			FurkTreeNode src = (FurkTreeNode) tpp;
			FurkTFile f = (FurkTFile) src.getUserObject();
			String s = "[size=" + f.getSizeString() + ", type="
					+ f.getContentType().split("/")[0] + "]";
			((Statable) getTopLevelAncestor()).setStatus(s);
			if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2) {
				src.action();
			} else if (e.getButton() == MouseEvent.BUTTON3
					&& e.getClickCount() == 1) {
				src.popupMenu().show(e.getComponent(), e.getX(), e.getY());
			}
		}
	};

}