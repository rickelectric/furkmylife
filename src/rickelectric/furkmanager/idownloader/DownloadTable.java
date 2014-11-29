package rickelectric.furkmanager.idownloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JOptionPane;
import javax.swing.JProgressBar;
import javax.swing.table.AbstractTableModel;

public class DownloadTable extends AbstractTableModel implements Observer {
	private static final long serialVersionUID = 1L;

	private static final String[] columnNames = { "Name", "Size", "Progress",
			"Status" };
	
	private static final Class<?>[] columnClasses = {
		String.class,
		String.class,
		JProgressBar.class,
		String.class };
	
	public void persist(){
		try{
			if(persistFile.exists()) persistFile.delete();
			FileOutputStream o = new FileOutputStream(persistFile);
			ObjectOutputStream oos=new ObjectOutputStream(o);
			oos.writeInt(downloadList.size());//Number of Downloads
			for(FileDownload d:downloadList){
				if(FileDownload.DOWNLOADING==d.getStatus())
				d.suspend();
				oos.writeObject(d);
			}
			oos.flush();
			oos.close();
			o.close();
		}catch(IOException e){}
	}
	
	public void reload(){
		try{
			FileInputStream i=new FileInputStream(persistFile);
			ObjectInputStream ois=new ObjectInputStream(i);
			int len=ois.readInt();
			for(int x=0;x<len;x++){
				FileDownload d=(FileDownload)ois.readObject();
				addDownload(d);
				if(d.getStatus()==FileDownload.SUSPENDED)
					d.resume();
			}
			ois.close();
			i.close();
		}catch(Exception e){}
	}
	
	public DownloadTable(){
		super();
		reload();
	}
		
	private static ArrayList<FileDownload> downloadList = new ArrayList<FileDownload>();

	private static final File persistFile = new File("downloads.dat");
	
	public void addDownload(FileDownload fileDownload){
		if(downloadList.contains(fileDownload)) return;
		fileDownload.addObserver(this);
		downloadList.add(fileDownload);
		fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
	}

	public FileDownload getDownload(int row) {
		return downloadList.get(row);
	}
	
	public void clearDownload(FileDownload r){
		if(r.getFile()!=null&&r.getFile().exists()){
			if(r.getStatus()==FileDownload.COMPLETE){
				int alert=JOptionPane.showConfirmDialog(null,
					"This download is complete.\nDelete completely downloaded file from disk?",
					"Confirm Delete", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE
				);
				if(alert==JOptionPane.YES_OPTION) r.getFile().delete();
				else if(alert==JOptionPane.CANCEL_OPTION) return;
			}
			else{
				r.getFile().delete();
			}
			
		}
		int row=downloadList.indexOf(r);
		downloadList.remove(r);
		fireTableRowsDeleted(row, row);
		System.gc();
	}
	
	public void clearDownload(int row) {
		FileDownload r=getDownload(row);
		if(r.getFile()!=null&&r.getFile().exists()){
			if(r.getStatus()==FileDownload.COMPLETE){
				int alert=JOptionPane.showConfirmDialog(null,
					"This download is complete.\nDelete completely downloaded file from disk?",
					"Confirm Delete", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.WARNING_MESSAGE
				);
				if(alert==JOptionPane.YES_OPTION) r.getFile().delete();
				else if(alert==JOptionPane.CANCEL_OPTION) return;
			}
			else{
				r.getFile().delete();
			}
			
		}
		r=null;
		downloadList.remove(row);
		fireTableRowsDeleted(row, row);
		System.gc();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public String getColumnName(int col) {
		return columnNames[col];
	}
	
	@Override
	public Class<?> getColumnClass(int col) {
		return columnClasses[col];
	}
	
	@Override
	public int getRowCount() {
		return downloadList.size();
	}

	@Override
	public Object getValueAt(int row, int col) {
		FileDownload fileDownload = downloadList.get(row);
		
		switch (col) {
			case 0: // URL
				return fileDownload.getName();
			case 1: // Size
				int size = fileDownload.getSize();
				return (size == -1) ? "N/A" : (getSizeString(fileDownload.downloaded)+"/"+fileDownload.getSizeString());
			case 2: // Progress
				return new Float(fileDownload.getProgress());
			case 3: // Status
				return FileDownload.STATUSES[fileDownload.getStatus()];
		}
		return "";
	}
	
	public String getSizeString(int size){
		float sz=size;
		String[] reps=new String[]{"B","kB","MB","GB","TB"};
		int i=0;
		while(sz>1024&&i<=4){
			sz=sz/1024f;
			i++;
		}
		String[] szs=(""+sz).split("\\.");
		if(szs[1].length()>2) szs[1]=szs[1].substring(0, 2);
		return szs[0]+"."+szs[1]+" "+reps[i];
	}

	@Override
	public void update(Observable o, Object arg) {
		int index = downloadList.indexOf(o);
		fireTableRowsUpdated(index, index);
	}
	
}