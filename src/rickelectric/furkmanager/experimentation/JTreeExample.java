package org.rickelectric.furkmanager.experimentation;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rickelectric.furkmanager.FurkManager;
import org.rickelectric.furkmanager.models.APIObject;
import org.rickelectric.furkmanager.models.FurkFile;
import org.rickelectric.furkmanager.models.FurkTFile;
import org.rickelectric.furkmanager.network.API;
import org.rickelectric.furkmanager.network.StreamDownloader;
import org.rickelectric.furkmanager.utils.SettingsManager;
import org.rickelectric.furkmanager.views.icons.TFileTreeNode;
import org.rickelectric.furkmanager.views.panels.TFileTreePanel;

public class JTreeExample extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTree tree;
	private DefaultTreeModel treeModel;

	public static void main(String[] args) {
		SettingsManager.init();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				createAndShowGUI();
			}
		});
	}

	private static void createAndShowGUI() {
		JFrame frame = new JFrame("My Warehouse");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JTreeExample newContentPane = new JTreeExample();
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);

		frame.pack();
		frame.setVisible(true);
		frame.setAlwaysOnTop(true);
	}

	public JTreeExample() {
		setLayout(new GridLayout(1, 3));
		JLabel lbl_parts = new JLabel("PARTS TO BE SHIPPED");
		tree = new JTree(getTreeModel());
		tree.setDragEnabled(true);
		tree.setPreferredSize(new Dimension(200, 400));
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(tree);
		treeModel = getTreeModel();

		JPanel topPanel = new JPanel(new BorderLayout());
		topPanel.add(lbl_parts, BorderLayout.NORTH);
		topPanel.add(scroll, BorderLayout.CENTER);

		add(topPanel);

	}
	
	private static DefaultTreeModel getTreeModel(){
		API.init("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		String json = null;
		try {
			json = StreamDownloader.fileToString(
				"JSON_Samples_And_Docs/JSON-FurkTFilesVideo.txt");
		}catch(IOException e){return null;}
		
		JSONObject o=new JSONObject(json);
		JSONArray ja=o.getJSONArray("files");
		
		List<APIObject> tfs=API.File.jsonFiles(ja);
		DefaultMutableTreeNode root=TFileTreePanel.getTFileTree((FurkFile)tfs.get(0));
		
		return new DefaultTreeModel(root);
	}
	
	
}