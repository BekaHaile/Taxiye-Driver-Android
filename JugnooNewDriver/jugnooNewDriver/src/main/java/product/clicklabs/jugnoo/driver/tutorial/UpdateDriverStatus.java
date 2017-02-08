package product.clicklabs.jugnoo.driver.tutorial;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import retrofit.client.Response;

/**
 * Created by gurmail on 2/8/17.
 */

public class UpdateDriverStatus extends IntentService {


    public UpdateDriverStatus(){
        this("UpdateDriverStatus");
    }

    public UpdateDriverStatus(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try{
            showToast("MyService is handling intent.");
            String accessToken = intent.getStringExtra("access_token");
            String status = intent.getStringExtra("status");
            String trainingId = intent.getStringExtra("training_id");

            HashMap<String, String> params = new HashMap<>();
            params.put("access_token", accessToken);
            params.put("status", status);
            params.put("training_id", trainingId);

            Response response = RestClient.getApiServices().updateDriverStatus(params);

        } catch (Exception e){
            e.printStackTrace();
        }
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

}
