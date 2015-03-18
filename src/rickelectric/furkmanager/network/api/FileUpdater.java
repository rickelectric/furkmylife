package rickelectric.furkmanager.network.api;

public class FileUpdater extends Thread {

	private static FileUpdater thisInst = null;

	public static synchronized FileUpdater getInstance() {
		if (thisInst == null)
			thisInst = new FileUpdater();
		return thisInst;
	}

	public static synchronized void destroyInstance() {
		if(thisInst==null) return;
		thisInst.running = false;
		thisInst.interrupt();
		thisInst = null;
	}

	private boolean running;

	public FileUpdater() {
		setDaemon(true);
		running = true;
	}

	public void run() {
		try {
			API_File.update(API_File.FINISHED);
			API_File.update(API_File.DELETED);
			sleep(30000);
			while (running) {
				try {
					API_File.update(API_File.FINISHED);
					sleep(30000);
				} catch (InterruptedException e) {
					System.err.println("Updating & Restarting File Thread...");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}