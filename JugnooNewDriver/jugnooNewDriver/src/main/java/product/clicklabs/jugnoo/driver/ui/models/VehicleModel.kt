package product.clicklabs.jugnoo.driver.ui.models

import android.content.Context
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Parminder Saini on 10/07/18.
 */

data class VehicleDetailsResponse(
        @Expose @SerializedName("data") val models: Map<String,List<VehicleModelDetails>>
):FeedCommonResponseKotlin()



data class VehicleModelDetails(
        @Expose @SerializedName("brand") val make:String,
        @Expose @SerializedName("name") val modelName:String,
        @Expose @SerializedName("model_id") val id:Int,
        @Expose @SerializedName("no_of_seat_belts") val noOfSeatBelts:Int,
        @Expose @SerializedName("no_of_doors") val noOfDoors:Int) :SearchDataModel() {
    override fun getLabel(): String {
       return modelName;
    }

    override fun getImage(context: Context?): Int {
        return -1
    }

    override fun showImage(): Boolean {
        return false;
    }
}

class VehicleMakeInfo(val makeName:String): SearchDataModel() {
    override fun getLabel(): String {
        return makeName;
    }

    override fun getImage(context: Context?): Int {
        return -1
    }

    override fun showImage(): Boolean {
        return false
    }


}

data class VehicleModelCustomisationsResponse(
        @Expose @SerializedName("data") val customisationList: CustomisationData
):FeedCommonResponseKotlin()

class CustomisationData(
        @Expose @SerializedName("colors") val colorCustomisationList: List<VehicleModelCustomisationDetails>,
        @Expose @SerializedName("doors") val doorCustomisationList: List<VehicleModelCustomisationDetails>,
        @Expose @SerializedName("seat_belts") val seatBeltsCustomisationList: List<VehicleModelCustomisationDetails>
)





data class VehicleModelCustomisationDetails(
        @Expose @SerializedName("value") val value:String,
        @Expose @SerializedName("id") val id:Int):SearchDataModel() {
    override fun getImage(context: Context?): Int {
        return -1

    }

    override fun showImage(): Boolean {
        return false
    }

    override fun getLabel(): String {
        return value;
    }
}
