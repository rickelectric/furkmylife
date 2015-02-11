package rickelectric.swingmods;


import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTextField;
import javax.swing.text.Document;

public class ConTextField extends JTextField implements MouseListener {
	private static final long serialVersionUID = 1L;

	public ConTextField() {
		super();
		addMouseListener(this);
	}

	public ConTextField(Document doc, String text, int columns) {
		super(doc, text, columns);
		addMouseListener(this);
	}

	public ConTextField(int columns) {
		super(columns);
		addMouseListener(this);
	}

	public ConTextField(String text, int columns) {
		super(text, columns);
		addMouseListener(this);
	}

	public ConTextField(String text) {
		super(text);
		addMouseListener(this);
	}

	public void mouseClicked(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3 && e.getClickCount() == 1) {
			new TextContextMenu(this)
					.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

}
