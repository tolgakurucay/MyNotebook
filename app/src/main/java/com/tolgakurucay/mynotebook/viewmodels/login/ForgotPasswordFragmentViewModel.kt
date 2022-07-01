package com.tolgakurucay.mynotebook.viewmodels.login

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordFragmentViewModel : ViewModel() {

    val emailMessage=MutableLiveData<String>()
    val forgotPasswordMessage=MutableLiveData<String>()
    val loadingDialog=MutableLiveData<Boolean>()
    private var auth=FirebaseAuth.getInstance()



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