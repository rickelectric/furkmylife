package rickelectric.furkmanager.views.windows;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.panels.Main_DownloadView;
import rickelectric.furkmanager.views.panels.Main_FeedView;
import rickelectric.furkmanager.views.panels.Main_FileView;
import rickelectric.furkmanager.views.panels.Main_SettingsView;
import rickelectric.furkmanager.views.panels.Main_UserView;
import rickelectric.furkmanager.views.swingmods.OpacEffects;
import rickelectric.furkmanager.views.swingmods.Slideable;
import rickelectric.furkmanager.views.swingmods.TranslucentPane;

public class MainWindow extends AppFrameClass{
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	public static void main(String[] args){
		JFrame f=new MainWindow();
		f.setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	private JLabel[][] dashArray; 
	private TranslucentPane[] currPanel;
	private int cpNum=0;
	
	public MainWindow(){
		windowClose();
		
		TopMenuBar bar=new TopMenuBar();
		setJMenuBar(bar);
		
		contentPane=new JPanel();
		contentPane.setBackground(SystemColor.inactiveCaption);
		contentPane.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		setSize(595,620);
		setLocationRelativeTo(null);
		
		currPanel=new TranslucentPane[5];
		
		JPanel iconDashPanel = new JPanel();
		iconDashPanel.setBackground(Color.LIGHT_GRAY);
		iconDashPanel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		iconDashPanel.setBounds(10, 10, 563, 103);
		contentPane.add(iconDashPanel);
		iconDashPanel.setLayout(null);
		
		dashArray=new JLabel[2][5];
		
		dashArray[0][0] = new JLabel("Files");
		dashArray[0][0].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[0][0].setFont(new Font("Tahoma", Font.BOLD, 12));
		dashArray[0][0].setBounds(10, 6, 78, 16);
		dashArray[0][0].setOpaque(true);
		dashArray[0][0].setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		iconDashPanel.add(dashArray[0][0]);
		
		dashArray[0][1] = new JLabel("Downloads");
		dashArray[0][1].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[0][1].setFont(new Font("Tahoma", Font.BOLD, 12));
		dashArray[0][1].setBounds(122, 6, 78, 16);
		iconDashPanel.add(dashArray[0][1]);
		
		dashArray[0][2] = new JLabel("RSS Feeds");
		dashArray[0][2].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[0][2].setFont(new Font("Tahoma", Font.BOLD, 12));
		dashArray[0][2].setBounds(240, 6, 78, 16);
		iconDashPanel.add(dashArray[0][2]);
		
		dashArray[0][3] = new JLabel("Settings");
		dashArray[0][3].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[0][3].setFont(new Font("Tahoma", Font.BOLD, 12));
		dashArray[0][3].setBounds(358, 6, 78, 16);
		iconDashPanel.add(dashArray[0][3]);
		
		dashArray[0][4] = new JLabel("User");
		dashArray[0][4].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[0][4].setFont(new Font("Tahoma", Font.BOLD, 12));
		dashArray[0][4].setBounds(475, 7, 78, 16);
		iconDashPanel.add(dashArray[0][4]);
		
		MouseAdapter iconEffect=new MouseAdapter(){
			
			static final int IN=1,OUT=2,CLICKED=3;
			
			int borderState=OUT;
			
			public void mouseEntered(MouseEvent e){
				borderState=IN;
				JLabel src=(JLabel)e.getSource();
				src.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
				src.setHorizontalAlignment(JLabel.LEADING);
				
			}
			
			public void mouseExited(MouseEvent e){
				borderState=OUT;
				JLabel src=(JLabel)e.getSource();
				src.setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
				src.setHorizontalAlignment(JLabel.CENTER);
			}
			
			public void mousePressed(MouseEvent e){
				borderState=CLICKED;
				JLabel src=(JLabel)e.getSource();
				src.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
			}
			
			public void mouseReleased(MouseEvent e){
				if(borderState!=CLICKED) return;
				JLabel src=(JLabel)e.getSource();
				src.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
			}
			
			public void mouseClicked(MouseEvent e){
				if(paneChanging) return;
				if(e.getButton()==MouseEvent.BUTTON1){
					for(int i=0;i<5;i++){
						
						if(e.getSource().equals(dashArray[1][i])){
							if(e.getClickCount()>1&&cpNum==i){
								if(e.getClickCount()==3){
									new Thread(new Runnable(){
										public void run(){
											APIBridge.overrideCache(true);
											UtilBox.pause(500);
											APIBridge.overrideCache(false);
											
										}
									}).start();
								}
								if(i==0){
									((Main_FileView)currPanel[0]).refreshMyFiles(true);
								}
								else if(i==1){
									((Main_DownloadView)currPanel[1]).refreshMyDownloads(true);
								}
							}
							
							try{
								changeViewSection(i);
								//Hilight Title Of Current Panel
								dashArray[0][i].setOpaque(true);
								dashArray[0][i].setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
							}catch(Exception ex){
								dashArray[0][cpNum].setOpaque(true);
								dashArray[0][cpNum].setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
							}
							
							
						}
						else{
							dashArray[0][i].setOpaque(false);
							dashArray[0][i].setBorder(null);
						}
					}
				}
			}
		};
		
		dashArray[1][0] = new JLabel("");
		dashArray[1][0].addMouseListener(iconEffect);
		dashArray[1][0].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[1][0].setIcon(new ImageIcon(FurkManager.class.getResource("img/dash/Files-64.png")));
		dashArray[1][0].setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		dashArray[1][0].setBounds(10, 24, 78, 68);
		iconDashPanel.add(dashArray[1][0]);
		
		dashArray[1][1] = new JLabel("");
		dashArray[1][1].addMouseListener(iconEffect);
		dashArray[1][1].setIcon(new ImageIcon(FurkManager.class.getResource("img/dash/Download-64.png")));
		dashArray[1][1].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[1][1].setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		dashArray[1][1].setBounds(122, 24, 78, 68);
		iconDashPanel.add(dashArray[1][1]);
		
		dashArray[1][2] = new JLabel("");
		dashArray[1][2].addMouseListener(iconEffect);
		dashArray[1][2].setIcon(new ImageIcon(FurkManager.class.getResource("img/dash/RSS-64.png")));
		dashArray[1][2].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[1][2].setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		dashArray[1][2].setBounds(240, 24, 78, 68);
		iconDashPanel.add(dashArray[1][2]);
		
		dashArray[1][3] = new JLabel("");
		dashArray[1][3].addMouseListener(iconEffect);
		dashArray[1][3].setIcon(new ImageIcon(FurkManager.class.getResource("img/dash/Settings-64.png")));
		dashArray[1][3].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[1][3].setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		dashArray[1][3].setBounds(358, 24, 78, 68);
		iconDashPanel.add(dashArray[1][3]);
		
		dashArray[1][4] = new JLabel("");
		dashArray[1][4].addMouseListener(iconEffect);
		dashArray[1][4].setIcon(new ImageIcon(FurkManager.class.getResource("img/dash/User-64.png")));
		dashArray[1][4].setHorizontalAlignment(SwingConstants.CENTER);
		dashArray[1][4].setBorder(new EtchedBorder(EtchedBorder.RAISED, null, null));
		dashArray[1][4].setBounds(475, 24, 78, 68);
		iconDashPanel.add(dashArray[1][4]);
		
		TranslucentPane curr;
		curr = new Main_FileView();
		curr.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		curr.setBounds(10, 124, 563, 400);
		curr.setAlpha(1);
		contentPane.add(curr);
		currPanel[0]=curr;
		
		curr = new Main_DownloadView();
		curr.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		curr.setBounds(10, 124, 563, 400);
		curr.setAlpha(1);
		curr.setVisible(false);
		contentPane.add(curr);
		currPanel[1]=curr;
		
		curr = new Main_FeedView();
		curr.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		curr.setBounds(10, 124, 563, 400);
		curr.setAlpha(1);
		curr.setVisible(false);
		contentPane.add(curr);
		currPanel[2]=curr;
		
		curr = new Main_SettingsView();
		curr.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		curr.setBounds(10, 124, 563, 400);
		curr.setAlpha(1);
		curr.setVisible(false);
		contentPane.add(curr);
		currPanel[3]=curr;
		
		curr = new Main_UserView();
		curr.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		curr.setBounds(10, 124, 563, 400);
		curr.setAlpha(1);
		curr.setVisible(false);
		contentPane.add(curr);
		currPanel[4]=curr;
		
		addConsole();
		addImgCacheViewer();
		setResizable(false);
		setVisible(true);
	}

	class TopMenuBar extends JMenuBar implements ActionListener {
		private static final long serialVersionUID = 1L;
		
		private JMenuItem mi_addfdownload;
		private JMenuItem mi_logout;

		private JMenuItem mi_exit;

		private JMenuItem mi_downloadman;

		private JMenuItem mi_imgcache;

		private JMenuItem mi_apiconsole;

		private JMenuItem mi_topics;

		private JMenuItem mi_about;

		public TopMenuBar(){
			JMenu mnFile = new JMenu("File");
			add(mnFile);
			
			mi_addfdownload = new JMenuItem("Add Furk Download");
			mi_addfdownload.addActionListener(this);
			mnFile.add(mi_addfdownload);
			
			mi_logout = new JMenuItem("Log Out");
			mi_logout.addActionListener(this);
			mnFile.add(mi_logout);
			
			mi_exit = new JMenuItem("Exit");
			mi_exit.addActionListener(this);
			mnFile.add(mi_exit);
			
			JMenu mnTools = new JMenu("Tools");
			add(mnTools);
			
			mi_downloadman = new JMenuItem("File Download Manager");
			mi_downloadman.addActionListener(this);
			mnTools.add(mi_downloadman);
			
			mi_imgcache = new JMenuItem("Image Cache Viewer");
			mi_imgcache.addActionListener(this);
			mnTools.add(mi_imgcache);
			
			mi_apiconsole = new JMenuItem("API Console");
			mi_apiconsole.addActionListener(this);
			mnTools.add(mi_apiconsole);
			
			JMenu mnHelp = new JMenu("Help");
			add(mnHelp);
			
			mi_topics = new JMenuItem("Topics");
			mi_topics.addActionListener(this);
			mnHelp.add(mi_topics);
			
			mi_about = new JMenuItem("About");
			mi_about.addActionListener(this);
			mnHelp.add(mi_about);
		}

		public void actionPerformed(ActionEvent e){
			Object src=e.getSource();
			if(src.equals(mi_addfdownload)){
				new AddDownloadFrame().setVisible(true);
			}
			if(src.equals(mi_logout)){
				FurkManager.logout();
			}
			if(src.equals(mi_exit)){
				FurkManager.exit();
			}
			if(src.equals(mi_downloadman)){
				FurkManager.downloader(true);
			}
			if(src.equals(mi_apiconsole)){
				FurkManager.showConsole(true);
			}
			if(src.equals(mi_imgcache)){
				FurkManager.showImgCache(true);
			}
			if(src.equals(mi_topics)){
				
			}
			if(src.equals(mi_about)){
				
			}
			
		}
		
	}
	
	private boolean paneChanging=false;
	private void changeViewSection(final int sec){
		//TODO Invoke Panel Fade & Slide Out & In Change Depending On Current sec
		if(cpNum==sec) return;
		if(paneChanging||currPanel[cpNum].isSliding()) return;
		paneChanging=true;
		
		new Thread(new Runnable(){
			public void run(){
				Point loc=currPanel[cpNum].getLocation();
				if(!currPanel[cpNum].isVisible()) throw new RuntimeException("Invisible Current Panel");
				try{
					int direction=Slideable.LEFT;
					int locIn=25;
					if(sec<cpNum){
						direction=Slideable.RIGHT;
						locIn=-5;
					}
					OpacEffects.slide(currPanel[cpNum], 15, direction, 50, Slideable.OUT);
					UtilBox.pause(100);
					while(currPanel[cpNum].isSliding());
					
					currPanel[sec].setLocation(locIn, 124);
					OpacEffects.slide(currPanel[sec], 15, direction, 18, Slideable.IN);
					cpNum=sec;
					paneChanging=false;
				}catch(RuntimeException e){
					e.printStackTrace();
					currPanel[cpNum].setLocation(loc);
					currPanel[cpNum].setAlpha(1f);
					
					currPanel[sec].setVisible(false);
					currPanel[sec].setAlpha(0f);
					cpNum=sec;
					paneChanging=false;
					throw e;
				}
				paneChanging=false;
			}
		}).start();;
	}
	
	public void settings() {
		changeViewSection(4);
	}
}