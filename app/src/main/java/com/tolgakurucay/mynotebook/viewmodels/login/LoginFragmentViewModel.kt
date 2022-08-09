package com.tolgakurucay.mynotebook.viewmodels.login



import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginFragmentViewModel @Inject constructor(): ViewModel() {

    private var auth=FirebaseAuth.getInstance()

    val emailMessage=MutableLiveData<String>()
    val passwordMessage=MutableLiveData<String>()
    val signMessage=MutableLiveData<String>()
    val loadingDialog=MutableLiveData<Boolean>()




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


    fun signInWithEmailAndPassword(email:String,password: String){
        loadingDialog.value=true
        auth.signInWithEmailAndPassword(email,password)
            .addOnSuccessListener {
                auth.currentUser?.let { user->
                    if(user.isEmailVerified){
                        signMessage.value="okay"
                        loadingDialog.value=false
                    }
                    else
                    {
                        signMessage.value="notverified"
                        loadingDialog.value=false
                    }
                }
            }
            .addOnFailureListener {
                signMessage.value="error"
                loadingDialog.value=false
            }

    }






}