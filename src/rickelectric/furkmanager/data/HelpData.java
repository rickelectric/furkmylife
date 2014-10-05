package rickelectric.furkmanager.data;

import javax.swing.JEditorPane;
import javax.swing.JOptionPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import rickelectric.furkmanager.utils.UtilBox;

public class HelpData {
	
	public static void main(String[] args){
		JEditorPane editorPane = new JEditorPane();
		editorPane.setContentType("text/html");
		String data=HelpData.apiKey(true, true);
		editorPane.setText(data);
		editorPane.setEditable(false);
		editorPane.addHyperlinkListener(new HyperlinkListener(){
			public void hyperlinkUpdate(HyperlinkEvent e) {
				if(e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					UtilBox.openUrl(e.getURL().toExternalForm());
				}
			}
		});
		JOptionPane.showMessageDialog(null, editorPane, "API Key Help",
				JOptionPane.PLAIN_MESSAGE);
	}
	
	public static String apiKey(boolean login,boolean html){
		String[] lines=new String[5];
		String link="http://www.furk.net/";
		if(html) link = "<b>"+link+"</b> (<a href=\""+link+"\" target=\"_blank\">click here</a>)";
		
		String open=html?"<b><i>":"\"";
		String close=html?"</i></b>":"\"";
		
		lines[0]="1. Go to "+link+" and log in using your username and password."+(html?"<br/>":"\n\t")+
				"(or a third party login such as Facebook)";
		lines[1]="2. Scroll down to the end of the page and click "+open+"Open API."+close;
		lines[2]="3. Scroll to "+open+"API methods"+close+" under "+open+"Common parameters and response keys"+close;
		lines[3]="4. Under Parameters, Look for "+open+"Your API key is:"+close+".";
		lines[4]="5. Copy the 40-character hexadecimal string and paste into the "+open+"API Key"+close+" field.";
		if(html){
			String ht="<html><body><h2>API Key Help</h3><ul>";
			ht+="<li>"+lines[0]+"</li>";
			ht+="<li>"+lines[1]+"</li>";
			ht+="<li>"+lines[2]+"</li>";
			ht+="<li>"+lines[3]+"</li>";
			if(login) ht+="<li>"+lines[4]+"</li>";
			ht+="</ul></body></html>";
			return ht;
		}
		else{
			String ret="";
			for(int i=0;i<5;i++){
				if(i==4){
					if(login) ret+="\n"+lines[i];
				}
				else{
					ret+=lines[i];
					if(i<3) ret+="\n";
				}
			}
			return ret;
		}
	}

}
