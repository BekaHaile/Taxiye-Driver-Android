package product.clicklabs.jugnoo.driver.vehicleGpsTracker;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;

import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.Data;
import product.clicklabs.jugnoo.driver.DriverLocationDispatcher;
import product.clicklabs.jugnoo.driver.DriverProfileActivity;
import product.clicklabs.jugnoo.driver.datastructure.ApiResponseFlags;
import product.clicklabs.jugnoo.driver.ui.api.APICommonCallbackKotlin;
import product.clicklabs.jugnoo.driver.ui.api.ApiCommonKt;
import product.clicklabs.jugnoo.driver.ui.api.ApiName;
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin;
import product.clicklabs.jugnoo.driver.utils.DialogPopup;
import product.clicklabs.jugnoo.driver.utils.Log;
import retrofit.RetrofitError;

public class TrackerLocationUpdater extends AppCompatActivity {
    private Socket mSocket;
    private Double lattitude=0.0;
    private Double longitude=0.0;
    private DriverLocationDispatcher locUpdater;
    private Activity activity;

   public void connectGpsDevice(String deviceImei,Activity activity){
       Log.e("external location updater initiated","---"+deviceImei);
       locUpdater = new DriverLocationDispatcher();
       this.activity = activity;
       try {
           mSocket = IO.socket("http://elnetech.com:8081/single-tracking");
       } catch (URISyntaxException e) {
           throw new RuntimeException(e);
       }
       mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

           @Override
           public void call(Object... args) {
               mSocket.emit("initiate-tracking", deviceImei);
           }

       }).on("location", new Emitter.Listener() {

           @Override
           public void call(Object... args) {
               JSONObject obj = (JSONObject)args[0];
             //  Log.e("connect done ",""+obj.toString());
               Log.e("external location updater started",""+obj.toString());
               if(obj.has("location")) {
                   try {
                       JSONObject locationObj = new JSONObject(String.valueOf(obj.getJSONObject("location")));
                      if(locationObj.has("latitude")) {
                          lattitude = locationObj.getDouble("latitude");
                      }
                      if(locationObj.has("longitude")){
                          longitude = locationObj.getDouble("longitude");
                      }
                   } catch (JSONException e) {
                       e.printStackTrace();
                   }
               }
               locUpdater.onLocationUpdate(lattitude,longitude);
               Log.e("external location updater running now",lattitude+"---"+longitude);


           }

       }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

           @Override
           public void call(Object... args) {
               Log.e("external location updater disconnected now",lattitude+"---"+longitude);
           }

       }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
           @Override
           public void call(Object... args) {

               Log.e("external location updater unable to connect ","connection failed");
               DialogPopup.alertPopup(activity, "", "Location from vehcile gps is not available at this moment", false, true, new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {
                       HashMap<String, String> params = new HashMap<>();
                       params.put(Constants.DEVICE_IMEI_NUMBER, Data.getGpsDeviceImeiNo());
                           params.put(Constants.GPS_PREFERENCE,"0");
                       new ApiCommonKt<FeedCommonResponseKotlin>(activity, true, false, true)
                               .execute(params, ApiName.UPDATE_GPS_PREFERENCE, new APICommonCallbackKotlin<FeedCommonResponseKotlin>() {
                                   @Override
                                   public void onSuccess(FeedCommonResponseKotlin feedCommonResponseKotlin, String message, int flag) {
                                       if(feedCommonResponseKotlin.getFlag() == ApiResponseFlags.ACTION_COMPLETE.getOrdinal()) {
                                           Data.setGpsPreference(0);
                                       }
                                   }

                                   @Override
                                   public boolean onError(FeedCommonResponseKotlin feedCommonResponseKotlin, String message, int flag) {
                                       return false;
                                   }

                                   @Override
                                   public boolean onFailure(RetrofitError error) {
                                       return super.onFailure(error);
                                   }
                               });
                   }
               });
           }
       });
       mSocket.connect();
   }

   public void stopTracker(){
       if(mSocket!=null)
        mSocket.disconnect();
   }


}
