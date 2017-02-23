package product.clicklabs.jugnoo.driver.tutorial;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by gurmail on 2/7/17.
 */
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
    public Double opDropLatitude;
    @SerializedName("op_drop_longitude")
    @Expose
    public Double opDropLongitude;
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
    public TourResponseModel.FareDetails fareDetails;
    @SerializedName("coupon")
    @Expose
    public TourResponseModel.Coupon coupon;
    @SerializedName("promotion")
    @Expose
    public TourResponseModel.Promotion promotion;
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
    @SerializedName("user_data")
    @Expose
    public TourResponseModel.UserData userData;

}
