package product.clicklabs.jugnoo.driver.ui.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Parminder Saini on 26/04/18.
 */
data class RegisterScreenResponse(
        @SerializedName("phone_no") val phoneNo: String,
        @SerializedName("access_token") val accessToken: String,
        @SerializedName("user_email") val userEmail: String
):FeedCommonResponse()