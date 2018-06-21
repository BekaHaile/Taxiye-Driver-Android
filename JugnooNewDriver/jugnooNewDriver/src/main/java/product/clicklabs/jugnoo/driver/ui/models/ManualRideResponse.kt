package product.clicklabs.jugnoo.driver.ui.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Parminder Saini on 13/06/18.
 */
data class ManualRideResponse(
        @Expose @SerializedName("locale_preference_enabled") val languagePrefStatus:Int,
        @Expose @SerializedName("locales") val languageList:ArrayList<String>
):FeedCommonResponseKotlin()