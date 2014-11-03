package rickelectric.furkmanager.views.windows;

import java.awt.Component;

public interface PrimaryEnv {
	
	boolean isVisible();
	void toFront();
	void setVisible(boolean b);
	void setLocationRelativeTo(Component c);
	void setEnabled(boolean b);
	void dispose();
	void userSettings();
	void settings();
	void loadMessages();
	void setStatus(String string);
	Component getContentPane();
	
	

}
