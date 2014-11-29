package rickelectric.furkmanager.setup;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class SetupWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	private JButton btn_Next;
	private JButton btn_SkipSetupuse;
	private JButton btn_Cancel;

	protected int pane;
	
	protected JPanel[] panels;
	private JPanel buttonPane;
	
	public SetupWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		
		panels = new JPanel[5];
		pane = 0;
		
		contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		contentPane.add(contentPane(), BorderLayout.CENTER);
		
		buttonPane = new JPanel();
		contentPane.add(buttonPane, BorderLayout.SOUTH);
		
		btn_SkipSetupuse = new JButton("Skip Setup (Use Defaults)");
		btn_SkipSetupuse.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int opt = JOptionPane.showConfirmDialog(contentPane, "Are You Sure You Want To Set Default Settings?", "Confirm Default Settings", JOptionPane.OK_CANCEL_OPTION);
				if(opt==JOptionPane.YES_OPTION){
					defaults();
					complete();
					dispose();
				}
			}
		});
		btn_SkipSetupuse.setBounds(10, 227, 165, 23);
		buttonPane.add(btn_SkipSetupuse);
		
		btn_Next = new JButton("Next>>");
		btn_Next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pane++;
				setContentPane(panels[pane]);
			}
		});
		btn_Next.setBounds(236, 227, 89, 23);
		buttonPane.add(btn_Next);
		
		btn_Cancel = new JButton("Cancel");
		btn_Cancel.setBounds(335, 227, 89, 23);
		buttonPane.add(btn_Cancel);
		
	}

	private JPanel contentPane() {
		JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		
		JLabel lblThisIsYour = new JLabel("This Is Your First Time Running FurkManager On This PC");
		lblThisIsYour.setFont(new Font("Dialog", Font.BOLD, 14));
		lblThisIsYour.setHorizontalAlignment(SwingConstants.CENTER);
		lblThisIsYour.setBounds(10, 11, 414, 57);
		contentPane.add(lblThisIsYour);
		
		JLabel lblPleaseTakeThis = new JLabel("Please Take This Time To Set Your Default Preferences");
		lblPleaseTakeThis.setHorizontalAlignment(SwingConstants.CENTER);
		lblPleaseTakeThis.setFont(new Font("Dialog", Font.BOLD, 14));
		lblPleaseTakeThis.setBounds(10, 79, 414, 57);
		contentPane.add(lblPleaseTakeThis);
		
		return contentPane;
	}

	protected void complete() {
		
	}

	protected void defaults() {
		
	}
}
