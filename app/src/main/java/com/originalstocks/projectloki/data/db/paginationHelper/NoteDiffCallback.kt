package com.originalstocks.projectloki.data.db.paginationHelper

import androidx.recyclerview.widget.DiffUtil
import com.originalstocks.projectloki.data.db.LocationsModel

object NoteDiffCallback : DiffUtil.ItemCallback<LocationsModel>() {
    override fun areItemsTheSame(oldItem: LocationsModel, newItem: LocationsModel): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: LocationsModel, newItem: LocationsModel): Boolean {
        return oldItem == newItem
    }
}