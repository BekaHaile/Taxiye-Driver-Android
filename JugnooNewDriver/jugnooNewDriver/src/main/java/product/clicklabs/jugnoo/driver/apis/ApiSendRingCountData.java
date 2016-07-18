package product.clicklabs.jugnoo.driver.apis;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 10/03/16.
 */
public class ApiSendRingCountData {

    private Activity activity;
    private Callback callback;

    public ApiSendRingCountData(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

	public void ringCountData() {
		try {
			if (AppStatus.getInstance(activity).isOnline(activity)) {

				String ringCountData = Database2.getInstance(activity).getRingCompleteData();
				HashMap<String, String> params = new HashMap<String, String>();
				params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
				params.put(Constants.KEY_RING_COUNT, ringCountData);

				Log.i("params", "=" + params);

				RestClient.getApiServices().sendRingCountData(params, new retrofit.Callback<RegisterScreenResponse>() {
					@Override
					public void success(RegisterScreenResponse registerScreenResponse, Response response) {
						String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
						try {
							JSONObject jObj = new JSONObject(responseStr);
							int flag = jObj.getInt(Constants.KEY_FLAG);
							if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
								callback.onSuccess();
							}
						} catch (Exception exception) {
							exception.printStackTrace();
						}
					}
					@Override
					public void failure(RetrofitError error) {
						Log.e("request fail", error.toString());
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

    public interface Callback {
        void onSuccess();
    }

}
