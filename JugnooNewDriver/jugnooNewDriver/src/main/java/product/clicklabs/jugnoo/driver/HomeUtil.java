package product.clicklabs.jugnoo.driver;

import android.app.Activity;
import android.view.View;

import com.google.gson.annotations.SerializedName;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.SettleUserDebt;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.MultipartTypedOutput;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedString;

/**
 * Created by shankar on 16/11/17.
 */

public class HomeUtil {

	public static void scheduleCallDriver(final Activity context) {
		DialogPopup.alertPopupTwoButtonsWithListeners(context, "",
				context.getString(R.string.schedule_call_message, context.getString(R.string.appname)),
				context.getString(R.string.get_call), context.getString(R.string.cancel),
				new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						scheduleCallDriverApi(context);
					}
				}, new View.OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				}, true, false);
	}

	private static void scheduleCallDriverApi(final Activity context) {
		try {
			DialogPopup.showLoadingDialog(context, context.getString(R.string.loading));
			HashMap<String, String> params = new HashMap<>();
			params.put(Constants.KEY_ACCESS_TOKEN, Data.userData.accessToken);
			params.put(Constants.KEY_PHONE_NO, Data.userData.phoneNo);
			HomeUtil.putDefaultParams(params);
			RestClient.getApiServices().scheduleCallDriver(params, new Callback<SettleUserDebt>() {
				@Override
				public void success(SettleUserDebt dailyEarningResponse, Response response) {
					try {

						String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
						JSONObject jObj;
						DialogPopup.dismissLoadingDialog();
						jObj = new JSONObject(jsonString);
						if (!jObj.isNull("error")) {
							String errorMessage = jObj.getString("error");
							if (Data.INVALID_ACCESS_TOKEN.equalsIgnoreCase(errorMessage.toLowerCase())) {
								HomeActivity.logoutUser(context, null);
							} else {
								DialogPopup.alertPopup(context, "", context.getString(R.string.error_occured_tap_to_retry));
							}

						} else {
							String message = JSONParser.getServerMessage(jObj);
							DialogPopup.alertPopup(context, "", message);
						}

					} catch (Exception e) {
						e.printStackTrace();
						try {
							DialogPopup.alertPopup(context, "", context.getString(R.string.error_occured_tap_to_retry));
							DialogPopup.dismissLoadingDialog();
						} catch (Exception e1) {
							e1.printStackTrace();
							DialogPopup.dismissLoadingDialog();
						}
					}

				}

				@Override
				public void failure(RetrofitError error) {
					try {
						DialogPopup.dismissLoadingDialog();
						DialogPopup.alertPopup(context, "", context.getString(R.string.error_occured_tap_to_retry));
					} catch (Exception e) {
						DialogPopup.dismissLoadingDialog();
						e.printStackTrace();
					}
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void putDefaultParams(HashMap<String, String> params){
		params.put(Constants.KEY_OPERATOR_TOKEN, MyApplication.getInstance().getString(R.string.white_label_key));
		params.put(Constants.LOGIN_TYPE, Data.LOGIN_TYPE);
		params.put(Constants.KEY_DEVICE_TYPE, Data.DEVICE_TYPE);
		params.put(Constants.KEY_LOCALE, Prefs.with(MyApplication.getInstance()).getString(SPLabels.SELECTED_LANGUAGE,MyApplication.getInstance().getString(R.string.default_lang)));
	}

	public static void putDefaultParams(MultipartTypedOutput params){
		params.addPart(Constants.KEY_OPERATOR_TOKEN, new TypedString(MyApplication.getInstance().getString(R.string.white_label_key)));
		params.addPart(Constants.LOGIN_TYPE, new TypedString(Data.LOGIN_TYPE));
		params.addPart(Constants.KEY_DEVICE_TYPE, new TypedString(Data.DEVICE_TYPE));
		params.addPart(Constants.KEY_LOCALE, new TypedString(Prefs.with(MyApplication.getInstance()).getString(SPLabels.SELECTED_LANGUAGE,MyApplication.getInstance().getString(R.string.default_lang))));
	}

	public static class DefaultParams{
		@SerializedName(Constants.KEY_ACCESS_TOKEN)
		private String accessToken;

		@SerializedName(Constants.KEY_OPERATOR_TOKEN)
		private String operatorToken;

		@SerializedName(Constants.KEY_DEVICE_TYPE)
		private String deviceType;

		@SerializedName(Constants.KEY_LOCALE)
		private String locale;

		public DefaultParams() {
			this.accessToken = Data.userData!=null?Data.userData.accessToken:"";
			this.operatorToken =MyApplication.getInstance().getString(R.string.white_label_key);
			this.deviceType = Data.DEVICE_TYPE;
			this.locale = Prefs.with(MyApplication.getInstance()).getString(SPLabels.SELECTED_LANGUAGE,MyApplication.getInstance().getString(R.string.default_lang));
		}
	}


}
