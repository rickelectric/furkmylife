package org.rickelectric.furkmanager.experimentation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.rickelectric.furkmanager.FurkManager;

public class TreeTest2 {

	public static void main(String[] args) {
		new TreeTest2();
	}

	public TreeTest2() {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {

				JFrame frame = new JFrame("Testing");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setLayout(new BorderLayout());
				frame.add(new TestPane());
				frame.pack();
				frame.setLocationRelativeTo(null);
				frame.setVisible(true);
			}
		});
	}

	public class TestPane extends JPanel {

		private static final long serialVersionUID = 1L;

		private DefaultTreeModel model;
		private JTree tree;

		public TestPane() {
			setLayout(new BorderLayout());

			tree = new JTree();
			File rootFile = new File(
					"C:\\Users\\Ionicle\\Music\\Amberian Dawn\\");
			final DefaultMutableTreeNode root = new DefaultMutableTreeNode(rootFile);
			model = new DefaultTreeModel(root);

			tree.setModel(model);
			tree.setRootVisible(true);
			tree.setShowsRootHandles(true);
			
			tree.setCellRenderer(new DefaultTreeCellRenderer() {
				private static final long serialVersionUID = 1L;
				
				private Toolkit tk=Toolkit.getDefaultToolkit();
				
				private ImageIcon pdf = new ImageIcon(tk.getImage(FurkManager.class
						.getResource("img/tree/pdf-16.png")));
				
				@Override
				public Component getTreeCellRendererComponent(JTree tree,
						Object value, boolean selected, boolean expanded,
						boolean isLeaf, int row, boolean focused) {

					Component c = super.getTreeCellRendererComponent(tree, value,
							selected, expanded, isLeaf, row, focused);
					
					ImageIcon def = new ImageIcon(tk.getImage(FurkManager.class
							.getResource("img/tree/loading-16.gif")));
					
					setIcon(def);
					def.setImageObserver(new NodeImageObserver(tree, (DefaultMutableTreeNode)value));
					return c;
				}
			});

			add(new JScrollPane(tree));

			JButton load = new JButton("Load");
			add(load, BorderLayout.SOUTH);

			load.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

					DefaultMutableTreeNode root = (DefaultMutableTreeNode) model
							.getRoot();
					root.removeAllChildren();
					model.reload();
					File rootFile = (File) root.getUserObject();

					addFiles(rootFile, model, root);

					tree.expandPath(new TreePath(root));

				}
			});

		}

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(400, 500);
		}

		protected void addFiles(File rootFile, DefaultTreeModel model,
				DefaultMutableTreeNode root) {

			for (File file : rootFile.listFiles()) {
				DefaultMutableTreeNode child = new DefaultMutableTreeNode(file);
				model.insertNodeInto(child, root, root.getChildCount());
				if (file.isDirectory()) {
					addFiles(file, model, child);
				}
			}

		}
	}
}