package product.clicklabs.jugnoo.driver.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin

class RentalVehicle(
        @SerializedName("city_id") @Expose var cityId : Int,
        @SerializedName("vehicle_type") @Expose var vehicleType : Int,
        @SerializedName("ride_type") @Expose var rideType : Int,
        @SerializedName("fares") @Expose var fares: List<Packages>
) : FeedCommonResponseKotlin()

class Packages(
        @SerializedName("accept_subsidy_threshold_distance") @Expose var acceptThresholdDistance : Double,
        @SerializedName("accept_subsidy_after_threshold") @Expose var acceptAfterThreshold : Double,
        @SerializedName("fare_fixed") @Expose var fareFixed : Double,
        @SerializedName("fare_per_km") @Expose var farePerKm : Double,
        @SerializedName("fare_per_min") @Expose var farePerMin : Double,
        @SerializedName("region_id") @Expose var regionId : Int,
        @SerializedName("fare_per_km_threshold_distance") @Expose var farePerKmThresholdDistance : Double,
        @SerializedName("fare_per_km_after_threshold") @Expose var farePerKmAfterThreshold : Double,
        @SerializedName("fare_minimum") @Expose var fareMinimum : Double,
        @SerializedName("fare_per_baggage") @Expose var farePerBaggage : Double,
        @SerializedName("vehicle_type") @Expose var vehicleType : Int,
        @SerializedName("ride_type") @Expose var rideType : Int,
        @SerializedName("city") @Expose var city : Int
)