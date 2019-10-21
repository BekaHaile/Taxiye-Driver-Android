package product.clicklabs.jugnoo.driver.directions.room.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "tb_point")
data class Point(
        @ColumnInfo(name = "pathId")
        val pathId:Int,
        @ColumnInfo(name = "lat")
        val lat:Double,
        @ColumnInfo(name = "lng")
        val lng:Double

){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}
@Entity(tableName = "tb_path")
data class Path(
        @ColumnInfo(name = "engagementId")
        val engagementId:Int,
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
        val time:Double

){
    @PrimaryKey(autoGenerate = true)
    var id:Int = 0
}
