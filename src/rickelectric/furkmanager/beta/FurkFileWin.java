package rickelectric.furkmanager.beta;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;

public class FurkFileWin extends JDialog {
	private static final long serialVersionUID = 1L;

	private FilePanel contentPane;
	private Ellipse oval_size, oval_title;
	private Rect ssRect, ss1, ss2, ss3;
	private Ellipse btn_fileTree, btn_download, btn_webview, btn_prev,
			btn_next;
	private Rect btn_close;
	private Ellipse ellipse;

	public FurkFileWin() {
		setUndecorated(true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setBounds(100, 100, 620, 465);
		setBackground(new Color(255,255,255,0));
		contentPane = new FilePanel();
		contentPane.setBackground(getBackground());
		setContentPane(contentPane);

		oval_size = new Ellipse();
		oval_size.setText("Size");
		oval_size.setForeground(Color.WHITE);
		oval_size.setBounds(200, 11, 214, 46);
		contentPane.add(oval_size);

		oval_title = new Ellipse();
		oval_title.setText("Title");
		oval_title.setBounds(124, 68, 355, 63);
		contentPane.add(oval_title);

		ss1 = new Rect();
		ss1.setForeground(Color.BLUE);
		ss1.setBounds(124, 184, 114, 106);
		contentPane.add(ss1);

		ss2 = new Rect();
		ss2.setForeground(Color.BLUE);
		ss2.setBounds(248, 184, 114, 106);
		contentPane.add(ss2);

		ss3 = new Rect();
		ss3.setForeground(Color.BLUE);
		ss3.setBounds(372, 184, 114, 106);
		contentPane.add(ss3);

		btn_fileTree = new Ellipse();
		btn_fileTree.setText("View File Tree");
		btn_fileTree.setBounds(62, 332, 142, 46);
		contentPane.add(btn_fileTree);

		btn_download = new Ellipse();
		btn_download.setText("Download");
		btn_download.setBounds(229, 332, 142, 46);
		contentPane.add(btn_download);

		btn_webview = new Ellipse();
		btn_webview.setText("Web View");
		btn_webview.setBounds(391, 332, 142, 46);
		contentPane.add(btn_webview);

		btn_prev = new Ellipse();
		btn_prev.setText("<<=");
		btn_prev.setBounds(62, 207, 51, 57);
		contentPane.add(btn_prev);

		btn_next = new Ellipse();
		btn_next.setText("=>>");
		btn_next.setBounds(496, 207, 51, 57);
		contentPane.add(btn_next);

		ssRect = new Rect();
		ssRect.setBounds(45, 167, 524, 144);
		contentPane.add(ssRect);

		btn_close = new Rect();
		btn_close.setText("Exit");
		btn_close.setBounds(195, 389, 219, 46);
		contentPane.add(btn_close);
		
		ellipse = new Ellipse();
		ellipse.setForeground(Color.LIGHT_GRAY);
		ellipse.setBounds(0, 0, 616, 460);
		contentPane.add(ellipse);
	}

	public static void main(String[] args) {
		FurkFileWin win = new FurkFileWin();
		win.setLocationRelativeTo(null);
		win.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		win.setVisible(true);
	}
}

class FilePanel extends JPanel {
	private static final long serialVersionUID = 1L;

	public FilePanel() {
		setBackground(Color.darkGray);
		setLayout(null);
	}

}

class Ellipse extends JComponent {
	private static final long serialVersionUID = 1L;

	private String text = "";

	public void setText(String s) {
		if (s == null)
			text = "";
		text = s;
	}
	
	public Ellipse(String txt){
		super();
		text=txt;
	}

	public Ellipse() {
		setBackground(new Color(255, 255, 255, 0));
		setForeground(Color.blue);
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(getForeground());
		g.fillOval(0, 0, getWidth(), getHeight());
		g.setFont(getFont());
		int w = g.getFontMetrics().stringWidth(text);
		g.setColor(Color.cyan);
		g.drawString(text,getWidth()/2-w/2,getHeight()/2+getFont().getSize()/2);

	}
}

class Rect extends JComponent {
	private static final long serialVersionUID = 1L;
	
	private String text = "Text";

	public void setText(String s) {
		if (s == null)
			text = "";
		text = s;
	}
	
	public Rect(String txt){
		super();
		text=txt;
	}

	public Rect() {
		setBackground(new Color(255, 255, 255, 0));
		setForeground(Color.red);
	}

	public void paint(Graphics g) {
		super.paint(g);
		g.setColor(getForeground());
		g.fillRect(0, 0, getWidth(), getHeight());
		g.setFont(getFont());
		int w = g.getFontMetrics().stringWidth(text);
		g.setColor(Color.cyan);
		g.drawString(text,getWidth()/2-w/2,getHeight()/2+getFont().getSize()/2);
	}
}
