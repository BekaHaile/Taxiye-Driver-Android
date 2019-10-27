package product.clicklabs.jugnoo.driver.room.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tb_accept_latlng")
data class AcceptLatLng(
        @ColumnInfo(name = "engagementId")
        val engagementId:Long,
        @ColumnInfo(name = "lat")
        val lat:Double,
        @ColumnInfo(name = "lng")
        val lng:Double,
        @ColumnInfo(name = "timeStamp")
        val timeStamp:Long

){
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}