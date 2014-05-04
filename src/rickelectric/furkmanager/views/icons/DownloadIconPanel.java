package rickelectric.furkmanager.views.icons;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.FurkDownload;
import rickelectric.furkmanager.network.API;
import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.panels.Main_DownloadView;

public class DownloadIconPanel extends JPanel implements
		Comparable<DownloadIconPanel> {

	private static final long serialVersionUID = 1L;

	private Timer refTimer;
	private TimerTask runTask;

	private JPanel thisPanel = null;
	private Color bgc;

	public Color getBgc() {
		return bgc;
	}

	private JLabel input_name, input_status;

	private JLabel label_name;
	private JLabel label_progress;
	private JLabel label_status;
	private JLabel lblSize;
	private JLabel label_size;

	private JProgressBar progress_have;

	private FurkDownload cDownload;
	private static boolean action = false;

	protected void finalize() {
		if (refTimer != null) {
			refTimer.cancel();
			refTimer=null;
		}
		if (runTask != null) {
			runTask.cancel();
			runTask=null;
		}
		thisPanel=null;
		System.gc();
	}
	
	private void parentRefresh(){
		Container parent = thisPanel;
		try {
			do {
				parent = parent.getParent();
			} while (!(parent instanceof Main_DownloadView));
			((Main_DownloadView) parent)
					.refreshMyDownloads(false);
		} catch (Exception e) {
			System.err
					.println("Could Not Find Proper Parent");
			e.printStackTrace();
		}
		this.finalize();
	}

	public class ContextMenu extends JPopupMenu implements ActionListener {
		private static final long serialVersionUID = 1L;

		private JMenuItem view, retry, delete;

		public ContextMenu() {
			view = new JMenuItem("View Details");
			view.addActionListener(this);
			add(view);
			
			retry=new JMenuItem("Retry Download");
			retry.addActionListener(this);
			add(retry);

			delete = new JMenuItem("Remove Download");
			delete.addActionListener(this);
			add(delete);

		}

		public void actionPerformed(final ActionEvent e) {
			if (action)
				return;
			Thread t = new Thread(new Runnable() {
				public void run() {
					try {
						action = true;
						Object src = e.getSource();
						boolean vis = progress_have.isVisible();
						progress_have.setIndeterminate(true);
						progress_have.setVisible(true);
						progress_have.setString("Please Wait...");
						thisPanel.repaint();
						if (src.equals(view)) {
							// View Details
						}
						if(src.equals(retry)){
							API.Download.addHash(cDownload.getInfoHash());
						}
						if (src.equals(delete)) {
							// Remove Download
							API.Download.unlink(new String[] { cDownload
									.getId() });
							parentRefresh();
						}
						// progress_have.setIndeterminate(det);
						progress_have.setVisible(vis);
						refreshData();
						thisPanel.repaint();
					} catch (Exception e) {
						e.printStackTrace();
					}
					action = false;
				}
			});
			t.start();
		}
	}

	public DownloadIconPanel(final FurkDownload o) {
		this.cDownload = o;
		this.thisPanel = this;

		if (cDownload.iconColor != null)
			bgc = cDownload.iconColor;
		else {
			bgc = UtilBox.getRandomColor();
			cDownload.iconColor = bgc;
		}
		setBackground(bgc);

		UtilBox.addMouseListenerToAll(this, new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getButton() == MouseEvent.BUTTON3)
					new ContextMenu().show(e.getComponent(), e.getX(), e.getY());
			}
		});

		setPreferredSize(new Dimension(510, 99));
		setBorder(new BevelBorder(BevelBorder.RAISED, null, null, null, null));
		setLayout(null);

		input_name = new JLabel();
		input_name.setFont(new Font("Dialog", Font.BOLD, 13));

		input_name
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		input_name.setBounds(63, 12, 435, 20);
		add(input_name);

		input_status = new JLabel();
		input_status.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null,
				null));

		input_status.setBounds(63, 67, 435, 20);
		add(input_status);

		progress_have = new JProgressBar();
		progress_have.setBounds(266, 40, 232, 20);
		progress_have.setOpaque(false);
		progress_have.setBackground(Color.LIGHT_GRAY);
		progress_have.setForeground(new Color(0, 0, 255));
		progress_have.setStringPainted(true);
		progress_have.setMaximum(1000);
		progress_have.setVisible(false);
		add(progress_have);

		if (!cDownload.getDlStatus().equals("failed")) {
			label_progress = new JLabel("% Done: ");
			label_progress.setBounds(206, 42, 53, 16);
			add(label_progress);
			
			progress_have.setVisible(true);
		}

		label_name = new JLabel("Name:");
		label_name.setBounds(12, 14, 38, 16);
		add(label_name);

		label_status = new JLabel("Status: ");
		label_status.setBounds(12, 69, 49, 16);
		add(label_status);

		lblSize = new JLabel("Size: ");
		lblSize.setBounds(12, 42, 49, 16);
		add(lblSize);

		label_size = new JLabel();
		label_size.setFont(new Font("Dialog", Font.BOLD, 13));
		label_size
				.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		label_size.setBounds(63, 40, 131, 20);
		add(label_size);

		refreshData();

		if (cDownload.getDlStatus().equals("active")) {
			refreshTimerExec();
		}

	}

	public void refreshTimerExec() {
		refTimer = new Timer();
		runTask = new TimerTask() {
			public void run() {
				if (!thisPanel.isVisible()) {
					thisPanel.setVisible(false);
					this.cancel();
					return;
				}
				try {
					FurkDownload did = API.Download.get(cDownload.getId());
					if (did != null) {
						if (cDownload.getDlStatus().equals("active")) {
							if (did.getDlStatus().equals("finished")
									|| (cDownload.getActiveStatus().equals(
											"leeching") && did
											.getActiveStatus()
											.equals("seeding"))){
								FurkManager.trayAlert(
										FurkManager.TRAY_INFO,
										"Download Complete",
										"Finished Downloading '"
												+ did.getName() + "'", null);
								new Thread(new Runnable(){
									public void run(){
										boolean override=APIBridge.overrideCache();
										APIBridge.overrideCache(true);
										try{API.File.getAllFinished();}
										catch(Exception e){}
										APIBridge.overrideCache(override);
									}
								}).start();
								this.cancel();
								parentRefresh();
							} else if (did.getDlStatus().equals("failed")) {
								FurkManager.trayAlert(FurkManager.TRAY_ERROR,
										"Download Failed", "Download of '"
												+ did.getName() + "' Failed: ("
												+ did.getFailReason() + ")",
										null);
								this.cancel();
								parentRefresh();
							}
							cDownload = did;
						}
					}
				} catch (Exception ex) {
					if (ex.getMessage().equals("No Downloads Found")) {
						FurkManager.trayAlert(FurkManager.TRAY_INFO,
								"Download Complete", "Finished Downloading '"
										+ cDownload.getName() + "'",
								new Runnable() {
									public void run() {
										API.File.info(cDownload.getInfoHash());
									}
								});
						APIBridge.fileGet(APIBridge.GET_ALL, null, null, null,
								false);
						this.cancel();
					} else if (ex.getMessage().equals("Connection Error")) {
						FurkManager.trayAlert(FurkManager.TRAY_ERROR, "Error",
								"Connection Error", null);
					}
				}
				refreshData();
				if (!cDownload.getDlStatus().equals("active")) {
					this.cancel();
					progress_have.setVisible(false);
					parentRefresh();
				}
			}
		};
		refTimer.scheduleAtFixedRate(runTask, 10000, 30000);
	}

	public void refreshData() {
		input_name.setText(cDownload.getName());
		String status = cDownload.getActiveStatus() + " - Seeds: "
				+ cDownload.getSeeders() + "/Peers: " + cDownload.getPeers()
				+ " | Updated: " + cDownload.getMtime();
		if (cDownload.getDlStatus().equals("failed"))
			status = "Failed: " + cDownload.getFailReason();
		input_status.setText(status);
		label_size.setText(cDownload.getSizeString());

		if (!cDownload.getDlStatus().equals("failed")) {
			float have = Float.parseFloat(cDownload.getHave());
			progress_have
					.setString(cDownload.getActiveStatus()
							+ ((cDownload.getActiveStatus().equals("adding") || cDownload
									.getActiveStatus().equals("starting")) ? ""
									: " : " + have + "%"));
			have = have * 10;
			progress_have.setValue(Math.round(have));
			if (cDownload.getActiveStatus().equals("leeching"))
				progress_have.setIndeterminate(false);
			else progress_have.setIndeterminate(true);
			
		}

	}

	@Override
	public int compareTo(DownloadIconPanel o) {
		return o.input_name.getText().compareTo(this.input_name.getText());
	}
}
