package com.fugu.model;

import com.fugu.BuildConfig;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by bhavya on 07/07/17.
 */

public class FuguGetByLabelIdParams {
    @SerializedName("page_start")
    @Expose
    private Integer pageStart;
    @SerializedName("app_secret_key")
    @Expose
    private String appSecretKey;
    @SerializedName("label_id")
    @Expose
    private Long labelId;
    @SerializedName("en_user_id")
    @Expose
    private String userId;
    @SerializedName("device_type")
    @Expose
    private int deviceType = 1;
    @SerializedName("app_version")
    @Expose
    private String appVersion = BuildConfig.VERSION_NAME;
    private int source;

    public FuguGetByLabelIdParams(String appSecretKey, Long labelId, String userId, Integer pageStart) {
        this.appSecretKey = appSecretKey;
        this.labelId = labelId;
        this.userId = userId;
        this.pageStart = pageStart;
        this.appVersion = BuildConfig.VERSION_NAME;
        this.deviceType = 1;
        source = 1;
    }
}
