package rickelectric.furkmanager.views.icons;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.APIFolder;
import rickelectric.furkmanager.network.APIFolderManager;
import rickelectric.furkmanager.views.panels.File_FolderView;

public class FolderTreeNode extends DefaultMutableTreeNode implements
		FurkTreeNode {
	private static final long serialVersionUID = 1L;

	private APIFolder folder;
	private JTree parentTree;
	
	public APIFolder getUserObject() {
		return folder;
	}
	
	public void setParent(JTree parent){
		this.parentTree=parent;
	}

	public FolderTreeNode(APIFolder folder){
		super(folder.getName());
		this.folder = folder;
	}

	@Override
	public String toString() {
		return folder.getName();
	}

	public JPopupMenu popupMenu() {
		return new ContextMenu(folder);
	}
	
	public void action(){
		
	}

	public class ContextMenu extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;
		
		private APIFolder folder = null;
		
		private JMenuItem folder_delete,folder_new;
		private JMenuItem folder_rename;
		private JMenuItem folder_colorchange;

		// End FolderType Menu

		public ContextMenu(APIFolder folder) {
			super();
			this.folder = folder;
			
			folder_new=new JMenuItem("New Folder");
			folder_new.addActionListener(this);
			folder_new.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/new_black.png")));
			add(folder_new);

			folder_colorchange = new JMenuItem("Change Color");
			folder_colorchange.addActionListener(this);
			folder_colorchange.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/web_view.png")));
			add(folder_colorchange);

			folder_rename = new JMenuItem("Rename Folder");
			folder_rename.addActionListener(this);
			folder_rename.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/edit_icon.png")));
			add(folder_rename);
			
			folder_delete = new JMenuItem("Delete Folder");
			folder_delete.addActionListener(this);
			folder_delete.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/edit_delete.png")));
			add(folder_delete);
			
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src=e.getSource();
			if(src.equals(folder_new)){
				JTextField f=new JTextField();
				f.setColumns(40);
				int resp=JOptionPane.showConfirmDialog(null, f, "Folder Name", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(resp==JOptionPane.OK_OPTION){
					String name=f.getText();
					if(name!=null&&name.length()>=0){
						if(APIFolderManager.newFolder(folder, name))
							parentRef(false);
					}
					
				}
			}
			else if(src==folder_colorchange){
				//TODO Change Color, Update The Label
			}
			else if(src==folder_rename){
				JTextField f=new JTextField();
				f.setColumns(40);
				f.setText(folder.getName());
				f.setSelectionStart(0);
				f.setSelectionEnd(folder.getName().length());
				int resp=JOptionPane.showConfirmDialog(null, f, "Rename", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(resp==JOptionPane.OK_OPTION){
					String name=f.getText();
					if(name!=null&&name.length()>=0)
						if(APIFolderManager.rename(folder,name)){
							parentRef(false);
						}
				}
			}
			else if(src==folder_delete){
				int resp=JOptionPane.showConfirmDialog(null,
					"Are You Sure You Want To Delete This Folder?\n"
					+ "(This Operation Is Permanent)", 
					"Delete", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
				if(resp==JOptionPane.YES_OPTION){
					if(APIFolderManager.delete(folder)){
						parentRef(true);
					}
				}
			}
		}
	}
	
	private void parentRef(boolean hard){
		try {
			Container parent=parentTree.getParent();
			do {
				parent = parent.getParent();
			} while (!(parent instanceof File_FolderView));
			((File_FolderView) parent).refreshMyFolders(hard);
		} catch (Exception ex){
			System.err
					.println("Could Not Find Proper Parent");
			ex.printStackTrace();
		}
	}

	@Override
	public boolean draggable() {
		return true;
	}

	@Override
	public boolean droppable() {
		return true;
	}

}