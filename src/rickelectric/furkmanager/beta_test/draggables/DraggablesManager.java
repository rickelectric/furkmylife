package rickelectric.furkmanager.beta_test.draggables;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import rickelectric.furkmanager.beta_test.FolderTreeManager;
import rickelectric.furkmanager.beta_test.draggables.models.AbstractDescriptor;
import rickelectric.furkmanager.beta_test.draggables.models.FileDescriptor;
import rickelectric.furkmanager.beta_test.draggables.models.FileItem;
import rickelectric.furkmanager.beta_test.draggables.models.FolderDescriptor;
import rickelectric.furkmanager.beta_test.draggables.models.FolderItem;
import rickelectric.furkmanager.beta_test.draggables.models.Item;

public class DraggablesManager {

	private FolderTreeManager tree;
	private FolderDescriptor currentFolder;
	private HashMap<String, Point> itemLocations;

	public DraggablesManager() {
		tree = new FolderTreeManager();
		read();
		currentFolder = null;
	}

	public void setCurrentFolder(FolderDescriptor folder) {
		this.currentFolder = folder;
	}

	public FolderDescriptor getCurrentFolder() {
		return currentFolder;
	}

	public ArrayList<Item> getItemsInCurrentFolder() {
		ArrayList<Item> items = new ArrayList<Item>();
		ArrayList<AbstractDescriptor> children = currentFolder.getChildren();
		for (AbstractDescriptor child : children) {
			Item i = generateItemFromDesc(child);
			items.add(i);
		}
		return items;
	}

	private Item generateItemFromDesc(AbstractDescriptor child) {
		if (child instanceof FolderDescriptor) {
			FolderItem fi = new FolderItem((FolderDescriptor) child);
			Point itemLoc = itemLocations.get(((FolderDescriptor) child)
					.getId());
			if (itemLoc == null) {
				itemLoc = new Point(30, 30);
				itemLocations.put(((FolderDescriptor) child).getId(), itemLoc);
			}
			fi.setLocation(itemLoc);
			return fi;
		}
		if (child instanceof FileDescriptor) {
			FileItem fi = new FileItem((FileDescriptor) child);
			Point itemLoc = itemLocations.get(((FileDescriptor) child).getId());
			if (itemLoc != null)
				fi.setLocation(itemLoc);
			return fi;
		}
		return null;
	}

	public void loadItems() {
		tree.load();
		currentFolder = tree.getRoot();
	}

	public void updateItemLocation(Item str) {
		// Save New Item Coordinates
		if (str instanceof FileItem) {
			itemLocations.put(((FileItem) str).getDescriptor().getId(),
					str.getLocation());
		}
		if (str instanceof FolderItem) {
			itemLocations.put(((FolderItem) str).getDescriptor().getId(),
					str.getLocation());
		}
	}

	@SuppressWarnings("unchecked")
	private void read() {
		try {
			File file = new File("settings/app.furkmanager.draggables.db");
			if (!file.exists())
				file.createNewFile();
			FileInputStream fos = new FileInputStream(file);
			ObjectInputStream oos = new ObjectInputStream(fos);
			itemLocations = (HashMap<String, Point>) oos.readObject();
			oos.close();
			return;
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		itemLocations = new HashMap<String, Point>();
	}

	public void save() {
		try {
			FileOutputStream fos = new FileOutputStream(new File(
					"settings/app.furkmanager.draggables.db"));
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(itemLocations);
			oos.flush();
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public FolderTreeManager getTree() {
		return this.tree;
	}

}
