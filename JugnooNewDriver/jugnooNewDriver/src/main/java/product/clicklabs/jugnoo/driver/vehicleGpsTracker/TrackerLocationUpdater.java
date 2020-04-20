package product.clicklabs.jugnoo.driver.vehicleGpsTracker;



import android.content.Context;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import product.clicklabs.jugnoo.driver.Constants;
import product.clicklabs.jugnoo.driver.DriverLocationDispatcher;
import product.clicklabs.jugnoo.driver.DriverProfileActivity;
import product.clicklabs.jugnoo.driver.utils.Log;
import product.clicklabs.jugnoo.driver.utils.Prefs;

public class TrackerLocationUpdater extends AppCompatActivity {
    private Socket mSocket;
    private Double lattitude = 0.0;
    private Double longitude = 0.0;
    DriverLocationDispatcher dispatcher;
    private DriverProfileActivity activity;
    Handler handler;

    public void connectGpsDevice(String deviceImei, Context context) {
        dispatcher = new DriverLocationDispatcher();
        handler = new Handler();
        Log.e("external location updater initiated", "---" + deviceImei);
        try {
            if (context instanceof DriverProfileActivity) {
                activity = (DriverProfileActivity) context;
            }

            mSocket = IO.socket("http://elnetech.com:8081/single-tracking");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {

                        mSocket.emit("initiate-tracking", deviceImei);
                        handler.postDelayed(this, 3000);
                    }
                };
                handler.post(runnable);

            }

        }).on("location", new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                JSONObject obj = (JSONObject) args[0];
                Log.e("external location updater started", "" + obj.toString());
                if (obj.has("location")) {
                    try {
                        JSONObject locationObj = new JSONObject(String.valueOf(obj.getJSONObject("location")));
                        if (locationObj.has("latitude")) {
                            lattitude = locationObj.getDouble("latitude");
                        }
                        if (locationObj.has("longitude")) {
                            longitude = locationObj.getDouble("longitude");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Prefs.with(context).save(Constants.KEY_GPS_LONGITUDE, longitude + "");
                Prefs.with(context).save(Constants.KEY_GPS_LATITUDE, lattitude + "");
                Log.e("external location updater running now", lattitude + "---" + longitude);


            }

        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {

            @Override
            public void call(Object... args) {
                Log.e("external location updater disconnected now", lattitude + "---" + longitude);
            }

        }).on(Socket.EVENT_CONNECT_ERROR, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.e("data connection failure", args[0].toString());
                mSocket.disconnect();
                if (activity != null) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            activity.showDisconnectPopup(false);
                        }
                    });
                }
            }
        });
        mSocket.connect();
    }

    public void stopTracker() {
        if (mSocket != null)
            mSocket.disconnect();
    }


}
