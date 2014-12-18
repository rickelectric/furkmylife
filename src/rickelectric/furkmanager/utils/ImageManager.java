package rickelectric.furkmanager.utils;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.xerces.impl.dv.util.Base64;

public class ImageManager {

	public static BufferedImage rotate(BufferedImage image, double angle) {
		double sin = Math.abs(Math.sin(angle)), cos = Math.abs(Math.cos(angle));
		int w = image.getWidth(), h = image.getHeight();
		int neww = (int) Math.floor(w * cos + h * sin), newh = (int) Math
				.floor(h * cos + w * sin);
		GraphicsConfiguration gc = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		BufferedImage result = gc.createCompatibleImage(neww, newh,
				Transparency.TRANSLUCENT);
		Graphics2D g = result.createGraphics();
		g.translate((neww - w) / 2, (newh - h) / 2);
		g.rotate(angle, w / 2, h / 2);
		g.drawRenderedImage(image, null);
		g.dispose();
		return result;
	}

	public static BufferedImage scaleImage(BufferedImage img, float percent) {
		int scaleX = (int) (img.getWidth() * (percent / 100f));
		int scaleY = (int) (img.getHeight() * (percent / 100f));
		Image newImg = img
				.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
		BufferedImage buffered = new BufferedImage(scaleX, scaleY,
				BufferedImage.TYPE_INT_RGB);
		buffered.getGraphics().drawImage(newImg, 0, 0, null);
		return buffered;
	}

	public static BufferedImage resizeImage(Image img, int width,
			int height) {
		Image newImg = img.getScaledInstance(width, height,
				Image.SCALE_AREA_AVERAGING);
		if (width <= 0)
			width = newImg.getWidth(null);
		if (height <= 0)
			height = newImg.getHeight(null);
		return imageToBufferedImage(newImg);
	}

	public static boolean saveImage(BufferedImage buffered, String destpath) {
		try {
			File dest = new File(destpath);
			ImageIO.write(buffered, "png", dest);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}

	public static BufferedImage imageToBufferedImage(Image im) {
		BufferedImage bi = new BufferedImage(im.getWidth(null),
				im.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(im, 0, 0, null);
		bg.dispose();
		return bi;
	}

	public static String bufferedToString(BufferedImage img) {
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream(1000);
			ImageIO.write(img, "png", baos);
			baos.flush();

			String base64String = Base64.encode(baos.toByteArray());
			return base64String;
		} catch (Exception e) {
			return null;
		}
	}

	public static BufferedImage stringToBuffered(String s) {
		try {
			byte[] bytes = Base64.decode(s);
			ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

			BufferedImage img = ImageIO.read(stream);
			return img;
		} catch (Exception e) {
			return null;
		}
	}

	public static void openInPaint(String image) {
		try {
			Runtime.getRuntime()
					.exec(new String[] { "C:\\Windows\\system32\\mspaint.exe",
							image });
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Pings the specified web address or ip address.
	 * 
	 * @param address
	 *            URL to ping
	 * @return round trip time(in ms)
	 */
	public static long ping(URL url) {
		try {
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setConnectTimeout(10000);
			long startTime = System.currentTimeMillis();
			urlConn.connect();
			long endTime = System.currentTimeMillis();
			if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				long time = (endTime - startTime);
				return time;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return -1;
	}

	/**/

	public static boolean ping(String host) {
		try {
			boolean isWindows = System.getProperty("os.name").toLowerCase()
					.contains("win");

			ProcessBuilder processBuilder = new ProcessBuilder("ping",
					isWindows ? "-n" : "-c", "1", host);
			Process proc = processBuilder.start();

			int returnVal = proc.waitFor();
			return returnVal == 0;
		} catch (Exception e) {
			return false;
		}
	}
	
	public static BufferedImage classLoadImage(String rootClassPath){
		try {
			return ImageIO.read(ImageManager.class.getResource(rootClassPath));
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static BufferedImage fileLoadImage(File imgFile) {
		try {
			return ImageIO.read(imgFile);
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) throws IOException,
			InterruptedException {

	}
}
