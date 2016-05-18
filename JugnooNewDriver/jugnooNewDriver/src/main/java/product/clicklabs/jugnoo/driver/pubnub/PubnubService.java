package product.clicklabs.jugnoo.driver.pubnub;

import android.app.Service;
import android.content.BroadcastReceiver;
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

public class PubnubService extends Service {
    public PubnubService() {
    }

    private final String PUBLISH_KEY = "pub-c-8fbeae12-1ce0-4f6a-8811-59296b4ab980";
    private final String SUBSCRIBE_KEY = "sub-c-f05204b0-1cea-11e6-be83-0619f8945a4f";
    private final String CHANNEL = "jugnoo_my_channel";

    private Pubnub pubnub;
    private Pubnub getPubnub(){
        if(pubnub == null){
            pubnub = new Pubnub(PUBLISH_KEY, SUBSCRIBE_KEY);
        }
        return pubnub;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            getPubnub().subscribe(CHANNEL, new Callback() {
                        @Override
                        public void connectCallback(String channel, Object message) {
                            getPubnub().publish(CHANNEL, "Hello from the PubNub Java SDK", new Callback() {
                            });

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
                            System.out.println("SUBSCRIBE : " + channel + " : "
                                    + message.getClass() + " : " + message.toString());
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
