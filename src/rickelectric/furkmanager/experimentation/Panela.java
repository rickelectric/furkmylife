package org.rickelectric.furkmanager.experimentation;

import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.json.JSONObject;
import org.rickelectric.furkmanager.models.FurkTFile;
import org.rickelectric.furkmanager.network.API;
import org.rickelectric.furkmanager.network.StreamDownloader;
import org.rickelectric.furkmanager.views.icons.TFileTreeNode;
import javax.swing.JScrollPane;

public class Panela extends JPanel {
	private static final long serialVersionUID = 1L;
	private JTree tree;

	/**
	 * @wbp.parser
	 */
	public static void main(String[] args) {
		JFrame f = new JFrame();
		f.setContentPane(new Panela());
		f.setSize(new Dimension(360, 400));
		f.setVisible(true);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Panela() {
		setLayout(null);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 11, 323, 278);
		add(scrollPane);

		tree = new JTree();
		scrollPane.setViewportView(tree);
		load();

	}

	void load() {
		try {
			String json = StreamDownloader
					.fileToString("./JSON_Samples_And_Docs/JSON-FurkTFilesVideo.txt");
			JSONObject j = new JSONObject(json);
			ArrayList<FurkTFile> files = API.TFile.jsonFiles(j
					.getJSONArray("files").getJSONObject(0)
					.getJSONArray("t_files"));
			DefaultMutableTreeNode root = new DefaultMutableTreeNode(
					"Sliders full");

			ArrayList<String> names = new ArrayList<String>();
			ArrayList<Integer> levels = new ArrayList<Integer>();

			for (int i = 0; i < files.size(); i++) {
				String path = files.get(i).getPath();
				if (path.equals("")) {
					levels.add(0);
					names.add("");
				} else {
					String[] p = path.split("/");
					levels.add(p.length);
					names.add(p[p.length - 1]);
				}
			}

			populate(root, files, levels, names, 0);
			tree.setModel(new DefaultTreeModel(root));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	void populate(DefaultMutableTreeNode parent, ArrayList<FurkTFile> flist,
			ArrayList<Integer> levels, ArrayList<String> names, int l) {

		System.out.println(levels);
		System.out.println(names);
		for (int i = 0; i < flist.size(); i++) {
			if (levels.get(i) < l)
				break;
			if (levels.get(i) > l) {
				DefaultMutableTreeNode curr = new DefaultMutableTreeNode(names.get(i));
				populate(curr,flist,levels,names,l+1);
				parent.add(curr);
			}
			else{
				parent.add(new TFileTreeNode(flist.get(i))); 
			}
		}
	}

}
