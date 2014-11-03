package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Proxy;
import java.net.Proxy.Type;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;

public class Settings_ProxyPorts extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel panel_proxy;
	private JTextField input_prox_ip;
	private JTextField input_prox_port;
	private JTextField input_prox_username;
	private JPasswordField input_prox_password;
	private JCheckBox check_proxy;
	private JButton button_prox_save;
	private JButton button_prox_revert;
	private JTextField input_loopback;
	private JTextField input_chrome;
	private JButton button_ext_save;
	private JButton button_ext_revert;
	private JPanel panel_socks;
	private JCheckBox check_tunnel;
	private JLabel lblProxyType;
	private JComboBox<Type> choice_proxyType;

	public Settings_ProxyPorts() {
		setPreferredSize(new Dimension(336, 314));

		setBackground(UtilBox.getRandomColor());
		setLayout(null);

		panel_proxy = new JPanel();
		panel_proxy.setOpaque(false);
		panel_proxy.setBorder(new TitledBorder(new LineBorder(new Color(184,
				207, 229)), "Proxy", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		panel_proxy.setBounds(12, 12, 312, 282);
		add(panel_proxy);
		panel_proxy.setLayout(null);

		check_proxy = new JCheckBox("Proxy Required");
		check_proxy.setFont(new Font("Dialog", Font.BOLD, 12));
		check_proxy.setOpaque(false);
		check_proxy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				boolean sel = check_proxy.isSelected();
				proxyEnabled(sel);
			}
		});
		check_proxy.setBounds(10, 23, 128, 20);
		panel_proxy.add(check_proxy);

		JLabel lblHostIp = new JLabel("Host IP: ");
		lblHostIp.setBounds(14, 55, 82, 20);
		panel_proxy.add(lblHostIp);

		input_prox_ip = new JTextField();
		input_prox_ip.setFont(new Font("Dialog", Font.BOLD, 12));
		input_prox_ip.setBounds(114, 55, 186, 20);
		panel_proxy.add(input_prox_ip);
		input_prox_ip.setColumns(10);

		input_prox_port = new JTextField();
		input_prox_port.setBounds(114, 80, 80, 20);
		panel_proxy.add(input_prox_port);
		input_prox_port.setColumns(10);

		JLabel lblPort = new JLabel("Port:");
		lblPort.setBounds(14, 80, 82, 20);
		panel_proxy.add(lblPort);

		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(14, 152, 82, 20);
		panel_proxy.add(lblUsername);

		input_prox_username = new JTextField();
		input_prox_username.setBounds(114, 152, 186, 20);
		panel_proxy.add(input_prox_username);
		input_prox_username.setColumns(10);

		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setBounds(14, 177, 82, 20);
		panel_proxy.add(lblPassword);

		input_prox_password = new JPasswordField();
		input_prox_password.setBounds(114, 177, 186, 20);
		panel_proxy.add(input_prox_password);

		button_prox_save = new JButton("Save");
		button_prox_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				SettingsManager.enableProxy(check_proxy.isSelected());
				SettingsManager.setProxy(
						Proxy.Type.values()[choice_proxyType.getSelectedIndex()],
						input_prox_ip.getText(), input_prox_port.getText(),
						input_prox_username.getText(),
						UtilBox.charToString(input_prox_password.getPassword()));
				SettingsManager.useTunnel(check_tunnel.isSelected());
				SettingsManager.save();
			}
		});
		button_prox_save.setBounds(212, 209, 88, 20);
		panel_proxy.add(button_prox_save);

		button_prox_revert = new JButton("Revert");
		button_prox_revert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				proxLoad();
			}
		});
		button_prox_revert.setBounds(114, 209, 88, 20);
		panel_proxy.add(button_prox_revert);

		check_tunnel = new JCheckBox("Enable Tunneled API");
		check_tunnel.setFont(new Font("Dialog", Font.BOLD, 12));
		check_tunnel.setOpaque(false);
		check_tunnel.setBounds(140, 22, 160, 23);
		check_tunnel.setSelected(SettingsManager.useTunnel());
		panel_proxy.add(check_tunnel);

		lblProxyType = new JLabel("Proxy Type: ");
		lblProxyType.setBounds(14, 114, 72, 14);
		panel_proxy.add(lblProxyType);

		choice_proxyType = new JComboBox<Proxy.Type>();
		choice_proxyType.setModel(new DefaultComboBoxModel<Proxy.Type>(Type
				.values()));
		choice_proxyType.setBounds(114, 111, 188, 20);
		panel_proxy.add(choice_proxyType);

		panel_socks = new JPanel();
		panel_socks.setOpaque(false);
		panel_socks.setBorder(new TitledBorder(new LineBorder(new Color(184,
				207, 229)), "Extension Ports", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panel_socks.setBounds(336, 12, 201, 172);
		add(panel_socks);
		panel_socks.setLayout(null);

		JLabel lblChromeExtension = new JLabel("Loopback Port: ");
		lblChromeExtension.setBounds(12, 27, 115, 16);
		panel_socks.add(lblChromeExtension);

		JLabel lblChromeExtensionPort = new JLabel("Chrome Extension Port:");
		lblChromeExtensionPort.setBounds(12, 76, 185, 16);
		panel_socks.add(lblChromeExtensionPort);

		input_loopback = new JTextField();
		input_loopback.setBounds(12, 44, 100, 20);
		panel_socks.add(input_loopback);
		input_loopback.setColumns(10);

		input_chrome = new JTextField();
		input_chrome.setEditable(false);
		input_chrome.setColumns(10);
		input_chrome.setBounds(12, 92, 100, 20);
		panel_socks.add(input_chrome);

		button_ext_save = new JButton("Save");
		button_ext_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int local = SettingsManager.getLocalPort();
				try {
					local = Integer.parseInt(input_loopback.getText());
				} catch (Exception ex) {
				}
				int websock = SettingsManager.getWebSockPort();
				try {
					websock = Integer.parseInt(input_chrome.getText());
				} catch (Exception ex) {
				}
				SettingsManager.setPorts(local, websock);
				SettingsManager.save();
			}
		});
		button_ext_save.setBounds(106, 124, 82, 20);
		panel_socks.add(button_ext_save);

		button_ext_revert = new JButton("Revert");
		button_ext_revert.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				extportLoad();
			}
		});
		button_ext_revert.setBounds(12, 124, 82, 20);
		panel_socks.add(button_ext_revert);

		proxLoad();
		extportLoad();

	}

	protected void proxyEnabled(boolean sel) {
		check_proxy.setSelected(sel);
		input_prox_ip.setEnabled(sel);
		input_prox_port.setEnabled(sel);
		choice_proxyType.setEnabled(sel);
		input_prox_username.setEnabled(sel);
		input_prox_password.setEnabled(sel);

	}

	private void proxLoad(){
		input_prox_ip.setText(SettingsManager.getProxyHost());
		input_prox_port.setText(SettingsManager.getProxyPort());
		
		for(int i=0;i<Type.values().length;i++){
			if(Type.values()[i].equals(SettingsManager.getProxyType())){
				choice_proxyType.setSelectedIndex(i);
				break;
			}
		}
		
		input_prox_username.setText(SettingsManager.getProxyUser());
		input_prox_password.setText(SettingsManager.getProxyPassword());
		proxyEnabled(SettingsManager.proxyEnabled());
	}

	private void extportLoad() {
		input_chrome.setText("" + SettingsManager.getWebSockPort());
		input_loopback.setText("" + SettingsManager.getLocalPort());
	}

	public void extDisable() {
		input_loopback.setEnabled(false);
		input_chrome.setEnabled(false);
		button_ext_revert.setEnabled(false);
		button_ext_save.setEnabled(false);
	}

	public void closeOnSave(final JDialog sf) {
		button_prox_save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				sf.dispose();
			}
		});
	}
}
