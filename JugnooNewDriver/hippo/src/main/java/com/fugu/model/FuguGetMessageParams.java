package com.fugu.model;

import com.fugu.BuildConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bhavya on 05/07/17.
 */

public class FuguGetMessageParams {
    @SerializedName("page_start")
    @Expose
    private Integer pageStart;
    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("channel_id")
    @Expose
    private Long channelId;
    @SerializedName("en_user_id")
    @Expose
    private String userId;
    @SerializedName("custom_label")
    @Expose
    private String channelName = null;
    @SerializedName("device_type")
    @Expose
    private int deviceType = 1;
    @SerializedName("app_version")
    @Expose
    private String appVersion = BuildConfig.VERSION_NAME;
    private int source;

    public FuguGetMessageParams(String appSecretKey, Long channelId,
                                String userId, Integer pageStart, String channelName) {
        this.appSecretKey = appSecretKey;
        this.channelId = channelId;
        this.userId = userId;
        this.pageStart = pageStart;
        this.channelName = channelName;
        this.deviceType = 1;
        this.appVersion = BuildConfig.VERSION_NAME;
        source = 1;
    }
}
