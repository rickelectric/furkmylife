import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.border.*;

public class IconPane extends JPanel implements Scrollable,Comparable<IconPane>{
	
	private static final long serialVersionUID = 1L;
	
	/**Menu / MenuFunctions BEGIN **/
	@SuppressWarnings("serial")
	class ContextMenu extends JPopupMenu implements ActionListener{
		public ContextMenu(){
			
			JMenuItem open=new JMenuItem("View Details");
			open.addActionListener(this);
			JMenuItem watch=new JMenuItem("View On Furk");
			watch.addActionListener(this);
			JMenuItem edit=new JMenuItem("Add To My Account");
			edit.addActionListener(this);
			JMenuItem vimage=new JMenuItem("View Thumbnails");
			vimage.addActionListener(this);
			JMenuItem locate=new JMenuItem("Download File");
			locate.addActionListener(this);
			JMenuItem refdata=new JMenuItem("Refresh");
			refdata.addActionListener(this);
			JMenuItem getid=new JMenuItem("Get Item ID");
			getid.addActionListener(this);
			JMenuItem deleterem=new JMenuItem("Clear From My Files  ");
			deleterem.addActionListener(this);
			
			add(open);
			add(watch);
			add(edit);
			add(vimage);
			add(locate);
			add(refdata);
			add(getid);
			add(deleterem);
		}

		@Override
		public void actionPerformed(ActionEvent e){
			String command=e.getActionCommand();
			if(command.equals("Watch Movie")){
				
			}
			if(command.equals("Get Movie ID")){
				
			}
			
		}
	}
	
	MouseAdapter popupMenu=new MouseAdapter(){
		public long clTime=-1L;
		
		@Override
		public void mouseClicked(final MouseEvent e){
			if(e.getButton()==MouseEvent.BUTTON3){
				ContextMenu cm=new ContextMenu();
				cm.show(e.getComponent(),e.getX(),e.getY());
				return;
			}
			if(e.getButton()==MouseEvent.BUTTON1&&e.getClickCount()==2){
				new FurkFileView(ff);
				return;
			}
		}
		@Override
		public void mousePressed(MouseEvent e){
			if(e.getButton()==MouseEvent.BUTTON1)
				clTime=System.currentTimeMillis();
		}
		@Override
		public void mouseReleased(MouseEvent e){
			if(e.getButton()==MouseEvent.BUTTON1)
			if(System.currentTimeMillis()-clTime>1200){
				//Mouse-Hold-For-2-Seconds Action Here
				
			}
		}
		@Override
		public void mouseEntered(MouseEvent arg0){
			setBackground(UtilBox.dimColor(bgc));
			
			field_title.setSelectionColor(getBackground());
			field_title.setBackground(getBackground());
			field_rating.setSelectionColor(getBackground());
			field_rating.setBackground(getBackground());
			checkBox.setBackground(getBackground());
		}
		
		@Override
		public void mouseExited(MouseEvent arg0){
			setBackground(bgc);
			field_title.setSelectionColor(getBackground());
			field_title.setBackground(getBackground());
			field_rating.setSelectionColor(getBackground());
			field_rating.setBackground(getBackground());
			checkBox.setBackground(getBackground());
		}
	};
	/**Menu / MenuFunctions END **/
	
	private FurkFile ff;
	@SuppressWarnings("unused")
	private FurkDownload fd;
	
	@SuppressWarnings("unused")
	private IconPane thisIconPanel;
	private Color bgc;
	
	public Color getBgc(){
		return bgc;
	}

	private static int 
		W,
		H;
    private final int 
    	hGap = 0,
    	vGap = 0;
    private Dimension size;
    
	private JTextArea label_title;
	private JLabel icon;

	private JCheckBox checkBox;
	
	private JTextField 
		field_title,
		field_rating;
	
	public IconPane(APIFile v){
		if(v instanceof FurkFile) this.ff=(FurkFile)v;
		new UtilBox();
		bgc=UtilBox.getRandomColor();
		setBackground(bgc);
		
		initResultMode();//Only Mode, For Now...
		
		thisIconPanel=this;
		setVisible(true);
	}
	
	private void initResultMode(){
		setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		FlowLayout fl=new FlowLayout();
		fl.setAlignment(FlowLayout.LEFT);
		setLayout(fl);
		setBounds(0, 0, 550, 35);
		
		W=550;
		H=35;
		
		addMouseListener(popupMenu);
		
		icon=new JLabel();
		//icon.addMouseListener(popupMenu);
		icon.addMouseListener(new MouseAdapter(){
			@Override
			public void mouseClicked(MouseEvent e){
				if(e.getButton()==MouseEvent.BUTTON1&&e.getClickCount()==2)
					;//DoubleClick On Image Action
			}
		});
		icon.setBounds(6,4, 21, 23);
		icon.setVisible(true);
		add(icon);
		
		field_title = new JTextField(36);
		field_title.setFont(new Font("Dialog", Font.BOLD, 12));
		field_title.setBackground(getBackground());
		field_title.setForeground(Color.BLACK);
		field_title.setSelectedTextColor(Color.BLACK);
		field_title.addMouseListener(popupMenu);
		field_title.setText(ff.getName());
		if(ff.getType()==null) field_title.setText(field_title.getText()+" [Not Ready]");
		else if(ff.getType().equals("video")||ff.getType().equals("audio")) field_title.setText(field_title.getText()+" ["+ff.getType()+"]");
		if(field_title.getText().length()>63){
			String t=field_title.getText().substring(0,62);
			t+="...";
			field_title.setText(t);
		}
		field_title.setBounds(34, 4, 360, 20);
		field_title.setVisible(true);
		field_title.setEditable(false);
		add(field_title);
		
		field_rating = new JTextField(6);
		field_rating.setFont(new Font("Dialog", Font.BOLD, 12));
		field_rating.setBackground(getBackground());
		field_rating.setForeground(Color.BLACK);
		field_rating.addMouseListener(popupMenu);
		field_rating.setText(ff.getSizeString());
		field_rating.setBounds(130, 4, 86, 20);
		field_rating.setVisible(true);
		field_rating.setEditable(false);
		add(field_rating);
		
		checkBox=new JCheckBox();
		checkBox.setSelected(ff.isLinked());
		checkBox.addMouseListener(popupMenu);
		checkBox.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0){
				checkBox.setSelected(ff.isLinked());
			}
		});
		checkBox.setBackground(getBackground());
		add(checkBox);
	}
	
	public void loadFileData(){
		field_title.setText(ff.getName()+" ("+ff.getType()+")");
		field_rating.setText(ff.getSizeString());
		checkBox.setSelected(ff.isLinked());
	}
	
	public Dimension getPreferredScrollableViewportSize(){
        return size;
    }

    public int getScrollableUnitIncrement(
        Rectangle visibleRect,int orientation,int direction){
        return getIncrement(orientation);
    }

    public int getScrollableBlockIncrement(
        Rectangle visibleRect,int orientation,int direction){
        return getIncrement(orientation);
    }

    private int getIncrement(int orientation){
        if (orientation==JScrollBar.HORIZONTAL){
            return W+hGap;
        } else {
            return H+vGap;
        }
    }

    public boolean getScrollableTracksViewportWidth(){
        return false;
    }

    public boolean getScrollableTracksViewportHeight(){
        return false;
    }
	
	@Override
	public int compareTo(IconPane o){
		return o.label_title.getText().compareTo(label_title.getText());
	}
	
	@Override
	public boolean equals(Object o){
		if(o==null) return false;
		if(o instanceof IconPane){
			IconPane i=(IconPane)o;
			if(i.ff.getId()==this.ff.getId()) return true;
		}
		return false;
	}
}
