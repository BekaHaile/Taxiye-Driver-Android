package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.CustomerInfo;
import product.clicklabs.jugnoo.driver.datastructure.EngagementStatus;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.home.StartRideLocationUpdateService;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.services.FetchDataUsageService;
import product.clicklabs.jugnoo.driver.utils.FlurryEventLogger;
import product.clicklabs.jugnoo.driver.utils.FlurryEventNames;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.MapUtils;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.RetrofitError;
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
				WakeLock wakeLock = null;
				if (powerManager != null) {
					wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, BuildConfig.FLAVOR+":MyWakelockTag2");
					wakeLock.acquire(5000);
				}

				String accessToken = JSONParser.getAccessTokenPair(context).first;

				String pushyToken = "";

				Location location = Database2.getInstance(context).getDriverCurrentLocation(context);
				
				if(!"".equalsIgnoreCase(accessToken)){
					if((Math.abs(location.getLatitude()) > LOCATION_TOLERANCE) && (Math.abs(location.getLongitude()) > LOCATION_TOLERANCE)){
						long freeStateTime = Prefs.with(context).getLong(Constants.FREE_STATE_UPDATE_TIME_PERIOD, 110000)/2L;
						long acceptedStateTime = Prefs.with(context).getLong(Constants.ACCEPTED_STATE_UPDATE_TIME_PERIOD, 12000)/2L;

						long diff = System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.UPDATE_DRIVER_LOCATION_TIME, 0);
						Database2.getInstance(context).insertUSLLog(Constants.EVENT_DLD_LOC_RECEIVED);

						//to check if any customer is engaged with driver
						ArrayList<CustomerInfo> customerInfos = Data.getAssignedCustomerInfosListForEngagedStatus();

						//to check if any engaged customer is in accepted or arrived state
						boolean isAnyCustomerBeforeStart = false;
						for(CustomerInfo customerInfo : customerInfos){
							if(customerInfo.getStatus() == EngagementStatus.ACCEPTED.getKOrdinal()
									|| customerInfo.getStatus() == EngagementStatus.ARRIVED.getKOrdinal()){
								isAnyCustomerBeforeStart = true;
								break;
							}
						}

						if (((customerInfos.size() == 0) && diff >= freeStateTime)
								|| (isAnyCustomerBeforeStart && diff >= acceptedStateTime)) {

							HashMap<String, String> nameValuePairs = new HashMap<>();
							nameValuePairs.put(Constants.KEY_ACCESS_TOKEN, accessToken);
							nameValuePairs.put(Constants.KEY_LATITUDE, String.valueOf(location.getLatitude()));
							nameValuePairs.put(Constants.KEY_LONGITUDE, String.valueOf(location.getLongitude()));
							nameValuePairs.put(Constants.KEY_BEARING, String.valueOf(location.getBearing()));
							FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
								@Override
								public void onComplete(@NonNull Task<InstanceIdResult> task) {
									if(!task.isSuccessful()) {
										Log.w("Driverlocationdispatcher","device_token_unsuccessful - onReceive",task.getException());
										return;
									}
									if(task.getResult() != null) {
										Log.i(SplashNewActivity.DEVICE_TOKEN_TAG + "Driverlocationdispatcher send location", task.getResult().getToken());
										nameValuePairs.put(Constants.KEY_DEVICE_TOKEN, task.getResult().getToken());
									}
									new Thread(new Runnable() {
										@Override
										public void run() {
											sendLocationToServer(context, responseTime, pushyToken, location, nameValuePairs);
										}
									}).start();
								}
							});
						} else {
							Database2.getInstance(context).insertUSLLog(Constants.EVENT_DLD_LOC_REJECTED_TIME_DIFF);
						}
					}
				}
				checkForMarkArrived(context, location, accessToken);
				if (wakeLock != null) {
					wakeLock.release();
				}
			}
			else{

				context.stopService(new Intent(context.getApplicationContext(), DriverLocationUpdateService.class));
			}
		}
		catch (RetrofitError retrofitError){
			try {
				Database2.getInstance(context).insertUSLLog(Constants.EVENT_DLD_LOC_FAILED_RETRO);
				long diff1 = System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.UPDATE_DRIVER_LOCATION_TIME, 0);
				if(diff1 > Prefs.with(context).getLong(Constants.DRIVER_OFFLINE_PERIOD, 0)) {
					Prefs.with(context).save(SPLabels.GET_USL_STATUS, true);
					Intent refreshUSL = new Intent(Constants.ACTION_REFRESH_USL);
					context.sendBroadcast(refreshUSL);
				}
				Intent intent1 = new Intent(context, FetchDataUsageService.class);
				intent1.putExtra("task_id", "1");
				context.startService(intent1);
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		catch (Exception e) {
			try {
				Database2.getInstance(context).insertUSLLog(Constants.EVENT_DLD_LOC_FAILED_GENERIC);
				long diff2 = System.currentTimeMillis() - Prefs.with(context).getLong(SPLabels.UPDATE_DRIVER_LOCATION_TIME, 0);
				if(diff2 > Prefs.with(context).getLong(Constants.DRIVER_OFFLINE_PERIOD, 0)) {
					Prefs.with(context).save(SPLabels.GET_USL_STATUS, true);
					Intent refreshUSL = new Intent(Constants.ACTION_REFRESH_USL);
					context.sendBroadcast(refreshUSL);
				}
				Intent intent1 = new Intent(context, FetchDataUsageService.class);
				intent1.putExtra("task_id", "1");
				context.startService(intent1);
				e.printStackTrace();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}

	private void sendLocationToServer(Context context, long responseTime, String pushyToken, Location location, HashMap<String, String> nameValuePairs) {
		nameValuePairs.put("pushy_token", pushyToken);
		nameValuePairs.put("battery_percentage", String.valueOf(Utils.getActualBatteryPer(context)));
		HomeUtil.putDefaultParams(nameValuePairs);
		if(Double.parseDouble(Utils.getActualBatteryPer(context)) < 20d && Utils.isBatteryChargingNew(context) == 0){
			Intent batteryLow = new Intent(Constants.ALERT_BATTERY_LOW);
			context.sendBroadcast(batteryLow);
		}
		nameValuePairs.put("is_charging", String.valueOf(Utils.isBatteryChargingNew(context)));
		if(Prefs.with(context).getBoolean(Constants.MOBILE_DATA_STATE, true)) {
			nameValuePairs.put("mobile_data_state", String.valueOf(1));
		}else {
			nameValuePairs.put("mobile_data_state", String.valueOf(0));
		}
		if(Prefs.with(context).getBoolean(Constants.POWER_OFF_INITIATED, false)) {
			nameValuePairs.put("power_off_state", String.valueOf(1));
		} else {
			nameValuePairs.put("power_off_state", String.valueOf(0));
		}
		nameValuePairs.put(Constants.KEY_LOCATION_ACCURACY, String.valueOf(location.getAccuracy()));
		nameValuePairs.put(Constants.KEY_APP_VERSION, String.valueOf(Utils.getAppVersion(context)));

		Log.i(TAG, "sendLocationToServer nameValuePairs=" + nameValuePairs.toString());


		try {
			Response response = RestClient.getApiServices().updateDriverLocation(nameValuePairs);
			String result = new String(((TypedByteArray) response.getBody()).getBytes());
			Log.i(TAG, "sendLocationToServer result=" + result);

			//{"log":"Updated"}
			JSONObject jObj = new JSONObject(result);
			if (jObj.has("log")) {
				String log = jObj.getString("log");
				if ("Updated".equalsIgnoreCase(log)) {
					Database2.getInstance(context).insertUSLLog(Constants.EVENT_DLD_LOC_SENT);
					Prefs.with(context).save(Constants.MOBILE_DATA_STATE, true);
					Prefs.with(context).save(Constants.POWER_OFF_INITIATED, false);
					Database2.getInstance(context).updateDriverLastLocationTime();
					Prefs.with(context).save(SPLabels.UPDATE_DRIVER_LOCATION_TIME, System.currentTimeMillis());
					Intent intent1 = new Intent(context, FetchDataUsageService.class);
					intent1.putExtra("task_id", "2");
					context.startService(intent1);
					Prefs.with(context).save(SPLabels.GET_USL_STATUS, false);
					Intent refreshUSL = new Intent(Constants.ACTION_REFRESH_USL);
					context.sendBroadcast(refreshUSL);
					FlurryEventLogger.logResponseTime(context, System.currentTimeMillis() - responseTime, FlurryEventNames.UPDATE_DRIVER_LOC_RESPONSE);
				}
			}

			int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
			if (ApiResponseFlags.RESET_DEVICE_TOKEN.getOrdinal() == flag) {
				sendLocationToServer(context);
				Database2.getInstance(context).insertUSLLog(Constants.EVENT_DLD_DEVICE_TOKEN_RESET);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	private void checkForMarkArrived(Context context, Location location, String accessToken){
		try{
			double driverArrivedDistance = Prefs.with(context).getInt(Constants.KEY_DRIVER_ARRIVED_DISTANCE, 100);
			double arrivingDistance = Prefs.with(context).getInt(Constants.KEY_DRIVER_SHOW_ARRIVE_UI_DISTANCE, 600);
			ArrayList<CustomerInfo> customerInfos = Data.getAssignedCustomerInfosListForEngagedStatus();
			if(customerInfos.size() == 0){
				return;
			}
			for(CustomerInfo customerInfo : customerInfos){
				try {
					if(customerInfo.getStatus() == EngagementStatus.ACCEPTED.getOrdinal()) {
						double distance = Math.abs(MapUtils.distance(new LatLng(location.getLatitude(), location.getLongitude()),
								customerInfo.getRequestlLatLng()));

						if (distance < driverArrivedDistance){
							if(HomeActivity.appInterruptHandler != null){
								customerInfo.setStatus(EngagementStatus.ARRIVED.getOrdinal());
								HomeActivity.appInterruptHandler.markArrivedInterrupt(new LatLng(location.getLatitude(),
										location.getLongitude()), customerInfo.getEngagementId());
							} else{
								HashMap<String, String> nameValuePairs = new HashMap<>();
								nameValuePairs.put(Constants.KEY_ACCESS_TOKEN, accessToken);
								nameValuePairs.put(Constants.KEY_ENGAGEMENT_ID, String.valueOf(customerInfo.getEngagementId()));
								nameValuePairs.put(Constants.KEY_CUSTOMER_ID, String.valueOf(customerInfo.getUserId()));
								nameValuePairs.put(Constants.KEY_PICKUP_LATITUDE, String.valueOf(location.getLatitude()));
								nameValuePairs.put(Constants.KEY_PICKUP_LONGITUDE, String.valueOf(location.getLongitude()));
								nameValuePairs.put(Constants.KEY_REFERENCE_ID, String.valueOf(customerInfo.getReferenceId()));
								HomeUtil.putDefaultParams(nameValuePairs);

								RestClient.getApiServices().driverMarkArriveSync(nameValuePairs);
								customerInfo.setStatus(EngagementStatus.ARRIVED.getOrdinal());
								Log.e("DriverLocationDispatcher", "checkForMarkArrived");
								Data.saveAssignedCustomers();

								Database2.getInstance(context).insertRideData(context, "0.0", "0.0", "" + System.currentTimeMillis(), customerInfo.getEngagementId());

								Prefs.with(context).save(Constants.FLAG_REACHED_PICKUP, true);
								Prefs.with(context).save(Constants.PLAY_START_RIDE_ALARM, true);
								Prefs.with(context).save(Constants.PLAY_START_RIDE_ALARM_FINALLY, false);
								Intent intent1 = new Intent(context, StartRideLocationUpdateService.class);
								context.startService(intent1);
								Log.e("startRideAlarmSErvice", "on");
								Log.writePathLogToFile(context, customerInfo.getEngagementId() + "m", "arrived sucessful");
							}
						} else if(distance < arrivingDistance){
							if(HomeActivity.appInterruptHandler != null){
								HomeActivity.appInterruptHandler.notifyArrivedButton(true, customerInfo.getEngagementId());
							}
						} else {
							if(HomeActivity.appInterruptHandler != null){
								HomeActivity.appInterruptHandler.notifyArrivedButton(false, customerInfo.getEngagementId());							}
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
