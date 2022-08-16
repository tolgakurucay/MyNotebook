package com.tolgakurucay.mynotebook.viewmodels.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tolgakurucay.mynotebook.dependencyinjection.AppModule;
import com.tolgakurucay.mynotebook.models.NoteFavoritesModel;
import com.tolgakurucay.mynotebook.services.NoteDAO;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FavoritesFragmentViewModel extends ViewModel {

    @Inject
    public FavoritesFragmentViewModel() {
    }

    public MutableLiveData<List<NoteFavoritesModel>> favoritesList = new MutableLiveData<>();
    public MutableLiveData<Boolean> deleted = new MutableLiveData<>();
    public MutableLiveData<Boolean> updated = new MutableLiveData<>();


    public void getFavorites(Context context) {

        NoteDAO dao = AppModule.INSTANCE.injectRoomDatabase(context).noteDao();
        List<NoteFavoritesModel> notes = dao.getFavorites();
        favoritesList.postValue(notes);

    }


    public void deleteFavorite(Context context, NoteFavoritesModel model) {

        try {
            NoteDAO dao = AppModule.INSTANCE.injectRoomDatabase(context).noteDao();
            dao.deleteFavorite(model);
            deleted.setValue(true);
        } catch (Exception ex) {
            deleted.setValue(false);
        }

    }

    public void updateFavorites(Context context, NoteFavoritesModel model) {


        try {
            NoteDAO dao = AppModule.INSTANCE.injectRoomDatabase(context).noteDao();
            dao.updateFavoriteItem(model);
            updated.setValue(true);
        } catch (Exception ex) {
            updated.setValue(false);
        }


    }

    public void shareFavorites(String title, String description, Activity activity) {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, title + "\n\n" + description);
        activity.startActivity(Intent.createChooser(shareIntent, title));


    }


}
