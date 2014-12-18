package rickelectric.furkmanager.views.windows;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import org.apache.commons.io.FileUtils;
import org.eclipse.ecf.protocol.bittorrent.TorrentFile;

import rickelectric.UtilBox;
import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.network.StreamDownloader;
import rickelectric.furkmanager.network.api.API_Download;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.img.ImageLoader;

public class AddDownloadFrame extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JComboBox<String> choice_type;
	private JButton btn_open;
	private JTextField input_name;
	private JTextField input_link;
	private JTextField input_size;
	private JButton btn_add;
	private JButton btn_cancel;
	private JLabel loading;
	
	private String gLink;

	public AddDownloadFrame() {
		setMinimumSize(new Dimension(530, 230));
		setResizable(false);
		setTitle("Add Furk Download");
		setLocationRelativeTo(null);
		contentPane=new JPanel();
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblType = new JLabel("Type: ");
		lblType.setBounds(10, 11, 47, 23);
		contentPane.add(lblType);
		
		choice_type = new JComboBox<String>();
		choice_type.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e){
				if(choice_type.getSelectedIndex()==1){
					btn_open.setVisible(true);
				}
				else{
					btn_open.setVisible(false);
				}
			}
		});
		choice_type.setFont(new Font("Dialog", Font.BOLD, 12));
		choice_type.setModel(
			new DefaultComboBoxModel<String>(
				new String[] {
					"URL / Magnet Link",
					"Torrent Metafile",
					"Info Hash"
				}
			)
		);
		choice_type.setBounds(57, 11, 301, 23);
		contentPane.add(choice_type);
		
		btn_open = new JButton("Open Torrent...");
		btn_open.addActionListener(this);
		btn_open.setFont(new Font("Dialog", Font.BOLD, 12));
		btn_open.setBounds(368, 10, 148, 23);
		contentPane.add(btn_open);
		btn_open.setVisible(false);
		
		JLabel lblName = new JLabel("Name: ");
		lblName.setBounds(10, 92, 47, 22);
		contentPane.add(lblName);
		
		input_name = new JTextField();
		input_name.setText("???");
		input_name.setFont(new Font("Dialog", Font.BOLD, 12));
		input_name.setBackground(Color.WHITE);
		input_name.setEditable(false);
		input_name.setBounds(57, 91, 459, 22);
		contentPane.add(input_name);
		input_name.setColumns(10);
		
		JLabel lblLink = new JLabel("Link: ");
		lblLink.setBounds(10, 46, 47, 22);
		contentPane.add(lblLink);
		
		input_link = new JTextField();
		input_link.setFont(new Font("Dialog", Font.BOLD, 12));
		input_link.setColumns(10);
		input_link.setBackground(Color.WHITE);
		input_link.setBounds(57, 45, 459, 22);
		input_link.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				getLinkType();
			}
		});
		contentPane.add(input_link);
		
		JLabel label = new JLabel("");
		label.setBorder(new LineBorder(new Color(128, 128, 128), 1, true));
		label.setBounds(-5, 79, 536, 2);
		contentPane.add(label);
		
		JLabel lblSize = new JLabel("Size: ");
		lblSize.setBounds(10, 125, 47, 22);
		contentPane.add(lblSize);
		
		input_size = new JTextField();
		input_size.setText("???");
		input_size.setFont(new Font("Dialog", Font.BOLD, 12));
		input_size.setEditable(false);
		input_size.setColumns(10);
		input_size.setBackground(Color.WHITE);
		input_size.setBounds(57, 124, 131, 22);
		contentPane.add(input_size);
		
		btn_add = new JButton("Check Download");
		btn_add.addActionListener(this);
		btn_add.setBounds(368, 124, 148, 23);
		btn_add.setFont(new Font("Dialog", Font.BOLD, 12));
		contentPane.add(btn_add);
		
		btn_cancel = new JButton("Cancel");
		btn_cancel.addActionListener(this);
		btn_cancel.setBounds(368, 156, 148, 23);
		btn_cancel.setFont(new Font("Dialog", Font.BOLD, 12));
		contentPane.add(btn_cancel);
		
		loading = new JLabel("");
		loading.setIcon(new ImageIcon(ImageLoader.class.getResource("ajax-loader.gif")));
		loading.setHorizontalAlignment(SwingConstants.CENTER);
		loading.setBounds(304, 124, 54, 55);
		loading.setVisible(false);
		contentPane.add(loading);
	}
	
	public AddDownloadFrame(String link) {
		this();
		input_link.setText(link);
		setVisible(true);
		getLinkType();
		info(new Runnable(){
			@Override
			public void run(){
				btn_add.setText("Add Download");
				loading.setVisible(false);
			}
		});
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object src=e.getSource();
		if(src.equals(btn_add)){
			if(btn_add.getText().contains("Check")){
				info(new Runnable(){
					@Override
					public void run(){
						btn_add.setText("Add Download");
						loading.setVisible(false);
					}
				});
			}
			else addLink(gLink);
		}
		else if(src.equals(btn_open)){
			openFile();
		}
		else if(src.equals(btn_cancel)){
			close();
		}
	}
	
	private void getLinkType(){
		String link=input_link.getText();
		int addType = 2;
		if (link.startsWith("http://") || link.startsWith("https://")
				|| link.startsWith("ftp://") || link.startsWith("magnet:"))
			addType = 0;
		else if (link.endsWith(".torrent"))
			addType = 1;
		choice_type.setSelectedIndex(addType);
		btn_add.setText("Check Download"); 
	}
	
	private void addLink(final String link) {
		new Thread(new Runnable(){
			@Override
			public void run(){
				loading.setVisible(true);
				try{
					int type=choice_type.getSelectedIndex();
					if(type==1){
						if(API_Download.addTorrentFile(link)){
							FurkManager.trayAlert(FurkManager.TRAY_INFO, "Success", "Added "+link+" to my Furk account.", new Runnable(){
								@Override
								public void run(){
									API_Download.getAll();
								}
							});
							close();
						}else{
							FurkManager.trayAlert(FurkManager.TRAY_ERROR, "Add Error", "Unable To Add "+link, null);
						}
					}
					else{
						if(API_Download.addHash(link)){
							FurkManager.trayAlert(FurkManager.TRAY_INFO, "Success", "Added "+link+" to my Furk account.", new Runnable(){
								@Override
								public void run(){
									API_Download.getAll();
								}
							});
							close();
						}else{
							FurkManager.trayAlert(FurkManager.TRAY_ERROR, "Add Error", "Unable To Add "+link, null);
						}
					}
				}catch(Exception e){}
				loading.setVisible(false);
			}
		}).start();
	}
	
	private void info(final Runnable r){
		new Thread(new Runnable(){
			@Override
			public void run(){
				info();
				r.run();
			}
		}).start();
	}
	
	private void info(){
		int type=choice_type.getSelectedIndex();
		String link=input_link.getText();
		if(type==0){
			link=parseURL(link);
			type=2;
		}
		if(type==1){
			torrentInfo(link);
		}
		if(type==2){
			hashInfo(link);
		}
	}
	
	private void hashInfo(String hash){
		APIObject info=API_File.info(hash);
		if(info==null){
			FurkManager.trayAlert(FurkManager.TRAY_WARNING, "Info GET Error", 
				"Unable to obtain data on this link from Furk.net.\n"
				+ "Possible Reasons:"
				+ "\n\t-Furk.net was unable to find the info on the supplied link."
				+ "\n\t-Unable To Connect To Furk.net", null);
		}
		input_name.setText(info.getName());
		input_size.setText(info.getSizeString());
		gLink=hash;
	}
	
	private void torrentInfo(String link){
		File f=new File(link);
		try {
			TorrentFile t = new TorrentFile(f);
			String hash = t.getHexHash().toUpperCase(),
			   name=t.getName();
		long size = t.getTotalLength();
		APIObject o=new APIObject(name,hash,size);
		input_name.setText(o.getName());
		input_size.setText(o.getSizeString());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IOException e){
			e.printStackTrace();
		}
		gLink=link;
	}
	
	private String parseURL(String link){
		if (link.startsWith("magnet:?")) {
			int i = link.indexOf("btih:");
			if (i == -1) {
				FurkManager.trayAlert(FurkManager.TRAY_WARNING,"Not Found","Info For This File Not Found",null);
				return link;
			}
			//Get Hash From Magnet
			String hash = link.substring(i + 5);
			hash = hash.split("&")[0];
			
			return hash;
		} else{
			try {
				File tmp = new File("tmp.torrent");
				String name=StreamDownloader.getInstance().getFileStreamWithName(link, tmp, 16);
				if(!name.contains(".torrent")) return null;
				if(tmp.renameTo(new File(name))) link=".\\"+name;
				else link=".\\tmp.torrent";
				File n=new File("dwtemp\\"+name);
				if(n.exists()) n.delete();
				FileUtils.moveFile(tmp, n);
				input_link.setText(name);
				return link;
			} catch (Exception e) {
				FurkManager.trayAlert(FurkManager.TRAY_WARNING,"N/A","Info Not Available. Unable To Download Torrent File",null);
				return link;
			}
		}
	}
	
	private void openFile() {
		String metafile = UtilBox.getInstance().openFile("Torrent");
		input_link.setText(metafile);
		info();
	}

	private void close(){
		dispose();
	}
}
