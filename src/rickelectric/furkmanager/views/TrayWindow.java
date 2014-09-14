package rickelectric.furkmanager.views;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;

import org.jdesktop.swingx.border.DropShadowBorder;

import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.swingmods.TranslucentPane;

public abstract class TrayWindow extends JDialog {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args){
		final TrayWindow w = new  FMTrayBox();
		w.setVisible(true);
	}

	protected TranslucentPane contentPane;

	private JPanel panel, panel_buttons;
	private JButton btn_close, btn_minimize;
	private JLabel label_top;

	private boolean moveable = false;

	protected Point startPoint = null;

	MouseMotionListener pointMotion = new MouseMotionAdapter() {
		@Override
		public synchronized void mouseDragged(MouseEvent e) {
			if (startPoint == null)
				return;
			Point mp = MouseInfo.getPointerInfo().getLocation();
			int x = mp.x - startPoint.x;
			int y = mp.y - startPoint.y;
			TrayWindow.this.setLocation(new Point(x, y));
			repaint();
		}
	};

	MouseListener pointInit = new MouseAdapter() {
		@Override
		public void mousePressed(MouseEvent e) {
			startPoint = null;
			if (moveable)
				startPoint = TrayWindow.this.getMousePosition();
		}

		public void mouseReleased(MouseEvent e) {
			startPoint = null;
		}
	}, opacInit = new MouseAdapter() {
		@Override
		public void mouseExited(MouseEvent e) {
			setOpacity(0.5f);
		}

		@Override
		public void mouseEntered(MouseEvent e) {
			setOpacity(1f);
		}
	};

	public void addListeners() {
		UtilBox.addMouseListenerToAll(this, opacInit);

		UtilBox.addMouseListenerToAll(contentPane, pointInit);
		UtilBox.addMouseMotionListenerToAll(contentPane, pointMotion);
	}

	public TrayWindow(String title, Font font) {
		setTitle(title);
		setAlwaysOnTop(true);
		setUndecorated(true);
		setOpacity(0.5f);
		setPreferredSize(new Dimension(325, 400));

		contentPane = new TranslucentPane();

		contentPane.setBorder(new CompoundBorder(new DropShadowBorder(
				Color.BLUE, 10, 0.6f, 30, true, true, true, true),
				new EmptyBorder(3, 3, 3, 3)));

		contentPane.setAlpha(1.0f);
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);

		panel = new JPanel();
		panel.setPreferredSize(new Dimension(10, 35));
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BorderLayout(0, 0));

		label_top = new JLabel(title);
		label_top.setFont(font);
		panel.add(label_top, BorderLayout.CENTER);

		panel_buttons = new JPanel();
		panel.add(panel_buttons, BorderLayout.EAST);

		btn_minimize = new JButton("");
		btn_minimize
				.setIcon(new ImageIcon(
						TrayWindow.class
								.getResource("/javax/swing/plaf/metal/icons/ocean/minimize.gif")));
		panel_buttons.add(btn_minimize);

		btn_close = new JButton("");
		panel_buttons.add(btn_close);
		btn_close
				.setIcon(new ImageIcon(
						TrayWindow.class
								.getResource("/rickelectric/furkmanager/img/sm/edit_delete.png")));

		setSize(getPreferredSize());
	}

	@Override
	public void setSize(Dimension size) {
		super.setSize(size);
		position();
	}

	@Override
	public void setSize(int width, int height) {
		super.setSize(width, height);
		position();
	}

	public void onMinimize(final Runnable onMinimizeAction) {
		UtilBox.clearActionListeners(btn_minimize);
		btn_minimize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onMinimizeAction.run();
			}
		});
	}

	public void onClose(final Runnable onCloseAction) {
		UtilBox.clearActionListeners(btn_close);
		btn_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				onCloseAction.run();
			}
		});
	}

	public void setMoveable(boolean moveable) {
		this.moveable = moveable;

	}

	public void position() {
		Dimension scrnSize = Toolkit.getDefaultToolkit().getScreenSize();
		Rectangle winSize = GraphicsEnvironment.getLocalGraphicsEnvironment()
				.getMaximumWindowBounds();

		int taskBarHeight = scrnSize.height - winSize.height;

		int x = (scrnSize.width - getPreferredSize().width) - 3;
		int y = (scrnSize.height - getPreferredSize().height) - taskBarHeight
				- 3;
		setLocation(x, y);
	}

}
