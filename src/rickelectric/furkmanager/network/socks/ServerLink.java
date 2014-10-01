package rickelectric.furkmanager.network.socks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

import rickelectric.furkmanager.FurkManager;
import rickelectric.furkmanager.network.APIBridge;
import rickelectric.furkmanager.utils.SettingsManager;
import rickelectric.furkmanager.views.windows.AddDownloadFrame;

/**
 * This Enables The User To Add A Torrent File By Opening It With The .exe
 * Launcher. <br/>
 * It Also Initializes The Google Chrome Extension Interface
 * 
 * @author Rick Lewis (Ionicle)
 * 
 */
public class ServerLink {
	
	public static void main(String[] args){
		new ServerLink().run();
	}

	private String error = null;
	private Thread localThread;
	private Thread webSockThread;

	public String error() {
		return error;
	}

	public void run() {
		localThread = new Thread(new Runnable() {
			public void run() {
				localServerProcess();
			}
		});
		try {
			webSockThread = new Thread(new CrxSocketLink(
					SettingsManager.getWebSockPort()));
			webSockThread.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}

		localThread.start();
	}

	private void localServerProcess() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(SettingsManager.getLocalPort());
		} catch (Exception e) {
			error = "Port Error";
			return;
		}
		while (!Thread.interrupted()) {
			Socket cliSocket = null;
			try {
				cliSocket = serverSocket.accept();
				debug("Connected");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			final Socket clientSocket = cliSocket;
			new Thread(new Runnable() {
				public void run() {
					try {
						PrintWriter out = new PrintWriter(clientSocket
								.getOutputStream(), true);
						BufferedReader in = new BufferedReader(
								new InputStreamReader(clientSocket
										.getInputStream()));

						String cText = null;
						String cResponse = "running";
						cText = in.readLine();

						if (cText.equals("url")) {
							debug("URL Recieved");
							if (passURL(in.readLine()))
								cResponse = "accepted";
							else
								cResponse = "url add failed";
						} else if (cText.equals("infoHash")) {
							debug("Info Hash Recieved");
							if (passHash(in.readLine()))
								cResponse = "accepted";
							else
								cResponse = "hash add failed";
						}

						else if (cText.equals("torrent")) {
							debug("Torrent Recieved");
							if (passTorrent(in.readLine()))
								cResponse = "accepted";
							else
								cResponse = "torrent add failed";
						} else if (cText.equals("ping")) {
							FurkManager.appRun();
						} else {
							while (!(cText = in.readLine())
									.equalsIgnoreCase("END"))
								System.out.println(cText);
						}

						debug("Response>> " + cResponse);
						out.print(cResponse + "\r\n"); // send the response to
														// client
						out.flush();
						out.close();
						in.close();
						clientSocket.close();
					} catch (Exception e) {
						debug(">>Connection Closed");
						try {
							clientSocket.close();
						} catch (Exception x) {
						}
					}
				}
			}).start();
		}
		try {
			serverSocket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void debug(String msg) {

	}

	private boolean passTorrent(final String link) {
		new Thread(new Runnable() {
			public void run() {
				while (APIBridge.key() == null)
					;
				new AddDownloadFrame(link);
			}
		}).start();
		return true;
	}

	private boolean passHash(final String link) {
		new Thread(new Runnable() {
			public void run() {
				while (APIBridge.key() == null)
					;
				new AddDownloadFrame(link);
			}
		}).start();
		return true;
	}

	private boolean passURL(final String link) {
		new Thread(new Runnable() {
			public void run() {
				while (APIBridge.key() == null)
					;
				new AddDownloadFrame(link);
			}
		}).start();
		return true;
	}

	public void shutdown() {
		localThread.interrupt();
		webSockThread.interrupt();
	}
}