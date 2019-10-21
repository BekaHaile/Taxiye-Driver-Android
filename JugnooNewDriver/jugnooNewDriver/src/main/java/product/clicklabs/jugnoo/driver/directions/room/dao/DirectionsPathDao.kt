package product.clicklabs.jugnoo.driver.directions.room.dao

import android.arch.persistence.room.*
import product.clicklabs.jugnoo.driver.directions.room.model.Path
import product.clicklabs.jugnoo.driver.directions.room.model.Point

@Dao
interface DirectionsPathDao {

    //Segment queries------
    @Query("SELECT * FROM tb_point WHERE pathId = :pathId")
    suspend fun getPathPoints(pathId:Int) : List<Point>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPathPoints(points:List<Point>) : List<Long>

    @Query("DELETE FROM tb_point WHERE pathId = :pathId")
    fun deletePathPoints(pathId:Int)


    //Path queries------
    @Query("SELECT * FROM tb_path WHERE engagementId = :engagementId AND pLat = :pLat AND pLng = :pLng AND dLat = :dLat AND dLng = :dLng")
    suspend fun getPath(engagementId:Int, pLat:Double, pLng:Double, dLat:Double, dLng:Double) : List<Path>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPath(path:Path) : Long

    @Query("DELETE FROM tb_path WHERE engagementId = :engagementId")
    fun deletePath(engagementId:Int)

    @Transaction
    suspend fun deleteAllPath(engagementId:Int){
        deletePath(engagementId)
        deletePathPoints(engagementId)
    }

}