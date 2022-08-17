package com.tolgakurucay.mynotebook.viewmodels.main

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tolgakurucay.mynotebook.dependencyinjection.AppModule
import com.tolgakurucay.mynotebook.models.ImageResponse
import com.tolgakurucay.mynotebook.services.APIConstants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GetImageViewModel @Inject constructor(): ViewModel()
{


    val TAG="bilgi"
    val responseFromAPI=MutableLiveData<ImageResponse>()


    fun getImages(photokey:String){
        val api=AppModule.injectImageAPI()
        viewModelScope.launch {
            delay(2000)
            val response=api.getImagesInfos(photokey,"en")
            response.let {
                responseFromAPI.value=response

            }


        }

    }

}