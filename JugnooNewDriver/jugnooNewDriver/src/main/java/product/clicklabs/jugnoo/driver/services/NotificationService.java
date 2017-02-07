package product.clicklabs.jugnoo.driver.services;

import android.content.Context;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.datastructure.AllNotificationData;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;


public class NotificationService extends NotificationListenerService {

	Context context;
	ArrayList<AllNotificationData> notificationList;


	@Override

	public void onCreate() {

		super.onCreate();
		context = getApplicationContext();

	}

	@Override

	public void onNotificationPosted(StatusBarNotification sbn) {


		try {
			String pack = sbn.getPackageName();
			int id = sbn.getId();
			String ticker = sbn.getNotification().tickerText.toString();
			Bundle extras = sbn.getNotification().extras;
			String title = extras.getString("android.counterTime");
			String text = extras.getCharSequence("android.text").toString();

			Log.i("Package", pack);
			Log.i("Ticker", ticker);
			Log.i("Title", title);
			Log.i("Text", text);


//        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
			if (sbn != null && (pack.toLowerCase().contains("ola") || pack.toLowerCase().contains("uber"))
					&& (Prefs.with(context).getInt(SPLabels.NOTIFICATION_SAVE_COUNT, 0) > 0)) {
				Database2.getInstance(context).insertNotificationdb(context, id, pack, text, title);
			}

			if (Database2.getInstance(context).getAllDbNotificationCount() >
					Prefs.with(context).getInt(SPLabels.NOTIFICATION_SAVE_COUNT, 0)) {
				notificationList = Database2.getInstance(context).getAllDBNotification();

				JSONArray notifications = new JSONArray();
				for (int i = 0; i < notificationList.size(); i++) {
					JSONObject obj = new JSONObject();
					try {
						obj.put("id", notificationList.get(i).getNotificationId());
						obj.put("package_name", notificationList.get(i).getNotificationPackage());
						obj.put("counterTime", notificationList.get(i).getTitle());
						obj.put("text", notificationList.get(i).getMessage());
					} catch (Exception e) {
						e.printStackTrace();
					}
					notifications.put(obj);
				}
				sendDriverNotifications(notifications);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

	@Override

	public void onNotificationRemoved(StatusBarNotification sbn) {
		Log.i("Msg", "Notification Removed");

	}


	public void sendDriverNotifications(JSONArray notificationList) {
		try {
			HashMap<String, String> params = new HashMap<String, String>();

			params.put("access_token", Data.userData.accessToken);
			params.put("notifications", String.valueOf(notificationList));
			Log.i("params", "=" + params);

			RestClient.getApiServices().sendDriverPushes(params, new Callback<RegisterScreenResponse>() {
				@Override
				public void success(RegisterScreenResponse registerScreenResponse, Response response) {
					String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
					try {
						JSONObject jObj = new JSONObject(responseStr);
						int flag = jObj.getInt("flag");
						if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
							Database2.getInstance(context).deleteNotificationTabledb();
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
