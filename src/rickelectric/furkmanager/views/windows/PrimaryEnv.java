package rickelectric.furkmanager.views.windows;

import java.awt.Component;
import java.awt.Window;

public interface PrimaryEnv {

	int AUDIO = 0, VIDEO = 1;

	boolean isVisible();

	void toFront();

	void setVisible(boolean b);

	void setLocationRelativeTo(Component c);

	void setEnabled(boolean b);

	void dispose();

	void main();

	void userSettings();

	void settings();

	void loadMessages();

	void setStatus(String string);

	Component getContentPane();

	void mediaCall(int mediaType, String mrl);

	Window getWindow();

	boolean isAlwaysOnTop();

	void setAlwaysOnTop(boolean b);

}
