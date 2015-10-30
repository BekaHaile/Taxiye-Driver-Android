package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;

/**
 * Created by aneeshbansal on 30/10/15.
 */
public class AGPSRefresh {
	public static long DAY_MILLIS = 24*60*60*1000;

	public static void refreshGpsData(Context context){
		try {
        /* Cold start */
			if(!Prefs.with(context).contains(SPLabels.GPS_REFRESH_TIME)){
				Prefs.with(context).save(SPLabels.GPS_REFRESH_TIME, System.currentTimeMillis()- 2*DAY_MILLIS);
			}
			if(System.currentTimeMillis()
					- (Prefs.with(context).getLong(SPLabels.GPS_REFRESH_TIME, System.currentTimeMillis()))
					> DAY_MILLIS) {
				Bundle bundle = new Bundle();
				bundle.putBoolean("all", true);
				LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
				locationManager.sendExtraCommand(LocationManager.GPS_PROVIDER, "delete_aiding_data", null);
				locationManager.sendExtraCommand("gps", "force_xtra_injection", bundle);
				locationManager.sendExtraCommand("gps", "force_time_injection", bundle);
				Prefs.with(context).save(SPLabels.GPS_REFRESH_TIME, System.currentTimeMillis());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
