package faye;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import com.fugu.adapter.ListItem;
import com.fugu.database.CommonData;
import com.fugu.model.FuguFileDetails;
import com.fugu.utils.DateUtils;
import com.fugu.utils.FuguLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Socket;
import java.net.URI;
import java.nio.channels.NotYetConnectedException;
import java.util.Date;
import java.util.HashSet;
import java.util.TreeMap;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

import static com.fugu.constant.FuguAppConstant.ANDROID_USER;
import static com.fugu.constant.FuguAppConstant.CHANNEL_ID;
import static com.fugu.constant.FuguAppConstant.DATE_TIME;
import static com.fugu.constant.FuguAppConstant.FILE_MESSAGE;
import static com.fugu.constant.FuguAppConstant.FULL_NAME;
import static com.fugu.constant.FuguAppConstant.IMAGE_MESSAGE;
import static com.fugu.constant.FuguAppConstant.IMAGE_URL;
import static com.fugu.constant.FuguAppConstant.IS_TYPING;
import static com.fugu.constant.FuguAppConstant.MESSAGE;
import static com.fugu.constant.FuguAppConstant.MESSAGE_INDEX;
import static com.fugu.constant.FuguAppConstant.MESSAGE_STATUS;
import static com.fugu.constant.FuguAppConstant.MESSAGE_TYPE;
import static com.fugu.constant.FuguAppConstant.MESSAGE_UNSENT;
import static com.fugu.constant.FuguAppConstant.NOTIFICATION_READ_ALL;
import static com.fugu.constant.FuguAppConstant.NOTIFICATION_TYPE;
import static com.fugu.constant.FuguAppConstant.TEXT_MESSAGE;
import static com.fugu.constant.FuguAppConstant.THUMBNAIL_URL;
import static com.fugu.constant.FuguAppConstant.TYPING_SHOW_MESSAGE;
import static com.fugu.constant.FuguAppConstant.USER_ID;
import static com.fugu.constant.FuguAppConstant.USER_TYPE;

/**
 * Created by Bhavya Rattan on 01/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FayeClient {

    private static final String LOG_TAG = FayeClient.class.getSimpleName();

    private WebSocket mWebSocket = null;
    private FayeClientListener mListener = null;
    private HashSet<String> mChannels;
    private String mServerUrl = "";
    private boolean mFayeConnected = false;
    private boolean mIsConnectedServer = false;
    private MetaMessage mMetaMessage;
    private Handler mMessageHandler;

    public FayeClient(String url, MetaMessage meta) {
        mServerUrl = url;
        mMetaMessage = meta;
        mChannels = new HashSet<String>();
    }

    {
        HandlerThread thread = new HandlerThread("FayeHandler");
        thread.start();
        mMessageHandler = new Handler(thread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case WebSocket.ON_OPEN:
                        FuguLog.i(LOG_TAG, "onOpen() executed");
                        mIsConnectedServer = true;
                        handShake();
                        break;
                    case WebSocket.ON_CLOSE:
                        FuguLog.i(LOG_TAG, "onClosed() executed");
                        mIsConnectedServer = false;
                        mFayeConnected = false;
                        if (mListener != null && mListener instanceof FayeClientListener) {
                            mListener.onDisconnectedServer(FayeClient.this);
                        }
                        break;
                    case WebSocket.ON_MESSAGE:
                        try {
                            FuguLog.i(LOG_TAG, "onMessage executed");
                            handleFayeMessage((String) msg.obj);
                        } catch (NotYetConnectedException e) {
                            // Do noting
                            e.printStackTrace();
                        }
                        break;

                }
            }
        };
    }

    /* Public Methods */
    public FayeClientListener getListener() {
        return mListener;
    }

    public void setListener(FayeClientListener listener) {
        mListener = listener;
    }

    public void addChannel(String channel) {
        mChannels.add(channel);
    }

    public boolean isConnectedServer() {
        return mIsConnectedServer;
    }

    public boolean isFayeConnected() {
        return mFayeConnected;
    }

    public void connectServer() {
        openWebSocketConnection();
    }

    public void disconnectServer() {
        for (String channel : mChannels) {
            unsubscribe(channel);
        }
        mChannels.clear();
        disconnect();
    }

    public void subscribeChannel(String channel) {
        mChannels.add(channel);
        subscribe(channel);
    }

    public void subscribeToChannels(String... channels) {
        for (String channel : channels) {
            mChannels.add(channel);
            subscribe(channel);
        }
    }

    public void unsubscribeChannel(String channel) {
        if (mChannels.contains(channel)) {
            unsubscribe(channel);
            mChannels.remove(channel);
        }
    }

    public void unsubscribeChannels(String... channels) {
        for (String channel : channels) {
            unsubscribe(channel);
        }
    }

    public void unsubscribeAll() {
        for (String channel : mChannels) {
            unsubscribe(channel);
        }
    }

    public void publish(String message, int messageType, String url, String thumbnailUrl,
                        FuguFileDetails fileDetails, int notificationType,
                        String uuid, int position, String channel, Long userId, String userName, Long channelId,
                        int isTyping) {
        JSONObject messageJson = new JSONObject();
        String localDate = DateUtils.getFormattedDate(new Date());
        try {
            if (notificationType == NOTIFICATION_READ_ALL) {
                messageJson.put(NOTIFICATION_TYPE, notificationType);
                messageJson.put(CHANNEL_ID, channelId);
            } else {
                messageJson.put(FULL_NAME, userName);
                messageJson.put(MESSAGE, message);
                messageJson.put(MESSAGE_TYPE, messageType);
                messageJson.put(DATE_TIME, DateUtils.getInstance().convertToUTC(localDate));
                if (position == 0) {
                    messageJson.put(MESSAGE_INDEX, position);
                } else {
                    messageJson.put(MESSAGE_INDEX, position);
                }
                messageJson.put("UUID", uuid);
                if (messageType == IMAGE_MESSAGE && !url.trim().isEmpty() && !thumbnailUrl.trim().isEmpty()) {
                    messageJson.put(IMAGE_URL, url);
                    messageJson.put(THUMBNAIL_URL, thumbnailUrl);
                }

                if (messageType == FILE_MESSAGE && !url.trim().isEmpty()) {
                    messageJson.put("url", url);
                    messageJson.put("file_name", fileDetails.getFileName());
                    messageJson.put("file_size", fileDetails.getFileSize());
                }

                if (messageType == TEXT_MESSAGE) {
                    messageJson.put(IS_TYPING, isTyping);
                } else {
                    messageJson.put(IS_TYPING, TYPING_SHOW_MESSAGE);
                }

                messageJson.put(MESSAGE_STATUS, MESSAGE_UNSENT);
            }

            messageJson.put(USER_ID, String.valueOf(userId));
            messageJson.put(USER_TYPE, ANDROID_USER);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        publish(channel, messageJson, null, null);
    }

    public void publish(String channel, JSONObject data) {
        publish(channel, data, null, null);
    }

    public void publish(String channel, JSONObject data, String ext, String id) {
        try {
            String publish = mMetaMessage.publish(channel, data, ext, id);
            mWebSocket.send(publish);
        } catch (Exception e) {
            FuguLog.e(LOG_TAG, "Build publish message to JSON error" + e);
            connectServer();
        }
    }

    /* Private Methods */
    private Socket getSSLWebSocket() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, null, null);
            SSLSocketFactory factory = sslContext.getSocketFactory();
            return factory.createSocket();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void openWebSocketConnection() {
        // Clean up any existing socket
        if (mWebSocket != null) {
            mWebSocket.close();
        }
        try {
            URI uri = new URI(mServerUrl);
            mWebSocket = new WebSocket(uri, mMessageHandler);

            FuguLog.e("uri.getScheme()", "==" + uri.getScheme());

            if (uri.getScheme().equals("https") || uri.getScheme().equals("wss")) {
                mWebSocket.setSocket(getSSLWebSocket());
            }
            mWebSocket.connect();
        } catch (Exception e) {
            FuguLog.e(LOG_TAG, "Server URL error" + e);
        }
    }

    private void closeWebSocketConnection() {
        if (mWebSocket != null) {
            mWebSocket.close();
        }
        mListener = null;
    }

    private void handShake() {
        try {
            String handshake = mMetaMessage.handShake();
            mWebSocket.send(handshake);
        } catch (Exception e) {
            FuguLog.e(LOG_TAG, "HandShake message error" + e);
        }
    }

    private void subscribe(String channel) {
        try {
            String subscribe = mMetaMessage.subscribe(channel);
            mWebSocket.send(subscribe);
        } catch (Exception e) {
            FuguLog.e(LOG_TAG, "Subscribe message error" + e);
        }
    }

    private void unsubscribe(String channel) {
        try {
            String unsubscribe = mMetaMessage.unsubscribe(channel);
            mWebSocket.send(unsubscribe);
            FuguLog.i(LOG_TAG, "UnSubscribe:" + channel);
        } catch (Exception e) {
            e.printStackTrace();
            FuguLog.e(LOG_TAG, "Unsubscribe message error" + e);
        }
    }

    private void connect() {
        try {
            String connect = mMetaMessage.connect();
            mWebSocket.send(connect);
        } catch (Exception e) {
            FuguLog.e(LOG_TAG, "Connect message error" + e);
        }
    }

    private void disconnect() {
        try {
            String disconnect = mMetaMessage.disconnect();
            mWebSocket.send(disconnect);
        } catch (Exception e) {
            FuguLog.e(LOG_TAG, "Disconnect message error" + e);
        }
    }

    private void handleFayeMessage(String message) {
        JSONArray arr = null;
        try {
            arr = new JSONArray(message);
        } catch (Exception e) {
            FuguLog.e(LOG_TAG, "Unknown message type: " + message + e);
        }

        int length = arr.length();
        for (int i = 0; i < length; ++i) {
            JSONObject obj = arr.optJSONObject(i);
            if (obj == null) continue;

            String channel = obj.optString(MetaMessage.KEY_CHANNEL);
            boolean successful = obj.optBoolean("successful");
            if (channel.equals(MetaMessage.HANDSHAKE_CHANNEL)) {
                if (successful) {
                    mMetaMessage.setClient(obj.optString(MetaMessage.KEY_CLIENT_ID));
                    if (mListener != null && mListener instanceof FayeClientListener) {
                        mListener.onConnectedServer(this);
                    }
                    connect();
                } else {
                    FuguLog.e(LOG_TAG, "Handshake Error: " + obj.toString());
                }
                return;
            }

            if (channel.equals(MetaMessage.CONNECT_CHANNEL)) {
                if (successful) {
                    mFayeConnected = true;
                    connect();
                } else {
                    FuguLog.e(LOG_TAG, "Connecting Error: " + obj.toString());
                }
                return;
            }

            if (channel.equals(MetaMessage.DISCONNECT_CHANNEL)) {
                if (successful) {
                    if (mListener != null && mListener instanceof FayeClientListener) {
                        mListener.onDisconnectedServer(this);
                    }
                    mFayeConnected = false;
                    closeWebSocketConnection();
                } else {
                    FuguLog.e(LOG_TAG, "Disconnecting Error: " + obj.toString());
                }
                return;
            }

            if (channel.equals(MetaMessage.SUBSCRIBE_CHANNEL)) {
                String subscription = obj.optString(MetaMessage.KEY_SUBSCRIPTION);
                if (successful) {
                    mFayeConnected = true;
                    FuguLog.i(LOG_TAG, "Subscribed channel " + subscription);
                } else {
                    FuguLog.e(LOG_TAG, "Subscribing channel " + subscription
                            + " Error: " + obj.toString());
                }
                return;
            }

            if (channel.equals(MetaMessage.UNSUBSCRIBE_CHANNEL)) {
                String subscription = obj.optString(MetaMessage.KEY_SUBSCRIPTION);
                if (successful) {
                    FuguLog.i(LOG_TAG, "Unsubscribed channel " + subscription);
                } else {
                    FuguLog.e(LOG_TAG, "Unsubscribing channel " + subscription
                            + " Error: " + obj.toString());
                }
                return;
            }

            if (mChannels.contains(channel)) {
                String data = obj.optString(MetaMessage.KEY_DATA, null);
                if (data != null) {
                    if (mListener != null && mListener instanceof FayeClientListener) {
                        TreeMap<String, ListItem> unsentMessageMap = new TreeMap<>();
                        try {
                            final JSONObject messageJson = new JSONObject(data);
                            String channelId = channel.substring(1, channel.length());
                            unsentMessageMap = CommonData.getUnsentMessageMapByChannel(Long.parseLong(channelId));
                            try {
                                unsentMessageMap.remove(messageJson.getString("UUID"));
                                CommonData.setUnsentMessageMapByChannel(Long.parseLong(channelId), unsentMessageMap);
                            } catch (Exception e) {

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mListener.onReceivedMessage(this, data, channel);

                    }
                }
            } else {
                FuguLog.e(LOG_TAG, "Cannot handle this message: " + obj.toString());
                String data = obj.optString(MetaMessage.KEY_DATA, null);
                if (data != null) {
                    if (mListener != null && mListener instanceof FayeClientListener) {
                        mListener.onReceivedMessage(this, data, channel);
                    }
                }
            }
            return;
        }
    }


}
