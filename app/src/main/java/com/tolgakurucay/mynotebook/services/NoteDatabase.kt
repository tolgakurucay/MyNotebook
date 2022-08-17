package com.tolgakurucay.mynotebook.services

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import com.tolgakurucay.mynotebook.models.NoteModel

@Database(entities = [NoteModel::class,NoteFavoritesModel::class], version = 6)
 abstract class NoteDatabase : RoomDatabase(){

    abstract fun noteDao(): NoteDAO



}