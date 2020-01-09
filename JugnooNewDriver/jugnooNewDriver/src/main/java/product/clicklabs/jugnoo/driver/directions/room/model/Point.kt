package product.clicklabs.jugnoo.driver.directions.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tb_point")
data class Point(
        @ColumnInfo(name = "pathId")
        val pathId:Long,
        @ColumnInfo(name = "lat")
        val lat:Double,
        @ColumnInfo(name = "lng")
        val lng:Double

){
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}
@Entity(tableName = "tb_path")
data class Path(
        @ColumnInfo(name = "engagementId")
        val engagementId:Long,
        @ColumnInfo(name = "pLat")
        val plat:Double,
        @ColumnInfo(name = "pLng")
        val pLng:Double,
        @ColumnInfo(name = "dLat")
        val dLat:Double,
        @ColumnInfo(name = "dLng")
        val dLng:Double,
        @ColumnInfo(name = "distance")
        val distance:Double,
        @ColumnInfo(name = "time")
        val time:Double,
        @ColumnInfo(name = "timeStamp")
        val timeStamp:Long


){
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
}
