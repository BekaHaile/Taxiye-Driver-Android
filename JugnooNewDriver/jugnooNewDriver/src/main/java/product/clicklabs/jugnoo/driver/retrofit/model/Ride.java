package product.clicklabs.jugnoo.driver.retrofit.model;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Ride {
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("user_id")
    @Expose
    private Integer userId;
    @SerializedName("request_latitude")
    @Expose
    private Double requestLatitude;
    @SerializedName("request_longitude")
    @Expose
    private Double requestLongitude;
    @SerializedName("request_address")
    @Expose
    private String requestAddress;
    @SerializedName("engagement_id")
    @Expose
    private String engagementId;
    @SerializedName("ride_type")
    @Expose
    private String rideType;
    @SerializedName("drop_location_address")
    @Expose
    private String dropLocationAddress;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("user_image")
    @Expose
    private String userImage;
    @SerializedName("reverse_bid")
    @Expose
    private Integer reverseBid;
    @SerializedName("can_accept_request")
    @Expose
    private Integer canAcceptRequest;
    @SerializedName("distance")
    @Expose
    private double distance;
    @SerializedName("fare")
    @Expose
    private String fare;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
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

    public String getEngagementId() {
        return engagementId.isEmpty()?"0":engagementId;
    }

    public void setEngagementId(String engagementId) {
        this.engagementId = engagementId;
    }

    public String getRideType() {
        return rideType;
    }

    public void setRideType(String rideType) {
        this.rideType = rideType;
    }

    public String getDropLocationAddress() {
        return dropLocationAddress;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getReverseBid() {
        return reverseBid;
    }

    public void setReverseBid(Integer reverseBid) {
        this.reverseBid = reverseBid;
    }

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public Integer getCanAcceptRequest() {
        return canAcceptRequest;
    }

    public void setCanAcceptRequest(Integer canAcceptRequest) {
        this.canAcceptRequest = canAcceptRequest;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public String getFare() {
        return fare;
    }

    public void setFare(String fare) {
        this.fare = fare;
    }
}
