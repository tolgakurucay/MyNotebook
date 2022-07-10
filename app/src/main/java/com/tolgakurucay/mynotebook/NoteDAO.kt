package com.tolgakurucay.mynotebook

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
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



}