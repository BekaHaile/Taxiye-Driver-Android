package product.clicklabs.jugnoo.driver.tutorial;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

import static product.clicklabs.jugnoo.driver.Data.context;


/**
 * Created by Parminder Singh on 19/04/16.
 */
public final class ExtendStopTimeService extends Service {

    private final String TAG = getClass().getSimpleName();
    public static Boolean isRunning = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        isRunning = true;
        Log.d(TAG, "onStartCommand() Extend Stop Time Service ");
        //ExtendingStopTimeAlarm.setAlarm(this);

        try {
            String accessToken = intent.getStringExtra("access_token");
            String status = intent.getStringExtra("status");
            String trainingId = intent.getStringExtra("training_id");

            HashMap<String, String> params = new HashMap<>();
            params.put("access_token", accessToken);
            params.put("status", status);
            params.put("training_id", trainingId);


            Log.i(TAG, "params before api=" + params);

            RestClient.getApiServices().updateDriverStatus(params, new Callback<UpdateTourStatusModel>() {
                @Override
                public void success(UpdateTourStatusModel statusModel, Response response) {
                    try {
                        String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                        JSONObject jObj = new JSONObject(responseStr);
                        int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_FAILED.getOrdinal());
                        if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                            Prefs.with(context).save(SPLabels.SET_DRIVER_TOUR_STATUS, "");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    stopSelf();
                }

                @Override
                public void failure(RetrofitError error) {
                    stopSelf();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            stopSelf();
        }


        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        isRunning = false;
        Log.d(TAG, "onDestroy() called  Extend stop time with: " + "");
        super.onDestroy();


    }


}
