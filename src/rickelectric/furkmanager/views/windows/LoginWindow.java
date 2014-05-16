package rickelectric.furkmanager.views.windows;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.data.HelpData;
import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.network.api.API_UserData;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.panels.Settings_ProxyPorts;

public class LoginWindow extends AppFrameClass {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField input_username;
	private JPasswordField input_password;
	private JTextField input_apikey;
	private JButton button_userlogin;
	
	private JFrame sf;
	
	private JLabel label_logo;
	
	public LoginWindow() {
		super();
		setResizable(false);
		setTitle("Login");
		windowClose();
		
		setBounds(100, 100, 320, 410);
		contentPane = new JPanel();
		contentPane.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		super.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel label_username = new JLabel("Username / Email:");
		label_username.setBounds(10, 72, 110, 14);
		contentPane.add(label_username);
		
		input_username = new JTextField();
		input_username.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e){
				if(input_username.getBackground()==Color.PINK)
					input_username.setBackground(Color.WHITE);
			}
		});
		input_username.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					input_password.requestFocus();
				}
			}
		});
		input_username.setText("rick.lewis92@gmail.com");
		input_username.setBounds(10, 88, 292, 20);
		contentPane.add(input_username);
		input_username.setColumns(10);
		
		JLabel label_password = new JLabel("Password: ");
		label_password.setBounds(10, 120, 110, 16);
		contentPane.add(label_password);
		
		input_password = new JPasswordField();
		input_password.addFocusListener(new FocusAdapter() {
			@Override
			public void focusGained(FocusEvent e){
				if(input_password.getBackground()==Color.PINK)
					input_password.setBackground(Color.WHITE);
			}
		});
		input_password.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e){
				if(e.getKeyCode()==KeyEvent.VK_ENTER){
					ThreadPool.run(userLogin);
				}
			}
		});
		input_password.setBounds(10, 137, 292, 20);
		contentPane.add(input_password);
		
		JCheckBox check_savecreds = new JCheckBox("Save Credentials");
		check_savecreds.setBounds(7, 165, 138, 20);
		contentPane.add(check_savecreds);
		
		button_userlogin = new JButton("Login");
		button_userlogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				ThreadPool.run(userLogin);
			}
		});
		button_userlogin.setBounds(179, 165, 123, 20);
		contentPane.add(button_userlogin);
		
		JLabel label_or = new JLabel("Or Use Your API Key");
		label_or.setBounds(10, 193, 123, 16);
		contentPane.add(label_or);
		
		JLabel lblhelp = new JLabel("API Key Help");
		lblhelp.setIcon(new ImageIcon(LoginWindow.class.getResource("/rickelectric/furkmanager/img/dash/User-16.png")));
		lblhelp.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		lblhelp.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				//TODO Open Help On Finding API Key On Furk.net With Link
				//TODO Make a Frame with clickable links to furk.net and paginated images hilighting steps.
				setStatus("Opening Help. Please Wait...");
				JEditorPane editorPane = new JEditorPane();
				editorPane.setContentType("text/html");
				editorPane.setText(HelpData.apiKey(true,true));
				editorPane.setEditable(false);
				JOptionPane.showMessageDialog(null, 
						editorPane,
						"API Key Help", JOptionPane.PLAIN_MESSAGE);
				setStatus("Ready");
				
			}
		});
		lblhelp.setForeground(Color.BLUE);
		lblhelp.setBounds(10, 210, 123, 25);
		contentPane.add(lblhelp);
		
		input_apikey = new JTextField();
		input_apikey.setBounds(10, 260, 292, 20);
		contentPane.add(input_apikey);
		input_apikey.setColumns(10);
		
		JLabel label_key = new JLabel("API Key:");
		label_key.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				//TODO Remove This Bypass When Complete
				if(e.getButton()==MouseEvent.BUTTON3)
					input_apikey.setText("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
			}
		});
		label_key.setBounds(10, 241, 55, 16);
		contentPane.add(label_key);
		
		JButton button_apilogin = new JButton("API Login");
		button_apilogin.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				ThreadPool.run(apiLogin);
				
			}
		});
		button_apilogin.setBounds(179, 315, 123, 20);
		contentPane.add(button_apilogin);
		
		JCheckBox check_remember = new JCheckBox("Remember Me");
		check_remember.setBounds(7, 290, 162, 20);
		contentPane.add(check_remember);
		
		JCheckBox check_autologin = new JCheckBox("Enable Auto Login");
		check_autologin.setBounds(7, 315, 159, 20);
		contentPane.add(check_autologin);
		
		label_logo = new JLabel("");
		//TODO Remove this bypass when complete
		label_logo.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getButton()==MouseEvent.BUTTON3&&e.getClickCount()==2){
					API.init("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
					dispose();
					FurkManager.appRun();
				}
			}
		});
		label_logo.setHorizontalAlignment(SwingConstants.CENTER);
		label_logo.setBounds(10, 4, 292, 64);
		label_logo.setIcon(new ImageIcon(FurkManager.class.getResource("img/fl_anim/fr-logo-speed-0.gif")));
		contentPane.add(label_logo);
		
		JLabel btn_settings = new JLabel("Proxy Settings");
		btn_settings.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(sf!=null){
					sf.setVisible(true);
					sf.toFront();
					return;
				}
				sf=new JFrame("Proxy Settings");
				sf.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				Settings_ProxyPorts pp=new Settings_ProxyPorts();
				pp.extDisable();
				pp.closeOnSave(sf);
				sf.getContentPane().add(pp);
				sf.setResizable(false);
				sf.pack();
				sf.setLocation(contentPane.getLocationOnScreen());
				sf.setVisible(true);
			}
		});
		btn_settings.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		btn_settings.setIcon(new ImageIcon(LoginWindow.class.getResource("/rickelectric/furkmanager/img/dash/Settings-16.png")));
		btn_settings.setBounds(179, 210, 123, 25);
		contentPane.add(btn_settings);
		
		addConsole();
	}
	
	boolean loggingIn=false;
	
	Runnable apiLogin=new Runnable(){
		
		public void run(){
			if(loggingIn) return;
			String key=input_apikey.getText();
			if(key.length()==0||key.length()<39||key.contains(" ")){
				setStatus("Blank Or Invalid API Key");
				return;
			}
			loggingIn=true;
			furkAnimate(4);
			try{
				boolean keyPing=APIBridge.ping(key);
				if(keyPing){
					APIBridge.initialize(key);
					API_UserData.loadUserData();
					FurkManager.appRun();
					loggingIn=false;
					dispose();
				}
				else{
					throw new Exception("Invalid API Key");
				}
			}catch(Exception e){
				furkAnimate(2);
				FurkManager.trayAlert(FurkManager.TRAY_ERROR,"Login Failed",e.getMessage(),null);
				loggingIn=false;
				furkAnimate(0);
				return;
			}
			loggingIn=false;
		}
	};
	
	Runnable userLogin=new Runnable(){
		public void run(){
			if(loggingIn) return;
			if(input_username.getText().length()==0){
				setStatus("Please Enter Username");
				input_username.setBackground(Color.PINK);
				return;
			}
			if(input_password.getPassword().length==0){
				input_password.setBackground(Color.PINK);
				setStatus("Please Enter Password");
				return;
			}
			loggingIn=true;
			String username=input_username.getText();
			char[] pass=input_password.getPassword();
			String password="";
			for(char c:pass) password+=c;
			furkAnimate(4);
			try{
				UtilBox.pause(500);
				boolean login=APIBridge.userLogin(username, password);
				if(login){
					dispose();
					loggingIn=false;
					FurkManager.appRun();
					return;
				}
				else{
					throw new Exception("Invalid Username or Password");
				}
			}catch(Exception e){
				furkAnimate(2);
				FurkManager.trayAlert(FurkManager.TRAY_ERROR,"Login Failed",e.getMessage(),null);
				loggingIn=false;
				furkAnimate(0);
				return;
			}
		}
	};
	
	/**
	 * Animate The Furk Logo by changing the static image to one of the animated gifs.
	 * @param speed Speed of the animation. A value between 0(still) and 4(fastest) inclusive.
	 */
	public void furkAnimate(int speed){
		if(speed>=0&&speed<=4)
			label_logo.setIcon(new ImageIcon(FurkManager.class.getResource("img/fl_anim/fr-logo-speed-"+speed+".gif")));
	}
	
	public void setVisible(boolean b){
		input_password.setText("");
		furkAnimate(0);
		super.setVisible(b);
	}
}
