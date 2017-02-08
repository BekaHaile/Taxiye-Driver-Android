package product.clicklabs.jugnoo.driver.tutorial;

import android.app.IntentService;
import android.content.Intent;
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
            String accessToken = intent.getStringExtra("access_token");
            String status = intent.getStringExtra("status");
            String trainingId = intent.getStringExtra("training_id");

            HashMap<String, String> params = new HashMap<>();
            params.put("access_token", accessToken);
            params.put("status", status);
            params.put("training_id", trainingId);

            Response response = RestClient.getApiServices().updateDriverStatus(params);
            for(int i=0;i<50;i++) {
                Log.v("Log", "***\n*****\n******\n********");
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

}
