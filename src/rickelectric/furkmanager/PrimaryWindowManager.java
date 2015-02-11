package rickelectric.furkmanager;

import java.awt.Component;
import java.awt.Window;

import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.windows.MainEnv;
import rickelectric.furkmanager.views.windows.MainWindow;
import rickelectric.furkmanager.views.windows.PrimaryEnv;

public class PrimaryWindowManager implements PrimaryEnv {

	public static final int ENV_MODE = 0, WIN_MODE = 1;

	private static PrimaryWindowManager thisInstance;

	private int mode;
	private MainEnv env;
	private MainWindow win;
	
	//private TranslucentPane[] panels;

	public static PrimaryWindowManager getInstance() {
		if (thisInstance == null) {
			thisInstance = new PrimaryWindowManager(SettingsManager
					.getInstance().getMainWinMode());
		}
		return thisInstance;
	}

	private PrimaryWindowManager(int mode) {
		this.mode = mode;
		initWithMode();
	}

	private void initWithMode() {
		if(mode==ENV_MODE){
			if(env==null) env = MainEnv.getInstance();
		}
	}

	@Override
	public boolean isVisible() {
		return mode == ENV_MODE ? env == null ? false : env.isVisible()
				: win == null ? false : win.isVisible();
	}

	@Override
	public void toFront() {
		if(mode == ENV_MODE && env != null) 
			env.toFront();
		else if(mode==WIN_MODE && win != null)
			win.toFront();
	}

	@Override
	public void setVisible(boolean b) {
		if(mode == ENV_MODE && env != null) 
			env.setVisible(b);
		else if(mode==WIN_MODE && win != null)
			win.setVisible(b);
	}

	@Override
	public void setLocationRelativeTo(Component c) {
		if(mode == ENV_MODE && env != null) 
			env.setLocationRelativeTo(c);
		else if(mode==WIN_MODE && win != null)
			win.setLocationRelativeTo(c);
	}

	@Override
	public void setEnabled(boolean b) {
		if(mode == ENV_MODE && env != null) 
			env.setEnabled(b);
		else if(mode==WIN_MODE && win != null)
			win.setEnabled(b);
	}

	@Override
	public void dispose() {
		if(mode == ENV_MODE && env != null) 
			env.dispose();
		else if(mode==WIN_MODE && win != null)
			win.dispose();
	}

	@Override
	public void main() {
		if(mode == ENV_MODE && env != null) 
			env.main();
		else if(mode==WIN_MODE && win != null)
			win.main();
	}

	@Override
	public void userSettings() {
		if(mode == ENV_MODE && env != null) 
			env.userSettings();
		else if(mode==WIN_MODE && win != null)
			win.userSettings();
	}

	@Override
	public void settings() {
		if(mode == ENV_MODE && env != null) 
			env.settings();
		else if(mode==WIN_MODE && win != null)
			win.settings();
	}

	@Override
	public void loadMessages() {
		if(mode == ENV_MODE && env != null) 
			env.loadMessages();
		else if(mode==WIN_MODE && win != null)
			win.loadMessages();
	}

	@Override
	public void setStatus(String string) {
		if(mode == ENV_MODE && env != null) 
			env.setStatus(string);
		else if(mode==WIN_MODE && win != null)
			win.setStatus(string);
	}

	@Override
	public Component getContentPane() {
		return mode == ENV_MODE ? env == null ? null : env.getContentPane()
				: win == null ? null : win.getContentPane();
	}

	@Override
	public void mediaCall(int mediaType, String mrl) {
		if(mode == ENV_MODE && env != null) 
			env.mediaCall(mediaType, mrl);
		else if(mode==WIN_MODE && win != null)
			win.mediaCall(mediaType, mrl);
	}

	@Override
	public Window getWindow() {
		return mode == ENV_MODE ? env == null ? null : env.getWindow()
				: win == null ? null : win.getWindow();
	}

	@Override
	public boolean isAlwaysOnTop() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAlwaysOnTop(boolean b) {
		// TODO Auto-generated method stub
		
	}

}
