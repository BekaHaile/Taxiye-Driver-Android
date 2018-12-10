package product.clicklabs.jugnoo.driver.retrofit.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin

class TollDataResponse(
        @SerializedName("data") @Expose var tollData: List<TollData>
) : FeedCommonResponseKotlin()

class TollData(
        @SerializedName("toll_visit_id") @Expose var tollVisitId : Int,
        @SerializedName("toll") @Expose var toll : Double,
        @SerializedName("toll_name") @Expose var tollName : String,
        var deleted:Boolean = false
)