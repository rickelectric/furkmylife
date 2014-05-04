package rickelectric.furkmanager.experimentation;

import java.util.ArrayList;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.models.APIMessage;
import rickelectric.furkmanager.network.API;

public class APIMessTest {

	public static void main(String[] argv) {
		API.init("5323228d687ed9f7f1bdf9ce87050a1fa672e485");
		ArrayList<APIMessage> msgs = API.Message.getAll();
		FurkManager.log(msgs.toString());
	}

}
