package org.rickelectric.furkmanager.sockstest;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.util.concurrent.Executors;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;


public class ServerFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ServerFrame frame = new ServerFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	Thread t;
	
	/**
	 * Create the frame.
	 */
	public ServerFrame(){
		setTitle("Server Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		final JTextArea output_request = new JTextArea();
		output_request.setEditable(false);
		output_request.setBorder(new BevelBorder(BevelBorder.LOWERED));
		contentPane.add(output_request, BorderLayout.CENTER);
		
		Executors.newSingleThreadExecutor().execute(new Runnable(){
			public void run(){
				/*ServerLink s=new ServerLink(output_request);
				t=new Thread(s);
				t.start();*/
			}
		});
	}

}
