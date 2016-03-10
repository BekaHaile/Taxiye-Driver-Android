package product.clicklabs.jugnoo.driver.apis;

import android.app.Activity;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.GCMIntentService;
import product.clicklabs.jugnoo.driver.R;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 10/03/16.
 */
public class ApiAcceptRide {

	private Activity activity;
	private Callback callback;

	public ApiAcceptRide(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void acceptRide(String accessToken, String customerId, String engagementId, String referenceId, double latitude, double longitude){
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {
				if (Utils.getBatteryPercentage(activity) >= 20) {
					GCMIntentService.clearNotifications(activity);
					GCMIntentService.stopRing(true);

					DialogPopup.showLoadingDialog(activity, "Loading...");

					HashMap<String, String> params = new HashMap<String, String>();

					params.put("access_token", accessToken);
					params.put("customer_id", customerId);
					params.put("engagement_id", engagementId);
					params.put("latitude", String.valueOf(latitude));
					params.put("longitude", String.valueOf(longitude));

					params.put("device_name", Utils.getDeviceName());
					params.put("imei", DeviceUniqueID.getUniqueId(activity));
					params.put("app_version", "" + Utils.getAppVersion(activity));

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
								if (ApiResponseFlags.PERFECT_RIDE_ACCEPTED.getOrdinal() == jObj.optInt("flag", 0)) {
									double pickupLatitude = jObj.getDouble("pickup_latitude");
									double pickupLongitude = jObj.getDouble("pickup_longitude");
									LatLng pickupLatLng = new LatLng(pickupLatitude, pickupLongitude);
									callback.onSuccess(pickupLatLng);
								}
							} catch (Exception e) {
								e.printStackTrace();
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
		void onSuccess(LatLng pickupLatLng);
	}

}
