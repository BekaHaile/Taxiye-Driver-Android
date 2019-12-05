package product.clicklabs.jugnoo.driver

import android.content.Context
import android.location.Location
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


object DLDKotlin {

    fun hitDLD(context: Context, location:Location){
        GlobalScope.launch(Dispatchers.IO){
            Database2.getInstance(context).updateDriverCurrentLocation(context, location)
            DriverLocationDispatcher().sendLocationToServer(context)
        }
    }

}