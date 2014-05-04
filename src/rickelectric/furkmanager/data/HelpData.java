package rickelectric.furkmanager.data;

public class HelpData {
	
	public static String apiKey(boolean login,boolean html){
		String[] lines=new String[5];
		lines[0]="1. Go to https://www.furk.net/ and log in.";
		lines[1]="2. Scroll down to the end of the screen and click \"Open API.\"";
		lines[2]="3. Scroll to \"API methods\" in \"Common parameters and response keys\"";
		lines[3]="4. Under Parameters, Look for \"Your API key is:\".";
		lines[4]="5. Copy the 40-character hexadecimal string and paste into the box below.";
		if(html){
			String ht="<html><body><h2>API Key Help</h3><ul><b>";
			ht+="<li>"+lines[0]+"</li>";
			ht+="<li>"+lines[1]+"</li>";
			ht+="<li>"+lines[2]+"</li>";
			ht+="<li>"+lines[3]+"</li>";
			if(login) ht+="<li>"+lines[4]+"</li>";
			ht+="</b></ul></body></html>";
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
