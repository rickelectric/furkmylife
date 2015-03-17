package rickelectric.furkmanager.beta_test;

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JComponent;

import rickelectric.img.ImageLoader;

public class FolderIcon extends JComponent {
	private static final long serialVersionUID = 1L;
	
	public FolderIcon(String folderName){
		
	}

	public void paint(Graphics g) {
		Image folderImage = ImageLoader.getInstance().getImage(
				"folder/folder-64.png");
		int imgX = getWidth() / 2 - folderImage.getWidth(null) / 2;
		int imgY = getHeight() / 2 - folderImage.getHeight(null) / 2;
		g.drawImage(folderImage, imgX,imgY, null);
	}

}
