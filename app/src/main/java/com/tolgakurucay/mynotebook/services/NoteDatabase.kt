package com.tolgakurucay.mynotebook.services

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import com.tolgakurucay.mynotebook.models.NoteModel

@Database(entities = [NoteModel::class,NoteFavoritesModel::class], version = 3)
 abstract class NoteDatabase : RoomDatabase(){

    abstract fun noteDao(): NoteDAO




       private var instance: NoteDatabase?=null
        fun getBookDatabase(context:Context) : NoteDatabase?{
            if(instance==null){
                instance=Room.databaseBuilder(context.applicationContext, NoteDatabase::class.java,"NoteDatabase")
                    .allowMainThreadQueries()
                   // .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }

        fun destroyInstance(){
            instance=null
        }














}