package com.tolgakurucay.mynotebook.viewmodels.main;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.google.firebase.auth.FirebaseAuth;
import com.tolgakurucay.mynotebook.models.NoteModel;
import com.tolgakurucay.mynotebook.services.NoteDAO;
import com.tolgakurucay.mynotebook.services.NoteDatabase;

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
           List<NoteModel> notes = dao.getAllNotes();
           noteList.setValue(notes);
        }

    }



}
