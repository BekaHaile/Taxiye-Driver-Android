package product.clicklabs.jugnoo.driver.datastructure

import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse

class PagerInfo(
        @SerializedName("title")
        var title:String?,
        @SerializedName("description")
        var message:String?,
        @SerializedName("image_url")
        var image:String?)

class TutorialDataResponse(
        @SerializedName("data")
        var tutorialsData:MutableList<PagerInfo>

) :FeedCommonResponse()