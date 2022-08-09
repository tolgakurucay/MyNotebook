package com.tolgakurucay.mynotebook.dependencyinjection

import android.content.Context
import androidx.room.Room
import com.tolgakurucay.mynotebook.services.APIConstants
import com.tolgakurucay.mynotebook.services.ImageAPI
import com.tolgakurucay.mynotebook.services.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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

    @Singleton
    @Provides
    fun injectImageAPI() : ImageAPI{
        return Retrofit.Builder()
            .baseUrl(APIConstants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImageAPI::class.java)
    }



}