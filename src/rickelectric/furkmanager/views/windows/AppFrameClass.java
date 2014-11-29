package rickelectric.furkmanager.views.windows;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.Statable;

public abstract class AppFrameClass extends JFrame implements Statable {
	private static final long serialVersionUID = 1L;

	protected JPanel afContentPane;
	private Container panel_main;
	private JLabel status_bar;

	@Override
	public void setTitle(String title) {
		super.setTitle("Furk Manager: " + title);
	}

	public AppFrameClass() {
		super.setTitle("Furk My Life!");
		setIconImage(new ImageIcon(FurkManager.class.getResource("img/fr.png")).getImage());

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

		setBounds(100, 100, 450, 357);
		afContentPane = new JPanel();
		afContentPane.setBorder(new EmptyBorder(1, 2, 0, 2));
		super.setContentPane(afContentPane);
		afContentPane.setLayout(new BorderLayout(0, 0));

		status_bar = new JLabel("Ready");
		status_bar.setOpaque(false);
		status_bar.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null,
				null, null));
		afContentPane.add(status_bar, BorderLayout.SOUTH);

		panel_main = new JPanel();
		afContentPane.add(panel_main, BorderLayout.CENTER);
		panel_main.setLayout(null);
	}
	
	//private static APIConsole console=null;
	//private static ImgCacheViewer cache=null;
	
	public static final KeyListener consoleAdp=new KeyAdapter(){
		@Override
		public void keyReleased(KeyEvent e){
			if(e.getKeyCode()==KeyEvent.VK_F12){
				/*if(console==null){
					console=new APIConsole();
				}
				console.setVisible(true);
				*/
				FurkManager.showConsole(true);
			}
				
		}
	};
	
	public void addConsole(){
		UtilBox.addKeyListenerToAll(this,consoleAdp);
	}
	
	public static final KeyListener cacheAdp=new KeyAdapter(){
		@Override
		public void keyReleased(KeyEvent e){
			if(e.getKeyCode()==KeyEvent.VK_F11){
				/*if(cache==null){
					cache=new ImgCacheViewer();
				}
				ImgCacheViewer.modelTable();
				cache.setVisible(true);
				*/
				FurkManager.showImgCache(true);
			}
				
		}
	};
	
	public void addImgCacheViewer(){
		UtilBox.addKeyListenerToAll(this, cacheAdp);
	}

	@Override
	public final void setStatus(String s) {
		status_bar.setText(s);
	}

	public final String getStatus() {
		return status_bar.getText();
	}

	@Override
	public Component add(Component c) {
		return panel_main.add(c);
	}

	@Override
	public void setContentPane(Container c) {
		afContentPane.add(c, BorderLayout.CENTER);
		panel_main = c;
		afContentPane.setBackground(c.getBackground());

	}
	
	@Override
	public Container getContentPane(){
		return panel_main;
	}

	public void windowClose() {
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		for(WindowListener w:getWindowListeners()){
			removeWindowListener(w);
		}
		WindowListener wl = new WindowAdapter() {
			@Override
			public void windowIconified(WindowEvent e) {
				super.windowIconified(e);
				// TODO if(SettingsManager.minimizeToTray()) //When
				// SettingsManager is complete
				FurkManager.trayRun();
				((JFrame) e.getSource()).dispose();
			}

			@Override
			public void windowClosing(WindowEvent e) {
				windowExit();
			}
		};
		addWindowListener(wl);
	}
	
	public void exitOnClose(){
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		for(WindowListener w:getWindowListeners()){
			removeWindowListener(w);
		}
		WindowListener wl = new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				FurkManager.exit();
			}
		};
		addWindowListener(wl);
	}
	
	private void windowExit(){
		int opt = JOptionPane.showConfirmDialog(
						null,
						"Exit FurkManager?\nPress 'Yes' to exit, "
						+ "'No' to minimize to the system tray, or 'Cancel' to abort.");
		if (opt == JOptionPane.YES_OPTION) {
			setStatus("Logging Out. Please Wait...");
			FurkManager.trayAlert(FurkManager.TRAY_MESSAGE,"Logging Out.",
					"Logging Out. Please Wait",null);
			FurkManager.exit();
		}
		if (opt == JOptionPane.NO_OPTION) {
			FurkManager.trayRun();
			dispose();
		}
	}
}
