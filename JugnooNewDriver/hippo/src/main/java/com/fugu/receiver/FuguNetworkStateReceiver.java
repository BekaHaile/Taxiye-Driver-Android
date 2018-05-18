package com.fugu.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;

import com.fugu.BuildConfig;
import com.fugu.activity.FuguChannelsActivityNew;
import com.fugu.activity.FuguChatActivity;
import com.fugu.adapter.EventItem;
import com.fugu.adapter.ListItem;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.FuguUploadImageResponse;
import com.fugu.model.Message;
import com.fugu.retrofit.APIError;
import com.fugu.retrofit.MultipartParams;
import com.fugu.retrofit.ResponseResolver;
import com.fugu.retrofit.RestClient;
import com.fugu.utils.DateUtils;
import com.fugu.utils.FuguLog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;

import faye.FayeClient;
import faye.FayeClientListener;
import faye.MetaMessage;
import io.paperdb.Paper;

/**
 * Created by Bhavya Rattan on 23/06/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguNetworkStateReceiver extends BroadcastReceiver implements FuguAppConstant {

    private String TAG = "FuguNetworkStateReceiver";
    // Initial Meta FuguMessage
    private static MetaMessage meta = new MetaMessage();

    private static FayeClient mClient;
    private Context mContext;

    @NonNull
    private static TreeMap<Long, TreeMap<String, ListItem>> unsentMessageMap = new TreeMap<>();


    public void onReceive(Context context, Intent intent) {
        Paper.init(context);
        int status = NetworkUtil.getConnectivityStatusString(context);
        try {
            //FuguChannelsActivity.changeStatus(status);
            FuguChannelsActivityNew.changeStatus(status);
        } catch (Exception e) {

        }
        try {
            FuguChatActivity.changeStatus(status);
        } catch (Exception e) {

        }

        try {

            if (CommonData.getUnsentMessageMap() != null) {
                unsentMessageMap = CommonData.getUnsentMessageMap();
            }

            FuguLog.d("app", "Network connectivity change");

            if (intent.getExtras() != null) {

                boolean isEnabled;


                NetworkInfo ni = (NetworkInfo) intent.getExtras().get(ConnectivityManager.EXTRA_NETWORK_INFO);

                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {

                    isEnabled = true;
                    mContext = context;

                    FuguLog.i("app", "Network " + ni.getTypeName() + " connected");

                    if (!CommonData.getServerUrl().isEmpty()) {

                        if (mClient == null) {
                            mClient = new FayeClient(CommonData.getServerUrl() + "/faye", meta);
                        }

                        FuguLog.i("server url == ", CommonData.getServerUrl());

                        setUpFayeConnection();
                    }
                } else {
                    isEnabled = false;
                }

                Intent mIntent = new Intent(NETWORK_STATE_INTENT);
                mIntent.putExtra("isConnected", isEnabled);
                LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent);

            }
        } catch (Exception e) {

        }
    }

    private void publishUnsentMessages(Long channelId, TreeMap<String, ListItem> unsentMessageMapByChannel) {

        FuguLog.e(TAG, "publishUnsentMessages");

        for (String key : unsentMessageMapByChannel.keySet()) {

            JSONObject messageJson = new JSONObject();

            Message messageObj = ((EventItem) unsentMessageMapByChannel.get(key)).getEvent();

            try {
                messageJson.put("user_id", String.valueOf(messageObj.getUserId()));

                messageJson.put("full_name", messageObj.getfromName());
                messageJson.put("message", messageObj.getMessage());
                messageJson.put("user_type", ANDROID_USER);
                messageJson.put("date_time", messageObj.getSentAtUtc());
                messageJson.put("message_index", messageObj.getMessageIndex());
                messageJson.put("is_typing", TYPING_SHOW_MESSAGE);
                messageJson.put("message_status", messageObj.getMessageStatus());

                if (messageObj.getUrl().isEmpty()) {
                    messageJson.put("message_type", messageObj.getMessageType());
                    mClient.publish("/" + String.valueOf(channelId), messageJson);

                } else {
                    messageJson.put("message_type", messageObj.getMessageType());
                    uploadFileServerCall(messageObj.getUrl(), channelId, messageJson, messageObj.getMessageType());
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void setUpFayeConnection() {
        // Set FayeClient listener
        mClient.setListener(new FayeClientListener() {
                                @Override
                                public void onConnectedServer(FayeClient fc) {

                                    FuguLog.e(TAG, "==" + unsentMessageMap.keySet());

                                    for (Long key : unsentMessageMap.keySet()) {

                                        if (FuguChatActivity.currentChannelId.compareTo(key) != 0) {
                                            fc.subscribeChannel("/" + String.valueOf(key));
                                            Calendar c = Calendar.getInstance();
                                            String date = DateUtils.getFormattedDate(c.getTime());
                                            int currentTime = DateUtils.getTimeInMinutes(date);
                                            if (CommonData.getMessageResponse(key) != null) {
                                                ArrayList<Message> message = CommonData.getMessageResponse(key).getData().getMessages();
                                                int lastSentTime = DateUtils.getTimeInMinutes(message.get(message.size() - 1).getSentAtUtc()) + 330;
                                                int difference = currentTime - lastSentTime;
                                                if (difference >= 1) {

                                                } else {
//                                                    publiFuguMeshUnsentMessages(key, unsentMessageMap.get(key));
                                                }
                                            }
//                            FuguLog.e("currentTime", currentTime + "");
//                                            publishUnsentMessages(key, unsentMessageMap.get(key));
                                        }
                                    }

                                }

                                @Override
                                public void onDisconnectedServer(FayeClient fc) {
                                    FuguLog.e(TAG, "Disconnected");
                                }


                                @Override
                                public void onReceivedMessage(FayeClient fc, String msg, String channel) {

                                    FuguLog.e(TAG, "FuguMessage: " + msg);

                                    Long channelId = Long.parseLong(channel.substring(1));

                                    try {
                                        final JSONObject messageJson = new JSONObject(msg);

                                        if ((!messageJson.getString("message").isEmpty()
                                                || !messageJson.optString("image_url", "").isEmpty() ||
                                                !messageJson.optString("url", "").isEmpty())
                                                && FuguChatActivity.currentChannelId.compareTo(channelId) != 0) {

                                            if (unsentMessageMap.get(channelId).size() == 1) {
                                                unsentMessageMap.remove(channelId);
                                                CommonData.removeUnsentMessageMapChannel(channelId);
                                            } else {
                                                unsentMessageMap.get(channelId).remove(messageJson.getString("UUID"));
                                                CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMap.get(channelId));
                                            }

                                            try {
                                                if (mClient.isConnectedServer() && CommonData.getUnsentMessageMap().size() == 0
                                                        && FuguChatActivity.currentChannelId.compareTo(channelId) != 0) {
                                                    mClient.unsubscribeAll();
                                                    mClient.disconnectServer();
                                                }
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    } catch (JSONException e) {

                                    }

                                }
                            }
        );

        // Connect to server
        if (!mClient.isConnectedServer()) {
            mClient.connectServer();
        }
    }

    public static void uploadFileServerCall(String file, final Long channelId, final JSONObject messageJson, final int messageType) {


        MultipartParams.Builder multipartBuilder = new MultipartParams.Builder();
        MultipartParams multipartParams = multipartBuilder
                .add("app_secret_key", CommonData.getAppSecretKey())
                .add(APP_VERSION, BuildConfig.VERSION_NAME)
                .add(DEVICE_TYPE, 1)
                .addFile("file", new File(file)).build();


        FuguLog.v("map = ", multipartParams.getMap().toString());
        RestClient.getApiInterface()
                .uploadFile(multipartParams.getMap())
                .enqueue(new ResponseResolver<FuguUploadImageResponse>() {
                    @Override
                    public void success(FuguUploadImageResponse fuguUploadImageResponse) {

                        try {

                            if (messageType == IMAGE_MESSAGE) {
                                messageJson.put("image_url", fuguUploadImageResponse.getData().getUrl());
                                messageJson.put("thumbnail_url", fuguUploadImageResponse.getData().getThumbnailUrl());
                            } else {
                                messageJson.put("url", fuguUploadImageResponse.getData().getUrl());
                            }

                            mClient.publish("/" + String.valueOf(channelId), messageJson);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void failure(APIError error) {
                        if (unsentMessageMap.get(channelId).size() == 1) {
                            unsentMessageMap.remove(channelId);
                            CommonData.removeUnsentMessageMapChannel(channelId);
                        } else {
                            try {
                                unsentMessageMap.get(channelId).remove(messageJson.getString("UUID"));
                                CommonData.setUnsentMessageMapByChannel(channelId, unsentMessageMap.get(channelId));
                            } catch (JSONException e) {

                            }
                        }
                    }
                });
    }
}
