package product.clicklabs.jugnoo;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.config.Config;
import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.CancelOption;
import product.clicklabs.jugnoo.datastructure.CancelOptionsList;
import product.clicklabs.jugnoo.datastructure.CouponInfo;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.EndRideData;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.FareStructure;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.PreviousAccountInfo;
import product.clicklabs.jugnoo.datastructure.PromoCoupon;
import product.clicklabs.jugnoo.datastructure.PromotionInfo;
import product.clicklabs.jugnoo.datastructure.ReferralMessages;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.SHA256Convertor;
import product.clicklabs.jugnoo.utils.Utils;

public class JSONParser {

	public JSONParser(){
		
	}
	
	
	public void parseFareDetails(JSONObject userData){
		try{
			JSONArray fareDetailsArr = userData.getJSONArray("fare_details");
			JSONObject fareDetails0 = fareDetailsArr.getJSONObject(0);
			double farePerMin = 0;
			double freeMinutes = 0;
			if(fareDetails0.has("fare_per_min")){
				farePerMin = fareDetails0.getDouble("fare_per_min");
			}
			if(fareDetails0.has("fare_threshold_time")){
				freeMinutes = fareDetails0.getDouble("fare_threshold_time");
			}
			Data.fareStructure = new FareStructure(fareDetails0.getDouble("fare_fixed"), 
					fareDetails0.getDouble("fare_threshold_distance"), 
					fareDetails0.getDouble("fare_per_km"), 
					farePerMin, freeMinutes);
		} catch(Exception e){
			e.printStackTrace();
			Data.fareStructure = new FareStructure(25, 2, 6, 1, 6);
		}
	}
	
	
	
	
	public UserData parseUserData(Context context, JSONObject userData) throws Exception{
		
//		"login": {
//        "flag": 407,
//        "user_name": "Shankar Bhagwatia",
//        "user_image": "http://graph.facebook.com/717496164959213/picture?width=160&height=160",
//        "phone_no": "+919780298413",
//        "user_email": "shankarsinghisking91@gmail.com",
//        "email_verification_status": 0,
//        "referral_code": "SHANKAR23",
//        "auth_key": "06c898728e0c84d903a93d647ba67858594d2080390860711ed0fa9b37828db9",
//        "jugnoo_balance": 2,
//        "current_user_status": 2,
//        "is_available": 1,
//        "can_change_location": 1,
//        "can_schedule": 1,
//        "scheduling_limit": 60,
//        "gcm_intent": 1,
//        "christmas_icon_enable": 0,
//        "fare_details": [
//            {
//                "fare_fixed": 25,
//                "fare_per_km": 6,
//                "fare_threshold_distance": 2,
//                "fare_per_min": 1,
//                "fare_threshold_time": 0
//            }
//        ],
//        "exceptional_driver": 0,
//        "update_popup": 0,
//        "access_token": "f3e2632ae5d84b70e2ebae4f448bfb273a24a03595d5cafd029e4491061e27c6"
//    }
		
		
		int canSchedule = 0, canChangeLocation = 0, schedulingLimitMinutes = 0, isAvailable = 1, exceptionalDriver = 0, gcmIntent = 1, 
				christmasIconEnable = 0, nukkadEnable = 0, enableJugnooMeals = 1, freeRideIconDisable = 1;
		int emailVerificationStatus = 1;
		int numCouponsAvailable = 0;
		String userEmail = "", phoneNo = "", nukkadIcon = "", jugnooMealsPackageName = "com.cdk23.nlk", jugnooFbBanner = "http://bit.ly/1OCgcke";
		double jugnooBalance = 0, fareFactor = 1.0;
		
		if(userData.has("can_schedule")){
			canSchedule = userData.getInt("can_schedule");
		}
		
		if(userData.has("can_change_location")){
			canChangeLocation = userData.getInt("can_change_location");
		}
		
		if(userData.has("scheduling_limit")){
			schedulingLimitMinutes = userData.getInt("scheduling_limit");
		}
		
		if(userData.has("is_available")){
			isAvailable = userData.getInt("is_available");
		}
		
		if(userData.has("exceptional_driver")){
			exceptionalDriver = userData.getInt("exceptional_driver");
		}
		
		try{
			if(userData.has("gcm_intent")){
				gcmIntent = userData.getInt("gcm_intent");
				Database2.getInstance(context).updateDriverGcmIntent(gcmIntent);
				Database2.getInstance(context).close();
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			if(userData.has("christmas_icon_enable")){
				christmasIconEnable = userData.getInt("christmas_icon_enable");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		if(userData.has("phone_no")){
			phoneNo = userData.getString("phone_no");
		}
		
		try{
			if(userData.has("nukkad_enable")){
				nukkadEnable = userData.getInt("nukkad_enable");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			if(userData.has("nukkad_icon")){
				nukkadIcon = userData.getString("nukkad_icon");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			if(userData.has("enable_jugnoo_meals")){
				enableJugnooMeals = userData.getInt("enable_jugnoo_meals");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		try{
			if(userData.has("jugnoo_meals_package_name")){
				jugnooMealsPackageName = userData.getString("jugnoo_meals_package_name");
			}
		} catch(Exception e){
			e.printStackTrace();
		}
		
		if(userData.has("free_ride_icon_disable")){
			freeRideIconDisable = userData.getInt("free_ride_icon_disable");
		}
		
		if(userData.has("jugnoo_balance")){
			jugnooBalance = userData.getDouble("jugnoo_balance");
		}
		
		if(userData.has("user_email")){
			userEmail = userData.getString("user_email");
		}
		
		if(userData.has("email_verification_status")){
			emailVerificationStatus = userData.getInt("email_verification_status");
		}
		
		if(userData.has("fare_factor")){
			fareFactor = userData.getDouble("fare_factor");
		}
		
		if(userData.has("jugnoo_fb_banner")){
			jugnooFbBanner = userData.getString("jugnoo_fb_banner");
		}
		
		if(userData.has("num_coupons_available")){
			numCouponsAvailable = userData.getInt("num_coupons_available");
		}
		
		String authKey = userData.getString("auth_key");
		AccessTokenGenerator.saveAuthKey(context, authKey);
		
		String authSecret = authKey + Config.getClientSharedSecret();
		String accessToken = SHA256Convertor.getSHA256String(authSecret);
		
		return new UserData(accessToken, authKey, userData.getString("user_name"), userEmail, emailVerificationStatus, 
				userData.getString("user_image"), userData.getString("referral_code"), phoneNo, 
				canSchedule, canChangeLocation, schedulingLimitMinutes, isAvailable, exceptionalDriver, gcmIntent, 
				christmasIconEnable, nukkadEnable, nukkadIcon, enableJugnooMeals, jugnooMealsPackageName, freeRideIconDisable, jugnooBalance, fareFactor,
				jugnooFbBanner, numCouponsAvailable);
	}
	

	
	
	
	
	
	public String parseAccessTokenLoginData(Context context, String response) throws Exception{
		
		JSONObject jObj = new JSONObject(response);
		
		//Fetching login data
		JSONObject jLoginObject = jObj.getJSONObject("login");
		Log.i("jLoginObject", "="+jLoginObject);
		
		Data.userData = parseUserData(context, jLoginObject);
		
		parseFareDetails(jLoginObject);
		
		//current_user_status = 1 driver or 2 user
		int currentUserStatus = jLoginObject.getInt("current_user_status");
		if(currentUserStatus == 2){
			//Fetching drivers info
			parseDriversToShow(jObj, "drivers");
			Database2.getInstance(context).updateUserMode(Database2.UM_PASSENGER);
		}
		else if(currentUserStatus == 1){
			Database2.getInstance(context).updateUserMode(Database2.UM_DRIVER);
		}
		
		parsePortNumber(context, jLoginObject);
		
		
		//Fetching user current status
		JSONObject jUserStatusObject = jObj.getJSONObject("status");
		String resp = parseCurrentUserStatus(context, currentUserStatus, jUserStatusObject);
			
		parseCancellationReasons(jObj);
		
		Data.referralMessages = parseReferralMessages(jObj);
		
		return resp;
	}
	
	
	public ReferralMessages parseReferralMessages(JSONObject jObj){
		String referralMessage = "Share your referral code "+Data.userData.referralCode+
				" with your friends and they will get a FREE ride because of your referral and once they have used Jugnoo, you will earn a FREE ride (up to Rs. 100) as well.";
		String referralSharingMessage = "Hey, \nUse Jugnoo app to call an auto at your doorsteps. It is cheap, convenient and zero haggling." +
				" Use this referral code: "+Data.userData.referralCode+" to get FREE ride up to Rs. 100." +
						"\nDownload it from here: http://smarturl.it/jugnoo";
		String fbShareCaption = "Use " + Data.userData.referralCode + " as code & get a FREE ride";
		String fbShareDescription = "Try Jugnoo app to call an auto at your doorsteps with just a tap.";
		
		try {
			if(jObj.has("referral_message")){
				referralMessage = jObj.getString("referral_message");
			}
			if(jObj.has("referral_sharing_message")){
				referralSharingMessage = jObj.getString("referral_sharing_message");
			}
			if(jObj.has("fb_share_caption")){
				fbShareCaption = jObj.getString("fb_share_caption");
			}
			if(jObj.has("fb_share_description")){
				fbShareDescription = jObj.getString("fb_share_description");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		ReferralMessages referralMessages = new ReferralMessages(referralMessage, referralSharingMessage, fbShareCaption, fbShareDescription);
		
		return referralMessages;
	}
	
	
	public void parsePortNumber(Context context, JSONObject jLoginObject){
		try {
			if(jLoginObject.has("port_number")){
				String port = jLoginObject.getString("port_number");
				updatePortNumber(context, port);
				SplashNewActivity.initializeServerURL(context);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void updatePortNumber(Context context, String port){
		SharedPreferences preferences = context.getSharedPreferences(Data.SETTINGS_SHARED_PREF_NAME, 0);
		String link = preferences.getString(Data.SP_SERVER_LINK, Config.getDefaultServerUrl());
		
		if(link.equalsIgnoreCase(Config.getTrialServerUrl())){
			Database2.getInstance(context).updateSalesPortNumber(port);
		}
		else if(link.equalsIgnoreCase(Config.getDevServerUrl())){
			Database2.getInstance(context).updateDevPortNumber(port);
		}
		else{
			Database2.getInstance(context).updateLivePortNumber(port);
		}
	}
	
	//TODO
	public void parseLastRideData(JSONObject jObj){
		
//		  "last_ride": {
//        "engagement_id": 6973,
//        "session_id": 3822,
//        "pickup_address": "Unnamed",
//        "drop_address": "Unnamed",
//        "fare": 26,
//        "discount": 0,
//        "paid_using_wallet": 0,
//        "to_pay": 26,
//        "distance": 0,
//        "ride_time": 1,
//        "rate_app": 0,
//        "drop_time": "05:14 AM",
//        "pickup_time": "05:14 AM",
//        "coupon": null,
//        "promotion": null,
//        "driver_info": {
//            "id": 231,
//            "name": "Driver 3",
//            "user_image": "http://tablabar.s3.amazonaws.com/brand_images/user.png"
//        }
//    }
		
		try {
			JSONObject jLastRideData = jObj.getJSONObject("last_ride");
			Data.cSessionId = "";
			Data.cEngagementId = jLastRideData.getString("engagement_id");
			
			JSONObject jDriverInfo = jLastRideData.getJSONObject("driver_info");
			Data.cDriverId = jDriverInfo.getString("id");
			
			Data.pickupLatLng = new LatLng(0, 0);
			
			Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, jDriverInfo.getString("name"), jDriverInfo.getString("user_image"), 
					jDriverInfo.getString("driver_car_image"), jDriverInfo.getString("driver_car_no"));
			
			try{
				if(jLastRideData.has("rate_app")){
					Data.customerRateAppFlag = jLastRideData.getInt("rate_app");
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
			
			double baseFare = Data.fareStructure.fixedFare;
			if(jLastRideData.has("base_fare")){
				baseFare = jLastRideData.getDouble("base_fare");
			}
			
			String banner = "";
			if(jLastRideData.has("banner")){
				banner = jLastRideData.getString("banner");
			}
			
			Data.endRideData = new EndRideData(jLastRideData.getString("engagement_id"), 
					jLastRideData.getString("pickup_address"), 
					jLastRideData.getString("drop_address"), 
					jLastRideData.getString("pickup_time"), 
					jLastRideData.getString("drop_time"), 
					banner, 
					jLastRideData.getDouble("fare"), 
					jLastRideData.getDouble("discount"), 
					jLastRideData.getDouble("paid_using_wallet"), 
					jLastRideData.getDouble("to_pay"), 
					jLastRideData.getDouble("distance"), 
					jLastRideData.getDouble("ride_time"),
					baseFare);
			
			HomeActivity.passengerScreenMode = PassengerScreenMode.P_RIDE_END;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}
	
	
	public String getUserStatus(Context context, String accessToken, int currentUserStatus){
		String returnResponse = "";
		try{
			ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
			HttpRequester simpleJSONParser = new HttpRequester();
			String result = simpleJSONParser.getJSONFromUrlParams(Config.getServerUrl() + "/get_current_user_status", nameValuePairs);
//			Log.e("result of = user_status", "="+result);
			if(result.contains(HttpRequester.SERVER_TIMEOUT)){
				returnResponse = HttpRequester.SERVER_TIMEOUT;
				return returnResponse;
			}
			else{
				JSONObject jObject1 = new JSONObject(result);
				returnResponse = parseCurrentUserStatus(context, currentUserStatus, jObject1);
				return returnResponse;
			}
		} catch(Exception e){
			e.printStackTrace();
			returnResponse = HttpRequester.SERVER_TIMEOUT;
			return returnResponse;
		}
	}
	
	
	public void parseDriversToShow(JSONObject jObject, String jsonArrayKey){
		try{
			JSONArray data = jObject.getJSONArray(jsonArrayKey);
			Data.driverInfos.clear();
			
			for(int i=0; i<data.length(); i++){
				JSONObject dataI = data.getJSONObject(i);
				String userId = dataI.getString("user_id");
				double latitude = dataI.getDouble("latitude");
				double longitude = dataI.getDouble("longitude");
				String userName = dataI.getString("user_name");
				String phoneNo = dataI.getString("phone_no");
				String rating = dataI.getString("rating");
				String userImage = "";
				String driverCarImage = "";
				String carNumber = "";
				Data.driverInfos.add(new DriverInfo(userId, latitude, longitude, userName, userImage, driverCarImage, phoneNo, rating, carNumber, 0));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public String parseCurrentUserStatus(Context context, int currentUserStatus, JSONObject jObject1){
//		Log.e("parseCurrentUserStatus jObject1", "="+jObject1);
		String returnResponse = "";
		
		if(currentUserStatus == 2){
			
			String screenMode = "";

			int engagementStatus = -1;
			String engagementId = "", sessionId = "",  userId = "", latitude = "", longitude = "", 
					driverName = "", driverImage = "", driverCarImage = "", driverPhone = "", driverRating = "", driverCarNumber = "", 
					pickupLatitude = "", pickupLongitude = "";
			int freeRide = 0;
			String promoName = "", eta = "";
			double fareFactor = 1.0;
			
			
			HomeActivity.userMode = UserMode.PASSENGER;
			
			try{
							
							if(jObject1.has("error")){
								returnResponse = HttpRequester.SERVER_TIMEOUT;
								return returnResponse;
							}
							else{
							
							
//							response = {
//									"log": "Assigning driver", 
//									"flag": constants.responseFlags.ASSIGNING_DRIVERS,
//									"session_id": 2020
//							};

							
							
//							"flag": constants.responseFlags.ENGAGEMENT_DATA, 
//							"last_engagement_info":[
//							{
//								�driver_id�, 
//								�pickup_latitude�, 
//								�pickup_longitude�, 
//								�engagement_id�, 
//								�status�, 
//								�session_id�,
//								�user_name�, 
//								�phone_no�, 
//								�user_image�, 
//								�driver_car_image�, 
//								�current_location_latitude�, 
//								�current_location_longitude�, 
//								�rating�
//								}

							
								int flag = jObject1.getInt("flag");
								
								if(ApiResponseFlags.ASSIGNING_DRIVERS.getOrdinal() == flag){
									sessionId = jObject1.getString("session_id");
									double assigningLatitude = 0, assigningLongitude = 0;
									if(jObject1.has("latitude") && jObject1.has("longitude")){
										assigningLatitude = jObject1.getDouble("latitude");
										assigningLongitude = jObject1.getDouble("longitude");
										Log.e("assigningLatitude,assigningLongitude ====@@@", ""+assigningLatitude+","+assigningLongitude);
									}
									Data.pickupLatLng = new LatLng(assigningLatitude, assigningLongitude);
									
									engagementStatus = EngagementStatus.REQUESTED.getOrdinal();
								}
								else if(ApiResponseFlags.ENGAGEMENT_DATA.getOrdinal() == flag){
									JSONArray lastEngInfoArr = jObject1.getJSONArray("last_engagement_info");
									JSONObject jObject = lastEngInfoArr.getJSONObject(0);
	
									engagementStatus = jObject.getInt("status");
									
									if((EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus) || (EngagementStatus.STARTED.getOrdinal() == engagementStatus)){
										engagementId = jObject.getString("engagement_id");
										sessionId = jObject.getString("session_id");
										userId = jObject.getString("driver_id");
										latitude = jObject.getString("current_location_latitude");
										longitude = jObject.getString("current_location_longitude");
										driverName = jObject.getString("user_name");
										driverImage = jObject.getString("user_image");
										driverCarImage = jObject.getString("driver_car_image");
										driverPhone = jObject.getString("phone_no");
										driverRating = jObject.getString("rating");
										pickupLatitude = jObject.getString("pickup_latitude");
										pickupLongitude = jObject.getString("pickup_longitude");
										if(jObject.has("driver_car_no")){
											driverCarNumber = jObject.getString("driver_car_no");
										}
										if(jObject.has("free_ride")){
											freeRide = jObject.getInt("free_ride");
										}
										
										promoName = getPromoName(jObject);
										
										if(jObject.has("eta")){
											eta = jObject.getString("eta");
										}
										
										if(jObject.has("fare_factor")){
											fareFactor = jObject.getDouble("fare_factor");
										}
										
									}
								}
								else if(ApiResponseFlags.LAST_RIDE.getOrdinal() == flag){
									parseLastRideData(jObject1);
									return returnResponse;
								}
							
							}
			} catch(Exception e){
				e.printStackTrace();
				engagementStatus = -1;
				returnResponse = HttpRequester.SERVER_TIMEOUT;
				return returnResponse;
			}
			
			if(EngagementStatus.REQUESTED.getOrdinal() == engagementStatus){
				screenMode = Data.P_ASSIGNING;
			}
			else if(EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus){
				screenMode = Data.P_REQUEST_FINAL;
			}
			else if(EngagementStatus.STARTED.getOrdinal() == engagementStatus){
				screenMode = Data.P_IN_RIDE;
			}
			else{
				screenMode = "";
			}
			
			
			if("".equalsIgnoreCase(screenMode)){
				HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
				clearSPData(context);
			}
			else if(Data.P_ASSIGNING.equalsIgnoreCase(screenMode)){
				HomeActivity.passengerScreenMode = PassengerScreenMode.P_ASSIGNING;
				Data.cSessionId = sessionId;
				clearSPData(context);
			}
			else{
				//TODO
				SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
				
				Data.cSessionId = sessionId;
				Data.cEngagementId = engagementId;
				Data.cDriverId = userId;
				
				Data.pickupLatLng = new LatLng(Double.parseDouble(pickupLatitude), Double.parseDouble(pickupLongitude));
				
				double dLatitude = Double.parseDouble(latitude);
				double dLongitude = Double.parseDouble(longitude);
				
				
				Data.assignedDriverInfo = new DriverInfo(userId, dLatitude, dLongitude, driverName, 
						driverImage, driverCarImage, driverPhone, driverRating, driverCarNumber, freeRide, promoName, eta);
				
				Data.userData.fareFactor = fareFactor;
				
				Log.e("Data.assignedDriverInfo on login","="+Data.assignedDriverInfo.latLng);
				
				
				if(Data.P_REQUEST_FINAL.equalsIgnoreCase(screenMode)){
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
				}
				else if(Data.P_IN_RIDE.equalsIgnoreCase(screenMode)){
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
					
					HomeActivity.totalDistance = Double.parseDouble(pref.getString(Data.SP_TOTAL_DISTANCE, "-1"));
					
					if(Utils.compareDouble(HomeActivity.totalDistance, -1.0) == 0){
						Data.startRidePreviousLatLng = Data.pickupLatLng;
					}
					else{
						String lat1 = pref.getString(Data.SP_LAST_LATITUDE, "0");
						String lng1 = pref.getString(Data.SP_LAST_LONGITUDE, "0");
						Data.startRidePreviousLatLng = new LatLng(Double.parseDouble(lat1), Double.parseDouble(lng1));
					}
				}
				else{
					
				}
				
			}
		}
		
		return returnResponse;
	}
	
	
	
	public static String getPromoName(JSONObject jObject){
		String promoName = "No Promo Code applied";
		try {
			String coupon = "", promotion = "";
			try {
				if(jObject.has("coupon")){
					coupon = jObject.getString("coupon");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			try {
				if(jObject.has("promotion")){
					promotion = jObject.getString("promotion");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(!"".equalsIgnoreCase(coupon)){
				promoName = coupon;
			}
			else if(!"".equalsIgnoreCase(promotion)){
				promoName = promotion;
			}
			else{
				promoName = "No Promo Code applied";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return promoName;
	}
	
	
	
	
	
	
	public void clearSPData(final Context context) {
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();

		editor.putString(Data.SP_TOTAL_DISTANCE, "-1");
		editor.putString(Data.SP_WAIT_TIME, "0");
		editor.putString(Data.SP_RIDE_TIME, "0");
		editor.putString(Data.SP_RIDE_START_TIME, ""+System.currentTimeMillis());
		editor.putString(Data.SP_LAST_LATITUDE, "0");
		editor.putString(Data.SP_LAST_LONGITUDE, "0");

		editor.commit();

		Database.getInstance(context).deleteSavedPath();
		Database.getInstance(context).close();

	}
	
	
	
	
	
	
	
	
	
	
	public static ArrayList<PromoCoupon> parsePromoCoupons(JSONObject jObj) throws Exception{
		ArrayList<PromoCoupon> promoCouponList = new ArrayList<PromoCoupon>();
		
//		{
//	    "flag": 174,
//	    "coupons": [
//	        {
//	            "title": "Free ride",
//	            "subtitle": "upto Rs. 100",
//	            "description": "Your next ride",
//	            "discount": 100,
//	            "maximum": 100,
//	            "image": "",
//	            "type": 0,
//	            "redeemed_on": "0000-00-00 00:00:00",
//	            "status": 1,
//	            "expiry_date": "December 31st 2015"
//	        }
//	    ],
//	    "promotions": [
//	        {
//	            "promo_id": 1,
//	            "title": "Flat 100% off",
//	            "terms_n_conds": "t"
//	        }
//	    ],
//	    "dynamic_factor": "1.0"
//	}
		
		
		JSONArray jCouponsArr = jObj.getJSONArray("coupons");
		for(int i=0; i<jCouponsArr.length(); i++){
			JSONObject coData = jCouponsArr.getJSONObject(i);
			promoCouponList.add(new CouponInfo(coData.getInt("account_id"),
					coData.getInt("type"), 
					coData.getInt("status"), 
					coData.getString("title"), 
					coData.getString("subtitle"), 
					coData.getString("description"), 
					coData.getString("image"), 
					coData.getString("redeemed_on"), 
					coData.getString("expiry_date"), 
					coData.getDouble("discount"), 
					coData.getDouble("maximum")));
		}
		
		JSONArray jPromoArr = jObj.getJSONArray("promotions");
		for(int i=0; i<jPromoArr.length(); i++){
			JSONObject coData = jPromoArr.getJSONObject(i);
			promoCouponList.add(new PromotionInfo(coData.getInt("promo_id"), 
					coData.getString("title"), 
					coData.getString("terms_n_conds")));
		}
		
		return promoCouponList;
	}
	
	
	
	

	
	public static void parseCancellationReasons(JSONObject jObj){
		
//		"cancellation": {
//      "message": "Cancellation of a ride more than 5 minutes after the driver is allocated will lead to cancellation charges of Rs. 20",
//      "reasons": [
//          "Driver is late",
//          "Driver denied duty",
//          "Changed my mind",
//          "Booked another auto"
//      ]
//  }



		try {
			ArrayList<CancelOption> options = new ArrayList<CancelOption>();
			options.add(new CancelOption("Driver is late"));
			options.add(new CancelOption("Driver denied duty"));
			options.add(new CancelOption("Changed my mind"));
			options.add(new CancelOption("Booked another auto"));
			
			Data.cancelOptionsList = new CancelOptionsList(options, "Cancellation of a ride more than 5 minutes after the driver is allocated " +
					"will lead to cancellation charges of Rs. 20");
			
			JSONObject jCancellation = jObj.getJSONObject("cancellation");
			
			String message = jCancellation.getString("message");
			
			JSONArray jReasons = jCancellation.getJSONArray("reasons");
			
			options.clear();
			
			for(int i=0; i<jReasons.length(); i++){
				options.add(new CancelOption(jReasons.getString(i)));
			}
			
			Data.cancelOptionsList = new CancelOptionsList(options, message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}







    public static ArrayList<PreviousAccountInfo> parsePreviousAccounts(JSONObject jsonObject){
        ArrayList<PreviousAccountInfo> previousAccountInfoList = new ArrayList<PreviousAccountInfo>();

//        {
//            "flag": 400,
//            "users": [
//            {
//                "user_id": 145,
//                "user_name": "Shankar16",
//                "user_email": "shankar+16@jugnoo.in",
//                "phone_no": "+919780111116",
//                "date_registered": "2015-01-26T13:55:58.000Z"
//            }
//            ]
//        }

        try{

            JSONArray jPreviousAccountsArr = jsonObject.getJSONArray("users");
            for(int i=0; i<jPreviousAccountsArr.length(); i++){
                JSONObject jPreviousAccount = jPreviousAccountsArr.getJSONObject(i);
                previousAccountInfoList.add(new PreviousAccountInfo(jPreviousAccount.getInt("user_id"),
                    jPreviousAccount.getString("user_name"),
                    jPreviousAccount.getString("user_email"),
                    jPreviousAccount.getString("phone_no"),
                    jPreviousAccount.getString("date_registered")));
            }


        } catch(Exception e){
            e.printStackTrace();
        }


        return previousAccountInfoList;
    }
	
	
}
