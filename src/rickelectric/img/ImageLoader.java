package rickelectric.img;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

import javax.imageio.ImageIO;

public class ImageLoader {
	private static ImageLoader imageManager;

	private HashMap<String, Image> loadedImages;

	private ImageLoader() {
		loadAssets();
	}

	public synchronized static ImageLoader getInstance() {
		if (imageManager == null)
			imageManager = new ImageLoader();
		return imageManager;
	}

	private void loadAssets() {
		loadedImages = new HashMap<String, Image>();
	}

	/**
	 * Retrieves an image from disk
	 * 
	 * @param ref
	 * @return
	 */
	private Image loadImage(String ref) {
		Image tempImage = null;
		BufferedImage sourceImage = null;

		try {
			URL url = ImageLoader.class.getResource(ref);

			if (url == null) {
				System.out.println("Can't find ref: " + ref);
			}
			sourceImage = ImageIO.read(url);
			
		} catch (IOException e) {
			System.out.println("Failed to load: " + ref);
		}

		// creates an accelerated image
		GraphicsConfiguration gc = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		tempImage = gc.createCompatibleImage(sourceImage.getWidth(),
				sourceImage.getHeight(), Transparency.TRANSLUCENT);

		// draw source image into the accelerated image
		tempImage.getGraphics().drawImage(sourceImage, 0, 0, null);

		return tempImage;

	}

	public Image getImage(String imageExtName) {
		Image img = loadedImages.get(imageExtName);
		if (img == null) {
			img = loadImage(imageExtName);
			loadedImages.put(imageExtName, img);
		}
		return img;
	}

}
