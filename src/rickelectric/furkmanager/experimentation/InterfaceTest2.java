package rickelectric.furkmanager.beta;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import rickelectric.furkmanager.network.RequestCache;
import rickelectric.furkmanager.network.api.API;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.utils.ThreadPool;
import rickelectric.furkmanager.utils.UtilBox;
import rickelectric.furkmanager.views.panels.SearchPanel;
import rickelectric.furkmanager.views.windows.AppFrameClass;

public class InterfaceTest2 extends AppFrameClass {
	private static final long serialVersionUID = 1L;
	
	private JPanel contentPane;
	
	public static void main(String[] args){
		ThreadPool.init();
		SettingsManager.init();
		UtilBox.init();
		RequestCache.init();
		
		API.init("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		
		ThreadPool.run(new Runnable(){
			public void run() {
				InterfaceTest2 i2=new InterfaceTest2();
				i2.setVisible(true);
			}
			
		});
	}
	
	public InterfaceTest2(){
		super();
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 560, 424);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		SearchPanel searchPanel = new SearchPanel(SearchPanel.METASEARCH);
		setContentPane(searchPanel);
	}
}
