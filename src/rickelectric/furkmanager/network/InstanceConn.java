package rickelectric.furkmanager.network;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.network.socks.ClientLink;
import rickelectric.furkmanager.network.socks.ServerLink;
import rickelectric.furkmanager.utils.UtilBox;

/**
 * Ensures Only One Instance Of The Application Is Running At A Time Using
 * Sockets. <br/>
 * 
 * @author Rick Lewis (Ionicle)
 *
 */
public class InstanceConn {

	private boolean appStart = false;

	private ClientLink c;
	private Thread cli;

	private ServerLink s;

	public boolean appStart() {
		return appStart;
	}

	public InstanceConn() throws InterruptedException {
		c = new ClientLink(ClientLink.PING, null);
		cli = new Thread(c);
		cli.start();
		while (cli.isAlive())
			;
		if (c.response().equals("accepted"))
			appStart = false;
		else if (c.response().equals("no-server")) {
			s = new ServerLink();
			s.run();
			UtilBox.pause(2000);
			if (s.error() != null && s.error().equals("Port Error")) {
				FurkManager.trayAlert(FurkManager.TRAY_ERROR, "Error",
						s.error(), null);
				appStart = false;
			}
			appStart = true;
		}
	}

	public boolean transmit(String s) {
		if (!appStart) {
			c = new ClientLink(ClientLink.ADD_TORRENT, s);
			cli = new Thread(c);
			cli.start();
			while (cli.isAlive())
				;
			if (c.response().equals("accepted"))
				return true;
		}
		return false;
	}

}
