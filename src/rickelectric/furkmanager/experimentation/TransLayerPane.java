import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class TransLayerPane {
    private JFrame frame = new JFrame();
    private JLayeredPane lpane = new JLayeredPane();
    private TranslucentPane panelBlue = new TranslucentPane();
    Color c=new Color(0,0,255);
    
    
	public TransLayerPane() {
		
        frame.setPreferredSize(new Dimension(600, 400));
        frame.setLayout(new BorderLayout());
        frame.add(lpane, BorderLayout.CENTER);
        lpane.setBounds(0, 0, 600, 400);
        
        panelBlue.setBackground(c);
        panelBlue.setBounds(0, 0, 600, 400);
        panelBlue.setAlpha(1f);
        
        final JLabel stat=new JLabel();
        stat.setBorder(new BevelBorder(BevelBorder.LOWERED));
        stat.setText("Opacity of Green: 100%");
        frame.add(stat,BorderLayout.SOUTH);
        
        
        final JSlider s=new JSlider();
        s.setMinimum(0);
        s.setMaximum(255);
        s.setValue(0);
        s.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				int val=s.getValue();
				Color x=new Color(c.getRed(),val,c.getBlue());
				panelBlue.setBackground(x);
				c=x;
				stat.setText("Lumen of Green: "+val);
			}
        	
        });
        frame.add(s,BorderLayout.NORTH);
        
        final JSlider s2=new JSlider();
        s2.setOrientation(SwingConstants.VERTICAL);
        s2.setMinimum(0);
        s2.setMaximum(255);
        s2.setValue(255);
        s2.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				int val=s2.getValue();
				Color x=new Color(c.getRed(),c.getGreen(),val);
				panelBlue.setBackground(x);
				c=x;
				stat.setText("Lumen of Blue: "+val);
			}
        	
        });
        frame.add(s2,BorderLayout.EAST);
        
        final JSlider s3=new JSlider();
        s3.setOrientation(SwingConstants.VERTICAL);
        s3.setMinimum(0);
        s3.setMaximum(255);
        s3.setValue(0);
        s3.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent arg0) {
				int val=s3.getValue();
				Color x=new Color(val,c.getGreen(),c.getBlue());
				panelBlue.setBackground(x);
				c=x;
				stat.setText("Lumen of Red: "+val);
			}
        	
        });
        frame.add(s3,BorderLayout.WEST);
        
        frame.getContentPane().setBackground(Color.WHITE);
        
        lpane.add(panelBlue, new Integer(0), 0);
        //lpane.add(panelGreen, new Integer(1), 0);
        //lpane.add(panelRed, new Integer(2), 0);
        frame.pack();
        frame.setVisible(true);
    }


    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		new TransLayerPane();
	}

}