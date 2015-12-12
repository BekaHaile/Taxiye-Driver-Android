package product.clicklabs.jugnoo.utils;

import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class AuthKeySaver {
	
	public static final String NOT_FOUND = "not_found";
	
	public static File getAuthFolder(){
		String strFolder = Environment.getExternalStorageDirectory() + "/Android/data/.jugnoo_auth";
		File folder = new File(strFolder);
		if(!folder.exists()){
			folder.mkdirs();
		}
		return folder;
	}
	
	public static File getAuthFile() throws IOException{
		String fileName = getAuthFolder() + "/auth";
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
			e1.printStackTrace();
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
				e.printStackTrace();
			}
        }
        return authKey;
    }
	
}
