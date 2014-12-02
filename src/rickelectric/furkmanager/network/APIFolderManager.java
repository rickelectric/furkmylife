package rickelectric.furkmanager.network;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.tree.DefaultMutableTreeNode;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.APIFolder;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkLabel;
import rickelectric.furkmanager.models.MoveableItem;
import rickelectric.furkmanager.network.api.API_Label;
import rickelectric.furkmanager.views.icons.FileTreeNode;
import rickelectric.furkmanager.views.icons.FolderTreeNode;

public class APIFolderManager {

	private static APIFolder root;
	private static ArrayList<MoveableItem> register;

	public enum UpdateSource {
		INIT, NEW, RENAME, DELETE, MOVE
	}

	public static class FolderObservable extends Observable {
		public void stateChanged(UpdateSource source) {
			System.out.println("Folder Observer Updated: " + source);
			setChanged();
			notifyObservers(source);
		}
	}

	private static FolderObservable fo = new FolderObservable();

	public static void addObserver(Observer o) {
		fo.addObserver(o);
	}

	public static void deleteObserver(Observer o) {
		fo.deleteObserver(o);
	}

	public static void init(FurkLabel rootLabel) {
		if (root == null || !root.getLabel().equals(rootLabel)) {
			register = null;
			System.gc();
			register = new ArrayList<MoveableItem>();
			root = new APIFolder(rootLabel);
			register(root);
			root.populate();
			sort();
			fo.stateChanged(UpdateSource.INIT);
		}
	}

	public static void refresh() {
		if (API_Label.getAll() != null) {
			root = null;
			init(API_Label.root());
		}
	}

	public static boolean newFolder(APIFolder parent, String name) {
		if (name == null || name.length() == 0)
			return false;
		if (isRegistered(parent)) {
			FurkLabel nf = new FurkLabel(null, name);
			if ((nf = API_Label.add(nf)) != null) {
				APIFolder child = new APIFolder(nf);
				register(child);
				parent.addItem(child);
				sort();
				fo.stateChanged(UpdateSource.NEW);
				return true;
			}
			// Add Failed
		}
		// Invalid Parent
		FurkManager.trayAlert(FurkManager.TRAY_ERROR, "Error",
				"Unable To Create Folder \"" + name + "\"", null);
		return false;
	}

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
		boolean r = register.add(i);
		return r;
	}

	public static boolean rename(APIFolder folder, String name) {
		if (isRegistered(folder)) {
			FurkLabel nf = folder.getLabel();
			String old = nf.getName();
			nf.setName(name);
			if (API_Label.update(nf)) {
				sort();
				fo.stateChanged(UpdateSource.RENAME);
				return true;
			}
			// Rename Failed
			nf.setName(old);
		}
		return false;
	}

	private static void sort() {
		root.sort();
	}

	public static boolean delete(APIFolder folder) {
		if (isRegistered(folder)) {
			FurkLabel l = folder.getLabel();
			if (move(folder, null) && API_Label.delete(l)) {
				fo.stateChanged(UpdateSource.DELETE);
				return true;
			}
		}
		return false;
	}

	public static boolean move(MoveableItem obj, APIFolder dest) {
		try {
			if (isRegistered(obj)) {
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
					if (dest != null && dest.equals(src)) {
						return true;
					}
					System.out.println("From " + src.getName());
					src.removeItem(obj);
				}
				if (dest == null) {
					return true;
				}
				dest.addItem(obj);
				if (obj instanceof APIFolder) {
					System.out.println("Updating Folder Label Details...");
					FurkLabel l = ((APIFolder) obj).getLabel();
					l.setParentID(dest.getID());
					API_Label.update(l);
				} else if (obj instanceof FurkFile) {
					String[] labels = ((FurkFile) obj).getIdLabels();
					String id = obj.getID();
					if (labels != null && labels.length != 0) {// Already In A
																// Folder
						API_Label.unlinkFromFiles(src.getID(),
								new String[] { id });
					}
					if (!dest.equals(root)) {// New Folder Is Not Root (null)
												// Folder
						if (API_Label.linkToFiles(dest.getID(),
								new String[] { id })) {
							((FurkFile) obj).setIdLabels(new String[] { dest
									.getID() });
						}
					}
				}
				sort();
				fo.stateChanged(UpdateSource.MOVE);
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
		FolderTreeNode head = populateTree(root);
		return head;
	}

	public static FolderTreeNode populateTree(APIFolder folder) {
		FolderTreeNode col = new FolderTreeNode(folder);
		// col.setParent(File_FolderView.currTree());

		ArrayList<MoveableItem> list = folder.getFiles();
		for (MoveableItem m : list) {
			if (m instanceof APIFolder) {
				FolderTreeNode ftr = populateTree((APIFolder) m);
				col.add(ftr);
			} else if (m instanceof FurkFile) {
				FileTreeNode ftr = new FileTreeNode((FurkFile) m);
				// ftr.setParent(File_FolderView.currTree());
				col.add(ftr);
			}
		}
		return col;
	}

}
