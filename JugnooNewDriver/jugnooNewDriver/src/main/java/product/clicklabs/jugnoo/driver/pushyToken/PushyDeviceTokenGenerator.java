package product.clicklabs.jugnoo.driver.pushyToken;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import me.pushy.sdk.Pushy;
import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.SplashLogin;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.IDeviceTokenReceiver;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;

/**
 * Created by aneeshbansal on 12/10/15.
 */
public class PushyDeviceTokenGenerator {

	String regId;

	private static final String PROPERTY_REG_ID = "pushy_registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";

	public PushyDeviceTokenGenerator(){
	}


	public void generateDeviceToken(Context context, IDeviceTokenReceiver deviceTokenReceiver){
		regId = getRegistrationId(context);
//		registerInBackground(context, deviceTokenReceiver);
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

			ExecutorService executor = Executors.newSingleThreadExecutor();
			try {
				executor.submit(new Runnable() {

				@Override
				public void run() {
					try {
                        long interval = Prefs.with(context)
                                .getLong(SPLabels.PUSHY_REFRESH_INTERVAL, Constants.PUSHY_REFRESH_INTERVAL_DEFAULT);
                        Pushy.setHeartbeatInterval((int) interval, context);
						regId = Pushy.register(context);
						setRegistrationId(context, regId);
					} catch(Exception e){
						e.printStackTrace();
					} finally{
						deviceTokenReceiver.deviceTokenReceived(regId); //try breakpoint here
					}
				}
			}).get(30, TimeUnit.SECONDS);
			} catch (Exception e) {
				e.printStackTrace();
			}
			executor.shutdown();
			deviceTokenReceiver.deviceTokenReceived(regId); //try breakpoint here

			Log.i(regId, "pushy");
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
		} catch (PackageManager.NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
}