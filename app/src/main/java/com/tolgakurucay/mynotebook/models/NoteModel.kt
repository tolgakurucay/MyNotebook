package com.tolgakurucay.mynotebook.models

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

    @ColumnInfo(name="ImageURI")
    val imageUri: Uri?,

    @ColumnInfo(name="Date")
    val date: Timestamp)
{
    @PrimaryKey(autoGenerate = true)
    var id:Int=0
}
