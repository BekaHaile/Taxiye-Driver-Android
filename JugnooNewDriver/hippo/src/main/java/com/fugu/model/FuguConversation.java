package com.fugu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fugu.datastructure.ChannelStatus;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhavya Rattan on 10/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguConversation implements Parcelable, Cloneable {

    @SerializedName("channel_id")
    @Expose
    private Long channelId = -1l;
    @SerializedName("label_id")
    @Expose
    private Long labelId = -1l;
    @SerializedName("user_id")
    @Expose
    private Long userId = -1l;
    @SerializedName("en_user_id")
    @Expose
    private String enUserId = "";
    @SerializedName("last_sent_by_full_name")
    @Expose
    private String last_sent_by_full_name;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    protected FuguConversation(Parcel in) {
        if (in.readByte() == 0) {
            channelId = null;
        } else {
            channelId = in.readLong();
        }
        if (in.readByte() == 0) {
            labelId = null;
        } else {
            labelId = in.readLong();
        }
        if (in.readByte() == 0) {
            userId = null;
        } else {
            userId = in.readLong();
        }
        enUserId = in.readString();
        last_sent_by_full_name = in.readString();
        message = in.readString();
        dateTime = in.readString();
        label = in.readString();
        status = in.readInt();
        channelStatus = in.readInt();
        channelImage = in.readString();
        isOpenChat = in.readByte() != 0;
        channelName = in.readString();
        transactionId = in.readString();
        message_type = in.readInt();
        if (in.readByte() == 0) {
            last_sent_by_id = null;
        } else {
            last_sent_by_id = in.readLong();
        }
        isTimeSet = in.readInt();
        userName = in.readString();
        last_message_status = in.readInt();
        defaultMessage = in.readString();
        default_message = in.readString();
        businessName = in.readString();
        unreadCount = in.readInt();
        startChannelsActivity = in.readByte() != 0;
        chatType = in.readInt();
    }

    public static final Creator<FuguConversation> CREATOR = new Creator<FuguConversation>() {
        @Override
        public FuguConversation createFromParcel(Parcel in) {
            return new FuguConversation(in);
        }

        @Override
        public FuguConversation[] newArray(int size) {
            return new FuguConversation[size];
        }
    };

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    @SerializedName("message")
    @Expose
    private String message = "";
    @SerializedName("date_time")
    @Expose
    private String dateTime;
    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("status")
    @Expose
    private int status = 1;
    @SerializedName("channel_status")
    @Expose
    private int channelStatus = ChannelStatus.OPEN.getOrdinal();
    @SerializedName("channel_image")
    @Expose
    private String channelImage = "";
    @SerializedName("isOpenChat")
    @Expose
    private boolean isOpenChat;
    @SerializedName("custom_label")
    @Expose
    private String channelName = null;
   /* @SerializedName("tags")
    @Expose
    private JsonArray tags = null;*/
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("message_type")
    @Expose
    private int message_type;
    @SerializedName("last_sent_by_id")
    @Expose
    private Long last_sent_by_id;

    private int isTimeSet = 0;

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserName() {
        return userName;
    }

    @SerializedName("userName")
    @Expose
    private String userName = "Anonymous";

    @SerializedName("last_message_status")
    @Expose
    private int last_message_status = 2;

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public void setDefaultMessage(String defaultMessage) {
        this.defaultMessage = defaultMessage;
    }

    @SerializedName("defaultMessage")
    @Expose
    private String defaultMessage = "";

    @SerializedName("default_message")
    @Expose
    private String default_message = "";

    public String getBusinessName() {
        return businessName;
    }

    public void setBusinessName(String businessName) {
        this.businessName = businessName;
    }

    @SerializedName("businessName")
    @Expose
    private String businessName = "";

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    @SerializedName("unread_count")
    @Expose
    private int unreadCount = 0;

    public boolean isStartChannelsActivity() {
        return startChannelsActivity;
    }

    public void setStartChannelsActivity(boolean startChannelsActivity) {
        this.startChannelsActivity = startChannelsActivity;
    }

    @SerializedName("startChannelsActivity")
    @Expose
    private boolean startChannelsActivity = false;

    @SerializedName("chat_type")
    private int chatType;

    public int getChatType() {
        return chatType;
    }

    public void setChatType(final int chatType) {
        this.chatType = chatType;
    }

    public String getChannelImage() {
        return channelImage;
    }

    public Long getChannelId() {
        return channelId;
    }

    public Long getLabelId() {
        return labelId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDateTime() {
        return dateTime;
    }

    public int getStatus() {
        return status;
    }

    public String getLabel() {
        return label;
    }

    public boolean isOpenChat() {
        return isOpenChat;
    }

    public void setOpenChat(boolean openChat) {
        isOpenChat = openChat;
    }

    public FuguConversation(Long channelId, String message, String dateTime, String last_sent_by_full_name) {
        this.channelId = channelId;
        this.message = message;
        this.dateTime = dateTime;
        this.last_sent_by_full_name = last_sent_by_full_name;
    }

    public FuguConversation() {
    }

    public void setChannelId(Long channelId) {
        this.channelId = channelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setChannelImage(String channelImage) {
        this.channelImage = channelImage;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    /*public JsonArray getTags() {
        return tags;
    }

    public void setTags(JsonArray tags) {
        this.tags = tags;
    }
*/
    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public int getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(int channelStatus) {
        this.channelStatus = channelStatus;
    }

    public int getIsTimeSet() {
        return isTimeSet;
    }

    public void setIsTimeSet(int isTimeSet) {
        this.isTimeSet = isTimeSet;
    }

    public String getLast_sent_by_full_name() {
        return last_sent_by_full_name;
    }

    public void setLast_sent_by_full_name(String last_sent_by_full_name) {
        this.last_sent_by_full_name = last_sent_by_full_name;
    }

    public Long getLast_sent_by_id() {
        return last_sent_by_id;
    }

    public void setLast_sent_by_id(Long last_sent_by_id) {
        this.last_sent_by_id = last_sent_by_id;
    }

    public int getLast_message_status() {
        return last_message_status;
    }

    public String getDefault_message() {
        return default_message;
    }

    public void setDefault_message(String default_message) {
        this.default_message = default_message;
    }

    public void setLast_message_status(int last_message_status) {
        this.last_message_status = last_message_status;

    }

    public String getEnUserId() {
        return enUserId;
    }

    public void setEnUserId(String enUserId) {
        this.enUserId = enUserId;
    }

    public int getMessage_type() {
        return message_type;
    }

    public void setMessage_type(int message_type) {
        this.message_type = message_type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        if (channelId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(channelId);
        }
        if (labelId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(labelId);
        }
        if (userId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(userId);
        }
        dest.writeString(enUserId);
        dest.writeString(last_sent_by_full_name);
        dest.writeString(message);
        dest.writeString(dateTime);
        dest.writeString(label);
        dest.writeInt(status);
        dest.writeInt(channelStatus);
        dest.writeString(channelImage);
        dest.writeByte((byte) (isOpenChat ? 1 : 0));
        dest.writeString(channelName);
        dest.writeString(transactionId);
        dest.writeInt(message_type);
        if (last_sent_by_id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeLong(last_sent_by_id);
        }
        dest.writeInt(isTimeSet);
        dest.writeString(userName);
        dest.writeInt(last_message_status);
        dest.writeString(defaultMessage);
        dest.writeString(default_message);
        dest.writeString(businessName);
        dest.writeInt(unreadCount);
        dest.writeByte((byte) (startChannelsActivity ? 1 : 0));
        dest.writeInt(chatType);
    }
}
