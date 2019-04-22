package product.clicklabs.jugnoo.driver.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin

class RentalAndOutstation(
        @SerializedName("data") @Expose var RentalData: List<RentalVehicle>
) : FeedCommonResponseKotlin()

class RentalVehicle(
        @SerializedName("package_name") @Expose var packageName : String,
        @SerializedName("additional_charge") @Expose var additionalCharge : Double,
        @SerializedName("package_info") @Expose var packageInfo: List<Packages>
)
class Packages(
        @SerializedName("fare_threshold_time") @Expose var fareThresholdTime : Double,
        @SerializedName("fare_threshold_distance") @Expose var fareThresholdDistance : Double,
        @SerializedName("fare_fixed") @Expose var fareFixed : Double
)