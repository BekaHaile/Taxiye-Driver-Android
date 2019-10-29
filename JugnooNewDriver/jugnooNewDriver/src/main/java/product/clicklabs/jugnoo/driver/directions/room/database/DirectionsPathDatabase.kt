package product.clicklabs.jugnoo.driver.directions.room.database

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import product.clicklabs.jugnoo.driver.directions.room.dao.DirectionsPathDao
import product.clicklabs.jugnoo.driver.directions.room.model.Path
import product.clicklabs.jugnoo.driver.directions.room.model.Point

@Database(entities = [
    Point::class,
    Path::class
],//please update version number in any changes or addition to schema classes
        version = 1)
abstract class DirectionsPathDatabase: RoomDatabase(){

    companion object {
        @Volatile
        private var instance:DirectionsPathDatabase? = null

        fun getInstance(context : Context):DirectionsPathDatabase?{
            if(instance == null){
                synchronized(DirectionsPathDatabase::class.java){
                    if(instance == null){
                        instance = Room.databaseBuilder(context.applicationContext, DirectionsPathDatabase::class.java, "db_directions_path")
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

    abstract fun getDao(): DirectionsPathDao

}