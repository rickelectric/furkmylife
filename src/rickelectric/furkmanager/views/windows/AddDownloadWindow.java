package rickelectric.furkmanager.views.windows;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JRootPane;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.eclipse.ecf.protocol.bittorrent.TorrentFile;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.network.API;
import rickelectric.furkmanager.network.StreamDownloader;
import rickelectric.furkmanager.utils.UtilBox;

public class AddDownloadWindow extends JFrame {

	private static final long serialVersionUID = 1L;
	private JTextField input_link;
	private JComboBox<String> choice_type;
	private JButton button_add;
	private JButton button_info;
	private JProgressBar prog_loading;

	APIObject info = null;
	private JLabel label_name;
	private JLabel label_size;
	private JTextField input_name;
	private JTextField input_size;
	private JButton button_open;

	public static final int INFO_HASH = 0, MAGNET_TORRENT_URL = 1,
			TORRENT_FILE = 2;
	private int addType;

	public static void main(String[] args) {
		API.init("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		new AddDownloadWindow("C:\\Users\\Ionicle\\Torrents\\Jake 2.0.torrent")
				.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		;
	}

	public AddDownloadWindow(String link) {
		this();
		getLinkType(link);
		addLink(link);
	}

	private void getLinkType(String link) {
		addType = INFO_HASH;
		if (link.startsWith("http://") || link.startsWith("https://")
				|| link.startsWith("ftp://") || link.startsWith("magnet:"))
			addType = MAGNET_TORRENT_URL;
		else if (link.endsWith(".torrent"))
			addType = TORRENT_FILE;
		choice_type.setSelectedIndex(addType);
	}

	public AddDownloadWindow(int addType, String link) {
		this();
		this.addType = addType;
		choice_type.setSelectedIndex(addType);
		addLink(link);
	}

	private void addLink(String link) {
		button_add.setVisible(false);
		input_link.setText(link);
		if (getInfo(link)) {
			new Thread(new Runnable() {
				public void run() {
					prog_loading.setString("Complete");
					UtilBox.pause(3000);
					prog_loading.setVisible(false);
				}
			}).start();
			button_add.setVisible(true);
		}
	}

	public AddDownloadWindow() {
		setResizable(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setTitle("Add Download");

		setIconImage(new ImageIcon("img/fr.png").getImage());

		getContentPane().setLayout(null);
		setUndecorated(true);
		getRootPane().setWindowDecorationStyle(JRootPane.PLAIN_DIALOG);
		setSize(489, 190);
		setLocationRelativeTo(null);
		
		JLabel label_type = new JLabel("Download Type: ");
		label_type.setBounds(10, 12, 121, 23);
		getContentPane().add(label_type);

		button_add = new JButton("Add To My Downloads");
		button_add.addActionListener(new ActionListener() {
			private boolean isRunning=false;
			public void actionPerformed(ActionEvent e) {
				Thread t=new Thread(new Runnable(){
					public void run(){
						if(isRunning) return;
						isRunning=true;
						prog_loading.setString("Adding...");
						prog_loading.setIndeterminate(true);
						prog_loading.setVisible(true);
						addToDownloads();
						prog_loading.setVisible(false);
						prog_loading.setString("");
						isRunning=false;
					}
				});
				t.start();
				
			}
		});
		button_add.setBounds(304, 119, 168, 20);
		getContentPane().add(button_add);

		button_open = new JButton("Open...");
		button_open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String torr = UtilBox.openFile("Torrent");
				if (torr == null || torr.equals("")
						|| !torr.endsWith(".torrent"))
					return;
				input_link.setText(torr);
			}
		});
		button_open.setToolTipText("Browse for a torrent file.");
		button_open.setVisible(false);
		button_open.setBounds(374, 10, 98, 26);
		getContentPane().add(button_open);

		choice_type = new JComboBox<String>();
		choice_type.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent arg0) {
				if (choice_type.getSelectedIndex() == 2)
					button_open.setVisible(true);
				else
					button_open.setVisible(false);
			}
		});
		choice_type.addItem("Info Hash");
		choice_type.addItem("Magnet / URL");
		choice_type.addItem("Torrent File");
		choice_type.setBounds(149, 11, 211, 24);
		getContentPane().add(choice_type);
		
		JLabel label_link = new JLabel("Link:");
		label_link.setBounds(10, 47, 121, 20);
		getContentPane().add(label_link);

		input_link = new JTextField();
		input_link.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				String link = input_link.getText();
				getLinkType(link);
			}
		});
		input_link.setBounds(10, 67, 462, 20);
		getContentPane().add(input_link);
		input_link.setColumns(10);

		button_info = new JButton("Get Info");
		button_info.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(new Runnable() {
					public void run() {
						info = null;
						String link = input_link.getText();
						getLinkType(link);
						addLink(link);
					}
				});
				t.start();
			}
		});
		button_info.setBounds(10, 119, 119, 20);
		getContentPane().add(button_info);

		prog_loading = new JProgressBar();
		prog_loading.setVisible(false);
		prog_loading.setBounds(141, 119, 151, 20);
		getContentPane().add(prog_loading);

		label_name = new JLabel("Name: ");
		label_name.setBounds(10, 114, 55, 16);
		getContentPane().add(label_name);

		label_size = new JLabel("Size:");
		label_size.setBounds(10, 146, 55, 16);
		getContentPane().add(label_size);

		input_name = new JTextField();
		input_name.setBounds(56, 112, 416, 20);
		getContentPane().add(input_name);
		input_name.setColumns(10);

		input_size = new JTextField();
		input_size.setBounds(56, 144, 114, 20);
		getContentPane().add(input_size);
		input_size.setColumns(10);

		label_name.setVisible(false);
		label_size.setVisible(false);
		input_name.setVisible(false);
		input_size.setVisible(false);

		setVisible(true);
	}

	public boolean getInfo(String link) {
		getLinkType(link);
		int choice = choice_type.getSelectedIndex();

		prog_loading.setBackground(Color.BLUE);
		prog_loading.setStringPainted(true);
		prog_loading.setMaximum(100);
		prog_loading.setString("Getting Info");
		prog_loading.setIndeterminate(true);
		prog_loading.setVisible(true);

		if (choice == MAGNET_TORRENT_URL) {
			if (link.startsWith("magnet:?")) {
				int i = link.indexOf("btih:");
				if (i == -1) {
					FurkManager.trayAlert(FurkManager.TRAY_WARNING,"Not Found","Info For This File Not Found",null);
					prog_loading.setIndeterminate(false);
					prog_loading.setForeground(Color.RED);
					prog_loading.setString("Info Not Found");
					prog_loading.setValue(prog_loading.getMaximum());
					return true;
				}
				String hash = link.substring(i + 5);
				hash = hash.split("&")[0];

				repaint();
				link = hash;
				choice = 0;
			} else {
				try {
					File tmp = new File("tmp.torrent");
					String name=StreamDownloader.getFileStreamWithName(link, tmp, 16);
					if(tmp.renameTo(new File(name))) link=".\\"+name;
					else link=".\\tmp.torrent";
					input_link.setText(name);
					choice=TORRENT_FILE;
				} catch (Exception e) {
					FurkManager.trayAlert(FurkManager.TRAY_WARNING,"N/A","Info Not Available. ",null);
					prog_loading.setString("Info Not Available. ");
					prog_loading.setIndeterminate(false);
					prog_loading.setForeground(Color.RED);
					prog_loading.setValue(prog_loading.getMaximum());
					return false;
				}
			}
		}
		if (choice == TORRENT_FILE) {
			try {
				File f=new File(link);
				TorrentFile t = new TorrentFile(f);
				String hash = t.getHexHash().toUpperCase(),
					   name=t.getName();
				long size = t.getTotalLength();
				info = new APIObject(name, hash, size);
				if(link.startsWith(".\\")){
					File n=new File("dwtemp\\"+link);
					if(n.exists()) n.delete();
					FileUtils.moveFile(f, n);
					input_link.setText("dwtemp\\"+link.substring(2));
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				FurkManager.trayAlert(FurkManager.TRAY_WARNING,"File Not Found?",
						"The File You Selected Was Not Found",null);
				prog_loading.setString("File Not Found?");
				prog_loading.setValue(prog_loading.getMaximum());
				return false;
			}
		}
		if (choice == INFO_HASH) {
			String hash = link.toUpperCase();
			if (hash.length() > 40) {
				FurkManager.trayAlert(FurkManager.TRAY_WARNING,"Hash Invalid","You Have Entered An Invalid Info Hash",null);
				prog_loading.setString("Hash Invalid");
				prog_loading.setIndeterminate(false);
				prog_loading.setForeground(Color.RED);
				prog_loading.setValue(prog_loading.getMaximum());
				return false;
			}

			try {
				info = API.File.info(hash);
			} catch (Exception e) {
				FurkManager.trayAlert(FurkManager.TRAY_ERROR,"Error",e.getMessage(),null);
				prog_loading.setString(e.getMessage());
				prog_loading.setIndeterminate(false);
				prog_loading.setForeground(Color.RED);
				prog_loading.setValue(prog_loading.getMaximum());
				return false;
			}
		}

		showInfo();

		prog_loading.setIndeterminate(false);
		prog_loading.setString("Complete");
		prog_loading.setForeground(Color.GREEN);
		prog_loading.setValue(prog_loading.getMaximum());

		return true;
	}

	private void showInfo() {
		if (info == null)
			return;

		setSize(500, 280);
		button_add.setBounds(304, 209, 168, 20);
		button_info.setBounds(10, 209, 119, 20);
		prog_loading.setBounds(141, 209, 151, 20);

		label_name.setVisible(true);
		label_size.setVisible(true);

		input_name.setText(info.getName());
		input_size.setText(info.getSizeString());
		input_name.setVisible(true);
		input_size.setVisible(true);

		repaint();
	}

	private void addToDownloads() {
		String link = input_link.getText();
		getLinkType(link);
		if (choice_type.getSelectedIndex() == TORRENT_FILE) {
			try{
				if(API.Download.addTorrentFile(link)){
					FurkManager.trayAlert(FurkManager.TRAY_INFO,"Success","Added Torrent File",null);
					dispose();
				}
				else
					FurkManager.trayAlert(FurkManager.TRAY_WARNING,"Failure","Add Failed: " + API.Download.getLastError(),null);
			}catch(Exception e){
				FurkManager.trayAlert(FurkManager.TRAY_ERROR,"Failure",e.getMessage(),null);
			}
			return;
		}

		if (addType == INFO_HASH) {
			if (API.Download.addHash(link)) {
				FurkManager.trayAlert(FurkManager.TRAY_INFO,"Success","Added Torrent Hash",null);
				dispose();
			} else
				FurkManager.trayAlert(FurkManager.TRAY_WARNING,"Failure","Add Failed: " + API.Download.getLastError(),null);
		} else if (addType == MAGNET_TORRENT_URL) {
			if (API.Download.addURL(link)) {
				FurkManager.trayAlert(FurkManager.TRAY_INFO,"Success","Added URL",null);
				dispose();
			} else
				FurkManager.trayAlert(FurkManager.TRAY_WARNING,"Failure","Add Failed: " + API.Download.getLastError(),null);
		}
	}

}