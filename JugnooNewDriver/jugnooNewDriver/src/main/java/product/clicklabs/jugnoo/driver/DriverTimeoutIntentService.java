package product.clicklabs.jugnoo.driver;

import android.app.IntentService;
import android.content.Intent;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 16/12/15.
 */
public class DriverTimeoutIntentService extends IntentService implements Constants {

	private final String TAG = DriverTimeoutIntentService.class.getSimpleName();

	public DriverTimeoutIntentService() {
		this("DriverTimeoutIntentService");
	}

	/**
	 * Creates an IntentService.  Invoked by your subclass's constructor.
	 *
	 * @param name Used to name the worker thread, important only for debugging.
	 */
	public DriverTimeoutIntentService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		switchJugnooOnThroughServer(0, 1);

	}


	public void switchJugnooOnThroughServer(final int jugnooOnFlag, final int timeoutPenalty) {
		try {
			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Database2.getInstance(DriverTimeoutIntentService.this).getDLDAccessToken());
			params.put("latitude", "0");
			params.put("longitude", "0");
			params.put("flag", "" + jugnooOnFlag);
			params.put("business_id", "1");
			params.put("timeout_penalty", "" + timeoutPenalty);

			Response response = RestClient.getApiServices().switchJugnooOnThroughServerRetro(params);
			String result = new String(((TypedByteArray) response.getBody()).getBytes());

			JSONObject jObj = new JSONObject(result);

			if (jObj.has("flag")) {
				int flag = jObj.getInt("flag");
				if (ApiResponseFlags.DRIVER_TIMEOUT.getOrdinal() == flag) {
					long remainingPenaltyPeriod = jObj.optLong("remaining_penalty_period", 0);
					if (HomeActivity.appInterruptHandler != null) {
						Prefs.with(DriverTimeoutIntentService.this).save(SPLabels.INGNORE_RIDEREQUEST_COUNT,0);
						HomeActivity.appInterruptHandler.driverTimeoutDialogPopup(remainingPenaltyPeriod);
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
