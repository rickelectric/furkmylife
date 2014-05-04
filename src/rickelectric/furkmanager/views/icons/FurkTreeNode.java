package rickelectric.furkmanager.views.icons;

import javax.swing.JPopupMenu;
import javax.swing.tree.MutableTreeNode;

public interface FurkTreeNode extends MutableTreeNode{
	
	public JPopupMenu popupMenu();
	
	public Object getUserObject();
	
	public void action();
	
	public boolean draggable();
	public boolean droppable();
}
