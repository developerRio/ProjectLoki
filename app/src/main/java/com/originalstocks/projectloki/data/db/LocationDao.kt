package com.originalstocks.projectloki.data.db

import androidx.paging.PagingSource
import androidx.room.*

@Dao
interface LocationDao {
    @Insert
    fun addLocation(locationsModel: LocationsModel)

    @Query("SELECT * FROM LocationsModel ORDER BY id DESC")
    fun getAllLocations(): List<LocationsModel>

    @Query("SELECT * FROM LocationsModel ORDER BY id DESC")
    fun getAllPagedLocations(): PagingSource<Int, LocationsModel>

    @Query("DELETE FROM LocationsModel")
    fun removeAllData()
}