package product.clicklabs.jugnoo.driver.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

import product.clicklabs.jugnoo.driver.BuildConfig;


public class AuthKeySaver {
	
	private static final String NOT_FOUND = "";
	
	private static File getAuthFolder(){
		String strFolder = Environment.getExternalStorageDirectory() + "/Android/data/"+ BuildConfig.APPLICATION_ID;
		File folder = new File(strFolder);
		if(!folder.exists()){
			folder.mkdirs();
		}
		return folder;
	}
	
	private static File getAuthFile() throws IOException {
		String fileNamePrefix = android.os.Build.SERIAL + "key2";
		try {
			fileNamePrefix = SHA256Convertor.getSHA256String(fileNamePrefix)+".jpg";
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		String fileName = getAuthFolder() + "/" + fileNamePrefix;
		File gpxfile = new File(fileName);
		if (!gpxfile.exists()) {
			gpxfile.createNewFile();
		}
		return gpxfile;
	}
	
	
	public static void writeAuthToFile(final String authKey) {
		try {
			File gpxfile = getAuthFile();
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
	
	
	public static String readAuthFromFile() {
		String authKey = "";
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(getAuthFile()));
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
