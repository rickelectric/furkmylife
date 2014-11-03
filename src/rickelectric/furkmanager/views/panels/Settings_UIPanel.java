package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.windows.MainEnvironment;
import rickelectric.furkmanager.views.windows.PrimaryEnv;

public class Settings_UIPanel extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel panel_mainInterface;
	private JRadioButton radio_windowed;
	private JRadioButton radio_env;
	private final ButtonGroup mainModeGroup = new ButtonGroup();
	private JPanel panel_theme;
	private JButton btn_Save;
	private JButton btn_revert;
	private JPanel panel_windowed;
	private JPanel panel_env;
	private JCheckBox check_slide;
	private JCheckBox check_dim;
	private JCheckBox check_onTop;
	private JCheckBox check_autohide;
	private JRadioButton rdbtnRandomColors;
	private JButton btn_InstallNew;
	private JButton btn_Save_1;

	public Settings_UIPanel() {
		setLayout(null);
		setBackground(UtilBox.getRandomColor());

		panel_mainInterface = new JPanel();
		panel_mainInterface.setBorder(new TitledBorder(new LineBorder(
				new Color(0, 0, 0)), "Main Interface Mode",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_mainInterface.setOpaque(false);
		panel_mainInterface.setBounds(10, 11, 225, 344);
		add(panel_mainInterface);
		panel_mainInterface.setLayout(null);

		btn_Save = new JButton("Save");
		btn_Save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				SettingsManager.dimEnvironment(check_dim.isSelected());
				if (getTopLevelAncestor() instanceof MainEnvironment) {
					((MainEnvironment) getTopLevelAncestor()).refreshBgc();
				}
				int newMode = radio_env.isSelected() ? SettingsManager.ENV_MODE
						: SettingsManager.WIN_MODE;
				if (newMode != SettingsManager.getMainWinMode()) {
					int opt = JOptionPane.showConfirmDialog(
							((PrimaryEnv) getTopLevelAncestor())
									.getContentPane(),
							"Main Window Mode Is About To Be Changed. Are You Sure You Want To Continue?",
							"Confirm Change", JOptionPane.YES_NO_OPTION);
					if (opt == JOptionPane.YES_OPTION) {
						SettingsManager.setMainWinMode(newMode);
						FurkManager.mWinModeChanged();
					}
				}
				SettingsManager.save();
			}
		});
		btn_Save.setBounds(19, 310, 89, 23);
		panel_mainInterface.add(btn_Save);

		btn_revert = new JButton("Revert");
		btn_revert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				windowEnvLoad();
			}
		});
		btn_revert.setBounds(118, 310, 89, 23);
		panel_mainInterface.add(btn_revert);

		panel_windowed = new JPanel();
		panel_windowed.setBorder(new TitledBorder(new LineBorder(new Color(0,
				0, 0)), "Windowed Interface Mode", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panel_windowed.setOpaque(false);
		panel_windowed.setBounds(10, 22, 205, 112);
		panel_mainInterface.add(panel_windowed);
		panel_windowed.setLayout(null);

		radio_windowed = new JRadioButton("Select");
		radio_windowed.setBounds(6, 18, 165, 23);
		panel_windowed.add(radio_windowed);
		mainModeGroup.add(radio_windowed);
		radio_windowed.setOpaque(false);

		check_slide = new JCheckBox("Slide Animations");
		check_slide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				check_slide.setSelected(true);
				// TODO Settings
			}
		});
		check_slide.setSelected(true);
		check_slide.setOpaque(false);
		check_slide.setBounds(16, 44, 155, 23);
		panel_windowed.add(check_slide);

		panel_env = new JPanel();
		panel_env.setBorder(new TitledBorder(
				new LineBorder(new Color(0, 0, 0)), "Desktop Environment Mode",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_env.setOpaque(false);
		panel_env.setBounds(10, 145, 205, 154);
		panel_mainInterface.add(panel_env);
		panel_env.setLayout(null);

		radio_env = new JRadioButton("Select");
		radio_env.setBounds(6, 19, 165, 23);
		panel_env.add(radio_env);
		mainModeGroup.add(radio_env);
		radio_env.setOpaque(false);

		check_dim = new JCheckBox("Dim Desktop When Active");
		check_dim.setOpaque(false);
		check_dim.setBounds(16, 45, 172, 23);
		panel_env.add(check_dim);

		check_onTop = new JCheckBox("Always On Top");
		check_onTop.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				check_onTop.setSelected(true);
				// TODO Settings
			}
		});
		check_onTop.setSelected(true);
		check_onTop.setOpaque(false);
		check_onTop.setBounds(16, 71, 145, 23);
		panel_env.add(check_onTop);

		check_autohide = new JCheckBox("Auto-Hide Buttons");
		check_autohide.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				check_autohide.setSelected(false);
				// TODO Settings
			}
		});
		check_autohide.setSelected(false);
		check_autohide.setOpaque(false);
		check_autohide.setBounds(16, 97, 145, 23);
		panel_env.add(check_autohide);

		panel_theme = new JPanel();
		panel_theme.setLayout(null);
		panel_theme.setOpaque(false);
		panel_theme.setBorder(new TitledBorder(new LineBorder(
				new Color(0, 0, 0)), "Themes", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panel_theme.setBounds(245, 11, 294, 344);
		add(panel_theme);
		
		rdbtnRandomColors = new JRadioButton("Color Randomization");
		rdbtnRandomColors.setOpaque(false);
		rdbtnRandomColors.setSelected(true);
		rdbtnRandomColors.setBounds(17, 28, 256, 23);
		panel_theme.add(rdbtnRandomColors);
		
		btn_InstallNew = new JButton("Add / Remove Themes");
		btn_InstallNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//TODO Manage JSON Themes
			}
		});
		btn_InstallNew.setBounds(17, 310, 157, 23);
		panel_theme.add(btn_InstallNew);
		
		btn_Save_1 = new JButton("Save");
		btn_Save_1.setBounds(184, 310, 89, 23);
		panel_theme.add(btn_Save_1);

		windowEnvLoad();
		themeLoad();
	}

	private void themeLoad() {
		// TODO Implement When #Themes# Is Added
	}

	private void windowEnvLoad() {
		int mode = SettingsManager.getMainWinMode();
		if (mode == SettingsManager.ENV_MODE)
			radio_env.setSelected(true);
		else
			radio_windowed.setSelected(true);

		check_dim.setSelected(SettingsManager.dimEnvironment());
	}
}
