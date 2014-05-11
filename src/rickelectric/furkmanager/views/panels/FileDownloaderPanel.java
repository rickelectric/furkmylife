package rickelectric.furkmanager.views.panels;

import javax.swing.JPanel;
import javax.swing.JTable;

import rickelectric.furkmanager.idownloader.DownloadTable;
import rickelectric.furkmanager.utils.UtilBox;

public class FileDownloaderPanel extends JPanel{
	private static final long serialVersionUID = 1L;
	private JTable table_downloads;
	private DownloadTable tmodel;

	public FileDownloaderPanel() {
		setLayout(null);
		setBackground(UtilBox.getRandomColor());
		
		tmodel=new DownloadTable();
		
		table_downloads = new JTable();
		table_downloads.setBounds(10, 11, 554, 314);
		table_downloads.setModel(tmodel);
		add(table_downloads);
		
	}
}