package rickelectric.furkmanager.network;

import java.util.ArrayList;

import javax.swing.tree.DefaultMutableTreeNode;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.APIFolder;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkLabel;
import rickelectric.furkmanager.models.MoveableItem;
import rickelectric.furkmanager.views.icons.FileTreeNode;
import rickelectric.furkmanager.views.icons.FolderTreeNode;

public class APIFolderManager {

	private static APIFolder root;
	private static ArrayList<MoveableItem> register;

	public static boolean isRegistered(MoveableItem i) {
		for (MoveableItem c : register) {
			if (i.getID().equals(c.getID())) {
				return true;
			}
		}
		return false;
	}

	public static boolean register(MoveableItem i) {
		if (isRegistered(i))
			return false;
		return register.add(i);
	}

	public static void init(FurkLabel rootLabel) {
		register = new ArrayList<MoveableItem>();
		root = new APIFolder(rootLabel);
		register(root);
		root.populate();
	}

	public static boolean newFolder(APIFolder parent, String name){
		if(name==null||name.length()==0) return false;
		if (isRegistered(parent)) {
			FurkLabel nf = new FurkLabel(null, name);
			if (API.Label.add(nf)){
				APIFolder child = new APIFolder(nf);
				register(child);
				parent.addItem(child);
				return true;
			}
			// Add Failed
		}
		FurkManager.trayAlert(FurkManager.TRAY_ERROR, "Error", "Unable To Create Folder \""+name+"\"", null);
		return false;// Invalid Parent
	}

	public static boolean rename(APIFolder folder, String name) {
		if (isRegistered(folder)){
			FurkLabel nf = folder.getLabel();
			String old=nf.getName();
			nf.setName(name);
			if (API.Label.update(nf)) {
				return true;
			}
			// Rename Failed
			nf.setName(old);
		}
		return false;
	}

	public static boolean moveItem(MoveableItem obj, APIFolder dest) {
		try {
			System.out.println("Moving " + obj.getName() + "\nTo "
					+ dest.getName());
			if (isRegistered(dest) && isRegistered(obj)) {
				APIFolder src = null;
				if (root.contains(obj))
					src = root;
				else {
					for (MoveableItem i : register) {
						if (i instanceof APIFolder) {
							System.out.println("API Folder!!!");
							if (((APIFolder) i).contains(obj)) {
								System.out.println("Contains!!!");
								src = (APIFolder) i;
								break;
							}
						}
					}
				}
				if (src != null) {
					if (dest.equals(src))
						return true;
					System.out.println("From " + src.getName());
					src.removeItem(obj);
				}
				dest.addItem(obj);
				if (obj instanceof APIFolder) {
					System.out.println("Updating Folder Label Details...");
					FurkLabel l = ((APIFolder) obj).getLabel();
					l.setParentID(dest.getID());
					API.Label.update(l);
				} else if (obj instanceof FurkFile) {
					String[] labels = ((FurkFile) obj).getIdLabels();
					String id = obj.getID();
					if (labels != null && labels.length != 0) {// Already In A
																// Folder
						API.Label.unlinkFromFiles(src.getID(),
								new String[] { id });
					}
					if (!dest.equals(root)) {// New Folder Is Not Root (null)
												// Folder
						API.Label
								.linkToFiles(dest.getID(), new String[] { id });
					}
				}
				return true;
			} else {
				System.out.println("Not Registered");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public static DefaultMutableTreeNode generateTree() {
		if (root == null)
			return null;
		FolderTreeNode head = popTree(root);
		return head;
	}

	public static FolderTreeNode popTree(APIFolder folder) {
		FolderTreeNode col = new FolderTreeNode(folder);

		ArrayList<MoveableItem> list = folder.getFiles();
		for (MoveableItem m : list) {
			if (m instanceof APIFolder) {
				FolderTreeNode ftr = popTree((APIFolder) m);
				col.add(ftr);
			} else if (m instanceof FurkFile) {
				FileTreeNode ftr = new FileTreeNode((FurkFile) m);
				col.add(ftr);
			}
		}
		return col;
	}

}
