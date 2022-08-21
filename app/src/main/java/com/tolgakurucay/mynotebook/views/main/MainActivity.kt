package com.tolgakurucay.mynotebook.views.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.firebase.auth.FirebaseAuth
import com.tolgakurucay.mynotebook.PolicyFragment
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.ActivityMainBinding
import com.tolgakurucay.mynotebook.utils.CustomLoadingDialog
import com.tolgakurucay.mynotebook.utils.Util
import com.tolgakurucay.mynotebook.viewmodels.main.MainActivityViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMainBinding
    private lateinit var navigationController:NavController
    private val viewModel:MainActivityViewModel by viewModels()
    @Inject lateinit var policyPopup : PolicyFragment
    val TAG="bilgi"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        animations()
        setContentView(binding.root)
        setup()
        checkPolicy()


        observeLiveData()



    }

    private fun setup(){
        val navHostFragment=supportFragmentManager.findFragmentById(R.id.mainContainerView) as NavHostFragment
        navigationController=navHostFragment.navController
        NavigationUI.setupActionBarWithNavController(this,navigationController)
        Util.setLocale(Util.getLanguage(this),this)




    }

    private fun animations(){
        if(intent.getStringExtra("from")=="buy"){
            overridePendingTransition(R.anim.from_right,R.anim.to_left)
        }
        else
        {
            overridePendingTransition(R.anim.from_left,R.anim.to_right)
        }
    }
    private fun checkPolicy(){
        viewModel.checkPolicyAgreement()
    }

    private fun observeLiveData(){
        viewModel.isAgreed.observe(this, Observer {
            it?.let {
                if(it){
                    Log.d(TAG, "agreement: $it")
                }
                else
                {
                    Log.d(TAG, "agreement: $it")
                    policyPopup.show(supportFragmentManager,null)
                    policyPopup.setFragmentResultListener("listener"){a,b->
                        val isAgreed=b.getBoolean("agreed")
                        if(isAgreed){
                            policyPopup.dismiss()
                        }
                    }

                }

            }
        })
    }


    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navigationController,null)
    }






}