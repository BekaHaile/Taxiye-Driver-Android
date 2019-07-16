package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import product.clicklabs.jugnoo.driver.BuildConfig;
import product.clicklabs.jugnoo.driver.Constants;


public class AuthKeySaver {
	
	private static final String NOT_FOUND = "";
	
	private static File getAuthFolder(Context context){
		String strFolder = context.getFilesDir() + "/Android/data/"+ BuildConfig.APPLICATION_ID;
		File folder = new File(strFolder);
		if(!folder.exists()){
			folder.mkdirs();
		}
		return folder;
	}
	
	private static File getAuthFile(Context context) throws IOException {
		String fileNamePrefix = BuildConfig.FLAVOR + "_auth" + "key2";
		try {
			fileNamePrefix = SHA256Convertor.getSHA256String(fileNamePrefix)+".jpg";
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String fileName = getAuthFolder(context) + "/" + fileNamePrefix;
		File gpxfile = new File(fileName);
		if (!gpxfile.exists()) {
			gpxfile.createNewFile();
		}
		return gpxfile;
	}
	
	
	public static void writeAuthToFile(Context context, final String authKey) {
		try {
			File gpxfile = getAuthFile(context);
			if (gpxfile != null) {
				FileWriter writer = new FileWriter(gpxfile, false);
				writer.write(authKey);
				writer.flush();
				writer.close();
			}
		} catch (Exception e1) {
            Log.w("e1", "="+e1);
		}
	}
	
	
	public static String readAuthFromFile(Context context) {
		String authKey = "";
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(getAuthFile(context)));
            while ((line = in.readLine()) != null) stringBuilder.append(line);
            authKey = stringBuilder.toString();
        }catch (Exception e) {
        	authKey = NOT_FOUND;
        } finally{
        	try {
				in.close();
			} catch (Exception e) {
                Log.w("e1", "=" + e);
			}
        }
        return authKey;
    }
	
}
