package rickelectric.furkmanager.views.windows;

import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.network.API;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.panels.ScreenshotViewPanel;
import rickelectric.furkmanager.views.panels.TFileTreePanel;

public class FurkFileView extends AppFrameClass {

	private static final long serialVersionUID = 1L;
	
	private static ArrayList<FurkFileView> windowlist=null;

	private JPanel contentPane;
	private JTextField input_id;
	private JTextField input_name;
	private JTextField input_size;
	private JTextField input_type;
	private JTextField input_hash;
	private JPanel ss_panel;
	
	private JTextField input_url;
	private JButton btn_viewfurk;
	private JButton btn_close;
	private JButton btn_linkfile;

	private JButton btn_changeview;

	private TFileTreePanel tfile_scroll;

	private FurkFile ff;

	public FurkFileView(final FurkFile ff){
		this.ff = ff;
		if(windowlist==null) windowlist=new ArrayList<FurkFileView>();
		int i=windowlist.indexOf(this);
		if(i>=0){
			windowlist.get(i).setVisible(true);
			return;
		}
		
		setResizable(false);
		setTitle(ff.getName());
		setBounds(100, 100, 450, 541);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		super.setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel label_id = new JLabel("File ID:");
		label_id.setBounds(10, 10, 84, 17);
		contentPane.add(label_id);

		input_id = new JTextField();
		input_id.setBackground(Color.WHITE);
		input_id.setEditable(false);
		input_id.setBounds(92, 8, 200, 20);
		input_id.setText(ff.getID());
		if (ff.getID() == null)
			input_id.setEnabled(false);
		contentPane.add(input_id);
		input_id.setColumns(10);

		JLabel label_name = new JLabel("Filename:");
		label_name.setBounds(10, 38, 64, 16);
		contentPane.add(label_name);

		input_name = new JTextField();
		input_name.setBackground(Color.WHITE);
		input_name.setEditable(false);
		input_name.setBounds(92, 36, 340, 20);
		input_name.setText(ff.getName());
		contentPane.add(input_name);
		input_name.setColumns(10);

		JLabel label_size = new JLabel("File Size: ");
		label_size.setBounds(10, 77, 64, 16);
		contentPane.add(label_size);

		input_size = new JTextField();
		input_size.setBackground(Color.WHITE);
		input_size.setEditable(false);
		input_size.setBounds(92, 75, 114, 20);
		input_size.setText(ff.getSizeString());
		contentPane.add(input_size);
		input_size.setColumns(10);

		JLabel label_type = new JLabel("Type:");
		label_type.setBounds(251, 77, 49, 16);
		contentPane.add(label_type);

		input_type = new JTextField();
		input_type.setBackground(Color.WHITE);
		input_type.setEditable(false);
		input_type.setBounds(318, 75, 114, 20);
		input_type.setText(ff.getType());
		if (ff.getType() == null)
			input_type.setEnabled(false);
		contentPane.add(input_type);
		input_type.setColumns(10);

		JLabel label_hash = new JLabel("Info Hash: ");
		label_hash.setBounds(10, 109, 64, 16);
		contentPane.add(label_hash);

		input_hash = new JTextField();
		input_hash.setBackground(Color.WHITE);
		input_hash.setEditable(false);
		input_hash.setBounds(92, 107, 340, 20);
		input_hash.setText(ff.getInfoHash());
		contentPane.add(input_hash);
		input_hash.setColumns(10);

		btn_linkfile = new JButton("Add To My Files");
		btn_linkfile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cmd = e.getActionCommand();
				if (cmd.equals("Add To My Files")) {
					String id = ff.getID();
					if (!API.File.link(new String[] { id })) {
						JOptionPane.showMessageDialog(null, "File Not Added");
						return;
					} else {
						JOptionPane.showMessageDialog(null, "File Added");
						ff.setLinked(true);
						btn_linkfile.setText("Remove From My Files");
					}
				}
				if (cmd.equals("Remove From My Files")) {
					String id = ff.getID();
					if (!API.File.unlink(new String[] { id })) {
						JOptionPane.showMessageDialog(null, "File Not Removed");
						return;
					} else {
						JOptionPane.showMessageDialog(null,
								"File Removed Successfully");
						btn_linkfile.setText("Add To My Files");
					}
				}
				if (cmd.equals("Add To My Downloads")) {
					String hash = ff.getInfoHash();
					if (!API.Download.addHash(hash)) {
						JOptionPane.showMessageDialog(null,
								"Download Not Added");
						return;
					} else {
						JOptionPane.showMessageDialog(null, "Download Added");
						btn_linkfile.setVisible(false);
					}
				}
			}
		});
		if (ff.getUrlDl() == null || ff.getUrlDl().equals("")){
			if(ff.isLinked()) btn_linkfile.setText("Re-Add To My Downloads");
			else btn_linkfile.setText("Add To My Downloads");
		}
		else{
			if(ff.isLinked()) btn_linkfile.setText("Remove From My Files");
			else btn_linkfile.setText("Add To My Files");
		}

		btn_linkfile.setBounds(10, 459, 162, 20);
		contentPane.add(btn_linkfile);

		setVisible(true);
		
		boolean showScreenshots=(ff.getType() != null && ff.getType().equals("video")
				&& ff.getThumbs() != null);
		if (showScreenshots) {
			ss_panel=new ScreenshotViewPanel(ff);
			ss_panel.setBounds(10, 191, 414, 157);
			contentPane.add(ss_panel);
		}
		

		JLabel label_url = new JLabel("URL:");
		label_url.setBounds(10, 137, 64, 16);
		contentPane.add(label_url);

		input_url = new JTextField();
		input_url.setBackground(Color.WHITE);
		input_url.setEditable(false);
		input_url.setBounds(92, 135, 340, 20);
		input_url.setText(ff.getUrlDl());
		contentPane.add(input_url);
		input_url.setColumns(10);

		JButton btn_copyLink = new JButton("Copy URL");
		btn_copyLink.setBounds(334, 159, 98, 20);
		btn_copyLink.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				UtilBox.sendToClipboard(ff.getUrlDl());
			}
		});
		contentPane.add(btn_copyLink);

		btn_viewfurk = new JButton("View on Furk");
		btn_viewfurk.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String url = ff.getUrlPage();
				UtilBox.openUrl("https://www.furk.net"+url);
			}
		});
		btn_viewfurk.setBounds(10, 427, 124, 20);
		if (ff.getUrlPage() != null && ff.getUrlPage().length() != 0)
			contentPane.add(btn_viewfurk);

		btn_close = new JButton("Close");
		btn_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		btn_close.setBounds(308, 459, 124, 20);
		contentPane.add(btn_close);
		
		btn_changeview = new JButton("Show File Tree");
		btn_changeview.setHorizontalAlignment(SwingConstants.LEFT);
		btn_changeview.setIcon(new ImageIcon(FurkManager.class.getResource("img/arrow_double_right.png")));
		final JPanel tmp=new JPanel();
		tmp.setLayout(new GridBagLayout());
		tmp.add(new JLabel(new ImageIcon(FurkManager.class.getResource("img/ajax-loader.gif"))));
		tmp.setBounds(10, 192, 418, 201);
		tmp.setBorder(new LineBorder(Color.BLACK));
		if(!showScreenshots) contentPane.add(tmp);
		else tmp.setVisible(false);
		
		btn_changeview.addMouseListener(new MouseAdapter(){
			public void mouseClicked(MouseEvent e){
				if(tfile_scroll==null){
					if(tmp.isVisible()){
						tmp.setVisible(false);
						if(ss_panel!=null) ss_panel.setVisible(true);
					}
					else{
						tmp.setVisible(true);
						if(ss_panel!=null) ss_panel.setVisible(false);
					}
				}
				else{
					if(tfile_scroll.isVisible()){
						tfile_scroll.setVisible(false);
						if(ss_panel!=null) ss_panel.setVisible(true);
					}
					else{
						tfile_scroll.setVisible(true);
						if(ss_panel!=null) ss_panel.setVisible(false);
					}
				}
				if(btn_changeview.getText().equals("Show File Tree")){
					btn_changeview.setIcon(new ImageIcon(FurkManager.class.getResource("img/arrow_double_left.png")));
					btn_changeview.setText("Back To Screenshots");
				}
				else{
					btn_changeview.setIcon(new ImageIcon(FurkManager.class.getResource("img/arrow_double_right.png")));
					btn_changeview.setText("Show File Tree");
				}
				
			}
		});
		btn_changeview.setBounds(189, 427, 243, 20);
		if(showScreenshots) 
			contentPane.add(btn_changeview);
		new Thread(new Runnable(){
			public void run(){
				tfile_scroll = new TFileTreePanel(ff);
				tfile_scroll.setBounds(10, 192, 418, 201);
				tfile_scroll.setVisible(false);
				contentPane.add(tfile_scroll);
				if(tmp.isVisible()){
					tmp.setVisible(false);
					tfile_scroll.setVisible(true);
				}
			}
		}).start();
		
		repaint();
		addConsole();
		addImgCacheViewer();
		
		windowlist.add(this);
	}
	
	@Override
	public boolean equals(Object o){
		if(o instanceof FurkFileView)
			return ff.getID().equals(((FurkFileView)o).ff.getID());
		return false;
	}
	
	@Override
	public void dispose(){
		super.dispose();
		if(windowlist==null) return;
		windowlist.remove(this);
	}

	public static void disposeAll(){
		if(windowlist==null) return;
		while(windowlist.size()>0)
			windowlist.get(0).dispose();
	}
}
