package com.tolgakurucay.mynotebook.viewmodels.main;

import android.accounts.AbstractAccountAuthenticator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

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
    public MutableLiveData<Boolean> updated=new MutableLiveData<>();
    public MutableLiveData<String> sharedMessage=new MutableLiveData<>();


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

    public void updateFavorites(Context context,NoteFavoritesModel model){
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
            dao.updateFavoriteItem(model);
            updated.setValue(true);
        }

    }

    public void shareFavorites(String title, String description, Activity activity){
        try{
            Intent shareIntent=new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT,title+"\n\n"+description);
            activity.startActivity(Intent.createChooser(shareIntent,title));
            sharedMessage.setValue("shared");

        }
        catch (Exception ex){
            sharedMessage.setValue(ex.getLocalizedMessage());
        }


    }


}
