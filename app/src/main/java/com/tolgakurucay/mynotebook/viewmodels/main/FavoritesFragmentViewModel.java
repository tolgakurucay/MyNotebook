package com.tolgakurucay.mynotebook.viewmodels.main;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.sqlite.db.SupportSQLiteOpenHelper;

import com.tolgakurucay.mynotebook.models.NoteFavoritesModel;
import com.tolgakurucay.mynotebook.services.NoteDAO;
import com.tolgakurucay.mynotebook.services.NoteDatabase;

import java.util.ArrayList;
import java.util.List;

public class FavoritesFragmentViewModel extends ViewModel {


   public MutableLiveData<List<NoteFavoritesModel>> favoritesList=new MutableLiveData<>();
    public MutableLiveData<Boolean> deleted=new MutableLiveData<>();


    public void getFavorites(Context context){

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
            NoteDAO dao= db.noteDao();
            List<NoteFavoritesModel> notes= dao.getFavorites();
            favoritesList.setValue(notes);
        }

    }


    public void deleteFavorite(Context context,NoteFavoritesModel model){
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
            dao.deleteFavorite(model);
            deleted.setValue(true);
        }
    }

    public void saveChanges(Context context){

    }


}
