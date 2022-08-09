package com.tolgakurucay.mynotebook.viewmodels.main;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.tolgakurucay.mynotebook.dependencyinjection.AppModule;
import com.tolgakurucay.mynotebook.models.NoteModel;
import com.tolgakurucay.mynotebook.services.NoteDAO;
import com.tolgakurucay.mynotebook.services.NoteDatabase;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class NoteFragmentViewModel extends ViewModel {

    @Inject
    NoteFragmentViewModel(){}


    public MutableLiveData<String> updated= new MutableLiveData<>();

    public MutableLiveData<String> titleMessage=new MutableLiveData<>();

    public MutableLiveData<String> descriptionMessage= new MutableLiveData<>();

    public MutableLiveData<String> deleted=new MutableLiveData<>();


    public void updateModel(NoteModel model, Context context){

        try{

            NoteDatabase db = AppModule.INSTANCE.injectRoomDatabase(context);
            NoteDAO dao=AppModule.INSTANCE.injectDao(db);
            dao.updateNote(model);
            updated.setValue("updated");

        }


        catch (Exception ex){
            updated.setValue(ex.getLocalizedMessage());
        }


    }

    public void deleteModel(NoteModel model,Context context){
        try{/*
            NoteDatabase db = new NoteDatabase() {
                @NonNull
                @Override
                public NoteDAO noteDao() {
                    return null;
                }

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
            }.getBookDatabase(context);
            if(db!=null){
                NoteDAO dao = db.noteDao();
                dao.deleteNote(model);
                deleted.setValue("deleted");
            }*/
            NoteDatabase db = AppModule.INSTANCE.injectRoomDatabase(context);
            NoteDAO dao=AppModule.INSTANCE.injectDao(db);
            dao.deleteNote(model);
            deleted.setValue("deleted");
        }
        catch (Exception ex){
            deleted.setValue(ex.getLocalizedMessage());
        }
    }

    public void validateTitle(String title){
        if(title.length()==0){
            titleMessage.setValue("enteratitle");
        }
        else if(title.length()<3)
        {
            titleMessage.setValue("atleast3");
        }
        else
        {
            titleMessage.setValue("validated");
        }
    }

    public void validateDescription(String description){
        if(description.length()==0){
            descriptionMessage.setValue("enteradescription");
        }
        else if(description.length()<3)
        {
            descriptionMessage.setValue("atleast3");
        }
        else
        {
            descriptionMessage.setValue("validated");
        }
    }








}
