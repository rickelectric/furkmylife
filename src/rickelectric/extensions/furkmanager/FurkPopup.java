package rickelectric.extensions.furkmanager;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JPanel;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.network.APIFolderManager;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.network.api.API_Label;
import rickelectric.furkmanager.network.api.API_UserData;
import rickelectric.furkmanager.views.windows.MainWindow;

public class FurkPopup extends JPanel {
	private static final long serialVersionUID = 1L;

	public FurkPopup() {
		FurkManager.init();
		API_UserData.loadUserData();
		API_File.getAllFinished();
		API_Label.getAll();
		APIFolderManager.init();

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(595, 540));
		setSize(getPreferredSize());

		add(MainWindow.getInstance().getContentPane(), BorderLayout.CENTER);
	}

}
