package product.clicklabs.jugnoo.driver.ui.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.retrofit.CurrencyModel

/**
 * Created by Parminder Saini on 26/03/17.
 */

open class FeedCommonResponseKotlin {
    @Expose
    @SerializedName("flag")
    var flag: Int = 0
    @Expose
    @SerializedName("message")
    val message: String = ""
    @Expose
    @SerializedName("error")
    val error: String = ""
    @Expose
    @SerializedName(Constants.DRIVER_VEHICLE_MAPPING_ID)
    val driverVehicleMappinId: Int = -1

    fun serverMessage() = if (message == null || message.isBlank()) error ?: "" else message
}