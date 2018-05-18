package com.fugu;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.HashMap;

/**
 * Created by Bhavya Rattan on 15/05/17
 * Click Labs
 * bhavya.rattan@click-labs.com
 */

public class CaptureUserData implements Parcelable {

    private String userUniqueKey = "";
    private String fullName = "";
    private String email = "";
    private String phoneNumber = "";
    private double latitude = 0;
    private double longitude = 0;
    private Long userId = -1l;
    private String enUserId = "";
    private String addressLine1 = "";
    private String addressLine2 = "";
    private String region = "";
    private String city = "";
    private String country = "";
    private String zipCode = "";
    private HashMap<String, String> custom_attributes = new HashMap<>();
    private String channelInfoJson;

    public String getChannelInfoJson() {
        return channelInfoJson;
    }

    public HashMap<String, String> getCustom_attributes() {
        return custom_attributes;
    }

    public void setCustom_attributes(HashMap<String, String> custom_attributes) {
        this.custom_attributes = custom_attributes;
    }

    public String getUserUniqueKey() {
        return userUniqueKey;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getRegion() {
        return region;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getZipCode() {
        return zipCode;
    }

    public String getEnUserId() {
        return enUserId;
    }

    public void setEnUserId(String enUserId) {
        this.enUserId = enUserId;
    }

    public static class Builder implements Parcelable {
        private CaptureUserData captureUserData = new CaptureUserData();

        public Builder userUniqueKey(String userUniqueKey) {
            captureUserData.userUniqueKey = userUniqueKey;
            return this;
        }

        public Builder fullName(String fullName) {
            captureUserData.fullName = fullName;
            return this;
        }

        public Builder email(String email) {
            captureUserData.email = email;
            return this;
        }

        public Builder phoneNumber(String phoneNumber) {
            captureUserData.phoneNumber = phoneNumber;
            return this;
        }

        public Builder addressLine1(String addressLine1) {
            captureUserData.addressLine1 = addressLine1;
            return this;
        }

        public Builder addressLine2(String addressLine2) {
            captureUserData.addressLine2 = addressLine2;
            return this;
        }

        public Builder region(String region) {
            captureUserData.region = region;
            return this;
        }

        public Builder city(String city) {
            captureUserData.city = city;
            return this;
        }

        public Builder country(String country) {
            captureUserData.country = country;
            return this;
        }

        public Builder zipCode(String zipCode) {
            captureUserData.zipCode = zipCode;
            return this;
        }

        public Builder latitude(double latitude) {
            captureUserData.latitude = latitude;
            return this;
        }

        public Builder longitude(double longitude) {
            captureUserData.longitude = longitude;
            return this;
        }

        public Builder customAttributes(HashMap<String, String> customAttributes) {
            captureUserData.custom_attributes = customAttributes;
            return this;
        }

        public Builder channelInfoJson(String channelInfoJson) {
            captureUserData.channelInfoJson = channelInfoJson;
            return this;
        }

        public CaptureUserData build() {
            return captureUserData;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeParcelable(this.captureUserData, flags);
        }

        public Builder() {
        }

        protected Builder(Parcel in) {
            this.captureUserData = in.readParcelable(CaptureUserData.class.getClassLoader());
        }

        public final Creator<Builder> CREATOR = new Creator<Builder>() {
            @Override
            public Builder createFromParcel(Parcel source) {
                return new Builder(source);
            }

            @Override
            public Builder[] newArray(int size) {
                return new Builder[size];
            }
        };
    }

    public CaptureUserData() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userUniqueKey);
        dest.writeString(this.fullName);
        dest.writeString(this.email);
        dest.writeString(this.phoneNumber);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
        dest.writeValue(this.userId);
        dest.writeString(this.enUserId);
        dest.writeString(this.addressLine1);
        dest.writeString(this.addressLine2);
        dest.writeString(this.region);
        dest.writeString(this.city);
        dest.writeString(this.country);
        dest.writeString(this.zipCode);
        dest.writeSerializable(this.custom_attributes);
        dest.writeString(channelInfoJson);
    }

    protected CaptureUserData(Parcel in) {
        this.userUniqueKey = in.readString();
        this.fullName = in.readString();
        this.email = in.readString();
        this.phoneNumber = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
        this.userId = (Long) in.readValue(Long.class.getClassLoader());
        this.enUserId = in.readString();
        this.addressLine1 = in.readString();
        this.addressLine2 = in.readString();
        this.region = in.readString();
        this.city = in.readString();
        this.country = in.readString();
        this.zipCode = in.readString();
        this.custom_attributes = (HashMap<String, String>) in.readSerializable();
        this.channelInfoJson = in.readString();
    }

    public static final Creator<CaptureUserData> CREATOR = new Creator<CaptureUserData>() {
        @Override
        public CaptureUserData createFromParcel(Parcel source) {
            return new CaptureUserData(source);
        }

        @Override
        public CaptureUserData[] newArray(int size) {
            return new CaptureUserData[size];
        }
    };
}
