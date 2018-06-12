package product.clicklabs.jugnoo.driver.ui.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Parminder Saini on 28/04/18.
 */
data class DriverLanguageResponse(
@Expose @SerializedName("locale_preference_enabled") val languagePrefStatus:Int,
@Expose @SerializedName("locales") val languageList:ArrayList<String>
):FeedCommonResponseKotlin()