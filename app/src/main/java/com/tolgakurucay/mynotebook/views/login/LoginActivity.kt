package com.tolgakurucay.mynotebook.views.login

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.FirebaseAuth
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.databinding.ActivityLoginBinding
import com.tolgakurucay.mynotebook.views.forgotpassword.ForgotPasswordFragmentDirections
import com.tolgakurucay.mynotebook.views.main.MainActivity
import com.tolgakurucay.mynotebook.views.signup.SignUpFragmentDirections
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var viewBinding:ActivityLoginBinding
    var doubleBackPress=false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding= ActivityLoginBinding.inflate(layoutInflater)
        overridePendingTransition(R.anim.from_right,R.anim.to_left)
        setContentView(viewBinding.root)
        checkUpdate()
        checkUserLoggedIn()




    }
    private lateinit var mAppUpdateManager: AppUpdateManager

    private fun checkUpdate() {
        mAppUpdateManager= AppUpdateManagerFactory.create(this)
        mAppUpdateManager.appUpdateInfo
                .addOnSuccessListener {
                      it?.let {appUpdateInfo ->
                          if(appUpdateInfo.updateAvailability()== UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(
                        AppUpdateType.IMMEDIATE)){
                       try {
                     //   Log.d(TAG, "onStart: güncelleme var")
                        mAppUpdateManager.startUpdateFlowForResult(
                                appUpdateInfo,
                                AppUpdateType.IMMEDIATE,
                                this,
                                58
                         )
                    } catch (e: IntentSender.SendIntentException) {
                        e.printStackTrace()

                    }
                }
                else
                {
                 //   Log.d(TAG, "no update here")
                }

            }
        }
            .addOnFailureListener {
            it?.let { exception ->

                 //   Toast.makeText(binding.root.context,exception.localizedMessage,Toast.LENGTH_LONG).show()
            }

        }

    }


    fun checkUserLoggedIn(){
        val auth=FirebaseAuth.getInstance()
        auth.currentUser?.let {
            val intent=Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onBackPressed() {
        val navController=viewBinding.navHostFragment.findNavController()

        navController.currentDestination?.let {

            if(it.displayName.equals("com.tolgakurucay.mynotebook:id/signUpFragment")){
                navController.navigate(SignUpFragmentDirections.actionSignUpFragmentToLoginFragment())
            }
            else if(it.displayName.equals("com.tolgakurucay.mynotebook:id/forgotPasswordFragment")){
                navController.navigate(ForgotPasswordFragmentDirections.actionForgotPasswordFragmentToLoginFragment())
            }
            else if(it.displayName.equals("com.tolgakurucay.mynotebook:id/loginFragment")){
                if (doubleBackPress){
                    val intent= Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
                doubleBackPress=true
                Handler(Looper.getMainLooper()).postDelayed(Runnable {doubleBackPress=false  },2000)

            }

        }

    }
}