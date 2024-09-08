package com.tolgakurucay.mynotebook.models


import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable


@Entity(tableName = "Notes")
data class NoteModel(
    @ColumnInfo(name = "Title")
    var title: String?,

    @ColumnInfo(name = "Description")
    var description: String?,

    @ColumnInfo(name = "Image")
    var imageBase64: String?,

    @ColumnInfo(name = "Date")
    var date: Long?,

    @PrimaryKey(autoGenerate = true)
    var id: Int? = null

) : Serializable

