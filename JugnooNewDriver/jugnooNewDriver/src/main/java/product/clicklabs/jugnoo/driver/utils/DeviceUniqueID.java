package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

import product.clicklabs.jugnoo.driver.BuildConfig;

public class DeviceUniqueID {

	public static String getUniqueId(Context context){
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyManager.getDeviceId();
			Log.e("imei", "="+imei);
            if(imei != null && !imei.isEmpty()){
                return imei + (BuildConfig.DEBUG ? "mn" : "");
            }
            else{
                return android.os.Build.SERIAL + (BuildConfig.DEBUG ? "mnrfr" : "");
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "not_found";
	}
	
}
