package product.clicklabs.jugnoo.driver.tutorial;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.FetchAndSendMessages;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by gurmail on 2/8/17.
 */

public class UpdateDriveTourStatus extends AsyncTask<String, Integer, String> {

    private final String TAG = UpdateDriveTourStatus.class.getSimpleName();


    private Context context;
    private String accessToken;
    private String status;
    private String trainingId;

    public UpdateDriveTourStatus(Context context, String accessToken, String status, String trainingId){
        this.context = context;
        this.accessToken = accessToken;
        this.status = status;
        this.trainingId = trainingId;
    }
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... param) {
        try {

            if(AppStatus.getInstance(context).isOnline(context)){
                HashMap<String, String> params = new HashMap<>();
                params.put("access_token", accessToken);
                params.put("status", status);
                params.put("training_id", trainingId);

                Log.i(TAG, "params before api=" + params);

                Response response = RestClient.getApiServices().updateDriverStatus(params);

//                RestClient.getApiServices().updateDriverStatus(params, new Callback<UpdateTourStatusModel>() {
//                    @Override
//                    public void success(UpdateTourStatusModel statusModel, Response response) {
//                        try {
//                            String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//                            JSONObject jObj = new JSONObject(responseStr);
//                            int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_FAILED.getOrdinal());
//                            if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
//                                Prefs.with(context).save(SPLabels.SET_DRIVER_TOUR_STATUS, "");
//                            }
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void failure(RetrofitError error) {
//                    }
//                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        try {

//                if(AppStatus.getInstance(context).isOnline(context)){
//                    HashMap<String, String> params = new HashMap<>();
//                    params.put("access_token", accessToken);
//                    params.put("status", status);
//                    params.put("training_id", trainingId);
//
//                    Log.i(TAG, "params before api=" + params);
//
//                    RestClient.getApiServices().updateDriverStatus(params, new Callback<UpdateTourStatusModel>() {
//                        @Override
//                        public void success(UpdateTourStatusModel statusModel, Response response) {
//                            try {
//                                String responseStr = new String(((TypedByteArray) response.getBody()).getBytes());
//                                JSONObject jObj = new JSONObject(responseStr);
//                                int flag = jObj.optInt(Constants.KEY_FLAG, ApiResponseFlags.ACTION_FAILED.getOrdinal());
//                                if (ApiResponseFlags.ACTION_COMPLETE.getOrdinal() == flag) {
//                                    Prefs.with(context).save(SPLabels.SET_DRIVER_TOUR_STATUS, "");
//                                }
//                            } catch (Exception e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void failure(RetrofitError error) {
//                        }
//                    });
//                }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void syncCall(HashMap<String, String> params){
        try {
            //Response response = RestClient.getApiServices().updateDriverStatus(params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}