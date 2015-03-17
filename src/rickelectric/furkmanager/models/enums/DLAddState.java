package rickelectric.furkmanager.models.enums;

public enum DLAddState {
	FAILED,DOWNLOADING,FILE;
	private Object arg;
	
	DLAddState(){
		arg=null;
	}
	
	public void setArg(Object arg){
		this.arg=arg;
	}
	
	public Object getArg(){
		return arg;
	}
	
}
