package com.tolgakurucay.mynotebook.models

data class ImageResponse(

    val hits: List<ImageResult>?,
    val total : Int?,
    val totalHits : Int?


)