package product.clicklabs.jugnoo.driver.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin

class RentalVehicle(
        @SerializedName("city_id") @Expose var cityId : Int,
        @SerializedName("vehicle_type") @Expose var vehicleType : Int,
        @SerializedName("ride_type") @Expose var rideType : Int,
        @SerializedName("region_name") @Expose var regionName : String,
        @SerializedName("fares") @Expose var fares: List<Packages>
) : FeedCommonResponseKotlin()

class Packages(
        @SerializedName("id") @Expose var id : Int = 0,
        @SerializedName("fare_threshold_distance") @Expose var fareThresholdDistance : Double = 0.0,
        @SerializedName("fare_fixed") @Expose var fareFixed : Double = 0.0,
        @SerializedName("fare_per_km") @Expose var farePerKm : Double = 0.0,
        @SerializedName("fare_per_min") @Expose var farePerMin : Double = 0.0,
        @SerializedName("region_id") @Expose var regionId : Int = 0,
        @SerializedName("fare_per_km_threshold_distance") @Expose var farePerKmThresholdDistance : Double = 0.0,
        @SerializedName("fare_per_km_after_threshold") @Expose var farePerKmAfterThreshold : Double = 0.0,
        @SerializedName("fare_per_km_before_threshold") @Expose var farePerKmBeforeThreshold : Double = 0.0,
        @SerializedName("fare_threshold_time") @Expose var fareThresholdTime : Double = 0.0,
        @SerializedName("fare_per_waiting_min") @Expose var farePerWaitingMin : Double = 0.0,
        @SerializedName("fare_threshold_waiting_time") @Expose var fareThresholdWaitingTime : Double = 0.0,
        @SerializedName("fare_minimum") @Expose var fareMinimum : Double = 0.0,
        @SerializedName("fare_per_baggage") @Expose var farePerBaggage : Double = 0.0,
        @SerializedName("accept_subsidy_after_threshold") @Expose var acceptSubsidyAfterThreshold : Double = 0.0,
        @SerializedName("accept_subsidy_threshold_distance") @Expose var acceptSubsidyThresholdDistance : Double = 0.0,
        @SerializedName("display_base_fare") @Expose var displayBaseFare : Double = 0.0,
        @SerializedName("vehicle_type") @Expose var vehicleType : Int = 0,
        @SerializedName("ride_type") @Expose var rideType : Int = 0,
        @SerializedName("type") @Expose var type : Int = 0,
        @SerializedName("city") @Expose var city : Int = 0,
        @SerializedName("operatot_id") @Expose var operatorId : Int = 0,
        @SerializedName("business_id") @Expose var businessId : Int = 0
)