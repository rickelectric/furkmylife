package rickelectric.furkmanager.beta_test;

import javax.swing.JFrame;

public class FolderTestWin extends JFrame{
	private static final long serialVersionUID = 1L;

	public static void main(String[] args){
		JFrame ft = new FolderTestWin();
		ft.setDefaultCloseOperation(EXIT_ON_CLOSE);
		ft.setVisible(true);
	}
	
	public FolderTestWin(){
		FolderGrid f = new FolderGrid();
		getContentPane().add(f);
		//getContentPane().add(Box.createGlue());
		f.refresh();
		pack();
		setLocationRelativeTo(null);
	}
}
