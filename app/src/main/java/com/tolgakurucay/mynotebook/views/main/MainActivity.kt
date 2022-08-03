package com.tolgakurucay.mynotebook.views.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.ActivityMainBinding
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.utils.Util
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    val TAG="bilgi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        if(intent.getStringExtra("from")=="buy"){
            overridePendingTransition(R.anim.from_right,R.anim.to_left)
        }
        else
        {
            overridePendingTransition(R.anim.from_left,R.anim.to_right)
        }
        setContentView(binding.root)
        Log.d(TAG, "onCreate: mainactivity")

        Util.setLocale(Util.getLanguage(this),this)



    }
}