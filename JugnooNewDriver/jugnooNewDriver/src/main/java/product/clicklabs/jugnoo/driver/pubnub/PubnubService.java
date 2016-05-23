package product.clicklabs.jugnoo.driver.pubnub;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubError;
import com.pubnub.api.PubnubException;

import product.clicklabs.jugnoo.driver.GCMIntentService;
import product.clicklabs.jugnoo.driver.datastructure.SPLabels;
import product.clicklabs.jugnoo.driver.utils.Prefs;

import static android.support.v4.content.WakefulBroadcastReceiver.startWakefulService;

public class PubnubService extends Service {
    public PubnubService() {
    }

//    private final String PUBLISH_KEY = "pub-c-8fbeae12-1ce0-4f6a-8811-59296b4ab980";
//    private final String SUBSCRIBE_KEY = "sub-c-f05204b0-1cea-11e6-be83-0619f8945a4f";
//    private final String CHANNEL = "jugnoo_my_channel";



    Context context;

    private Pubnub pubnub;

    private Pubnub getPubnub() {
        if (pubnub == null) {
            pubnub = new Pubnub(Prefs.with(this).getString(SPLabels.PUBNUB_PUBLISHER_KEY, ""),
                    Prefs.with(this).getString(SPLabels.PUBNUB_SUSCRIBER_KEY, ""));
        }
        return pubnub;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {

        try {
            String CHANNEL =  Prefs.with(this).getString(SPLabels.PUBNUB_CHANNEL, "");
            getPubnub().subscribe(CHANNEL, new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {

                            registerReceiver(new BroadcastReceiver() {
                                @Override
                                public void onReceive(Context arg0, Intent intent) {
                                    getPubnub().disconnectAndResubscribe();
                                }

                            }, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
                        }

                        @Override
                        public void disconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : DISCONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        public void reconnectCallback(String channel, Object message) {
                            System.out.println("SUBSCRIBE : RECONNECT on channel:" + channel
                                    + " : " + message.getClass() + " : "
                                    + message.toString());
                        }

                        @Override
                        public void successCallback(String channel, Object message) {
                            System.out.println("MESSAGE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());

                            Intent service = new Intent(context, GCMIntentService.class);
                            service.putExtra("message", String.valueOf(message));

                            // Start the service, keeping the device awake while it is launching.
                            startWakefulService(context, service);

                        }

                        @Override
                        public void errorCallback(String channel, PubnubError error) {
                            System.out.println("SUBSCRIBE : ERROR on channel " + channel
                                    + " : " + error.toString());
                        }
                    }
            );
        } catch (PubnubException e) {
            System.out.println(e.toString());
        }

        return START_STICKY;
    }


}
