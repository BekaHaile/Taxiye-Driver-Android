package product.clicklabs.jugnoo.driver.room.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
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