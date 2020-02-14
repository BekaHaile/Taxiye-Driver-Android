package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;
import android.content.Intent;

import org.json.JSONObject;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.Database2;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.PendingAPICall;
import product.clicklabs.jugnoo.driver.datastructure.PendingCall;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneesh on 6/2/16.
 */
public class PendingApiHit {
    private final String TAG = PendingApiHit.class.getSimpleName();

    public void startAPI(Context context, PendingAPICall pendingAPICall) {
        try {
            if (AppStatus.getInstance(context).isOnline(context)) {
                Response response = null;
                HomeUtil.putDefaultParams(pendingAPICall.nameValuePairs);
                if(PendingCall.END_RIDE.getPath().equalsIgnoreCase(pendingAPICall.url)){
                    response = RestClient.getApiServices().endRideSync(pendingAPICall.nameValuePairs);
                }
                else if(PendingCall.MARK_DELIVERED.getPath().equalsIgnoreCase(pendingAPICall.url)){
                    response = RestClient.getApiServices().markDeliveredSync(pendingAPICall.nameValuePairs);
                }
                else if(PendingCall.UPLOAD_RIDE_DATA.getPath().equalsIgnoreCase(pendingAPICall.url)){
                    response = RestClient.getApiServices().uploadRideDataSync(pendingAPICall.nameValuePairs);
                }
                else if(PendingCall.END_DELIVERY.getPath().equalsIgnoreCase(pendingAPICall.url)){
                    response = RestClient.getApiServices().endDelivery(pendingAPICall.nameValuePairs);
                }
				else if(PendingCall.EMERGENCY_ALERT.getPath().equalsIgnoreCase(pendingAPICall.url)){
					response = RestClient.getApiServices().emergencyAlertSync(pendingAPICall.nameValuePairs);
				}
                Log.e(TAG, "response="+response);
                if(response != null){
                    try {
						if (PendingCall.END_RIDE.getPath().equalsIgnoreCase(pendingAPICall.url)) {
							String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
							JSONObject jObj = new JSONObject(jsonString);
							int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
							if (!(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag)) {
								Database2.getInstance(context).deletePendingAPICall(pendingAPICall.id);
								Log.e(TAG, "responseto string=" + new String(((TypedByteArray) response.getBody()).getBytes()));
								Prefs.with(context).save(Constants.DRIVER_RIDE_EARNING, jObj.optString("driver_ride_earning", ""));
								Prefs.with(context).save(Constants.DRIVER_RIDE_DATE, jObj.optString("driver_ride_date", ""));
								Intent fetchDocIntent = new Intent(Constants.ACTION_UPDATE_RIDE_EARNING);
								context.sendBroadcast(fetchDocIntent);
								if(Data.userData.getMinDriverBalance()>0){
                                    Intent balanceAlertIntent = new Intent(Constants.ACTION_ALERT_FOR_MINIMUM_BALANCE);
                                    context.sendBroadcast(balanceAlertIntent);
                                }
							}
						} else if (PendingCall.END_DELIVERY.getPath().equalsIgnoreCase(pendingAPICall.url)) {
                            String jsonString = new String(((TypedByteArray) response.getBody()).getBytes());
                            JSONObject jObj = new JSONObject(jsonString);
                            int flag = jObj.optInt("flag", ApiResponseFlags.ACTION_COMPLETE.getOrdinal());
                            if (!(ApiResponseFlags.SHOW_ERROR_MESSAGE.getOrdinal() == flag)) {
                                Database2.getInstance(context).deletePendingAPICall(pendingAPICall.id);
                                Intent dismisspopup = new Intent(Constants.DISMISS_END_DELIVERY_POPUP);
								dismisspopup.putExtra("response", jsonString);
                                context.sendBroadcast(dismisspopup);
                            }
                        } else {
							Database2.getInstance(context).deletePendingAPICall(pendingAPICall.id);

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
