package com.tolgakurucay.mynotebook.viewmodels.main;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;

public class FeedFragmentViewModel extends ViewModel {

    MutableLiveData noteAdded=new MutableLiveData();



    public void addNote(){

        Log.d("bilgi","deneme");
        noteAdded.setValue(false);

    }

    public void signOut(FirebaseAuth auth){
        if(auth.getCurrentUser()!=null){

        }
        else
        {

        }

    }



}
