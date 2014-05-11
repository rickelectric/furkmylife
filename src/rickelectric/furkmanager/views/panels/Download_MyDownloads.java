package rickelectric.furkmanager.views.panels;

import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.FurkDownload;
import rickelectric.furkmanager.network.api.API_Download;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.icons.DownloadIcon;
import rickelectric.furkmanager.views.windows.AppFrameClass;

public class Download_MyDownloads extends JPanel {
	private static final long serialVersionUID = 1L;

	private JPanel resultPanel;
	private JScrollPane resultScroller;

	private enum Mode {
		ACTIVE, FAILED
	}

	public static final Mode ACTIVE = Mode.ACTIVE, FAILED = Mode.FAILED;

	private Mode mode = ACTIVE;

	public Download_MyDownloads(Mode mode) {
		super();
		if (mode == null)
			throw new IllegalArgumentException("Mode Cannot Be Null");
		this.mode = mode;
		setLayout(null);
		setBackground(UtilBox.getRandomColor());
		resultScroller = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		resultScroller.getVerticalScrollBar().setBlockIncrement(200);
		resultScroller.getVerticalScrollBar().setUnitIncrement(50);
		resultScroller.getViewport().setBackground(getBackground());
		resultScroller.setBounds(1, 1, 547, 364);
		add(resultScroller);

		resultPanel = new JPanel();
		resultPanel.setBackground(getBackground());
		resultScroller.setViewportView(resultPanel);

		refreshMyDownloads(true);
	}

	public void refreshMyDownloads(final boolean hardReload) {
		resultPanel.removeAll();
		resultPanel.setLayout(null);

		final JLabel label_loading = new JLabel();
		label_loading.setHorizontalAlignment(SwingConstants.CENTER);
		label_loading.setIcon(new ImageIcon(FurkManager.class
				.getResource("img/ajax-loader.gif")));
		label_loading.setBounds(200, 123, 107, 91);
		resultPanel.add(label_loading);
		repaint();

		Thread tLoad = ThreadPool.getThread(new Runnable() {

			private void populateResultPanel(FurkDownload o) {
				JPanel pane;
				pane = new DownloadIcon(o);

				resultPanel.add(pane);
				resultPanel.add(Box.createVerticalStrut(3));
			}

			public void run() {
				try {
					API_Download.GET_STATUS stat = API_Download.STATUS_ACTIVE;
					if (mode == FAILED)
						stat = API_Download.STATUS_FAILED;
					ArrayList<FurkDownload> fdarray = hardReload ? API_Download
							.getAll(stat) : mode == FAILED ? API_Download
							.getAll(stat) : API_Download.getAllCached();

					resultPanel.removeAll();
					resultPanel.setLayout(new BoxLayout(resultPanel,
							BoxLayout.Y_AXIS));
					resultPanel.setBorder(BorderFactory.createEmptyBorder(3, 3,
							3, 3));
					if (fdarray == null)
						return;

					int numResults = 0;
					for (FurkDownload o : fdarray) {
						populateResultPanel(o);
						numResults++;
					}
					if (numResults <= 4) {
						int delnum = 4 - numResults;
						int structsize = (delnum * 99) - ((delnum - 1) * 5);
						resultPanel.add(Box.createVerticalStrut(structsize));
						resultPanel.setLocation(0, 0);
						resultScroller.repaint();
					}
					repaint();
					((AppFrameClass) getTopLevelAncestor())
							.setStatus("My Active Downloads Loaded: "
									+ numResults + " Active Downloads");
				} catch (Exception e) {
					label_loading.setIcon(new ImageIcon("img/remove.png"));
				}
				((AppFrameClass) getTopLevelAncestor()).addConsole();
				((AppFrameClass) getTopLevelAncestor()).addImgCacheViewer();
			}
		});
		tLoad.start();

	}

}
