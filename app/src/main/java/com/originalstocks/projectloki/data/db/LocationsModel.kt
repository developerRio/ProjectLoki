package com.originalstocks.projectloki.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationsModel(
    // Column names
    val title: String,
    val locationLatLng: String,
) {
    // id will be a primary key hence it'll be annotated as @PrimaryKey
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}