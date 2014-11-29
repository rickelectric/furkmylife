package rickelectric.furkmanager.utils;

import java.util.HashMap;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseInputListener;
import org.jnativehook.mouse.NativeMouseListener;
import org.jnativehook.mouse.NativeMouseMotionListener;

//TODO Detect Clicks Beyond The Bounds Of The Popups / Buttons / Notifications 
//(Close on clickout when overlay is not active)
//TODO Detect When Mouse Passes Over Hotzones And Slide In Buttons

public class MouseActivity implements NativeMouseListener,
		NativeMouseInputListener, NativeMouseMotionListener {

	public GlobalScreen gscr;
	public static MouseActivity a;

	private int mouseX, mouseY;
	private HashMap<Integer, Boolean> mousePresses;

	public static void main(String[] args) throws NativeHookException {
		GlobalScreen.registerNativeHook();
	}

	public static synchronized MouseActivity getInstance() {
		if (a == null){
			a = new MouseActivity();
			try{
				GlobalScreen.registerNativeHook();
				GlobalScreen.getInstance().addNativeMouseListener(a);
				GlobalScreen.getInstance().addNativeMouseMotionListener(a);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		return a;
	}
	
	public static synchronized void destroyInstance(){
		GlobalScreen.getInstance().removeNativeMouseListener(a);
		GlobalScreen.getInstance().removeNativeMouseMotionListener(a);
		GlobalScreen.unregisterNativeHook();
		a=null;
		System.gc();
	}

	private MouseActivity() {
		mousePresses = new HashMap<Integer, Boolean>();
		mouseX = mouseY = 0;
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
		Boolean ml = mousePresses.get(NativeMouseEvent.BUTTON1);
		return ml == null ? false : ml;
	}
	
	public boolean right() {
		Boolean ml = mousePresses.get(NativeMouseEvent.BUTTON3);
		return ml == null ? false : ml;
	}

	@Override
	public void nativeMouseDragged(NativeMouseEvent e) {
		// N/A
	}

	@Override
	public void nativeMouseMoved(NativeMouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY();
		//System.out.println("Mouse: "+mouseX+","+mouseY);
	}

	@Override
	public void nativeMouseClicked(NativeMouseEvent e) {
		
	}

	@Override
	public void nativeMousePressed(NativeMouseEvent e) {
		mousePresses.put(e.getButton(), true);
	}

	@Override
	public void nativeMouseReleased(NativeMouseEvent e) {
		mousePresses.put(e.getButton(), false);
	}

}
