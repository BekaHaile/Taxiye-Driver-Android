package product.clicklabs.jugnoo;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.google.android.gms.maps.model.LatLng;

public class JSONParser {

	public JSONParser(){
		
	}
	
	
	public void parseLoginData(Context context, String response) throws Exception{
		JSONObject jObj = new JSONObject(response);
		JSONObject userData = jObj.getJSONObject("user_data");
		Data.userData = new UserData(userData.getString("access_token"), userData.getString("user_name"), 
				userData.getString("user_image"), userData.getString("id"));
		
		SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
		Editor editor = pref.edit();
		editor.putString(Data.SP_ACCESS_TOKEN_KEY, Data.userData.accessToken);
		editor.putString(Data.SP_ID_KEY, Data.userData.id);
		editor.commit();
		
		try{
			int currentUserStatus = userData.getInt("current_user_status");
			if(currentUserStatus == 1){
				HomeActivity.userMode = UserMode.DRIVER;
				HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				
				try {
					int excepInt = userData.getInt("exceptional_driver");
					if(1 == excepInt){
						HomeActivity.exceptionalDriver = ExceptionalDriver.YES;
					}
					else{
						HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
					}
				} catch (Exception e) {
					HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
					e.printStackTrace();
				}
			}
			else if(currentUserStatus == 2){
				HomeActivity.userMode = UserMode.PASSENGER;
				HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
				HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
			}
		} catch(Exception e){
			e.printStackTrace();
			HomeActivity.userMode = UserMode.PASSENGER;
			HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
			HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
		}
		
		
		
//		{
//		    "user_data": {
//		        "access_token": "c274d8d70e77850511df24a6255dab48",
//		        "user_name": "Shankar Bhagwati",
//		        "user_image": "http://graph.facebook.com/717496164959213/picture?width=160&height=160",
//		        "id": 1,
//		        "current_user_status": 1,
//		        "fare_details": [
//		            {
//		                "fare_fixed": 30,
//		                "fare_per_km": 10,
//		                "fare_threshold_distance": 2
//		            }
//		        ]
//		    },
//		    "popup": 0
//		}
		
		
		try{
			JSONArray fareDetailsArr = userData.getJSONArray("fare_details");
			JSONObject fareDetails0 = fareDetailsArr.getJSONObject(0);
			HomeActivity.fareFixed = fareDetails0.getInt("fare_fixed");
			HomeActivity.farePerKm = fareDetails0.getInt("fare_per_km");
			HomeActivity.fareThresholdDistance = fareDetails0.getInt("fare_threshold_distance");
		} catch(Exception e){
			e.printStackTrace();
			HomeActivity.fareFixed = 30;
			HomeActivity.farePerKm = 10;
			HomeActivity.fareThresholdDistance = 2;
		}
		
		
	}
	
	
	
	
	
	public void parseAccessTokenLoginData(Context context, String response, String accessToken, String id) throws Exception{
		JSONObject jObj = new JSONObject(response);
		JSONObject userData = jObj.getJSONObject("user_data");
		Data.userData = new UserData(accessToken, userData.getString("user_name"), 
				userData.getString("user_image"), id);
		
		try{
			JSONArray fareDetailsArr = userData.getJSONArray("fare_details");
			JSONObject fareDetails0 = fareDetailsArr.getJSONObject(0);
			HomeActivity.fareFixed = fareDetails0.getInt("fare_fixed");
			HomeActivity.farePerKm = fareDetails0.getInt("fare_per_km");
			HomeActivity.fareThresholdDistance = fareDetails0.getInt("fare_threshold_distance");
		} catch(Exception e){
			e.printStackTrace();
			HomeActivity.fareFixed = 30;
			HomeActivity.farePerKm = 10;
			HomeActivity.fareThresholdDistance = 2;
		}
		
		
		//current_user_status = 1 driver or 2 user
		
		int currentUserStatus = userData.getInt("current_user_status");
		
		
		if(currentUserStatus == 1){
			
			try{
				int excepInt = userData.getInt("exceptional_driver");
				if(1 == excepInt){
					HomeActivity.exceptionalDriver = ExceptionalDriver.YES;
				}
				else{
					HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
				}
			} catch(Exception e){
				e.printStackTrace();
				HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
			}
			
			HomeActivity.userMode = UserMode.DRIVER;
			
			SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
			
			String screenMode = pref.getString(Data.SP_DRIVER_SCREEN_MODE, "");
			
			
			int engagementStatus = -1;
			
			//TODO
			try{
//				if(!"".equalsIgnoreCase(pref.getString(Data.SP_D_ENGAGEMENT_ID, ""))){
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
					
					SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
					String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/user_status", nameValuePairs);
					
					if(result.equalsIgnoreCase(SimpleJSONParser.SERVER_TIMEOUT)){
						Log.e("timeout","=");
					}
					else{
						try{
							JSONObject jObject = new JSONObject(result);
							engagementStatus = jObject.getInt("log");
							
							if((1 == engagementStatus) || (2 == engagementStatus)){
								String engagementId = jObject.getString("engagement_id");
								String userId = jObject.getString("user_id");
								String latitude = jObject.getString("latitude");
								String longitude = jObject.getString("longitude");
								String customerName = jObject.getString("customer_name");
								String customerImage = jObject.getString("customer_image");
								String customerPhone = jObject.getString("customer_phone");
								String customerRating = jObject.getString("customer_raitng");
								
								
							}
							else{
								
							}
							
						} catch(Exception e){
							e.printStackTrace();
						}
					}
//				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
			// 0 for request, 1 for accepted,2 for started,3 for ended, 4 for rejected by driver, 5 for rejected by user,6 for timeout, 7 for nullified by chrone
			if(engagementStatus == 1){
				screenMode = Data.D_START_RIDE;
			}
			else if(engagementStatus == 2){
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
				
				if(Data.D_START_RIDE.equalsIgnoreCase(screenMode)){
					
					HomeActivity.driverScreenMode = DriverScreenMode.D_START_RIDE;
					
					Data.dEngagementId = pref.getString(Data.SP_D_ENGAGEMENT_ID, "");
					Data.dCustomerId = pref.getString(Data.SP_D_CUSTOMER_ID, "");
					
					String lat = pref.getString(Data.SP_D_LATITUDE, "0");
					String lng = pref.getString(Data.SP_D_LONGITUDE, "0");
					
					Data.dCustLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
					
					String name = pref.getString(Data.SP_D_CUSTOMER_NAME, "");
					String image = pref.getString(Data.SP_D_CUSTOMER_IMAGE, "");
					String phone = pref.getString(Data.SP_D_CUSTOMER_PHONE, "");
					String rating = pref.getString(Data.SP_D_CUSTOMER_RATING, "");
					
					Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, name, image, phone, rating);
					
				}
				else if(Data.D_IN_RIDE.equalsIgnoreCase(screenMode)){
					
					HomeActivity.driverScreenMode = DriverScreenMode.D_IN_RIDE;
					
					Data.dEngagementId = pref.getString(Data.SP_D_ENGAGEMENT_ID, "");
					Data.dCustomerId = pref.getString(Data.SP_D_CUSTOMER_ID, "");
					
					String name = pref.getString(Data.SP_D_CUSTOMER_NAME, "");
					String image = pref.getString(Data.SP_D_CUSTOMER_IMAGE, "");
					String phone = pref.getString(Data.SP_D_CUSTOMER_PHONE, "");
					String rating = pref.getString(Data.SP_D_CUSTOMER_RATING, "");
					
					Data.assignedCustomerInfo = new CustomerInfo(Data.dCustomerId, name, image, phone, rating);
					
					HomeActivity.totalDistance = Double.parseDouble(pref.getString(Data.SP_TOTAL_DISTANCE, "-1"));
					HomeActivity.previousWaitTime = Double.parseDouble(pref.getString(Data.SP_WAIT_TIME, "0"));
					HomeActivity.previousRideTime = Double.parseDouble(pref.getString(Data.SP_RIDE_TIME, "0"));
					
					HomeActivity.waitStart = 2;
					
					
					
					String lat = pref.getString(Data.SP_LAST_LATITUDE, "0");
					String lng = pref.getString(Data.SP_LAST_LONGITUDE, "0");
					
					Data.startRidePreviousLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
					
				}
				else{
					HomeActivity.driverScreenMode = DriverScreenMode.D_INITIAL;
				}
				
			}
			
			
		}
		else{
			
			HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
			
			HomeActivity.userMode = UserMode.PASSENGER;
			
			SharedPreferences pref = context.getSharedPreferences(Data.SHARED_PREF_NAME, 0);
			
			
			String screenMode = pref.getString(Data.SP_CUSTOMER_SCREEN_MODE, "");
			

			int engagementStatus = -1;
			
			//TODO
			try{
				if(!"".equalsIgnoreCase(pref.getString(Data.SP_C_ENGAGEMENT_ID, ""))){
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
					nameValuePairs.add(new BasicNameValuePair("engage_id", pref.getString(Data.SP_C_ENGAGEMENT_ID, "")));
					
					SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
					String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/user_status", nameValuePairs);
					
					if(result.equalsIgnoreCase(SimpleJSONParser.SERVER_TIMEOUT)){
						Log.e("timeout","=");
					}
					else{
						try{
							JSONObject jObject = new JSONObject(result);
							engagementStatus = jObject.getInt("log");
							
							if((1 == engagementStatus) || (2 == engagementStatus)){
								String engagementId = jObject.getString("engagement_id");
								String sessionId = jObject.getString("session_id");
								String userId = jObject.getString("user_id");
								String latitude = jObject.getString("latitude");
								String longitude = jObject.getString("longitude");
								String driverName = jObject.getString("driver_name");
								String driverImage = jObject.getString("driver_image");
								String driverCarImage = jObject.getString("driver_car_image");
								String driverPhone = jObject.getString("driver_phone");
								String driverRating = jObject.getString("driver_raitng");
								
								if(2 == engagementStatus){
									String pickupLatitude = jObject.getString("pickup_latitude");
									String pickupLongitude = jObject.getString("pickup_longitude");
								}
							}
							else{
								
							}
							
						} catch(Exception e){
							e.printStackTrace();
						}
					}
				}
			} catch(Exception e){
				e.printStackTrace();
			}
			
			// 0 for request, 1 for accepted,2 for started,3 for ended, 4 for rejected by driver, 5 for rejected by user,6 for timeout
			if(engagementStatus == 1){
				screenMode = Data.P_REQUEST_FINAL;
			}
			else if(engagementStatus == 2){
				screenMode = Data.P_IN_RIDE;
			}
			else if(engagementStatus == 3){
//				screenMode = Data.P_RIDE_END;
				screenMode = "";
			}
			else{
				screenMode = "";
			}
			
			
			if("".equalsIgnoreCase(screenMode)){
				HomeActivity.passengerScreenMode = PassengerScreenMode.P_INITIAL;
				clearSPData(context);
			}
			else{
				
				String SP_C_SESSION_ID = pref.getString(Data.SP_C_SESSION_ID, "");
				String SP_C_ENGAGEMENT_ID = pref.getString(Data.SP_C_ENGAGEMENT_ID, "");
				String SP_C_DRIVER_ID = pref.getString(Data.SP_C_DRIVER_ID, "");
				String SP_C_LATITUDE = pref.getString(Data.SP_C_LATITUDE, "0");
				String SP_C_LONGITUDE = pref.getString(Data.SP_C_LONGITUDE, "0");
				String SP_C_DRIVER_NAME = pref.getString(Data.SP_C_DRIVER_NAME, "");
				String SP_C_DRIVER_IMAGE = pref.getString(Data.SP_C_DRIVER_IMAGE, "");
				String SP_C_DRIVER_CAR_IMAGE = pref.getString(Data.SP_C_DRIVER_CAR_IMAGE, "");
				String SP_C_DRIVER_PHONE = pref.getString(Data.SP_C_DRIVER_PHONE, "");
				String SP_C_DRIVER_RATING = pref.getString(Data.SP_C_DRIVER_RATING, "");
				String SP_C_DRIVER_DISTANCE = pref.getString(Data.SP_C_DRIVER_DISTANCE, "");
				String SP_C_DRIVER_DURATION = pref.getString(Data.SP_C_DRIVER_DURATION, "");
				
				Data.cSessionId = SP_C_SESSION_ID;
				Data.cEngagementId = SP_C_ENGAGEMENT_ID;
				Data.cDriverId = SP_C_DRIVER_ID;
				
				double latitude = Double.parseDouble(SP_C_LATITUDE);
				double longitude = Double.parseDouble(SP_C_LONGITUDE);
				
				Data.assignedDriverInfo = new DriverInfo(SP_C_DRIVER_ID, latitude, longitude, SP_C_DRIVER_NAME, 
						SP_C_DRIVER_IMAGE, SP_C_DRIVER_CAR_IMAGE, SP_C_DRIVER_PHONE, SP_C_DRIVER_RATING);
				Log.e("Data.assignedDriverInfo on login","="+Data.assignedDriverInfo.latLng);
				Data.assignedDriverInfo.distanceToReach = SP_C_DRIVER_DISTANCE;
				Data.assignedDriverInfo.durationToReach = SP_C_DRIVER_DURATION;
				
				
				if(Data.P_REQUEST_FINAL.equalsIgnoreCase(screenMode)){
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_REQUEST_FINAL;
				}
				else if(Data.P_IN_RIDE.equalsIgnoreCase(screenMode)){
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_IN_RIDE;
					
					HomeActivity.totalDistance = Double.parseDouble(pref.getString(Data.SP_TOTAL_DISTANCE, "-1"));
					
					String lat = pref.getString(Data.SP_LAST_LATITUDE, "0");
					String lng = pref.getString(Data.SP_LAST_LONGITUDE, "0");
					
					Data.startRidePreviousLatLng = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
					
				}
				else if(Data.P_RIDE_END.equalsIgnoreCase(screenMode)){
					
					String SP_C_TOTAL_DISTANCE = pref.getString(Data.SP_C_TOTAL_DISTANCE, "0");
					String SP_C_TOTAL_FARE = pref.getString(Data.SP_C_TOTAL_FARE, "0");
					String SP_C_WAIT_TIME = pref.getString(Data.SP_C_WAIT_TIME, "0");
					
					Data.totalDistance = Double.parseDouble(SP_C_TOTAL_DISTANCE);
					Data.totalFare = Double.parseDouble(SP_C_TOTAL_FARE);
					Data.waitTime = SP_C_WAIT_TIME;
					
					
					HomeActivity.passengerScreenMode = PassengerScreenMode.P_RIDE_END;
					
				}
				
			}
			
		}
		
		
		
		
		
	}
	
	
	
	
//	public void fetchExceptionalDriver(String accessToken){
//
//		ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
//		nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
//		
//		SimpleJSONParser simpleJSONParser = new SimpleJSONParser();
//		String result = simpleJSONParser.getJSONFromUrlParams(Data.SERVER_URL + "/exceptional_driver", nameValuePairs);
//		
//		try{
//			//{"exceptional_driver":1} show Jugnoo ON
//			//{"exceptional_driver":0} show driver mode ON
//			if(result.equalsIgnoreCase(SimpleJSONParser.SERVER_TIMEOUT)){
//				Log.e("timeout","=");
//				HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
//			}
//			else{
//				try{
//					JSONObject jObject = new JSONObject(result);
//					int excepInt = jObject.getInt("exceptional_driver");
//					if(1 == excepInt){
//						HomeActivity.exceptionalDriver = ExceptionalDriver.YES;
//					}
//					else{
//						HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
//					}
//				} catch(Exception e){
//					e.printStackTrace();
//					HomeActivity.exceptionalDriver = ExceptionalDriver.NO;
//				}
//			}
//		} catch(Exception e){
//			e.printStackTrace();
//		}
//	}
	
	
	public void clearSPData(final Context context){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
        	
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
        		
        		
        		
        		
        		editor.putString(Data.SP_TOTAL_DISTANCE, "0");
        		editor.putString(Data.SP_WAIT_TIME, "0");
        		editor.putString(Data.SP_RIDE_TIME, "0");
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
        		editor.putString(Data.SP_C_DRIVER_DURATION, "0");
        		
        		editor.putString(Data.SP_C_TOTAL_DISTANCE, "0");
        		editor.putString(Data.SP_C_TOTAL_FARE, "0");
        		editor.putString(Data.SP_C_WAIT_TIME, "0");
        		
        	
        	editor.commit();
    		
        	
        	Database database = new Database(context);
			database.deleteSavedPath();
			database.close();
        	
		
			}
		}).start();
	}
	
	
	
	
}
