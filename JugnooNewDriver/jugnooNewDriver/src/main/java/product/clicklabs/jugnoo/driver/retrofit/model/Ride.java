package product.clicklabs.jugnoo.driver.retrofit.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ride {

    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("request_latitude")
    @Expose
    private Double requestLatitude;
    @SerializedName("request_longitude")
    @Expose
    private Double requestLongitude;
    @SerializedName("request_address")
    @Expose
    private String requestAddress;
    @SerializedName("drop_location_address")
    @Expose
    private String dropLocationAddress;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("can_accept_request")
    @Expose
    private Integer canAcceptRequest;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Double getRequestLatitude() {
        return requestLatitude;
    }

    public void setRequestLatitude(Double requestLatitude) {
        this.requestLatitude = requestLatitude;
    }

    public Double getRequestLongitude() {
        return requestLongitude;
    }

    public void setRequestLongitude(Double requestLongitude) {
        this.requestLongitude = requestLongitude;
    }

    public String getRequestAddress() {
        return requestAddress;
    }

    public void setRequestAddress(String requestAddress) {
        this.requestAddress = requestAddress;
    }

    public String getDropLocationAddress() {
        return dropLocationAddress;
    }

    public void setDropLocationAddress(String dropLocationAddress) {
        this.dropLocationAddress = dropLocationAddress;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getCanAcceptRequest() {
        return canAcceptRequest;
    }

    public void setCanAcceptRequest(Integer canAcceptRequest) {
        this.canAcceptRequest = canAcceptRequest;
    }

}
