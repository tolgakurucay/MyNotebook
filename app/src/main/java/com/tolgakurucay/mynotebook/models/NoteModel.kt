package com.tolgakurucay.mynotebook.models

import android.graphics.Bitmap
import android.net.Uri
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import kotlinx.android.parcel.Parcelize
import java.io.Serializable
import java.util.*

@Parcelize
@Entity(tableName = "Notes")
 data class NoteModel(
    @ColumnInfo(name="Title")
     var title:String,

    @ColumnInfo(name="Description")
    var description:String,

    @ColumnInfo(name="Image")
    var imageBase64: String?,

    @ColumnInfo(name="Date")
    var date: Long):Parcelable
{
    @PrimaryKey(autoGenerate = true)
    var id:Int=0
}
