package com.tolgakurucay.mynotebook.viewmodels.login


import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.lang.Exception


class LoginFragmentViewModel : ViewModel() {

    private var auth=FirebaseAuth.getInstance()
    private lateinit var googleSignInClient :GoogleSignInClient

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

    fun googleSignIn(default_web_client_id:String,context:Context){
        auth.signOut()


         val gso=GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
             .requestIdToken(default_web_client_id)
             .requestEmail()
             .build()

        googleSignInClient= GoogleSignIn.getClient(context,gso)

        val signInIntent=googleSignInClient.signInIntent


           val task=GoogleSignIn.getSignedInAccountFromIntent(signInIntent)

           if (task.isSuccessful){
               try{
                   val account=task.getResult(ApiException::class.java)
                   val credential=GoogleAuthProvider.getCredential(account.idToken!!,null)
                   auth.signInWithCredential(credential)
                       .addOnSuccessListener {
                           Log.d("bilgi","başarılı")
                       }
                       .addOnFailureListener {
                           Log.d("bilgi","başarısız")
                       }
               }
               catch (e:ApiException){

               }

           }
           else
           {
               Log.d("bilgi",task.exception!!.stackTraceToString().toString())


           }










    }
    fun facebookSignIn(){

    }





}