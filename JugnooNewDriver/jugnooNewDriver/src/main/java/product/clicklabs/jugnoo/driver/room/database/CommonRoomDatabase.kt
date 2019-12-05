package product.clicklabs.jugnoo.driver.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import product.clicklabs.jugnoo.driver.room.dao.CommonRoomDao
import product.clicklabs.jugnoo.driver.room.model.AcceptLatLng

@Database(entities = [
    AcceptLatLng::class
],//please update version number in any changes or addition to schema classes
        version = 1)
abstract class CommonRoomDatabase: RoomDatabase(){

    companion object {
        @Volatile
        private var instance:CommonRoomDatabase? = null

        fun getInstance(context : Context):CommonRoomDatabase?{
            if(instance == null){
                synchronized(CommonRoomDatabase::class.java){
                    if(instance == null){
                        instance = Room.databaseBuilder(context.applicationContext, CommonRoomDatabase::class.java, "db_common_room")
                                .fallbackToDestructiveMigration()
                                .addCallback(callback).build()
                    }
                }
            }
            return instance
        }


        private val callback: RoomDatabase.Callback = object : RoomDatabase.Callback(){
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        }

    }

    abstract fun getDao(): CommonRoomDao

}