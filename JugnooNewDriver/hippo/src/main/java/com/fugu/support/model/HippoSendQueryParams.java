package com.fugu.support.model;

import com.fugu.BuildConfig;
import com.fugu.model.CustomAttributes;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by Gurmail S. Kang on 17/04/18.
 * @author gurmail
 */

public class HippoSendQueryParams {

    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("label_id")
    @Expose
    private Long labelId = -1l;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("user_unique_key")
    @Expose
    private String userUniqueKey;
    @SerializedName("other_user_unique_key")
    @Expose
    private JsonArray otherUserUniqueKeys;
    @SerializedName("chat_type")
    @Expose
    private int chatType = 0;
    @SerializedName("user_id")
    @Expose
    private Long userId;
    @SerializedName("en_user_id")
    @Expose
    private String enUserId;
    @SerializedName("custom_label")
    @Expose
    private String channelName = null;
    @SerializedName("tags")
    @Expose
    private ArrayList<TagsModel> tags;
    @SerializedName("user_first_messages")
    @Expose
    private String[] user_first_messages = null;
    @SerializedName("device_type")
    @Expose
    private int deviceType = 1;
    @SerializedName("app_version")
    @Expose
    private String appVersion = BuildConfig.VERSION_NAME;
    private int source;
    @SerializedName("in_app_support_channel")
    @Expose
    private int isSupportTicket;
    @SerializedName("custom_attributes")
    @Expose
    public CustomAttributes customAttributes;

    public void setIsSupportTicket(int isSupportTicket) {
        this.isSupportTicket = isSupportTicket;
    }

    public void setCustomAttributes(CustomAttributes customAttributes) {
        this.customAttributes = customAttributes;
    }


    @Override
    public String toString() {
        return appSecretKey + ", " + labelId + ", " + userId + ", " + chatType;
    }


    public HippoSendQueryParams(String appSecretKey, Long labelId, String transactionId, Long userId,
                                String channelName, ArrayList<TagsModel> tags, String[] user_first_messages, String enUserId, int isSupportTicket) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.transactionId = transactionId;
        this.userId = userId;
        this.chatType = 0;
        this.channelName = channelName;
        this.tags = tags;
        this.enUserId=enUserId;
        this.user_first_messages = user_first_messages;
        this.deviceType = 1;
        this.appVersion = BuildConfig.VERSION_NAME;
        this.isSupportTicket = isSupportTicket;
        source = 1;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }


}
