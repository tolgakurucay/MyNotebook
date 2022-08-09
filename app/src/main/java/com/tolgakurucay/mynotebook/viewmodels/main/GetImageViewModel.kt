package com.tolgakurucay.mynotebook.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tolgakurucay.mynotebook.dependencyinjection.AppModule
import com.tolgakurucay.mynotebook.services.APIConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetImageViewModel @Inject constructor(): ViewModel()
{


    val TAG="bilgi"

    fun getImages(){
        val api=AppModule.injectImageAPI()
        viewModelScope.launch {
            val response=api.getImagesInfos("sun","en")
            Log.d(TAG, "getImages: $response")

        }

    }

}