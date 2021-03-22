package com.originalstocks.projectloki.ui.models

import android.os.Parcelable

@kotlinx.parcelize.Parcelize
data class Route(
    val status: String = "",
    val error_message: String = "",
    val startName: String = "",
    val endName: String = "",
    val startLat: Double?,
    val startLng: Double?,
    val endLat: Double?,
    val endLng: Double?,
    val overviewPolyline: String = ""
) : Parcelable