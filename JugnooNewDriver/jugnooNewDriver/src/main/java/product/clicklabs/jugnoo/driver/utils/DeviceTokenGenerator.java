package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import product.clicklabs.jugnoo.driver.Data;

public class DeviceTokenGenerator {

	String regId;

	public DeviceTokenGenerator(){
		regId = "";
	}

	public void generateDeviceToken(final Context context, final IDeviceTokenReceiver deviceTokenReceiver){
		if (AppStatus.getInstance(context).isOnline(context)) {
			new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						InstanceID instanceID = InstanceID.getInstance(context);
						regId = instanceID.getToken(Data.GOOGLE_PROJECT_ID, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
					} catch(Exception e){
						e.printStackTrace();
					} finally{
						if(regId == null){
							regId = "not_found";
						} else if(regId.equalsIgnoreCase("")){
							regId = "not_found";
						}
						deviceTokenReceiver.deviceTokenReceived(regId);
					}
				}
			}).start();
		}
		else{
			deviceTokenReceiver.deviceTokenReceived(regId);
		}
	}

}
