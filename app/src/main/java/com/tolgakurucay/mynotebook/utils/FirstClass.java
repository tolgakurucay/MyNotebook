package com.tolgakurucay.mynotebook.utils;

import android.app.Activity;
import android.app.Application;
import android.content.ContextParams;
import android.content.Intent;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tolgakurucay.mynotebook.views.login.LoginActivity;
import com.tolgakurucay.mynotebook.views.main.MainActivity;

import java.util.Objects;

public class FirstClass extends Application {

    private FirebaseAuth auth;


    @Override
    public void onCreate() {
        super.onCreate();

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {

            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }



    }






}
