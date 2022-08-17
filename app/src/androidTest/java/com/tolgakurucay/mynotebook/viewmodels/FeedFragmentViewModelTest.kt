package com.tolgakurucay.mynotebook.viewmodels


import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.google.common.truth.Truth.assertThat
import com.google.firebase.Timestamp
import com.tolgakurucay.mynotebook.MainCoroutineRule
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import com.tolgakurucay.mynotebook.services.NoteDAO
import com.tolgakurucay.mynotebook.services.NoteDatabase
import com.tolgakurucay.mynotebook.viewmodels.main.FeedFragmentViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test


    @ExperimentalCoroutinesApi
    class FeedFragmentViewModelTest {

        @get:Rule
        var instantTaskExecutorRule = InstantTaskExecutorRule()

        @get:Rule
        var mainCoroutineRule = MainCoroutineRule()

        private lateinit var viewModel: FeedFragmentViewModel
        lateinit var database : NoteDatabase
        private lateinit var dao : NoteDAO

        //the function running when the file open first
        @Before
        fun setup() {
            viewModel = FeedFragmentViewModel()
            database = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(),NoteDatabase::class.java)
                .allowMainThreadQueries()
                .build()

            dao=database.noteDao()
        }

        @Test
        fun addFavoriteNoteToDatabase() {
            val exampleNote=NoteFavoritesModel("test title","test desc",null,Timestamp.now().seconds)
            dao.insertFavorites(exampleNote)
            val favoritesList=dao.getFavorites()
            assertThat(favoritesList).contains(exampleNote)

        }

        @Test
        fun deleteFavoriteNoteFromDatabase(){

            val exampleNote=NoteFavoritesModel("test title1","test desc1",null,Timestamp.now().seconds)
            dao.insertFavorites(exampleNote)
            dao.deleteFavorite(exampleNote)
            val list= dao.getFavorites()
            assertThat(list).doesNotContain(exampleNote)

        }




        //the function running when the file close
        @After
        fun tearDown(){
            database.close()
        }







    }
