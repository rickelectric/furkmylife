import javax.swing.*;
import java.awt.*;
public class ScrollTest {

    private JPanel panel;
    private Icon[] icons = new Icon[3];

    public static void main( String[] args ){ 
        new ScrollTest();
    }

    public ScrollTest() {
        panel =new JPanel();

        // Use top to bottom layout in a column
        panel.setLayout( new BoxLayout( panel, BoxLayout.Y_AXIS ));


        panel.setBackground(Color.WHITE);

        int v=ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS;
        int h=ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS; 
        JScrollPane jsp=new JScrollPane(panel,v,h);
        jsp.setPreferredSize(new Dimension(600,600));
        jsp.setBounds(150,670,850,200);
        JFrame frame = new JFrame();
        frame.add(jsp); 

        // my addition to load sample icons
        loadImages();
        // simulate dynamic buttons 
        addButtons();

        frame.pack();
        frame.setVisible( true );

    }
    void loadImages() {
        icons[0] = new ImageIcon( "img/furk_logo.png" );
        icons[1] = new ImageIcon( "img/furk_logo.png" );
        icons[2] = new ImageIcon( "img/furk_logo.png" );
    }

    void addButtons() {
        for( int i = 0 ; i < icons.length ; i++ ) {
           JPanel button = new JPanel();
           //button.setSize(500, 120);
           Icon icon = icons[i];
           button.add(new JLabel(icon));
           // Set the button size to be the same as the icon size
           // The preferred size is used by the layout manager
           // to know what the component "better" size is.
           button.setPreferredSize(new Dimension(500,120));
           // This is IMPORTANT. The maximum size is used bythe layout manager 
           // to know "how big" could this component be.
           button.setMaximumSize( button.getPreferredSize() );
           panel.add( button );
        }
    }
}