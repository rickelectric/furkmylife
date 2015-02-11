package rickelectric.furkmanager.views.panels;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;

import rickelectric.IndexPair;
import rickelectric.UtilBox;
import rickelectric.furkmanager.models.FurkFile;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.views.icons.FileIcon;
import rickelectric.furkmanager.views.windows.AppFrameClass;
import rickelectric.img.ImageLoader;

public class File_MyFiles extends JPanel implements Runnable, Observer {
	private static final long serialVersionUID = 1L;

	public static final API_File.FileSection MYFILES = API_File.FINISHED,
			RECYCLER = API_File.DELETED;

	protected JScrollPane resultScroller;
	private JPanel resultPanel;
	private boolean hardReload;

	private API_File.FileSection mode = MYFILES;

	private JLabel label_loading;

	private boolean loading;

	private int numResults = 0;

	/**
	 * @wbp.parser.constructor
	 */
	public File_MyFiles(API_File.FileSection mode, boolean hardReload) {
		this(mode);

		refreshMyFiles(hardReload);
	}

	private File_MyFiles(API_File.FileSection mode) {
		super();
		if (mode == null) {
			return;
		}
		this.mode = mode;
		this.hardReload = false;
		this.loading = false;

		setLayout(null);
		setBackground(UtilBox.getInstance().getRandomColor());
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

		API_File.addObserver(this);
	}

	public void refreshMyFiles(final boolean hardReload) {
		if (loading)
			return;
		resultPanel.removeAll();
		resultPanel.setLayout(null);

		this.hardReload = hardReload;

		label_loading = new JLabel();
		label_loading.setHorizontalAlignment(SwingConstants.CENTER);
		label_loading.setIcon(new ImageIcon(ImageLoader.class
				.getResource("ajax-loader.gif")));
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
			FileIcon ico = new FileIcon(o[i]);
			ico.num(numResults + i + 1);
			pane.add(ico);
			i++;
		}
		pane.setBackground(resultPanel.getBackground());
		resultPanel.add(pane);
		resultPanel.add(Box.createVerticalStrut(3));
	}

	@Override
	public void run() {
		if (loading == true)
			return;
		loading = true;
		try {
			int position = resultScroller.getVerticalScrollBar().getValue();
			if (hardReload)
				position = resultScroller.getHorizontalScrollBar().getMinimum();

			if (hardReload) {
				API_File.update(mode);
			}
			hardReload = false;
			IndexPair[] fs = API_File.getFileIDs(mode);

			resultPanel.removeAll();
			resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
			resultPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));

			numResults = 0;
			FurkFile[] oArr = new FurkFile[3];
			int i = 0;
			for (IndexPair ip : fs) {
				String fid = ip.getKey();
				FurkFile o = API_File.getFile(mode, fid);
				oArr[i] = o;
				i++;
				if (i == 3) {
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
						.setStatus("Loaded: " + numResults + " "
								+ (mode == MYFILES ? "Finished" : "Deleted")
								+ " Files");
			} catch (Exception e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
			label_loading.setIcon(new ImageIcon(ImageLoader.getInstance()
					.getImage("remove.png")));
		}
		try {
			if (getTopLevelAncestor() instanceof AppFrameClass) {
				((AppFrameClass) getTopLevelAncestor()).addConsole();
				((AppFrameClass) getTopLevelAncestor()).addImgCacheViewer();
			}
		} catch (Exception e) {
		}
		loading = false;
	}

	public void removeIcon(FileIcon icon) {
		JPanel prev = null;
		boolean propagate = false;
		int prevNum = 0;
		Component[] cpl = resultPanel.getComponents();
		List<JPanel> cps = getPanels(cpl);
		for (int i = 0; i < cps.size(); i++) {
			JPanel c = cps.get(i);
			prev = c;
			List<FileIcon> pIcons = getIcons(prev);
			int index = pIcons.indexOf(icon);
			if (index >= 0) {
				prev.remove(icon);
				prevNum = icon.num();
				if (index == 0) {
					pIcons.get(0).num(prevNum++);
					pIcons.get(1).num(prevNum++);
				}
				if (index == 1) {
					pIcons.get(1).num(prevNum++);
				}
				propagate = true;
			}
			if (propagate && i < cps.size() - 1) {
				JPanel next = cps.get(i + 1);
				List<FileIcon> ics = getIcons(next);
				for (FileIcon s : ics)
					s.num(prevNum++);
				next.remove(ics.get(0));
				prev.add(ics.get(0));
				prev.repaint();
				next.repaint();
			}
			if (i == cps.size() - 1) {
				JPanel next = cps.get(i);
				List<FileIcon> ics = getIcons(next);
				if (ics.size() == 0)
					resultPanel.remove(next);
			}
		}
		resultPanel.repaint();
	}

	private List<JPanel> getPanels(Component[] cs) {
		ArrayList<JPanel> panel = new ArrayList<JPanel>();
		for (Component c : cs) {
			if (c instanceof JPanel) {
				panel.add((JPanel) c);
			}
		}
		return panel;
	}

	private List<FileIcon> getIcons(JPanel prev) {
		ArrayList<FileIcon> icons = new ArrayList<FileIcon>();
		for (Component c : prev.getComponents()) {
			if (c instanceof FileIcon) {
				icons.add((FileIcon) c);
			}
		}
		return icons;
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if(arg1.equals(mode)){
			refreshMyFiles(false);
		}
	}
}
