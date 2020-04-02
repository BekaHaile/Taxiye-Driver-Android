package product.clicklabs.jugnoo.driver.vehicleGpsTracker;


import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;


import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import product.clicklabs.jugnoo.driver.DriverLocationDispatcher;
import product.clicklabs.jugnoo.driver.utils.Log;

public class TrackerLocationUpdater extends AppCompatActivity {
    private Socket mSocket;
    private Double lattitude=0.0;
    private Double longitude=0.0;
    private DriverLocationDispatcher locUpdater;

   public void connectGpsDevice(String deviceImei){
       Log.e("external location updater initiated","---"+deviceImei);
       locUpdater = new DriverLocationDispatcher();
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
               Log.e("external location updater start0",""+obj.toString());
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
           public void call(Object... args) {}

       });
       mSocket.connect();
   }

   public void stopTracker(){
       if(mSocket!=null)
        mSocket.disconnect();
   }


}
