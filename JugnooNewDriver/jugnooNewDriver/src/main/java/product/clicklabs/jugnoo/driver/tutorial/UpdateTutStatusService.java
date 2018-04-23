package product.clicklabs.jugnoo.driver.tutorial;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONObject;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.DriverScreenMode;
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
public final class UpdateTutStatusService extends Service {

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
            String accessToken = Prefs.with(context).getString(SPLabels.PREF_TRAINING_ACCESS_TOKEN, "");
            String status = Prefs.with(context).getString(SPLabels.SET_DRIVER_TOUR_STATUS, "");
            String trainingId = Prefs.with(context).getString(SPLabels.PREF_TRAINING_ID, "");

            if (TextUtils.isEmpty(accessToken) || TextUtils.isEmpty(status) || TextUtils.isEmpty(trainingId)) {
                Log.d(TAG, "onStartCommand() Extend Stop Time Service empty data found");
                stopSelf();
            } else {
                HashMap<String, String> params = new HashMap<>();
                params.put("access_token", accessToken);
                params.put("status", status);
                params.put("training_id", trainingId);
                HomeUtil.putDefaultParams(params);

                Log.i(TAG, "params before api=" + params);

                RestClient.getApiServices().updateDriverStatus(params, new Callback<UpdateTourStatusModel>() {
                    @Override
                    public void success(UpdateTourStatusModel statusModel, Response response) {
                        try {
                            String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
                            JSONObject jObj = new JSONObject(responseStr);
                            int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_FAILED.getOrdinal());
                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
                                Prefs.with(context).save(SPLabels.PREF_TRAINING_ACCESS_TOKEN, "");
                                Prefs.with(context).save(SPLabels.SET_DRIVER_TOUR_STATUS, "");
                                Prefs.with(context).save(SPLabels.PREF_TRAINING_ID, "");
                                Prefs.with(context).save(SPLabels.DRIVER_SCREEN_MODE, DriverScreenMode.D_INITIAL.getOrdinal());
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
            }
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
