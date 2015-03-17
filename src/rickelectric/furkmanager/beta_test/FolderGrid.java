package rickelectric.furkmanager.beta_test;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

public class FolderGrid extends JPanel implements ComponentListener {
	private static final long serialVersionUID = 1L;
	private GridLayout grid;

	public FolderGrid() {
		grid = new GridLayout(0,10);
		setLayout(grid);
		setBackground(Color.white);
		setPreferredSize(new Dimension(500, 500));
		setSize(getPreferredSize());
		addComponentListener(this);
		populate();
	}
	
	public void refresh(){
		grid.setColumns((int) Math.floor(getWidth()/80));
		repaint();
	}

	public void populate() {
		for (int i = 0; i < 10; i++) {
			FolderIcon fo = new FolderIcon("Icon " + i);
			this.add(fo);
		}
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void componentMoved(ComponentEvent arg0) {
		refresh();
	}

	@Override
	public void componentResized(ComponentEvent arg0) {
		refresh();
	}

	@Override
	public void componentShown(ComponentEvent arg0) {
		refresh();
	}
}
