package com.tolgakurucay.mynotebook.utils;

import android.app.Application;
import android.content.Intent;

import com.google.firebase.auth.FirebaseAuth;
import com.tolgakurucay.mynotebook.views.main.MainActivity;

public class FirstClass extends Application {

    private FirebaseAuth auth;


    @Override
    public void onCreate() {
        super.onCreate();
        auth = FirebaseAuth.getInstance();
        if(auth.getCurrentUser()!=null){
            Intent intent= new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }


    }
}
