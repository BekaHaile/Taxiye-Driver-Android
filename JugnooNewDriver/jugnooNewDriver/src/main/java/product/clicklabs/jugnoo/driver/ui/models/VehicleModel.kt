package product.clicklabs.jugnoo.driver.ui.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Parminder Saini on 10/07/18.
 */

data class VehicleDetails(
        @Expose @SerializedName("data") val models: Map<String,List<VehicleModelDetails>>
):FeedCommonResponseKotlin()



data class VehicleModelDetails(
        @Expose @SerializedName("brand") val make:String,
        @Expose @SerializedName("name") val modelName:String,
        @Expose @SerializedName("model_id") val id:Int
)


/*



data class VehicleModelCustomisationDetails(
        @Expose @SerializedName("color") val color:String,
        @Expose @SerializedName("id") val id:Int
):FeedCommonResponseKotlin()*/
