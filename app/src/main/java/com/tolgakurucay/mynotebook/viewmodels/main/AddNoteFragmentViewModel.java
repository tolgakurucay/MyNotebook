package com.tolgakurucay.mynotebook.viewmodels.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;


import com.tolgakurucay.mynotebook.services.NoteDAO;
import com.tolgakurucay.mynotebook.services.NoteDatabase;
import com.tolgakurucay.mynotebook.models.NoteModel;
import com.tolgakurucay.mynotebook.services.NoteDatabase_Impl;

import java.util.Objects;

public class AddNoteFragmentViewModel extends ViewModel {

    public MutableLiveData<String> titleMessage=new MutableLiveData();
    public MutableLiveData<String> descriptionMessage=new MutableLiveData();
    public MutableLiveData<String> addingMessage=new MutableLiveData();

    public void verifyTitle(String title){
        if(Objects.equals(title, "")){
            titleMessage.setValue("empty");
        }
        else if(title.length()<3){
            titleMessage.setValue("atleast3characters");
        }
        else
        {
            titleMessage.setValue(null);
        }
    }

    public void verifyDescription(String description){
        if(description.equals("")){
            descriptionMessage.setValue("empty");
        }

        else
        {
            descriptionMessage.setValue(null);
        }
    }

    public void addNoteToLocal(NoteModel note, Context context){

        try{


            NoteDatabase db = new NoteDatabase() {
                @NonNull
                @Override
                protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration config) {
                    return null;
                }

                @NonNull
                @Override
                protected InvalidationTracker createInvalidationTracker() {
                    return null;
                }

                @Override
                public void clearAllTables() {

                }

                @NonNull
                @Override
                public NoteDAO noteDao() {
                    return null;
                }
            }.getBookDatabase(context);

            NoteDAO dao= db.noteDao();
            dao.insertNote(note);


        }
        catch (Exception ex){
            addingMessage.setValue(ex.getLocalizedMessage());
        }


    }


    public void getNotes(){

    }




}
