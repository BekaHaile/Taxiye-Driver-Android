package product.clicklabs.jugnoo.driver.altmetering.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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

    @Query("SELECT count(*) FROM segment")
    fun getAllSegments() : Int


    //Scanning pointer queries-------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertScanningPointer(scanningPointer: ScanningPointer)

    @Query("DELETE FROM scanning_pointer WHERE engagementId = :engagementId")
    fun deleteScanningPointer(engagementId:Int)

    @Query("SELECT * FROM scanning_pointer WHERE engagementId = :engagementId")
    fun getScanningPointer(engagementId:Int) : List<ScanningPointer>

    @Query("SELECT count(*) FROM scanning_pointer")
    fun getAllScanningPointers() : Int



    //LastLocationTimestamp queries---------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLastLocationTimestamp(lastLocationTimestamp: LastLocationTimestamp)

    @Query("DELETE FROM last_location_timestamp WHERE engagementId = :engagementId")
    fun deleteLastLocationTimestamp(engagementId:Int)

    @Query("SELECT * FROM last_location_timestamp WHERE engagementId = :engagementId")
    fun getLastLocationTimeStamp(engagementId:Int) : List<LastLocationTimestamp>

    @Query("SELECT count(*) FROM last_location_timestamp")
    fun getAllLocationTimeStamps() : Int



    //Waypoints queries---------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWaypoint(waypoint: Waypoint)

    @Query("SELECT * FROM waypoint WHERE engagementId = :engagementId")
    fun getAllWaypoints(engagementId:Int) : List<Waypoint>

    @Query("DELETE FROM waypoint WHERE engagementId = :engagementId")
    fun deleteAllWaypoints(engagementId:Int)

    @Query("SELECT count(*) FROM waypoint")
    fun getAllWaypoints() : Int



    //Log queries---------
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertLogItem(logItem: LogItem)

    @Query("SELECT * FROM log_item WHERE engagementId = :engagementId")
    fun getLogItem(engagementId:Int) : List<LogItem>

    @Query("DELETE FROM log_item WHERE engagementId = :engagementId")
    fun deleteLogItem(engagementId:Int)

    @Query("SELECT count(*) FROM log_item")
    fun getAllLogItems() : Int
}
