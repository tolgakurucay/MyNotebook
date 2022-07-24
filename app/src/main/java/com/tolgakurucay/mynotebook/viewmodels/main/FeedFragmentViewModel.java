package com.tolgakurucay.mynotebook.viewmodels.main;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.google.android.material.snackbar.Snackbar;
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel;
import com.tolgakurucay.mynotebook.models.NoteModel;
import com.tolgakurucay.mynotebook.services.NoteDAO;
import com.tolgakurucay.mynotebook.services.NoteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FeedFragmentViewModel extends ViewModel {


    public MutableLiveData<List<NoteModel>> noteList= new MutableLiveData<>();




    public void getAllNotes(Context context){
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
            NoteDAO dao= db.noteDao();
            //dao.deleteAll();
           List<NoteModel> notes = dao.getAllNotes();
           noteList.setValue(notes);
        }

    }

    public void deleteNotes(Context context, ArrayList<NoteModel> noteList){
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
            NoteDAO dao =db.noteDao();
            for(int i=0;i<noteList.size();i++){
                dao.deleteNote(noteList.get(i));
            }


        }

    }

    public void addFavorites(Context context,ArrayList<NoteFavoritesModel> favoritesList){
        NoteDatabase db= new NoteDatabase() {
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

            NoteDAO dao=db.noteDao();

            for(int i=0;i<favoritesList.size();i++){

                dao.insertFavorites(favoritesList.get(i));
            }

        }
    }



}
