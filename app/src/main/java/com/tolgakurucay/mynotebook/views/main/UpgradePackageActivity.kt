package com.tolgakurucay.mynotebook.views.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.tolgakurucay.mynotebook.databinding.ActivityUpgradePackageBinding

class UpgradePackageActivity : AppCompatActivity() {

    private lateinit var binding:ActivityUpgradePackageBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpgradePackageBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}