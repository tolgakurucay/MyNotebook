package com.tolgakurucay.mynotebook.services

import androidx.room.*
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel
import com.tolgakurucay.mynotebook.models.NoteModel

@Dao
interface NoteDAO {

    @Insert
      fun insertNote(note:NoteModel)

    @Delete
      fun deleteNote(note:NoteModel)

    @Query("select * from notes")
      fun getAllNotes() :  List<NoteModel>

     @Query("select * from notes where id=:uuid")
      fun getNote(uuid:Int):NoteModel

      @Query("delete from notes")
    fun deleteAll()

    @Update
    fun updateNote(noteModel: NoteModel)

    @Insert
        fun insertFavorites(favoriteNote:NoteFavoritesModel)

        @Query("select * from NoteFavorites")
        fun getFavorites() : List<NoteFavoritesModel>


}