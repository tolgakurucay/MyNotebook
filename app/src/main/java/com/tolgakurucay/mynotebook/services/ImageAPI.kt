package com.tolgakurucay.mynotebook.services
import com.tolgakurucay.mynotebook.models.ImageResponse
import retrofit2.http.POST
import retrofit2.http.Query

interface ImageAPI {


    @POST(APIConstants.ENDPOINT+"?key=${APIConstants.KEY}")
    suspend fun getImagesInfos(
        @Query("q") photoKey:String,
        @Query("lang") language:String?="en"
    ) : ImageResponse

}