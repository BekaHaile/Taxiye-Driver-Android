package product.clicklabs.jugnoo.driver.altmetering.db

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import product.clicklabs.jugnoo.driver.altmetering.model.*

@Dao
interface MeteringDao{

    //Segment queries------
    @Query("SELECT * FROM segment WHERE engagementId = :engagementId AND position >= :position")
    fun getAllSegments(engagementId:Int, position:Int) : List<Segment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllSegments(segments:List<Segment>)

    @Query("UPDATE segment SET lastProjectedLat = :latitude, lastProjectedLng = :longitude WHERE engagementId = :engagementId AND position = :position")
    fun updateSegment(engagementId:Int, position:Int, latitude:Double, longitude:Double)

    @Query("DELETE FROM segment WHERE engagementId = :engagementId")
    fun deleteAllSegments(engagementId:Int)


    //Scanning pointer queries-------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScanningPointer(scanningPointer: ScanningPointer)

    @Query("DELETE FROM scanning_pointer WHERE engagementId = :engagementId")
    fun deleteScanningPointer(engagementId:Int)

    @Query("SELECT * FROM scanning_pointer WHERE engagementId = :engagementId")
    fun getScanningPointer(engagementId:Int) : List<ScanningPointer>



    //LastLocationTimestamp queries---------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLastLocationTimestamp(lastLocationTimestamp: LastLocationTimestamp)

    @Query("DELETE FROM last_location_timestamp WHERE engagementId = :engagementId")
    fun deleteLastLocationTimestamp(engagementId:Int)

    @Query("SELECT * FROM last_location_timestamp WHERE engagementId = :engagementId")
    fun getLastLocationTimeStamp(engagementId:Int) : List<LastLocationTimestamp>



    //Waypoints queries---------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWaypoint(waypoint: Waypoint)

    @Query("SELECT * FROM waypoint WHERE engagementId = :engagementId")
    fun getAllWaypoints(engagementId:Int) : List<Waypoint>

    @Query("DELETE FROM waypoint WHERE engagementId = :engagementId")
    fun deleteAllWaypoints(engagementId:Int)



    //Log queries---------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLogItem(logItem: LogItem)

    @Query("SELECT * FROM log_item WHERE engagementId = :engagementId")
    fun getLogItem(engagementId:Int) : List<LogItem>

    @Query("DELETE FROM log_item WHERE engagementId = :engagementId")
    fun deleteLogItem(engagementId:Int)
}
