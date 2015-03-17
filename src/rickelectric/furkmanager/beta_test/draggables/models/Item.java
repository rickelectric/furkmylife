package rickelectric.furkmanager.beta_test.draggables.models;


import java.awt.Color;
import java.awt.Image;
import java.awt.Rectangle;

public abstract class Item extends Rectangle implements Comparable<Item> {
	private static final long serialVersionUID = 1L;

	private static int idg=1000;
	
	private int ID;
	private String name;
	protected Image icon;

	private String toolTip;

	private boolean selected;

	public Item(int id, String name) {
		this.ID = id;
		this.name = name;
		selected=false;
		//defIcon();
	}

	public Item(String name) {
		this.ID = idg++;
		this.name = name;
		//defIcon();
	}

	public abstract void defIcon();

	public String getToolTip() {
		return toolTip;
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	public void setID(int id) {
		this.ID = id;
	}

	public int getID() {
		return ID;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Image getIcon() {
		return icon;
	}

	public void setIcon(Image icon) {
		this.icon = icon;
		this.width = icon.getWidth(null);
		this.height = icon.getHeight(null);
	}

	@Override
	public int compareTo(Item o) {
		int oid=o.getID();
		int mid=getID();
		return mid - oid;
	}

	public Color online() {
		return Color.green;
	}

	public abstract AbstractDescriptor getDescriptor();

	public void setSelected(boolean b) {
		this.selected=b;
	}
	
	public boolean isSelected(){
		return selected;
	}

}
