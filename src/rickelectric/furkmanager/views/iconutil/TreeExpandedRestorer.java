package rickelectric.furkmanager.views.iconutil;

import ch.xmatrix.collection.ArrayUtils;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Category;

/**
 * This class proviedes methodes to save and restore expansion and selection states of a tree. Make sure to save
 * the state on the <b>same nodes</b> as restore takes place on.
 *
 * @author    $Author: daniel_frey $
 * @version   $Revision: 1.7 $ $Date: 2003/05/25 21:38:47 $
 */
public class TreeExpandedRestorer {

    private static final Category cat = Category.getInstance(TreeExpandedRestorer.class);
    private static final boolean DEBUG = cat.isDebugEnabled();

    /**
     * Selections are stored with path information. If you plan to insert different nodes, you should use this option.
     */
    public static final SelectionType SELECTION_BY_PATH = new SelectionType("SelectionByPath");

    /**
     * Selections are stored with row information. If you decide to delete nodes, you should select this option.
     */
    public static final SelectionType SELECTION_BY_ROW = new SelectionType("SelectionByRow");

    private List expandedPaths;
    private int[] selectionRows;
    private JTree tree;
    private SelectionType selectionType;

    /**
     * Saves the expanded state and selection of the tree.
     * @param tree the tree for which selection and expanstion state are saved
     * @param selectionType the type of selection, either {@link #SELECTION_BY_ROW} or {@link #SELECTION_BY_PATH}
     */
    public TreeExpandedRestorer(JTree tree, SelectionType selectionType) {
        this(tree, false);
    }

    /**
     * Saves the expanded state and optionally discards the selection of the tree.
     * @param tree the tree for which selection and expanstion state are saved
     * @param clearSelection whether to discard selection
     */
    public TreeExpandedRestorer(JTree tree, boolean clearSelection) {
        this.tree = tree;
        save();
        if (clearSelection) clearSelection();
    }

    /**
     * Saves selected node and all expanded paths.
     */
    public void save() {
        saveSelection();
        saveExpandedState();
        if (DEBUG) print();
    }

    /**
     * Use this method if you want to transfer a node with a tree, and you want to have the selection transfered too.
     * @param target the node being transfered
     * @param old the source tree path where the node was or is attached to
     * @param nw the destination tree path where the node will be or is attached to
     */
    public void update(TreeNode target, TreePath old, TreePath nw) {
        TreePath[] tp = (TreePath[]) expandedPaths.toArray(new TreePath[0]);
        for (int i = 0; i < tp.length; i++) {
            Object[] objs = tp.getPath();

for (int j = 0; j < objs.length; j++) {

if (objs[j] == target) {

Object[] newPath = new Object[objs.length - old.getPathCount() + nw.getPathCount()];

System.arraycopy(nw.getPath(), 0, newPath, 0, nw.getPathCount());

System.arraycopy(objs, j, newPath, nw.getPathCount(), objs.length - j);

tp[i] = new TreePath(newPath);

}

}

}

}



public void remove(TreePath tp) {

for (Iterator iterator = expandedPaths.iterator(); iterator.hasNext();) {

TreePath path = (TreePath) iterator.next();

if (tp.isDescendant(path)) iterator.remove();

}

int i = tree.getRowForPath(tp);

for (int j = 0; j < selectionRows.length; j++) {

int row = selectionRows[j];

if (row == i)

selectionRows = ArrayUtils.remove(selectionRows, j);

else if (row > i) selectionRows[j] -= 1;

}

if (DEBUG) print();

}



/**

* Expands all paths last saved and restores selected node.

* @param selectionType the type of selection, either {@link #SELECTION_BY_ROW} or {@link #SELECTION_BY_PATH}

*/

public void restore(SelectionType selectionType) {

restoreExpandedState();

restoreSelections(selectionType);

}



public void clearSelection() {

selectionRows = new int[0];

}



public void setSelection(TreePath path) {

selectionRows = new int[1];

selectionRows[0] = tree.getRowForPath(path);

}



public void addSelection(TreePath path) {

int[] newRows = new int[selectionRows.length + 1];

for (int i = 0; i < selectionRows.length; i++) {

newRows[i] = selectionRows[i];

}

newRows[selectionRows.length] = tree.getRowForPath(path);

selectionRows = newRows;

}



/**

*

* @param selectionType the type of selection, either {@link #SELECTION_BY_ROW} or {@link #SELECTION_BY_PATH}

*/

private void restoreSelections(SelectionType selectionType) {

if (selectionType == SELECTION_BY_ROW) {

if (selectionRows == null) return;

for (int i = 0; i < selectionRows.length; i++) {

int selectionRow = selectionRows[i];

selectionRows[i] = Math.min(selectionRow, tree.getRowCount() - 1);

}

tree.setSelectionRows(selectionRows);

} else {

tree.setSelectionPaths((TreePath[])expandedPaths.toArray(new TreePath[0]));

}

}



private void restoreExpandedState() {

for (Iterator iterator = expandedPaths.iterator(); iterator.hasNext();) {

TreePath path = (TreePath) iterator.next();

tree.expandPath(path);

}

}



private void saveExpandedState() {

expandedPaths = new ArrayList();

TreePath rootPath = new TreePath(((TreeNode) tree.getModel().getRoot()));

Enumeration enum = tree.getExpandedDescendants(rootPath);

while (enum != null && enum.hasMoreElements()) {

expandedPaths.add(enum.nextElement());

}

}



private void saveSelection() {

TreePath[] tp = tree.getSelectionPaths();

if (tp != null) {

selectionRows = new int[tp.length];

for (int i = 0; i < tp.length; i++) {

TreePath treePath = tp[i];

selectionRows[i] = tree.getRowForPath(treePath);

}

} else {

selectionRows = null;

}

}



private void print() {

if (selectionRows == null) return;

StringBuffer buffer = new StringBuffer("selection rows are: ");

int length = selectionRows.length;

for (int i = 0; i < length; i++) {

int selectionRow = selectionRows[i];

buffer.append(selectionRow + ((i == (length - 1)) ? "." : ", "));

}

cat.debug(buffer);

}



private static class SelectionType {

private String type;



private SelectionType(String type) {

this.type = type;

}

}



public static void main(String[] args) {

final javax.swing.JTree tree = new javax.swing.JTree();

final javax.swing.tree.DefaultTreeModel model = (javax.swing.tree.DefaultTreeModel) tree.getModel();



javax.swing.JButton reload = new javax.swing.JButton("Reload");

reload.addActionListener(new java.awt.event.ActionListener() {

public void actionPerformed(ActionEvent e) {

model.reload();

System.out.println("reloaded...");

}

});



final TreeExpandedRestorer ter = new TreeExpandedRestorer(tree, SELECTION_BY_ROW);



javax.swing.JButton save = new javax.swing.JButton("Save");

save.addActionListener(new java.awt.event.ActionListener() {

public void actionPerformed(ActionEvent e) {

System.out.println("Selected path before ter.save is: " + tree.getSelectionPath());

ter.save();

System.out.println("saved...");

}

});



javax.swing.JButton restore = new javax.swing.JButton("Restore");

restore.addActionListener(new java.awt.event.ActionListener() {

public void actionPerformed(ActionEvent e) {

System.out.println("Selected path before ter.restore is: " + tree.getSelectionPath());

ter.restore(SELECTION_BY_ROW);

System.out.println("restored...");

}

});



javax.swing.JButton print = new javax.swing.JButton("Print");

print.addActionListener(new java.awt.event.ActionListener() {

public void actionPerformed(ActionEvent e) {

System.out.println("Selected path is: " + tree.getSelectionPath());

}

});



javax.swing.JPanel p = new javax.swing.JPanel();

p.add(save);

p.add(reload);

p.add(restore);

p.add(print);



javax.swing.JFrame f = new javax.swing.JFrame();

f.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

f.getContentPane().setLayout(new java.awt.BorderLayout());

f.getContentPane().add(p, java.awt.BorderLayout.NORTH);

f.getContentPane().add(new javax.swing.JScrollPane(tree), java.awt.BorderLayout.CENTER);



f.setSize(400, 300);

f.setVisible(true);

}

}

