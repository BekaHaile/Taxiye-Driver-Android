package faye;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.fugu.HippoConfig;
import com.fugu.utils.FuguLog;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

/**
 * Created by Bhavya Rattan on 01/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */


public class WebSocket extends WebSocketClient {

    private static final String LOG_TAG = WebSocket.class.getSimpleName();

    public static final int ON_OPEN = 1;
    public static final int ON_CLOSE = 2;
    public static final int ON_MESSAGE = 3;
    public static final int ON_ERROR = 4;

    private Handler messageHandler;

    public WebSocket(URI serverUri, Handler handler) {
        super(serverUri);
        messageHandler = handler;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        messageHandler.sendMessage(Message.obtain(messageHandler, ON_OPEN));
    }

    @Override
    public void onMessage(String s) {
        messageHandler.sendMessage(Message.obtain(messageHandler, ON_MESSAGE, s));
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        FuguLog.i(LOG_TAG, "code: " + code + ", reason: "
                + reason + ", remote: " + remote);
        messageHandler.sendMessage(
                Message.obtain(messageHandler, ON_CLOSE));
    }

    @Override
    public void onError(Exception e) {
        FuguLog.e(LOG_TAG, "On WebSocket Error:" + e);
        Looper.prepare();
        HippoConfig.getClient(new FayeCallback() {
            @Override
            public void onClientReady(final FayeClient fayeClient) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fayeClient.connectServer();
                    }
                },5000);
            }
        });
    }

}
