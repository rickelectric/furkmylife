import java.awt.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class IconPanel2 extends JPanel implements Comparable<IconPanel2>{
	
	private static final long serialVersionUID = 1L;
	
	private Color bgc;
	
	public Color getBgc(){
		return bgc;
	}
	
    private Dimension size=new Dimension(549,99);
    private JLabel input_name,input_size,input_hash;
    private JLabel lblName;
    private JLabel lblInfoHash;
    private JLabel lblSize;
    private JLabel label;
    
	public IconPanel2(final APIObject o){
		new UtilBox();
		bgc=UtilBox.getRandomColor();
		setBackground(bgc);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		add(panel);
		panel.setOpaque(false);
		panel.setLayout(null);
		
		input_name = new JLabel();
		input_name.setFont(new Font("Dialog", Font.BOLD, 13));
		//input_name.setOpaque(false);
		input_name.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		input_name.setText(o.getName());
		input_name.setBounds(78, 12, 459, 20);
		panel.add(input_name);
		
		input_size = new JLabel();
		//input_size.setOpaque(false);
		input_size.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		input_size.setText(o.getSizeString());
		input_size.setBounds(78, 67, 96, 20);
		panel.add(input_size);
		
		input_hash = new JLabel();
		//input_hash.setOpaque(false);
		input_hash.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		input_hash.setText(o.getInfoHash());
		input_hash.setBounds(78, 40, 311, 20);
		panel.add(input_hash);
		
		lblName = new JLabel("Name:");
		lblName.setBounds(12, 14, 38, 16);
		panel.add(lblName);
		
		lblInfoHash = new JLabel("Info Hash:");
		lblInfoHash.setBounds(12, 42, 64, 16);
		panel.add(lblInfoHash);
		
		lblSize = new JLabel("Size: ");
		lblSize.setBounds(12, 69, 55, 16);
		panel.add(lblSize);
		
		if(o instanceof FurkFile){
			label = new JLabel("");
			label.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e){
					if(e.getButton()==MouseEvent.BUTTON1&&e.getClickCount()==1)
						new FurkFileView((FurkFile)o);
				}
			});
			label.setIcon(new ImageIcon(IconPanel2.class.getResource("/javax/swing/plaf/metal/icons/ocean/maximize.gif")));
			label.setBounds(521, 65, 16, 20);
			panel.add(label);
		}
		
		setPreferredSize(size);
		
		setVisible(true);
	}
	
	@Override
	public int compareTo(IconPanel2 o){
		return 1;
		//return o.label_title.getText().compareTo(label_title.getText());
	}
}
