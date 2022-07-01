package com.tolgakurucay.mynotebook.viewmodels

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ForgotPasswordFragmentViewModel : ViewModel() {

    val emailMessage=MutableLiveData<String>()



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






}