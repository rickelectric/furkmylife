package rickelectric.furkmanager.views.panels;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.FurkUserData;
import rickelectric.furkmanager.models.URI_Enums;
import rickelectric.furkmanager.network.api.API_UserData;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.swingmods.TranslucentPane;

import com.xeiam.xchart.XChartPanel;

public class Main_UserView extends TranslucentPane {
	private static final long serialVersionUID = 1L;

	private JTextField input_email, input_username, input_sdate;

	private JButton button_ChangeEmail, button_ChangePassword,
			button_VerifyEmail, button_save, button_Refresh;

	private JComboBox<String> choice_scheme, choice_host, choice_port;

	private JTabbedPane tabbedPane;

	private JSpinner spinner_linkkey, spinner_ppresults;

	private JLabel loading_box_tab1;

	private JCheckBox check_noadult;

	private JLabel saving_box_tab_3, label_saving_tab_3;

	private boolean loading = false;

	private JLabel loading_box_tab3;

	private JLabel label_loading_tab_3;

	private JPanel pane_bandwidth;

	private JPanel panel_searchdl;

	private JPanel panel_dlinks;

	private JPanel panel_search;
	private JButton btn_Maximize;

	public Main_UserView() {
		super();
		setAlpha(1);
		setPreferredSize(new Dimension(561, 400));
		setLayout(null);
		if (SettingsManager.getInstance().getMainWinMode() == SettingsManager.ENV_MODE)
			setBackground(Color.yellow);

		tabbedPane = new JTabbedPane(SwingConstants.TOP);
		tabbedPane.setBounds(4, 0, 554, 394);
		add(tabbedPane);

		JPanel pane_info = new JPanel();
		pane_info.setBackground(UtilBox.getRandomColor());
		tabbedPane.addTab("Account Info", null, pane_info, null);
		pane_info.setLayout(null);

		JPanel panel_userdetails = new JPanel();
		panel_userdetails.setOpaque(false);
		panel_userdetails.setBorder(new TitledBorder(null, "User Info",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_userdetails.setBounds(12, 12, 525, 128);
		pane_info.add(panel_userdetails);
		panel_userdetails.setLayout(null);

		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(12, 24, 114, 19);
		panel_userdetails.add(lblUsername);

		input_username = new JTextField();
		input_username.setBackground(Color.WHITE);
		input_username.setEditable(false);
		input_username.setBounds(144, 23, 235, 20);
		panel_userdetails.add(input_username);
		input_username.setColumns(10);

		JLabel lblLoginEmail = new JLabel("Email: ");
		lblLoginEmail.setBounds(12, 56, 114, 16);
		panel_userdetails.add(lblLoginEmail);

		input_email = new JTextField();
		input_email.setEditable(false);
		input_email.setBackground(Color.WHITE);
		input_email.setBounds(144, 55, 288, 20);
		panel_userdetails.add(input_email);
		input_email.setColumns(10);

		JLabel lblSignupDate = new JLabel("Date Registered:");
		lblSignupDate.setBounds(12, 84, 114, 16);
		panel_userdetails.add(lblSignupDate);

		input_sdate = new JTextField();
		input_sdate.setEditable(false);
		input_sdate.setBackground(Color.WHITE);
		input_sdate.setBounds(144, 85, 235, 20);
		panel_userdetails.add(input_sdate);
		input_sdate.setColumns(10);

		final JCheckBox chckbxVerified = new JCheckBox("Verified");
		chckbxVerified.setOpaque(false);
		chckbxVerified.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				chckbxVerified
						.setSelected(FurkUserData.User.emailVerified == 1);
			}
		});
		chckbxVerified.setSelected(true);
		chckbxVerified.setBounds(440, 54, 77, 20);
		panel_userdetails.add(chckbxVerified);

		button_ChangeEmail = new JButton("Change Email");
		button_ChangeEmail.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		button_ChangeEmail.setBounds(12, 152, 153, 20);
		pane_info.add(button_ChangeEmail);

		button_ChangePassword = new JButton("Change Password");
		button_ChangePassword.setBounds(186, 152, 153, 20);
		pane_info.add(button_ChangePassword);

		button_VerifyEmail = new JButton("Verify Email");
		button_VerifyEmail.setBounds(12, 184, 153, 20);
		if (FurkUserData.User.emailVerified == 0)
			pane_info.add(button_VerifyEmail);

		loading_box_tab1 = new JLabel("");
		loading_box_tab1.setBounds(457, 274, 80, 80);
		loading_box_tab1.setIcon(new ImageIcon(FurkManager.class
				.getResource("img/ajax-loader.gif")));
		loading_box_tab1.setVisible(false);
		pane_info.add(loading_box_tab1);

		pane_bandwidth = new JPanel();
		tabbedPane.addTab("Bandwidth Stats", null, pane_bandwidth, null);
		pane_bandwidth.setLayout(new BorderLayout());

		btn_Maximize = new JButton("Maximize");
		final JDialog wnd = new JDialog();
		wnd.setVisible(false);
		wnd.setResizable(false);
		btn_Maximize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				wnd.setBounds(GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds());
				wnd.getContentPane().setLayout(new BorderLayout());
				XChartPanel chartPanel = new XChartPanel(
						FurkUserData.BandwidthStats.getChart());
				wnd.getContentPane().add(chartPanel, BorderLayout.CENTER);
				wnd.setVisible(true);
			}
		});
		btn_Maximize.setFont(new Font("Dialog", Font.BOLD, 12));
		btn_Maximize
				.setIcon(new ImageIcon(
						Main_UserView.class
								.getResource("/rickelectric/furkmanager/img/sm/arrow_expand.png")));
		pane_bandwidth.add(btn_Maximize, BorderLayout.SOUTH);

		panel_searchdl = new JPanel();
		panel_searchdl.setBackground(UtilBox.getRandomColor());
		tabbedPane.addTab("Search & Downloads", null, panel_searchdl, "");
		panel_searchdl.setLayout(null);

		loading_box_tab3 = new JLabel("");
		loading_box_tab3.setBounds(457, 256, 66, 64);
		loading_box_tab3.setIcon(new ImageIcon(FurkManager.class
				.getResource("img/ajax-loader.gif")));
		loading_box_tab3.setVisible(false);
		panel_searchdl.add(loading_box_tab3);

		button_save = new JButton("Save Settings");
		button_save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				saving_box_tab_3.setVisible(true);
				label_saving_tab_3.setVisible(true);
				String host = choice_host.getSelectedItem().toString();
				String port = choice_port.getSelectedItem().toString();
				String scheme = choice_scheme.getSelectedItem().toString();
				int linkKey = (Integer) spinner_linkkey.getValue();

				FurkUserData.User.dlHost = URI_Enums.URI_Host.eval(host);
				FurkUserData.User.dlPort = URI_Enums.URI_Port.eval(port);
				FurkUserData.User.dlScheme = URI_Enums.URI_Scheme.eval(scheme);
				FurkUserData.User.uriKey = linkKey;

				if (check_noadult.isSelected())
					FurkUserData.User.flags = URI_Enums.Prefs_Flags.MODERATED_FULL;
				else
					FurkUserData.User.flags = URI_Enums.Prefs_Flags.NONE;

				SettingsManager.getInstance().searchResultsPerPage(
						(Integer) spinner_ppresults.getValue());

				FurkUserData.User.save(new Runnable() {
					@Override
					public void run() {
						saving_box_tab_3.setVisible(false);
						label_saving_tab_3.setVisible(false);
					}
				});

			}
		});

		saving_box_tab_3 = new JLabel("");
		saving_box_tab_3.setBounds(457, 256, 66, 64);
		saving_box_tab_3.setIcon(new ImageIcon(FurkManager.class
				.getResource("img/ajax-loader.gif")));
		saving_box_tab_3.setVisible(false);
		panel_searchdl.add(saving_box_tab_3);
		button_save.setBounds(12, 328, 131, 26);
		panel_searchdl.add(button_save);

		panel_dlinks = new JPanel();
		panel_dlinks.setOpaque(false);
		panel_dlinks.setBorder(new TitledBorder(new LineBorder(new Color(184,
				207, 229)), "Direct Download Links", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panel_dlinks.setBounds(12, 12, 525, 112);
		panel_searchdl.add(panel_dlinks);
		panel_dlinks.setLayout(null);

		JLabel lblScheme = new JLabel("Scheme: ");
		lblScheme.setBounds(12, 23, 61, 20);
		panel_dlinks.add(lblScheme);

		choice_scheme = new JComboBox<String>();
		choice_scheme.setBounds(91, 21, 160, 25);
		panel_dlinks.add(choice_scheme);
		choice_scheme.setModel(new DefaultComboBoxModel<String>(
				URI_Enums.URI_Scheme.stringValues()));

		JLabel lblDomain = new JLabel("Domain: ");
		lblDomain.setBounds(290, 23, 56, 20);
		panel_dlinks.add(lblDomain);

		choice_host = new JComboBox<String>();
		choice_host.setBounds(353, 21, 160, 25);
		panel_dlinks.add(choice_host);
		choice_host.setModel(new DefaultComboBoxModel<String>(
				URI_Enums.URI_Host.stringValues()));

		choice_port = new JComboBox<String>();
		choice_port.setBounds(91, 58, 160, 25);
		panel_dlinks.add(choice_port);
		choice_port.setModel(new DefaultComboBoxModel<String>(
				URI_Enums.URI_Port.stringValues()));

		JLabel lblPort = new JLabel("Port: ");
		lblPort.setBounds(12, 60, 61, 20);
		panel_dlinks.add(lblPort);

		spinner_linkkey = new JSpinner();
		spinner_linkkey.setToolTipText("Premium Feature: \r\n"
				+ "To reset all your direct download links, "
				+ "change this number to any other positive number. "
				+ "'0' disables this function.");
		spinner_linkkey.setBounds(353, 59, 71, 22);
		panel_dlinks.add(spinner_linkkey);
		spinner_linkkey.setModel(new SpinnerNumberModel(0, 0, 999, 1));

		JLabel lblLinkKey = new JLabel("Link Key:");
		lblLinkKey.setBounds(290, 60, 61, 20);
		panel_dlinks.add(lblLinkKey);

		panel_search = new JPanel();
		panel_search.setOpaque(false);
		panel_search.setBorder(new TitledBorder(new LineBorder(new Color(184,
				207, 229)), "Search", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		panel_search.setBounds(12, 140, 272, 151);
		panel_searchdl.add(panel_search);
		panel_search.setLayout(null);

		check_noadult = new JCheckBox("Disable Adult Content");
		check_noadult.setOpaque(false);
		check_noadult.setBounds(8, 26, 170, 24);
		panel_search.add(check_noadult);

		JLabel lblResultsPerPage = new JLabel("Results Per Page: ");
		lblResultsPerPage.setBounds(12, 58, 113, 24);
		panel_search.add(lblResultsPerPage);

		spinner_ppresults = new JSpinner();
		spinner_ppresults.setModel(new SpinnerNumberModel(20, 5, 100, 1));
		spinner_ppresults.setBounds(122, 58, 56, 24);
		panel_search.add(spinner_ppresults);

		button_Refresh = new JButton("Refresh");
		button_Refresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent a) {
				loadAccountData(500);
			}
		});
		button_Refresh.setBounds(153, 328, 131, 26);
		panel_searchdl.add(button_Refresh);

		label_saving_tab_3 = new JLabel("Saving.....");
		label_saving_tab_3.setVisible(false);
		label_saving_tab_3.setBounds(465, 328, 55, 16);
		panel_searchdl.add(label_saving_tab_3);

		label_loading_tab_3 = new JLabel("Loading.....");
		label_loading_tab_3.setVisible(false);
		label_loading_tab_3.setBounds(465, 328, 55, 16);
		panel_searchdl.add(label_loading_tab_3);

		JPanel pane_account = new JPanel();
		tabbedPane.addTab("User Account Settings", null, pane_account, null);
		pane_account.setLayout(null);

		loadAccountData(10);

	}

	public void loadAccountData(final int time) {
		if (loading)
			return;
		loading = true;
		Thread t = new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					loading_box_tab1.setVisible(true);
					loading_box_tab3.setVisible(true);
					label_loading_tab_3.setVisible(true);

					Thread.sleep(time);
					if (time > 50)
						API_UserData.loadUserData();

					input_username.setText(FurkUserData.User.login);
					input_email.setText(FurkUserData.User.email);
					input_sdate.setText(FurkUserData.User.signupDate);

					check_noadult
							.setSelected(FurkUserData.User.flags == URI_Enums.Prefs_Flags.MODERATED_FULL);
					choice_host.setSelectedItem(URI_Enums.URI_Host
							.getName(FurkUserData.User.dlHost));
					choice_port.setSelectedItem(URI_Enums.URI_Port
							.getName(FurkUserData.User.dlPort));
					choice_scheme.setSelectedItem(URI_Enums.URI_Scheme
							.getName(FurkUserData.User.dlScheme));
					spinner_linkkey.setValue(FurkUserData.User.uriKey);

					XChartPanel chartPanel = new XChartPanel(
							FurkUserData.BandwidthStats.getChart());
					pane_bandwidth.add(chartPanel, BorderLayout.CENTER);
				} catch (Exception e) {
					e.printStackTrace();
				}
				loading_box_tab1.setVisible(false);
				loading_box_tab3.setVisible(false);
				label_loading_tab_3.setVisible(false);
				loading = false;

			}
		});
		t.start();
	}
}
