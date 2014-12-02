package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
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
	private JButton btn_addRemoveThemes;
	private JButton btn_saveThemes;
	private JCheckBox check_uieffects;

	public Settings_UIPanel() {
		// setPreferredSize(new Dimension(245, 363));
		setPreferredSize(new Dimension(245, 363));
		setSize(new Dimension(242, 363));

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
			@Override
			public void actionPerformed(ActionEvent e) {
				SettingsManager.getInstance().dimEnvironment(
						check_dim.isSelected());
				SettingsManager.getInstance().autoHideEnvrionment(
						check_autohide.isSelected());
				SettingsManager.getInstance().slideDashWin(
						check_slide.isSelected());

				if (getTopLevelAncestor() instanceof MainEnvironment) {
					((MainEnvironment) getTopLevelAncestor()).refreshBgc();
				}
				int newMode = radio_env.isSelected() ? SettingsManager.ENV_MODE
						: SettingsManager.WIN_MODE;
				if (newMode != SettingsManager.getInstance().getMainWinMode()) {
					int opt;
					try {
						opt = JOptionPane.showConfirmDialog(
								((PrimaryEnv) getTopLevelAncestor())
										.getContentPane(),
								"Main Window Mode Is About To Be Changed. Are You Sure You Want To Continue?",
								"Confirm Change", JOptionPane.YES_NO_OPTION);
						if (opt == JOptionPane.YES_OPTION) {
							SettingsManager.getInstance().setMainWinMode(
									newMode);
							FurkManager.mWinModeChanged();
						}
					} catch (ClassCastException ex) {
						SettingsManager.getInstance().setMainWinMode(newMode);
					}
				}
				SettingsManager.save();
			}
		});
		btn_Save.setBounds(19, 310, 89, 23);
		panel_mainInterface.add(btn_Save);

		btn_revert = new JButton("Revert");
		btn_revert.addActionListener(new ActionListener() {
			@Override
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
		panel_windowed.setBounds(10, 15, 205, 109);
		panel_mainInterface.add(panel_windowed);
		panel_windowed.setLayout(null);

		radio_windowed = new JRadioButton("Select");
		radio_windowed.setBounds(6, 18, 165, 23);
		panel_windowed.add(radio_windowed);
		mainModeGroup.add(radio_windowed);
		radio_windowed.setOpaque(false);

		check_slide = new JCheckBox("Slide Animations");
		check_slide.setSelected(true);
		check_slide.setOpaque(false);
		check_slide.setBounds(16, 44, 155, 23);
		panel_windowed.add(check_slide);

		panel_env = new JPanel();
		panel_env.setBorder(new TitledBorder(
				new LineBorder(new Color(0, 0, 0)), "Desktop Environment Mode",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_env.setOpaque(false);
		panel_env.setBounds(10, 135, 205, 164);
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
			@Override
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
		check_autohide.setSelected(false);
		check_autohide.setOpaque(false);
		check_autohide.setBounds(16, 97, 145, 23);
		panel_env.add(check_autohide);

		check_uieffects = new JCheckBox("Enable UI Effects");
		check_uieffects.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				check_uieffects.setSelected(false);
				// TODO Settings
			}
		});
		check_uieffects.setSelected(false);
		check_uieffects.setOpaque(false);
		check_uieffects.setBounds(16, 123, 145, 23);
		panel_env.add(check_uieffects);

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

		btn_addRemoveThemes = new JButton("Add / Remove Themes");
		btn_addRemoveThemes.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Manage JSON Themes
			}
		});
		btn_addRemoveThemes.setBounds(17, 310, 157, 23);
		panel_theme.add(btn_addRemoveThemes);

		btn_saveThemes = new JButton("Save");
		btn_saveThemes.setBounds(184, 310, 89, 23);
		panel_theme.add(btn_saveThemes);

		windowEnvLoad();
		themeLoad();
	}

	private void themeLoad() {
		// TODO Implement When #Themes# Is Added
	}

	private void windowEnvLoad() {
		int mode = SettingsManager.getInstance().getMainWinMode();
		if (mode == SettingsManager.ENV_MODE)
			radio_env.setSelected(true);
		else
			radio_windowed.setSelected(true);

		check_dim.setSelected(SettingsManager.getInstance().dimEnvironment());
		check_autohide.setSelected(SettingsManager.getInstance()
				.autoHideEnvrionment());

		check_slide.setSelected(SettingsManager.getInstance().slideDashWin());
	}

	public void closeOnSave(final JDialog sf) {
		btn_Save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sf.dispose();
			}
		});
	}
}
