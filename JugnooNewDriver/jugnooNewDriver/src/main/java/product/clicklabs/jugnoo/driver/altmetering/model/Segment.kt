package product.clicklabs.jugnoo.driver.altmetering.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "segment")
class Segment(
        @Ignore var source:LatLng?,
        @Ignore var destination:LatLng?,
        val position: Int

){
    val sLat:Double = source!!.latitude
    val sLng:Double = source!!.longitude
    val dLat:Double = destination!!.latitude
    val dLng:Double = destination!!.longitude
    var lastProjectedLat:Double = 0.0
    var lastProjectedLng:Double = 0.0

    fun getSourceLatLng():LatLng?{
        if(source == null)
            source = LatLng(sLat, sLng)
        return source
    }
    fun getDestinationLatLng():LatLng?{
        if(destination == null)
            destination = LatLng(dLat, dLng)
        return destination
    }

}

@Entity(tableName = "scanning_pointer")
class ScanningPointer(val position: Int)

@Entity(tableName = "last_location_timestamp")
class LastLocationTimestamp(val timestamp:String)