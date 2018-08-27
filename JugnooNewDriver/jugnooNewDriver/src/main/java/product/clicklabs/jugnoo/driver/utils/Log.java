package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;

import java.io.File;
import java.io.FileWriter;

import product.clicklabs.jugnoo.driver.BuildConfig;

/**
 * Custom log class overrides Android Log
 * @author shankar
 *
 */
public class Log {

	public static void i(String tag, String message){
		if(BuildConfig.DEBUG_MODE){
			android.util.Log.i(tag, message);
		}
	}

	public static void d(String tag, String message){
		if(BuildConfig.DEBUG_MODE){
			android.util.Log.d(tag, message);
		}
	}
	
	public static void e(String tag, String message){
		if(BuildConfig.DEBUG_MODE){
			android.util.Log.e(tag, message);
		}
	}
	
	public static void v(String tag, String message){
		if(BuildConfig.DEBUG_MODE){
			android.util.Log.v(tag, message);
		}
	}
	
	public static void w(String tag, String message){
		if(BuildConfig.DEBUG_MODE){
			android.util.Log.w(tag, message);
		}
	}
	
	


	




	
	
	public static File getPathLogFolder(Context context){
		try {
			String strFolder = context.getFilesDir() + "/"+BuildConfig.APP_DB_ID+"Data";
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
	
	public static File getPathLogFile(Context context, final String filePrefix, boolean createNew){
		try {
			String fileName = getPathLogFolder(context) + "/" + filePrefix + ".txt";
			File gpxfile = new File(fileName);
			if (!gpxfile.exists() && createNew) {
				gpxfile.createNewFile();
			}
			return gpxfile;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public static void writePathLogToFile(Context context, String filePrefix, String response) {
		try {
			File gpxfile = getPathLogFile(context, filePrefix, true);
			if (gpxfile != null) {
				FileWriter writer = new FileWriter(gpxfile, true);
				writer.append("\n" + DateOperations.getCurrentTime() + " - " + response);
				writer.flush();
				writer.close();
			}
		} catch (Exception ignored) {
		}
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
	
	public static void deletePathLogFolder(Context context) {
		try {
			deleteFolder(getPathLogFolder(context));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
}

