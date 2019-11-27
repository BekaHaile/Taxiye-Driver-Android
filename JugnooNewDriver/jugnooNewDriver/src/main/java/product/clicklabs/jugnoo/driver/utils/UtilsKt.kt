package product.clicklabs.jugnoo.driver.utils

import android.app.Activity
import android.content.Intent
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

}