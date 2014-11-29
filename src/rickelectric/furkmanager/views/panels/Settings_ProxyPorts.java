package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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

import rickelectric.furkmanager.utils.ProxySettings;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;
import java.awt.event.ItemListener;
import java.awt.event.ItemEvent;

public class Settings_ProxyPorts extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel panel_proxy;
	private JTextField input_prox_ip;
	private JTextField input_prox_port;
	private JTextField input_prox_username;
	private JPasswordField input_prox_password;
	private JButton button_prox_save;
	private JButton button_prox_revert;
	private JTextField input_loopback;
	private JTextField input_chrome;
	private JButton button_ext_save;
	private JButton button_ext_revert;
	private JPanel panel_socks;
	private JCheckBox check_tunnel;
	private JLabel lblProxyType;
	private JComboBox<ProxySettings.Type> choice_proxyType;
	private JLabel lblDomain;
	private JTextField input_prox_domain;

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
		
		lblDomain = new JLabel("Domain: ");
		lblDomain.setToolTipText("Windows Domain Name (For ISA or NTLM Proxy Servers): ");
		lblDomain.setBounds(14, 132, 82, 20);
		panel_proxy.add(lblDomain);
		
		input_prox_domain = new JTextField();
		input_prox_domain.setToolTipText("Windows Domain Name (For ISA or NTLM Proxy Servers): ");
		input_prox_domain.setEnabled(false);
		input_prox_domain.setColumns(10);
		input_prox_domain.setBounds(114, 132, 186, 20);
		panel_proxy.add(input_prox_domain);

		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setBounds(14, 163, 82, 20);
		panel_proxy.add(lblUsername);

		input_prox_username = new JTextField();
		input_prox_username.setBounds(114, 163, 186, 20);
		panel_proxy.add(input_prox_username);
		input_prox_username.setColumns(10);

		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setBounds(14, 194, 82, 20);
		panel_proxy.add(lblPassword);

		input_prox_password = new JPasswordField();
		input_prox_password.setBounds(114, 194, 186, 20);
		panel_proxy.add(input_prox_password);

		button_prox_save = new JButton("Save");
		button_prox_save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ProxySettings ps = SettingsManager.getInstance().getProxySettings();
				
				ps.proxyType = ProxySettings.Type.values()[choice_proxyType.getSelectedIndex()];
				ps.HOST = input_prox_ip.getText();
				ps.PORT = Integer.parseInt(input_prox_port.getText());
				
				ps.DOMAIN = input_prox_domain.getText();
				ps.USER = input_prox_username.getText();
				ps.PASS = UtilBox.charToString(input_prox_password.getPassword());
				SettingsManager.getInstance().useTunnel(check_tunnel.isSelected());
				SettingsManager.save();
			}
		});
		button_prox_save.setBounds(212, 225, 88, 20);
		panel_proxy.add(button_prox_save);

		button_prox_revert = new JButton("Revert");
		button_prox_revert.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				proxLoad();
			}
		});
		button_prox_revert.setBounds(114, 225, 88, 20);
		panel_proxy.add(button_prox_revert);

		check_tunnel = new JCheckBox("Enable Tunneled API");
		check_tunnel.setFont(new Font("Dialog", Font.BOLD, 12));
		check_tunnel.setOpaque(false);
		check_tunnel.setBounds(14, 252, 160, 23);
		check_tunnel.setSelected(SettingsManager.getInstance().useTunnel());
		panel_proxy.add(check_tunnel);

		lblProxyType = new JLabel("Proxy Type: ");
		lblProxyType.setBounds(10, 22, 72, 14);
		panel_proxy.add(lblProxyType);

		choice_proxyType = new JComboBox<ProxySettings.Type>();
		choice_proxyType.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				proxyEnabled(choice_proxyType.getSelectedItem()!=ProxySettings.Type.NONE);
			}
		});
		choice_proxyType.setModel(new DefaultComboBoxModel<ProxySettings.Type>(ProxySettings.Type
				.values()));
		choice_proxyType.setBounds(114, 19, 186, 20);
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
			@Override
			public void actionPerformed(ActionEvent e) {
				int local = SettingsManager.getInstance().getLocalPort();
				try {
					local = Integer.parseInt(input_loopback.getText());
				} catch (Exception ex) {
				}
				int websock = SettingsManager.getInstance().getWebSockPort();
				try {
					websock = Integer.parseInt(input_chrome.getText());
				} catch (Exception ex) {
				}
				SettingsManager.getInstance().setPorts(local, websock);
				SettingsManager.save();
			}
		});
		button_ext_save.setBounds(106, 124, 82, 20);
		panel_socks.add(button_ext_save);

		button_ext_revert = new JButton("Revert");
		button_ext_revert.addActionListener(new ActionListener() {
			@Override
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
		input_prox_ip.setEnabled(sel);
		input_prox_port.setEnabled(sel);
		
		input_prox_domain.setEnabled(sel && choice_proxyType.getSelectedItem()==ProxySettings.Type.NTLM);
		input_prox_username.setEnabled(sel);
		input_prox_password.setEnabled(sel);

	}

	private void proxLoad(){
		ProxySettings ps = SettingsManager.getInstance().getProxySettings();
		for(int i=0;i<ProxySettings.Type.values().length;i++){
			if(ProxySettings.Type.values()[i].equals(ps.proxyType)){
				choice_proxyType.setSelectedIndex(i);
				break;
			}
		}
		
		input_prox_ip.setText(ps.HOST);
		input_prox_port.setText(ps.PORT+"");
		
		input_prox_domain.setText(ps.DOMAIN);
		input_prox_username.setText(ps.USER);
		input_prox_password.setText(ps.PASS);
		proxyEnabled(ps.proxyType!=ProxySettings.Type.NONE);
	}

	private void extportLoad() {
		input_chrome.setText("" + SettingsManager.getInstance().getWebSockPort());
		input_loopback.setText("" + SettingsManager.getInstance().getLocalPort());
	}

	public void extDisable() {
		input_loopback.setEnabled(false);
		input_chrome.setEnabled(false);
		button_ext_revert.setEnabled(false);
		button_ext_save.setEnabled(false);
	}

	public void closeOnSave(final JDialog sf) {
		button_prox_save.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				sf.dispose();
			}
		});
	}
}
