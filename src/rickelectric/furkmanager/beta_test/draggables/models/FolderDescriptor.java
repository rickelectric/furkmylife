package rickelectric.furkmanager.beta_test.draggables.models;

import java.util.ArrayList;

import rickelectric.furkmanager.models.FurkLabel;

public class FolderDescriptor extends AbstractDescriptor{
	
	private FurkLabel curr;
	private boolean orphaned;
	private ArrayList<AbstractDescriptor> children;
	
	public FolderDescriptor(FolderDescriptor parent,FurkLabel curr){
		super(parent);
		this.curr=curr;
		children=new ArrayList<AbstractDescriptor>();
		orphaned = false;
	}
	
	public String getName(){
		return curr.getName();
	}
	
	public boolean isOrphaned(){
		return orphaned;
	}
	
	public void setOrphaned(boolean b){
		this.orphaned=b;
	}
	
	public ArrayList<AbstractDescriptor> getChildren(){
		return children;
	}
	
	public boolean addChild(AbstractDescriptor child){
		return children.add(child);
	}
	
	public boolean removeChild(AbstractDescriptor child){
		return children.remove(child);
	}
	
	public boolean existsChild(AbstractDescriptor child){
		for(AbstractDescriptor c:children){
			if(c.equals(child)) return true;
		}
		return false;
	}

	public FurkLabel getLabel() {
		return curr;
	}

	public String getId() {
		return curr.getID();
	}

}
