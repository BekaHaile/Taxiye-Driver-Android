package product.clicklabs.jugnoo.driver.altmetering.db

import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import product.clicklabs.jugnoo.driver.altmetering.model.*

@Database(entities = [
    Segment::class,
    Waypoint::class,
    ScanningPointer::class,
    LastLocationTimestamp::class,
    LogItem::class
],//please update version number in any changes or addition to schema classes
        version = 5)
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