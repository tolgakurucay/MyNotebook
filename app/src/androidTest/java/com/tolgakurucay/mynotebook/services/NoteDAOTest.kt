package com.tolgakurucay.mynotebook.services

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import com.google.common.truth.Truth.assertThat
import com.google.firebase.Timestamp
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import com.tolgakurucay.mynotebook.models.NoteModel
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import javax.inject.Inject
import javax.inject.Named

@HiltAndroidTest
@SmallTest
@ExperimentalCoroutinesApi
class NoteDAOTest {


    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Named("testDatabase")
    @Inject
    lateinit var database : NoteDatabase


    private lateinit var dao : NoteDAO
    //private lateinit var db : NoteDatabase

    @Before
    fun setup(){

        hiltRule.inject()
        /*
        db= Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext()
        ,NoteDatabase::class.java)
            .allowMainThreadQueries()
            .build()*/

        dao= database.noteDao()


    }

    @After
    fun tearDown(){
        database.close()
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
    @Test
    fun updateFavoriteNoteTesting(){
        val exampleFavoriteNote=NoteFavoritesModel("TestTitle2","TestDesc2",null,Timestamp.now().seconds,1)
        val firstTitle=exampleFavoriteNote.title
        dao.insertFavorites(exampleFavoriteNote)
        exampleFavoriteNote.title="TestTitle3"
        dao.updateFavoriteItem(exampleFavoriteNote)

        val list=dao.getFavorites()
        val updatedModelTitle=list[0].title

        assertThat(updatedModelTitle).isNotEqualTo(firstTitle)



    }

    @Test
    fun deleteAllFavoritesNoteTesting(){
        val exampleFavoriteNote=NoteFavoritesModel("TestTitle3","TestDesc4",null,Timestamp.now().seconds,1)
        val exampleFavoriteNote2=NoteFavoritesModel("TestTitle4","TestDesc5",null,Timestamp.now().seconds,2)
        dao.insertFavorites(exampleFavoriteNote)
        dao.insertFavorites(exampleFavoriteNote2)
        dao.deleteAllFavorites()
        val list=dao.getFavorites()

        assertThat(list).isEmpty()
    }

    @Test
    fun insertNote(){
        val exampleNote = NoteModel("TestTitle3","TestDesc4",null,Timestamp.now().seconds,1)
        dao.insertNote(exampleNote)

        val noteList = dao.getAllNotes()

        assertThat(noteList).contains(exampleNote)

    }

    @Test
    fun deleteNote(){
        val exampleNote = NoteModel("TestTitle3","TestDesc4",null,Timestamp.now().seconds,2)
        dao.insertNote(exampleNote)
        dao.deleteNote(exampleNote)

        val noteList = dao.getAllNotes()

        assertThat(noteList).doesNotContain(exampleNote)
    }

    @Test
    fun getNote(){
        val exampleNote1 = NoteModel("TestTitle3","TestDesc4",null,Timestamp.now().seconds,3)
        val exampleNote2 = NoteModel("TestTitle4","TestDesc5",null,Timestamp.now().seconds,4)
        val exampleNote3 = NoteModel("TestTitle5","TestDesc6",null,Timestamp.now().seconds,5)

        dao.insertNote(exampleNote1)
        dao.insertNote(exampleNote2)
        dao.insertNote(exampleNote3)


        val findExampleNote1 = dao.getNote(exampleNote1.id!!)


        assertThat(exampleNote1).isEqualTo(findExampleNote1)

    }

    @Test
    fun deleteNotes(){
        val exampleNote1 = NoteModel("TestTitle3","TestDesc4",null,Timestamp.now().seconds,3)
        val exampleNote2 = NoteModel("TestTitle4","TestDesc5",null,Timestamp.now().seconds,4)
        val exampleNote3 = NoteModel("TestTitle5","TestDesc6",null,Timestamp.now().seconds,5)

        dao.insertNote(exampleNote1)
        dao.insertNote(exampleNote2)
        dao.insertNote(exampleNote3)

        dao.deleteAll()

        val list=dao.getAllNotes()

        assertThat(list).isEmpty()

    }

    @Test
    fun updateNote(){
        val exampleNote1 = NoteModel("TestTitle3","TestDesc4",null,Timestamp.now().seconds,3)
        val firstDesc=exampleNote1.description
        dao.insertNote(exampleNote1)
        exampleNote1.description="i changed the description of TestTitle3"
        dao.updateNote(exampleNote1)

        val note=dao.getNote(exampleNote1.id!!)
        assertThat(note.description).isNotEqualTo(firstDesc)

    }







}