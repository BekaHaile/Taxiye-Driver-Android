package com.fugu.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by cl-macmini-01 on 2/2/18.
 */

public class FuguChannelInfoObject {

    @SerializedName("key")
    private String key;

    @SerializedName("values")
    private String value;

    @SerializedName("is_visible")
    private int isVisible;

    @SerializedName("filter_by_chat_type")
    private int filterByChatType;

    @SerializedName("chat_type_filter_value")
    private int chatTypeFilterValue;

    public boolean isFilterByChatType() {
        return filterByChatType == 1;
    }

    public void setFilterByChatType(final int filterByChatType) {
        this.filterByChatType = filterByChatType;
    }

    public int getChatTypeFilterValue() {
        return chatTypeFilterValue;
    }

    public void setChatTypeFilterValue(final int chatTypeFilterValue) {
        this.chatTypeFilterValue = chatTypeFilterValue;
    }

    public String getKey() {
        return key;
    }

    public void setKey(final String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    public boolean getIsVisible() {
        return isVisible == 1;
    }

    public void setIsVisible(final int isVisible) {
        this.isVisible = isVisible;
    }
}
