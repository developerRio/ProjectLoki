package com.originalstocks.projectloki.ui.baseActivity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient
import com.originalstocks.projectloki.R
import com.originalstocks.projectloki.data.helpers.SharedPref


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    companion object {
        var placesClient: PlacesClient? = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (!Places.isInitialized()) {
            Places.initialize(application, getString(R.string.api_key))
        }

        //setting up places client
        placesClient = Places.createClient(this)

        SharedPref.init(this)
        SharedPref.write(SharedPref.AUTH_KEY, getString(R.string.api_key))

    }



}