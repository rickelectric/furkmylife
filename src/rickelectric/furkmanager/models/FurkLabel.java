package rickelectric.furkmanager.models;

public class FurkLabel implements Comparable<FurkLabel>{
	
	private String
		ID,
		name,
		parentID,
		color,
		background;
	
	private int sortOrder;

	public FurkLabel(String ID,String name){
		this.ID=ID;
		this.name=name;
		parentID="0";
		sortOrder=0;
	}
	
	public FurkLabel(String ID,String name,String parentID){
		this(ID,name);
		this.parentID=parentID;
	}
	
	public void setParentID(String parentID){this.parentID=parentID;}
	public String getParentID(){return parentID;}
	
	public void setID(String ID){this.ID=ID;}
	public String getID(){return ID;}

	public String getName(){return name;}
	public void setName(String name){this.name = name;}
	
	public int getSortOrder(){return sortOrder;}
	public void setSortOrder(int sortOrder){this.sortOrder=sortOrder;}
	
	public String getColor(){return color;}
	public void setColor(String color){this.color = color;}

	public String getBackground(){return background;}
	public void setBackground(String background){this.background = background;}

	@Override
	public String toString() {
		return "FurkLabel [" + (ID != null ? "ID=" + ID + ", " : "")
				+ (name != null ? "name=" + name + ", " : "")
				+ (parentID != null ? "parentID=" + parentID + ", " : "")
				+ (color != null ? "color=" + color + ", " : "")
				+ (background != null ? "background=" + background + ", " : "")
				+ "sortOrder=" + sortOrder + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ID == null) ? 0 : ID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FurkLabel other = (FurkLabel) obj;
		if (ID == null) {
			if (other.ID != null)
				return false;
		} else if (!ID.equals(other.ID))
			return false;
		return true;
	}

	@Override
	public int compareTo(FurkLabel o) {
		String left="",right="";
		if(this.name!=null && o.name!=null){
			left+=this.name;
			right+=o.name;
		}
		if(this.ID!=null && o.ID!=null){
			left+=this.ID;
			right+=o.ID;
		}
		if(left.equals("")||right.equals("")) (this.hashCode()+"").compareTo((o.hashCode()+""));
		
		return left.compareTo(right);
	}

}
