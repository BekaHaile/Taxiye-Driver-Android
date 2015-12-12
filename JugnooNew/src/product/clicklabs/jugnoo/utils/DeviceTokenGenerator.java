package product.clicklabs.jugnoo.utils;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.SplashLogin;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class DeviceTokenGenerator {

	GoogleCloudMessaging gcm;
	String regId;
	
	private static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
	
	public DeviceTokenGenerator(Context context){
		gcm = GoogleCloudMessaging.getInstance(context);
	}
	
	
	public void generateDeviceToken(Context context, IDeviceTokenReceiver deviceTokenReceiver){
		regId = getRegistrationId(context);
		if(regId.isEmpty()){
			registerInBackground(context, deviceTokenReceiver);
		}
		else{
			deviceTokenReceiver.deviceTokenReceived(regId);
		}
	}
	
	
	private String getRegistrationId(Context context) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    String registrationId = prefs.getString(PROPERTY_REG_ID, "");
	    if (registrationId.isEmpty()) {
	        Log.i("dfs", "Registration not found.");
	        return "";
	    }
	    // Check if app was updated; if so, it must clear the registration ID
	    // since the existing regID is not guaranteed to work with the new
	    // app version.
	    int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
	    int currentVersion = getAppVersion(context);
	    if (registeredVersion != currentVersion) {
	        Log.i("sdfs", "App version changed.");
	        return "";
	    }
	    return registrationId;
	}
	
	private void registerInBackground(final Context context, final IDeviceTokenReceiver deviceTokenReceiver) {
		if (AppStatus.getInstance(context).isOnline(context)) {
			if (gcm == null) {
                gcm = GoogleCloudMessaging.getInstance(context);
            }
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						regId = gcm.register(Data.GOOGLE_PROJECT_ID);
						setRegistrationId(context, regId);
					} catch(Exception e){
						e.printStackTrace();
					} finally{
						deviceTokenReceiver.deviceTokenReceived(regId);
					}
				}
			}).start();
		}
		else{
			deviceTokenReceiver.deviceTokenReceived(regId);
		}
	}
	
	
	private void setRegistrationId(Context context, String regId) {
	    final SharedPreferences prefs = getGCMPreferences(context);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putString(PROPERTY_REG_ID, regId);
	    editor.putInt(PROPERTY_APP_VERSION, getAppVersion(context));
	    editor.commit();
	}
	
	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
	    // This sample app persists the registration ID in shared preferences, but
	    // how you store the regID in your app is up to you.
	    return context.getSharedPreferences(SplashLogin.class.getSimpleName(), Context.MODE_PRIVATE);
	}
	
	private static int getAppVersion(Context context) {
	    try {
	        PackageInfo packageInfo = context.getPackageManager()
	                .getPackageInfo(context.getPackageName(), 0);
	        return packageInfo.versionCode;
	    } catch (NameNotFoundException e) {
	        // should never happen
	        throw new RuntimeException("Could not get package name: " + e);
	    }
	}
}
