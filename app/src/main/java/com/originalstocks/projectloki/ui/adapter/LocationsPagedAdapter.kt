package com.originalstocks.projectloki.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.originalstocks.projectloki.data.db.LocationsModel
import com.originalstocks.projectloki.data.db.paginationHelper.NoteDiffCallback
import com.originalstocks.projectloki.data.helpers.showToast
import com.originalstocks.projectloki.databinding.LocationsItemsLayoutBinding

class LocationsPagedAdapter(val context: Context) :
    PagingDataAdapter<LocationsModel, LocationsPagedAdapter.LocationViewHolder>(NoteDiffCallback) {
    class LocationViewHolder(val binding: LocationsItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {}

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val locationData = getItem(position)
        if (locationData == null) {
            holder.binding.apply {
                titleTextView.text = ""
                latLngTextView.text = ""
            }
        } else {
            holder.binding.apply {
                titleTextView.text = locationData.title
                latLngTextView.text = locationData.locationLatLng
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            LocationsItemsLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }
}