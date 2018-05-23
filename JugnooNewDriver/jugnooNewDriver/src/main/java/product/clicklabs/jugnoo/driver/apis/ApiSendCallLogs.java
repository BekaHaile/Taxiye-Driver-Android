package product.clicklabs.jugnoo.driver.apis;

import android.Manifest;
import android.app.Activity;
import android.support.annotation.RequiresPermission;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Utils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by shankar on 5/26/16.
 */
public class ApiSendCallLogs {

	@RequiresPermission(Manifest.permission.READ_CALL_LOG)
	public void sendCallLogs(Activity activity, String accessToken, String engagementId, String phoneNumber) {
		try {
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
			params.put("eng_id", engagementId);
			params.put(Constants.KEY_CALL_LOGS, Utils.getCallDetails(activity, phoneNumber));
			HomeUtil.putDefaultParams(params);
			Log.i("params", "=" + params);

			RestClient.getApiServices().sendCallLogs(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					try {
						JSONObject jObj = new JSONObject(responseStr);
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {

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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
