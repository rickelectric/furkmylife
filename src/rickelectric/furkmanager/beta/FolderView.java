package rickelectric.furkmanager.views.panels;

import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.DropMode;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import rickelectric.furkmanager.network.APIFolderManager;
import rickelectric.furkmanager.views.icons.FileTreeNode;
import rickelectric.furkmanager.views.icons.FolderTreeRenderer;
import rickelectric.furkmanager.views.icons.FolderTreeTransferHandler;
import rickelectric.furkmanager.views.icons.FurkTreeNode;
import rickelectric.furkmanager.views.windows.AppFrameClass;

public class FolderView extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTree label_tree;

	public FolderView() {
		setBorder(new EmptyBorder(5, 5, 5, 5));

		label_tree = new JTree();
		//Something about this thread causes it to stop working when removed
		//TODO Eliminate This Thread
		Thread t = new Thread(new Runnable() {
			public void run() {
				label_tree.setModel(getTreeModel());

				MouseListener ml = new MouseAdapter() {
					public void mouseReleased(MouseEvent e) {
						int row = label_tree.getRowForLocation(e.getX(),
								e.getY());
						if (row == -1) {
							label_tree.clearSelection();
							return;
						}

						TreePath tp = label_tree.getPathForLocation(e.getX(),
								e.getY());
						String pathString = tp.toString();
						String ps = pathString.substring(1,
								pathString.length() - 1);

						String[] sp = ps.split(", ");
						ps = "Furk:/";
						for (String s : sp) {
							ps += "/" + s;
						}
						((AppFrameClass) getTopLevelAncestor()).setStatus(ps);

						label_tree.setSelectionPath(tp);

						DefaultMutableTreeNode tpp = (DefaultMutableTreeNode) tp
								.getPathComponent(tp.getPathCount() - 1);
						if (!(tpp instanceof FurkTreeNode))
							return;

						FurkTreeNode src = (FurkTreeNode) tpp;

						if (e.getButton() == MouseEvent.BUTTON1
								&& e.getClickCount() == 2) {
							if (src instanceof FileTreeNode) {
								((FileTreeNode) src).action(new Runnable() {
									public void run() {
										label_tree.repaint();
									}
								});
							} else
								src.action();
						} else if (e.getButton() == MouseEvent.BUTTON3
								&& e.getClickCount() == 1) {
							src.popupMenu().show(e.getComponent(), e.getX(),
									e.getY());
						}
					}
				};
				label_tree.addMouseListener(ml);
				label_tree.addKeyListener(new KeyAdapter(){
					public void keyReleased(KeyEvent e){
						if(e.getKeyCode()==KeyEvent.VK_F5){
							label_tree.setModel(getTreeModel());
							repaint();
							
						}
					}
				});
				label_tree.setDragEnabled(true);
		        label_tree.setDropMode(DropMode.ON_OR_INSERT);
				
				label_tree.setTransferHandler(new FolderTreeTransferHandler());

				label_tree.setCellRenderer(new FolderTreeRenderer());
				
				label_tree.putClientProperty("JTree.lineStyle", "Angled");
				label_tree.setFont(new Font("Segoe UI", Font.PLAIN, 13));
				label_tree.getSelectionModel().setSelectionMode(
						TreeSelectionModel.SINGLE_TREE_SELECTION);

				JScrollPane scroller = new JScrollPane(label_tree);
				scroller.setViewportView(label_tree);
				scroller.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
				scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				add(scroller);
			}
		});
		t.start();

	}

	private static DefaultTreeModel getTreeModel() {
		DefaultMutableTreeNode root=APIFolderManager.generateTree();
		return new DefaultTreeModel(root);
	}

	public void refreshMyFolders(boolean hardReload) {

	}

}
