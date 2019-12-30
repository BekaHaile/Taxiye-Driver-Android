package com.fugu.service;

import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.content.LocalBroadcastManager;

import com.fugu.HippoConfig;
import com.fugu.HippoNotificationConfig;
import com.fugu.activity.FuguChatActivity;
import com.fugu.constant.FuguAppConstant;
import com.fugu.model.FuguConversation;
import com.google.gson.Gson;

import static com.fugu.constant.FuguAppConstant.NOTIFICATION_TAPPED;

/**
 * Created by Bhavya Rattan on 26/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguPushIntentService extends IntentService {

    private static final String TAG = "FuguPushIntentService";

    public FuguPushIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent notificationIntent;


        if (HippoConfig.getInstance() != null && !HippoConfig.getInstance().isDataCleared()) {
            notificationIntent = new Intent(this, FuguChatActivity.class);

            FuguConversation conversation = new FuguConversation();
            conversation.setChannelId(intent.getLongExtra("channelId", -1l));
            conversation.setEnUserId(intent.getStringExtra("en_user_id"));
            conversation.setUserId(intent.getLongExtra("userId",-1L));

            if (HippoNotificationConfig.pushChannelId.compareTo(-1l) == 0) {
                notificationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(notificationIntent);
            } else {
                Intent mIntent = new Intent(NOTIFICATION_TAPPED);
                mIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));
                LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);
            }
        } else {
            PackageManager pm = this.getPackageManager();
            notificationIntent = pm.getLaunchIntentForPackage(this.getPackageName());

            FuguConversation conversation = new FuguConversation();
            conversation.setChannelId(intent.getLongExtra("channelId", -1l));
            conversation.setLabel("");
            //conversation.setOpenChat(true);
            conversation.setEnUserId(intent.getStringExtra("en_user_id"));
            conversation.setUserId(intent.getLongExtra("userId",-1L));
            conversation.setStartChannelsActivity(true);

            notificationIntent.putExtra(FuguAppConstant.CONVERSATION, new Gson().toJson(conversation, FuguConversation.class));

            startActivity(notificationIntent);
        }
    }
}
