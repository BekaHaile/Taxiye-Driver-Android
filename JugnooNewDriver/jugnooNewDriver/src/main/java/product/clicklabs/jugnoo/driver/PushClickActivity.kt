package product.clicklabs.jugnoo.driver

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import org.json.JSONObject
import product.clicklabs.jugnoo.driver.datastructure.PushFlags
import product.clicklabs.jugnoo.driver.ui.DriverSplashActivity
import product.clicklabs.jugnoo.driver.utils.Prefs

class PushClickActivity : Activity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if(intent.hasExtra(Constants.KEY_PAYLOAD)) {
            val payload = intent.getStringExtra(Constants.KEY_PAYLOAD)
            try{
                val jObj = JSONObject(payload!!)
                val flag = jObj.getInt(Constants.KEY_FLAG)
                when(flag){
                    PushFlags.CHAT_MESSAGE.kOrdinal -> {
                        Prefs.with(this).save(Constants.KEY_OPEN_CHAT, true)
                    }
                }



            } catch (e:Exception){}
        }

        finish()
        startActivity(Intent(this, DriverSplashActivity::class.java))

    }

}