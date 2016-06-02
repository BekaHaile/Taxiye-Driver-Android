package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.content.Intent;
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
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
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
			String driverServiceRun = Database2.getInstance(context).getDriverServiceRun();
			long responseTime = System.currentTimeMillis();
			if(Database2.YES.equalsIgnoreCase(driverServiceRun)){
				
				PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
				WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyWakelockTag2");
				wakeLock.acquire();
				
				String accessToken = Database2.getInstance(context).getDLDAccessToken();
				String deviceToken = Database2.getInstance(context).getDLDDeviceToken();
				String serverUrl = Database2.getInstance(context).getDLDServerUrl();

				Location location = Database2.getInstance(context).getDriverCurrentLocation(context);
				
				if((!"".equalsIgnoreCase(accessToken)) && (!"".equalsIgnoreCase(deviceToken)) && (!"".equalsIgnoreCase(serverUrl))){
					if((Math.abs(location.getLatitude()) > LOCATION_TOLERANCE) && (Math.abs(location.getLongitude()) > LOCATION_TOLERANCE)){
						int screenMode = Prefs.with(context).getInt(SPLabels.DRIVER_SCREEN_MODE,
								DriverScreenMode.D_INITIAL.getOrdinal());
						long freeStateTime = Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD, 120000);
						long acceptedStateTime = Prefs.with(context).getLong(Constants.ACCEPTED_STATE_UPDATE_TIME_PERIOD, 15000);

						long diff = System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.UPDATE_DRIVER_LOCATION_TIME, 0);
						if ((screenMode == DriverScreenMode.D_INITIAL.getOrdinal() && diff >= freeStateTime)
								|| ((screenMode == DriverScreenMode.D_REQUEST_ACCEPT.getOrdinal()
									|| screenMode == DriverScreenMode.D_ARRIVED.getOrdinal()
									|| screenMode == DriverScreenMode.D_START_RIDE.getOrdinal())
									&& diff >= acceptedStateTime)) {


							HashMap<String, String> nameValuePairs = new HashMap<>();
							nameValuePairs.put("access_token", accessToken);
							nameValuePairs.put("latitude", "" + location.getLatitude());
							nameValuePairs.put("longitude", "" + location.getLongitude());
							nameValuePairs.put("bearing", "" + location.getBearing());
							nameValuePairs.put("device_token", deviceToken);
							nameValuePairs.put("location_accuracy", "" + location.getAccuracy());
							nameValuePairs.put("app_version", String.valueOf(Utils.getAppVersion(context)));

							Log.i(TAG, "sendLocationToServer nameValuePairs=" + nameValuePairs.toString());

							RestClient.setupRestClient(serverUrl);

							Response response = RestClient.getApiServices().updateDriverLocation(nameValuePairs);
							String result = new String(((TypedByteArray) response.getBody()).getBytes());

							Log.i(TAG, "sendLocationToServer result=" + result);

							try {
								//{"log":"Updated"}
								JSONObject jObj = new JSONObject(result);
								if (jObj.has("log")) {
									String log = jObj.getString("log");
									if ("Updated".equalsIgnoreCase(log)) {
										Database2.getInstance(context).updateDriverLastLocationTime();
										Prefs.with(context).save(SPLabels.UPDATE_DRIVER_LOCATION_TIME, System.currentTimeMillis());
										FlurryEventLogger.logResponseTime(context, System.currentTimeMillis() - responseTime, FlurryEventNames.UPDATE_DRIVER_LOC_RESPONSE);
									}
								}

								int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
								if (ApiResponseFlags.RESET_DEVICE_TOKEN.getOrdinal() == flag) {
									String deviceTokenNew = new DeviceTokenGenerator(context).forceGenerateDeviceToken(context);
									Database2.getInstance(context).insertDriverLocData(accessToken, deviceTokenNew, serverUrl);
									sendLocationToServer(context);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							nameValuePairs = null;
						}
					}
				}


				if(Prefs.with(context).getInt(SPLabels.DRIVER_SCREEN_MODE,
						DriverScreenMode.D_INITIAL.getOrdinal()) == DriverScreenMode.D_ARRIVED.getOrdinal()){
					String pickupLatitude = Prefs.with(context).getString(SPLabels.DRIVER_C_PICKUP_LATITUDE, "");
					String pickupLongitude = Prefs.with(context).getString(SPLabels.DRIVER_C_PICKUP_LONGITUDE, "");
					String driverArrivedDistance = Prefs.with(context).getString(SPLabels.DRIVER_ARRIVED_DISTANCE, "100");

					double distance = Math.abs(MapUtils.distance(new LatLng(location.getLatitude(), location.getLongitude()),
							new LatLng(Double.parseDouble(pickupLatitude), Double.parseDouble(pickupLongitude))));

					if (!"".equalsIgnoreCase(pickupLatitude) && !"".equalsIgnoreCase(pickupLongitude)
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

							HashMap<String, String> nameValuePairs = new HashMap<>();
							nameValuePairs.put("access_token", accessTokenA);
							nameValuePairs.put("engagement_id", engagementId);
							nameValuePairs.put("customer_id", customerId);
							nameValuePairs.put("pickup_latitude", "" + location.getLatitude());
							nameValuePairs.put("pickup_longitude", "" + location.getLongitude());
							nameValuePairs.put("reference_id", referenceId);

							Response response = RestClient.getApiServices().driverMarkArriveSync(nameValuePairs);

						}
						Prefs.with(context).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_START_RIDE.getOrdinal());
					}
				}

				wakeLock.release();
			}
			else{
				context.stopService(new Intent(context, DriverLocationUpdateService.class));
			}

			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	
}
