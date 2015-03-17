package rickelectric.extensions.furkmanager;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JComponent;
import javax.swing.JPanel;

import rickelectric.desktop.views.windows.MainEnvironment;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.network.APIFolderManager;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.network.api.API_Label;
import rickelectric.furkmanager.network.api.API_UserData;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.panels.Main_DownloadView;
import rickelectric.furkmanager.views.panels.Main_FileView;
import rickelectric.furkmanager.views.panels.Settings_UIPanel;
import rickelectric.furkmanager.views.windows.MainWindow;
import rickelectric.img.ImageLoader;
import rickelectric.swingmods.CircleButton;

public class FurkPopup extends JPanel {
	private static final long serialVersionUID = 1L;

	public FurkPopup() {
		FurkManager.init();
		SettingsManager.getInstance().setMainWinMode(SettingsManager.ENV_MODE);
		Settings_UIPanel.lockWinMode(true);
		API_UserData.loadUserData();
		API_File.update(API_File.FINISHED, false);
		API_Label.getAll();
		APIFolderManager.init();

		setLayout(new BorderLayout());
		setPreferredSize(new Dimension(595, 540));
		setSize(getPreferredSize());

		add(MainWindow.getInstance().getContentPane(), BorderLayout.CENTER);
	}

	/**
	 * Post-Loader Function
	 */
	public static void generateRefreshButtons() {
		final CircleButton[] buttons = new CircleButton[2], refreshButtons = new CircleButton[2];
		final JComponent[] sections = new JComponent[2];

		buttons[0] = MainEnvironment.getInstance().getButton(
				"extensions.furkmanager.My Files");
		buttons[1] = MainEnvironment.getInstance().getButton(
				"extensions.furkmanager.My Downloads");

		sections[0] = MainEnvironment.getInstance().getBalloonContents(
				"extensions.furkmanager.My Files");
		sections[1] = MainEnvironment.getInstance().getBalloonContents(
				"extensions.furkmanager.My Downloads");

		refreshButtons[0] = new CircleButton("Refresh Files", ImageLoader
				.getInstance().getImage("dash/Reload-32.png"));
		refreshButtons[0].setStrokeSize(3);
		Point loc0 = buttons[0].getLocation();
		Dimension sz0 = buttons[0].getSize();
		refreshButtons[0].setLocation(
				loc0.x + sz0.width - refreshButtons[0].getWidth(), loc0.y
						+ sz0.height - refreshButtons[0].getHeight());

		refreshButtons[1] = new CircleButton("Refresh Downloads", ImageLoader
				.getInstance().getImage("dash/Reload-32.png"));
		refreshButtons[1].setStrokeSize(3);
		Point loc1 = buttons[1].getLocation();
		Dimension sz1 = buttons[1].getSize();
		refreshButtons[1].setLocation(
				loc1.x + sz1.width - refreshButtons[1].getWidth(), loc1.y
						+ sz1.height - refreshButtons[1].getHeight());

		Runnable[] refreshRunners = new Runnable[] { new Runnable() {// File
																		// Refresh
					public void run() {
						((Main_FileView) sections[0]).refreshActive(true);
					}
				}, new Runnable() {// Dls Refresh
					public void run() {
						((Main_DownloadView) sections[1]).refreshActive(true);
					}
				} };

		for (int i = 0; i < refreshButtons.length; i++) {
			if (refreshButtons[i] != null) {
				buttons[i].tetherButton(refreshButtons[i]);
				MainEnvironment.getInstance().addItemPair(
						"extensions.furkmanager."
								+ refreshButtons[i].getPopup(),
						refreshButtons[i], null);
				MainEnvironment.getInstance().addRunner(
						"extensions.furkmanager."
								+ refreshButtons[i].getPopup(),
						refreshRunners[i]);
			}
		}
	}

}
