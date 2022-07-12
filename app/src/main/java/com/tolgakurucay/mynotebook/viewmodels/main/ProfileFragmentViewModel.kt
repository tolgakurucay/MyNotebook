package com.tolgakurucay.mynotebook.viewmodels.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tolgakurucay.mynotebook.models.CreateUserModel
import com.tolgakurucay.mynotebook.models.CreateUserWithPhone
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ProfileFragmentViewModel : ViewModel() {

    val TAG="bilgi"


    val mailFlow=MutableStateFlow<CreateUserModel?>(null)
    val phoneFlow= MutableStateFlow<CreateUserWithPhone?>(null)
    val savedMail= MutableSharedFlow<String>()
    val savedPhone=MutableStateFlow<String?>(null)
    val loading= MutableStateFlow<Boolean?>(null)


    fun getFromMail(user:FirebaseUser){
        viewModelScope.launch {
            loading.emit(true)
        }
        val firestore=FirebaseFirestore.getInstance()
        val path=firestore.collection("Users").document(user.uid)
        path.get()
            .addOnSuccessListener {
                if(it.exists()){
                    val name=it["name"].toString()
                    val surname=it["surname"].toString()
                    val mail=it["mail"].toString()
                    val password=it["password"].toString()
                    val photo=it["photo"].toString()
                    val userModel=CreateUserModel(name,surname,mail,password,photo)
                    Log.d(TAG, "getFromMail: ${userModel}")
                    viewModelScope.launch {
                        mailFlow.emit(userModel)
                        loading.emit(false)
                    }



                }

            }
            .addOnFailureListener {
                viewModelScope.launch {
                    loading.emit(false)
                }
            }

    }




    fun getFromPhone(user:FirebaseUser){
        viewModelScope.launch {
            loading.emit(true)
        }
        val firestore=FirebaseFirestore.getInstance()
        val path=firestore.collection("Users").document(user.uid)
        path.get()
            .addOnSuccessListener {
                if(it.exists()){
                    val phoneNumber=it.get("phoneNumber").toString()
                    val name=it.get("name").toString()
                    val surname=it.get("surname").toString()
                    val photo=it.get("photo").toString()
                    val model=CreateUserWithPhone(phoneNumber,name,surname,photo)
                    viewModelScope.launch {
                        phoneFlow.emit(model)
                    }
                }
                viewModelScope.launch {
                    loading.emit(false)
                }
            }
            .addOnFailureListener {
                viewModelScope.launch {
                    loading.emit(false)
                }
            }

    }
    fun setMail(name:String?,surname:String?,photo:String?,user:FirebaseUser){

        viewModelScope.launch {
            loading.emit(true)
        }
            val firestore=FirebaseFirestore.getInstance()
            val path=firestore.collection("Users").document(user.uid)
            path.update("name",name)
            path.update("surname",surname)
            path.update("photo",photo)
                .addOnSuccessListener {

                    viewModelScope.launch {
                        savedMail.emit("saved")
                        loading.emit(false)
                    }

                }
                .addOnFailureListener {

                    viewModelScope.launch {
                        savedMail.emit(it.localizedMessage)
                        loading.emit(false)

                    }

                }


    }
    fun setPhone(name:String,surname:String,photo:String?,user:FirebaseUser){
        viewModelScope.launch {
            loading.emit(true)
        }
        val firestore=FirebaseFirestore.getInstance()
        val path=firestore.collection("Users").document(user.uid)
        path.update("name",name)
        path.update("surname",surname)
        path.update("photo",photo)
            .addOnSuccessListener {
                viewModelScope.launch {
                    savedPhone.emit("saved")
                    loading.emit(false)
                }

            }
            .addOnFailureListener {
                viewModelScope.launch {
                    savedPhone.emit(it.localizedMessage)
                    loading.emit(false)
                }

            }


    }
}