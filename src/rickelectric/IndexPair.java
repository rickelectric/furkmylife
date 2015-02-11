package rickelectric;

public class IndexPair implements Comparable<IndexPair>{
	private String key;
	private String value;
	
	public IndexPair(String key,String value) {
		this.key = key;
		this.value = value==null?"null":value;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public int compareTo(IndexPair o) {
		return value.compareTo(o.value);
	}
	
	public boolean equals(Object o){
		if(o instanceof IndexPair){
			if(key.equals(((IndexPair) o).key)) return true;
		}
		return false;
	}
}