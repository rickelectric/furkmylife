package rickelectric.furkmanager.views.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.APIObject;
import rickelectric.furkmanager.models.FurkUserData;
import rickelectric.furkmanager.models.URI_Enums;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.network.api.API_File;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.icons.FileIcon;
import rickelectric.furkmanager.views.windows.AppFrameClass;

public class SearchPanel extends JPanel implements ActionListener, Runnable {
	private static final long serialVersionUID = 1L;

	public static final int FILESEARCH = 100, FURKSEARCH = 101,
			METASEARCH = 102;

	private JLabel loading;
	private JScrollPane resultScroller;
	private JPanel resultPanel;
	private JTextField input_searchtext;
	private int searchMode;

	private boolean isSearching = false;
	private JButton button_search;
	private JRadioButton mod_full;
	private JRadioButton mod_yes;
	private JRadioButton mod_no;
	private JPanel panel;
	private final ButtonGroup group_moderator = new ButtonGroup();

	private void isSearching(boolean b) {
		isSearching = b;

		if (b) {
			input_searchtext.setBounds(9, 20, 450, 20);
			button_search.setBounds(339, 54, 120, 23);
			loading.setVisible(b);
		} else {
			loading.setVisible(b);
			input_searchtext.setBounds(9, 20, 511, 20);
			button_search.setBounds(400, 54, 120, 23);
			if (getTopLevelAncestor() instanceof AppFrameClass) {
				((AppFrameClass) getTopLevelAncestor()).addConsole();
				((AppFrameClass) getTopLevelAncestor()).addImgCacheViewer();
			}
		}
	}

	public SearchPanel(int searchMode) {
		this();
		this.searchMode = searchMode;
	}

	public SearchPanel() {
		new UtilBox();
		setBackground(UtilBox.getRandomColor());
		searchMode = METASEARCH;
		setLayout(null);
		setSize(554, 370);
		final JPanel searchPanel = new JPanel();
		searchPanel.setBackground(getBackground());
		searchPanel.setBorder(new TitledBorder(null, "Search",
				TitledBorder.LEADING, TitledBorder.TOP, null, null));
		searchPanel.setBounds(10, 12, 530, 100);
		add(searchPanel);
		searchPanel.setLayout(null);

		input_searchtext = new JTextField();
		input_searchtext.setBounds(9, 20, 511, 20);
		searchPanel.add(input_searchtext);
		input_searchtext.setColumns(10);

		button_search = new JButton("Search");
		button_search.setBounds(400, 54, 120, 23);
		button_search.addActionListener(this);
		input_searchtext.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_ENTER)
					search();
			}
		});
		searchPanel.add(button_search);

		loading = new JLabel();
		loading.setBounds(470, 11, 50, 50);
		loading.setIcon(new ImageIcon(FurkManager.class
				.getResource("img/ajax-loader-48.gif")));
		loading.setVisible(false);
		searchPanel.add(loading);

		panel = new JPanel();
		panel.setOpaque(false);
		panel.setBorder(new TitledBorder(new LineBorder(new Color(0, 0, 0)),
				"Moderator (Safe Search)", TitledBorder.LEADING,
				TitledBorder.TOP, null, null));
		panel.setBounds(9, 42, 305, 53);

		if (searchMode != FILESEARCH && FurkUserData.User.flags != null
				&& FurkUserData.User.flags == URI_Enums.Prefs_Flags.NONE)
			searchPanel.add(panel);

		panel.setLayout(null);

		mod_full = new JRadioButton("Fully Moderated");
		mod_full.setSelected(true);
		group_moderator.add(mod_full);
		mod_full.setOpaque(false);
		mod_full.setBounds(8, 21, 121, 24);
		panel.add(mod_full);

		mod_yes = new JRadioButton("Yes (On)");
		group_moderator.add(mod_yes);
		mod_yes.setOpaque(false);
		mod_yes.setBounds(133, 21, 82, 24);
		panel.add(mod_yes);

		mod_no = new JRadioButton("No (Off)");
		group_moderator.add(mod_no);
		mod_no.setOpaque(false);
		mod_no.setBounds(219, 21, 68, 24);
		panel.add(mod_no);

		resultScroller = new JScrollPane(
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		resultScroller.getVerticalScrollBar().setBlockIncrement(20);
		resultScroller.getViewport().setBackground(getBackground());
		resultScroller.setBounds(10, 124, 530, 233);
		add(resultScroller);

		resultPanel = new JPanel();
		resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
		resultPanel.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		resultPanel.setBackground(getBackground());
		resultScroller.setViewportView(resultPanel);

	}

	@Override
	public void run() {
		try {
			resultPanel.removeAll();
			resultPanel.setBounds(10, 115, 574, 42);

			int numResults = furkSearch();
			if (numResults <= 2) {
				int delnum = 2 - numResults;
				int structsize = (delnum * 88) - 5;
				resultPanel.add(Box.createVerticalStrut(structsize));
				resultPanel.setLocation(0, 0);
				resultScroller.repaint();
			}
			if (numResults > 0) {
				setStatus("Displaying " + numResults + " Found Files");
				resultScroller.getVerticalScrollBar().setBlockIncrement(200);
				resultScroller.getVerticalScrollBar().setUnitIncrement(50);
			} else
				setStatus("No Files Found.");
			isSearching(false);
		} catch (Exception e) {
			isSearching(false);
		}
	}

	private void search() {
		if (isSearching)
			return;
		isSearching(true);
		new Thread(this).start();
	}

	private void setStatus(String s) {
		try {
			((AppFrameClass) getTopLevelAncestor()).setStatus(s);
		} catch (Exception e) {
		}
	}

	private int furkSearch() {
		int numResults = 0;
		String txt = input_searchtext.getText();

		ArrayList<APIObject> ffarray = null;
		if (searchMode == FURKSEARCH)
			ffarray = API.search(txt, API.FURKSEARCH);
		else if (searchMode == METASEARCH)
			ffarray = API.search(txt, API.METASEARCH);
		else if (searchMode == FILESEARCH)
			ffarray = API_File.find(txt);
		for (APIObject o : ffarray) {
			populateResultField(o, true);
			numResults++;
		}
		return numResults;
	}

	private void populateResultField(APIObject o, boolean wide) {
		FileIcon pane = new FileIcon(o);

		resultPanel.add(pane);
		resultPanel.add(Box.createVerticalStrut(5));
	}

	@Override
	public void actionPerformed(ActionEvent event) {
		String command = event.getActionCommand();
		if (command.equals("Search")) {
			search();
		}
	}
}
