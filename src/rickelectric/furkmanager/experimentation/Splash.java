package rickelectric.furkmanager.views;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.border.BevelBorder;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.views.swingmods.JFadeLabel;
import rickelectric.furkmanager.views.swingmods.JFadeLayeredPane;

public class Splash extends JWindow {
	private static final long serialVersionUID = 1L;

	private JFadeLayeredPane contentPane;
	private JFadeLabel splashImage;

	private JFadeLabel dispLine;

	private JProgressBar bLoad;

	private Timer fader;
	private JButton button_X;
	private JButton button_info;

	public static void main(String[] args) {
		FurkManager.LAF(2);
		new Splash().setVisible(true);
	}

	public Splash() {
		setAlwaysOnTop(true);
		setSize(566, 382);
		
		contentPane = new JFadeLayeredPane();
		contentPane.setAlpha(1.0f);
		contentPane.setOpaque(true);
		contentPane.setBackground(Color.LIGHT_GRAY);
		contentPane.setBorder(new CompoundBorder(new BevelBorder(
				BevelBorder.RAISED, null, null, null, null), new LineBorder(
				new Color(0, 0, 0), 5)));
		setContentPane(contentPane);
		setLocationRelativeTo(null);

		splashImage = new JFadeLabel();
		splashImage.setIcon(new ImageIcon(Splash.class
				.getResource("/rickelectric/furkmanager/img/Splash.png")));
		splashImage.setHorizontalAlignment(SwingConstants.CENTER);
		splashImage.setBounds(7, 7, 550, 340);
		contentPane.add(splashImage);

		dispLine = new JFadeLabel("Loading...");
		contentPane.setLayer(dispLine, 8);
		dispLine.setFont(new Font("Dialog", Font.BOLD, 14));
		dispLine.setBounds(6, 322, 551, 20);
		dispLine.setHorizontalAlignment(JLabel.CENTER);
		contentPane.add(dispLine);
		
		bLoad = new JProgressBar();
		bLoad.setFont(new Font("SansSerif", Font.BOLD | Font.ITALIC, 13));
		contentPane.setLayer(bLoad, 8);
		bLoad.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null,
				null));
		bLoad.setBounds(8, 354, 549, 20);
		bLoad.setForeground(Color.BLACK);
		bLoad.setOpaque(false);
		bLoad.setStringPainted(true);
		bLoad.setString("Please Wait...");
		bLoad.setIndeterminate(true);
		contentPane.add(bLoad);

		button_X = new JButton("");
		button_X.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				new Thread(onClose).start();
			}
		});
		button_X.setIcon(new ImageIcon(
				Splash.class
						.getResource("/rickelectric/furkmanager/img/sm/edit_delete.png")));
		contentPane.setLayer(button_X, 8);
		button_X.setBounds(527, 7, 32, 32);
		contentPane.add(button_X);

		button_info = new JButton("");
		button_info
				.setIcon(new ImageIcon(
						Splash.class
								.getResource("/rickelectric/furkmanager/img/sm/Information24.gif")));
		contentPane.setLayer(button_info, 8);
		button_info.setBounds(494, 7, 32, 32);
		contentPane.add(button_info);
	}
	
	private Runnable defClose=new Runnable() {
		public void run() {
			setVisible(false);
			while (isVisible()) {
				Splash.this.setText("Exiting...");
				try {
					Thread.sleep(100);
				} catch (Exception e) {
				}
			}
			FurkManager.exit();
		}
	};
	
	private Runnable onClose=null;
	
	public void onClose(Runnable closeAction){
		if(closeAction==null) onClose=defClose;
		else onClose=closeAction;
	}

	@Override
	public void setVisible(final boolean b) {
		if (!isVisible() && !b)
			return;
		super.setVisible(true);
		setOpacity(b ? 0 : 1);
		fader = new Timer(50, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				float newOpac = getOpacity() + (b ? 0.05f : -0.05f);
				if (newOpac > 1) {
					setOpacity(1);
					((Timer) e.getSource()).stop();
				} else if (newOpac < 0) {
					setOpacity(0);
					if (!b)
						setVisible(false);
					((Timer) e.getSource()).stop();
				} else {
					setOpacity(newOpac);
				}
			}
		});
		fader.start();
	}

	@Override
	public void setOpacity(float opacity) {
		super.setOpacity(opacity);
		this.contentPane.setAlpha(opacity);
	}

	@Override
	public boolean isVisible() {
		if (getOpacity() == 0)
			return false;
		return super.isVisible();
	}

	public void setText(String s) {
		dispLine.setText(s);
	}

	public String getText() {
		return dispLine.getText();
	}

	public void showClose(boolean b) {
		button_X.setVisible(b);
		button_info.setVisible(b);
	}
}
