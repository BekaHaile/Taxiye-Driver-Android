package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import product.clicklabs.jugnoo.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.datastructure.DriverInfo;
import product.clicklabs.jugnoo.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.datastructure.FareStructure;
import product.clicklabs.jugnoo.datastructure.PassengerScreenMode;
import product.clicklabs.jugnoo.datastructure.UserData;
import product.clicklabs.jugnoo.datastructure.UserMode;
import product.clicklabs.jugnoo.utils.HttpRequester;
import product.clicklabs.jugnoo.utils.Log;
import product.clicklabs.jugnoo.utils.SHA256Convertor;
import product.clicklabs.jugnoo.utils.Utils;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.maps.model.LatLng;

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
		
		int canSchedule = 0, canChangeLocation = 0, schedulingLimitMinutes = 0, isAvailable = 1, exceptionalDriver = 0, gcmIntent = 1, 
				christmasIconEnable = 0, nukkadEnable = 0, enableJugnooMeals = 1, freeRideIconDisable = 1;;
		String phoneNo = "", nukkadIcon = "", jugnooMealsPackageName = "com.cdk23.nlk";
		double jugnooBalance = 0;
		
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
		
		String authKey = userData.getString("auth_key");
		AccessTokenGenerator.saveAuthKey(context, authKey);
		
		String authSecret = authKey + Data.CLIENT_SHARED_SECRET;
		String accessToken = SHA256Convertor.getSHA256String(authSecret);
		
		return new UserData(accessToken, authKey, userData.getString("user_name"), 
				userData.getString("user_image"), userData.getString("referral_code"), phoneNo, 
				canSchedule, canChangeLocation, schedulingLimitMinutes, isAvailable, exceptionalDriver, gcmIntent, 
				christmasIconEnable, nukkadEnable, nukkadIcon, enableJugnooMeals, jugnooMealsPackageName, freeRideIconDisable, jugnooBalance);
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
				
		parseLastRideData(jObj);
		
		return resp;
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
		String link = preferences.getString(Data.SP_SERVER_LINK, Data.DEFAULT_SERVER_URL);
		
		if(link.equalsIgnoreCase(Data.TRIAL_SERVER_URL)){
			Database2.getInstance(context).updateSalesPortNumber(port);
		}
		else if(link.equalsIgnoreCase(Data.DEV_SERVER_URL)){
			Database2.getInstance(context).updateDevPortNumber(port);
		}
		else{
			Database2.getInstance(context).updateLivePortNumber(port);
		}
	}
	
	//TODO
	public void parseLastRideData(JSONObject jObj){
//	    "last_ride": {
//        "engagement_id": 5982,
//        "fare": 25,
//        "to_pay": 25,
//        "discount": 0,
//        "distance_travelled": 0,
//        "ride_time": 1,
//        "wait_time": 0,
//        "coupon": null,
//        "rate_us": 0,
//        "driver_info": {
//            "id": 234,
//            "name": "Driver 1",
//            "user_image": "http://tablabar.s3.amazonaws.com/brand_images/user.png"
//        }
//    },
		
		try {
			JSONObject jLastRideData = jObj.getJSONObject("last_ride");
			Data.cSessionId = "";
			Data.cEngagementId = jLastRideData.getString("engagement_id");
			
			JSONObject jDriverInfo = jLastRideData.getJSONObject("driver_info");
			Data.cDriverId = jDriverInfo.getString("id");
			
			Data.pickupLatLng = new LatLng(0, 0);
			
			Data.assignedDriverInfo = new DriverInfo(Data.cDriverId, jDriverInfo.getString("name"), jDriverInfo.getString("user_image"));
			
			Data.totalDistance = jLastRideData.getDouble("distance_travelled");
			Data.totalFare = jLastRideData.getDouble("fare");
			Data.waitTime = jLastRideData.getString("wait_time");
			Data.rideTime = jLastRideData.getString("ride_time");
			
			try{
				if(jLastRideData.has("rate_app")){
					Data.customerRateAppFlag = jLastRideData.getInt("rate_app");
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
			HomeActivity.passengerScreenMode = PassengerScreenMode.P_RIDE_END;
			
			try{
				Data.couponJSON = jLastRideData;
				Log.i("Data.couponJSON", "="+Data.couponJSON);
			} catch(Exception e){
				e.printStackTrace();
				Data.couponJSON = new JSONObject();
			}
			
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
			String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/get_current_user_status", nameValuePairs);
			Log.e("result of = user_status", "="+result);
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
				String userImage = dataI.getString("user_image");
				String driverCarImage = dataI.getString("driver_car_image");
				String phoneNo = dataI.getString("phone_no");
				String rating = dataI.getString("rating");
				String carNumber = "";
				if(dataI.has("driver_car_no")){
					carNumber = dataI.getString("driver_car_no");
				}
				Data.driverInfos.add(new DriverInfo(userId, latitude, longitude, userName, userImage, driverCarImage, phoneNo, rating, carNumber, 0));
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	
	
	public String parseCurrentUserStatus(Context context, int currentUserStatus, JSONObject jObject1){
		
		String returnResponse = "";
		
		if(currentUserStatus == 2){
			
			String screenMode = "";

			int engagementStatus = -1;
			String engagementId = "", sessionId = "",  userId = "", latitude = "", longitude = "", 
					driverName = "", driverImage = "", driverCarImage = "", driverPhone = "", driverRating = "", driverCarNumber = "", 
					pickupLatitude = "", pickupLongitude = "";
			int freeRide = 0;
			
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
//								Òdriver_idÓ, 
//								Òpickup_latitudeÓ, 
//								Òpickup_longitudeÓ, 
//								Òengagement_idÓ, 
//								ÒstatusÓ, 
//								Òsession_idÓ,
//								Òuser_nameÓ, 
//								Òphone_noÓ, 
//								Òuser_imageÓ, 
//								Òdriver_car_imageÓ, 
//								Òcurrent_location_latitudeÓ, 
//								Òcurrent_location_longitudeÓ, 
//								ÒratingÓ
//								}

							
								int flag = jObject1.getInt("flag");
								
								if(ApiResponseFlags.ASSIGNING_DRIVERS.getOrdinal() == flag){
									sessionId = jObject1.getString("session_id");
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
									}
								}
							
							}
			} catch(Exception e){
				e.printStackTrace();
				engagementStatus = -1;
				returnResponse = HttpRequester.SERVER_TIMEOUT;
				return returnResponse;
			}
			
			HomeActivity.userMode = UserMode.PASSENGER;
			
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
				
				String SP_C_DRIVER_DISTANCE = pref.getString(Data.SP_C_DRIVER_DISTANCE, "0");
				String SP_C_DRIVER_DURATION = pref.getString(Data.SP_C_DRIVER_DURATION, "");
				
				Data.assignedDriverInfo = new DriverInfo(userId, dLatitude, dLongitude, driverName, 
						driverImage, driverCarImage, driverPhone, driverRating, driverCarNumber, freeRide);
				Log.e("Data.assignedDriverInfo on login","="+Data.assignedDriverInfo.latLng);
				Data.assignedDriverInfo.distanceToReach = SP_C_DRIVER_DISTANCE;
				Data.assignedDriverInfo.durationToReach = SP_C_DRIVER_DURATION;
				
				Log.e("Data.assignedDriverInfo.durationToReach in get_current_user_status", "="+Data.assignedDriverInfo.durationToReach);
				
				
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
				else if(Data.P_RIDE_END.equalsIgnoreCase(screenMode)){
					String SP_C_TOTAL_DISTANCE = pref.getString(Data.SP_C_TOTAL_DISTANCE, "0");
					String SP_C_TOTAL_FARE = pref.getString(Data.SP_C_TOTAL_FARE, "0");
					String SP_C_WAIT_TIME = pref.getString(Data.SP_C_WAIT_TIME, "0");
					String SP_C_RIDE_TIME = pref.getString(Data.SP_C_RIDE_TIME, "0");
					
					Data.totalDistance = Double.parseDouble(SP_C_TOTAL_DISTANCE);
					Data.totalFare = Double.parseDouble(SP_C_TOTAL_FARE);
					Data.waitTime = SP_C_WAIT_TIME;
					Data.rideTime = SP_C_RIDE_TIME;
					
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_RIDE_END;
				}
				
			}
		}
		
		return returnResponse;
	}
	
	
	
	
	
	
	
	
	public void clearSPData(final Context context) {
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();

		editor.putString(Data.SP_DRIVER_SCREEN_MODE, "");

		editor.putString(Data.SP_D_ENGAGEMENT_ID, "");
		editor.putString(Data.SP_D_CUSTOMER_ID, "");
		editor.putString(Data.SP_D_LATITUDE, "0");
		editor.putString(Data.SP_D_LONGITUDE, "0");
		editor.putString(Data.SP_D_CUSTOMER_NAME, "");
		editor.putString(Data.SP_D_CUSTOMER_IMAGE, "");
		editor.putString(Data.SP_D_CUSTOMER_PHONE, "");
		editor.putString(Data.SP_D_CUSTOMER_RATING, "");

		editor.putString(Data.SP_TOTAL_DISTANCE, "-1");
		editor.putString(Data.SP_WAIT_TIME, "0");
		editor.putString(Data.SP_RIDE_TIME, "0");
		editor.putString(Data.SP_RIDE_START_TIME, ""+System.currentTimeMillis());
		editor.putString(Data.SP_LAST_LATITUDE, "0");
		editor.putString(Data.SP_LAST_LONGITUDE, "0");

		editor.putString(Data.SP_CUSTOMER_SCREEN_MODE, "");

		editor.putString(Data.SP_C_SESSION_ID, "");
		editor.putString(Data.SP_C_ENGAGEMENT_ID, "");
		editor.putString(Data.SP_C_DRIVER_ID, "");
		editor.putString(Data.SP_C_LATITUDE, "0");
		editor.putString(Data.SP_C_LONGITUDE, "0");
		editor.putString(Data.SP_C_DRIVER_NAME, "");
		editor.putString(Data.SP_C_DRIVER_IMAGE, "");
		editor.putString(Data.SP_C_DRIVER_CAR_IMAGE, "");
		editor.putString(Data.SP_C_DRIVER_PHONE, "");
		editor.putString(Data.SP_C_DRIVER_RATING, "");
		editor.putString(Data.SP_C_DRIVER_DISTANCE, "0");
		editor.putString(Data.SP_C_DRIVER_DURATION, "");

		editor.putString(Data.SP_C_TOTAL_DISTANCE, "0");
		editor.putString(Data.SP_C_TOTAL_FARE, "0");
		editor.putString(Data.SP_C_WAIT_TIME, "0");
		editor.putString(Data.SP_C_RIDE_TIME, "0");

		editor.commit();

		Database.getInstance(context).deleteSavedPath();
		Database.getInstance(context).close();

	}
	
	
	
	
}
