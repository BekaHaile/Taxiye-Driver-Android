package product.clicklabs.jugnoo.driver.altmetering.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "segment")
data class Segment(
        @ColumnInfo(name = "sLat")
        val slat:Double,
        @ColumnInfo(name = "sLng")
        val sLng:Double,
        @ColumnInfo(name = "dLat")
        val dLat:Double,
        @ColumnInfo(name = "dLng")
        val dLng:Double,
        @ColumnInfo(name = "position")
        val position: Int

){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0

    @ColumnInfo(name = "lastProjectedLat")
    var lastProjectedLat:Double = 0.0
    @ColumnInfo(name = "lastProjectedLng")
    var lastProjectedLng:Double = 0.0
}

@Entity(tableName = "scanning_pointer")
data class ScanningPointer(@ColumnInfo(name = "position") val position: Int){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}

@Entity(tableName = "last_location_timestamp")
data class LastLocationTimestamp(@ColumnInfo(name = "timestamp") val timestamp:String){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}

@Entity(tableName = "waypoint")
class Waypoint(
        @ColumnInfo(name = "lat")
        val lat:Double,
        @ColumnInfo(name = "lng")
        val lng:Double
){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}