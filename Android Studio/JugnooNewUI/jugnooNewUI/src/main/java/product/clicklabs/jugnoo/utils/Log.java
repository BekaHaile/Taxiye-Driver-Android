package product.clicklabs.jugnoo.utils;

import android.os.Environment;

import java.io.File;
import java.io.FileWriter;

/**
 * Custom log class overrides Android Log
 * @author shankar
 *
 */
public class Log {
	
	
	public static boolean PRINT = true; 											// true for printing and false for not
	private static final boolean WRITE_TO_FILE = false; 									// true for writing log to file and false for not 
	private static final boolean WRITE_TO_FILE_IN = false; 									// true for writing log to file and false for not 
	
	public Log(){}

	public static void i(String tag, String message){
		if(PRINT){
			android.util.Log.i(tag, message);
			if(WRITE_TO_FILE){
				writeLogToFile(APP_NAME, tag + "<>" + message);
			}
		}
	}

	public static void d(String tag, String message){
		if(PRINT){
			android.util.Log.d(tag, message);
			if(WRITE_TO_FILE){
				writeLogToFile(APP_NAME, tag + "<>" + message);
			}
		}
	}
	
	public static void e(String tag, String message){
		if(PRINT){
			android.util.Log.e(tag, message);
			if(WRITE_TO_FILE){
				writeLogToFile(APP_NAME, tag + "<>" + message);
			}
		}
	}
	
	public static void v(String tag, String message){
		if(PRINT){
			android.util.Log.v(tag, message);
			if(WRITE_TO_FILE){
				writeLogToFile(APP_NAME, tag + "<>" + message);
			}
		}
	}
	
	public static void w(String tag, String message){
		if(PRINT){
			android.util.Log.w(tag, message);
			if(WRITE_TO_FILE){
				writeLogToFile(APP_NAME, tag + "<>" + message);
			}
		}
	}
	
	
	
	static String LOG_FILE = "LOGFILE";
	static String APP_NAME = "Jugnoo";
	
	public static void writeLogToFile(final String filePrefix, final String response) {
		if(WRITE_TO_FILE_IN){
			new Thread(new Runnable() {
	
				@Override
				public void run() {
					try {
						String fileName = Environment.getExternalStorageDirectory() + "/" + filePrefix + "_" + LOG_FILE + ".txt";
						File gpxfile = new File(fileName);
						
						if(!gpxfile.exists()){
							gpxfile.createNewFile();
						}
						
						FileWriter writer = new FileWriter(gpxfile, true);
						writer.append("\n" + response);
						writer.flush();
						writer.close();
						
					} catch (Exception e1) {
						e1.printStackTrace();
					}
				}
			}).start();
		}

	}
	
	
	
	
	
	
	
	
	
	public static File getPathLogFolder(){
		try {
			String strFolder = Environment.getExternalStorageDirectory() + "/JugnooData";
			File folder = new File(strFolder);
			if(!folder.exists()){
				folder.mkdirs();
			}
			return folder;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static File getPathLogFile(final String filePrefix){
		try {
			String fileName = getPathLogFolder() + "/" + filePrefix + ".txt";
			File gpxfile = new File(fileName);
			if (!gpxfile.exists()) {
				gpxfile.createNewFile();
			}
			return gpxfile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static void writePathLogToFile(final String filePrefix, final String response) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					File gpxfile = getPathLogFile(filePrefix);
					if(gpxfile != null){
						FileWriter writer = new FileWriter(gpxfile, true);
						writer.append("\n" + DateOperations.getCurrentTime() + " - "+ response);
						writer.flush();
						writer.close();
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		}).start();
	}
	
	public static void deleteFolder(File folder) {
	    try {
			File[] files = folder.listFiles();
			if(files!=null) { //some JVMs return null for empty dirs
			    for(File f: files) {
			        if(f.isDirectory()) {
			            deleteFolder(f);
			        } else {
			            f.delete();
			        }
			    }
			}
			folder.delete();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void deletePathLogFolder() {
		try {
			deleteFolder(getPathLogFolder());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}

