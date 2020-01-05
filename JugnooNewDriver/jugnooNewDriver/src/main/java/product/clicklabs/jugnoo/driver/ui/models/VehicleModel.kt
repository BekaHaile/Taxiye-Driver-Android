package product.clicklabs.jugnoo.driver.ui.models

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Parminder Saini on 10/07/18.
 */

data class VehicleDetailsResponse(
        @Expose @SerializedName("data") val models: Map<String,List<VehicleModelDetails>>
):FeedCommonResponseKotlin()



data class VehicleModelDetails(
        @Expose @SerializedName("brand") val make:String?,
        @Expose @SerializedName("name") val modelName:String?,
        @Expose @SerializedName("model_id") val id:Int) :Parcelable,SearchDataModel() {
    override fun isSelected(): Boolean {
        return false
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun getLabel(): String? {
       return modelName;
    }

    override fun getImage(context: Context?): Int {
        return -1
    }

    override fun showImage(): Boolean {
        return false;
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(make)
        parcel.writeString(modelName)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VehicleModelDetails> {
        override fun createFromParcel(parcel: Parcel): VehicleModelDetails {
            return VehicleModelDetails(parcel)
        }

        override fun newArray(size: Int): Array<VehicleModelDetails?> {
            return arrayOfNulls(size)
        }
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

    override fun isSelected(): Boolean {
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





data class  VehicleModelCustomisationDetails (

        @Expose @SerializedName("value") val value:String?,
        @Expose @SerializedName("id") val id:Int):Parcelable,SearchDataModel() {
    override fun isSelected(): Boolean {
        return false
    }

    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readInt()) {
    }

    override fun getImage(context: Context?): Int {
        return -1

    }

    override fun showImage(): Boolean {
        return false
    }

    override fun getLabel(): String? {
        return value;
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(value)
        parcel.writeInt(id)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VehicleModelCustomisationDetails> {
        override fun createFromParcel(parcel: Parcel): VehicleModelCustomisationDetails {
            return VehicleModelCustomisationDetails(parcel)
        }

        override fun newArray(size: Int): Array<VehicleModelCustomisationDetails?> {
            return arrayOfNulls(size)
        }
    }
}
