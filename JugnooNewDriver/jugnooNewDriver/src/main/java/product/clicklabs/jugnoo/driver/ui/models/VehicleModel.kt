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
        @Expose @SerializedName("no_of_seats") val noOfSeatBelts:Int,
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
        @Expose @SerializedName("data") val customisationList: List<VehicleModelCustomisationDetails>
):FeedCommonResponseKotlin()

data class VehicleModelCustomisationDetails(
        @Expose @SerializedName("color") val color:String,
        @Expose @SerializedName("id") val id:Int):SearchDataModel() {
    override fun getImage(context: Context?): Int {
        return -1

    }

    override fun showImage(): Boolean {
        return false
    }

    override fun getLabel(): String {
        return color;
    }
}
