package com.fugu.model;

import com.fugu.constant.FuguAppConstant;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by Bhavya Rattan on 12/06/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class Message {
    @SerializedName("uuid")
    @Expose
    String uuid;
    @SerializedName("isSent")
    @Expose
    boolean isSent;
    @SerializedName("full_name")
    @Expose
    private String fromName;
    @SerializedName("id")
    @Expose
    private Long id;
    @SerializedName("user_id")
    @Expose
    private Long userId;

    public void setSentAtUtc(String sentAtUtc) {
        this.sentAtUtc = sentAtUtc;
    }

    @SerializedName("date_time")
    @Expose
    private String sentAtUtc = "";
    @SerializedName("message")
    @Expose
    private String message;

    public void setMessageStatus(Integer messageStatus) {
        this.messageStatus = messageStatus;
    }

    @SerializedName("message_status")
    @Expose
    private Integer messageStatus;
    @SerializedName("thumbnail_url")
    @Expose
    private String thumbnailUrl = "";
    @SerializedName("image_url")
    @Expose
    private String url = "";
    @SerializedName("message_type")
    @Expose
    private int messageType = FuguAppConstant.TEXT_MESSAGE;

    @SerializedName("custom_action")
    private CustomAction customAction;

    public void setCustomAction(final CustomAction customAction) {
        this.customAction = customAction;
    }

    public CustomAction getCustomAction() {
        return customAction;
    }

    public Long getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(Long downloadId) {
        this.downloadId = downloadId;
    }

    private Long downloadId;

    public String getFileExtension() {
        return fileExtension;
    }

    public void setFileExtension(String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    @SerializedName("file_name")
    @Expose
    String fileName = "";
    @SerializedName("file_size")
    @Expose
    String fileSize = "";
    @SerializedName("file_extension")
    @Expose
    String fileExtension = "";
    @SerializedName("file_path")
    @Expose
    String filePath = "";

    private int messageIndex = 0;
    private int timeIndex = 0;
    private boolean isSelf;

    public Integer getMessageStatus() {
        return messageStatus;
    }

    public String getfromName() {
        return fromName;
    }

    public Long getUserId() {
        return userId;
    }

    public String getSentAtUtc() {
        return sentAtUtc;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public String getUrl() {
        return url;
    }

    public int getMessageType() {
        return messageType;
    }

    public Long getId() {
        if (id == null) {
            id = -1l;
        }
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Message() {
    }


    public Message(String fromName, Long userId, String message, String sentAtUtc, boolean isSelf,
                   int messageStatus, int messageIndex, int messageType, boolean isSent, String uuid) {
        this.fromName = fromName;
        this.userId = userId;
        this.message = message;
        this.sentAtUtc = sentAtUtc;
        this.isSelf = isSelf;
        this.messageStatus = messageStatus;
        this.messageIndex = messageIndex;
        this.messageType = messageType;
        this.isSent = isSent;
        this.uuid = uuid;
    }

    public Message(long id, String fromName, Long userId, String message, String sentAtUtc, boolean isSelf,
                   int messageStatus, int messageIndex, String url, String thumbnailUrl, int messageType, boolean isSent, String uuid) {
        this.id = id;
        this.fromName = fromName;
        this.userId = userId;
        this.message = message;
        this.sentAtUtc = sentAtUtc;
        this.isSelf = isSelf;
        this.messageStatus = messageStatus;
        this.messageIndex = messageIndex;
        this.thumbnailUrl = thumbnailUrl;
        this.messageType = messageType;
        this.url = url;
        this.isSent = isSent;
        this.uuid = uuid;
    }

    public int getMessageIndex() {
        return messageIndex;
    }

    public boolean isSelf() {
        return isSelf;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void setTimeIndex(int timeIndex) {
        this.timeIndex = timeIndex;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
