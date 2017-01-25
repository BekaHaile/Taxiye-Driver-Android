package product.clicklabs.jugnoo.driver.services;

import android.app.IntentService;
import android.content.Intent;
import android.location.Location;
import org.json.JSONObject;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.BlankActivityForDialog;
import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.GCMIntentService;
import product.clicklabs.jugnoo.driver.SharingRidesActivity;
import product.clicklabs.jugnoo.driver.SplashNewActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DeviceUniqueID;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
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
			int isPooled = intent.getIntExtra(Constants.KEY_IS_POOLED, 0);
			int isDelivery = intent.getIntExtra(Constants.KEY_IS_DELIVERY, 0);
			int isDeliveryPool = intent.getIntExtra(Constants.KEY_IS_DELIVERY_POOL, 0);

			Log.i("accceptRide Logs",""+location+" "+accessToken+" "+engagementId+" "+customerId+" "+referenceId);
			if (!"".equalsIgnoreCase(accessToken)) {
				acceptRide(accessToken, customerId, engagementId, referenceId, location.getLatitude(), location.getLongitude(),
						isPooled, isDelivery, isDeliveryPool);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.i("acceptRideError",""+e);
		}
	}

	public void acceptRide(String accessToken, final String customerId, final String engagementId,
						   final int referenceId, final double latitude, final double longitude,
						   final int isPooled, final int isDelivery, final int isDeliveryPool) {
		try {
			if (AppStatus.getInstance(ApiAcceptRideServices.this).isOnline(ApiAcceptRideServices.this)) {
				if (Utils.getBatteryPercentage(ApiAcceptRideServices.this) >= 20) {
					GCMIntentService.clearNotifications(ApiAcceptRideServices.this);
					GCMIntentService.stopRing(true, ApiAcceptRideServices.this);


					HashMap<String, String> params = new HashMap<String, String>();

					params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
					params.put(Constants.KEY_CUSTOMER_ID, customerId);
					params.put(Constants.KEY_ENGAGEMENT_ID, engagementId);
					params.put(Constants.KEY_LATITUDE, String.valueOf(latitude));
					params.put(Constants.KEY_LONGITUDE, String.valueOf(longitude));

					params.put(Constants.KEY_DEVICE_NAME, Utils.getDeviceName());
					params.put(Constants.KEY_IMEI, DeviceUniqueID.getUniqueId(ApiAcceptRideServices.this));
					params.put(Constants.KEY_APP_VERSION, "" + Utils.getAppVersion(ApiAcceptRideServices.this));

					params.put(Constants.KEY_REFERENCE_ID, String.valueOf(referenceId));
					params.put(Constants.KEY_IS_POOLED, String.valueOf(isPooled));
					params.put(Constants.KEY_IS_DELIVERY, String.valueOf(isDelivery));
					if(isDeliveryPool ==1){
						params.put(Constants.KEY_RIDE_TYPE,"4");
					}
					Log.i("request", String.valueOf(params));
					Response response = RestClient.getApiServices().driverAcceptRideSync(params);
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
							Prefs.with(ApiAcceptRideServices.this).save(SPLabels.ACCEPT_RIDE_VIA_PUSH, true);
							Intent splashActivity = new Intent(ApiAcceptRideServices.this, SharingRidesActivity.class);
							splashActivity.putExtra("wakeUp", true);
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
