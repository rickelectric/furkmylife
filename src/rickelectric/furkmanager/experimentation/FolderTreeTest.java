package org.rickelectric.furkmanager.experimentation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.DropMode;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.rickelectric.furkmanager.FurkManager;
import org.rickelectric.furkmanager.models.APIObject;
import org.rickelectric.furkmanager.models.FurkFile;
import org.rickelectric.furkmanager.models.FurkLabel;
import org.rickelectric.furkmanager.models.FurkTFile;
import org.rickelectric.furkmanager.models.NodeImageObserver;
import org.rickelectric.furkmanager.network.API;
import org.rickelectric.furkmanager.utils.SettingsManager;
import org.rickelectric.furkmanager.views.icons.FileTreeNode;
import org.rickelectric.furkmanager.views.icons.FolderTreeNode;
import org.rickelectric.furkmanager.views.icons.FurkTreeNode;
import org.rickelectric.furkmanager.views.icons.TFileTreeNode;
import org.rickelectric.furkmanager.views.windows.AppFrameClass;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class FolderTreeTest extends AppFrameClass {
	private static final long serialVersionUID = 1L;
	
	private FolderView contentPane;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e1) {
			try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (ClassNotFoundException | InstantiationException
					| IllegalAccessException | UnsupportedLookAndFeelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		SettingsManager.init();
		API.init("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		API.File.getAllFinished();
		API.Label.getAll();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					FolderTreeTest frame = new FolderTreeTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public FolderTreeTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 571, 442);
		contentPane = new FolderView();
		contentPane.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		contentPane.setLayout(new BorderLayout(0, 0));
		super.setContentPane(contentPane);
	}

}

class FolderView extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTree label_tree;
	private static FolderTreeNode root;
	private JLabel loading;
	
	public FolderView() {
		setBorder(new EmptyBorder(5, 5, 5, 5));

		setLayout(new BorderLayout());
		
		loading = new JLabel();
		loading.setHorizontalAlignment(SwingConstants.CENTER);
		loading.setIcon(new ImageIcon(FolderTreeTest.class.getResource("/rickelectric/furkmanager/img/ajax-loader.gif")));
		add(loading,BorderLayout.CENTER);
		
		repaint();

		label_tree = new JTree();
		Thread t = new Thread(new Runnable() {
			private JScrollPane scroller;

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
				label_tree.setDragEnabled(true);
				label_tree.setDropMode(DropMode.ON_OR_INSERT);

				label_tree.setTransferHandler(new FurkTransferHandler());

				label_tree.setCellRenderer(new DefaultTreeCellRenderer() {
					private static final long serialVersionUID = 1L;

					private Toolkit tk = Toolkit.getDefaultToolkit();

					private ImageIcon folderY = new ImageIcon(
							tk.getImage(FurkManager.class
									.getResource("img/tree/folder-yellow-16.png")));

					private ImageIcon folderB = new ImageIcon(
							tk.getImage(FurkManager.class
									.getResource("img/tree/folder-blue-16.png")));

					private ImageIcon audio = new ImageIcon(tk
							.getImage(FurkManager.class
									.getResource("img/tree/audio-16.png")));

					private ImageIcon video = new ImageIcon(tk
							.getImage(FurkManager.class
									.getResource("img/tree/video-16.png")));

					private ImageIcon loading = new ImageIcon(tk
							.getImage(FurkManager.class
									.getResource("img/tree/loading-16.gif")));

					private Icon image = new ImageIcon(tk
							.getImage(FurkManager.class
									.getResource("img/tree/image-16.png")));

					private Icon text = new ImageIcon(tk
							.getImage(FurkManager.class
									.getResource("img/tree/text-16.png")));

					private Icon pdf = new ImageIcon(tk
							.getImage(FurkManager.class
									.getResource("img/tree/pdf-16.png")));

					private ImageIcon def = new ImageIcon(tk
							.getImage(FurkManager.class
									.getResource("img/fr-16.png")));

					@Override
					public Component getTreeCellRendererComponent(JTree tree,
							Object value, boolean selected, boolean expanded,
							boolean isLeaf, int row, boolean focused) {
						Component c = super
								.getTreeCellRendererComponent(tree, value,
										selected, expanded, isLeaf, row,
										focused);
						if (value instanceof FolderTreeNode) {
							if (selected)
								setIcon(folderY);
							else
								setIcon(folderB);
						} else if (value instanceof FileTreeNode) {
							FileTreeNode val = (FileTreeNode) value;
							if (val.isBusy()) {
								setIcon(loading);
								loading.setImageObserver(new NodeImageObserver(
										tree, val));
							} else {
								FurkFile curr = val.getUserObject();
								String type = curr.getType();
								if (type.equals("audio"))
									setIcon(audio);
								else if (type.equals("video"))
									setIcon(video);
								else
									setIcon(def);
							}
						} else if (value instanceof TFileTreeNode) {
							TFileTreeNode val = (TFileTreeNode) value;
							FurkTFile curr = val.getUserObject();
							String type = curr.getContentType();
							if (val.isBusy()) {
								setIcon(loading);
								loading.setImageObserver(new NodeImageObserver(
										tree, val));
							} else {
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
						}
						return c;
					}
				});

				label_tree.putClientProperty("JTree.lineStyle", "Angled");
				label_tree.setFont(new Font("Segoe UI", Font.PLAIN, 13));
				label_tree.getSelectionModel().setSelectionMode(
						TreeSelectionModel.SINGLE_TREE_SELECTION);

				scroller = new JScrollPane(label_tree);
				scroller.setViewportView(label_tree);
				//scroller.setBounds(5, 5, getWidth() - 10, getHeight() - 10);
				scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
				add(scroller,BorderLayout.CENTER);
				
				repaint();
			}
		});
		t.start();

	}

	private static DefaultTreeModel getTreeModel() {
		
		
		
		root = null;
		FurkLabel orph = new FurkLabel("0", "Other Files");
		FolderTreeNode orp = new FolderTreeNode(orph);

		ArrayList<FurkLabel> flist = API.Label.getAll();
		if (flist == null)
			return null;
		ArrayList<FurkLabel> folders = new ArrayList<FurkLabel>();
		for (FurkLabel x : flist) {
			folders.add(x);
		}
		Iterator<FurkLabel> iter = folders.iterator();
		while (iter.hasNext()) {
			FurkLabel f = iter.next();
			if (f.getName().equals("0-FurkManagerRoot")) {
				f.setName("My Files");
				root = new FolderTreeNode(f);
				iter.remove();
				break;
			}
		}
		if (root == null)
			return null;
		List<APIObject> files = null;
		do {
			files = API.File.getAllCached();
		} while (files == null);
		connectTreeFileFolders(root, folders, files);
		connectTreeFileFolders(orp, folders, files);
		root.add(orp);
		return new DefaultTreeModel(root);
	}

	public static void connectTreeFileFolders(FolderTreeNode parent,
			List<FurkLabel> folders, List<APIObject> files) {
		if (folders != null)
			for (FurkLabel f : folders) {
				if (f.getParentID().equals(parent.getUserObject().getID())) {
					FolderTreeNode fn = new FolderTreeNode(f);
					connectTreeFileFolders(fn, folders, files);
					parent.add(fn);
				}
			}
		if (files != null) {
			for (APIObject a : files) {
				FurkFile f = (FurkFile) a;
				String[] pfs = f.getIdLabels();
				if (pfs == null || pfs.length == 0)
					pfs = new String[] { "0" };
				for (String s : pfs) {
					if (s.equals(parent.getUserObject().getID())) {
						FileTreeNode fn = new FileTreeNode(f);
						parent.add(fn);
						break;
					}
				}
			}
		}
	}

	public void refreshMyFolders(boolean hardReload) {

	}

}
