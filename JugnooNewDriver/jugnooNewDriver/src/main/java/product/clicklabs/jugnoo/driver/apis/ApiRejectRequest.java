package product.clicklabs.jugnoo.driver.apis;

import android.app.Activity;
import android.content.Intent;


import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverTimeoutCheck;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 10/03/16.
 */
public class ApiRejectRequest {

	private Activity activity;
	private Callback callback;

	public ApiRejectRequest(Activity activity, Callback callback){
		this.activity = activity;
		this.callback = callback;
	}

	public void rejectRequestAsync(String accessToken, String customerId, final String engagementId, String referenceId) {

		if (AppStatus.getInstance(activity).isOnline(activity)) {


			HashMap<String, String> params = new HashMap<String, String>();
			params.put("access_token", accessToken);
			params.put("customer_id", customerId);
			params.put("engagement_id", engagementId);
			HomeUtil.putDefaultParams(params);

			if (!"".equalsIgnoreCase(referenceId)) {
				params.put("reference_id", referenceId);
			}
			RestClient.getApiServices().driverRejectRequestRetro(params, new retrofit.Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					try {
						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {

							String errorMessage = jObj.getString("error");

							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(activity);
							} else {
								DialogPopup.alertPopup(activity, "", errorMessage);
							}
						} else {
							try {
								int flag = jObj.getInt("flag");
								if (ApiResponseFlags.REQUEST_TIMEOUT.getOrdinal() == flag) {
									String log = jObj.getString("log");
									DialogPopup.alertPopup(activity, "", "" + log);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}

							callback.onSuccess(engagementId);
						}

					} catch (Exception exception) {
						exception.printStackTrace();
						DialogPopup.alertPopup(activity, "", Data.SERVER_ERROR_MSG);
					}

				}

				@Override
				public void failure(RetrofitError error) {
					DialogPopup.alertPopup(activity, "", Data.SERVER_NOT_RESOPNDING_MSG);
				}
			});


		} else {
			DialogPopup.alertPopup(activity, "", Data.CHECK_INTERNET_MSG);
		}


	}



	public interface Callback{
		void onSuccess(String engagementId);
	}

}
