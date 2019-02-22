package product.clicklabs.jugnoo.driver.altmetering.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import product.clicklabs.jugnoo.driver.altmetering.model.LastLocationTimestamp
import product.clicklabs.jugnoo.driver.altmetering.model.ScanningPointer
import product.clicklabs.jugnoo.driver.altmetering.model.Segment
import product.clicklabs.jugnoo.driver.altmetering.model.Waypoint

@Database(entities = [Segment::class,
    Waypoint::class,
    ScanningPointer::class,
    LastLocationTimestamp::class], version = 3)
abstract class MeteringDatabase: RoomDatabase(){

    companion object {
        @Volatile
        private var instance:MeteringDatabase? = null

        fun getInstance(context : Context):MeteringDatabase?{
            if(instance == null){
                synchronized(MeteringDatabase::class.java){
                    if(instance == null){
                        instance = Room.databaseBuilder(context.applicationContext, MeteringDatabase::class.java, "db_user")
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

    abstract fun getMeteringDao(): MeteringDao

}