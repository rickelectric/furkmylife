package rickelectric.furkmanager.views.panels;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkTFile;
import rickelectric.furkmanager.network.api.API_TFile;
import rickelectric.furkmanager.views.Statable;
import rickelectric.furkmanager.views.icons.FurkTreeNode;
import rickelectric.furkmanager.views.icons.TFileTreeNode;
import rickelectric.furkmanager.views.iconutil.FolderTreeRenderer;

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

		jtree.setCellRenderer(new FolderTreeRenderer());

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