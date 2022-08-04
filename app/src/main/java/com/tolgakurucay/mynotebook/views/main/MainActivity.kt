package com.tolgakurucay.mynotebook.views.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
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
    private lateinit var navigationController:NavController
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

       val navHostFragment=supportFragmentManager.findFragmentById(R.id.mainContainerView) as NavHostFragment
        navigationController=navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this,navigationController)




        Util.setLocale(Util.getLanguage(this),this)




    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navigationController,null)
    }


}