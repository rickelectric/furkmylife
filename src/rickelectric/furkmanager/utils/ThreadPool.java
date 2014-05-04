package rickelectric.furkmanager.utils;

import java.util.List;
import java.util.concurrent.*;

public class ThreadPool{
	
	private static ExecutorService lowPool=null;
	private static ExecutorService highPool=null;
	private static ThreadFactory factory=null;
	
	public static void init(){
		if(lowPool==null) lowPool=Executors.newFixedThreadPool(3);
		if(highPool==null) highPool=Executors.newCachedThreadPool(Executors.privilegedThreadFactory());
		if(factory==null) factory=Executors.privilegedThreadFactory();
	}
	
	public static List<Runnable> createNewPool(){
		if(lowPool==null){
			lowPool=Executors.newCachedThreadPool();
			return null;
		}
		else{
			List<Runnable> list=lowPool.shutdownNow();
			lowPool=Executors.newCachedThreadPool();
			return list;
		}
	}
	
	public static Thread getThread(Runnable r){
		if(factory==null) init();
		return factory.newThread(r);
	}
	
	public static void run(Runnable r){
		if(lowPool==null) init();
		lowPool.execute(r);
	}
	
	public static void priorityExecute(Runnable r){
		if(highPool==null) init();
		highPool.execute(r);
	}
	
	public static void shutdown(boolean force){
		if(lowPool==null) return;
		if(force) lowPool.shutdownNow();
		else lowPool.shutdown();
	}
	
	
}
