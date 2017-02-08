package product.clicklabs.jugnoo.driver.tutorial;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import retrofit.client.Response;


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

//        showToast("MyService is handling intent.");
        String accessToken = intent.getStringExtra("access_token");
        String status = intent.getStringExtra("status");
        String trainingId = intent.getStringExtra("training_id");

        HashMap<String, String> params = new HashMap<>();
        params.put("access_token", accessToken);
        params.put("status", status);
        params.put("training_id", trainingId);

        new UpdateDriveTourStatus(this, accessToken, status, trainingId).execute("");

        //Response response = RestClient.getApiServices().updateDriverStatus(params);


        stopSelf();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        isRunning = false;
        //ExtendingStopTimeAlarm.cancelAlarm(this);
//        showToast("onDestroy() called  Extend stop time with.");
        Log.d(TAG, "onDestroy() called  Extend stop time with: " + "");
        super.onDestroy();


    }

    public void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }

//    @Override
//    public void onTaskRemoved(Intent rootIntent) {
//        isRunning = false;
//        //ExtendingStopTimeAlarm.cancelAlarm(this);
//        stopSelf();
//        Log.d(TAG, "onTaskRemoved() called with: " + "rootIntent = [" + rootIntent + "]");
//    }


}
