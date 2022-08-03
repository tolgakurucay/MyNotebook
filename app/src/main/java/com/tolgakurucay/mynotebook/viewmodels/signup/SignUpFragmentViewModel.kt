package com.tolgakurucay.mynotebook.viewmodels.signup

import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.tolgakurucay.mynotebook.models.CreateUserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignUpFragmentViewModel @Inject constructor() : ViewModel() {

    private val auth=FirebaseAuth.getInstance()
    private val firestore=FirebaseFirestore.getInstance()

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

    fun createUserWithEmailAndPassword(email:String,password: String,name:String,surname:String){

        loadingDialog.value=true
        auth.createUserWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                auth.currentUser!!.sendEmailVerification()
                    .addOnSuccessListener {
                        val newUser=CreateUserModel(name,surname,email,password,null)
                        firestore.collection("Users").document(auth.currentUser!!.uid).set(newUser)
                            .addOnSuccessListener {
                                createMessage.value="success"
                                loadingDialog.value=false
                            }
                            .addOnFailureListener {
                                createMessage.value="fail"
                                loadingDialog.value=false

                            }


                    }
                    .addOnFailureListener {
                        createMessage.value="fail"
                        loadingDialog.value=false

                    }
            }
            .addOnFailureListener {
                createMessage.value="fail"
                loadingDialog.value=false

            }

    }


}