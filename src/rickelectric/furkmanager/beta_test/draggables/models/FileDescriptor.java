package rickelectric.furkmanager.beta_test.draggables.models;

import rickelectric.furkmanager.models.FurkFile;

public class FileDescriptor extends AbstractDescriptor{
	
	private FurkFile file;

	public FileDescriptor(FolderDescriptor parent,FurkFile file){
		super(parent);
		this.file=file;
	}
	
	public String getName(){
		return file.getName();
	}
	
	public FurkFile getFileObject(){
		return file;
	}

	public String getId() {
		return file.getID();
	}

}
