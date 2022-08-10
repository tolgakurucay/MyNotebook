package com.tolgakurucay.mynotebook.views.login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.navigation.findNavController
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

        checkUserLoggedIn()




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