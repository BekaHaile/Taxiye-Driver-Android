package product.clicklabs.jugnoo.driver.apis;

import android.app.Activity;
import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.GCMIntentService;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 10/03/16.
 */
public class ApiAcceptRide {

	private Activity activity;
	private Callback callback;

	public ApiAcceptRide(){
	}

	public ApiAcceptRide(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void acceptRide(String accessToken, final String customerId, final String engagementId,
						   final String referenceId, final double latitude, final double longitude, final int perfectRide){
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				if (Utils.getBatteryPercentage(activity) >= 20) {
					GCMIntentService.clearNotifications(activity);
					GCMIntentService.stopRing(true);

					DialogPopup.showLoadingDialog(activity, activity.getResources().getString(R.string.loading));

					HashMap<String, String> params = new HashMap<String, String>();

					params.put("access_token", accessToken);
					params.put("customer_id", customerId);
					params.put("engagement_id", engagementId);
					params.put("latitude", String.valueOf(latitude));
					params.put("longitude", String.valueOf(longitude));

					params.put("device_name", Utils.getDeviceName());
					params.put("imei", DeviceUniqueID.getUniqueId(activity));
					params.put("app_version", "" + Utils.getAppVersion(activity));
					params.put("is_accepting_perfect_ride", String.valueOf(perfectRide));

					if (!"".equalsIgnoreCase(referenceId)) {
						params.put("reference_id", referenceId);
					}
					Log.i("request", String.valueOf(params));
					RestClient.getApiServices().driverAcceptRideRetro(params, new retrofit.Callback<RegisterScreenResponse>() {
						@Override
						public void success(RegisterScreenResponse registerScreenResponse, Response response) {
							try {
								String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
								JSONObject jObj = new JSONObject(jsonString);
								int flag = ApiResponseFlags.RIDE_ACCEPTED.getOrdinal();

								if (jObj.has("flag")) {
									flag = jObj.getInt("flag");
								}
								if (ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
									Prefs.with(activity).save(SPLabels.PERFECT_ACCEPT_RIDE_DATA, jsonString);
									perfectRideVariables(activity, customerId, engagementId, referenceId, latitude, longitude);
									double pickupLatitude = jObj.getDouble("pickup_latitude");
									double pickupLongitude = jObj.getDouble("pickup_longitude");
									jObj =  jObj.getJSONObject("user_data");
									String customerName = jObj.getString("user_name");
									Prefs.with(activity).save(SPLabels.PERFECT_CUSTOMER_CONT, jObj.getString("phone_no"));
									LatLng pickupLatLng = new LatLng(pickupLatitude, pickupLongitude);
									Prefs.with(activity).save(SPLabels.ACCEPT_RIDE_TIME, System.currentTimeMillis());
									callback.onSuccess(pickupLatLng, customerName);
								}

								DialogPopup.dismissLoadingDialog();
							} catch (Exception e) {
								e.printStackTrace();
								DialogPopup.dismissLoadingDialog();
							}
						}

						@Override
						public void failure(RetrofitError error) {
							DialogPopup.dismissLoadingDialog();
						}
					});
				} else {
					DialogPopup.alertPopup(activity, "", activity.getResources().getString(R.string.battery_low_message));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}


	public interface Callback{
		void onSuccess(LatLng pickupLatLng, String customerName);
		void onFailure();
	}

	public void perfectRideVariables(Context activity, String customerId, String engagementId, String referenceId, double latitude, double longitude){
			Prefs.with(activity).save(SPLabels.PERFECT_ENGAGEMENT_ID, engagementId);
			Prefs.with(activity).save(SPLabels.PERFECT_CUSTOMER_ID, customerId);
			Prefs.with(activity).save(SPLabels.PERFECT_REFERENCE_ID, referenceId);
			Prefs.with(activity).save(SPLabels.PERFECT_LATITUDE, String.valueOf(latitude));
			Prefs.with(activity).save(SPLabels.PERFECT_LONGITUDE, String.valueOf(longitude));
	}

}
