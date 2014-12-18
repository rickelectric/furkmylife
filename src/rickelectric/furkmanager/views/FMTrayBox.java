package rickelectric.furkmanager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.utils.ImageManager;
import rickelectric.furkmanager.views.panels.APIMessagePanel;
import rickelectric.furkmanager.views.swingmods.JButtonLabel;
import rickelectric.furkmanager.views.swingmods.TrayWindow;
import rickelectric.img.ImageLoader;

public class FMTrayBox extends TrayWindow {
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;
	private JPanel panel_functions;
	private JLabel btn_showmm, btn_settings;
	private LoadingCircle loadingCircle;
	private JLabel btn_userSettings;

	public FMTrayBox() {
		super("Furk Manager", new Font("Dialog", Font.BOLD | Font.ITALIC, 24));
		setIconImage(new ImageIcon(ImageLoader.getInstance().getImage(
				"rickelectric.png")).getImage());
		setTitle("FurkManager - Tray Utility");
		setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);

		super.onClose(new Runnable() {
			@Override
			public void run() {
				FurkManager.logout();
				setVisible(false);
			}
		});
		super.onMinimize(new Runnable() {
			@Override
			public void run() {
				FMTrayBox.this.setVisible(false);
			}
		});

		setMoveable(true);
		setContentPane(contentPane);

		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		panel_functions = new JPanel();
		tabbedPane.addTab("Menu", new ImageIcon(ImageLoader.getInstance()
				.getImage("dash/Settings-16.png")), panel_functions, null);
		panel_functions.setLayout(null);

		btn_showmm = new JLabel("  Show Furk Manager");
		btn_showmm.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				FurkManager.appRun();
				FMTrayBox.this.setVisible(false);

			}
		});
		btn_showmm.setHorizontalAlignment(SwingConstants.LEFT);
		btn_showmm.setBounds(10, 7, 274, 40);
		panel_functions.add(btn_showmm);
		btn_showmm.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		btn_showmm.setFont(new Font("Dialog", Font.BOLD, 14));

		btn_showmm.setIcon(new ImageIcon(ImageManager.resizeImage(ImageLoader
				.getInstance().getImage("fr.png"), 32, -1)));

		btn_settings = new JLabel("  Change App Settings");
		btn_settings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				FMTrayBox.this.setVisible(false);
				FurkManager.getMainWindow().setVisible(true);
				FurkManager.getMainWindow().toFront();
				FurkManager.getMainWindow().settings();
			}
		});
		btn_settings.setIcon(new ImageIcon(ImageManager.resizeImage(ImageLoader
				.getInstance().getImage("dash/Settings-64.png"), 32, -1)));
		btn_settings.setHorizontalAlignment(SwingConstants.LEFT);
		btn_settings.setFont(new Font("Dialog", Font.BOLD, 14));
		btn_settings.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,

		null, null));
		btn_settings.setBounds(10, 58, 274, 40);
		panel_functions.add(btn_settings);

		loadingCircle = new LoadingCircle();
		loadingCircle.setBackground(Color.ORANGE);
		loadingCircle.setForeground(Color.BLACK);
		loadingCircle.setVisible(false);
		loadingCircle.setBounds(74, 170, 126, 128);
		panel_functions.add(loadingCircle);

		btn_userSettings = new JLabel("  Change User Settings");
		btn_userSettings.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseReleased(MouseEvent e) {
				FMTrayBox.this.setVisible(false);
				FurkManager.getMainWindow().setVisible(true);
				FurkManager.getMainWindow().toFront();
				FurkManager.getMainWindow().userSettings();
			}
		});
		btn_userSettings
				.setIcon(new ImageIcon(ImageManager.resizeImage(ImageLoader
						.getInstance().getImage("dash/User-64.png"), 32, -1)));
		btn_userSettings.setHorizontalAlignment(SwingConstants.LEFT);
		btn_userSettings.setFont(new Font("Dialog", Font.BOLD, 14));
		btn_userSettings.setBorder(new BevelBorder(BevelBorder.RAISED));
		btn_userSettings.setBounds(10, 109, 274, 40);
		panel_functions.add(btn_userSettings);

		JPanel panel_utils = new JPanel();
		tabbedPane.addTab("Utilities", new ImageIcon(ImageLoader.getInstance()
				.getImage("dash/Settings-16.png")), panel_utils, null);
		panel_utils.setLayout(null);

		JButtonLabel btn_showmm = new JButtonLabel("  Downloader",
				new Runnable() {
					@Override
					public void run() {
						FurkManager.downloader(true);
						FMTrayBox.this.setVisible(false);
					}
				});
		btn_showmm.setHorizontalAlignment(SwingConstants.LEFT);
		btn_showmm.setBounds(10, 7, 274, 40);
		panel_utils.add(btn_showmm);
		// btn_showmm.setBorder(new BevelBorder(BevelBorder.RAISED));
		btn_showmm.setFont(new Font("Dialog", Font.BOLD, 14));

		btn_showmm.setIcon(new ImageIcon(ImageManager.resizeImage(ImageLoader
				.getInstance().getImage("download-lg.png"), 32, -1)));

		JButtonLabel btn_settings = new JButtonLabel("  API Console",
				new Runnable() {
					@Override
					public void run() {
						FMTrayBox.this.setVisible(false);
						FurkManager.showConsole(true);
					}
				});
		btn_settings.setIcon(new ImageIcon(ImageManager.resizeImage(ImageLoader
				.getInstance().getImage("cmd.png"), 32, -1)));
		btn_settings.setHorizontalAlignment(SwingConstants.LEFT);
		btn_settings.setFont(new Font("Dialog", Font.BOLD, 14));
		// btn_settings.setBorder(new BevelBorder(BevelBorder.RAISED));
		btn_settings.setBounds(10, 58, 274, 40);
		panel_utils.add(btn_settings);

		JButtonLabel btn_userSettings = new JButtonLabel(
				"  Image Cache Viewer", new Runnable() {
					@Override
					public void run() {
						FMTrayBox.this.setVisible(false);
						FurkManager.showImgCache(true);
					}
				});
		btn_userSettings.setIcon(new ImageIcon(
				ImageManager
						.resizeImage(
								ImageLoader.getInstance().getImage(
										"tree/image-48.png"), 32, -1)));
		btn_userSettings.setHorizontalAlignment(SwingConstants.LEFT);
		btn_userSettings.setFont(new Font("Dialog", Font.BOLD, 14));
		// btn_userSettings.setBorder(new BevelBorder(BevelBorder.RAISED));
		btn_userSettings.setBounds(10, 109, 274, 40);
		panel_utils.add(btn_userSettings);

		APIMessagePanel messages = new APIMessagePanel();
		tabbedPane.addTab("Messages & Notifications", new ImageIcon(ImageLoader
				.getInstance().getImage("sm/web_view.png")), messages,
				"Notfications");

		addListeners();
	}

	public void loadNotify(boolean show) {
		loadingCircle.setVisible(show);
	}
}
