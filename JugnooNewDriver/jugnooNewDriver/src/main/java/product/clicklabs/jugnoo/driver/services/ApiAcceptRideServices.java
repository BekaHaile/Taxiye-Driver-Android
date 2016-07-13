package product.clicklabs.jugnoo.driver.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import org.json.JSONObject;
import java.util.HashMap;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.GCMIntentService;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

public class ApiAcceptRideServices extends IntentService {

	public ApiAcceptRideServices() {
		this("ApiAcceptRideServices");
	}

	public ApiAcceptRideServices(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		try {
			Location location = Database2.getInstance(ApiAcceptRideServices.this).getDriverCurrentLocation(ApiAcceptRideServices.this);
			String accessToken = Database2.getInstance(ApiAcceptRideServices.this).getDLDAccessToken();

			String engagementId = intent.getStringExtra("engagement_id");
			String customerId = intent.getStringExtra("user_id");
			int referenceId = intent.getIntExtra("referrence_id", 0);
			Log.i("accceptRide Logs",""+location+" "+accessToken+" "+engagementId+" "+customerId+" "+referenceId);
			if (!"".equalsIgnoreCase(accessToken)) {
				acceptRide(accessToken, customerId, engagementId, referenceId, location.getLatitude(), location.getLongitude());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("acceptRideError",""+e);
		}
	}

	public void acceptRide(String accessToken, final String customerId, final String engagementId,
						   final int referenceId, final double latitude, final double longitude) {
		try {
			if (AppStatus.getInstance(ApiAcceptRideServices.this).isOnline(ApiAcceptRideServices.this)) {
				if (Utils.getBatteryPercentage(ApiAcceptRideServices.this) >= 20) {
					GCMIntentService.clearNotifications(ApiAcceptRideServices.this);
					GCMIntentService.stopRing(true);


					HashMap<String, String> params = new HashMap<String, String>();

					params.put("access_token", accessToken);
					params.put("customer_id", customerId);
					params.put("engagement_id", engagementId);
					params.put("latitude", String.valueOf(latitude));
					params.put("longitude", String.valueOf(longitude));

					params.put("device_name", Utils.getDeviceName());
					params.put("imei", DeviceUniqueID.getUniqueId(ApiAcceptRideServices.this));
					params.put("app_version", "" + Utils.getAppVersion(ApiAcceptRideServices.this));
					params.put("is_accepting_perfect_ride", "1");

					if (!"".equalsIgnoreCase(String.valueOf(referenceId))) {
						params.put("reference_id", String.valueOf(referenceId));
					}
					Log.i("request", String.valueOf(params));
					Response response = RestClient.getApiServices().driverAcceptRideSync(params);
					Log.i("acceptRideResp", String.valueOf(response));
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						Log.i("acceptRideRespString", jsonString);
						JSONObject jObj = new JSONObject(jsonString);
						int flag = ApiResponseFlags.RIDE_ACCEPTED.getOrdinal();

						if (jObj.has("flag")) {
							flag = jObj.getInt("flag");
						}
						if (ApiResponseFlags.RIDE_ACCEPTED.getOrdinal() == flag) {
							try {
								Intent collapseNotification = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
								ApiAcceptRideServices.this.sendBroadcast(collapseNotification);
							} catch (Exception e) {
								e.printStackTrace();
							}
							Intent splashActivity = new Intent(ApiAcceptRideServices.this, SplashNewActivity.class);
							splashActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
							startActivity(splashActivity);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}
