package product.clicklabs.jugnoo.driver.utils;

import android.content.Context;

import product.clicklabs.jugnoo.driver.Database2;
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
                Log.e(TAG, "response="+response);
                if(response != null){
                    Database2.getInstance(context).deletePendingAPICall(pendingAPICall.id);
                    Log.e(TAG, "responseto string=" + new String(((TypedByteArray) response.getBody()).getBytes()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
