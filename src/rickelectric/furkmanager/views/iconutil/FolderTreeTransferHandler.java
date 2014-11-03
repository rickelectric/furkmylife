package rickelectric.furkmanager.views.iconutil;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import rickelectric.furkmanager.models.APIFolder;
import rickelectric.furkmanager.models.MoveableItem;
import rickelectric.furkmanager.network.APIFolderManager;
import rickelectric.furkmanager.views.icons.FileTreeNode;
import rickelectric.furkmanager.views.icons.FolderTreeNode;
import rickelectric.furkmanager.views.windows.AppFrameClass;

public class FolderTreeTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;

	private DataFlavor nodesFlavor;
	private DataFlavor[] flavors = new DataFlavor[1];
	private DefaultMutableTreeNode[] nodesToRemove;

	public FolderTreeTransferHandler() {
		try {
			String mimeType = DataFlavor.javaJVMLocalObjectMimeType
					+ ";class=\""
					+ javax.swing.tree.DefaultMutableTreeNode[].class.getName()
					+ "\"";
			nodesFlavor = new DataFlavor(mimeType);
			flavors[0] = nodesFlavor;
		} catch (ClassNotFoundException e) {
			System.out.println("ClassNotFound: " + e.getMessage());
		}
	}

	@Override
	public boolean canImport(TransferSupport support) {
		JTree tree = (JTree) support.getComponent();
		if (!support.isDrop()) {
			((AppFrameClass)tree.getTopLevelAncestor()).setStatus("Drop Forbidden - Not A Drop Location");
			return false;
		}
		support.setShowDropLocation(true);
		if (!support.isDataFlavorSupported(nodesFlavor)) {
			((AppFrameClass)tree.getTopLevelAncestor()).setStatus("Drop Forbidden - Flavour Not Spprtd");
			return false;
		}

		// Do Not Allow Any Action Other Than A MOVE
		int action = support.getDropAction();
		if (action != MOVE){
			((AppFrameClass)tree.getTopLevelAncestor()).setStatus("Drop Forbidden - Not A MOVE");
			return false;
		}

		// Do not allow a drop on the drag source selections.
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
		int dropRow = tree.getRowForPath(dl.getPath());
		int[] selRows = tree.getSelectionRows();
		for (int i = 0; i < selRows.length; i++) {
			if (selRows[i] == dropRow) {
				((AppFrameClass)tree.getTopLevelAncestor()).setStatus("Drop Forbidden - Destination is Source");
				return false;
			}
		}
		TreePath dest = dl.getPath();
		DefaultMutableTreeNode target = (DefaultMutableTreeNode) dest
				.getLastPathComponent();

		// Do not allow MOVE-action unless the destination is a FolderTreeNode
		if (!(target instanceof FolderTreeNode)) {
			((AppFrameClass)tree.getTopLevelAncestor()).setStatus("Drop Forbidden - Not a FolderTreeNode");
			return false;
		}

		// Do not allow a non-leaf node to be moved to a level
		// which is lower than its source level.
		TreePath path = tree.getPathForRow(selRows[0]);
		DefaultMutableTreeNode firstNode = (DefaultMutableTreeNode) path
				.getLastPathComponent();
		if (firstNode.getChildCount() > 0
				&& target.getLevel() > firstNode.getLevel()) {
			((AppFrameClass)tree.getTopLevelAncestor()).setStatus("Drop Forbidden - Parent to Child Transfer");
			return false;
		}
		((AppFrameClass)tree.getTopLevelAncestor()).setStatus("Drop Allowed");
		return true;
	}

	protected Transferable createTransferable(JComponent c) {
		JTree tree = (JTree) c;
		TreePath path = tree.getSelectionPath();
		if (path != null){
			// Make up a copy for transfer and
			// another for/of the node that will be removed in
			// exportDone after a successful drop.
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) path
					.getLastPathComponent();
			DefaultMutableTreeNode copy = copy(node);
			if(copy==null) return null;

			DefaultMutableTreeNode[] nodes = { copy };
			nodesToRemove = new DefaultMutableTreeNode[] { node };
			return new NodesTransferable(nodes);
		}
		return null;
	}

	/** Defensive copy used in createTransferable. */
	private DefaultMutableTreeNode copy(TreeNode node) {
		if (node instanceof FolderTreeNode) {
			return new FolderTreeNode(((FolderTreeNode) node).getUserObject());
		}
		if (node instanceof FileTreeNode) {
			return new FileTreeNode(((FileTreeNode) node).getUserObject());
		}
		return null;
	}

	protected void exportDone(JComponent source, Transferable data, int action) {
		if ((action & MOVE) == MOVE) {
			JTree tree = (JTree) source;
			DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
			// Remove nodes saved in nodesToRemove in createTransferable.
			for (int i = 0; i < nodesToRemove.length; i++) {
				model.removeNodeFromParent(nodesToRemove[i]);
			}
		}
	}

	public int getSourceActions(JComponent c) {
		return MOVE;
	}

	public boolean importData(TransferSupport support) {
		if (!canImport(support)) {
			return false;
		}
		// Extract transfer data.
		DefaultMutableTreeNode[] nodes = null;
		try{
			Transferable t = support.getTransferable();
			nodes = (DefaultMutableTreeNode[]) t.getTransferData(nodesFlavor);
		}catch (UnsupportedFlavorException ufe) {
			System.out.println("UnsupportedFlavor: " + ufe.getMessage());
		}catch (java.io.IOException ioe) {
			System.out.println("I/O error: " + ioe.getMessage());
		}
		// Get drop location info.
		JTree.DropLocation dl = (JTree.DropLocation) support.getDropLocation();
		int childIndex = dl.getChildIndex();
		TreePath dest = dl.getPath();
		FolderTreeNode parent=(FolderTreeNode)dest.getLastPathComponent();
		JTree tree = (JTree) support.getComponent();
		DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
		// Configure for drop mode.
		int index = childIndex; // DropMode.INSERT
		if (childIndex == -1) { // DropMode.ON
			index = parent.getChildCount();
		}
		// Add data to model.
		for (int i = 0; i < nodes.length; i++) {
			if(nodes[i].getUserObject() instanceof MoveableItem){
				//Move Item In FolderManager and on Furk Servers Before Move Here
				MoveableItem item=(MoveableItem)(nodes[i].getUserObject());
				APIFolderManager.move(item, parent.getUserObject());
				if(nodes[i] instanceof FolderTreeNode){
					APIFolder folder=((FolderTreeNode)nodes[i]).getUserObject();
					nodes[i]=APIFolderManager.populateTree(folder);
				}
			}
			model.insertNodeInto(nodes[i], parent, index++);
			tree.repaint();
		}
		return true;
	}

	public class NodesTransferable implements Transferable {
		DefaultMutableTreeNode[] nodes;

		public NodesTransferable(DefaultMutableTreeNode[] nodes) {
			this.nodes = nodes;
		}

		public Object getTransferData(DataFlavor flavor)
				throws UnsupportedFlavorException {
			if (!isDataFlavorSupported(flavor))
				throw new UnsupportedFlavorException(flavor);
			return nodes;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return nodesFlavor.equals(flavor);
		}
	}

	public void alert(String s) {
		JOptionPane.showMessageDialog(null, s);
	}

}
