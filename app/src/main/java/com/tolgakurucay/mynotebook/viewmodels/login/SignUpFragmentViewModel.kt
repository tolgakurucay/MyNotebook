package com.tolgakurucay.mynotebook.viewmodels.login

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignUpFragmentViewModel : ViewModel() {

    private val auth=FirebaseAuth.getInstance()

    val name=MutableLiveData<String>()
    val surname=MutableLiveData<String>()
    val mail=MutableLiveData<String>()
    val password=MutableLiveData<String>()
    val createMessage=MutableLiveData<String>()
    val loadingDialog=MutableLiveData<Boolean>()


    fun validateName(name:String){
        if (name.isEmpty()){
            this.name.value="Enter An Name"
        }
        else if(name.equals("")){
            this.name.value="Enter An Name"
        }
        else{
            this.name.value="validated"
        }

    }
    fun validateSurname(surname:String){
        if (surname.isEmpty()){
            this.surname.value="Enter An Surname"
        }
        else if(surname.equals("")){
            this.surname.value="Enter An Surname"
        }
        else{
            this.surname.value="validated"
        }


    }
    fun validateMail(email:String){
        if(email.isEmpty()){
            this.mail.value="Enter An Mail"
        }
        else if(email.equals("")){
            this.mail.value="Enter An Mail"
        }
        else if(Patterns.EMAIL_ADDRESS.matcher(email).matches()!=true){
            this.mail.value="Invalid Email"
        }
        else{
            this.mail.value="validated"
        }

    }
    fun validatePassword(password:String){
        if(password==""){
            this.password.value="Enter An Password"
        }
        else if(password.length<8){
            this.password.value="Minimum 8 Characters"
        }

        else
        {
            this.password.value="validated"
        }

    }

    fun createUserWithEmailAndPassword(email:String,password: String){

        auth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                auth.currentUser!!.sendEmailVerification()
                    .addOnSuccessListener {
                        createMessage.value="success"
                    }
                    .addOnFailureListener {
                        createMessage.value="fail"
                    }
            }
            .addOnFailureListener {
                createMessage.value="fail"
            }

    }


}