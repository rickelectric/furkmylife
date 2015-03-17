package rickelectric.furkmanager.beta_test.draggables.models;

import java.awt.Color;

import rickelectric.img.ImageLoader;

public class FileItem extends Item {
	private static final long serialVersionUID = 1L;

	private FileDescriptor desc;

	public FileItem(FileDescriptor desc) {
		super(desc.getName());
		this.desc = desc;
		defIcon();
	}

	public FileDescriptor getDescriptor() {
		return desc;
	}

	public void defIcon() {
		switch (desc.getFileObject().getType()) {
		case AUDIO:
			this.icon = ImageLoader.getInstance().getImage(
					"folder/audio-48.png");
			break;
		case VIDEO:
			this.icon = ImageLoader.getInstance().getImage(
					"folder/video-48.png");
			break;
		default:
			this.icon = ImageLoader.getInstance().getImage(
					"folder/default-file-64.png");
			break;
		}
		if (icon != null) {
			this.width = icon.getWidth(null);
			this.height = icon.getHeight(null);
		} else {
			this.width = this.height = 32;
		}
	}

	@Override
	public String toString() {
		return "Item [ID=" + getID() + ", "
				+ (getName() != null ? "name=" + getName() + ", " : "") + "x="
				+ x + ", y=" + y + ", width=" + width + ", height=" + height
				+ "]";
	}
	
	public boolean equals(Object o) {
		if (o instanceof FileItem) {
			return getID() == ((FileItem) o).getID();
		}
		return false;
	}

	public Color online() {
		if (!desc.getFileObject().isReady())
			return Color.red;
		if (desc.getFileObject().getAvResult() != null){
			if(desc.getFileObject().getAvResult().contains("error"))
				return Color.orange;
		}
		if(desc.getFileObject().getSize()>100*1024*1024) return Color.black;
		return Color.green;
	}

}
