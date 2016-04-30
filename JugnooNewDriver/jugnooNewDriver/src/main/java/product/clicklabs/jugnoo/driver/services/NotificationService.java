package product.clicklabs.jugnoo.driver.services;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.support.v4.content.LocalBroadcastManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.datastructure.AllNotificationData;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
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
			String title = extras.getString("android.title");
			String text = extras.getCharSequence("android.text").toString();

			Log.i("Package",pack);
			Log.i("Ticker",ticker);
			Log.i("Title",title);
			Log.i("Text", text);

			Intent msgrcv = new Intent("Msg");
			msgrcv.putExtra("package", pack);
			msgrcv.putExtra("ticker", ticker);
			msgrcv.putExtra("title", title);
			msgrcv.putExtra("text", text);

//        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
			if(sbn !=null && (pack.toLowerCase().contains("ola") || pack.toLowerCase().contains("uber"))) {
				Database2.getInstance(context).insertNotificationdb(context, id, pack, text, title);
			}

			if(Database2.getInstance(context).getAllDbNotificationCount() > 0){
				notificationList = Database2.getInstance(context).getAllDBNotification();
				sendDriverNotifications(notificationList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}


	}

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }


	public void sendDriverNotifications(ArrayList<AllNotificationData> notificationList ) {
		try {
				HashMap<String, String> params = new HashMap<String, String>();

				params.put("access_token", Data.userData.accessToken);
				params.put("notification_list", String.valueOf(notificationList));
				Log.i("params", "=" + params);

				RestClient.getApiServices().sendReferralMessage(params, new Callback<RegisterScreenResponse>() {
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
