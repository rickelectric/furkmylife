package rickelectric.swingmods;

import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.border.BevelBorder;

public class JButtonLabel extends JLabel {
	private static final long serialVersionUID = 1L;
	private static final Font defFont = new Font(Font.DIALOG,Font.BOLD,12);
	private Runnable action;
	
	public JButtonLabel(String title,Runnable action){
		this(title);
		this.action = action;
	}

	public JButtonLabel(String title) {
		super(title);
		action=null;
		
		final BevelBorder raised = new BevelBorder(BevelBorder.RAISED);
		final BevelBorder lowered = new BevelBorder(BevelBorder.LOWERED);
		setBorder(raised);
		setHorizontalAlignment(CENTER);
		setFont(defFont);
		
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
			}
			@Override
			public void mousePressed(MouseEvent e){
				setBorder(lowered);
			}
			@Override
			public void mouseReleased(MouseEvent e){
				setBorder(raised);
				if(JButtonLabel.this.action!=null)
					JButtonLabel.this.action.run();
			}
		});
	}

	public void setAction(Runnable action) {
		this.action=action;
	}

	public void invoke() {
		if(JButtonLabel.this.action!=null)
			JButtonLabel.this.action.run();
	}

}
