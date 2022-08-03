package com.tolgakurucay.mynotebook.viewmodels.main

import android.app.Activity
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.tolgakurucay.mynotebook.R
import com.tolgakurucay.mynotebook.models.CreateUserModel
import com.tolgakurucay.mynotebook.models.CreateUserWithPhone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(): ViewModel() {

    val TAG="bilgi"

    val mailFlow=MutableStateFlow<CreateUserModel?>(null)
    val phoneFlow= MutableStateFlow<CreateUserWithPhone?>(null)
    val savedMail= MutableSharedFlow<String>()
    val savedPhone=MutableStateFlow<String?>(null)
    val loading= MutableStateFlow<Boolean?>(null)
    val savebackgroundMessage= MutableSharedFlow<String>()
    val userRight= MutableStateFlow<String?>(null)

    val auth=FirebaseAuth.getInstance()
    val storage=FirebaseStorage.getInstance()
    val firestore=FirebaseFirestore.getInstance()


    fun getRightForCurrentUser(){
        firestore.collection("Right").document(auth.uid.toString()).get()
            .addOnSuccessListener {

                val right= it.getDouble("right")?.toInt()
                if(right!=null){

                    viewModelScope.launch { userRight.emit(right.toString()) }
                }
                else
                {
                    viewModelScope.launch{userRight.emit("0")}
                }

            }
            .addOnFailureListener {
                viewModelScope.launch { userRight.emit(it.localizedMessage) }
            }

    }

    fun saveBackgroundToStorage(uri: Uri,activity: Activity){
        storage.reference.child("backgrounds").child(auth.uid.toString()).putFile(uri)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    viewModelScope.launch { savebackgroundMessage.emit("success") }
                }
                else
                {
                    viewModelScope.launch { savebackgroundMessage.emit(activity.getString(R.string.error)+"\n"+task.exception!!.localizedMessage) }
                }

            }
    }

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