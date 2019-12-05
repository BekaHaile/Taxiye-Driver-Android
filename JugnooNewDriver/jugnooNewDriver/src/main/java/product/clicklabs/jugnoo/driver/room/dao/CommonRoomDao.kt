package product.clicklabs.jugnoo.driver.room.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import product.clicklabs.jugnoo.driver.room.model.AcceptLatLng

@Dao
interface CommonRoomDao{

    @Query("SELECT * FROM tb_accept_latlng WHERE engagementId = :engagementId")
    fun getAcceptLatLng(engagementId:Long) : MutableList<AcceptLatLng>?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAcceptLatLng(acceptLatLng: AcceptLatLng)

    @Query("DELETE FROM tb_accept_latlng WHERE engagementId = :engagementId")
    fun deleteAcceptLatLng(engagementId:Long)

    @Query("DELETE FROM tb_accept_latlng WHERE timeStamp < :timeStamp")
    fun deleteAcceptLatLngOld(timeStamp:Long)

}