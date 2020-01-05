package product.clicklabs.jugnoo.driver.retrofit.model

import com.google.gson.annotations.SerializedName
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponse
import product.clicklabs.jugnoo.driver.ui.models.FeedCommonResponseKotlin

class BranchUrlRequest(
        @SerializedName("data")
        var data: HashMap<String, String>,
        @SerializedName("branch_key")
        var branchKey: String,
        @SerializedName("branch_secret")
        var branchSecret: String
)

class BranchUrlResponse(
        @SerializedName("url")
        var url:String?
): FeedCommonResponse()