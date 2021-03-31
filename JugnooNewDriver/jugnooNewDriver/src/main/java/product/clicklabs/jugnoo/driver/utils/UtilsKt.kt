package product.clicklabs.jugnoo.driver.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import io.michaelrocks.libphonenumber.android.NumberParseException
import io.michaelrocks.libphonenumber.android.PhoneNumberUtil
import product.clicklabs.jugnoo.driver.R

object UtilsKt {


    @JvmStatic
    fun whatsappIntent(activity: Activity, content:String, subject:String){
        try {
            if(Utils.appInstalledOrNot(activity, "com.whatsapp")) {
                try {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "text/plain"
                    val activities = activity.packageManager.queryIntentActivities(intent, 0)
                    for (info in activities) {
                        if (info.activityInfo.packageName.contains("com.whatsapp")) {
                            intent.setClassName(info.activityInfo.packageName, info.activityInfo.name)
                            intent.putExtra(Intent.EXTRA_TEXT, content)
                            activity.startActivity(intent)
                            break
                        }
                    }
                } catch (e: Exception) {
                    Utils.showToast(activity, activity.getString(R.string.whatsapp_not_installed))
                }
            } else {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, content)
                activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.send_via)))
            }
        } catch (e: Exception) {}

    }

    fun defaultShareIntent(activity: Activity, content:String, subject:String){
        try {
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT, subject)
            intent.putExtra(Intent.EXTRA_TEXT, content)
            activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.send_via)))
        } catch (e: Exception) {
        }
    }

    private var phoneNumberUtil: PhoneNumberUtil? = null

    @JvmStatic
    fun splitCountryCodeAndPhoneNumber(context: Context, number: String):CountryCodePhoneNo {
        if(phoneNumberUtil == null) {
            phoneNumberUtil = PhoneNumberUtil.createInstance(context)
        }


        var number1 = number
        number1 = number1.replace(" ", "")
        number1 = if (number1.startsWith("0")) number1.replaceFirst("0", "") else number1

        if (number1.startsWith("+")) {

            val phoneNumber = try {
                phoneNumberUtil?.parse(number1, null)
            } catch (e: NumberParseException) {
                Log.e("PhoneNumberUtil", "error during parsing a number $number1")
                null
            }
            if (phoneNumber == null) {
                number1 = Utils.retrievePhoneNumberTenChars("", number1)
                return CountryCodePhoneNo("", "", number1)
            } else {
                val countryIso = phoneNumberUtil?.getRegionCodeForCountryCode(phoneNumber.countryCode) ?: ""
                val countryCode = "+".plus(phoneNumber.countryCode)
                number1 = Utils.retrievePhoneNumberTenChars(countryCode, number1)
                return CountryCodePhoneNo(countryCode, countryIso, number1)
            }
        } else {
            number1 = Utils.retrievePhoneNumberTenChars("", number1)
            return CountryCodePhoneNo("", "", number1)
        }
    }


    private fun getCountryCodeFromNumber(context: Context, number: String): String? {
        if(phoneNumberUtil == null) {
            phoneNumberUtil = PhoneNumberUtil.createInstance(context)
        }
        val validatedNumber = if (number.startsWith("+")) number else "+$number"

        val phoneNumber = try {
            phoneNumberUtil?.parse(validatedNumber, null)
        } catch (e: NumberParseException) {
            Log.e("PhoneNumberUtil", "error during parsing a number")
            null
        }
        if(phoneNumber == null) return null

        return phoneNumber.countryCode.toString()
//        return phoneNumberUtil.getRegionCodeForCountryCode(phoneNumber.countryCode)
    }

}

class CountryCodePhoneNo(val countryCode:String, val countryIso:String, val phoneNo:String)