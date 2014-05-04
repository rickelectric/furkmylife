package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import rickelectric.furkmanager.swingmods.TranslucentPane;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;

public class Main_SettingsView extends TranslucentPane {
	private static final long serialVersionUID = 1L;
	private JTextField input_api_base;
	private JTextField input_dwFolder;
	private JButton button_browse;
	private JCheckBox check_dwAsk;
	private JCheckBox check_multipart;
	private JSpinner spinner_buffsize;
	private JButton button_dfsave;

	public Main_SettingsView() {
		super();
		setAlpha(1);
		setPreferredSize(new Dimension(561, 400));
		setLayout(null);

		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setBounds(4, 0, 554, 394);
		add(tabbedPane);
		
		JPanel pane_connection = new Settings_ProxyPorts();
		tabbedPane.addTab("Connection", null, pane_connection, null);
		
		JPanel panel_downloader = new JPanel();
		panel_downloader.setBackground(UtilBox.getRandomColor());
		tabbedPane.addTab("Downloader", null, panel_downloader, null);
		panel_downloader.setLayout(null);
		
		JPanel panel_dwfolder = new JPanel();
		panel_dwfolder.setOpaque(false);
		panel_dwfolder.setBorder(new TitledBorder(null, "Default Folder", TitledBorder.LEADING, TitledBorder.TOP, null, null));
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
		input_dwFolder.setText(SettingsManager.getDownloadFolder());
		input_dwFolder.setColumns(10);
		
		check_dwAsk = new JCheckBox("Ask Before Every Download");
		check_dwAsk.setBounds(8, 64, 211, 20);
		panel_dwfolder.add(check_dwAsk);
		check_dwAsk.setOpaque(false);
		check_dwAsk.setSelected(true);
		
		button_browse = new JButton("Browse...");
		button_browse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				String f=UtilBox.openFile(SettingsManager.getDownloadFolder());
				if(f!=null&&!f.equals("")) input_dwFolder.setText(f);
			}
		});
		button_browse.setBounds(415, 35, 98, 20);
		panel_dwfolder.add(button_browse);
		
		button_dfsave = new JButton("Save");
		button_dfsave.addActionListener(new ActionListener() {
			boolean action=false;
			public void actionPerformed(ActionEvent e){
				if(action) return;
				action=true;
				button_dfsave.setText("Saving");
				SettingsManager.setDownloadFolder(input_dwFolder.getText());
				SettingsManager.askFolderOnDownload(check_dwAsk.isSelected());
				SettingsManager.save();
				new Thread(new Runnable(){
					public void run(){
						Color c=button_dfsave.getForeground();
						Color b=button_dfsave.getBackground();
						
						button_dfsave.setForeground(Color.GREEN);
						button_dfsave.setBackground(Color.BLACK);
						
						button_dfsave.setText("Saved");
						try{Thread.sleep(2000);}catch(Exception e){}
						
						button_dfsave.setText("Save");
						button_dfsave.setForeground(c);
						button_dfsave.setBackground(b);
						
						action=false;
					}
				}).start();
			}
		});
		button_dfsave.setBounds(415, 67, 98, 20);
		panel_dwfolder.add(button_dfsave);
		
		JPanel panel_dwopt = new JPanel();
		panel_dwopt.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Optimization", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		panel_dwopt.setOpaque(false);
		panel_dwopt.setBounds(12, 129, 229, 132);
		panel_downloader.add(panel_dwopt);
		panel_dwopt.setLayout(null);
		
		JLabel lblBufferSize = new JLabel("Buffer Size (bytes): ");
		lblBufferSize.setBounds(12, 25, 119, 16);
		panel_dwopt.add(lblBufferSize);
		
		spinner_buffsize = new JSpinner();
		spinner_buffsize.setModel(new SpinnerNumberModel(1024, 8, 40960, 8));
		spinner_buffsize.setBounds(149, 23, 68, 20);
		panel_dwopt.add(spinner_buffsize);
		
		check_multipart = new JCheckBox("Use Multipartition Mode");
		check_multipart.setEnabled(false);
		check_multipart.setOpaque(false);
		check_multipart.setBounds(10, 64, 207, 24);
		panel_dwopt.add(check_multipart);

		JPanel panel = new JPanel();
		panel.setBackground(UtilBox.getRandomColor());
		panel.setLayout(null);
		tabbedPane.addTab("API Access", null, panel, null);

		JLabel label = new JLabel("API Base URL: ");
		label.setBounds(12, 14, 139, 16);
		panel.add(label);

		input_api_base = new JTextField();
		input_api_base.setFont(new Font("Dialog", Font.BOLD, 12));
		input_api_base.setText("https://www.furk.net/api");
		input_api_base.setColumns(10);
		input_api_base.setBounds(169, 12, 285, 20);
		panel.add(input_api_base);

		JLabel label_1 = new JLabel("ms");
		label_1.setBounds(248, 44, 55, 16);
		panel.add(label_1);

		JSpinner spinner = new JSpinner();
		spinner.setFont(new Font("Dialog", Font.BOLD, 12));
		spinner.setBounds(169, 42, 61, 20);
		panel.add(spinner);

		JLabel label_2 = new JLabel("Connection Timeout: ");
		label_2.setBounds(12, 44, 139, 16);
		panel.add(label_2);

		JPanel pane_ui = new JPanel();
		tabbedPane.addTab("User Interface", null, pane_ui, null);
		pane_ui.setLayout(null);
		
		load();
	}
	
	private void load(){
		input_dwFolder.setText(SettingsManager.getDownloadFolder());
		check_dwAsk.setSelected(SettingsManager.askFolderOnDownload());
		
		spinner_buffsize.setValue(SettingsManager.downloadBuffer());
		
	}
	
}
