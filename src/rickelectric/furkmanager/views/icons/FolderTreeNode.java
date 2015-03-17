package rickelectric.furkmanager.views.icons;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.tree.DefaultMutableTreeNode;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.APIFolder;
import rickelectric.furkmanager.network.APIFolderManager;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.windows.MainEnv;
import rickelectric.furkmanager.views.windows.MainWindow;
import rickelectric.img.ImageLoader;

public class FolderTreeNode extends DefaultMutableTreeNode implements
		FurkTreeNode {
	private static final long serialVersionUID = 1L;

	private APIFolder folder;

	@Override
	public APIFolder getUserObject() {
		return folder;
	}

//	public void setParent(JTree parent) {
//		this.parentTree = parent;
//	}

	public FolderTreeNode(APIFolder folder) {
		super(folder.getName());
		this.folder = folder;
	}

	@Override
	public String toString() {
		return folder.getName();
	}

	@Override
	public JPopupMenu popupMenu() {
		return new ContextMenu(folder,this);
	}

	@Override
	public void action() {

	}

	public static class ContextMenu extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;

		private APIFolder folder = null;

		private JMenuItem folder_delete, folder_new;
		private JMenuItem folder_rename;
		private JMenuItem folder_colorchange;

		private FolderTreeNode thisNode;

		// End FolderType Menu

		public ContextMenu(APIFolder folder,FolderTreeNode thisNode) {
			super();
			this.folder = folder;
			this.thisNode = thisNode;

			folder_new = new JMenuItem("New Folder");
			folder_new.addActionListener(this);
			folder_new.setIcon(new ImageIcon(ImageLoader.getInstance().getImage("sm/new_black.png")));
			add(folder_new);

			folder_colorchange = new JMenuItem("Change Color");
			folder_colorchange.addActionListener(this);
			folder_colorchange.setIcon(new ImageIcon(ImageLoader.getInstance().getImage("sm/web_view.png")));
			add(folder_colorchange);

			folder_rename = new JMenuItem("Rename Folder");
			folder_rename.addActionListener(this);
			folder_rename.setIcon(new ImageIcon(ImageLoader.getInstance().getImage("sm/edit_icon.png")));
			add(folder_rename);

			folder_delete = new JMenuItem("Delete Folder");
			folder_delete.addActionListener(this);
			folder_delete.setIcon(new ImageIcon(ImageLoader.getInstance().getImage("sm/edit_delete.png")));
			add(folder_delete);

		}

		@Override
		public void actionPerformed(ActionEvent e) {
			Object src = e.getSource();
			if (src.equals(folder_new)) {
				JTextField f = new JTextField();
				f.setColumns(40);
				int resp = JOptionPane
						.showConfirmDialog(
								((SettingsManager.getInstance().getMainWinMode() == SettingsManager.ENV_MODE ? ((MainEnv) FurkManager
										.getMainWindow()).getWindow()
										: (MainWindow) FurkManager
												.getMainWindow())), f,
								"Folder Name", JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (resp == JOptionPane.OK_OPTION) {
					String name = f.getText();
					if (name != null && name.length() >= 0) {
						if (APIFolderManager.newFolder(folder, name)){
							//parentRef(false);
						}
					}

				}
			} else if (src == folder_colorchange) {
				// TODO Change Color, Update The Label
			} else if (src == folder_rename) {
				JTextField f = new JTextField();
				f.setColumns(40);
				f.setText(folder.getName());
				f.setSelectionStart(0);
				f.setSelectionEnd(folder.getName().length());
				int resp = JOptionPane
						.showConfirmDialog(
								((SettingsManager.getInstance().getMainWinMode() == SettingsManager.ENV_MODE ? ((MainEnv) FurkManager
										.getMainWindow()).getWindow()
										: (MainWindow) FurkManager
												.getMainWindow())), f,
								"Rename", JOptionPane.OK_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (resp == JOptionPane.OK_OPTION) {
					String name = f.getText();
					if (name != null && name.length() >= 0)
						if (APIFolderManager.rename(folder, name)) {
							//parentRef(false);
						}
				}
			} else if (src == folder_delete) {
				int resp = JOptionPane
						.showConfirmDialog(
								((SettingsManager.getInstance().getMainWinMode() == SettingsManager.ENV_MODE ? ((MainEnv) FurkManager
										.getMainWindow()).getWindow()
										: (MainWindow) FurkManager
												.getMainWindow())),
								"Are You Sure You Want To Delete This Folder?\n"
										+ "(This Operation Is Permanent)",
								"Delete", JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
				if (resp == JOptionPane.YES_OPTION) {
					if (APIFolderManager.delete(folder)) {
						APIFolderManager.getModel().removeNodeFromParent(thisNode);
					}
				}
			}
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