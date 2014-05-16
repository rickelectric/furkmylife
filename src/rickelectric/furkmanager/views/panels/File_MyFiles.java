package rickelectric.furkmanager.views.panels;

import java.awt.Dimension;
import java.awt.FlowLayout;
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
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.icons.FileIcon;
import rickelectric.furkmanager.views.windows.AppFrameClass;

public class File_MyFiles extends JPanel implements Runnable{
	private static final long serialVersionUID = 1L;

	private enum Mode {
		MYFILES, RECYCLER
	}

	public static final Mode MYFILES = Mode.MYFILES, RECYCLER = Mode.RECYCLER;

	private JScrollPane resultScroller;
	private JPanel resultPanel;
	private boolean hardReload;

	private Mode mode = MYFILES;

	private JLabel label_loading;

	private boolean loading;

	private int numResults=0;

	public File_MyFiles(Mode mode) {
		super();
		if (mode == null) {
			return;
		}
		this.mode = mode;
		this.hardReload=false;
		this.loading=false;
		
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

		refreshMyFiles(true);
	}

	public void refreshMyFiles(final boolean hardReload){
		if(loading) return;
		resultPanel.removeAll();
		resultPanel.setLayout(null);
		
		this.hardReload=hardReload;
		
		label_loading = new JLabel();
		label_loading.setHorizontalAlignment(SwingConstants.CENTER);
		label_loading.setIcon(new ImageIcon(FurkManager.class
				.getResource("img/ajax-loader.gif")));
		label_loading.setBounds(200, 123, 107, 91);
		resultPanel.add(label_loading);

		repaint();

		new Thread(this).start();

	}
	
	private void populateFileManager(FurkFile[] o) {
		JPanel pane;
		pane = new JPanel();
		pane.setLayout(new FlowLayout(FlowLayout.LEFT, 17, 3));
		pane.setPreferredSize(new Dimension(510, 142));
		int i = 0;
		while (i < 3 && i < o.length && o[i] != null) {
			FileIcon ico=new FileIcon(o[i]);
			ico.num(numResults+i+1);
			pane.add(ico);
			i++;
		}
		pane.setBackground(resultPanel.getBackground());
		resultPanel.add(pane);
		resultPanel.add(Box.createVerticalStrut(3));
	}

	public void run() {
		if(loading==true) return;
		loading=true;
		try {
			int position = resultScroller.getVerticalScrollBar()
					.getValue();
			if (hardReload)
				position = resultScroller.getHorizontalScrollBar()
						.getMinimum();

			ArrayList<FurkFile> ffarray = hardReload ? (mode == MYFILES ? API_File
					.getAllFinished() : API_File.getAllDeleted())
					: (mode == RECYCLER ? API_File.getAllDeleted()
							: API_File.getAllCached());

			resultPanel.removeAll();
			resultPanel.setLayout(new BoxLayout(resultPanel,
					BoxLayout.Y_AXIS));
			resultPanel.setBorder(BorderFactory.createEmptyBorder(3, 3,
					3, 3));
			if (ffarray == null)
				return;

			numResults = 0;
			FurkFile[] oArr = new FurkFile[3];
			int i = 0;
			for (FurkFile o : ffarray) {
				if (i < 3) {
					oArr[i] = o;
					i++;
				} else {
					populateFileManager(oArr);
					oArr = new FurkFile[3];
					i = 0;
					numResults += 3;
				}
			}
			if (i <= 3 && i != 0) {
				populateFileManager(oArr);
				numResults += i;
			}

			resultScroller.getVerticalScrollBar().setValue(position);
			try {
				getTopLevelAncestor().repaint();
				((AppFrameClass) getTopLevelAncestor())
						.setStatus("Loaded: "
								+ numResults
								+ " "
								+ (mode == MYFILES ? "Finished"
										: "Deleted") + " Files");
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
			label_loading.setIcon(new ImageIcon(FurkManager.class
					.getResource("img/remove.png")));
		}
		try {
			((AppFrameClass) getTopLevelAncestor()).addConsole();
			((AppFrameClass) getTopLevelAncestor()).addImgCacheViewer();
		} catch (Exception e) {
		}
		loading=false;
	}
}
