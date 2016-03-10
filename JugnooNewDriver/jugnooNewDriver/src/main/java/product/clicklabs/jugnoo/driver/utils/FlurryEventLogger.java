package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.location.Location;

import com.flurry.android.FlurryAgent;

import java.util.HashMap;
import java.util.Map;

import product.clicklabs.jugnoo.driver.Data;

public class FlurryEventLogger {

	public static void event(String eventName){
		try{ FlurryAgent.logEvent(eventName); } catch(Exception e){ e.printStackTrace(); }
	}

	public static void appStarted(String deviceToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("device_token", deviceToken);
			FlurryAgent.logEvent("App started", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void logResponseTime(Context context, long respTime, String event){
		try{
//			FlurryAgent.init(context, Data.FLURRY_KEY);
			FlurryAgent.onStartSession(context, Data.FLURRY_KEY);
			Map<String, String> params = new HashMap<String, String>();
			params.put("response_time", String.valueOf(respTime));
			params.put("event", event);
			FlurryAgent.logEvent("Api response log", params);
			FlurryAgent.onEndSession(context);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void logStartRing(Context context, int screenMode, int appVersion, String engId, String event){
		try{
//			FlurryAgent.init(context, Data.FLURRY_KEY);
			FlurryAgent.onStartSession(context, Data.FLURRY_KEY);
			Map<String, String> params = new HashMap<String, String>();
			params.put("screen_mode", String.valueOf(screenMode));
			params.put("app_version", String.valueOf(appVersion));
			params.put("engagement_id", String.valueOf(engId));
			FlurryAgent.logEvent(event, params);
			FlurryAgent.onEndSession(context);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void requestPushReceived(Context context, String engagementId, String startTime, String receivedTime){
		try{
//			FlurryAgent.init(context, Data.FLURRY_KEY);
			FlurryAgent.onStartSession(context, Data.FLURRY_KEY);
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("engagement_id", engagementId);
			articleParams.put("start_time", startTime);
			articleParams.put("received_time", receivedTime);
			FlurryAgent.logEvent("Request push received", articleParams);
			FlurryAgent.onEndSession(context);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public static void checkServerPressed(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Check server link pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void debugPressed(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Debug pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	

	

	
	public static void sharedViaWhatsapp(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Shared via Whatsapp", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sharedViaEmail(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Shared via Email", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sharedViaSMS(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Shared via SMS", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	


	
	
	
	
	
	
	

	
	public static void otpConfirmClick(String otp){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("otp_text", otp);
			FlurryAgent.logEvent("OTP entered", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void otpThroughCall(String phoneNo){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("phone_no", phoneNo);
			FlurryAgent.logEvent("OTP through call clicked", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	
	public static void emailSignupClicked(String email){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("email", email);
			FlurryAgent.logEvent("Signup clicked via Email", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}

	

	
	public static void fareDetailsOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Fare Details screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void connectionFailure(String error){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("error_description", error);
			FlurryAgent.logEvent("Connection Failure", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void locationLog(Location location){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", Data.userData.accessToken);
			articleParams.put("latitude", ""+location.getLatitude());
			articleParams.put("longitude", ""+location.getLongitude());
			FlurryAgent.logEvent("Location Log", articleParams);
		} catch(Exception e){
		}
	}
	
	public static void locationRestart(String cause){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", Data.userData.accessToken);
			articleParams.put("cause", cause);
			FlurryAgent.logEvent("Location Restart", articleParams);
		} catch(Exception e){
		}
	}



    public static void gpsStatus(Context context, String event){
        try{
			FlurryAgent.onStartSession(context, Data.FLURRY_KEY);
			FlurryAgent.logEvent(event);
			FlurryAgent.onEndSession(context);
        } catch(Exception e){
            e.printStackTrace();
        }
    }


}

