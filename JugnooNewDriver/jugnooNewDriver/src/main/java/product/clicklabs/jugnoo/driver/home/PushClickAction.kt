package product.clicklabs.jugnoo.driver.home

import android.app.Activity
import android.content.Intent
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.Data
import product.clicklabs.jugnoo.driver.chat.ChatActivity
import product.clicklabs.jugnoo.driver.utils.Prefs

object PushClickAction {

    fun performAction(activity:Activity){

        if(Prefs.with(activity).getBoolean(Constants.KEY_OPEN_CHAT, false) && Data.getCurrentCustomerInfo() != null){
            val chatIntent  =Intent(activity, ChatActivity::class.java)
            chatIntent.putExtra("engagement_id", Data.getCurrentEngagementId())
            chatIntent.putExtra("user_image", Data.getCurrentCustomerInfo().image)
            activity.startActivity(chatIntent)
            Prefs.with(activity).save(Constants.KEY_OPEN_CHAT, false)
        }
    }


}