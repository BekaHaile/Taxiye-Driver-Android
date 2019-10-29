package com.fugu;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;

import com.fugu.activity.FuguChatActivity;
import com.fugu.constant.FuguAppConstant;
import com.fugu.database.CommonData;
import com.fugu.model.FuguConversation;
import com.fugu.service.FuguPushIntentService;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

import io.paperdb.Paper;

/**
 * Created by Bhavya Rattan on 19/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class HippoNotificationConfig implements FuguAppConstant {

    public static String hippoDeviceToken = "";
    public static final String CHANNEL_ONE_ID = "com.fugu.ONE";
    public static Long pushChannelId = -1L;
    public static final String CHANNEL_ONE_NAME = "Default notification";

    public void setNotificationSoundEnabled(boolean notificationSoundEnabled) {
        this.notificationSoundEnabled = notificationSoundEnabled;
    }

    public void setSmallIcon(int smallIcon) {
        this.smallIcon = smallIcon;
    }

    public void setLargeIcon(int largeIcon) {
        this.largeIcon = largeIcon;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }


    private boolean notificationSoundEnabled = true;
    private int smallIcon = -1, largeIcon;
    private int priority;

    private static NotificationManager notificationManager;

//    private int getAppIcon() {
//        return activity.getResources().getIdentifier("ic_launcher", "mipmap", activity.getPackageName());
//        return  R.drawable.default_notif_icon;
//    }

    public static void updateFcmRegistrationToken(String deviceToken) {
        hippoDeviceToken = deviceToken;
    }

    public boolean isHippoNotification(final Map<String, String> data) {

        if (data.containsKey("push_source") && data.get("push_source").equalsIgnoreCase("FUGU"))
            return true;
        else
            return false;
    }

    public static void handleHippoPushNotification(Context context, Bundle bundle) {

        if (bundle != null) {

            FuguConversation conversation = new Gson().fromJson(bundle.getString(FuguAppConstant.CONVERSATION), FuguConversation.class);

            if (conversation != null && conversation.isStartChannelsActivity() && HippoConfig.getInstance() != null &&
                    !HippoConfig.getInstance().isDataCleared()) {

                Intent conversationIntent = new Intent(context, FuguChatActivity.class);

                conversationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                context.startActivity(conversationIntent);

            }
        } else
            return;
    }

    public static void cancelNotification(Context ctx, int notifyId) {
        String ns = Context.NOTIFICATION_SERVICE;
        NotificationManager nMgr = (NotificationManager) ctx.getSystemService(ns);
        nMgr.cancel(notifyId);
    }

    /**
     * @param data notification data
     */
    public void showNotification(final Context context, final Map<String, String> data) {
        Paper.init(context);
        CommonData.setPushBoolean(true);
        try {
            JSONObject messageJson = new JSONObject(data.get("message"));

            Intent mIntent = new Intent(NOTIFICATION_INTENT);
            Bundle dataBundle = new Bundle();
            for (String key : data.keySet()) {
                dataBundle.putString(key, data.get(key));
            }
            mIntent.putExtras(dataBundle);
            LocalBroadcastManager.getInstance(context).sendBroadcast(mIntent);

//            FuguLog.e("notification pushChannelId", "==" + FuguChatActivity.pushChannelId);

//            if(FuguChatActivity.pushChannelId.compareTo(messageJson.getLong("channel_id")) == 0){
//                FuguChatActivity.notificationIds.add((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE));
//            }
            if (pushChannelId != null) {
                if (pushChannelId.compareTo(messageJson.getLong("channel_id")) != 0) {
                    Paper.init(context);
                    Intent notificationIntent = new Intent(context, FuguPushIntentService.class);

                    notificationIntent.putExtra("channelId", messageJson.getLong("channel_id"));
                    notificationIntent.putExtra("en_user_id", CommonData.getUserDetails().getData().getEn_user_id());
                    notificationIntent.putExtra("userId", CommonData.getUserDetails().getData().getUserId());
                    //notificationIntent.putExtra("labelId", messageJson.optLong("label_id", -1));

                    Bundle mBundle = new Bundle();
                    for (String key : data.keySet()) {
                        mBundle.putString(key, data.get(key));
                    }
                    notificationIntent.putExtra("data", mBundle);
                    //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                    PendingIntent pi = PendingIntent.getService(context, (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE)
                            , notificationIntent, 0);

                    int notificationDefaults = Notification.DEFAULT_ALL;
                    if (!notificationSoundEnabled)
                        notificationDefaults = Notification.DEFAULT_LIGHTS;

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context, CHANNEL_ONE_ID)
                            //     .setTicker(r.getString(R.string.app_name))
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(messageJson.getString("new_message")))
                            //.setSmallIcon(android.R.color.transparent)
                            .setSmallIcon(smallIcon == -1 ? R.drawable.default_notif_icon : smallIcon)
                            .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                            .setContentTitle(messageJson.getString("title"))
                            .setContentText(messageJson.getString("new_message"))
                            .setContentIntent(pi)
                            .setDefaults(notificationDefaults)
                            .setPriority(priority)
                            .setAutoCancel(true);
                    mBuilder.setChannelId(CHANNEL_ONE_ID);
                    Notification notification = mBuilder.build();

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                        int smallIconViewId = context.getResources().getIdentifier("right_icon", "id", "android");

                        if (smallIconViewId != 0) {
                            if (notification.contentIntent != null)
//                                notification.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                                if (notification.headsUpContentView != null)
                                    notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                            if (notification.bigContentView != null)
                                notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                        }
                    }

                    notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), notification);

                } else {
//                    Uri defaultRingtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//                    MediaPlayer mediaPlayer = new MediaPlayer();
//
//                    try {
//                        mediaPlayer.setDataSource(context, defaultRingtoneUri);
//                        mediaPlayer.setAudioStreamType(AudioManager.STREAM_NOTIFICATION);
//
//                        mediaPlayer.prepare();
//                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//                            @Override
//                            public void onCompletion(MediaPlayer mp) {
//                                mp.release();
//
//                                PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
//                                    if (!pm.isInteractive()) {
//                                        ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(500);
//                                    }
//                                } else {
//                                    if (!pm.isScreenOn()) {
//                                        ((Vibrator) context.getSystemService(VIBRATOR_SERVICE)).vibrate(500);
//                                    }
//                                }
//                            }
//                        });
//                        mediaPlayer.start();
//                    } catch (IllegalArgumentException e) {
//                        e.printStackTrace();
//                    } catch (SecurityException e) {
//                        e.printStackTrace();
//                    } catch (IllegalStateException e) {
//                        e.printStackTrace();
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
                }
            } else {
                Intent notificationIntent = new Intent(context, FuguPushIntentService.class);

                notificationIntent.putExtra("channelId", messageJson.getLong("channel_id"));
                notificationIntent.putExtra("userId", CommonData.getUserDetails().getData().getUserId());
                //notificationIntent.putExtra("labelId", messageJson.optLong("label_id", -1));

                Bundle mBundle = new Bundle();
                for (String key : data.keySet()) {
                    mBundle.putString(key, data.get(key));
                }
                notificationIntent.putExtra("data", mBundle);
                //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent pi = PendingIntent.getService(context, (int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE)
                        , notificationIntent, 0);

                int notificationDefaults = Notification.DEFAULT_ALL;
                if (!notificationSoundEnabled)
                    notificationDefaults = Notification.DEFAULT_LIGHTS;

                Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                        //     .setTicker(r.getString(R.string.app_name))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageJson.getString("new_message")))
                        //.setSmallIcon(android.R.color.transparent)
                        .setSmallIcon(smallIcon == -1 ? R.drawable.default_notif_icon : smallIcon)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), largeIcon))
                        .setContentTitle(messageJson.getString("title"))
                        .setContentText(messageJson.getString("new_message"))
                        .setContentIntent(pi)
                        .setDefaults(notificationDefaults)
                        .setPriority(priority)
                        .setAutoCancel(true).setChannelId(CHANNEL_ID)
                        .build();


                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

                    int smallIconViewId = context.getResources().getIdentifier("right_icon", "id", "android");

                    if (smallIconViewId != 0) {
                        if (notification.contentIntent != null)
//                            notification.contentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                            if (notification.headsUpContentView != null)
                                notification.headsUpContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);

                        if (notification.bigContentView != null)
                            notification.bigContentView.setViewVisibility(smallIconViewId, View.INVISIBLE);
                    }
                }

                notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify((int) ((new Date().getTime() / 1000L) % Integer.MAX_VALUE), notification);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ONE_ID,
                    CHANNEL_ONE_NAME, NotificationManager.IMPORTANCE_HIGH);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
//            return new Notification.Builder(context, CHANNEL_ONE_ID);
        }
    }

}
