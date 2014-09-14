package rickelectric.furkmanager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.utils.ImageManager;

public class FMTrayBox extends TrayWindow {
	private static final long serialVersionUID = 1L;

	private JTabbedPane tabbedPane;
	private JPanel panel_functions, panel_stats;
	private JLabel label_1, btn_showmm;
	private JLabel btn_settings;
	private LoadingCircle loadingCircle;

	public FMTrayBox() {
		super("Movie Manager", new Font("Dialog", Font.BOLD | Font.ITALIC, 24));
		setIconImage(new ImageIcon(
				FurkManager.class.getResource("img/rickelectric.png")).getImage());
		setTitle("Movie Manager - Tray Utility");
		setModalExclusionType(ModalExclusionType.TOOLKIT_EXCLUDE);
		
		super.onClose(new Runnable() {
			public void run() {
				FurkManager.exit();
			}
		});
		super.onMinimize(new Runnable() {
			public void run() {
				FMTrayBox.this.setVisible(false);
			}
		});
		
		setMoveable(true);
		setContentPane(contentPane);

		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane, BorderLayout.CENTER);

		panel_functions = new JPanel();
		tabbedPane
				.addTab("Menu",
						new ImageIcon(
								FMTrayBox.class
										.getResource("/rickelectric/furkmanager/img/dash/Settings-16.png")),
						panel_functions, null);
		panel_functions.setLayout(null);

		btn_showmm = new JLabel("Show Furk Manager");
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
		try {
			btn_showmm
					.setIcon(new ImageIcon(
							ImageManager.resizeImage(
									ImageIO.read(FMTrayBox.class
											.getResource("/rickelectric/furkmanager/img/fr.png")),
									32, -1)));

			btn_settings = new JLabel("Change Settings");
			btn_settings.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseReleased(MouseEvent e) {
					FMTrayBox.this.setVisible(false);
					FurkManager.getMainWindow().setVisible(true);
					FurkManager.getMainWindow().toFront();
					FurkManager.getMainWindow().settings();
				}
			});
			btn_settings
					.setIcon(new ImageIcon(
							ImageManager.resizeImage(
									ImageIO.read(FMTrayBox.class
											.getResource("/rickelectric/furkmanager/img/dash/Settings-64.png")),
									32, -1)));
			btn_settings.setHorizontalAlignment(SwingConstants.LEFT);
			btn_settings.setFont(new Font("Dialog", Font.BOLD, 14));
			btn_settings.setBorder(new BevelBorder(BevelBorder.RAISED, null,
					null,

					null, null));
			btn_settings.setBounds(10, 58, 274, 40);
			panel_functions.add(btn_settings);

			loadingCircle = new LoadingCircle();
			loadingCircle.setBackground(Color.ORANGE);
			loadingCircle.setForeground(Color.BLACK);
			loadingCircle.setVisible(false);
			loadingCircle.setBounds(74, 170, 126, 128);
			panel_functions.add(loadingCircle);
			
			tabbedPane
					.addTab("Quick Search",
							new ImageIcon(
									FMTrayBox.class
											.getResource("/rickelectric/furkmanager/img/sm/web_view.png")),
							new JPanel(), null);

			panel_stats = new JPanel();
			tabbedPane
					.addTab("Stats",
							new ImageIcon(
									FMTrayBox.class
											.getResource("/rickelectric/furkmanager/img/tree/text-16.png")),
							panel_stats, null);
			panel_stats.setLayout(new BorderLayout(0, 0));

			label_1 = new JLabel("");
			label_1.setIcon(new ImageIcon(
					FMTrayBox.class
							.getResource("/rickelectric/furkmanager/img/ajax-loader.gif")));
			label_1.setHorizontalAlignment(SwingConstants.CENTER);
			panel_stats.add(label_1, BorderLayout.CENTER);
		} catch (IOException e) {
			e.printStackTrace();
		}
		addListeners();
	}

	public void loadNotify(boolean show) {
		loadingCircle.setVisible(show);
	}
}
