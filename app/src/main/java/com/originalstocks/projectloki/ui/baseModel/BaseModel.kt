package com.originalstocks.projectloki.ui.baseModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.originalstocks.projectloki.data.api.Api
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

open class BaseModel(application: Application) : AndroidViewModel(application) {
    private val viewModelJob = Job()
    val scope = CoroutineScope(viewModelJob + Dispatchers.Main)
    val service = Api.retrofitService

    override fun onCleared() {
        super.onCleared()
        viewModelJob.cancel()
    }

}