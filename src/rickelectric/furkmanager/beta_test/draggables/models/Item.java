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

	private boolean hovered;

	public Item(int id, String name) {
		this.ID = id;
		this.name = name;
		selected=false;
		hovered=false;
		//defIcon();
	}

	public Item(String name) {
		this.ID = idg++;
		this.name = name;
		selected=false;
		hovered=false;
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
		if(o==null) return 100000; 
		int numeral,oNumeral = numeral = 1;
		if(this instanceof FolderItem) numeral = 10000;
		if(o instanceof FolderItem) oNumeral = 10000;
		int oid=oNumeral*o.getID();
		int mid=numeral*getID();
		return oid - mid;
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

	public boolean isHovered() {
		return hovered;
	}

	public void setHovered(boolean hovered) {
		this.hovered = hovered;
	}
	
	public int randCode() {
		final int prime = 31;
		int result = prime * ID;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((toolTip == null) ? 0 : toolTip.hashCode());
		return result;
	}

}
