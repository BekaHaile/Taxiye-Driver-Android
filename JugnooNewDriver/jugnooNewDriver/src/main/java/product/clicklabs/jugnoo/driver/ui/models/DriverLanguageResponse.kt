package product.clicklabs.jugnoo.driver.ui.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by Parminder Saini on 28/04/18.
 */
data class DriverLanguageResponse(
@Expose @SerializedName("locale_preference_enabled") val languagePrefStatus:Int,
@Expose @SerializedName("default_lang") val defaultLang:String?,
@Expose @SerializedName("locale_set") val languageList:ArrayList<LocaleModel>,
@Expose @SerializedName("default_country_code") val defaultCountryCode:String?,
@Expose @SerializedName("default_sub_country_code") val defaultSubCountryCode:String?,
@Expose @SerializedName("default_country_iso") val defaultCountryIso:String?,
@Expose @SerializedName("show_terms") val showTerms:Int?,
@Expose @SerializedName("email_input_at_signup") val emailInputAtSignup:Int?,
@Expose @SerializedName("driver_email_optional") val driverEmailOptional:Int?,
@Expose @SerializedName("driver_dob_input") val driverDobInput:Int?,
@Expose @SerializedName("driver_gender_filter") val driverGenderFilter:Int?
):FeedCommonResponse()

class LocaleModel(@Expose @SerializedName("locale") val locale:String?,
                       @Expose @SerializedName("name") val name:String?){
    override fun equals(other: Any?): Boolean {
        return other is LocaleModel && other.locale.equals(locale, ignoreCase = false)
    }

    override fun toString(): String {
        return name!!;
    }
}