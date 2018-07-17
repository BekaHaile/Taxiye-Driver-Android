package product.clicklabs.jugnoo.driver.ui.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Parminder Saini on 28/04/18.
 */
data class DriverLanguageResponse(
@Expose @SerializedName("locale_preference_enabled") val languagePrefStatus:Int,
@Expose @SerializedName("default_lang") val defaultLang:String?,
@Expose @SerializedName("locale_set") val languageList:ArrayList<LocaleModel>
):FeedCommonResponseKotlin()

class LocaleModel(@Expose @SerializedName("locale") val locale:String?,
                       @Expose @SerializedName("name") val name:String?){
    override fun equals(other: Any?): Boolean {
        return other is LocaleModel && other.locale.equals(locale, ignoreCase = false)
    }

    override fun toString(): String {
        return name!!;
    }
}