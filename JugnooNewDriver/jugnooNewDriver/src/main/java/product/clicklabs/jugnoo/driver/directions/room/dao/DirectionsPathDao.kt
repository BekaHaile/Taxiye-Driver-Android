package product.clicklabs.jugnoo.driver.directions.room.dao

import android.arch.persistence.room.*
import product.clicklabs.jugnoo.driver.directions.room.model.Path
import product.clicklabs.jugnoo.driver.directions.room.model.Point

@Dao
interface DirectionsPathDao {

    //Segment queries------
    @Query("SELECT * FROM tb_point WHERE pathId = :pathId")
    fun getPathPoints(pathId:Long) : List<Point>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPathPoints(points:List<Point>)

    @Query("DELETE FROM tb_point WHERE pathId = :pathId")
    fun deletePathPoints(pathId:Long)


    //Path queries------
    @Query("SELECT * FROM tb_path WHERE engagementId = :engagementId AND pLat = :pLat AND pLng = :pLng AND dLat = :dLat AND dLng = :dLng AND timeStamp >= :timeStamp")
    fun getPath(engagementId:Long, pLat:Double, pLng:Double, dLat:Double, dLng:Double, timeStamp:Long) : List<Path>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPath(path:Path)

    @Query("DELETE FROM tb_path WHERE engagementId = :engagementId")
    fun deletePath(engagementId:Long)

    @Query("DELETE FROM tb_path WHERE timeStamp < :timeStamp")
    fun deletePathsOld(timeStamp:Long)

    @Query("SELECT * FROM tb_path WHERE engagementId = :engagementId")
    fun getPath(engagementId:Long) : List<Path>?

    @Query("SELECT * FROM tb_path WHERE timeStamp < :timeStamp")
    fun getOldPaths(timeStamp:Long) : List<Path>?

    @Transaction
    fun deleteAllPath(engagementId:Long){
        val paths = getPath(engagementId)
        if(paths != null){
            for(path in paths){
                deletePathPoints(path.timeStamp)
            }
        }
        deletePath(engagementId)
    }

    @Transaction
    fun deleteOldPaths(timeStamp:Long){
        val paths = getOldPaths(timeStamp)
        if(paths != null){
            for(path in paths){
                deletePathPoints(path.timeStamp)
            }
        }
        deletePathsOld(timeStamp)
    }

}