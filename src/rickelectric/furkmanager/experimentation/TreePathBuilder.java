package org.rickelectric.furkmanager.experimentation;

import javax.swing.tree.DefaultMutableTreeNode;

public class TreePathBuilder {

	private DefaultMutableTreeNode root;

	public TreePathBuilder(String root) {
		this.root = new DefaultMutableTreeNode(root);
	}

	public DefaultMutableTreeNode getRoot() {
		return root;
	}

	public void addFolder(String path,String name){
		if(path.equals("")) root.add(new DefaultMutableTreeNode(name));
		String[] p=path.split("/");
		
		DefaultMutableTreeNode c=root;
		for(int i=0;i<path.length();i++){
			c=findChild(c,p[i]);
			if(c==null) break;
		}
	}

	private DefaultMutableTreeNode findChild(DefaultMutableTreeNode parent,String s) {
		for (int i = 0; i < parent.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) parent
					.getChildAt(i);
			if (s.equals(child.getUserObject()))
				return child;
		}
		return null;
	}

}
