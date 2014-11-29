package rickelectric.furkmanager.views.swingmods;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

public class JButtonLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	private static final Font defFont = new Font(Font.DIALOG,Font.BOLD,12);

	public JButtonLabel(String title, final Runnable onclick) {
		super(title);
		final BevelBorder raised = new BevelBorder(BevelBorder.RAISED);
		final BevelBorder lowered = new BevelBorder(BevelBorder.LOWERED);
		setBorder(raised);
		setHorizontalAlignment(CENTER);
		setFont(defFont);
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onclick.run();
			}
			@Override
			public void mousePressed(MouseEvent e){
				setBorder(lowered);
			}
			@Override
			public void mouseReleased(MouseEvent e){
				setBorder(raised);
			}
		});
	}

}
