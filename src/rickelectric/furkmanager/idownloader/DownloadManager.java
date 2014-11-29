package rickelectric.furkmanager.idownloader;

import java.awt.BorderLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URL;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.windows.AppFrameClass;

public class DownloadManager extends AppFrameClass implements Observer {
	private static final long serialVersionUID = 1L;

	private static DownloadManager thisObj;
	private JTextField addTextField = new JTextField(30);
	private static DownloadTable tableModel = null;

	public static void persist() {
		if (tableModel != null)
			tableModel.persist();
	}

	private JTable table;
	private static JButton pauseButton = new JButton("Pause");
	private static JButton resumeButton = new JButton("Resume");
	private static JButton cancelButton, clearButton;
	private static JLabel saveFileLabel = new JLabel();

	private static FileDownload selectedDownload;
	private boolean clearing;

	public DownloadManager() {
		if(tableModel!=null) return;
		
		tableModel = new DownloadTable();
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		setTitle("Download Manager");
		setSize(640, 529);

		JPanel addPanel = new JPanel(new BorderLayout());

		JPanel targetPanel = new JPanel(new BorderLayout());
		targetPanel.setVisible(false);
		targetPanel.add(addTextField, BorderLayout.WEST);

		JButton addButton = new JButton("Add Download");
		addButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionAdd();
			}
		});
		targetPanel.add(addButton, BorderLayout.EAST);

		JPanel destinationPanel = new JPanel(new BorderLayout());
		destinationPanel.setVisible(false);

		JButton saveFileButton = new JButton("Download To");
		saveFileButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionSaveTo(false);
			}
		});
		destinationPanel.add(saveFileLabel, BorderLayout.CENTER);
		destinationPanel.add(saveFileButton, BorderLayout.EAST);

		addPanel.add(destinationPanel, BorderLayout.NORTH);

		JLabel lblFolder = new JLabel("Folder: ");
		destinationPanel.add(lblFolder, BorderLayout.WEST);
		addPanel.add(targetPanel, BorderLayout.SOUTH);

		// Set up Downloads table.
		table = new JTable(tableModel);
		TableColumn column = null;
		for (int i = 0; i < 4; i++) {
			column = table.getColumnModel().getColumn(i);
			if (i == 0) {
				column.setPreferredWidth(160);// URL Column
			} else if (i == 1) {
				column.setPreferredWidth(30);
			} else {
				column.setPreferredWidth(60);
			}
		}
		table.getSelectionModel().addListSelectionListener(
				new ListSelectionListener() {
					@Override
					public void valueChanged(ListSelectionEvent e) {
						tableSelectionChanged();
					}
				});
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		setDropTarget(new DropTarget() {
			private static final long serialVersionUID = 1L;

			@Override
			public synchronized void drop(DropTargetDropEvent evt) {
				try {
					evt.acceptDrop(DnDConstants.ACTION_COPY);
					String droppedLink = (evt.getTransferable()
							.getTransferData(DataFlavor.stringFlavor))
							.toString();
					addDownload(null, droppedLink);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		});

		JProgressCell renderer = new JProgressCell(0, 100);
		renderer.setStringPainted(true);
		table.setDefaultRenderer(JProgressBar.class, renderer);
		table.setRowHeight((int) renderer.getPreferredSize().getHeight());

		table.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e){
				if (e.getClickCount() == 1) {
					int r = table.rowAtPoint(e.getPoint());
					if (r >= 0 && r < table.getRowCount()) {
						table.setRowSelectionInterval(r, r);
					} else {
						table.clearSelection();
					}
					int rowindex = table.getSelectedRow();
					if (rowindex < 0)
						return;
					if (e.getButton()==MouseEvent.BUTTON3&&e.getClickCount()==1) {
						FileDownload d=tableModel.getDownload(r);
						JPopupMenu popup=d.popup();
						popup.show(e.getComponent(), e.getX(), e.getY());
					}
				}
			}
		});

		JPanel downloadsPanel = new JPanel();
		downloadsPanel.setBorder(BorderFactory.createTitledBorder("Downloads"));
		downloadsPanel.setLayout(new BorderLayout());
		downloadsPanel.add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel buttonsPanel = new JPanel();
		pauseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionPause();
			}
		});
		pauseButton.setEnabled(false);
		buttonsPanel.add(pauseButton);

		resumeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionResume();
			}
		});
		resumeButton.setEnabled(false);
		buttonsPanel.add(resumeButton);

		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionCancel();
			}
		});
		cancelButton.setEnabled(false);
		buttonsPanel.add(cancelButton);

		clearButton = new JButton("Clear");
		clearButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				actionClear();
			}
		});
		clearButton.setEnabled(false);
		buttonsPanel.add(clearButton);

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(addPanel, BorderLayout.NORTH);
		getContentPane().add(downloadsPanel, BorderLayout.CENTER);
		getContentPane().add(buttonsPanel, BorderLayout.SOUTH);

		thisObj = this;
	}

	public static void addDownload(String name, String url) {
		String dest = actionSaveTo(true);
		if (dest == null || dest.equals(""))
			return;
		thisObj.actionAdd(name, url, dest);
	}

	public static boolean addDownload(String name, String url, String folder,
			boolean autostart) {
		FileDownload d = thisObj.actionAdd(name, url, folder);
		if (d == null)
			return false;
		if (autostart)
			d.resume();
		if (!thisObj.isVisible())
			thisObj.setVisible(true);
		return true;
	}

	public static String actionSaveTo(boolean ext) {
		String sm=SettingsManager.getInstance().getDownloadFolder();
		if(!SettingsManager.getInstance().askFolderOnDownload()) return sm;
		JFileChooser jfchooser = new JFileChooser(sm);
		jfchooser.setApproveButtonText("OK");
		jfchooser.setDialogTitle("Select Folder...");
		jfchooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int result = jfchooser.showOpenDialog(thisObj);
		if (result != JFileChooser.APPROVE_OPTION)
			return null;

		File chosenFolder = jfchooser.getSelectedFile();
		if (!ext)
			saveFileLabel.setText(chosenFolder.getPath());
		return chosenFolder.getPath();
	}

	private void actionAdd() {
		String url = addTextField.getText();
		String dest = saveFileLabel.getText();
		actionAdd(null, url, dest);
		addTextField.setText("");
	}

	private FileDownload actionAdd(String name, String url, String dest) {
		URL verifiedUrl = verifyUrl(url);
		if (verifiedUrl != null) {
			FileDownload d = new FileDownload(name, verifiedUrl, dest);
			
			tableModel.addDownload(d);

			return d;
		} else {
			JOptionPane.showMessageDialog(this, "Invalid Download URL",
					"Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
	}
/*
	protected boolean isHttpsURL(String url) {
		if (url.toLowerCase().startsWith("https://"))
			return true;
		else if (url.toLowerCase().startsWith("http://"))
			return false;
		throw new IllegalArgumentException("Invalid URL Type");
	}
*/
	private URL verifyUrl(String url) {
		if (!url.toLowerCase().startsWith("http://")
				&& !url.toLowerCase().startsWith("https://")){
			String proto=url.split("/")[0];
			if(proto.length()>5){
				url="http://"+url;
			}
			else return null;
		}
		URL verifiedUrl = null;

		try {
			verifiedUrl = new URL(url);
		} catch (Exception e) {
			return null;
		}

		// if (verifiedUrl.getFile().length() < 2) return null;

		return verifiedUrl;

	}

	private void tableSelectionChanged() {
		if (selectedDownload != null)
			selectedDownload.deleteObserver(DownloadManager.this);

		if (!clearing && table.getSelectedRow() > -1) {
			selectedDownload = tableModel.getDownload(table.getSelectedRow());
			selectedDownload.addObserver(DownloadManager.this);
			updateButtons();
		}
	}

	private void actionPause() {
		selectedDownload.pause();
		updateButtons();
	}

	private void actionResume() {
		selectedDownload.resume();
		updateButtons();
	}

	private void actionCancel() {
		selectedDownload.cancel();
		updateButtons();
	}

	private void actionClear() {
		clearing = true;
		tableModel.clearDownload(table.getSelectedRow());
		clearing = false;
		selectedDownload = null;
		updateButtons();
	}
	
	public static void actionClear(FileDownload d){
		tableModel.clearDownload(d);
		selectedDownload=null;
		updateButtons();
	}

	private static void updateButtons() {
		if (selectedDownload != null) {
			int status = selectedDownload.getStatus();
			switch (status) {
			case FileDownload.DOWNLOADING:
				pauseButton.setEnabled(true);
				resumeButton.setEnabled(false);
				cancelButton.setEnabled(true);
				clearButton.setEnabled(false);
				break;
			case FileDownload.PAUSED:
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(true);
				resumeButton.setText("Resume");
				cancelButton.setEnabled(true);
				clearButton.setEnabled(false);
				break;
			case FileDownload.ERROR:
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(true);
				cancelButton.setEnabled(false);
				clearButton.setEnabled(true);
				break;
			case FileDownload.IDLE:
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(true);
				resumeButton.setText("Start");
				cancelButton.setEnabled(true);
				clearButton.setEnabled(false);
				break;
			default: // COMPLETE or CANCELLED
				pauseButton.setEnabled(false);
				resumeButton.setEnabled(false);
				cancelButton.setEnabled(false);
				clearButton.setEnabled(true);
			}
		} else {
			pauseButton.setEnabled(false);
			resumeButton.setEnabled(false);
			cancelButton.setEnabled(false);
			clearButton.setEnabled(false);
		}
	}

	@Override
	public void update(Observable o, Object arg) {
		// Update buttons if the selected download has changed.
		if (selectedDownload != null && selectedDownload.equals(o))
			updateButtons();
	}

	// Run the Download Manager.
	public static void main(String[] args) {
		SettingsManager.getInstance();
		DownloadManager manager = new DownloadManager();
		manager.addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				tableModel.persist();
				((JFrame)e.getSource()).dispose();
				System.exit(0);
			}
		});
		manager.setVisible(true);
	}

}
