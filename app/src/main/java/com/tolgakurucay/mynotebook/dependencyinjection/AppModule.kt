package com.tolgakurucay.mynotebook.dependencyinjection

import android.content.Context
import androidx.room.Room
import com.tolgakurucay.mynotebook.services.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun injectRoomDatabase(@ApplicationContext context:Context) = Room.databaseBuilder(context,NoteDatabase::class.java,"NoteDatabase")
        .allowMainThreadQueries()
        .fallbackToDestructiveMigration()
        .build()

    @Singleton
    @Provides
    fun injectDao(database: NoteDatabase)= database.noteDao()

}