package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.location.Location;

import java.util.HashMap;
import java.util.Map;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.MyApplication;

public class FlurryEventLogger {

	public static void event(String eventName){
		try {MyApplication.getInstance().trackEvent("Driver", eventName, eventName);} catch (Exception e) {}
	}

	public static void appStarted(String deviceToken){
		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("device_token", deviceToken);
		try {MyApplication.getInstance().trackEvent("Driver", "App started", "App started", articleParams);} catch (Exception e) {}
	}

	public static void logResponseTime(Context context, long respTime, String event){
		Map<String, String> params = new HashMap<String, String>();
		params.put("response_time", String.valueOf(respTime));
		params.put("event", event);

		try {MyApplication.getInstance().trackEvent("Driver", "Api response log", "Api response log", params);} catch (Exception e) {}
	}

	public static void logStartRing(Context context, int screenMode, int appVersion, String engId, String event){

//			FlurryAgent.init(context, Data.FLURRY_KEY);
		Map<String, String> params = new HashMap<String, String>();
		params.put("screen_mode", String.valueOf(screenMode));
		params.put("app_version", String.valueOf(appVersion));
		params.put("engagement_id", String.valueOf(engId));
		try {MyApplication.getInstance().trackEvent("Driver", event, event, params);} catch (Exception e) {}

	}
	
	public static void requestPushReceived(Context context, String engagementId, String startTime, String receivedTime){
		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("engagement_id", engagementId);
		articleParams.put("start_time", startTime);
		articleParams.put("received_time", receivedTime);
		try {MyApplication.getInstance().trackEvent("Driver", "Request push received", "Request push received", articleParams);} catch (Exception e) {}

	}

	
	public static void checkServerPressed(String accessToken){

		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("access_token", accessToken);
		try {MyApplication.getInstance().trackEvent("Driver", "Check server link pressed", "Check server link pressed", articleParams);} catch (Exception e) {}

	}
	
	
	public static void debugPressed(String accessToken){
		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("access_token", accessToken);
		try {MyApplication.getInstance().trackEvent("Driver", "Debug pressed", "Debug pressed", articleParams);} catch (Exception e) {}

	}


	
	
	
	
	
	
	

	
	public static void otpConfirmClick(String otp){
		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("otp_text", otp);
		try {MyApplication.getInstance().trackEvent("Driver", "OTP entered", "OTP entered", articleParams);} catch (Exception e) {}

	}
	
	public static void otpThroughCall(String phoneNo){
		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("phone_no", phoneNo);
		try {MyApplication.getInstance().trackEvent("Driver", "OTP through call clicked", "OTP through call clicked", articleParams);} catch (Exception e) {}

	}

	
	public static void emailSignupClicked(String email){

		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("email", email);
		try {MyApplication.getInstance().trackEvent("Driver", "Signup clicked via Email", "Signup clicked via Email", articleParams);} catch (Exception e) {}

	}

	

	
	public static void fareDetailsOpened(String accessToken){

		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("access_token", accessToken);
		try {MyApplication.getInstance().trackEvent("Driver", "Fare Details screen opened", "Fare Details screen opened", articleParams);} catch (Exception e) {}

	}
	
	
	public static void connectionFailure(String error){
		Map<String, String> articleParams = new HashMap<String, String>();
		articleParams.put("error_description", error);
		try {MyApplication.getInstance().trackEvent("Driver", "Connection Failure", "Connection Failure", articleParams);} catch (Exception e) {}

	}
	
	
	public static void locationLog(Location location){
		try {
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", Data.userData.accessToken);
			articleParams.put("latitude", ""+location.getLatitude());
			articleParams.put("longitude", ""+location.getLongitude());
			try {MyApplication.getInstance().trackEvent("Driver", "Location Log", "Location Log", articleParams);} catch (Exception e) {}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	public static void locationRestart(String cause){
		try {
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", Data.userData.accessToken);
			articleParams.put("cause", cause);
			try {MyApplication.getInstance().trackEvent("Driver", "Location Restart", "Location Restart", articleParams);} catch (Exception e) {}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}



    public static void gpsStatus(Context context, String event){
        try {MyApplication.getInstance().trackEvent("Driver", event, event);} catch (Exception e) {}

	}


}

