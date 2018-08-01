package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GeniePositonsSaver {

    public static File getGenieParamsFolder(Context context) {
        String strFolder = context.getFilesDir() + "/Android/data/.jugnoo_auth";
        File folder = new File(strFolder);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        return folder;
    }

    public static File getGenieParamsFile(Context context) throws IOException {
        String fileName = getGenieParamsFolder(context) + "/gp";
        File gpxfile = new File(fileName);
        if (!gpxfile.exists()) {
            gpxfile.createNewFile();
        }
        return gpxfile;
    }


    public static void writeGenieParams(Context context, int x, int y) {
        try {
            File gpxfile = getGenieParamsFile(context);
            if (gpxfile != null) {
                FileWriter writer = new FileWriter(gpxfile, false);
                writer.write(x+","+y);
                writer.flush();
                writer.close();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        }
    }


    public static int[] readGenieParams(Context context) {
        new ASSL(context, 1134, 720, true);
        int x = (int) (520 * ASSL.Xscale());
        int y = (int) (837 * ASSL.Yscale());

        String fileCont = x+","+y;
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        BufferedReader in = null;
        try {
            in = new BufferedReader(new FileReader(getGenieParamsFile(context)));
            while ((line = in.readLine()) != null) stringBuilder.append(line);
            fileCont = stringBuilder.toString();

        } catch (Exception e) {
            fileCont = x+","+y;
        } finally {
            try {
                in.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if("".equalsIgnoreCase(fileCont)){
            fileCont = x+","+y;
        }
        int[] params = new int[] {Integer.parseInt(fileCont.split(",")[0]), Integer.parseInt(fileCont.split(",")[1])};
        return params;
    }

}
