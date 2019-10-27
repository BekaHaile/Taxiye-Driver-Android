package product.clicklabs.jugnoo.driver.directions

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import product.clicklabs.jugnoo.driver.Constants
import product.clicklabs.jugnoo.driver.MyApplication
import product.clicklabs.jugnoo.driver.room.database.CommonRoomDatabase
import product.clicklabs.jugnoo.driver.room.model.AcceptLatLng

object AcceptLatLngCoroutine {

    private var db: CommonRoomDatabase? = null
        get() {
            if (field == null) {
                field = CommonRoomDatabase.getInstance(MyApplication.getInstance())
            }
            return field
        }


    fun getAcceptLatLng(engagementId:Int, acceptLatLng: AcceptLatLng, callback: Callback?){
        GlobalScope.launch(Dispatchers.IO){
            val list = db!!.getDao().getAcceptLatLng(engagementId.toLong())
            if (list != null && list.isNotEmpty()) {
                launch(Dispatchers.Main) { callback?.onSuccess(list[0]) }
            } else {
                db!!.getDao().insertAcceptLatLng(acceptLatLng)
                launch(Dispatchers.Main) { callback?.onSuccess(acceptLatLng) }
            }

        }
    }

    fun insertAcceptLatLng(acceptLatLng: AcceptLatLng, callback: Callback?){
        GlobalScope.launch(Dispatchers.IO){
            db!!.getDao().insertAcceptLatLng(acceptLatLng)
            launch(Dispatchers.Main) { callback?.onSuccess(acceptLatLng) }
        }
    }

    fun deleteAcceptLatLng(engagementId:Int){
        GlobalScope.launch(Dispatchers.IO){
            db!!.getDao().deleteAcceptLatLng(engagementId.toLong())
            db!!.getDao().deleteAcceptLatLngOld(System.currentTimeMillis() - Constants.DAY_MILLIS)
        }
    }


    interface Callback{
        fun onSuccess(acceptLatLng: AcceptLatLng)
    }
}