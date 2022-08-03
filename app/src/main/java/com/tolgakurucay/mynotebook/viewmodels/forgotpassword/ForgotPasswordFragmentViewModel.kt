package com.tolgakurucay.mynotebook.viewmodels.forgotpassword

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordFragmentViewModel @Inject constructor(): ViewModel() {

    val emailMessage=MutableLiveData<String>()
    val forgotPasswordMessage=MutableLiveData<String>()
    val loadingDialog=MutableLiveData<Boolean>()
    private var auth=FirebaseAuth.getInstance()



    fun validateEmail(email:String){
        if(email.isEmpty()){
            emailMessage.value="Enter An Mail"
        }
        else if(email == ""){
            emailMessage.value="Enter An Mail"
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailMessage.value="Invalid Email"
        }
        else{
            emailMessage.value="validated"
        }

    }


    fun forgotPassword(email:String){
        loadingDialog.value=true
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener {
                forgotPasswordMessage.value="true"
                loadingDialog.value=false
            }
            .addOnFailureListener {
                forgotPasswordMessage.value="error"
                loadingDialog.value=false
            }
    }






}