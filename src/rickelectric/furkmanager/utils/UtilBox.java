package rickelectric.furkmanager.utils;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;
import java.util.Random;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.apache.commons.io.FileUtils;
//Toolkit,Color,Component,Container
//Clipboard
//Listeners (Key,Mouse,Acion)
//File/Folder Access

public class UtilBox {

	public static final int MAX_COLOR_INDEX=11;

	private static boolean initd = false;

	public static boolean divertUrlOpen = false;

	public static void main(String[] args) {
		new UtilBox();
		for (Color c : cols) {
			JFrame j = new JFrame();
			j.getContentPane().setBackground(c);
			j.add(new JLabel("( " + c.getRed() + ", " + c.getGreen() + ", "
					+ c.getBlue() + " )"));
			j.setSize(100, 100);
			j.setLocationRelativeTo(null);
			j.setVisible(true);
		}
	}
	
	public static void init(){
		new UtilBox();
	}

	public UtilBox() {
		if (initd)
			return;
		
		File tc = new File("dwtemp");
		if (!tc.exists() || !tc.isDirectory())
			try {
				FileUtils.copyFileToDirectory(new File("fr.ico"), new File(
						"dwtemp"), true);
				FileUtils.deleteQuietly(new File("dwtemp/fr.ico"));
			} catch (IOException e) {
		}
		
		createColours();
		initd = true;
	}

	public static int getRandomNumber(int range) {
		return r.nextInt(range);
	}

	public static void sendToClipboard(String s) {
		StringSelection stringSelection = new StringSelection(s);
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		clip.setContents(stringSelection, null);
	}

	public static String getFromClipboard() {
		Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable t = clip.getContents(null);
		if (t == null)
			return "";
		try {
			return (String) t.getTransferData(DataFlavor.stringFlavor);
		} catch (Exception e) {
			return null;
		}
	}

	private static Color[] cols = null;
	private static Random r = null;

	private static void createColours() {
		if (r == null)
			r = new Random();
		if (cols == null) {
			cols = new Color[12];
			cols[0] = new Color(238, 232, 170);
			cols[1] = new Color(255, 127, 80);
			cols[2] = new Color(0, 191, 255);
			cols[3] = new Color(221, 160, 221);
			cols[4] = new Color(188, 143, 143);
			cols[5] = new Color(255, 228, 181);
			cols[6] = new Color(250, 128, 114);
			cols[7] = new Color(219, 112, 147);
			cols[8] = new Color(248, 248, 255);
			cols[9] = new Color(82, 222, 22);
			cols[10] = new Color(200, 166, 202);
			cols[11] = new Color(255, 127, 80);
		}
	}

	public static Color dimColor(Color c) {
		int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
		if (r >= 40)
			r = r - 40;
		else
			r = 0;
		if (g >= 60)
			g = g - 60;
		else
			g = 0;
		if (b >= 80)
			b = b - 80;
		else
			b = 0;
		return new Color(r, g, b);
	}

	public static Color getRandomColor() {
		if (r == null)
			new UtilBox();
		int cIndex = r.nextInt(12);
		if (cols == null)
			createColours();
		return cols[cIndex];
	}
	
	public static Color getColor(int cid){
		if (r == null)
			new UtilBox();
		if(cid>MAX_COLOR_INDEX) cid=cid%MAX_COLOR_INDEX;
		if (cols == null)
			createColours();
		return cols[cid];
	}

	public static long getRandomLong() {
		return r.nextLong();
	}

	/**
	 * 
	 * @param url
	 *            Internet Address To Open.
	 */
	public static void openUrl(String url) {
		if (divertUrlOpen || url==null) return;
		try{
			Desktop.getDesktop().browse(URI.create(url));
		}catch (IOException e){
			System.err.println(e.getMessage());
		}
	}

	/**
	 * Pings the specified web address or ip address.
	 * 
	 * @param address
	 *            URL to ping
	 * @return round trip time(in ms)
	 */
	public static long ping(String address) {
		try {
			if (!address.contains("http://"))
				address = "http://" + address;
			URL url = new URL(address);
			HttpURLConnection urlConn = (HttpURLConnection) url
					.openConnection();
			urlConn.setConnectTimeout(10000);
			long startTime = System.currentTimeMillis();
			urlConn.connect();
			long endTime = System.currentTimeMillis();
			if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				long time = (endTime - startTime);
				return time;
			}
		} catch (MalformedURLException e1) {// e1.printStackTrace();
		} catch (IOException e) {// e.printStackTrace();
		}
		return -1;
	}

	/**
	 * 
	 * @param millis Number Of Milliseconds To Pause For.
	 */
	public static void pause(int millis) {
		long base = System.currentTimeMillis();
		while (System.currentTimeMillis() - base < millis)
			;
	}
	
	public static void wait(int millis){
		try{Thread.sleep(millis);}
		catch(InterruptedException e){}
	}

	public static String alphanum(String s) {
		if(s==null) return "";
		char[] str = s.toCharArray();
		s = "";
		for (char p:str) {
			if (Character.isAlphabetic(p) || Character.isDigit(p))
				s += p;
		}
		return s;
	}

	public static String charToString(char[] c) {
		if (c == null)
			return null;
		String s = "";
		for (char ch : c)
			s += ch;
		return s;
	}

	public static char[] stringToChar(String s) {
		if (s == null)
			return null;
		return s.toCharArray();
	}

	public static float compareSentence(String s1, String s2, int minLength) {
		int numMatches = 0;
		s1 = s1.toLowerCase();
		s2 = s2.toLowerCase();
		String[] s1s = s1.split(" "), s2s = s2.split(" ");
		int j = 0;
		for (int i = 0; i < s1s.length; i++) {
			s1s[i] = alphanum(s1s[i]);
		}
		for (int i = 0; i < s2s.length; i++) {
			s2s[i] = alphanum(s2s[i]);
		}
		String[] main, sub;
		if (s1s.length > s1s.length) {
			main = s1s;
			sub = s2s;
		} else {
			main = s2s;
			sub = s1s;
		}

		int maxnum = 0;
		for (int i = 0; i < main.length; i++) {
			if (main[i].length() >= minLength)
				maxnum++;
			for (j = 0; j < sub.length; j++) {
				if (sub[j].equals(main[i]) && sub[j].length() >= minLength) {
					numMatches++;
					break;
				}
			}
		}
		float per = ((float) numMatches / (float) maxnum) * 100f;
		return per;
	}

	public static void addKeyListenerToAll(Component parent,
			KeyListener listener) {
		if (parent instanceof AbstractButton) {
			AbstractButton a = (AbstractButton) parent;
			// Check If The Listener is Already There (Avoid Double Reactions)
			boolean is = false;
			KeyListener[] kl = a.getKeyListeners();
			for (KeyListener k : kl) {
				if (k.equals(listener)) {
					is = true;
					break;
				}
			}
			if (!is)
				a.addKeyListener(listener);
		}

		else if (parent instanceof JComponent) {
			JComponent a = (JComponent) parent;
			// Check If The Listener is Already There (Avoid Double Reactions)
			boolean is = false;
			KeyListener[] kl = a.getKeyListeners();
			for (KeyListener k : kl) {
				if (k.equals(listener)) {
					is = true;
					break;
				}
			}
			if (!is)
				a.addKeyListener(listener);
		}

		if (parent instanceof Container) {
			Component[] comps = ((Container) parent).getComponents();
			for (Component c : comps) {
				addKeyListenerToAll(c, listener);
			}
		}
	}

	public static void addMouseListenerToAll(Component parent,
			MouseListener listener) {
		if (parent instanceof AbstractButton) {
			AbstractButton a = (AbstractButton) parent;
			boolean is = false;
			MouseListener[] kl = a.getMouseListeners();
			for (MouseListener k : kl) {
				if (k.equals(listener)) {
					is = true;
					break;
				}
			}
			if (!is)
				a.addMouseListener(listener);
		}

		else if (parent instanceof JComponent) {
			JComponent a = (JComponent) parent;
			boolean is = false;
			MouseListener[] kl = a.getMouseListeners();
			for (MouseListener k : kl) {
				if (k.equals(listener)) {
					is = true;
					break;
				}
			}
			if (!is)
				a.addMouseListener(listener);
		}

		if (parent instanceof Container) {
			Component[] comps = ((Container) parent).getComponents();
			for (Component c : comps) {
				addMouseListenerToAll(c, listener);
			}
		}
	}
	
	public static void addMouseMotionListenerToAll(Component parent,
			MouseMotionListener listener) {
		if (parent instanceof AbstractButton) {
			AbstractButton a = (AbstractButton) parent;
			boolean is = false;
			MouseListener[] kl = a.getMouseListeners();
			for (MouseListener k : kl) {
				if (k.equals(listener)) {
					is = true;
					break;
				}
			}
			if (!is)
				a.addMouseMotionListener(listener);
		}

		else if (parent instanceof JComponent) {
			JComponent a = (JComponent) parent;
			boolean is = false;
			MouseListener[] kl = a.getMouseListeners();
			for (MouseListener k : kl) {
				if (k.equals(listener)) {
					is = true;
					break;
				}
			}
			if (!is)
				a.addMouseMotionListener(listener);
		}

		if (parent instanceof Container) {
			Component[] comps = ((Container) parent).getComponents();
			for (Component c : comps) {
				if(!(c instanceof JScrollPane))
				addMouseMotionListenerToAll(c, listener);
			}
		}
	}

	public static String openFile(String type) {
		JFileChooser fc = null;

		if (type.equals("Torrent")) {
			fc = new JFileChooser(System.getProperty("user.home"));
			fc.setFileFilter(new FileNameExtensionFilter(
					"Torrent File (.torrent)", "torrent"));
		} else {
			fc = new JFileChooser(System.getProperty("user.home"));
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}
		
		int returnVal = fc.showOpenDialog(null);

		File file = null;
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			file = fc.getSelectedFile();
			if (file.getPath().equals(""))
				return null;
			return file.getPath();
		}
		return null;
	}

	public static void applyProxySettings(){
		Properties systemSettings = System.getProperties();
		if (SettingsManager.proxyEnabled()) {
			// This can be put in a menu, updated via interface
			System.out.println("Setting proxy");
			
			systemSettings.put("http.proxySet", "true");
			systemSettings.put("http.proxyHost", SettingsManager.getProxyHost());
			systemSettings.put("http.proxyPort", SettingsManager.getProxyPort());
			
			systemSettings.put("http.proxyUser", SettingsManager.getProxyUser());
			systemSettings.put("http.proxyPassword", SettingsManager.getProxyPassword());
			systemSettings.put("http.nonProxyHosts", "localhost|127.0.0.1");
			System.setProperties(systemSettings);
		}
		else{
			systemSettings.put("http.proxySet", "false");
			System.setProperties(systemSettings);
		}
		
	}
	
	public static void openFileLocation(File location){
		try {
			if(location.exists()){
				String sPath="\""+location.getAbsolutePath()+"\"";
				Runtime.getRuntime().exec("explorer.exe /select,"+sPath);
			}
			else{
				String[] arg=location.getAbsolutePath().split("\\\\");
				String[] cmdArray=new String[2];
				cmdArray[0]="explorer.exe";
				int delim=arg.length;
				if(location.getAbsolutePath().contains(" ")){
					cmdArray[1]=arg[0];
					delim--;
				}
				else cmdArray[1]="/select,"+arg[0];
				
				if(arg.length>1)
					for(int i=1;i<delim;i++)
						cmdArray[1]+="\\"+arg[i];
				Runtime.getRuntime().exec(cmdArray);
			}
		}catch(IOException ioe){}
	}

	public static String regex(String b4){
		if(b4.contains("\\\\")) return b4;
		String after;
		String[] split=b4.split("\\\\");
		after=split[0];
		for(int i=1;i<split.length;i++)
			after+="\\\\"+split[i];
		return after;
	}

	public static void schedule(final int i, final Runnable runnable) {
		new Thread(new Runnable(){
			public void run(){
				try{Thread.sleep(i);}
				catch(InterruptedException e){}
				runnable.run();
			}
		}).start();
	}

	public static int numberParse(String str, int i) {
		char[] chars=str.toCharArray();
		str="";
		for(char c:chars){
			if(Character.isDigit(c)){
				str+=c;
			}
			else str="";
			if(str.length()==4) break;
		}
		if(str.length()<4) return -1;
		return Integer.parseInt(str);
	}

	public static void clearActionListeners(AbstractButton cmpt){
		for (ActionListener a : cmpt.getActionListeners()) {
			cmpt.removeActionListener(a);
		}
	}

}
