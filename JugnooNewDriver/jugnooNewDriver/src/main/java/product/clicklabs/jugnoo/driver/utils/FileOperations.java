package product.clicklabs.jugnoo.driver.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class FileOperations {

	public static void writeToFile(File file, String response) {
		try {
			if (file != null) {
				FileWriter writer = new FileWriter(file, false);
				writer.write(response);
				writer.flush();
				writer.close();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public static void appendToFile(File file, String response) {
		try {
			if (file != null) {
				FileWriter writer = new FileWriter(file, true);
				writer.append(response);
				writer.flush();
				writer.close();
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public static String readFromFile(File file) {
		String content = "";
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) stringBuilder.append(line);
            content = stringBuilder.toString();
        }catch (Exception e) {
        	e.printStackTrace();
        } finally{
        	try {
				in.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        return content;
    }
	
}
