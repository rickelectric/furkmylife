package rickelectric.furkmanager;
import java.awt.AWTException;
import java.awt.Font;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.windows.AddDownloadFrame;

public class FurkTrayIcon {

	public static void main(String[] a) {
		new FurkTrayIcon();
	}

	private static TrayIcon trayIcon = null;
	
	private ActionListener defAction = new ActionListener() {
		public void actionPerformed(ActionEvent e) {
			ThreadPool.run(new Runnable() {
				public void run() {
					FurkManager.appRun();
				}
			});
		}
	};

	public FurkTrayIcon() {
		
		if (trayIcon != null)
			return;

		if (SystemTray.isSupported()) {
			SystemTray tray = SystemTray.getSystemTray();
			Image image = Toolkit.getDefaultToolkit().getImage(FurkManager.class.getResource("img/fr.png"));
			
			ActionListener exitAction = new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					if (APIBridge.key() != null)
						popupMessage("Exiting", "Logging out. Please wait.",null);
					FurkManager.exit();
				}
			};

			final PopupMenu popup = new PopupMenu();
			// create menu item for the default action
			MenuItem showApp = new MenuItem("Show Main Window");
			showApp.addActionListener(defAction);
			popup.add(showApp);

			MenuItem addDl = new MenuItem("Add Furk Download");
			addDl.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					new AddDownloadFrame().setVisible(true);
				}
			});
			popup.add(addDl);
			
			MenuItem dlsView=new MenuItem("Show File Download Manager");
			dlsView.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e){
					FurkManager.downloader(true);
				}
			});
			popup.add(dlsView);
			popup.setFont(new Font("Dialog",Font.BOLD,12));

			MenuItem anim = new MenuItem("Test Loading Animation");
			anim.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					loading();
					UtilBox.pause(2000);
					stopLoading();
				}
			});
			popup.add(anim);

			MenuItem hardExitApp = new MenuItem("Exit");
			hardExitApp.addActionListener(exitAction);
			popup.add(hardExitApp);
			// construct a TrayIcon
			trayIcon = new TrayIcon(image, "FurkManager", popup);
			trayIcon.setImageAutoSize(true);

			try {
				tray.add(trayIcon);
			} catch (AWTException e) {
				System.err.println(e);
			}

		} else {
			throw new RuntimeException("");
		}
	}

	Image currIcon = null;

	public void loading() {
		final Toolkit def = Toolkit.getDefaultToolkit();
		currIcon = trayIcon.getImage();
		trayIcon.setImage(def.getImage(FurkManager.class.getResource("img/ajax-loader.gif")));
	}

	public void stopLoading() {
		if (currIcon == null)
			return;
		trayIcon.setImage(currIcon);
	}
	
	public void actionRemove(){
		for(ActionListener a:trayIcon.getActionListeners()){
			trayIcon.removeActionListener(a);
		}
	}

	public void popupMessage(final String title, final String text,final Runnable clickAction){
		new Thread(new Runnable(){
			public void run(){
				actionRemove();
				trayIcon.displayMessage(title, text, TrayIcon.MessageType.NONE);
				if(clickAction!=null) trayIcon.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e){
						clickAction.run();
						actionRemove();
						trayIcon.addActionListener(defAction);
					}
				});
				else 
					trayIcon.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							trayIcon.addActionListener(defAction);
						}
					});
				try{Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				}catch(InterruptedException e){}
			}
		}).start();
	}

	public void popupInfo(final String title, final String text,final Runnable clickAction){
		new Thread(new Runnable(){
			public void run(){
				actionRemove();
				trayIcon.displayMessage(title, text, TrayIcon.MessageType.INFO);
				if(clickAction!=null){
					trayIcon.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							clickAction.run();
							actionRemove();
							trayIcon.addActionListener(defAction);
						}
					});
				}
				else 
					trayIcon.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							trayIcon.addActionListener(defAction);
						}
					});
				try{
					Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				}catch(InterruptedException e){}
			}
		}).start();
	}

	public void popupWarning(final String title, final String text,final Runnable clickAction){
		new Thread(new Runnable(){
			public void run(){
				actionRemove();
				trayIcon.displayMessage(title, text, TrayIcon.MessageType.WARNING);
				if(clickAction!=null){
					trayIcon.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							clickAction.run();
							actionRemove();
							trayIcon.addActionListener(defAction);
						}
					});
				}
				else 
					trayIcon.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							trayIcon.addActionListener(defAction);
						}
					});
				try{
					Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				}catch(InterruptedException e){}
			}
		}).start();
	}

	public void popupError(final String title, final String text,final Runnable clickAction){
		new Thread(new Runnable(){
			public void run(){
				actionRemove();
				trayIcon.displayMessage(title, text, TrayIcon.MessageType.ERROR);
				if(clickAction!=null){
					trayIcon.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							clickAction.run();
							actionRemove();
							trayIcon.addActionListener(defAction);
						}
					});
				}
				else 
					trayIcon.addActionListener(new ActionListener(){
						public void actionPerformed(ActionEvent e){
							trayIcon.addActionListener(defAction);
						}
					});
				try{
					Thread.sleep(4000);
					actionRemove();
					trayIcon.addActionListener(defAction);
				}catch(InterruptedException e){}
			}
		}).start();
	}

}
