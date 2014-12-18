package rickelectric.furkmanager.network.api;

import javax.swing.tree.DefaultTreeModel;

import rickelectric.furkmanager.views.icons.FolderTreeNode;
import rickelectric.furkmanager.views.icons.FurkTreeNode;

public class API_Folder {

	private static API_Folder folder;

	public static synchronized API_Folder getInstance() {
		if (folder == null)
			folder = new API_Folder();
		return folder;
	}
	
	private DefaultTreeModel model;

	private API_Folder() {
		
	}
	
	public boolean move(FurkTreeNode item,FolderTreeNode srcFolder,FolderTreeNode destFolder){
		return false;
	}

	public DefaultTreeModel getModel() {
		return model;
	}
	
	

}
