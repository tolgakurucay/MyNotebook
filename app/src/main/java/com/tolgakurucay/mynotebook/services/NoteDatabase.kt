package com.tolgakurucay.mynotebook.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import com.tolgakurucay.mynotebook.models.NoteModel

@Database(entities = [NoteModel::class,NoteFavoritesModel::class], version = 5)
 abstract class NoteDatabase : RoomDatabase(){

    abstract fun noteDao(): NoteDAO



}