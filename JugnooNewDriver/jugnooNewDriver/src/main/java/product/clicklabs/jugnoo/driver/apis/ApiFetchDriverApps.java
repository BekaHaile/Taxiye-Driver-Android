package product.clicklabs.jugnoo.driver.apis;

import android.app.Activity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.HomeActivity;
import product.clicklabs.jugnoo.driver.HomeUtil;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.retrofit.RestClient;
import product.clicklabs.jugnoo.driver.retrofit.model.RegisterScreenResponse;
import product.clicklabs.jugnoo.driver.utils.AppStatus;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedByteArray;

/**
 * Created by aneeshbansal on 10/03/16.
 */
public class ApiFetchDriverApps {

    private Activity activity;
    private Callback callback;

    public ApiFetchDriverApps(Activity activity, Callback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    public void fetchDriverAppAsync(String accessToken, JSONArray appList) {

        if (AppStatus.getInstance(activity).isOnline(activity)) {

            HashMap<String, String> params = new HashMap<String, String>();
            params.put("access_token", accessToken);
            params.put("app_list", String.valueOf(appList));
            HomeUtil.putDefaultParams(params);
            RestClient.getApiServices().fetchAlldriverApps(params, new retrofit.Callback<RegisterScreenResponse>() {
                @Override
                public void success(RegisterScreenResponse registerScreenResponse, Response response) {
                    try {
                        callback.onSuccess();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    public interface Callback {
        void onSuccess();
    }

}
