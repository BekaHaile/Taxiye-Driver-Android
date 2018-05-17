package com.fugu.utils;

import android.content.Context;
import android.os.Build;
import android.telephony.TelephonyManager;

public class UniqueIMEIID {

	public static String getUniqueIMEIId(Context activity){
		try {
				TelephonyManager telephonyManager = (TelephonyManager) activity.getSystemService(Context.TELEPHONY_SERVICE);
				String imei = telephonyManager.getDeviceId();
				if (imei != null && !imei.isEmpty()) {
					return imei;
				} else {
					return Build.SERIAL;
				}
		} catch (Exception e) {
			e.printStackTrace();
		}
		//String serial = Build.SERIAL;
		return Build.SERIAL;
	}


}
