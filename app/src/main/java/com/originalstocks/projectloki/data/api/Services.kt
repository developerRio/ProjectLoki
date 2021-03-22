package com.originalstocks.projectloki.data.api

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.originalstocks.projectloki.ui.models.Route
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

//val BASE_URL = "https://maps.googleapis.com/maps/api/directions/json?origin=Time+Square&destination=Chelsea+Market&key=AIzaSyCan9PUaxerh16jjnuO6u5QklWhe8UT_bU"
const val BASE_URL = "https://maps.googleapis.com/maps/api/"

private val retrofit = Retrofit
    .Builder()
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .addConverterFactory(GsonConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface Services {

    @GET("directions/json")
    fun getDataAsync(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("key") key: String
    ): Deferred<Route>

}

object Api {
    val retrofitService: Services by lazy { retrofit.create(Services::class.java) }
}