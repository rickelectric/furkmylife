package rickelectric.furkmanager.utils;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageManager{
	
	public static BufferedImage scaleImage(BufferedImage img,float percent){
		int scaleX =(int)(img.getWidth()*(percent/100f));
		int scaleY =(int)(img.getHeight()*(percent/100f));
		Image newImg = img.getScaledInstance(scaleX, scaleY, Image.SCALE_SMOOTH);
		BufferedImage buffered = new BufferedImage(scaleX, scaleY, BufferedImage.TYPE_INT_RGB);
		buffered.getGraphics().drawImage(newImg, 0, 0 , null);
		return buffered;
	}
	
	public static BufferedImage resizeImage(BufferedImage img,int width,int height){
		Image newImg=img.getScaledInstance(width,height,Image.SCALE_AREA_AVERAGING);
		if(width<=0) width=newImg.getWidth(null);
		if(height<=0) height=newImg.getHeight(null);
		return imageToBufferedImage(newImg);
	}
	
	public static boolean saveImage(BufferedImage buffered,String destpath){
		try {
			File dest=new File(destpath);
			ImageIO.write(buffered,"png",dest);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static BufferedImage imageToBufferedImage(Image im){
		BufferedImage bi = new BufferedImage
	    		 (im.getWidth(null),im.getHeight(null),BufferedImage.TYPE_INT_ARGB);
		Graphics bg = bi.getGraphics();
		bg.drawImage(im, 0, 0, null);
		bg.dispose();
		return bi;
	}
	
	public static void openInPaint(String image){
		try {
			Runtime.getRuntime().exec(new String[]{"C:\\Windows\\system32\\mspaint.exe",image});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
