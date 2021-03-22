package com.originalstocks.projectloki.ui.fragments

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.room.Room
import com.originalstocks.projectloki.data.db.LocationDatabase
import com.originalstocks.projectloki.data.db.LocationsModel
import com.originalstocks.projectloki.data.helpers.Status
import com.originalstocks.projectloki.data.helpers.getApiKey
import com.originalstocks.projectloki.ui.baseModel.BaseModel
import com.originalstocks.projectloki.ui.models.Route
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(application: Application) : BaseModel(application) {
    private val TAG = "HomeViewModel"

    private val mDbStatusLiveData = MutableLiveData<Status>()
    val dbStatusLiveData: LiveData<Status>
        get() = mDbStatusLiveData

    private val mDbStatusDelete = MutableLiveData<Status>()
    val dbStatusDelete: LiveData<Status>
        get() = mDbStatusDelete

    private val mDbStatusDirections = MutableLiveData<Status>()
    val dbStatusDirections: LiveData<Status>
        get() = mDbStatusDirections

    private val mSubscriptionData = MutableLiveData<Route>()
    val mSubscriptionLiveData: LiveData<Route>
        get() = mSubscriptionData


    private val mDao =
        Room.databaseBuilder(application, LocationDatabase::class.java, "locationsdatabase")
            .build()
            .getLocationsDao()

    // applying pagination for 20 results as threshold.
    val locationsFlowData = Pager(
        PagingConfig(
            pageSize = 20,
            enablePlaceholders = true,
            maxSize = 80
        )
    ) { mDao.getAllPagedLocations() }.flow

    fun getQueriedLocation(originPlace: String, enteredPlace: String) {
        scope.launch {
            mDbStatusDirections.value = Status.INIT

            try {
                mDbStatusDirections.value = Status.SUCCESS
                val response = service.getDataAsync(originPlace, enteredPlace, getApiKey()!!)
                mSubscriptionData.value = response.await()
                Log.i(TAG, "getQueriedLocation_response = ${mDbStatusDirections.value}")

                if (mSubscriptionData.value!!.status == "OK") {
                    mDbStatusDirections.value = Status.SUCCESS

                } else {
                    mDbStatusDirections.value = Status.ERROR
                }

            } catch (e: Exception) {
                e.printStackTrace()
                mDbStatusDirections.value = Status.ERROR
            }
        }

    }

    fun saveThisData(context: Context, noteModel: LocationsModel) {

        scope.launch {
            mDbStatusLiveData.value = Status.INIT
            Log.i(TAG, "startCheckingForConnection: INIT !")

            try {
                mDbStatusLiveData.value = Status.SAVING
                val isDataSaved = doAddInBackground(context, noteModel)
                if (isDataSaved) {
                    mDbStatusLiveData.value = Status.DATA_SAVED
                    Log.i(TAG, "startCheckingForConnection: DATA_SAVED !")
                }

            } catch (e: Exception) {
                e.printStackTrace()
                mDbStatusLiveData.value = Status.ERROR
                Log.e(TAG, "startCheckingForConnection: SOME ERROR CAME!")
            }
        }
    }

    fun deleteAllData(context: Context) {
        scope.launch {
            mDbStatusDelete.value = Status.INIT
            try {
                mDbStatusDelete.value = Status.DELETING
                val isDataCleared = doRemoveInBackground(context)
                if (isDataCleared) {
                    mDbStatusDelete.value = Status.DELETED
                }
            } catch (e: Exception) {
                e.printStackTrace()
                mDbStatusDelete.value = Status.ERROR
            }
        }
    }

    private suspend fun doAddInBackground(
        context: Context,
        locationsModel: LocationsModel
    ): Boolean =
        withContext(Dispatchers.IO) { // to run code in Background Thread
            // doing async work
            Log.i(TAG, "doInBackground: called")
            LocationDatabase(context).getLocationsDao().addLocation(locationsModel)
            //NoteDatabase(context).getNoteDao().addNote(note = noteModel)
            return@withContext true
        }

    private suspend fun doRemoveInBackground(context: Context): Boolean =
        withContext(Dispatchers.IO) { // to run code in Background Thread
            // doing async work
            Log.i(TAG, "doInBackground: called")
            LocationDatabase(context).getLocationsDao().removeAllData()
            return@withContext true
        }

}