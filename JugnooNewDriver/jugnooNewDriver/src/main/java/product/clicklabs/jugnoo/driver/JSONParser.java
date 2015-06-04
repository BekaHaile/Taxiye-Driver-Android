package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Pair;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.AutoCustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.AutoRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.BusinessType;
import product.clicklabs.jugnoo.driver.datastructure.CouponInfo;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EndRideData;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.FareStructure;
import product.clicklabs.jugnoo.driver.datastructure.FatafatCustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.FatafatDeliveryInfo;
import product.clicklabs.jugnoo.driver.datastructure.FatafatOrderInfo;
import product.clicklabs.jugnoo.driver.datastructure.FatafatRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.MealRideRequest;
import product.clicklabs.jugnoo.driver.datastructure.PaymentMode;
import product.clicklabs.jugnoo.driver.datastructure.PromoInfo;
import product.clicklabs.jugnoo.driver.datastructure.UserData;
import product.clicklabs.jugnoo.driver.datastructure.UserMode;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.HttpRequester;
import product.clicklabs.jugnoo.driver.utils.Log;

public class JSONParser {

	public JSONParser(){
		
	}
	
	public void parseLoginData(Context context, String response) throws Exception{
		JSONObject jObj = new JSONObject(response);
		JSONObject userData = jObj.getJSONObject("user_data");
		
		Data.userData = parseUserData(context, userData);
		
		Data.termsAgreed = 1;
		saveAccessToken(context, Data.userData.accessToken);
		
		try{
			int currentUserStatus = userData.getInt("current_user_status");
			
			if(currentUserStatus == 1){
				Database2.getInstance(context).updateUserMode(Database2.UM_DRIVER);
				new DriverServiceOperations().startDriverService(context);
				HomeActivity.userMode = UserMode.DRIVER;
				HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
			}
		} catch(Exception e){
			e.printStackTrace();
			Database2.getInstance(context).updateUserMode(Database2.UM_DRIVER);
			new DriverServiceOperations().startDriverService(context);
			HomeActivity.userMode = UserMode.DRIVER;
			HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
		}

	}
	
	
	public static void saveAccessToken(Context context, String accessToken){
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();
		editor.putString(Data.SP_ACCESS_TOKEN_KEY, accessToken);
		editor.putString(Data.SP_IS_ACCESS_TOKEN_NEW, "1");
		editor.commit();
	}
	
	public static Pair<String, String> getAccessTokenPair(Context context){
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		String accessToken = pref.getString(Data.SP_ACCESS_TOKEN_KEY, "");
		String isAccessTokenNew = pref.getString(Data.SP_IS_ACCESS_TOKEN_NEW, "not_found");
		return new Pair<String, String>(accessToken, isAccessTokenNew);
	}
	
	
	public static FareStructure parseFareObject(JSONObject fareDetails){
		try{
//			{
//		        "id": 1,
//		        "fare_fixed": 25,
//		        "fare_per_km": 6,
//		        "fare_threshold_distance": 2,
//		        "fare_per_min": 1,
//		        "fare_threshold_time": 0,
//		        "type": 0,
//		        "per_ride_driver_subsidy": 20
//		    }
			
//			For testing
//			return new FareStructure(40, 1, 20, 2, 1, 0, 0);
			
			return new FareStructure(fareDetails.getDouble("fare_fixed"), 
					fareDetails.getDouble("fare_threshold_distance"), 
					fareDetails.getDouble("fare_per_km"), 
					fareDetails.getDouble("fare_per_min"), 
					fareDetails.getDouble("fare_threshold_time"), 
					fareDetails.getDouble("fare_per_waiting_min"), 
					fareDetails.getDouble("fare_threshold_waiting_time"));
		} catch(Exception e){
			e.printStackTrace();
			return new FareStructure(25, 2, 6, 1, 6, 0, 0);
		}
	}
	
	
	public static CouponInfo parseCouponInfo(JSONObject couponObject){
		try{
			
//			"coupon": {
//	        "account_id": 367,
//	        "coupon_id": 1,
//	        "title": "Free ride",
//	        "description": "Your next ride with Jugnoo upto Rs. 100 will be FREE. \n\nTerms of Use:\n1. The coupon will be applied automatically at the end of your next ride.\n2. Only one coupon will be applied in one ride.\n3. The maximum value of this coupon is Rs. 100 and you will have to pay the remaining amount at the end of the ride.\n4. Jugnoo reserves the right to discontinue the coupon at its discretion.",
//	        "discount": 100,
//	        "maximum": 100,
//	        "image": "",
//	        "type": 0,
//	        "subtitle": "upto Rs. 100"
//	    }

//			For testing
//			CouponInfo couponInfo = new CouponInfo(0, 
//					"50% off", 
//					"upto 100/-", 
//					"discount", 
//					50, 
//					100);
			
			CouponInfo couponInfo = new CouponInfo(couponObject.getInt("type"), 
					couponObject.getString("title"), 
					couponObject.getString("subtitle"), 
					couponObject.getString("description"), 
					couponObject.getDouble("discount"), 
					couponObject.getDouble("maximum"),
					couponObject.getDouble("capped_fare"), 
					couponObject.getDouble("capped_fare_maximum"));
			return couponInfo;
		} catch(Exception e){
			Log.w("couponInfo", "e="+e.toString());
			return null;
		}
	}
	
	public static PromoInfo parsePromoInfo(JSONObject jPromoObject){
		try{
			
//			"promotion": {
//	        	"title": "After 18th Flat 40% off",
//	        	"discount_percentage": 40,
//	        	"discount_maximum": 40,
//	        	"capped_fare": -1,
//	        	"capped_fare_maximum": -1
//	    	}	
			PromoInfo promoInfo = new PromoInfo(jPromoObject.getString("title"), 
					jPromoObject.getDouble("discount_percentage"), 
					jPromoObject.getDouble("discount_maximum"), 
					jPromoObject.getDouble("capped_fare"), 
					jPromoObject.getDouble("capped_fare_maximum"));
			
			return promoInfo;
		} catch(Exception e){
			Log.w("promoInfo", "e="+e.toString());
			return null;
		}
	}
	
	
	
	public UserData parseUserData(Context context, JSONObject userData) throws Exception{

//        {
//            "flag": 150,
//            "login": {
//            "user_data": {
//                "access_token": "35adf238dd7d1af82291ac6c2fe2f0ca6a978ed543d087507f7cf2e2b814dcfd",
//                    "user_name": "Driver 2",
//                    "user_image": "http://tablabar.s3.amazonaws.com/brand_images/user.png",
//                    "phone_no": "+919780298413",
//                    "referral_code": "DRIVER19",
//                    "is_available": 0,
//                    "autos_enabled": 1,
//                    "meals_enabled": 0,
//                    "fatafat_enabled": 1,
//                    "autos_available": 0,
//                    "meals_available": 0,
//                    "fatafat_available": 0,
//                    "gcm_intent": 1,
//                    "current_user_status": 1,
//                    "fare_details": [
//                {
//                    "fare_fixed": 20,
//                    "fare_per_km": 5,
//                    "fare_threshold_distance": 0,
//                    "fare_per_min": 1,
//                    "fare_threshold_time": 0
//                }
//                ],
//                "exceptional_driver": 0
//            },
//            "popup": 0
//        },
//            "status": {
//            "flag": 133,
//                "active_requests": []
//        }
//        }


		int freeRideIconDisable = 1;
		
		int autosEnabled = 1, mealsEnabled = 0, fatafatEnabled = 0;
		int autosAvailable = 1, mealsAvailable = 0, fatafatAvailable = 0;
		
		if(userData.has("free_ride_icon_disable")){
			freeRideIconDisable = userData.getInt("free_ride_icon_disable");
		}
		
		if(userData.has("autos_enabled")){
			autosEnabled = userData.getInt("autos_enabled");
		}
		if(userData.has("meals_enabled")){
			mealsEnabled = userData.getInt("meals_enabled");
		}
		if(userData.has("fatafat_enabled")){
			fatafatEnabled = userData.getInt("fatafat_enabled");
		}
		
		if(userData.has("autos_available")){
			autosAvailable = userData.getInt("autos_available");
		}
		if(userData.has("meals_available")){
			mealsAvailable = userData.getInt("meals_available");
		}
		if(userData.has("fatafat_available")){
			fatafatAvailable = userData.getInt("fatafat_available");
		}

        if(1 != autosEnabled){
            autosAvailable = 0;
        }
        if(1 != mealsEnabled){
            mealsAvailable = 0;
        }
        if(1 != fatafatEnabled) {
            fatafatAvailable = 0;
        }

		
		try{
			if(userData.has("gcm_intent")){
				Database2.getInstance(context).updateDriverGcmIntent(userData.getInt("gcm_intent"));
			}
		} catch(Exception e){}
		
		
		return new UserData(userData.getString("access_token"), userData.getString("user_name"), 
				userData.getString("user_image"), userData.getString("referral_code"), userData.getString("phone_no"), 
				freeRideIconDisable, autosEnabled, mealsEnabled, fatafatEnabled, autosAvailable, mealsAvailable, fatafatAvailable);
	}
	
	public String parseAccessTokenLoginData(Context context, String response) throws Exception{
		
		Log.e("response ==", "="+response);
		
		JSONObject jObj = new JSONObject(response);
		
		//Fetching login data
		JSONObject jLoginObject = jObj.getJSONObject("login");
		JSONObject userData = jLoginObject.getJSONObject("user_data");
		
		Data.userData = parseUserData(context, userData);
		saveAccessToken(context, Data.userData.accessToken);
		
		//current_user_status = 1 driver or 2 user
		int currentUserStatus = userData.getInt("current_user_status");
		if(currentUserStatus == 1){
			Database2.getInstance(context).updateUserMode(Database2.UM_DRIVER);
		}
		else if(currentUserStatus == 2){
			Database2.getInstance(context).updateUserMode(Database2.UM_PASSENGER);
		}
		
		parsePortNumber(context, jLoginObject);
		
		
		//Fetching user current status
		JSONObject jUserStatusObject = jObj.getJSONObject("status");
		String resp = parseCurrentUserStatus(context, currentUserStatus, jUserStatusObject);
				
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
	
	
	
	
	
	//TODO
	public void parseLastRideData(JSONObject jObj){
		
//	    "status": {
//	        "flag": 135,
//	        "last_engagement_info": [
//	            {
//	                "engagement_id": 7939,
//	                "user_id": 207,
//	                "session_id": 4404,
//	                "distance_travelled": 0,
//	                "ride_time": 1,
//	                "money_transacted": 26,
//	                "discount": 0,
//	                "paid_using_wallet": 0,
//	                "paid_by_customer": 26,
//	                "fare_factor": 1.2,
//	                "fare_details": {
//	                    "id": 2,
//	                    "fare_fixed": 20,
//	                    "fare_per_km": 5,
//	                    "fare_threshold_distance": 0,
//	                    "fare_per_min": 1,
//	                    "fare_threshold_time": 0,
//	                    "fare_per_waiting_min": 0,
//	                    "fare_threshold_waiting_time": 0,
//	                    "type": 1,
//	                    "per_ride_driver_subsidy": 0,
//	                    "accept_subsidy_per_km": 3
//	                },
//	                "user_name": "Shankar Bhagwati",
//	                "phone_no": "+919000111001",
//	                "user_image": "http://graph.facebook.com/717496164959213/picture?width=160&height=160",
//	            }
//	        ]
//	    }
		
		try {
			JSONArray lastEngInfoArr = jObj.getJSONArray("last_engagement_info");
			JSONObject jLastRideData = lastEngInfoArr.getJSONObject(0);
			
			Log.e("jLastRideData", "="+jLastRideData);
			
			Data.dEngagementId = jLastRideData.getString("engagement_id");
			Data.dCustomerId = jLastRideData.getString("user_id");

			HomeActivity.totalDistance = jLastRideData.getDouble("distance_travelled");
			HomeActivity.waitTime = "0";
			HomeActivity.rideTime = jLastRideData.getString("ride_time");
			HomeActivity.totalFare = jLastRideData.getDouble("fare");
			HomeActivity.waitStart = 2;
			
			Data.dCustLatLng = new LatLng(0, 0);
			Data.startRidePreviousLatLng = new LatLng(0, 0);
			Data.startRidePreviousLocationTime = System.currentTimeMillis();
			
			CouponInfo couponInfo = null;
			PromoInfo promoInfo = null;
			
			if(jLastRideData.has("coupon")){
				try{
					couponInfo = JSONParser.parseCouponInfo(jLastRideData.getJSONObject("coupon"));
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			
			if(jLastRideData.has("promotion")){
				try{
					promoInfo = JSONParser.parsePromoInfo(jLastRideData.getJSONObject("promotion"));
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			
			int referenceId = 0;
			
			Data.assignedCustomerInfo = new AutoCustomerInfo(Integer.parseInt(Data.dEngagementId), Integer.parseInt(Data.dCustomerId),
					referenceId, jLastRideData.getString("user_name"), jLastRideData.getString("phone_no"), 
					new LatLng(0, 0),
					jLastRideData.getString("user_image"), couponInfo, promoInfo);
			
			
			if(jLastRideData.has("fare_details")){
				try{
					Data.fareStructure = JSONParser.parseFareObject(jLastRideData.getJSONObject("fare_details"));
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			
			if(jLastRideData.has("fare_factor")){
				try{
					Data.fareStructure.fareFactor = jLastRideData.getDouble("fare_factor");
				} catch(Exception e){
					e.printStackTrace();
				}
			}
			
			parseEndRideData(jLastRideData, Data.dEngagementId, HomeActivity.totalFare);
			
			
			HomeActivity.driverScreenMode = DriverScreenMode.D_RIDE_END;
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	
	
	public String parseCurrentUserStatus(Context context, int currentUserStatus, JSONObject jObject1){
		
		String returnResponse = "";
		
		if(currentUserStatus == 1){ // TODO for driver
			
			String screenMode = "";
			
			int engagementStatus = -1;
			String engagementId = "", userId = "", customerName = "", customerImage = "", customerPhone = "", customerRating = "4", schedulePickupTime = "";
			double pickupLatitude = 0, pickupLongitude = 0;
			int freeRide = 0;
			CouponInfo couponInfo = null;
			PromoInfo promoInfo = null;
			double jugnooBalance = 0;
			int dBusinessId = BusinessType.AUTOS.getOrdinal();
			int dReferenceId = 0;
			String storeAddress = "";
			int storeOrderAmount = 0;
			FatafatDeliveryInfo deliveryInfo = null;
			FatafatCustomerInfo customerInfo = null;
			
			HomeActivity.userMode = UserMode.DRIVER;
			
			try{
							
							if(jObject1.has("error")){
								returnResponse = HttpRequester.SERVER_TIMEOUT;
								return returnResponse;
							}
							else{
							
//							{
//								"flag": constants.responseFlags.ACTIVE_REQUESTS,
//								"active_requests":[
//									{
//								�engagement_id�, 
//								�user_id�, 
//								�pickup_latitude�, 
//								�pickup_longitude�, 
//								�pickup_location_address�, 
//								�current_time�
//								}
//								]};
							
							
							
//							{
//							"flag": constants.responseFlags.ENGAGEMENT_DATA,
//							"last_engagement_info":[
//							{
//							�user_id�,
//							�pickup_latitude�,
//							�pickup_longitude�,
//							�engagement_id�,
//							�status�,
//							�user_name�,
//							�phone_no�,
//							�user_image�,
//							�rating�
//							}
//							]
							
								int flag = jObject1.getInt("flag");
								
								if(ApiResponseFlags.ACTIVE_REQUESTS.getOrdinal() == flag){

									JSONArray jActiveRequests = jObject1.getJSONArray("active_requests");
									
									Data.driverRideRequests.clear();
									for(int i=0; i<jActiveRequests.length(); i++){
										JSONObject jActiveRequest = jActiveRequests.getJSONObject(i);
										 String requestEngagementId = jActiveRequest.getString("engagement_id");
		    	    					 String requestUserId = jActiveRequest.getString("user_id");
		    	    					 double requestLatitude = jActiveRequest.getDouble("pickup_latitude");
		    	    					 double requestLongitude = jActiveRequest.getDouble("pickup_longitude");
		    	    					 String requestAddress = jActiveRequest.getString("pickup_location_address");
		    	    					 
		    	    					 String startTime = jActiveRequest.getString("start_time");
                                        String endTime = "";
                                        if(jActiveRequest.has("end_time")){
                                            endTime = jActiveRequest.getString("end_time");
                                        }
		    	    					 
		    	    					 String startTimeLocal = DateOperations.utcToLocal(startTime);
		    	    					 
		    	    					 long requestTimeOutMillis = GCMIntentService.REQUEST_TIMEOUT;
		    	    					 if("".equalsIgnoreCase(endTime)){
		    	    						 long serverStartTimeLocalMillis = DateOperations.getMilliseconds(startTimeLocal);
		    	    						 long serverStartTimeLocalMillisPlus60 = serverStartTimeLocalMillis + 60000;
		    	    						 requestTimeOutMillis = serverStartTimeLocalMillisPlus60 - System.currentTimeMillis();
		    	    					 }
		    	    					 else{
		    	    						 long startEndDiffMillis = DateOperations.getTimeDifference(DateOperations.utcToLocal(endTime), 
		    	    								 startTimeLocal);
		    	    						 Log.i("startEndDiffMillis = ", "="+startEndDiffMillis);
		    	    						 if(startEndDiffMillis < GCMIntentService.REQUEST_TIMEOUT){
		    	    							 requestTimeOutMillis = startEndDiffMillis;
		    	    						 }
		    	    						 else{
		    	    							 requestTimeOutMillis = GCMIntentService.REQUEST_TIMEOUT;
		    	    						 }
		    	    					 }
		    	    					 
		    	    					 startTime = DateOperations.getDelayMillisAfterCurrentTime(requestTimeOutMillis);
		    	    					 
		    	    					 int businessId = BusinessType.AUTOS.getOrdinal();
		    	    					 if(jActiveRequest.has("business_id")){
		    	    						 businessId = jActiveRequest.getInt("business_id");
		    	    					 }
		    	    					 
		    	    					 int referenceId = jActiveRequest.getInt("reference_id");
		    	    					 
		    	    					 if(BusinessType.AUTOS.getOrdinal() == businessId){
		    	    						 Data.driverRideRequests.add(new AutoRideRequest(requestEngagementId, requestUserId, 
			    	    								new LatLng(requestLatitude, requestLongitude), startTime, requestAddress, 
			    	    								businessId, referenceId));
	    								 }
	    								 else if(BusinessType.MEALS.getOrdinal() == businessId){
	    									 String rideTime = jActiveRequest.getString("ride_time");
	    									
	    									 Data.driverRideRequests.add(new MealRideRequest(requestEngagementId, requestUserId, 
			    	    								new LatLng(requestLatitude, requestLongitude), startTime, requestAddress, 
			    	    								businessId, referenceId, rideTime));
	    								 }
	    								 else if(BusinessType.FATAFAT.getOrdinal() == businessId){
	    									 int orderAmount = jActiveRequest.getInt("order_amount");
	    									 Data.driverRideRequests.add(new FatafatRideRequest(requestEngagementId, requestUserId, 
			    	    								new LatLng(requestLatitude, requestLongitude), startTime, requestAddress, 
			    	    								businessId, referenceId, orderAmount));
	    								 }
		    	    					 
		    	    					 Log.i("inserter in db", "insertDriverRequest = "+requestEngagementId);
									}
									
									
									if(jActiveRequests.length() == 0){
										GCMIntentService.stopRing();
									}
									
								}
								else if(ApiResponseFlags.ENGAGEMENT_DATA.getOrdinal() == flag){
									JSONArray lastEngInfoArr = jObject1.getJSONArray("last_engagement_info");
									JSONObject jObject = lastEngInfoArr.getJSONObject(0);
									
									dReferenceId = jObject.getInt("reference_id");
									dBusinessId = jObject.getInt("business_id");
									
									if(jObject.has("fare_details")){
										try{
											Data.fareStructure = JSONParser.parseFareObject(jObject.getJSONObject("fare_details"));
										} catch(Exception e){
											e.printStackTrace();
										}
									}
									
									if(jObject.has("fare_factor")){
										try{
											Data.fareStructure.fareFactor = jObject.getDouble("fare_factor");
										} catch(Exception e){
											e.printStackTrace();
										}
									}
									
									
									if(BusinessType.AUTOS.getOrdinal() == dBusinessId){
										engagementStatus = jObject.getInt("status");
										
										if((EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus) || 
												(EngagementStatus.STARTED.getOrdinal() == engagementStatus)){
											engagementId = jObject.getString("engagement_id");
											userId = jObject.getString("user_id");
											pickupLatitude = jObject.getDouble("pickup_latitude");
											pickupLongitude = jObject.getDouble("pickup_longitude");
											customerName = jObject.getString("user_name");
											customerImage = jObject.getString("user_image");
											customerPhone = jObject.getString("phone_no");
											if(jObject.has("rating")){
												customerRating = jObject.getString("rating");
											}
											int isScheduled = 0;
											if(jObject.has("is_scheduled")){
												isScheduled = jObject.getInt("is_scheduled");
												if(isScheduled == 1 && jObject.has("pickup_time")){
													schedulePickupTime = jObject.getString("pickup_time");
												}
											}
											if(jObject.has("jugnoo_balance")){
												jugnooBalance = jObject.getDouble("jugnoo_balance");
											}
											if(jObject.has("free_ride")){
												freeRide = jObject.getInt("free_ride");
											}
											if(jObject.has("coupon")){
												try{
													couponInfo = JSONParser.parseCouponInfo(jObject.getJSONObject("coupon"));
												} catch(Exception e){
													e.printStackTrace();
												}
											}
											if(jObject.has("promotion")){
												try{
													promoInfo = JSONParser.parsePromoInfo(jObject.getJSONObject("promotion"));
												} catch(Exception e){
													e.printStackTrace();
												}
											}
										}
									}
									else if(BusinessType.MEALS.getOrdinal() == dBusinessId){
										
									}
									else if(BusinessType.FATAFAT.getOrdinal() == dBusinessId){
//										"store_data": {
//					                    "store_id": 6,
//					                    "name": "Subway",
//					                    "latitude": 30.705879,
//					                    "longitude": 76.801118,
//					                    "address": "Plot No. 178, Industrial Area, Phase 1 Industrial Area, Chandigarh, 160017",
//					                    "phone_no": "+911725049297"
//					                }
										
										engagementStatus = jObject.getInt("status");
										
										if((EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus) || 
												(EngagementStatus.STARTED.getOrdinal() == engagementStatus)){
											
											engagementId = jObject.getString("engagement_id");
											storeOrderAmount = jObject.getInt("order_amount");
											
											JSONObject storeData = jObject.getJSONObject("store_data");
											userId = storeData.getString("store_id");
											customerName = storeData.getString("name");
											pickupLatitude = storeData.getDouble("latitude");
											pickupLongitude = storeData.getDouble("longitude");
											customerPhone = storeData.getString("phone_no");
											storeAddress = storeData.getString("address");
											
											if(EngagementStatus.STARTED.getOrdinal() == engagementStatus){
												JSONObject jDeliveryInfo = jObject.getJSONObject("delivery_info");
												deliveryInfo = new FatafatDeliveryInfo(jDeliveryInfo.getInt("order_id"), 
														jDeliveryInfo.getString("delivery_address"), 
														new LatLng(jDeliveryInfo.getDouble("delivery_latitude"), jDeliveryInfo.getDouble("delivery_longitude")), 
														jDeliveryInfo.getDouble("final_price"), 
														jDeliveryInfo.getDouble("discount"),
														jDeliveryInfo.getDouble("paid_from_wallet"), 
														jDeliveryInfo.getDouble("customer_to_pay"));
												
												JSONObject jCustomerInfo = jObject.getJSONObject("customer_info");
												customerInfo = new FatafatCustomerInfo(jCustomerInfo.getInt("user_id"), 
														jCustomerInfo.getString("name"), 
														jCustomerInfo.getString("phone_no"));
											}
										}
									}
									
									try{
										Log.writePathLogToFile(engagementId + "accept", "JSONPARSER  = "+jObject1);
									} catch(Exception e){
										e.printStackTrace();
									}
									
								}
								else if(ApiResponseFlags.LAST_ENGAGEMENT_DATA.getOrdinal() == flag){
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
			
			
			
			// 0 for request, 1 for accepted,2 for started,3 for ended, 4 for rejected by driver, 5 for rejected by user,6 for timeout, 7 for nullified by chrone
			if(EngagementStatus.ACCEPTED.getOrdinal() == engagementStatus){
				screenMode = Data.D_START_RIDE;
			}
			else if(EngagementStatus.STARTED.getOrdinal() == engagementStatus){
				screenMode = Data.D_IN_RIDE;
			}
			else{
				screenMode = "";
			}

			
			if("".equalsIgnoreCase(screenMode)){
				HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				clearSPData(context);
			}
			else{
				
				Data.dEngagementId = engagementId;
				Data.dCustomerId = userId;
				
				Data.dCustLatLng = new LatLng(pickupLatitude, pickupLongitude);
				
				if(BusinessType.AUTOS.getOrdinal() == dBusinessId){
					Data.assignedCustomerInfo = new AutoCustomerInfo(Integer.parseInt(engagementId), Integer.parseInt(userId), 
							dReferenceId, customerName, customerPhone, Data.dCustLatLng, 
							customerImage, customerRating, schedulePickupTime, freeRide, 
							couponInfo, promoInfo, jugnooBalance);
				}
				else if(BusinessType.MEALS.getOrdinal() == dBusinessId){
					
				}
				else if(BusinessType.FATAFAT.getOrdinal() == dBusinessId){
					Data.assignedCustomerInfo = new FatafatOrderInfo(Integer.parseInt(engagementId), Integer.parseInt(userId), 
							dReferenceId, customerName, customerPhone, Data.dCustLatLng, storeAddress, storeOrderAmount);
					
					if((EngagementStatus.STARTED.getOrdinal() == engagementStatus) && (deliveryInfo != null) && (customerInfo != null)){
						((FatafatOrderInfo)Data.assignedCustomerInfo).setCustomerDeliveryInfo(customerInfo, deliveryInfo);
					}
				}
				
				
				
				if(Data.D_START_RIDE.equalsIgnoreCase(screenMode)){
					HomeActivity.driverScreenMode = DriverScreenMode.D_START_RIDE;
				}
				else if(Data.D_IN_RIDE.equalsIgnoreCase(screenMode)){
					HomeActivity.driverScreenMode = DriverScreenMode.D_IN_RIDE;
					
					SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
					
					HomeActivity.totalDistance = Double.parseDouble(pref.getString(Data.SP_TOTAL_DISTANCE, "-1"));
					HomeActivity.previousWaitTime = Long.parseLong(pref.getString(Data.SP_WAIT_TIME, "0"));
					
					long rideStartTime = Long.parseLong(pref.getString(Data.SP_RIDE_START_TIME, ""+System.currentTimeMillis()));
					long timeDiffToAdd = System.currentTimeMillis() - rideStartTime;
					if(timeDiffToAdd > 0){
						HomeActivity.previousRideTime = timeDiffToAdd;
					}
					else{
						HomeActivity.previousRideTime = 0;
					}
					
					
					HomeActivity.waitStart = 2;
					
					String lat1 = pref.getString(Data.SP_LAST_LATITUDE, "0");
					String lng1 = pref.getString(Data.SP_LAST_LONGITUDE, "0");
					
					String previousLocationTime = pref.getString(Data.SP_LAST_LOCATION_TIME, ""+System.currentTimeMillis());
					
					Data.startRidePreviousLatLng = new LatLng(Double.parseDouble(lat1), Double.parseDouble(lng1));
					
					Data.startRidePreviousLocationTime = Long.parseLong(previousLocationTime);
					
					Log.e("Data on app restart", "-----");
					Log.i("HomeActivity.totalDistance", "="+HomeActivity.totalDistance);
					Log.i("Data.startRidePreviousLatLng", "="+Data.startRidePreviousLatLng);
					Log.i("Data.previousLocationTime", "="+Data.startRidePreviousLocationTime);
					Log.e("----------", "-----");
					
					Log.writePathLogToFile(Data.dEngagementId, "Got from SP totalDistance = "+HomeActivity.totalDistance);
					Log.writePathLogToFile(Data.dEngagementId, "Got from SP Data.startRidePreviousLatLng = "+Data.startRidePreviousLatLng);
					Log.writePathLogToFile(Data.dEngagementId, "Got from SP Data.startRidePreviousLocationTime = "+Data.startRidePreviousLocationTime);
					
				}
				else{
					HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				}
				
			}
			
		}
		
		return returnResponse;
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
		editor.putString(Data.SP_LAST_LOCATION_TIME, ""+System.currentTimeMillis());

		editor.commit();

		Database.getInstance(context).deleteSavedPath();

	}
	
	
	
	public static void parseEndRideData(JSONObject jObj, String engagementId, double totalFare){
		try {
			Data.endRideData = new EndRideData(engagementId, 
					jObj.getDouble("fare"), 
					jObj.getDouble("discount"), 
					jObj.getDouble("paid_using_wallet"), 
					jObj.getDouble("to_pay"),
					jObj.getInt("payment_mode"));
			
		} catch (JSONException e) {
			e.printStackTrace();
			Data.endRideData = new EndRideData(Data.dEngagementId, 
					totalFare, 
					0, 
					0, 
					totalFare,
					PaymentMode.CASH.getOrdinal());
		}
	}
	
	
}
