package rickelectric.furkmanager.views.iconutil;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkTFile;
import rickelectric.furkmanager.views.icons.FileTreeNode;
import rickelectric.furkmanager.views.icons.FolderTreeNode;
import rickelectric.furkmanager.views.icons.TFileTreeNode;
import rickelectric.img.ImageLoader;

public class FolderTreeRenderer extends DefaultTreeCellRenderer {

	private static final long serialVersionUID = 1L;

	private ImageIcon folderY = new ImageIcon(ImageLoader.getInstance()
			.getImage("tree/folder-yellow-16.png"));

	private ImageIcon folderB = new ImageIcon(ImageLoader.getInstance()
			.getImage("tree/folder-blue-16.png"));

	private ImageIcon audio = new ImageIcon(ImageLoader.getInstance().getImage(
			"tree/audio-16.png"));

	private ImageIcon video = new ImageIcon(ImageLoader.getInstance().getImage(
			"tree/video-16.png"));

	private ImageIcon loading = new ImageIcon(
			ImageLoader.class.getResource("tree/loading-16.gif"));

	private Icon image = new ImageIcon(ImageLoader.getInstance().getImage(
			"tree/image-16.png"));

	private Icon text = new ImageIcon(ImageLoader.getInstance().getImage(
			"tree/text-16.png"));

	private Icon pdf = new ImageIcon(ImageLoader.getInstance().getImage(
			"tree/pdf-16.png"));

	private ImageIcon def = new ImageIcon(ImageLoader.getInstance().getImage(
			"fr-16.png"));

	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean isLeaf, int row,
			boolean focused) {
		Component c = super.getTreeCellRendererComponent(tree, value, selected,
				expanded, isLeaf, row, focused);
		if (value instanceof FolderTreeNode) {
			if (selected)
				setIcon(folderY);
			else
				setIcon(folderB);
		}
		if (value instanceof FileTreeNode) {
			FileTreeNode val = (FileTreeNode) value;
			if (val.isBusy()) {
				setIcon(loading);
				loading.setImageObserver(new NodeImageObserver(tree, val));
			} else {
				FurkFile curr = val.getUserObject();
				String type = curr.getType().toString();
				if (type.equals("audio"))
					setIcon(audio);
				else if (type.equals("video"))
					setIcon(video);
				else
					setIcon(def);
			}
		}
		if (value instanceof TFileTreeNode) {
			TFileTreeNode val = (TFileTreeNode) value;
			FurkTFile curr = val.getUserObject();
			String type = curr.getContentType();
			if (val.isBusy()) {
				setIcon(loading);
				loading.setImageObserver(new NodeImageObserver(tree, val));
			} else {
				if (type.contains("audio")) {
					setIcon(audio);
				} else if (type.contains("video")) {
					setIcon(video);
				} else if (type.contains("image")) {
					setIcon(image);
				} else if (type.contains("msword") || type.contains("text")) {
					setIcon(text);
				} else if (type.contains("pdf")) {
					setIcon(pdf);
				} else
					setIcon(def);
			}
		}
		return c;
	}

}
