package product.clicklabs.jugnoo.driver.tutorial;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 2/7/17.
 */

public class TourResponseModel {

    @SerializedName("flag")
    @Expose
    public Integer flag;
    @SerializedName("responses")
    @Expose
    public Responses responses;



    public class AcceptResponse {

        @SerializedName("business_id")
        @Expose
        public Integer businessId;
        @SerializedName("reference_id")
        @Expose
        public Integer referenceId;
        @SerializedName("pickup_latitude")
        @Expose
        public Double pickupLatitude;
        @SerializedName("pickup_longitude")
        @Expose
        public Double pickupLongitude;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("drop_address")
        @Expose
        public String dropAddress;
        @SerializedName("op_drop_latitude")
        @Expose
        public Integer opDropLatitude;
        @SerializedName("op_drop_longitude")
        @Expose
        public Integer opDropLongitude;
        @SerializedName("current_latitude")
        @Expose
        public Double currentLatitude;
        @SerializedName("current_longitude")
        @Expose
        public Double currentLongitude;
        @SerializedName("is_pooled")
        @Expose
        public Integer isPooled;
        @SerializedName("free_ride")
        @Expose
        public Integer freeRide;
        @SerializedName("fare_factor")
        @Expose
        public Integer fareFactor;
        @SerializedName("fare_details")
        @Expose
        public FareDetails fareDetails;
        @SerializedName("coupon")
        @Expose
        public Coupon coupon;
        @SerializedName("promotion")
        @Expose
        public Promotion promotion;
        @SerializedName("meter_fare_applicable")
        @Expose
        public Integer meterFareApplicable;
        @SerializedName("luggage_charges")
        @Expose
        public Integer luggageCharges;
        @SerializedName("luggage_charges_applicable")
        @Expose
        public Integer luggageChargesApplicable;
        @SerializedName("waiting_charges_applicable")
        @Expose
        public Integer waitingChargesApplicable;
        @SerializedName("get_jugnoo_fare_enabled")
        @Expose
        public Integer getJugnooFareEnabled;
        @SerializedName("convenience_charge")
        @Expose
        public Integer convenienceCharge;
        @SerializedName("convenience_charge_waiver")
        @Expose
        public Integer convenienceChargeWaiver;
        @SerializedName("cached_api_enabled")
        @Expose
        public Integer cachedApiEnabled;
        @SerializedName("eta")
        @Expose
        public Integer eta;
        @SerializedName("chat_enabled")
        @Expose
        public Integer chatEnabled;

    }
    //-----------------------------------product.clicklabs.jugnoo.driver.tutorial.Coupon.java-----------------------------------



    public class Coupon {


    }
    //-----------------------------------product.clicklabs.jugnoo.driver.tutorial.FareDetails.java-----------------------------------



    public class FareDetails {

        @SerializedName("id")
        @Expose
        public Integer id;
        @SerializedName("fare_fixed")
        @Expose
        public Integer fareFixed;
        @SerializedName("fare_per_km")
        @Expose
        public Integer farePerKm;
        @SerializedName("fare_threshold_distance")
        @Expose
        public Integer fareThresholdDistance;
        @SerializedName("fare_per_km_threshold_distance")
        @Expose
        public Integer farePerKmThresholdDistance;
        @SerializedName("fare_per_km_after_threshold")
        @Expose
        public Integer farePerKmAfterThreshold;
        @SerializedName("fare_per_km_before_threshold")
        @Expose
        public Integer farePerKmBeforeThreshold;
        @SerializedName("fare_per_min")
        @Expose
        public Integer farePerMin;
        @SerializedName("fare_threshold_time")
        @Expose
        public Integer fareThresholdTime;
        @SerializedName("fare_per_waiting_min")
        @Expose
        public Integer farePerWaitingMin;
        @SerializedName("fare_threshold_waiting_time")
        @Expose
        public Integer fareThresholdWaitingTime;
        @SerializedName("night_fare_applicable")
        @Expose
        public Integer nightFareApplicable;
        @SerializedName("type")
        @Expose
        public Integer type;
        @SerializedName("accept_subsidy_threshold_distance")
        @Expose
        public Integer acceptSubsidyThresholdDistance;
        @SerializedName("accept_subsidy_before_threshold")
        @Expose
        public Integer acceptSubsidyBeforeThreshold;
        @SerializedName("accept_subsidy_after_threshold")
        @Expose
        public Integer acceptSubsidyAfterThreshold;
        @SerializedName("ride_type")
        @Expose
        public Integer rideType;
        @SerializedName("display_base_fare")
        @Expose
        public String displayBaseFare;

    }
    //-----------------------------------product.clicklabs.jugnoo.driver.tutorial.Promotion.java-----------------------------------


    public class Promotion {


    }
    //-----------------------------------product.clicklabs.jugnoo.driver.tutorial.RequestResponse.java-----------------------------------


    public class RequestResponse {

        @SerializedName("is_pooled")
        @Expose
        public Integer isPooled;
        @SerializedName("penalise_driver_timeout")
        @Expose
        public Integer penaliseDriverTimeout;
        @SerializedName("is_delivery")
        @Expose
        public Integer isDelivery;
        @SerializedName("ring_type")
        @Expose
        public Integer ringType;
        @SerializedName("fare_factor")
        @Expose
        public Integer fareFactor;
        @SerializedName("flag")
        @Expose
        public Integer flag;
        @SerializedName("current_latitude")
        @Expose
        public String currentLatitude;
        @SerializedName("end_time")
        @Expose
        public String endTime;
        @SerializedName("address")
        @Expose
        public String address;
        @SerializedName("wake_up_lock_enabled")
        @Expose
        public Integer wakeUpLockEnabled;
        @SerializedName("engagement_id")
        @Expose
        public Integer engagementId;
        @SerializedName("dry_distance")
        @Expose
        public Double dryDistance;
        @SerializedName("reference_id")
        @Expose
        public Integer referenceId;
        @SerializedName("business_id")
        @Expose
        public Integer businessId;
        @SerializedName("perfect_ride")
        @Expose
        public Integer perfectRide;
        @SerializedName("start_time")
        @Expose
        public String startTime;
        @SerializedName("ride_type")
        @Expose
        public Integer rideType;
        @SerializedName("longitude")
        @Expose
        public Double longitude;
        @SerializedName("user_id")
        @Expose
        public Integer userId;
        @SerializedName("latitude")
        @Expose
        public Double latitude;
        @SerializedName("current_longitude")
        @Expose
        public String currentLongitude;

        public Integer getIsPooled() {
            return isPooled;
        }

        public void setIsPooled(Integer isPooled) {
            this.isPooled = isPooled;
        }

        public Integer getPenaliseDriverTimeout() {
            return penaliseDriverTimeout;
        }

        public void setPenaliseDriverTimeout(Integer penaliseDriverTimeout) {
            this.penaliseDriverTimeout = penaliseDriverTimeout;
        }

        public Integer getIsDelivery() {
            return isDelivery;
        }

        public void setIsDelivery(Integer isDelivery) {
            this.isDelivery = isDelivery;
        }

        public Integer getRingType() {
            return ringType;
        }

        public void setRingType(Integer ringType) {
            this.ringType = ringType;
        }

        public Integer getFareFactor() {
            return fareFactor;
        }

        public void setFareFactor(Integer fareFactor) {
            this.fareFactor = fareFactor;
        }

        public Integer getFlag() {
            return flag;
        }

        public void setFlag(Integer flag) {
            this.flag = flag;
        }

        public String getCurrentLatitude() {
            return currentLatitude;
        }

        public void setCurrentLatitude(String currentLatitude) {
            this.currentLatitude = currentLatitude;
        }

        public String getEndTime() {
            return endTime;
        }

        public void setEndTime(String endTime) {
            this.endTime = endTime;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Integer getWakeUpLockEnabled() {
            return wakeUpLockEnabled;
        }

        public void setWakeUpLockEnabled(Integer wakeUpLockEnabled) {
            this.wakeUpLockEnabled = wakeUpLockEnabled;
        }

        public Integer getEngagementId() {
            return engagementId;
        }

        public void setEngagementId(Integer engagementId) {
            this.engagementId = engagementId;
        }

        public Double getDryDistance() {
            return dryDistance;
        }

        public void setDryDistance(Double dryDistance) {
            this.dryDistance = dryDistance;
        }

        public Integer getReferenceId() {
            return referenceId;
        }

        public void setReferenceId(Integer referenceId) {
            this.referenceId = referenceId;
        }

        public Integer getBusinessId() {
            return businessId;
        }

        public void setBusinessId(Integer businessId) {
            this.businessId = businessId;
        }

        public Integer getPerfectRide() {
            return perfectRide;
        }

        public void setPerfectRide(Integer perfectRide) {
            this.perfectRide = perfectRide;
        }

        public String getStartTime() {
            return startTime;
        }

        public void setStartTime(String startTime) {
            this.startTime = startTime;
        }

        public Integer getRideType() {
            return rideType;
        }

        public void setRideType(Integer rideType) {
            this.rideType = rideType;
        }

        public Double getLongitude() {
            return longitude;
        }

        public void setLongitude(Double longitude) {
            this.longitude = longitude;
        }

        public Integer getUserId() {
            return userId;
        }

        public void setUserId(Integer userId) {
            this.userId = userId;
        }

        public Double getLatitude() {
            return latitude;
        }

        public void setLatitude(Double latitude) {
            this.latitude = latitude;
        }

        public String getCurrentLongitude() {
            return currentLongitude;
        }

        public void setCurrentLongitude(String currentLongitude) {
            this.currentLongitude = currentLongitude;
        }
    }
    //-----------------------------------product.clicklabs.jugnoo.driver.tutorial.Responses.java-----------------------------------


    public class Responses {

        @SerializedName("request_response")
        @Expose
        public RequestResponse requestResponse;
        @SerializedName("user_data")
        @Expose
        public UserData userData;
        @SerializedName("accept_response")
        @Expose
        public AcceptResponse acceptResponse;

    }

    //-----------------------------------product.clicklabs.jugnoo.driver.tutorial.UserData.java-----------------------------------

    public class UserData {

        @SerializedName("user_id")
        @Expose
        public Integer userId;
        @SerializedName("user_name")
        @Expose
        public String userName;
        @SerializedName("phone_no")
        @Expose
        public String phoneNo;
        @SerializedName("user_image")
        @Expose
        public String userImage;
        @SerializedName("user_rating")
        @Expose
        public Double userRating;
        @SerializedName("jugnoo_balance")
        @Expose
        public Integer jugnooBalance;
        @SerializedName("address")
        @Expose
        public String address;

    }

}
