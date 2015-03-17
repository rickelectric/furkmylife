package rickelectric.furkmanager.beta_test.draggables.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;

import rickelectric.furkmanager.beta_test.draggables.Space;
import rickelectric.img.ImageLoader;

public class SpaceContextMenu extends JPopupMenu implements ActionListener {
	private static final long serialVersionUID = 1L;

	private Space space;
	private JMenuItem addFolder, refresh, close;

	public SpaceContextMenu(Space space) {
		this.space = space;

		addFolder = new JMenuItem("New Folder");
		addFolder.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
				"sm/new_black.png")));
		add(addFolder);
		addFolder.addActionListener(this);

		close = new JMenuItem("Close this Space");
		close.setIcon(new ImageIcon(ImageLoader.getInstance().getImage(
				"sm/edit_delete.png")));
		close.addActionListener(this);
		add(close);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object src = e.getSource();
		if (src.equals(addFolder)) {
			JTextField f = new JTextField();
			f.setColumns(40);
			int resp = JOptionPane.showConfirmDialog(space.getContentPane(), f,
					"Folder Name", JOptionPane.OK_CANCEL_OPTION,
					JOptionPane.QUESTION_MESSAGE);
			if (resp == JOptionPane.OK_OPTION) {
				String name = f.getText();
				try {
					boolean newF = space
							.getManager()
							.getTree()
							.newFolder(space.getManager().getCurrentFolder(),
									name);
					if (!newF)
						throw new RuntimeException("Folder Creation Failed");
					space.loadItemsInCurrentFolder();
				} catch (Exception ex) {
					JOptionPane.showMessageDialog(space.getContentPane(),
							ex.getMessage());
				}
			}
		}
		if(src.equals(refresh)){
			space.loadItemsInCurrentFolder();
		}
		if (src.equals(close)) {
			space.close();
		}
	}

}
