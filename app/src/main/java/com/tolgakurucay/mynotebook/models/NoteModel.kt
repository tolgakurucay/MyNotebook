package com.tolgakurucay.mynotebook.models

import android.graphics.Bitmap
import android.net.Uri
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp
import java.util.*

@Entity(tableName = "Notes")
data class NoteModel(
    @ColumnInfo(name="Title")
     val title:String,

    @ColumnInfo(name="Description")
    val description:String,

    @ColumnInfo(name="Image")
    val imageBase64: String?,

    @ColumnInfo(name="Date")
    val date: Long)
{
    @PrimaryKey(autoGenerate = true)
    var id:Int=0
}
