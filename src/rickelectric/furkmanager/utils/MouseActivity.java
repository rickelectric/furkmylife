package rickelectric.furkmanager.utils;

import java.util.HashMap;

import de.ksquared.system.mouse.GlobalMouseListener;
import de.ksquared.system.mouse.MouseAdapter;
import de.ksquared.system.mouse.MouseEvent;

//TODO Detect Clicks Beyond The Bounds Of The Popups / Buttons / Notifications 
//(Close on clickout when overlay is not active)
//TODO Detect When Mouse Passes Over Hotzones And Slide In Buttons

public class MouseActivity extends MouseAdapter {

	private GlobalMouseListener gml;
	private static MouseActivity a;

	private int mouseX, mouseY;
	private HashMap<Integer, Boolean> mousePresses;

	public static synchronized MouseActivity getInstance() {
		if (a == null){
			a = new MouseActivity();
		}
		return a;
	}
	
	public static synchronized void destroyInstance(){
		if(a==null) return;
		a.gml.removeMouseListener(a);
		a=null;
		System.gc();
	}

	private MouseActivity() {
		gml=new GlobalMouseListener();
		mousePresses = new HashMap<Integer, Boolean>();
		mouseX = mouseY = 0;
		gml.addMouseListener(this);
	}

	public int getMouseX() {
		return mouseX;
	}

	public void setMouseX(int mouseX) {
		this.mouseX = mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public void setMouseY(int mouseY) {
		this.mouseY = mouseY;
	}

	public boolean left() {
		Boolean ml = mousePresses.get(MouseEvent.BUTTON_LEFT);
		return ml == null ? false : ml;
	}
	
	public boolean right() {
		Boolean ml = mousePresses.get(MouseEvent.BUTTON_RIGHT);
		return ml == null ? false : ml;
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
	}

	@Override
	public void mousePressed(MouseEvent e) {
		mousePresses.put(e.getButton(), true);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		mousePresses.put(e.getButton(), false);
	}

}
