package rickelectric.furkmanager.views.windows;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.ConsoleWin;

public class APIConsole extends JFrame implements ConsoleWin {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;

	private JTextField input_text;
	private JTextArea output_result;
	private JProgressBar bar = null;

	private JScrollPane scrollPane;
	private JPanel panel_top;
	private JComboBox<String> method;

	// private JPanel centerPane;

	public static void main(String[] args) {
		SettingsManager.init();
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					APIConsole frame = new APIConsole();
					frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public APIConsole() {
		setMinimumSize(new Dimension(450, 400));
		setTitle("API Console");
		setBounds(100, 100, 450, 400);
		setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);

		/*
		 * centerPane = new JPanel(); centerPane.setLayout(null);
		 * contentPane.add(centerPane,BorderLayout.CENTER);
		 * 
		 * JLabel lblResult = new JLabel("Result:"); lblResult.setBounds(10, 11,
		 * 55, 16); centerPane.add(lblResult);
		 */

		scrollPane = new JScrollPane();
		scrollPane.setBorder(new CompoundBorder(new TitledBorder(
				new LineBorder(new Color(184, 207, 229)), "Result",
				TitledBorder.LEADING, TitledBorder.TOP, null, null), UIManager
				.getBorder("ScrollPane.border")));
		scrollPane.setBounds(10, 39, 448, 327);
		contentPane.add(scrollPane, BorderLayout.CENTER);

		output_result = new JTextArea();
		output_result.setFont(new Font("Dialog", Font.BOLD, 12));
		scrollPane.setViewportView(output_result);

		panel_top = new JPanel();
		panel_top.setPreferredSize(new Dimension(10, 70));
		contentPane.add(panel_top, BorderLayout.NORTH);
		panel_top.setLayout(null);

		JLabel lblUri = new JLabel("URI:");
		lblUri.setBounds(12, 12, 40, 16);
		lblUri.setPreferredSize(new Dimension(40, 16));
		panel_top.add(lblUri);

		input_text = new JTextField();
		input_text.setBounds(58, 10, 364, 20);
		input_text.setPreferredSize(new Dimension(60, 20));
		panel_top.add(input_text);
		input_text.setFont(new Font("Arial", Font.BOLD, 14));
		input_text.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				if (arg0.getKeyCode() == KeyEvent.VK_ENTER) {
					new Thread(new Runnable() {
						public void run() {
							request();
						}
					}).start();
				}
			}
		});
		input_text.setColumns(30);

		method = new JComboBox<String>();
		method.setModel(new DefaultComboBoxModel<String>(new String[] { "GET",
				"POST" }));
		method.setBounds(83, 38, 107, 20);
		panel_top.add(method);

		JLabel lblMethod = new JLabel("Method:");
		lblMethod.setBounds(10, 40, 55, 16);
		panel_top.add(lblMethod);

	}

	private void request() {
		Rectangle ibound = input_text.getBounds();
		String s = input_text.getText();
		if (bar == null) {
			bar = new JProgressBar();
			bar.setIndeterminate(true);
			bar.setBounds(ibound);
			panel_top.add(bar);
		}
		input_text.transferFocus();
		input_text.setVisible(false);
		bar.setVisible(true);

		if (APIBridge.key() == null) {
			API.init("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		}
		String key = APIBridge.key();
		String[] q = s.split("[?]");
		s = q[0] += "?" + key + "&pretty=1";
		if (q.length > 1) {
			s += "&" + q[1];
		}
		s = "https://www.furk.net/api" + s;
		try {
			String m = method.getSelectedItem().toString();

			String json;
			if (m.equals("GET"))
				json = APIBridge.jsonGet(s, false, false);
			else
				json = APIBridge.jsonPost(s, false, false);

			output_result.setText(json);
		} catch (Exception e) {
			output_result.setText(e.getMessage());
		}
		scrollPane.getVerticalScrollBar().setValue(0);
		bar.setVisible(false);
		input_text.setVisible(true);

	}
}
