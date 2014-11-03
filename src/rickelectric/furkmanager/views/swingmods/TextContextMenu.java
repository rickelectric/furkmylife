package rickelectric.furkmanager.views.swingmods;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import rickelectric.furkmanager.utils.UtilBox;

public class TextContextMenu extends JPopupMenu implements ActionListener{
	private static final long serialVersionUID = 1L;
	private JMenuItem cut;
	private JMenuItem copy;
	private JMenuItem paste;
	private JMenuItem delete;
	
	private JTextComponent txt;

	public TextContextMenu(JTextComponent txt){
		this.txt = txt;
		boolean hasText=txt.getSelectedText()!=null;
		
		copy = new JMenuItem("Copy");
		copy.setIcon(new ImageIcon(TextContextMenu.class.getResource("/com/sun/javafx/scene/web/skin/Copy_16x16_JFX.png")));
		copy.addActionListener(this);
		add(copy);
		
		cut = new JMenuItem("Cut");
		cut.setIcon(new ImageIcon(TextContextMenu.class.getResource("/com/sun/javafx/scene/web/skin/Cut_16x16_JFX.png")));
		cut.addActionListener(this);
		add(cut);
		
		paste = new JMenuItem("Paste");
		paste.setIcon(new ImageIcon(TextContextMenu.class.getResource("/com/sun/javafx/scene/web/skin/Paste_16x16_JFX.png")));
		paste.addActionListener(this);
		add(paste);
		
		delete = new JMenuItem("Delete");
		delete.setIcon(new ImageIcon(TextContextMenu.class.getResource("/com/sun/javafx/scene/web/skin/Strikethrough_16x16_JFX.png")));
		delete.addActionListener(this);
		add(delete);
		
		copy.setEnabled(hasText);
		cut.setEnabled(hasText);
		delete.setEnabled(hasText);
		
	}

	public void actionPerformed(ActionEvent e) {
		Object src=e.getSource();
		if(src.equals(copy) || src.equals(cut)){
			String sel=txt.getSelectedText();
			if(sel==null || sel.equals("")) return;
			UtilBox.sendToClipboard(sel);
		}
		if(src.equals(cut) || src.equals(delete) || src.equals(paste)){
			int start=txt.getSelectionStart();
			int end=txt.getSelectionEnd();
			if(start==end) return;
			Document doc = txt.getDocument();
			try {
				doc.remove(start, end);
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
		}
		if(src.equals(paste)){
			String s=UtilBox.getFromClipboard();
			if(s==null) s="";
			Document doc = txt.getDocument();
			int pos=txt.getCaretPosition();
			try {
				doc.insertString(pos, s, null);
			} catch (BadLocationException ex) {
				ex.printStackTrace();
			}
		}
	}
}
