package com.tolgakurucay.mynotebook.viewmodels.main;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.tolgakurucay.mynotebook.BaseViewModel;
import com.tolgakurucay.mynotebook.NoteDAO;
import com.tolgakurucay.mynotebook.NoteDatabase;
import com.tolgakurucay.mynotebook.models.NoteModel;

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

    public void addNoteToLocal(NoteModel note){

        try{
           NoteDatabase database= new NoteDatabase() {
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
           };

            NoteDAO dao=database.noteDao();
            dao.insertNote(note);
            addingMessage.setValue("added");

        }
        catch (Exception ex){
            addingMessage.setValue(ex.getLocalizedMessage());
        }









    }




}
