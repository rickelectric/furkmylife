package org.rickelectric.furkmanager.sockstest;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.EmptyBorder;

import org.rickelectric.furkmanager.network.ClientLink;

public class ClientFrame extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextField input_text;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ClientFrame frame = new ClientFrame();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public ClientFrame() {
		setTitle("Client Test");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 490, 370);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblText = new JLabel("Text: ");
		lblText.setBounds(10, 11, 46, 14);
		contentPane.add(lblText);
		
		input_text = new JTextField();
		input_text.setBounds(47, 8, 295, 20);
		contentPane.add(input_text);
		input_text.setColumns(10);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		scrollPane.setBounds(10, 79, 454, 227);
		contentPane.add(scrollPane);
		
		final JTextArea output_response = new JTextArea();
		scrollPane.setViewportView(output_response);
		
		JButton button_send = new JButton("Send");
		button_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e){
				String txt=input_text.getText();
				if(txt.length()==0) return;
				try{
					ClientLink i=new ClientLink(ClientLink.ADD_HASH,txt);
					Thread t=new Thread(i);
					t.start();
					while(t.isAlive());
					output_response.append(i.response()+"\n");
					
				}catch(Exception ex){}
			}
		});
		button_send.setBounds(366, 8, 98, 20);
		contentPane.add(button_send);
		
		
		
		JLabel lblServerResponse = new JLabel("Server Response: ");
		lblServerResponse.setBounds(10, 52, 135, 16);
		contentPane.add(lblServerResponse);
	}
}
