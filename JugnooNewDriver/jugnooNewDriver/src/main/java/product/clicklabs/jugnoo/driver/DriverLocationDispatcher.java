package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.home.models.EngagementSPData;
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
						long freeStateTime = Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD, 110000);
						long acceptedStateTime = Prefs.with(context).getLong(Constants.ACCEPTED_STATE_UPDATE_TIME_PERIOD, 12000);

						long diff = System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.UPDATE_DRIVER_LOCATION_TIME, 0);
						if ((screenMode == DriverScreenMode.D_INITIAL.getOrdinal() && diff >= freeStateTime)
								|| (screenMode == DriverScreenMode.D_ARRIVED.getOrdinal() && diff >= acceptedStateTime)) {

							HashMap<String, String> nameValuePairs = new HashMap<>();
							nameValuePairs.put(Constants.KEY_ACCESS_TOKEN, accessToken);
							nameValuePairs.put(Constants.KEY_LATITUDE, String.valueOf(location.getLatitude()));
							nameValuePairs.put(Constants.KEY_LONGITUDE, String.valueOf(location.getLongitude()));
							nameValuePairs.put(Constants.KEY_BEARING, String.valueOf(location.getBearing()));
							nameValuePairs.put(Constants.KEY_DEVICE_TOKEN, deviceToken);
							nameValuePairs.put(Constants.KEY_LOCATION_ACCURACY, String.valueOf(location.getAccuracy()));
							nameValuePairs.put(Constants.KEY_APP_VERSION, String.valueOf(Utils.getAppVersion(context)));

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
						}
					}
				}

				checkForMarkArrived(context, location, accessToken);

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
	


	private void checkForMarkArrived(Context context, Location location, String accessToken){
		try{
			String driverArrivedDistance = Prefs.with(context).getString(SPLabels.DRIVER_ARRIVED_DISTANCE, "100");
			ArrayList<EngagementSPData> engagementSPDatas = (ArrayList<EngagementSPData>) MyApplication.getInstance()
					.getEngagementSP().getEngagementSPDatasArray();
			for(EngagementSPData engagementSPData : engagementSPDatas){
				try {
					if(engagementSPData.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()) {
						double distance = Math.abs(MapUtils.distance(new LatLng(location.getLatitude(), location.getLongitude()),
								new LatLng(engagementSPData.getPickupLatitude(), engagementSPData.getPickupLongitude())));
						if (distance < Double.parseDouble(driverArrivedDistance)){
							if(HomeActivity.appInterruptHandler != null){
								HomeActivity.appInterruptHandler.markArrivedInterrupt(new LatLng(location.getLatitude(),
										location.getLongitude()), engagementSPData.getEngagementId());
							} else{
								HashMap<String, String> nameValuePairs = new HashMap<>();
								nameValuePairs.put(Constants.KEY_ACCESS_TOKEN, accessToken);
								nameValuePairs.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(engagementSPData.getEngagementId()));
								nameValuePairs.put(Constants.KEY_CUSTOMER_ID, String.valueOf(engagementSPData.getCustomerId()));
								nameValuePairs.put(Constants.KEY_PICKUP_LATITUDE, String.valueOf(location.getLatitude()));
								nameValuePairs.put(Constants.KEY_PICKUP_LONGITUDE, String.valueOf(location.getLongitude()));
								nameValuePairs.put(Constants.KEY_REFERENCE_ID, String.valueOf(engagementSPData.getReferenceId()));

								RestClient.getApiServices().driverMarkArriveSync(nameValuePairs);
								engagementSPData.setStatus(EngagementStatus.ARRIVED.getOrdinal());
								MyApplication.getInstance().getEngagementSP().updateEngagementSPData(engagementSPData);
							}
							break;
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	
}
