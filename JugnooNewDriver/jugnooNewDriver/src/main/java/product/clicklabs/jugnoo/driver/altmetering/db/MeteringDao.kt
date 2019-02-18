package product.clicklabs.jugnoo.driver.altmetering.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import product.clicklabs.jugnoo.driver.altmetering.model.LastLocationTimestamp
import product.clicklabs.jugnoo.driver.altmetering.model.ScanningPointer
import product.clicklabs.jugnoo.driver.altmetering.model.Segment
import product.clicklabs.jugnoo.driver.altmetering.model.Waypoint

@Dao
interface MeteringDao{

    //Segment queries------
    @Query("SELECT * FROM segment WHERE position >= :position")
    fun getAllSegments(position:Int) : List<Segment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSegments(segments:List<Segment>)

    @Query("UPDATE segment SET lastProjectedLat = :latitude, lastProjectedLng = :longitude WHERE position = :position")
    fun updateSegment(position:Int, latitude:Double, longitude:Double)

    @Query("DELETE FROM segment")
    fun deleteAllSegments()


    //Scanning pointer queries-------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScanningPointer(scanningPointer: ScanningPointer)

    @Query("DELETE FROM scanning_pointer")
    fun deleteScanningPointer()



    //LastLocationTimestamp queries---------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLastLocationTimestamp(lastLocationTimestamp: LastLocationTimestamp)

    @Query("DELETE FROM last_location_timestamp")
    fun deleteLastLocationTimestamp()



    //Waypoints queries---------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWaypoint(waypoint: Waypoint)

    @Query("SELECT * FROM waypoint")
    fun getAllWaypoints() : List<Waypoint>

    @Query("DELETE FROM waypoint")
    fun deleteAllWaypoints()
}
