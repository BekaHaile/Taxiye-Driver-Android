package product.clicklabs.jugnoo.driver;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.utils.DateOperations;
import product.clicklabs.jugnoo.driver.utils.HttpRequester;
import product.clicklabs.jugnoo.driver.utils.Log;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.google.android.gms.maps.model.LatLng;

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
				
				if((!"".equalsIgnoreCase(accessToken)) && (!"".equalsIgnoreCase(deviceToken)) && (!"".equalsIgnoreCase(serverUrl))){
					LatLng latLng = Database2.getInstance(context).getDriverCurrentLocation();
					if((Math.abs(latLng.latitude) > LOCATION_TOLERANCE) && (Math.abs(latLng.longitude) > LOCATION_TOLERANCE)){
						ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
						nameValuePairs.add(new BasicNameValuePair("access_token", accessToken));
						nameValuePairs.add(new BasicNameValuePair("latitude", "" + latLng.latitude));
						nameValuePairs.add(new BasicNameValuePair("longitude", "" + latLng.longitude));
						nameValuePairs.add(new BasicNameValuePair("device_token", deviceToken));
			
						HttpRequester simpleJSONParser = new HttpRequester();
						String result = simpleJSONParser.getJSONFromUrlParams(serverUrl + "/update_driver_location", nameValuePairs);
									
//						Log.e("result in DLD", "=" + result);
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
