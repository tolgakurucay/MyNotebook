package com.tolgakurucay.mynotebook.utils;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.tolgakurucay.mynotebook.views.main.MainActivity;

import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class FirstClass extends Application {

    private FirebaseAuth auth;
    String TAG="bilgi";


    @Override
    public void onCreate() {



        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null && auth.getCurrentUser().isEmailVerified()) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            Log.d(TAG, "onCreateapplication: girildi");

        }


            super.onCreate();



    }






}
