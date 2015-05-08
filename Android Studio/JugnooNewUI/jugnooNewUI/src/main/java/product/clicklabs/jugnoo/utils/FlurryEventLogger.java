package product.clicklabs.jugnoo.utils;

import android.content.Context;
import android.location.Location;

import com.flurry.android.FlurryAgent;
import com.google.android.gms.maps.model.LatLng;

import java.util.HashMap;
import java.util.Map;

import product.clicklabs.jugnoo.Data;
import product.clicklabs.jugnoo.config.Config;

public class FlurryEventLogger {

	public static void appStarted(String deviceToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("device_token", deviceToken);
			FlurryAgent.logEvent("App started", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void requestPushReceived(Context context, String engagementId, String startTime, String receivedTime){
		try{
			FlurryAgent.onStartSession(context, Config.getFlurryKey());
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
	
	public static void requestRidePressed(String accessToken, LatLng latLng){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("latitude", ""+latLng.latitude);
			articleParams.put("longitude", "" + latLng.longitude);
			FlurryAgent.logEvent("Call an auto pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void cancelRequestPressed(String accessToken, String sessionId){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("session_id", sessionId);
			FlurryAgent.logEvent("Cancel request pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void callDriverPressed(String accessToken, String userId, String phoneNo){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("driver_id", userId);
			articleParams.put("phone_no", phoneNo);
			FlurryAgent.logEvent("Call driver pressed", articleParams);
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
	
	public static void switchedToDriverMode(String accessToken, int flag){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("driver_mode", ""+flag);
			FlurryAgent.logEvent("Switch to driver mode pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void shareScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Share screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void shareScreenOpenedThroughCoupons(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Share screen opened through coupons", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void couponsScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Coupon screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void walletScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Wallet screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void helpScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Help screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void rideScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Ride screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void logoutPressed(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Logout pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void logoutPressedBetweenRide(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Logout pressed between ride", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void reviewSubmitted(String accessToken, String engagementId){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("engagement_id", engagementId);
			FlurryAgent.logEvent("Review submitted", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void promoCodeTried(String accessToken, String promoCode){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("promo_code", promoCode);
			FlurryAgent.logEvent("Promo code tried", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void promoCodeApplied(String accessToken, String promoCode, String message){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("promo_code", promoCode);
			articleParams.put("message", message);
			FlurryAgent.logEvent("Promo code applied", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void couponInfoOpened(String accessToken, int couponType){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("coupon_type", ""+couponType);
			FlurryAgent.logEvent("Coupon info opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void sharedViaFacebook(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Shared via Facebook", articleParams);
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
	
	public static void mailToSupportPressed(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Mail to support pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void callToSupportPressed(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Call to support pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void particularHelpOpened(String helpName, String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent(helpName + " opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void forgotPasswordClicked(String email){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("email", email);
			FlurryAgent.logEvent("Forgot password clicked", articleParams);
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
	
	public static void facebookSignupClicked(String email){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("email", email);
			FlurryAgent.logEvent("Signup clicked via Facebook", articleParams);
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
	
	public static void referralCodeAtFBSignup(String email, String referralCode){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("email", email);
			articleParams.put("referral_code", referralCode);
			FlurryAgent.logEvent("Referral code at Facebook Signup", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void referralCodeAtEmailSignup(String email, String referralCode){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("email", email);
			articleParams.put("referral_code", referralCode);
			FlurryAgent.logEvent("Referral code at Email Signup", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void registerViaFBClicked(String fbId){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("fb_id", fbId);
			FlurryAgent.logEvent("Facebook button pressed from Register screen", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public static void facebookLoginClicked(String fbId){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("fb_id", fbId);
			FlurryAgent.logEvent("Facebook button pressed from Login screen", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void emailLoginClicked(String email){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("email", email);
			FlurryAgent.logEvent("Email login button pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	public static void logRideData(String accessToken, String engagementId, LatLng latLng, double totalDistance){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("engagement_id", engagementId);
			articleParams.put("current_latitude", ""+latLng.latitude);
			articleParams.put("current_longitude", ""+latLng.longitude);
			articleParams.put("total_distance", ""+totalDistance);
			FlurryAgent.logEvent("Ride Data", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void logRideDataGAPI(String accessToken, String engagementId, LatLng latLng1, LatLng latLng2, double totalDistance){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			articleParams.put("engagement_id", engagementId);
			articleParams.put("latitude_1", ""+latLng1.latitude);
			articleParams.put("longitude_1", ""+latLng1.longitude);
			articleParams.put("latitude_2", ""+latLng2.latitude);
			articleParams.put("longitude_2", ""+latLng2.longitude);
			articleParams.put("total_distance", ""+totalDistance);
			FlurryAgent.logEvent("Ride Data Google API", articleParams);
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
	
	public static void christmasScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Christmas screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void christmasNewScreenOpened(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Christmas new screen opened", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void christmasScreenContinuePressed(String accessToken){
		try{
			Map<String, String> articleParams = new HashMap<String, String>();
			articleParams.put("access_token", accessToken);
			FlurryAgent.logEvent("Christmas screen Continue pressed", articleParams);
		} catch(Exception e){
			e.printStackTrace();
		}
	}



}
