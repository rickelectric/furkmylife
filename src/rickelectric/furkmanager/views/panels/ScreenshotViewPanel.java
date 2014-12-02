package rickelectric.furkmanager.views.panels;

import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.net.URL;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.models.FurkTFile;
import rickelectric.furkmanager.network.AsyncDownload;
import rickelectric.furkmanager.network.RequestCache;
import rickelectric.furkmanager.network.StreamDownloader;
import rickelectric.furkmanager.utils.ImageManager;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.views.ImageViewer;

public class ScreenshotViewPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	
	private JButton button_prev;
	private JButton button_next;
	
	private int currSS = 0;
	private boolean diffSizes;

	ScreenshotIcon[] ssf = null;
	
	public ScreenshotViewPanel(FurkFile ff){
		diffSizes=true;
		init(ff.getThumbs(),ff.getScreenshots());
	}
	
	public ScreenshotViewPanel(FurkTFile tf){
		diffSizes=false;
		tf.getThumbURL();
		try{
			BufferedImage img=StreamDownloader.getInstance().getImageStream(tf.getThumbURL(),8);
			int width=img.getWidth()/9;
			int height=img.getHeight();
			String[] imgloc=new String[9];
			for(int i=0;i<9;i++){
				imgloc[i]="http://"+tf.getThumbURL()+"/I"+i;
				RequestCache.ImageR.add(imgloc[i],img.getSubimage(width*i, 0, width, height));
			}
			
			init(imgloc,imgloc);
			
			JFrame frame=new JFrame();
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			frame.setLocationRelativeTo(null);
			
			frame.setContentPane(this);
			frame.setSize(430, 188);
			frame.setLocationRelativeTo(null);
			frame.setResizable(false);
			
			frame.setVisible(true);
			
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Failed to show screenshots.", "Failed", JOptionPane.ERROR_MESSAGE);
			e.printStackTrace();
			return;
		}
	}

	private void init(final String[] ssthumbs, final String[] ssfull) {
		setBorder(new TitledBorder(new BevelBorder(
				BevelBorder.LOWERED, null, null, null, null),
				"Screenshots", TitledBorder.LEADING, TitledBorder.TOP,
				null, null));
		setBounds(10, 191, 414, 157);
		setLayout(null);

		ActionListener backnext = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Object src = e.getSource();
				if (src.equals(button_prev)) {
					if (currSS >= 1)
						currSS--;
				} else if (src.equals(button_next)) {
					if (currSS < 6)
						currSS++;
				}
				loadScreenshots(ssthumbs, ssfull, currSS);
			}
		};

		button_prev = new JButton("");
		button_prev.setIcon(new ImageIcon(FurkManager.class.getResource("img/arrow_double_left.png")));
		button_prev.addActionListener(backnext);
		button_prev.setBounds(12, 125, 98, 20);
		add(button_prev);

		button_next = new JButton("");
		button_next.setIcon(new ImageIcon(FurkManager.class.getResource("img/arrow_double_right.png")));
		button_next.addActionListener(backnext);
		button_next.setBounds(304, 125, 98, 20);
		add(button_next);

		loadScreenshots(ssthumbs, ssfull, 0);
	}
	
	private void loadScreenshots(final String[] ssthumbs, final String[] ssfull, final int offset) {

		if (ssf == null)
			ssf = new ScreenshotIcon[9];

		for (int i = 0; i < 9; i++) {
			if (ssf != null) {
				try {
					remove(ssf[i]);
				} catch (Exception e) {
				}
			}
		}
		new Thread(new Runnable() {
			@Override
			public void run(){
				for (int z = offset; z < offset + 3; z++) {
					if (ssf[z] == null) {
						ssf[z] = new ScreenshotIcon(ssthumbs[z], ssfull[z], z + 1, diffSizes);
					}
					ssf[z].setBounds(12 + (134 * (z - offset)), 20, 120, 90);
					add(ssf[z]);
					repaint();
				}
			}
		}).start();

	}

}

class ScreenshotIcon extends JLabel {
	private static final long serialVersionUID = 1L;

	String ssThumbUrl, ssFullUrl;
	Icon currIcon, ajax;
	int ssNum = 0;

	MouseAdapter doubleClick = new MouseAdapter() {
		@Override
		public void mouseClicked(final MouseEvent e) {
			if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1) {
				setIcon(ajax);
				repaint();
				ThreadPool.run(new Runnable() {
					@Override
					public void run() {
						try {
							if (currIcon == null)
								loadImg();
							AsyncDownload d = new AsyncDownload(new URL(ssFullUrl));
							new ImageViewer(d).setVisible(true);
						} catch (Exception ex) {
						}
						setIcon(currIcon);
						repaint();
					}
				});
			}
		}
	};

	public ScreenshotIcon(String thumbUrl, String fullUrl, int num, boolean diffSizes) {
		super();
		ssNum = num;
		ajax = new ImageIcon(
				FurkManager.class.getResource("img/ajax-loader.gif"));
		setIcon(ajax);
		ssThumbUrl = thumbUrl;
		this.setHorizontalAlignment(CENTER);
		this.ssFullUrl = fullUrl;
		this.setLayout(new GridBagLayout());
		this.setToolTipText("Screenshot " + num);
		this.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		ThreadPool.run(new Runnable() {
			@Override
			public void run() {
				loadImg();
			}
		});
		if(diffSizes) this.addMouseListener(doubleClick);
	}

	@Override
	public void repaint() {
		super.repaint();
	}

	public void loadImg() {
		if (ssThumbUrl == null)
			return;
		try {
			BufferedImage ci = StreamDownloader.getInstance().getImageStream(ssThumbUrl, 8);
			float h = 90;
			ci = ImageManager.scaleImage(ci, (h / ci.getHeight()) * 100f);
			// ci=ImageManager.resizeImage(ci, 120, 90);
			this.setIcon(new ImageIcon(ci));
			currIcon = getIcon();
			repaint();
		} catch (Exception e) {
			this.setIcon(new ImageIcon(FurkManager.class
					.getResource("img/remove.png")));
			repaint();
		}
	}
}
