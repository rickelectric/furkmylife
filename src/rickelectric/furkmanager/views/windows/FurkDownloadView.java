package rickelectric.furkmanager.views.windows;

import java.awt.Color;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import rickelectric.furkmanager.models.FurkDownload;

public class FurkDownloadView extends JDialog {

	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JTextField input_id;
	private JTextField input_name;
	private JTextField input_size;
	private JLabel lblProgress;
	private JLabel lblSpeed;
	private JTextField input_uspeed;
	private JProgressBar progress_have;
	private JLabel lblDownSpeed;
	private JTextField input_dspeed;
	
	public FurkDownloadView(FurkDownload dl){
		setResizable(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 477, 362);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		super.setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblDownloadId = new JLabel("Download ID: ");
		lblDownloadId.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblDownloadId.setBounds(10, 9, 94, 20);
		contentPane.add(lblDownloadId);
		
		JLabel lblName = new JLabel("Name: ");
		lblName.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblName.setBounds(10, 40, 94, 20);
		contentPane.add(lblName);
		
		JLabel lblSize = new JLabel("Size: ");
		lblSize.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblSize.setBounds(10, 71, 94, 20);
		contentPane.add(lblSize);
		
		input_id = new JTextField(dl.getId());
		input_id.setEditable(false);
		input_id.setBackground(Color.WHITE);
		input_id.setFont(new Font("Dialog", Font.PLAIN, 12));
		input_id.setBounds(114, 9, 210, 20);
		contentPane.add(input_id);
		input_id.setColumns(10);
		
		input_name = new JTextField(dl.getName());
		input_name.setBackground(Color.WHITE);
		input_name.setEditable(false);
		input_name.setFont(new Font("Dialog", Font.PLAIN, 12));
		input_name.setColumns(10);
		input_name.setBounds(114, 40, 342, 20);
		contentPane.add(input_name);
		
		input_size = new JTextField(dl.getSizeString());
		input_size.setBackground(Color.WHITE);
		input_size.setEditable(false);
		input_size.setFont(new Font("Dialog", Font.PLAIN, 12));
		input_size.setColumns(10);
		input_size.setBounds(114, 71, 116, 20);
		contentPane.add(input_size);
		
		lblProgress = new JLabel("Progress: ");
		lblProgress.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblProgress.setBounds(10, 103, 94, 20);
		contentPane.add(lblProgress);
		
		progress_have = new JProgressBar();
		progress_have.setMaximum(100);
		progress_have.setValue((int)Float.parseFloat(dl.getHave()));
		progress_have.setStringPainted(true);
		//progress_have.setIndeterminate(true);
		progress_have.setBounds(114, 103, 342, 20);
		contentPane.add(progress_have);
		
		lblSpeed = new JLabel("Up Speed:");
		lblSpeed.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblSpeed.setBounds(10, 142, 94, 20);
		contentPane.add(lblSpeed);
		
		input_uspeed = new JTextField(dl.getUpSpeed()+"");
		input_uspeed.setFont(new Font("Dialog", Font.PLAIN, 12));
		input_uspeed.setEditable(false);
		input_uspeed.setColumns(10);
		input_uspeed.setBackground(Color.WHITE);
		input_uspeed.setBounds(114, 142, 200, 20);
		contentPane.add(input_uspeed);
		
		lblDownSpeed = new JLabel("Down Speed:");
		lblDownSpeed.setFont(new Font("Dialog", Font.PLAIN, 12));
		lblDownSpeed.setBounds(10, 173, 94, 20);
		contentPane.add(lblDownSpeed);
		
		input_dspeed = new JTextField(dl.getDownSpeed()+"");
		input_dspeed.setFont(new Font("Dialog", Font.PLAIN, 12));
		input_dspeed.setEditable(false);
		input_dspeed.setColumns(10);
		input_dspeed.setBackground(Color.WHITE);
		input_dspeed.setBounds(114, 173, 200, 20);
		contentPane.add(input_dspeed);
		
		setVisible(true);
	}
}
