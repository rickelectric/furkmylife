package rickelectric.furkmanager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.network.AsyncDownload;
import rickelectric.furkmanager.utils.ImageManager;
import rickelectric.furkmanager.views.swingmods.JFadeLabel;
import rickelectric.furkmanager.views.swingmods.OpacEffects;

public class ImageViewer extends JDialog {
	private static final long serialVersionUID = 1L;

	private final Dimension screenSize = Toolkit.getDefaultToolkit()
			.getScreenSize();

	private JPanel contentPane;
	private JPanel titleBar;
	private JButton btn_X;
	private JLabel title_text;
	private JFadeLabel image_poster;
	private JButton next;
	private JButton prev;

	private JLabel infobar;

	private static int position;

	private static ArrayList<String> allImages;

	private static ImageViewer openDialog;

	public static void main(String[] args) throws MalformedURLException {
		// loadAll("Pictures", "C:\\Users\\Ionicle\\", true);
		// viewAll(20);
		AsyncDownload d = new AsyncDownload(
				new URL(
						"https://j5h7rua7iqqvh35k5nmake7rio.gcdn.biz/ss/4764133/3"));
		new ImageViewer(d).setVisible(true);
	}

	public static void loadAll(String name, String rootFolder,
			boolean exitOnClose) {
		try {
			allImages = new ArrayList<String>();
			File f = new File(rootFolder);
			File[] files = f.listFiles();
			for (File l : files)
				if (l.getName().endsWith(".png")
						|| l.getName().endsWith(".jpg"))
					allImages.add(rootFolder + l.getName());

			openDialog = new ImageViewer(name, allImages);
			if (exitOnClose)
				openDialog.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosing(WindowEvent e) {
						System.exit(0);
					}
				});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void viewAll(int start) {
		setPosition(start);
		openDialog.imageToDisplay(load(allImages.get(start)));
		openDialog.setVisible(true);
	}

	public static void show(String title, String imagePath) {
		if (imagePath == null || imagePath.length() == 0)
			return;
		BufferedImage img = load(imagePath);
		if (img == null)
			return;
		new ImageViewer(title, img).setVisible(true);
	}

	public static void show(String title, BufferedImage img) {
		if (img == null)
			return;
		new ImageViewer(title, img).setVisible(true);
	}

	/**
	 * @wbp.parser.constructor
	 */
	public ImageViewer(String title, final ArrayList<String> imagePaths) {
		if (imagePaths == null || imagePaths.size() == 0)
			return;
		construct(title);

		position = 0;

		next = new JButton();
		next.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (position + 1 >= imagePaths.size())
					position = -1;
				BufferedImage img = load(imagePaths.get(++position));
				if (img != null)
					imageToDisplay(img);
				infobar.setText(infobar.getText() + "    _|_    File: "
						+ imagePaths.get(position));
			}
		});
		next.setBorder(null);
		next.setBackground(Color.BLACK);
		next.setOpaque(false);
		next.setIcon(new ImageIcon(
				ImageViewer.class
						.getResource("/rickelectric/furkmanager/img/arrow_double_right.png")));
		contentPane.add(next, BorderLayout.EAST);

		prev = new JButton();
		prev.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (position - 1 < 0)
					position = imagePaths.size();
				BufferedImage img = load(imagePaths.get(--position));
				imageToDisplay(img);
				infobar.setText(infobar.getText() + "    _|_    File: "
						+ imagePaths.get(position));
			}
		});
		prev.setBorder(null);
		prev.setBackground(Color.BLACK);
		prev.setOpaque(false);
		prev.setIcon(new ImageIcon(
				ImageViewer.class
						.getResource("/rickelectric/furkmanager/img/arrow_double_left.png")));
		contentPane.add(prev, BorderLayout.WEST);
	}

	/**
	 * 
	 * @param d
	 *            A Download
	 */
	public ImageViewer(final AsyncDownload d) {
		construct("Downloading...");
		image_poster.setIcon(new ImageIcon(FurkManager.class
				.getResource("img/ajax-loader.gif")));
		new Thread(new Runnable() {
			@Override
			public void run() {
				d.run();
				setTitle(d.getFileName());
				title_text.setText(d.getFileName());
				imageToDisplay(d.toImage());
			}
		}).start();
	}

	public ImageViewer(String title, String imagePath) {
		BufferedImage img = load(imagePath);
		if (img != null) {
			construct(title);
			imageToDisplay(img);
		}
	}

	public static BufferedImage load(String imagePath) {
		try {
			File image = new File(imagePath);
			if (!image.exists())
				return null;
			BufferedImage img = ImageIO.read(image);
			return img;
		} catch (Exception ex) {
			return null;
		}
	}

	public ImageViewer(String title, BufferedImage image) {
		setIconImage(Toolkit
				.getDefaultToolkit()
				.getImage(
						ImageViewer.class
								.getResource("/rickelectric/furkmanager/img/image-48.png")));
		setModalityType(ModalityType.APPLICATION_MODAL);
		construct(title);
		imageToDisplay(image);
	}

	private void construct(String title) {
		setModal(true);
		setUndecorated(true);

		setSize(screenSize);
		setLocation(0, 0);
		setLocationRelativeTo(null);
		getContentPane().setLayout(new BorderLayout());

		contentPane = new JPanel();
		contentPane.setFocusCycleRoot(true);
		contentPane.setFocusTraversalPolicyProvider(true);
		contentPane.setBackground(Color.BLACK);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPane, BorderLayout.CENTER);
		contentPane.setLayout(new BorderLayout(0, 0));

		image_poster = new JFadeLabel();
		image_poster.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(image_poster);

		titleBar = new JPanel();
		titleBar.setPreferredSize(new Dimension(10, 26));
		getContentPane().add(titleBar, BorderLayout.NORTH);
		titleBar.setLayout(new BorderLayout(0, 0));

		btn_X = new JButton("");
		btn_X.setDoubleBuffered(true);
		btn_X.setFocusTraversalPolicyProvider(true);
		btn_X.setIcon(new ImageIcon(
				ImageViewer.class
						.getResource("/rickelectric/furkmanager/img/sm/edit_delete.png")));
		btn_X.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				close();
			}
		});
		titleBar.add(btn_X, BorderLayout.EAST);

		title_text = new JLabel(title);
		title_text.setFocusTraversalPolicyProvider(true);
		title_text.setFocusCycleRoot(true);
		title_text.setFont(new Font("Dialog", Font.BOLD, 14));
		title_text.setHorizontalAlignment(SwingConstants.LEFT);
		titleBar.add(title_text, BorderLayout.CENTER);

		infobar = new JLabel();
		infobar.setPreferredSize(new Dimension(0, 15));
		infobar.setFont(new Font("Dialog", Font.BOLD, 12));
		infobar.setIcon(new ImageIcon(ImageViewer.class
				.getResource("/rickelectric/furkmanager/img/tree/image-16.png")));
		infobar.setForeground(Color.WHITE);
		contentPane.add(infobar, BorderLayout.SOUTH);
		image_poster.requestFocus();
	}

	private void close() {
		ImageViewer.this.setVisible(false);
		ImageViewer.this.dispose();
		if (getWindowListeners().length > 0)
			getWindowListeners()[0].windowClosing(null);
	}

	public static int getPosition() {
		return position;
	}

	public static void setPosition(int pos) {
		if (pos < 0 || pos >= allImages.size())
			return;
		position = pos;
	}

	public int positionOf(String str) {
		return allImages.indexOf(str);
	}

	private boolean imageLoading = false;

	private void imageToDisplay(final BufferedImage x) {
		if (imageLoading)
			return;
		new Thread(new Runnable() {
			@Override
			public void run() {
				imageLoading = true;
				image_poster.setIcon(new ImageIcon(FurkManager.class
						.getResource("img/ajax-loader.gif")));
				if (x == null) {
					image_poster.setIcon(new ImageIcon(FurkManager.class
							.getResource("img/remove.png")));
					imageLoading = false;
					return;
				}
				BufferedImage img = x;
				int newHeight = titleBar.getPreferredSize().height
						+ infobar.getPreferredSize().height;
				if (x.getWidth() > x.getHeight()
						&& x.getWidth() > screenSize.width) {
					img = ImageManager.resizeImage(x, screenSize.width, -1);
				} else if (x.getHeight() > newHeight) {
					img = ImageManager.resizeImage(x, -1, screenSize.height
							- (newHeight));
				}
				infobar.setText("Screen Resolution: " + screenSize.width
						+ " x " + screenSize.height
						+ "   _|_   Original Image Size: " + x.getWidth()
						+ " x " + x.getHeight()
						+ "   _|_   Displayed Image Size:  " + img.getWidth()
						+ " x " + img.getHeight());

				ImageIcon i = new ImageIcon(img);
				image_poster.setAlpha(0);
				image_poster.setVisible(false);
				image_poster.setIcon(i);
				OpacEffects.fadeIn(image_poster, 50);

				imageLoading = false;
			}
		}).start();

	}

}
