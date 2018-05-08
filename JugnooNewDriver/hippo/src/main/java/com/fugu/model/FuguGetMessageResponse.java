package com.fugu.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bhavya Rattan on 09/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class FuguGetMessageResponse {

    public FuguGetMessageResponse() {
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

    @SerializedName("statusCode")
    @Expose
    private Integer statusCode;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Data data = new Data();

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public class Data {

        public Data() {
        }

        public void setMessages(ArrayList<Message> messages) {
            this.messages = messages;
        }

        @SerializedName("messages")
        @Expose
        private ArrayList<Message> messages = new ArrayList<>();
        @SerializedName("label")
        @Expose
        private String label;

        public void setLabel(String label) {
            this.label = label;
        }

        public void setFullName(String fullName) {
            this.fullName = fullName;
        }

        public void setOnSubscribe(int onSubscribe) {
            this.onSubscribe = onSubscribe;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        @SerializedName("full_name")
        @Expose
        private String fullName;
        @SerializedName("on_subscribe")
        @Expose
        private int onSubscribe;
        @SerializedName("page_size")
        @Expose
        private int pageSize;
        @SerializedName("channel_id")
        @Expose
        private Long channelId;

        public void setStatus(Integer status) {
            this.status = status;
        }

        @SerializedName("status")
        @Expose
        private Integer status;
        @SerializedName("business_name")
        @Expose
        private String businessName;

        public ArrayList<Message> getMessages() {
            return messages;
        }

        public String getLabel() {
            return label;
        }

        public String getFullName() {
            return fullName;
        }

        public int getOnSubscribe() {
            return onSubscribe;
        }

        public int getPageSize() {
            return pageSize;
        }


        public Long getChannelId() {
            return channelId;
        }

        public void setChannelId(Long channelId) {
            this.channelId = channelId;
        }

        public Integer getStatus() {
            return status;
        }

        public String getBusinessName() {
            return businessName;
        }

        public void setBusinessName(String businessName) {
            this.businessName = businessName;
        }

    }

}