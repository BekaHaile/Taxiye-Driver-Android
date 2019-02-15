package product.clicklabs.jugnoo.driver.altmetering.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "waypoint")
class Waypoint(
        @Ignore val latLng:LatLng?
){
    val lat:Double = latLng!!.latitude
    val lng:Double = latLng!!.longitude
}