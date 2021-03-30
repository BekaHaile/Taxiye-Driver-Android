package product.clicklabs.jugnoo.driver.datastructure

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse

class EndStopResponse(
        @SerializedName("data")
        @Expose
        val data: EndStopData?

) : FeedCommonResponse()

class EndStopData(
        @SerializedName("chosen_address")
        @Expose
        val chosenAddress: String?,
        @SerializedName("chosen_drop_latitude")
        @Expose
        val chosenDropLatitude: Double?,
        @SerializedName("chosen_drop_longitude")
        @Expose
        val chosenDropLongitude: Double?,
        @SerializedName("stop_id")
        @Expose
        val stopId: Int?
)