package rickelectric.furkmanager.beta_test.draggables.models;

public abstract class AbstractDescriptor {
	
	private FolderDescriptor parent;
	
	public AbstractDescriptor(FolderDescriptor parent){
		this.parent=parent;
	}
	
	public abstract String getName();
	
	public FolderDescriptor getParent(){
		return parent;
	}

}
