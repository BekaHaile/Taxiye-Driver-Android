package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.telephony.TelephonyManager;

public class UniqueIMEIID {

	public static String getUniqueIMEIId(Context context){
		try {
			TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			String imei = telephonyManager.getDeviceId();
			if(imei != null && !imei.isEmpty()){
				return imei;
			}
            else{
                return android.os.Build.SERIAL;
            }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "not_found";
	}
	
}
