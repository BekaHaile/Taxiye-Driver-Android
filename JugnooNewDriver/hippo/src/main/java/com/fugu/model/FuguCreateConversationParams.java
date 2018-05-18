package com.fugu.model;

import com.fugu.BuildConfig;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bhavya on 22/08/17.
 */

public class FuguCreateConversationParams {

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
    private JsonArray tags = null;
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

    public void setIsSupportTicket(int isSupportTicket) {
        this.isSupportTicket = isSupportTicket;
    }

    @SerializedName("in_app_support_channel")
    @Expose
    private int isSupportTicket;

    public void setCustomAttributes(CustomAttributes customAttributes) {
        this.customAttributes = customAttributes;
    }

    @SerializedName("custom_attributes")
    @Expose
    public CustomAttributes customAttributes;

    public FuguCreateConversationParams(String appSecretKey, Long labelId,
                                        String transactionId, String userUniqueKey, JsonArray otherUserUniqueKeys,
                                        String channelName, JsonArray tags,String enUserId) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.transactionId = transactionId;
        this.userUniqueKey = userUniqueKey;
        this.otherUserUniqueKeys = otherUserUniqueKeys;
        this.deviceType = 1;
        this.enUserId=enUserId;
        this.appVersion = BuildConfig.VERSION_NAME;
        if (otherUserUniqueKeys != null) {
            if (otherUserUniqueKeys.size() > 1) {
                this.chatType = 2;
            } else {
                this.chatType = 1;
            }
        } else {
            this.chatType = 0;
        }

        this.channelName = channelName;
        this.tags = tags;
        this.customAttributes = customAttributes;
        source = 1;
    }

    public FuguCreateConversationParams(String appSecretKey, Long labelId,
                                        String transactionId, Long userId,
                                        String channelName, JsonArray tags,String enUserId) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.transactionId = transactionId;
        this.userId = userId;
        this.chatType = 0;
        this.channelName = channelName;
        this.tags = tags;
        this.enUserId=enUserId;
        this.deviceType = 1;
        this.appVersion = BuildConfig.VERSION_NAME;
        source = 1;
    }

    public FuguCreateConversationParams(String appSecretKey, Long labelId, String enUserId) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.enUserId = enUserId;
        source = 1;
    }

    @Override
    public String toString() {
        return appSecretKey + ", " + labelId + ", " + userId + ", " + chatType;
    }

    public FuguCreateConversationParams(String appSecretKey, Long labelId,
                                        String transactionId, Long userId,
                                        String channelName, JsonArray tags, String[] user_first_messages,String enUserId) {
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
        source = 1;
    }

  public FuguCreateConversationParams(String appSecretKey, Long labelId, String transactionId, Long userId,
           String channelName, JsonArray tags, String[] user_first_messages, String enUserId, int isSupportTicket) {
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

    public FuguCreateConversationParams(String appSecretKey, Long labelId, String transactionId, Long userId,
                                        String channelName, JsonArray tags, String[] user_first_messages,
                                        String enUserId, int isSupportTicket, CustomAttributes customAttributes) {
        this(appSecretKey, labelId, transactionId, userId, channelName, tags, user_first_messages, enUserId, isSupportTicket);
        this.customAttributes = customAttributes;

    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

}
