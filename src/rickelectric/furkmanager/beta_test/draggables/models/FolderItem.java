package rickelectric.furkmanager.beta_test.draggables.models;

import java.awt.Color;

import rickelectric.img.ImageLoader;

public class FolderItem extends Item {
	private static final long serialVersionUID = 1L;

	private FolderDescriptor desc;
	private boolean target;

	public FolderItem(FolderDescriptor desc) {
		super(desc.getName());
		this.desc = desc;
		this.target=false;
		defIcon();
	}
	
	public FolderDescriptor getDescriptor(){
		return desc;
	}

	public void defIcon() {
		this.icon = ImageLoader.getInstance().getImage("spaces/folder-64.png");
		if (icon != null) {
			this.width = icon.getWidth(null);
			this.height = icon.getHeight(null);
		} else {
			this.width = this.height = 32;
		}
	}
	
	public Color online() {
		return desc.isOrphaned()?Color.red:Color.green;
	}

	public boolean isTarget() {
		return target;
	}

	public void setTarget(boolean target) {
		this.target = target;
	}
}
