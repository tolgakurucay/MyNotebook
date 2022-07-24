package com.tolgakurucay.mynotebook.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize




    @Parcelize
    @Entity(tableName = "NoteFavorites")
    data class NoteFavoritesModel(
        @ColumnInfo(name="Title")
        var title:String,

        @ColumnInfo(name="Description")
        var description:String,

        @ColumnInfo(name="Image")
        var imageBase64: String?,

        @ColumnInfo(name="Date")
        var date: Long): Parcelable
    {
        @PrimaryKey(autoGenerate = true)
        var id:Int=0
    }



