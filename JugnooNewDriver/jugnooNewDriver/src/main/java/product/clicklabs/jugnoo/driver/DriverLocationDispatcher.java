package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.location.Location;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.DeviceTokenGenerator;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class DriverLocationDispatcher {

	private final String TAG = DriverLocationDispatcher.class.getSimpleName();

	public void sendLocationToServer(Context context){
		
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

						HashMap<String, String> nameValuePairs = new HashMap<>();
						nameValuePairs.put("access_token", accessToken);
						nameValuePairs.put("latitude", "" + location.getLatitude());
						nameValuePairs.put("longitude", "" + location.getLongitude());
						nameValuePairs.put("bearing", "" + location.getBearing());
						nameValuePairs.put("device_token", deviceToken);
						nameValuePairs.put("location_accuracy", "" + location.getAccuracy());
						nameValuePairs.put("pushy_token", pushyToken);
						nameValuePairs.put("app_version", String.valueOf(Utils.getAppVersion(context)));

						Log.i(TAG, "sendLocationToServer nameValuePairs="+nameValuePairs.toString());


						Response response = RestClient.getApiServices().updateDriverLocation(nameValuePairs);
						String result = new String(((TypedByteArray)response.getBody()).getBytes());

						Log.i(TAG, "sendLocationToServer result=" + result);

						try{
							//{"log":"Updated"}
							JSONObject jObj = new JSONObject(result);
							if(jObj.has("log")){
								String log = jObj.getString("log");
								if("Updated".equalsIgnoreCase(log)){
									Database2.getInstance(context).updateDriverLastLocationTime();
								}
							}

							int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
							if(ApiResponseFlags.RESET_DEVICE_TOKEN.getOrdinal() == flag){
								String deviceTokenNew = new DeviceTokenGenerator().forceGenerateDeviceToken(context);
								Database2.getInstance(context).insertDriverLocData(accessToken, deviceTokenNew, serverUrl);
								sendLocationToServer(context);
							}
						} catch(Exception e){
							e.printStackTrace();
						}
						
						nameValuePairs = null;
					}
				}


				if(Prefs.with(context).getInt(SPLabels.DRIVER_SCREEN_MODE, -1) == DriverScreenMode.D_ARRIVED.getOrdinal()){
					String pickupLatitude = Prefs.with(context).getString(SPLabels.DRIVER_C_PICKUP_LATITUDE, "");
					String pickupLongitude = Prefs.with(context).getString(SPLabels.DRIVER_C_PICKUP_LONGITUDE, "");
					String driverArrivedDistance = Prefs.with(context).getString(SPLabels.DRIVER_ARRIVED_DISTANCE, "100");

					double distance = Math.abs(MapUtils.distance(new LatLng(location.getLatitude(), location.getLongitude()),
							new LatLng(Double.parseDouble(pickupLatitude), Double.parseDouble(pickupLongitude))));

					if (!"".equalsIgnoreCase(pickupLatitude) && !"".equalsIgnoreCase(pickupLongitude)
						&& Math.abs(MapUtils.distance(new LatLng(location.getLatitude(), location.getLongitude()),
						new LatLng(Double.parseDouble(pickupLatitude), Double.parseDouble(pickupLongitude))))
						< Double.parseDouble(driverArrivedDistance)){

						Prefs.with(context).save(SPLabels.DRIVER_SCREEN_MODE, -1);

						if(HomeActivity.appInterruptHandler != null){
							HomeActivity.appInterruptHandler.markArrivedInterrupt(new LatLng(location.getLatitude(),
								location.getLongitude()));
						} else{
							String accessTokenA = Prefs.with(context).getString(SPLabels.DRIVER_ACCESS_TOKEN, "");
							String engagementId = Prefs.with(context).getString(SPLabels.DRIVER_ENGAGEMENT_ID, "");
							String customerId = Prefs.with(context).getString(SPLabels.DRIVER_CUSTOMER_ID, "");
							String referenceId = Prefs.with(context).getString(SPLabels.DRIVER_REFERENCE_ID, "");

							HashMap<String, String> nameValuePairs = new HashMap<>();
							nameValuePairs.put("access_token", accessTokenA);
							nameValuePairs.put("engagement_id", engagementId);
							nameValuePairs.put("customer_id", customerId);
							nameValuePairs.put("pickup_latitude", "" + location.getLatitude());
							nameValuePairs.put("pickup_longitude", "" + location.getLongitude());
							nameValuePairs.put("reference_id", referenceId);

							Response response = RestClient.getApiServices().driverMarkArriveSync(nameValuePairs);

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
		}
	}
	
	
	
}
