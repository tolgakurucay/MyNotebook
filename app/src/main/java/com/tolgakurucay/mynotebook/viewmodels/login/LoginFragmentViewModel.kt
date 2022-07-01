package com.tolgakurucay.mynotebook.viewmodels.login

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginFragmentViewModel : ViewModel() {

    val emailMessage=MutableLiveData<String>()
    val passwordMessage=MutableLiveData<String>()


    fun validateEmail(email:String){
        if(email.isEmpty()){
            emailMessage.value="Enter An Mail"
        }
        else if(email.equals("")){
            emailMessage.value="Enter An Mail"
        }
        else if(Patterns.EMAIL_ADDRESS.matcher(email).matches()!=true){
            emailMessage.value="Invalid Email"
        }
        else{
            emailMessage.value="validated"
        }



    }

    fun validatePassword(password:String){
        if (password.isEmpty()){
            passwordMessage.value="Enter An Password"
        }
        else if(password.equals("")){
            passwordMessage.value="Enter An Password"
        }
        else{
            passwordMessage.value="validated"
        }

    }




}