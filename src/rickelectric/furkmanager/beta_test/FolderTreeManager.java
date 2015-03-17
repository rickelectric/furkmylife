package rickelectric.furkmanager.beta_test;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import rickelectric.UtilBox;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.beta_test.draggables.models.AbstractDescriptor;
import rickelectric.furkmanager.beta_test.draggables.models.FileDescriptor;
import rickelectric.furkmanager.beta_test.draggables.models.FolderDescriptor;
import rickelectric.furkmanager.exception.LoginException;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkLabel;
import rickelectric.furkmanager.models.LoginModel;
import rickelectric.furkmanager.network.api_new.FurkAPI;

public class FolderTreeManager {

	private FolderDescriptor root;

	public static void main(String... args) {
		FurkManager.init();
		LoginModel lm = new LoginModel(
				"5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		try {
			boolean logged = FurkAPI.getInstance().login(lm);
			System.out.println("Logged In: " + logged);
			if (logged) {
				FolderTreeManager f = new FolderTreeManager();
				f.load();
				System.out.println(f.toString());
			}
		} catch (LoginException e) {
			e.printStackTrace();
		}

	}

	public FolderTreeManager() {
		// load();
	}

	public boolean delete(FolderDescriptor folder) {
		boolean del = FurkAPI.getInstance().label().delete(folder.getId());
		if (del) {
			folder.getParent().removeChild(folder);
			return true;
		}
		return false;
	}

	public boolean newFolder(FolderDescriptor parent, String name) {
		FurkLabel l = FurkAPI.getInstance().label()
				.insert(name, parent.getId(), 0, "", "");
		if (l == null)
			return false;
		FolderDescriptor child = new FolderDescriptor(parent, l);
		parent.addChild(child);
		return true;
	}

	public boolean rename(FolderDescriptor folder, String newName) {
		if (folder == null || folder.equals(root)) {
			throw new IllegalArgumentException("Cannot Rename Root Folder");
		}
		if (newName == null
				|| UtilBox.getInstance().alphanum(newName).equals("")) {
			throw new IllegalArgumentException("Invalid Name");
		}
		FurkLabel l = folder.getLabel();
		boolean updated = FurkAPI
				.getInstance()
				.label()
				.update(l.getID(), newName, l.getParentID(), l.getSortOrder(),
						l.getColor(), l.getBackground());
		if (updated)
			l.setName(newName);
		return updated;
	}

	public boolean move(AbstractDescriptor item, FolderDescriptor dest) {
		if (item instanceof FileDescriptor) {
			boolean moved = FurkAPI
					.getInstance()
					.label()
					.link(dest.getId(),
							new String[] { ((FileDescriptor) item).getId() });
			if (moved) {
				dest.getChildren().add(item);
				item.getParent().removeChild(item);
				((FileDescriptor) item).getFileObject().setParentID(
						dest.getId());
			}
		} else if (item instanceof FolderDescriptor) {
			FurkLabel l = ((FolderDescriptor) item).getLabel();
			boolean moved = FurkAPI
					.getInstance()
					.label()
					.update(l.getID(), l.getName(), dest.getId(),
							l.getSortOrder(), l.getColor(), l.getBackground());
			if (moved) {
				dest.getChildren().add(item);
				item.getParent().removeChild(item);
				l.setParentID(dest.getId());
			}
		}
		return false;
	}

	public String toString() {
		OutputStream os = new ByteArrayOutputStream();
		PrintWriter out = new PrintWriter(os);
		traverse(out, root, 0);
		out.flush();
		out.close();
		return os.toString();
	}

	public void traverse(PrintWriter out, FolderDescriptor f, int level) {
		String indent = "";
		for (int i = 0; i < level; i++) {
			indent += "    ";
		}
		out.println(indent + "+" + f.getName());
		for (AbstractDescriptor o : f.getChildren()) {
			if (o instanceof FolderDescriptor) {
				traverse(out, (FolderDescriptor) o, level + 1);
			} else if (o instanceof FileDescriptor) {
				out.println(indent + "|-->" + o.getName());
			}
		}
	}

	public void load() {
		ArrayList<FurkLabel> labels = FurkAPI.getInstance().label().get();
		ArrayList<FurkFile> files = FurkAPI.getInstance().file()
				.getAllFinished();
		for (FurkLabel l : labels) {
			if (l.getName().equals("0-FurkManagerRoot")) {
				root = new FolderDescriptor(null, l);
				break;
			}
		}
		labels.remove(root.getLabel());
		populateFolders(root, labels);
		if (!labels.isEmpty()) {// Orphaned Folders
			ArrayList<FolderDescriptor> fd = new ArrayList<FolderDescriptor>();
			for (int i = 0; i < labels.size(); i++) {
				FurkLabel l = labels.remove(0);
				FolderDescriptor f = null;
				for (FolderDescriptor tf : fd) {
					if (tf.getLabel().equals(l)) {
						f = tf;
						break;
					}
				}
				if (f == null)
					f = new FolderDescriptor(root, l);
				f.setOrphaned(true);
				populateFolders(f, labels);
				labels.add(l);
				fd.add(f);
			}
			if (labels.size() != fd.size()) {
				Iterator<FolderDescriptor> fi = fd.iterator();
				while (fi.hasNext()) {
					FolderDescriptor f = fi.next();
					for (FurkLabel l : labels) {
						if (f.getLabel().equals(l))
							f.setOrphaned(false);
					}
					if (f.isOrphaned())
						fi.remove();
				}
				for(FolderDescriptor f:fd){
					f.setOrphaned(true);
					root.addChild(f);
				}
			}
			fd=null;
		}
		populateFiles(root, files);
	}

	private void populateFolders(FolderDescriptor folder,
			ArrayList<FurkLabel> childFolders) {
		Iterator<FurkLabel> i = childFolders.iterator();
		while (i.hasNext()) {
			FurkLabel n = i.next();
			if ((folder == root && n.getParentID().equals("0"))
					|| n.getParentID().equals(folder.getId())) {
				folder.addChild(new FolderDescriptor(folder, n));
				i.remove();
			}
		}
		for (AbstractDescriptor o : folder.getChildren()) {
			if (o instanceof FolderDescriptor) {
				populateFolders((FolderDescriptor) o, childFolders);
			}
		}
	}

	private void populateFiles(FolderDescriptor folder,
			ArrayList<FurkFile> childFiles) {
		Iterator<FurkFile> i = childFiles.iterator();
		while (i.hasNext()) {
			FurkFile n = i.next();
			if ((folder == root && n.getParentID().equals("0"))
					|| n.getParentID().equals(folder.getId())) {
				folder.addChild(new FileDescriptor(folder, n));
				i.remove();
			}
		}
		for (AbstractDescriptor o : folder.getChildren()) {
			if (o instanceof FolderDescriptor) {
				populateFiles((FolderDescriptor) o, childFiles);
			}
		}
	}

	public FolderDescriptor getRoot() {
		return root;
	}

}