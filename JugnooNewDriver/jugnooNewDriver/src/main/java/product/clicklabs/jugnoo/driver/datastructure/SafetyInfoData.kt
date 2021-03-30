package product.clicklabs.jugnoo.driver.datastructure

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class SafetyInfoData (
        @SerializedName("safety_points")
        @Expose
        var safetyPoints:ArrayList<SafetyPoint>?,
        @SerializedName("title")
        @Expose
        var title:String?,
        @SerializedName("banner_image")
        @Expose
        var bannerImage:String?
){
}

class SafetyPoint(
        @SerializedName("mandatory")
        @Expose
        var mandatory:Int?,
        @SerializedName("name")
        @Expose
        var name:String?
){

    var isSelected:Boolean = false

    fun isMandatory():Boolean{
        return mandatory == null || mandatory == 1
    }
}