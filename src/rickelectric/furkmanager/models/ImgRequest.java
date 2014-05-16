package rickelectric.furkmanager.models;

import java.awt.image.BufferedImage;

public class ImgRequest implements Comparable<Object>{

	private String url;
	private BufferedImage image;
	private long time;
	private int size;

	public ImgRequest(String url, BufferedImage image, long time) {
		this.url = url;
		this.image = image;
		this.time = time;
		size=image.getHeight()*image.getWidth();
	}

	public BufferedImage getImage() {
		return image;
	}

	public void setImage(BufferedImage image){
		this.image = image;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getUrl() {
		return url;
	}
	
	private enum CompCriteria{
		URL,TIME,SIZE
	}
	
	public static final CompCriteria 
		URL=CompCriteria.URL,
		TIME=CompCriteria.TIME,
		SIZE=CompCriteria.SIZE;
	private static CompCriteria cVal=URL;
	
	/**
	 * Default: URL
	 * @param by Comparison Criterion of the compareTo method (TIME / SIZE / URL)
	 */
	public static void compareCriterion(CompCriteria by){
		cVal=by;
	}

	public int compareTo(Object o) {
		if(cVal==TIME){
			if(o instanceof ImgRequest){
				new Long(time).compareTo(new Long(((ImgRequest)o).getTime()));
			}
			throw new IllegalArgumentException("Expected an ImgRequest object, not a "+o.getClass().getName());
		}
		
		else if(cVal==SIZE){
			if(o instanceof ImgRequest){
				return new Integer(size).compareTo(new Integer(((ImgRequest)o).size));
			}
			throw new IllegalArgumentException("Expected an ImgRequest object, not a "+o.getClass().getName());
		}
		
		else{
			if(o instanceof ImgRequest){
				if(url==null) throw new RuntimeException("Null URL Identifier in ImgRequest");
				return url.compareTo(((ImgRequest) o).getUrl());
			}
			else if(o instanceof String){
				return url.compareTo((String)o);
			}
			throw new IllegalArgumentException("Expected Either a String or an ImgRequest object, not a "+o.getClass().getName());
		}
	}
	
	@Override
	public boolean equals(Object o){
		if(url==null) return false;
		if(o instanceof ImgRequest){
			return url.equals(((ImgRequest) o).getUrl());
		}
		else if(o instanceof String){
			return url.equals((String)o);
		}
		throw new IllegalArgumentException("Expected Either a String or an ImgRequest object, not a "+o.getClass().getName());
	}

}
