package product.clicklabs.jugnoo.driver;

/**
 * Created by socomo20 on 8/18/15.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;


public class MyInstanceIDListenerService extends FirebaseInstanceIdService {

    private static final String TAG = "MyInstanceIDLS";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is also called
     * when the InstanceID token is initially generated, so this is where
     * you retrieve the token.
     */
    // [START refresh_token]
    @Override
    public void onTokenRefresh() {
        try {
            // Get updated InstanceID token.
            Log.i(SplashNewActivity.DEVICE_TOKEN_TAG, "onTokenRefresh: "+ FirebaseInstanceId.getInstance().getToken());
            Intent intent = new Intent(Constants.ACTION_DEVICE_TOKEN_UPDATED);
			try{LocalBroadcastManager.getInstance(this).sendBroadcast(intent);}catch(Exception ignored){}


        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void refreshDeviceToken(final Context activity, String refreshedToken, String accessToken) {
        try {
          /*  if (MyApplication.getInstance().isOnline()) {
                final HashMap<String, String> params = new HashMap<>();
                params.put(Constants.KEY_ACCESS_TOKEN, accessToken);
                params.put(Constants.KEY_DEVICE_TOKEN, refreshedToken);
                new HomeUtil().putDefaultParams(params);
                Response response = RestClient.getApiService().refreshDeviceToken(params);
            } else {
            }*/
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}