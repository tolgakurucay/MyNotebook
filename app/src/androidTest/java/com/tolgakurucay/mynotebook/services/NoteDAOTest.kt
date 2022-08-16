package com.tolgakurucay.mynotebook.services

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.google.firebase.Timestamp
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@SmallTest
@ExperimentalCoroutinesApi
class NoteDAOTest {

    companion object{
        const val TAG = "bilgi"
    }

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()


    private lateinit var dao : NoteDAO
    private lateinit var db : NoteDatabase

    @Before
    fun setup(){
        db= Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext()
        ,NoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()

        dao= db.noteDao()
    }

    @After
    fun tearDown(){
        db.close()
    }

    @Test
    fun insertFavoriteNoteTesting(){
        val exampleFavoriteNote=NoteFavoritesModel("TestTitle1","TestDesc1",null,Timestamp.now().seconds,1)
        dao.insertFavorites(exampleFavoriteNote)
        val favoriteList=dao.getFavorites()
        assertThat(favoriteList).contains(exampleFavoriteNote)
    }
    @Test
    fun deleteFavoriteNoteTesting(){

        val exampleFavoriteNote=NoteFavoritesModel("TestTitle2","TestDesc2",null,Timestamp.now().seconds,1)
        dao.insertFavorites(exampleFavoriteNote)
        dao.deleteFavorite(exampleFavoriteNote)


        val favoriteList=dao.getFavorites()
        assertThat(favoriteList).doesNotContain(exampleFavoriteNote)


    }







}