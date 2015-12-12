package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.provider.Settings.Secure;

public class AndroidID {

	public static String getAndroidId(Context context){
		try {
			String androidId = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
			Log.e("androidId", "="+androidId);
			if(androidId != null && !androidId.isEmpty()){
				return androidId;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "not_found";
	}
	
}
