package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import rickelectric.UtilBox;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.network.FurkBridge;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.swingmods.TranslucentPane;
import rickelectric.furkmanager.views.windows.MainEnvironment;
import rickelectric.furkmanager.views.windows.MainWindow;

public class Main_SettingsView extends TranslucentPane {
	private static final long serialVersionUID = 1L;
	private JTextField input_api_base;
	private JTextField input_dwFolder;
	private JButton button_browse;
	private JCheckBox check_dwAsk;
	private JCheckBox check_multipart;
	private JSpinner spinner_buffsize;
	private JButton button_dfsave;
	private JPanel panel_idm;
	private JCheckBox check_idm;
	private JTextField input_idmexe;
	private JButton btn_idmbrowse;
	private JButton btn_idmdetect;
	private JButton btn_optsave;
	private JButton btn_idmSave;
	private JTabbedPane tabbedPane;

	public Main_SettingsView() {
		super();
		setAlpha(1);
		setPreferredSize(new Dimension(561, 400));
		setLayout(null);
		if (SettingsManager.getInstance().getMainWinMode() == SettingsManager.ENV_MODE)
			setBackground(Color.lightGray);

		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setBounds(4, 0, 554, 394);
		add(tabbedPane);

		JPanel pane_connection = new Settings_ProxyPorts();
		tabbedPane.addTab("Connection", null, pane_connection, null);

		JPanel panel_downloader = new JPanel();
		panel_downloader.setBackground(UtilBox.getInstance().getRandomColor());
		tabbedPane.addTab("Downloader", null, panel_downloader, null);
		panel_downloader.setLayout(null);

		JPanel panel_dwfolder = new JPanel();
		panel_dwfolder.setOpaque(false);
		panel_dwfolder.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Default Folder", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_dwfolder.setBounds(12, 12, 525, 105);
		panel_downloader.add(panel_dwfolder);
		panel_dwfolder.setLayout(null);

		JLabel lblDefaultFolder = new JLabel("Default Destination Folder:");
		lblDefaultFolder.setBounds(11, 18, 211, 16);
		panel_dwfolder.add(lblDefaultFolder);

		input_dwFolder = new JTextField();
		input_dwFolder.setFont(new Font("Dialog", Font.BOLD | Font.ITALIC, 12));
		input_dwFolder.setBackground(Color.WHITE);
		input_dwFolder.setEditable(false);
		input_dwFolder.setBounds(11, 35, 392, 20);
		panel_dwfolder.add(input_dwFolder);
		input_dwFolder.setText(SettingsManager.getInstance().getDownloadFolder());
		input_dwFolder.setColumns(10);

		check_dwAsk = new JCheckBox("Ask Before Every Download");
		check_dwAsk.setBounds(8, 64, 211, 20);
		panel_dwfolder.add(check_dwAsk);
		check_dwAsk.setOpaque(false);
		check_dwAsk.setSelected(true);

		button_browse = new JButton("Browse...");
		button_browse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String f = UtilBox.getInstance().openFile(SettingsManager.getInstance().getDownloadFolder());
				if (f != null && !f.equals(""))
					input_dwFolder.setText(f);
			}
		});
		button_browse.setBounds(415, 35, 98, 20);
		panel_dwfolder.add(button_browse);

		button_dfsave = new JButton("Save");
		button_dfsave.addActionListener(new ActionListener() {
			boolean action = false;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (action)
					return;
				action = true;
				button_dfsave.setText("Saving");
				SettingsManager.getInstance().setDownloadFolder(input_dwFolder.getText());
				SettingsManager.getInstance().askFolderOnDownload(check_dwAsk.isSelected());
				SettingsManager.save();
				new Thread(new Runnable() {
					@Override
					public void run() {
						Color c = button_dfsave.getForeground();
						Color b = button_dfsave.getBackground();

						button_dfsave.setForeground(Color.GREEN);
						button_dfsave.setBackground(Color.BLACK);

						button_dfsave.setText("Saved");
						try {
							Thread.sleep(2000);
						} catch (Exception e) {
						}

						button_dfsave.setText("Save");
						button_dfsave.setForeground(c);
						button_dfsave.setBackground(b);

						action = false;
					}
				}).start();
			}
		});
		button_dfsave.setBounds(415, 67, 98, 20);
		panel_dwfolder.add(button_dfsave);

		JPanel panel_dwopt = new JPanel();
		panel_dwopt.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "Optimization (Internal Downloader)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_dwopt.setOpaque(false);
		panel_dwopt.setBounds(12, 129, 204, 128);
		panel_downloader.add(panel_dwopt);
		panel_dwopt.setLayout(null);

		JLabel lblBufferSize = new JLabel("Buffer Size (bytes): ");
		lblBufferSize.setBounds(12, 15, 119, 16);
		panel_dwopt.add(lblBufferSize);

		spinner_buffsize = new JSpinner();
		spinner_buffsize.setModel(new SpinnerNumberModel(SettingsManager
				.getInstance().downloadBuffer(), 8, 524288, 8));
		spinner_buffsize.setBounds(12, 29, 68, 20);
		panel_dwopt.add(spinner_buffsize);

		check_multipart = new JCheckBox("Use Multipart Mode");
		check_multipart.setEnabled(false);
		check_multipart.setOpaque(false);
		check_multipart.setBounds(12, 56, 161, 24);
		panel_dwopt.add(check_multipart);

		btn_optsave = new JButton("Save");
		btn_optsave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int val = (Integer) spinner_buffsize.getValue();
				SettingsManager.getInstance().downloadBuffer(val);
				SettingsManager.save();
			}
		});
		btn_optsave.setBounds(12, 87, 98, 20);
		panel_dwopt.add(btn_optsave);

		panel_idm = new JPanel();
		panel_idm.setLayout(null);
		panel_idm.setOpaque(false);
		panel_idm.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)), "External Downloader (IDM)", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_idm.setBounds(226, 129, 311, 128);
		panel_downloader.add(panel_idm);

		check_idm = new JCheckBox("Enable IDM");
		check_idm.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean b = check_idm.isSelected();
				if (!b)
					input_idmexe.setText("");
				input_idmexe.setEnabled(b);
				btn_idmbrowse.setEnabled(b);
				btn_idmdetect.setEnabled(b);
				btn_idmSave.setEnabled(b);
			}
		});
		check_idm.setOpaque(false);
		check_idm.setBounds(6, 20, 97, 23);
		panel_idm.add(check_idm);

		input_idmexe = new JTextField();
		input_idmexe.setEditable(false);
		input_idmexe.setBounds(6, 60, 282, 20);
		panel_idm.add(input_idmexe);
		input_idmexe.setColumns(10);

		JLabel lblPathToExecutable = new JLabel("Path To Executable");
		lblPathToExecutable.setBounds(10, 44, 120, 14);
		panel_idm.add(lblPathToExecutable);

		btn_idmbrowse = new JButton("Browse...");
		btn_idmbrowse.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String f = UtilBox.getInstance().openFile(
						"IDMExecutable",
						((SettingsManager.getInstance().getMainWinMode() == SettingsManager.ENV_MODE ? (MainEnvironment) FurkManager
								.getMainWindow() : (MainWindow) FurkManager
								.getMainWindow())));
				if (f != null && !f.equals(""))
					input_idmexe.setText(f);
			}
		});
		btn_idmbrowse.setBounds(205, 91, 83, 20);
		panel_idm.add(btn_idmbrowse);

		btn_idmdetect = new JButton("Detect");
		btn_idmdetect.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String idmPath = SettingsManager.getInstance().checkPaths();
				if (idmPath != null) {
					input_idmexe.setText(idmPath);
				} else {
					JOptionPane.showMessageDialog(null, "IDM Not Detected");
					btn_idmdetect.setEnabled(false);
				}
			}
		});
		btn_idmdetect.setBounds(106, 91, 83, 20);
		panel_idm.add(btn_idmdetect);

		btn_idmSave = new JButton("Save");
		btn_idmSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsManager.getInstance().idmPath(input_idmexe.getText());
				SettingsManager.save();
			}
		});
		btn_idmSave.setBounds(6, 91, 83, 20);
		panel_idm.add(btn_idmSave);

		JPanel panel = new JPanel();
		panel.setBackground(UtilBox.getInstance().getRandomColor());
		panel.setLayout(null);
		tabbedPane.addTab("API Access", null, panel, null);

		JLabel label = new JLabel("API Base URL: ");
		label.setBounds(12, 14, 139, 16);
		panel.add(label);

		input_api_base = new JTextField();
		input_api_base.setBackground(Color.WHITE);
		input_api_base.setEditable(false);
		input_api_base.setFont(new Font("Dialog", Font.BOLD, 12));
		input_api_base.setText(FurkBridge.API_BASE());
		input_api_base.setColumns(10);
		input_api_base.setBounds(169, 12, 285, 20);
		panel.add(input_api_base);

		JLabel lblMilliseconds = new JLabel("milliseconds");
		lblMilliseconds.setBounds(248, 44, 92, 16);
		panel.add(lblMilliseconds);

		JSpinner spinner_timeout = new JSpinner();
		spinner_timeout.setModel(new SpinnerNumberModel(10000, 3000, 30000, 100));
		spinner_timeout.setFont(new Font("Dialog", Font.BOLD, 12));
		spinner_timeout.setBounds(169, 42, 69, 20);
		panel.add(spinner_timeout);

		JLabel label_2 = new JLabel("Connection Timeout: ");
		label_2.setBounds(12, 44, 139, 16);
		panel.add(label_2);

		JPanel pane_ui = new Settings_UIPanel();
		tabbedPane.addTab("User Interface", null, pane_ui, null);
		pane_ui.setLayout(null);

		load();
	}

	private void load() {
		input_dwFolder.setText(SettingsManager.getInstance().getDownloadFolder());
		check_dwAsk.setSelected(SettingsManager.getInstance().askFolderOnDownload());

		check_idm.setSelected(SettingsManager.getInstance().idm());
		input_idmexe.setText(SettingsManager.getInstance().idmPath());

		spinner_buffsize.setValue(SettingsManager.getInstance().downloadBuffer());

	}
}
