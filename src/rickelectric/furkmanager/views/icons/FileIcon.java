package rickelectric.furkmanager.views.icons;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.idownloader.DownloadManager;
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.network.api.API_Download;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.panels.Main_FileView;
import rickelectric.furkmanager.views.windows.FurkFileView;

public class FileIcon extends JPanel implements Comparable<FileIcon> {

	private static final long serialVersionUID = 1L;

	private Color bgc;

	private FileIcon thisPanel;

	private APIObject cFile;
	public static final int WIDE_MODE = 100, SMALL_MODE = 200;

	// Wide Mode
	private JLabel input_name;
	private JLabel input_size;
	private JLabel input_hash;
	private JLabel label_name;
	private JLabel label_hash;
	private JLabel label_open;
	private JLabel label_furkview;
	private JCheckBox check_linked;
	// End Wide Mode

	private JLabel label_size;

	// Small Mode
	private JLabel icon_type;
	private JCheckBox check_ready;
	private JTextArea txt_name;
	// End Small Mode

	private boolean action = false;

	private int mode;
	private JLabel label_loading;
	
	private void parentRefresh(){
		Container parent = thisPanel;
		try {
			do {
				parent = parent.getParent();
			} while (!(parent instanceof Main_FileView));
			((Main_FileView) parent).refreshMyFiles(false);
		} catch (Exception e) {
			System.err
					.println("Could Not Find Proper Parent");
			e.printStackTrace();
		}
	}

	public class ContextMenu extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;
		private JMenuItem view;
		private JMenuItem fview;
		private JMenuItem browser;
		private JMenuItem internal;
		private JMenuItem idm;
		private JMenuItem link;
		private JMenuItem recycle;
		private JMenuItem delete;
		private JMenuItem add;
		private JMenuItem cp;
		private JMenuItem add_dl;

		public ContextMenu() {
			super();

			view = new JMenuItem("View Details");
			view.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/arrow_expand.png")));
			view.addActionListener(this);

			fview = new JMenuItem("Open Download Page");
			fview.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/web_view.png")));
			fview.addActionListener(this);

			add = new JMenuItem("Add To My Files");
			add.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/edit_add.png")));
			add.addActionListener(this);

			add_dl = new JMenuItem("Add To My Downloads");
			add_dl.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/edit_add.png")));
			add_dl.addActionListener(this);
			
			cp = new JMenuItem("Copy Download Page Link");
			cp.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/edit_icon.png")));
			cp.addActionListener(this);

			JMenu download = new JMenu("Download File Using...");
			download.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/download_gr.png")));
			{	
				idm = new JMenuItem("Internet Download Manager");
				idm.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/idm.png")));
				idm.addActionListener(this);
				if(SettingsManager.idm()){ 
					download.add(idm);
				}
				
				internal = new JMenuItem("Internal Downloader");
				internal.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/file_download.png")));
				internal.addActionListener(this);
				if(cFile.getSize()>104857600L){
					internal.setEnabled(false);
					internal.setToolTipText("For files <100MB in size");
				}
				download.add(internal);
				
				browser = new JMenuItem("Browser");
				browser.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/internet_alt.png")));
				browser.addActionListener(this);
				download.add(browser);
				
				link = new JMenuItem("Link URL");
				link.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/edit_icon.png")));
				link.addActionListener(this);
				download.add(link);
			}

			recycle = new JMenuItem("Send To Recycle Bin");
			recycle.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/recycler.png")));
			recycle.addActionListener(this);

			delete = new JMenuItem("Delete Permanently");
			delete.setIcon(new ImageIcon(FurkManager.class.getResource("img/sm/edit_delete.png")));
			delete.addActionListener(this);

			if (cFile instanceof FurkFile){
				FurkFile cfFile=(FurkFile)cFile;
				
				if(cfFile.getDeletedReason()==null||cfFile.getDeletedReason().equals("")){
					add(view);
					add(fview);
					
					if(!cfFile.isLinked()) add(add);
					
					add(cp);
					
					if(cfFile.getUrlPage()==null||cfFile.getUrlPage().length()<4)
						fview.setEnabled(false);
					
					if (cfFile.isReady()) add(download);
					if(cfFile.isLinked()) add(recycle);
				}
				else{
					add(add);
					add(delete);
				}
			} else {
				add(add_dl);
			}

		}

		@Override
		public void actionPerformed(final ActionEvent e) {
			if (action)
				return;
			Thread t = new Thread(new Runnable() {
				private Icon curr;

				public void run() {
					action = true;
					if (mode == SMALL_MODE) {
						curr = icon_type.getIcon();
						icon_type.setIcon(new ImageIcon(FurkManager.class.getResource("img/ajax-loader-48.gif")));
						icon_type.repaint();
					} else if (mode == WIDE_MODE) {
						label_loading.setVisible(true);
					}
					try {
						Object src = e.getSource();
						if (src.equals(view)) {
							// View
							new FurkFileView((FurkFile) cFile);
						}
						if (src.equals(fview)) {
							// Furk View
							UtilBox.openUrl("https://www.furk.net"+((FurkFile) cFile).getUrlPage());
						}
						if(src.equals(cp)){
							UtilBox.sendToClipboard("https://www.furk.net"+((FurkFile) cFile).getUrlPage());
						}
						if (src.equals(add)) {
							// Add To My Files
							API_File.link(new String[] { ((FurkFile) cFile)
									.getID() });
							FurkManager.trayAlert(
								FurkManager.TRAY_INFO,"Added",
								"File '" + cFile.getName()+ "' Added To My Files",
								null);
							if(mode==WIDE_MODE){
								check_linked.setSelected(true);
							}
							parentRefresh();
						}
						if (src.equals(add_dl)) {
							// Add To My Downloads
							API_Download.addHash(cFile.getInfoHash());
							FurkManager.trayAlert(FurkManager.TRAY_INFO,
									"Download Added",
									"'" + cFile.getName()+ "' Added To My Downloads",
									null);
						}
						if (src.equals(browser)) {
							// Browser Download
							UtilBox.openUrl(((FurkFile) cFile).getUrlDl());
						}
						if(src.equals(idm)){
							String path=SettingsManager.idmPath();
							Runtime.getRuntime().exec(new String[]{
								path,
								"-d",
								((FurkFile)cFile).getUrlDl()
							});
						}
						if (src.equals(internal)) {
							boolean dwld=DownloadManager.addDownload(
									((FurkFile)cFile).getName(),
								((FurkFile)cFile).getUrlDl(),
								DownloadManager.actionSaveTo(true),true);
							if(dwld) 
								FurkManager.trayAlert(
									FurkManager.TRAY_INFO, "Downloading...",
									"File "+cFile.getName()+
									" is being downloaded uing the internal downloader.",null);
						}
						if (src.equals(link)) {
							// Copy Link To Clipboard
							UtilBox.sendToClipboard(((FurkFile) cFile)
									.getUrlDl());
							FurkManager.trayAlert(FurkManager.TRAY_INFO,
									"Link Copied",
									"Download link to '" + cFile.getName()+ "' Copied To Clipboard",
									null);
						}
						if (src.equals(recycle)) {
							// Recycle
							String id = ((FurkFile) cFile).getID();
							if(API_File.unlink(new String[] { id })){
								FurkManager.trayAlert(FurkManager.TRAY_INFO,
									"Deleted",
									"File '" + cFile.getName()+ "' Sent To Recycle Bin",
									null);
							}
							parentRefresh();
						}
						if (src.equals(delete)) {
							// Permanently Delete
							String id = ((FurkFile) cFile).getID();
							API_File.clear(new String[] { id });
							parentRefresh();
						}
					} catch (Exception e) {
					}
					if (mode == SMALL_MODE) {
						icon_type.setIcon(curr);
						icon_type.repaint();
					} else {
						label_loading.setVisible(false);
					}
					action = false;
				}
			});
			t.start();
		}
	}
	
	public FileIcon(APIObject o){
		this.cFile = o;
		this.mode = WIDE_MODE;
		this.thisPanel = this;
		
		bgc = UtilBox.getColor(Math.abs(o.getInfoHash().hashCode()));
		setBackground(bgc);
		
		initWideMode(o);
		
		UtilBox.addMouseListenerToAll(this, new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getButton() == MouseEvent.BUTTON1
						&& evt.getClickCount() == 2) {
					if (cFile instanceof FurkFile)
						if(
							((FurkFile) cFile).getDeletedReason()==null||
							((FurkFile) cFile).getDeletedReason().equals("")
						)//Not Deleted
						new FurkFileView((FurkFile) cFile);
				}
				if (evt.getButton() == MouseEvent.BUTTON3
						&& evt.getClickCount() == 1) {
					ContextMenu c = new ContextMenu();
					c.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});
	}
	
	public FileIcon(FurkFile o){
		this.cFile = o;
		this.mode = SMALL_MODE;
		this.thisPanel = this;
		
		bgc = UtilBox.getColor(Math.abs(o.getInfoHash().hashCode()));
		setBackground(bgc);
		
		initSmallMode(o);
		
		UtilBox.addMouseListenerToAll(this, new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getButton() == MouseEvent.BUTTON1
						&& evt.getClickCount() == 2) {
					if (cFile instanceof FurkFile)
						if(
							((FurkFile) cFile).getDeletedReason()==null||
							((FurkFile) cFile).getDeletedReason().equals("")
						)//Not Deleted
						new FurkFileView((FurkFile) cFile);
				}
				if (evt.getButton() == MouseEvent.BUTTON3
						&& evt.getClickCount() == 1) {
					ContextMenu c = new ContextMenu();
					c.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});
	}

	public FileIcon(int mode, APIObject o) {
		this.cFile = o;
		this.mode = mode;
		this.thisPanel = this;

		setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));

		bgc = UtilBox.getColor(Math.abs(o.getInfoHash().hashCode()));
		setBackground(bgc);

		if (mode == SMALL_MODE) {
			initSmallMode(o);
			// return;
		} else {
			initWideMode(o);
		}

		UtilBox.addMouseListenerToAll(this, new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent evt) {
				if (evt.getButton() == MouseEvent.BUTTON1
						&& evt.getClickCount() == 2) {
					if (cFile instanceof FurkFile)
						if(
							((FurkFile) cFile).getDeletedReason()==null||
							((FurkFile) cFile).getDeletedReason().equals("")
						)//Not Deleted
						new FurkFileView((FurkFile) cFile);
				}
				if (evt.getButton() == MouseEvent.BUTTON3
						&& evt.getClickCount() == 1) {
					ContextMenu c = new ContextMenu();
					c.show(evt.getComponent(), evt.getX(), evt.getY());
				}
			}
		});

	}

	public void initSmallMode(final APIObject o) {
		if (!(o instanceof FurkFile)) {
			return;
		}
		setPreferredSize(new Dimension(145, 120));
		setLayout(null);

		final FurkFile f = (FurkFile) o;

		icon_type = new JLabel();
		icon_type.setHorizontalAlignment(SwingConstants.CENTER);
		String ty = f.getType();
		ImageIcon icon = null;
		if (ty == null)
			ty = "";
		if (ty.equals("audio"))
			icon = new ImageIcon(FurkManager.class.getResource("img/audio-48.png"));
		else if (ty.equals("video"))
			icon = new ImageIcon(FurkManager.class.getResource("img/video-48.png"));
		else if (ty.equals("image"))
			icon = new ImageIcon(FurkManager.class.getResource("img/image-48.png"));
		else
			icon = new ImageIcon(FurkManager.class.getResource("img/fr-32.png"));
		icon_type.setIcon(icon);
		icon_type.setBorder(new BevelBorder(BevelBorder.RAISED, null, null,
				null, null));
		icon_type.setBounds(12, 12, 50, 50);
		add(icon_type);

		label_size = new JLabel(f.getSizeString());
		label_size.setBounds(63, 12, 80, 21);
		add(label_size);

		check_ready = new JCheckBox("Ready");
		check_ready.setSelected(f.isReady());
		check_ready.setOpaque(false);
		check_ready.setBounds(63, 41, 80, 21);
		check_ready.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				check_ready.setSelected(f.isReady());
			}
		});
		add(check_ready);

		txt_name = new JTextArea();
		txt_name.setEditable(false);
		txt_name.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		txt_name.setLineWrap(true);
		txt_name.setWrapStyleWord(true);
		txt_name.setFont(new Font("Dialog", Font.BOLD, 10));
		txt_name.setColumns(20);
		txt_name.setText(f.getName());
		txt_name.setToolTipText(f.getName());
		txt_name.setOpaque(false);
		txt_name.setBounds(4, 64, 138, 53);
		add(txt_name);
	}

	public void initWideMode(final APIObject o) {

		setPreferredSize(new Dimension(510, 99));
		setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		setLayout(null);

		input_name = new JLabel();
		input_name.setFont(new Font("Dialog", Font.BOLD, 13));

		input_name
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		input_name.setText(o.getName());
		input_name.setBounds(78, 12, 420, 20);
		add(input_name);

		input_size = new JLabel();
		input_size
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		input_size.setText(o.getSizeString());
		input_size.setBounds(78, 67, 96, 20);
		add(input_size);

		input_hash = new JLabel();
		input_hash
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		input_hash.setText(o.getInfoHash());
		input_hash.setBounds(78, 40, 311, 20);
		add(input_hash);

		label_name = new JLabel("Name:");
		label_name.setBounds(12, 14, 38, 16);
		add(label_name);

		label_hash = new JLabel("Info Hash:");
		label_hash.setBounds(12, 42, 64, 16);
		add(label_hash);

		label_size = new JLabel("Size: ");
		label_size.setBounds(12, 69, 55, 16);
		add(label_size);

		label_loading = new JLabel();
		label_loading.setBounds(394, 40, 48, 48);
		label_loading.setIcon(new ImageIcon(FurkManager.class.getResource("img/ajax-loader-48.gif")));
		label_loading.setVisible(false);
		add(label_loading);

		if (o instanceof FurkFile) {

			label_furkview = new JLabel();
			label_furkview.setToolTipText("Double Click To View On Furk.net");
			label_furkview.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1)
						UtilBox.openUrl(((FurkFile) o).getUrlPage());
				}

				@Override
				public void mousePressed(MouseEvent e) {
					label_furkview.setBorder(new BevelBorder(
							BevelBorder.LOWERED));
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					label_furkview
							.setBorder(new BevelBorder(BevelBorder.RAISED));
				}
			});
			label_furkview.setIcon(new ImageIcon(FurkManager.class.getResource("img/fr-16.png")));
			label_furkview.setBorder(new BevelBorder(BevelBorder.RAISED));
			label_furkview.setBounds(453, 67, 20, 20);
			add(label_furkview);

			label_open = new JLabel();
			label_open.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getButton() == MouseEvent.BUTTON1)
						new FurkFileView((FurkFile) o);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					label_open.setBorder(new BevelBorder(BevelBorder.LOWERED));
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					label_open.setBorder(new BevelBorder(BevelBorder.RAISED));
				}
			});
			label_open.setToolTipText("Click To Open File Details");
			label_open.setIcon(new ImageIcon(getClass().getResource(
					"/javax/swing/plaf/metal/icons/ocean/maximize.gif")));
			label_open.setBorder(new BevelBorder(BevelBorder.RAISED));
			label_open.setBounds(478, 67, 20, 20);
			add(label_open);

			check_linked = new JCheckBox("In My Files");
			check_linked.setOpaque(false);
			check_linked.setBounds(188, 67, 115, 20);
			check_linked.setSelected(((FurkFile) o).isLinked());
			check_linked.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					check_linked.setSelected(((FurkFile) o).isLinked());
				}
			});
			add(check_linked);
		}
	}

	@Override
	public int compareTo(FileIcon o) {
		return o.input_name.getText().compareTo(this.input_name.getText());
	}
}
