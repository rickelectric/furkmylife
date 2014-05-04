package org.rickelectric.furkmanager.experimentation;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;

import org.json.JSONArray;
import org.json.JSONObject;
import org.rickelectric.furkmanager.FurkManager;
import org.rickelectric.furkmanager.models.APIObject;
import org.rickelectric.furkmanager.models.FurkFile;
import org.rickelectric.furkmanager.network.API;
import org.rickelectric.furkmanager.network.APIBridge;
import org.rickelectric.furkmanager.utils.ThreadPool;
import org.rickelectric.furkmanager.utils.UtilBox;
import org.rickelectric.furkmanager.views.windows.FurkFileView;


public class Interface_Test {

	private static JFrame frmTestWindow=null;
	private JTextArea jsonResult=null;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run(){
				try{
					APIBridge.initialize("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
					/*if(!APIBridge.ping()){
						JOptionPane.showMessageDialog(null, "Furk Is Not Accessible");
						return;
					}*/
					new Interface_Test();
					Interface_Test.frmTestWindow.setVisible(true);
					Interface_Test.frmTestWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Interface_Test(){
		if(frmTestWindow!=null){
			frmTestWindow.setVisible(true);
			return;
		}
		ThreadPool.init();
		initialize();
		frmTestWindow.setVisible(true);
	}
	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		
		frmTestWindow = new JFrame();
		frmTestWindow.setTitle("Furk Manager");
		frmTestWindow.setBounds(100, 100, 567, 592);
		frmTestWindow.addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowIconified(WindowEvent arg0){
				super.windowIconified(arg0);
				UtilBox.pause(1000);
				//TODO if(SettingsManager.minimizeToTray()) //When SettingsManager is complete
				FurkManager.trayRun();
				frmTestWindow.dispose();
			}
			
			@Override
			public void windowClosing(WindowEvent e){
				int opt=JOptionPane.showConfirmDialog(null, "Completely exit the application?\nPress 'Yes' to exit, 'No' to minimize to the system tray, or 'Cancel' to abort.");
				if(opt==JOptionPane.YES_OPTION) FurkManager.exit();
				if(opt==JOptionPane.NO_OPTION){
					FurkManager.trayRun();
					frmTestWindow.dispose();
				}
			}
		});
		frmTestWindow.getContentPane().setLayout(null);
		
		ImageIcon fr=new ImageIcon("img/fr.png");
		frmTestWindow.setIconImage(fr.getImage());
		
		JLabel label = new JLabel("");
		label.setIcon(new ImageIcon("img/furk_logo.png"));
		label.setBounds(10, 11, 250, 69);
		frmTestWindow.getContentPane().add(label);
		
		JLabel label_1 = new JLabel("");
		label_1.setBounds(470, 11, 69, 69);
		label_1.setIcon(new ImageIcon("img/rls-68x68.png"));
		frmTestWindow.getContentPane().add(label_1);
		
		final JButton btnGetFiles = new JButton("Get File(s)");
		btnGetFiles.setBounds(10, 91, 105, 23);
		
		btnGetFiles.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ThreadPool.run(new Runnable(){
					public void run(){
						btnGetFiles.setText("Please Wait...");
						btnGetFiles.setEnabled(false);
						String json=APIBridge.fileGet(APIBridge.GET_ALL, null, null, null,true);
						jsonResult.setText(json);
						btnGetFiles.setEnabled(true);
						btnGetFiles.setText("Get File(s)");
					}
				});
				
			}
		});
		frmTestWindow.getContentPane().add(btnGetFiles);
		
		final JButton btnGetDownloads = new JButton("Get Download(s)");
		btnGetDownloads.setBounds(127, 91, 135, 23);
		btnGetDownloads.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				ThreadPool.run(new Runnable(){
					public void run(){
						btnGetDownloads.setText("Please Wait...");
						btnGetDownloads.setEnabled(false);
						String json=APIBridge.dlGet(APIBridge.GET_STATUS,"active", null,true);
						jsonResult.setText(json);
						btnGetDownloads.setEnabled(true);
						btnGetDownloads.setText("Get Download(s)");
					}
				});
				
			}
		});
		frmTestWindow.getContentPane().add(btnGetDownloads);
		
		final JButton btnAddMagnet = new JButton("Add Magnet");
		btnAddMagnet.setBounds(281, 91, 111, 23);
		frmTestWindow.getContentPane().add(btnAddMagnet);
		
		JButton btnAddInfoHash = new JButton("Add Info Hash");
		btnAddInfoHash.setBounds(404, 91, 135, 23);
		frmTestWindow.getContentPane().add(btnAddInfoHash);
		
		final JButton btnFetchInfoFrom = new JButton("Fetch Info Using Hash");
		btnFetchInfoFrom.setBounds(10, 125, 250, 23);
		btnFetchInfoFrom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				final String hashLink=JOptionPane.showInputDialog("Enter Info Hash");
				if(hashLink==null||hashLink.length()==0) return;
				ThreadPool.run(new Runnable(){
					public void run(){
						btnFetchInfoFrom.setText("Please Wait...");
						btnFetchInfoFrom.setEnabled(false);
						String json=APIBridge.fileInfo(hashLink);
						if(json!=null){
							JSONObject js=new JSONObject(json);
							if(js.getString("status").equals("ok")){
								JSONArray files=js.getJSONArray("files");
								ArrayList<APIObject> fs=API.File.jsonFiles(files);
								if(fs.size()>0){
									APIObject o=fs.get(0);
									if(o instanceof FurkFile){
										FurkFileView fv=new FurkFileView((FurkFile)o);
										fv.setVisible(true);
									}
									else{
										JOptionPane.showMessageDialog(null,"File Info Not Available");
									}
								}
							}
						}
						btnFetchInfoFrom.setEnabled(true);
						btnFetchInfoFrom.setText("Fetch Info Using Hash");
					}
				});
			}
		});
		frmTestWindow.getContentPane().add(btnFetchInfoFrom);
		
		JButton btnLinmstates = new JButton("Linkstates");
		btnLinmstates.setBounds(280, 126, 111, 23);
		frmTestWindow.getContentPane().add(btnLinmstates);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 178, 529, 363);
		frmTestWindow.getContentPane().add(scrollPane);
		
		jsonResult = new JTextArea();
		scrollPane.setViewportView(jsonResult);
		
		JLabel lblJsonResult = new JLabel("JSON Result:");
		lblJsonResult.setBounds(12, 160, 103, 16);
		frmTestWindow.getContentPane().add(lblJsonResult);
		
		final JButton btnSearchTorrents = new JButton("Search Torrents");
		btnSearchTorrents.addActionListener(new ActionListener(){
			
			public void actionPerformed(ActionEvent e){
				InterfaceTest2 is2=new InterfaceTest2();
				is2.setDefaultCloseOperation(InterfaceTest2.DISPOSE_ON_CLOSE);
				is2.setVisible(true);
			}
		});
		btnSearchTorrents.setBounds(404, 125, 135, 22);
		frmTestWindow.getContentPane().add(btnSearchTorrents);
		
		
	}
}
