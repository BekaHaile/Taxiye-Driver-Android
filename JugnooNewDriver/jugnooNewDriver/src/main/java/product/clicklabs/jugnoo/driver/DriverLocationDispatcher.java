package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.location.Location;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.google.android.gms.maps.model.LatLng;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.util.ArrayList;

import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.HttpRequester;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;

public class DriverLocationDispatcher {

	public void sendLocationToServer(Context context, String filePrefix){
		
		double LOCATION_TOLERANCE = 0.0001;
		
		try {
			String userMode = Database2.getInstance(context).getUserMode();
			
			if(Database2.UM_DRIVER.equalsIgnoreCase(userMode)){
				
				PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag2");
				wakeLock.acquire();
				
				String accessToken = Database2.getInstance(context).getDLDAccessToken();
				String deviceToken = Database2.getInstance(context).getDLDDeviceToken();
				String serverUrl = Database2.getInstance(context).getDLDServerUrl();
				String pushyToken = Database2.getInstance(context).getPushyToken();

				Location location = Database2.getInstance(context).getDriverCurrentLocation();
				
				if((!"".equalsIgnoreCase(accessToken)) && (!"".equalsIgnoreCase(deviceToken)) && (!"".equalsIgnoreCase(serverUrl))){
					if((Math.abs(location.getLatitude()) > LOCATION_TOLERANCE) && (Math.abs(location.getLongitude()) > LOCATION_TOLERANCE)){
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
						nameValuePairs.add(new BasicNameValuePair("latitude", "" + location.getLatitude()));
						nameValuePairs.add(new BasicNameValuePair("longitude", "" + location.getLongitude()));
						nameValuePairs.add(new BasicNameValuePair("device_token", deviceToken));
						nameValuePairs.add(new BasicNameValuePair("location_accuracy",""+ location.getAccuracy()));
						nameValuePairs.add(new BasicNameValuePair("pushy_token", pushyToken));

			
						HttpRequester simpleJSONParser = new HttpRequester();
						String result = simpleJSONParser.getJSONFromUrlParams(serverUrl + "/update_driver_location", nameValuePairs);
									
						Log.e("equal_Low_acc2 result in DLD", "=" + result);
						Log.writeLogToFile(filePrefix, "Server result "+DateOperations.getCurrentTime()+" = "+result);
						
						try{
							//{"log":"Updated"}
							JSONObject jObj = new JSONObject(result);
							if(jObj.has("log")){
								String log = jObj.getString("log");
								if("Updated".equalsIgnoreCase(log)){
									Database2.getInstance(context).updateDriverLastLocationTime();
								}
							}
						} catch(Exception e){
							e.printStackTrace();
						}
						
						simpleJSONParser = null;
						nameValuePairs = null;
					}
				}


				if(Prefs.with(context).getInt(SPLabels.DRIVER_SCREEN_MODE, -1) == DriverScreenMode.D_ARRIVED.getOrdinal()){
					String pickupLatitude = Prefs.with(context).getString(SPLabels.DRIVER_C_PICKUP_LATITUDE, "");
					String pickupLongitude = Prefs.with(context).getString(SPLabels.DRIVER_C_PICKUP_LONGITUDE, "");
					String driverArrivedDistance = Prefs.with(context).getString(SPLabels.DRIVER_ARRIVED_DISTANCE, "100");

					if(!"".equalsIgnoreCase(pickupLatitude) && !"".equalsIgnoreCase(pickupLongitude)
						&& Math.abs(MapUtils.distance(new LatLng(location.getLatitude(), location.getLongitude()),
						new LatLng(Double.parseDouble(pickupLatitude), Double.parseDouble(pickupLongitude))))
						< Double.parseDouble(driverArrivedDistance)){

						if(HomeActivity.appInterruptHandler != null){
							HomeActivity.appInterruptHandler.markArrivedInterrupt(new LatLng(location.getLatitude(),
								location.getLongitude()));
						} else{
							String accessTokenA = Prefs.with(context).getString(SPLabels.DRIVER_ACCESS_TOKEN, "");
							String engagementId = Prefs.with(context).getString(SPLabels.DRIVER_ENGAGEMENT_ID, "");
							String customerId = Prefs.with(context).getString(SPLabels.DRIVER_CUSTOMER_ID, "");
							String referenceId = Prefs.with(context).getString(SPLabels.DRIVER_REFERENCE_ID, "");

							ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
							nameValuePairs.add(new BasicNameValuePair("access_token", accessTokenA));
							nameValuePairs.add(new BasicNameValuePair("engagement_id", engagementId));
							nameValuePairs.add(new BasicNameValuePair("customer_id", customerId));
							nameValuePairs.add(new BasicNameValuePair("pickup_latitude", ""+location.getLatitude()));
							nameValuePairs.add(new BasicNameValuePair("pickup_longitude", ""+location.getLongitude()));
							nameValuePairs.add(new BasicNameValuePair("reference_id", referenceId));

							HttpRequester simpleJSONParser = new HttpRequester();
							String result = simpleJSONParser.getJSONFromUrlParams(serverUrl + "/mark_arrived", nameValuePairs);
							simpleJSONParser = null;
							result = null;
						}
					}
				}

				wakeLock.release();
			}
			else{
				new DriverServiceOperations().stopService(context);
			}

			
		}
		catch (Exception e) {
			e.printStackTrace();
			Log.writeLogToFile(filePrefix, "Exception in sending to server "+DateOperations.getCurrentTime()+" = "+e);
		}
		finally{
    	}
	}
	
	
	
}
