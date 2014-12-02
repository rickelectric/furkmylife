package rickelectric.furkmanager.views.windows;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.SpinnerNumberModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import rickelectric.furkmanager.models.ImgRequest;
import rickelectric.furkmanager.network.RequestCache;
import rickelectric.furkmanager.network.StreamDownloader;
import rickelectric.furkmanager.utils.SettingsManager;

public class ImgCacheViewer extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private static JTable table_imgs;

	private JScrollPane scrollPane;
	
	private static MouseListener tableM = new MouseAdapter() {
		@Override
		public void mouseReleased(MouseEvent e) {
			int r = table_imgs.rowAtPoint(e.getPoint());
			if (r >= 0 && r < table_imgs.getRowCount()) {
				table_imgs.setRowSelectionInterval(r, r);
			} else {
				table_imgs.clearSelection();
			}

			int rowindex = table_imgs.getSelectedRow();
			if (rowindex < 0)
				return;
			String url=table_imgs.getModel().getValueAt(rowindex,0).toString();
			ImgRequest ir=RequestCache.ImageR.get(url);
			if(ir==null) return;
			if (e.isPopupTrigger() && e.getComponent() instanceof JTable) {
				popup(ir).show(e.getComponent(), e.getX(), e.getY());
			}
		}
	};
	private JButton button_clear;
	private static JSpinner spinner_capacity;
	private JButton button_SaveNewCapacity;

	public ImgCacheViewer() {
		setTitle("Image Cache");
		setBounds(0, 0, 584, 500);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Dimension size = getSize();
				scrollPane.setSize(size.width - 36, size.height - 120);
				super.componentResized(e);
			}
		});

		table_imgs = new JTable();

		scrollPane = new JScrollPane(table_imgs);
		scrollPane.setBounds(10, 10, 548, 381);
		contentPane.add(scrollPane);

		scrollPane.setViewportView(table_imgs);
		
		button_clear = new JButton("Clear Cache");
		button_clear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				RequestCache.ImageR.flush();
				modelTable();
			}
		});
		button_clear.setBounds(10, 402, 110, 20);
		contentPane.add(button_clear);
		
		JLabel lblCapacity = new JLabel("Capacity: ");
		lblCapacity.setBounds(207, 402, 55, 20);
		contentPane.add(lblCapacity);
		
		spinner_capacity = new JSpinner();
		spinner_capacity.setModel(new SpinnerNumberModel(50, 5, 200, 1));
		spinner_capacity.setBounds(280, 402, 61, 20);
		contentPane.add(spinner_capacity);
		
		button_SaveNewCapacity = new JButton("Save New Capacity");
		button_SaveNewCapacity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0){
				SettingsManager.getInstance().numCachedImages((Integer)spinner_capacity.getValue());
				SettingsManager.save();
			}
		});
		button_SaveNewCapacity.setBounds(353, 402, 142, 20);
		contentPane.add(button_SaveNewCapacity);
		modelTable();
	}

	public static void modelTable() {
		if (table_imgs == null)
			return;
		table_imgs.setModel(getImgCacheModel());
		spinner_capacity.setValue(SettingsManager.getInstance().numCachedImages());
		TableColumnModel l = table_imgs.getColumnModel();
		table_imgs.addMouseListener(tableM);
		l.getColumn(0).setPreferredWidth(table_imgs.getWidth() - 55);
		l.getColumn(1).setWidth(53);

	}

	private static class ContextMenu extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;
		private JMenuItem view, update, delete;
		
		private ImgRequest im;

		public ContextMenu(ImgRequest im) {
			this.im=im;
			
			view = new JMenuItem("View");
			view.addActionListener(this);

			update = new JMenuItem("Re-Download");
			update.addActionListener(this);

			delete = new JMenuItem("Delete From Cache");
			delete.addActionListener(this);

			add(view);
			add(update);
			add(delete);

		}
		
		Thread action;

		@Override
		public void actionPerformed(final ActionEvent e) {
			if(action==null)
			action=new Thread(new Runnable(){
				@Override
				public void run(){
					try{
						Object src = e.getSource();
						
						if (src.equals(view)) {
							// View
							BufferedImage img=im.getImage();
							JOptionPane.showMessageDialog(null, new ImageIcon(img), "Image", JOptionPane.PLAIN_MESSAGE);
						}
						if (src.equals(update)) {
							// Re-Download
							BufferedImage img=StreamDownloader.getInstance().getImageStream(im.getUrl(),4);
							if(img!=null){
								RequestCache.ImageR.add(im.getUrl(), img);
							}
						}
						if (src.equals(delete)) {
							// Delete From Cache
							RequestCache.ImageR.remove(im);
						}
					}catch(Exception e){}
				}
			});
			if(action.isAlive()) return;
			action.start();
		}

	}

	private static JPopupMenu popup(ImgRequest im) {
		return new ContextMenu(im);
	}

	public static DefaultTableModel getImgCacheModel() {
		Vector<String> columnNames = new Vector<String>();
		columnNames.add("URL");
		columnNames.add("Time Downloaded");

		// Retrieve Table Data
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		Iterator<ImgRequest> im = RequestCache.ImageR.iterate();
		if (im != null)
			while (im.hasNext()) {
				Vector<Object> vector = new Vector<Object>();
				ImgRequest curr = im.next();
				vector.add(curr.getUrl());
				vector.add(curr.getTime());
				data.add(vector);
			}
		return new DefaultTableModel(data, columnNames);
	}
}
