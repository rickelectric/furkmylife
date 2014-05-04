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
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.network.API;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.icons.FileIconPanel;
import rickelectric.furkmanager.views.windows.AppFrameClass;

public class File_MyFiles extends JPanel {
	private static final long serialVersionUID = 1L;
	
	private enum Mode{MYFILES,RECYCLER}

	public static final Mode MYFILES = Mode.MYFILES, RECYCLER = Mode.RECYCLER;

	private JScrollPane resultScroller;
	private JPanel resultPanel;

	private Mode mode = MYFILES;

	public File_MyFiles(Mode mode) {
		super();
		if(mode==null){return;}
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
		
		refreshMyFiles(true);
	}

	public void refreshMyFiles(final boolean hardReload) {
		resultPanel.removeAll();
		resultPanel.setLayout(null);

		final JLabel label_loading = new JLabel();
		label_loading.setHorizontalAlignment(SwingConstants.CENTER);
		label_loading.setIcon(new ImageIcon(FurkManager.class.getResource("img/ajax-loader.gif")));
		label_loading.setBounds(200, 123, 107, 91);
		resultPanel.add(label_loading);

		repaint();

		Thread tLoad = ThreadPool.getThread(new Runnable() {

			private void populateFileManager(APIObject[] o) {
				JPanel pane;
				pane = new JPanel();
				pane.setLayout(new FlowLayout(FlowLayout.LEFT, 17, 3));
				pane.setPreferredSize(new Dimension(510, 142));
				int i = 0;
				while (i < 3 && i < o.length && o[i] != null) {
					pane.add(new FileIconPanel(FileIconPanel.SMALL_MODE, o[i]));
					i++;
				}
				pane.setBackground(resultPanel.getBackground());
				resultPanel.add(pane);
				resultPanel.add(Box.createVerticalStrut(3));
			}

			public void run() {
				try {
					int position = resultScroller.getVerticalScrollBar()
							.getValue();
					if (hardReload)
						position = resultScroller.getHorizontalScrollBar()
								.getMinimum();

					ArrayList<APIObject> ffarray = hardReload 
							? (mode == MYFILES 
								? API.File.getAllFinished()
								: API.File.getAllDeleted()
								)
							: (mode==RECYCLER?
									API.File.getAllDeleted():
									API.File.getAllCached()
								);

					resultPanel.removeAll();
					resultPanel.setLayout(new BoxLayout(resultPanel,
							BoxLayout.Y_AXIS));
					resultPanel.setBorder(BorderFactory.createEmptyBorder(3, 3,
							3, 3));
					if (ffarray == null)
						return;

					int numResults = 0;
					APIObject[] oArr = new APIObject[3];
					int i = 0;
					for (APIObject o : ffarray) {
						if (i < 3) {
							oArr[i] = o;
							i++;
						} else {
							populateFileManager(oArr);
							oArr = new APIObject[3];
							i = 0;
							numResults += 3;
						}
					}
					if (i <= 3 && i != 0) {
						populateFileManager(oArr);
						numResults += i;
					}

					resultScroller.getVerticalScrollBar().setValue(position);
					getTopLevelAncestor().repaint();
					((AppFrameClass) getTopLevelAncestor())
							.setStatus("My Files Loaded: " + numResults
									+ " Files");
				} catch (Exception e) {
					e.printStackTrace();
					label_loading.setIcon(new ImageIcon(FurkManager.class.getResource("img/remove.png")));
				}
				((AppFrameClass)getTopLevelAncestor()).addConsole();
				((AppFrameClass)getTopLevelAncestor()).addImgCacheViewer();
			}
		});
		tLoad.start();

	}
}
