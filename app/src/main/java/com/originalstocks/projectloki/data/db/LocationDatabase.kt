package com.originalstocks.projectloki.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [LocationsModel::class],
    version = 1,
    exportSchema = false
)
abstract class LocationDatabase : RoomDatabase() {
    abstract fun getLocationsDao(): LocationDao

    companion object {
        @Volatile // this annotation means this var will be instantly available for all threads.
        private var instance: LocationDatabase? = null

        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDataBase(context).also {
                instance = it
            }
        }
        private fun buildDataBase(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            LocationDatabase::class.java,
            "locationsdatabase"
        ).build()

    }
}